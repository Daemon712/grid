package ru.foobarbaz.grid.transport;

import edu.uci.ics.jung.graph.Graph;
import ru.foobarbaz.grid.entity.Task;

import java.util.function.Function;

public class TaskDeserializer<G extends Graph<V, E>, V, E> implements Function<String, Task<G, V, E>> {
    private GraphDeserializer<G, V, E> graphDeserializer;

    public TaskDeserializer(GraphDeserializer<G, V, E> graphDeserializer) {
        this.graphDeserializer = graphDeserializer;
    }

    @Override
    public Task<G, V, E> apply(String s) {
        Task<G, V, E> task = new Task<>();

        int delimiter = s.indexOf("\n\n");
        task.setGraph(graphDeserializer.apply(s.substring(0, delimiter)));

        String[] vertexLine = s.substring(delimiter + 2).split(" ");
        task.setSource(graphDeserializer.getVertexDeserializer().apply(vertexLine[0]));
        task.setTarget(graphDeserializer.getVertexDeserializer().apply(vertexLine[1]));

        return task;
    }
}
