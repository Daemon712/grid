package ru.foobarbaz.grid.broker;

import edu.uci.ics.jung.graph.Graph;
import ru.foobarbaz.grid.entity.Task;
import ru.foobarbaz.grid.logic.AbstractDistributedPathFinder;
import ru.foobarbaz.grid.logic.SubTask;
import ru.foobarbaz.grid.transport.PathDeserializer;
import ru.foobarbaz.grid.transport.TaskSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.foobarbaz.grid.broker.FilesUtils.cleanDirectoryAsync;
import static ru.foobarbaz.grid.broker.ProcessUtils.executeCommand;

public class GridShortestPathFinder<G extends Graph<V, E>, V, E> extends AbstractDistributedPathFinder<G, V, E> {
    private static final Path APP_DIR = Paths.get(System.getProperty("user.home"), "pfgrid");
    private static final Path MY_GRID = Paths.get(System.getProperty("user.home"), "mygrid", "bin", "mygrid");

    private static final String JOB_FILE_NAME = "path-find-job.jdf";
    private static final String TASK_FILE_NAME_TEMPLATE = "task-%04d.data";
    private static final String JOB_DESCRIPTION = "job :\n\tlabel : ShortestPathFinder";

    private static final Pattern JOB_ID_PATTERN = Pattern.compile("\\[(\\d+)]");
    private static final Pattern TASK_ID_PATTERN = Pattern.compile(".*task-(\\d+)\\.data");

    private static final Collection<String> REQUIRED_JARS = Arrays.asList(
            "guava-19.0.jar",
            "grid-core-1.0.jar",
            "grid-worker-1.0.jar",
            "jung-api-2.1.1.jar",
            "jung-graph-impl-2.1.1.jar"
    );

    private final TaskSerializer<G, V, E> taskSerializer;
    private final PathDeserializer<G, V, E> pathDeserializer;

    public GridShortestPathFinder(TaskSerializer<G, V, E> taskSerializer, PathDeserializer<G, V, E> pathDeserializer) {
        this.taskSerializer = taskSerializer;
        this.pathDeserializer = pathDeserializer;
    }

    @Override
    public List<E> getShortestPath(Task<G, V, E> task) {
        try {
            List<SubTask<G, V, E>> subTasks = generateSubTasks(task);

            Path inputDir = Files.createTempDirectory(APP_DIR, "input");
            Path outputDir = Files.createTempDirectory(APP_DIR, "output");
            writeJobFiles(inputDir, outputDir, subTasks);

            runJob(inputDir);
            List<String> results = collectResults(outputDir);

            cleanDirectoryAsync(inputDir);
            cleanDirectoryAsync(outputDir);

            return parseResults(subTasks, results)
                    .stream()
                    .filter(Objects::nonNull)
                    .min(task.getPathComparator())
                    .orElse(null);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<List<E>> parseResults(List<SubTask<G, V, E>> subTasks, List<String> results) {
        if (subTasks.size() != results.size())
            throw new RuntimeException("Some tasks was missed; expected: "
                    + subTasks.size() + "; actual: " + results.size());

        List<List<E>> paths = new ArrayList<>(subTasks.size());
        for (int i = 0; i < subTasks.size(); i++) {
            SubTask<G, V, E> subTask = subTasks.get(i);
            G graph = subTask.getTask().getGraph();
            List<E> computedPath = pathDeserializer.apply(graph, results.get(i));
            if (computedPath == null){
                paths.add(null);
            } else {
                subTask.getPath().addAll(computedPath);
                paths.add(subTask.getPath());
            }
        }
        return paths;
    }

    private List<String> collectResults(Path outputDir) throws IOException {
        Path[] files = Files.walk(outputDir)
                .filter(p -> TASK_ID_PATTERN.matcher(p.toString()).matches())
                .toArray(Path[]::new);

        String[] result = new String[files.length];
        for (Path path : files){
            Matcher matcher = TASK_ID_PATTERN.matcher(path.toString());
            if (!matcher.find()) continue;
            Integer taskId = Integer.valueOf(matcher.group(1));

            String taskResult = new String(Files.readAllBytes(path));
            System.out.println("Read " + path);

            result[taskId] = taskResult;
        }
        return Arrays.asList(result);
    }

    private void runJob(Path inputDir) throws IOException, InterruptedException {
        String addResult = executeCommand(MY_GRID + " addjob " +  inputDir.resolve(JOB_FILE_NAME));
        Matcher matcher = JOB_ID_PATTERN.matcher(addResult);

        if (!addResult.contains("Job successfully added") || !matcher.find())
            throw new RuntimeException("Job failed with error: " + addResult);

        String jobId = matcher.group(1);

        String waitResult = executeCommand(MY_GRID + " waitforjob " + jobId);
        if (!waitResult.contains("Job execution done"))
            throw new RuntimeException("Job failed with error: " + waitResult);
    }

    private void writeJobFiles(Path inputDir, Path outputDir, List<SubTask<G, V, E>> subTasks) {
        try {
            String jobBody = JOB_DESCRIPTION + "\n\n" + subTasks.stream()
                    .map(subTask -> writeSubTaskFile(inputDir, subTask))
                    .map(fileName -> generateTaskDescription(inputDir, outputDir, fileName))
                    .collect(Collectors.joining("\n"));
            Files.write(inputDir.resolve(JOB_FILE_NAME), jobBody.getBytes());
            System.out.println("Create " + inputDir.resolve(JOB_FILE_NAME));
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private String writeSubTaskFile(Path inputDir, SubTask<G, V, E> subTask){
        try {
            String taskBody = taskSerializer.apply(subTask.getTask());
            String fileName = String.format(TASK_FILE_NAME_TEMPLATE, subTask.getId());
            Files.write(inputDir.resolve(fileName), taskBody.getBytes());
            System.out.println("Create " + inputDir.resolve(fileName));
            return fileName;
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private String generateTaskDescription(Path inputDir, Path outputDir, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("task :")
                .append("\n\tinit : put ")
                .append(inputDir.resolve(fileName))
                .append(" input.data");
        REQUIRED_JARS.stream()
                .map(jar -> MessageFormat.format("\n\t\tput {1}/{0} {0}", jar, APP_DIR))
                .forEach(stringBuilder::append);
        return stringBuilder.append("\n\tremote : java -jar ")
                .append("grid-worker-1.0.jar input.data output.data")
                .append("\n\tfinal : get output.data ")
                .append(outputDir.resolve(fileName))
                .toString();
    }
}
