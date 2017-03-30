package ru.foobarbaz.grid.transport;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import ru.foobarbaz.grid.entity.Edge;

public class TransportConfig {
    public static TaskSerializer<DirectedGraph<Integer, Edge>, Integer, Edge> getTaskSerializer(){
        return new TaskSerializer<>(
                getGraphSerializer()
        );
    }

    public static GraphSerializer<DirectedGraph<Integer, Edge>, Integer, Edge> getGraphSerializer() {
        return new GraphSerializer<>(
                e -> Integer.toString(e.getWeight()),
                Integer::toUnsignedString
        );
    }

    public static PathSerializer<DirectedGraph<Integer, Edge>, Integer, Edge> getPathSerializer() {
        return new PathSerializer<>(
                getGraphSerializer().getVertexSerializer()
        );
    }


    public static TaskDeserializer<DirectedGraph<Integer, Edge>, Integer, Edge> getTaskDeserializer(){
        return new TaskDeserializer<>(
                getGraphDeserializer()
        );
    }

    public static GraphDeserializer<DirectedGraph<Integer, Edge>, Integer, Edge> getGraphDeserializer() {
        return new GraphDeserializer<>(
                DirectedSparseGraph::new,
                Integer::valueOf,
                new EdgeWeightDeserializer()
        );
    }

    public static PathDeserializer<DirectedGraph<Integer, Edge>, Integer, Edge> getPathDeserializer() {
        return new PathDeserializer<>(
                getGraphDeserializer().getVertexDeserializer()
        );
    }
}
