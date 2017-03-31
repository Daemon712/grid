import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
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

public class Program<G extends Graph<V, E>, V, E>  {
    private final SimpleShortestPathFinder<G, V, E> pathFinder;
    private final TaskDeserializer<G, V, E> taskDeserializer;
    private final PathSerializer<G, V, E> pathSerializer;
    private final Comparator<List<E>> comparator;

    public Program(SimpleShortestPathFinder<G, V, E> pathFinder,
                   TaskDeserializer<G, V, E> taskDeserializer,
                   PathSerializer<G, V, E> pathSerializer,
                   Comparator<List<E>> comparator) {
        this.pathFinder = pathFinder;
        this.taskDeserializer = taskDeserializer;
        this.pathSerializer = pathSerializer;
        this.comparator = comparator;
    }

    private void run(Path inputFile, Path outputFile) throws IOException{
        String taskSerialized = new String(Files.readAllBytes(inputFile));
        Task<G, V, E> task = taskDeserializer.apply(taskSerialized);

        task.setPathComparator(comparator);
        List<E> path = pathFinder.getShortestPath(task);

        String pathSerialized = pathSerializer.apply(task.getGraph(), path);
        Files.write(outputFile, pathSerialized.getBytes());
    }

    public static void main(String[] args) throws IOException {
        Program<DirectedGraph<Integer, Edge>, Integer, Edge> program = new Program<>(
                new SimpleShortestPathFinder<>(),
                TransportConfig.getTaskDeserializer(),
                TransportConfig.getPathSerializer(),
                Comparator.comparingInt(path -> path.stream().mapToInt(Edge::getWeight).sum())
        );
        program.run(Paths.get(args[0]), Paths.get(args[1]));
    }
}
