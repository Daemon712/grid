package ru.foobarbaz.grid.transport;

import edu.uci.ics.jung.graph.DirectedGraph;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathSerializer<G extends DirectedGraph<V, E>, V, E> implements BiFunction<G, List<E>, String> {

    private Function<V, String> vertexSerializer;

    public PathSerializer(Function<V, String> vertexSerializer) {
        this.vertexSerializer = vertexSerializer;
    }

    @Override
    public String apply(G graph, List<E> edgesPath) {
        Stream.Builder<V> pathBuilder = Stream.builder();

        pathBuilder.add(graph.getEndpoints(edgesPath.get(0)).getFirst());
        edgesPath.stream().map(e -> graph.getEndpoints(e).getSecond())
                .forEach(pathBuilder);

        return pathBuilder.build().map(vertexSerializer).collect(Collectors.joining(" "));
    }
}
