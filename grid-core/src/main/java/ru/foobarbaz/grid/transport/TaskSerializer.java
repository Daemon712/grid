package ru.foobarbaz.grid.transport;

import edu.uci.ics.jung.graph.Graph;
import ru.foobarbaz.grid.entity.Task;

import java.text.MessageFormat;
import java.util.function.Function;

public class TaskSerializer<G extends Graph<V, E>, V, E>
        implements Function<Task<G, V, E>, String> {
    private static final String FORMAT = "{0}\n\n{1} {2}";
    private GraphSerializer<G, V, E> graphSerializer;

    public TaskSerializer(GraphSerializer<G, V, E> graphSerializer) {
        this.graphSerializer = graphSerializer;
    }

    @Override
    public String apply(Task<G, V, E> task) {
        return MessageFormat.format(FORMAT,
                graphSerializer.apply(task.getGraph()),
                graphSerializer.getVertexSerializer().apply(task.getSource()),
                graphSerializer.getVertexSerializer().apply(task.getTarget()));
    }
}
