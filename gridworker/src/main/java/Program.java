import ru.foobarbaz.grid.entity.Edge;
import ru.foobarbaz.grid.entity.Task;
import ru.foobarbaz.grid.logic.SimpleShortestPathFinder;
import ru.foobarbaz.grid.transport.PathSerializer;
import ru.foobarbaz.grid.transport.TaskDeserializer;
import ru.foobarbaz.grid.transport.TransportConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class Program {
    private final static SimpleShortestPathFinder pathFinder = new SimpleShortestPathFinder();
    private final static TaskDeserializer taskDeserializer = TransportConfig.getTaskDeserializer();
    private final static PathSerializer pathSerializer = TransportConfig.getPathSerializer();

    private final static Comparator<List<Edge>> comparator =
            Comparator.comparingInt(path -> path.stream().mapToInt(Edge::getWeight).sum());

    public static void main(String[] args) throws IOException {
        Path inputFile = Paths.get(args[0]);
        String taskSerialized = new String(Files.readAllBytes(inputFile));
        Task task = taskDeserializer.apply(taskSerialized);

        task.setPathComparator(comparator);
        List path = pathFinder.getShortestPath(task);

        String pathSerialized = pathSerializer.apply(task.getGraph(), path);
        Path outputFile = Paths.get(args[1]);
        Files.write(outputFile, pathSerialized.getBytes());
    }
}
