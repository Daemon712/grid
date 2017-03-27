package ru.foobarbaz.grid.transport;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

import java.text.MessageFormat;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphSerializer<G extends Graph<V, E>, V, E> implements Function<G, String> {

    private static final String FORMAT = "{0} {1} {2}";
    private Function<E, String> edgeSerializer = Object::toString;
    private Function<V, String> vertexSerializer = Object::toString;

    public GraphSerializer() {
    }

    public GraphSerializer(Function<E, String> edgeSerializer, Function<V, String> vertexSerializer) {
        this.edgeSerializer = edgeSerializer;
        this.vertexSerializer = vertexSerializer;
    }

    @Override
    public String apply(G graph) {
        return graph.getEdges().stream().map(edge -> {
            Pair<V> endpoints = graph.getEndpoints(edge);
            return MessageFormat.format(FORMAT,
                    vertexSerializer.apply(endpoints.getFirst()),
                    vertexSerializer.apply(endpoints.getSecond()),
                    edgeSerializer.apply(edge));
        }).collect(Collectors.joining("\n"));
    }

    public Function<E, String> getEdgeSerializer() {
        return edgeSerializer;
    }

    public void setEdgeSerializer(Function<E, String> edgeSerializer) {
        this.edgeSerializer = edgeSerializer;
    }

    public Function<V, String> getVertexSerializer() {
        return vertexSerializer;
    }

    public void setVertexSerializer(Function<V, String> vertexSerializer) {
        this.vertexSerializer = vertexSerializer;
    }
}
