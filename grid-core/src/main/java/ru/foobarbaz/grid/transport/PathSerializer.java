package ru.foobarbaz.grid.transport;

import edu.uci.ics.jung.graph.Graph;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Actually it works incorrectly with undirected graphs
public class PathSerializer<G extends Graph<V, E>, V, E> implements BiFunction<G, List<E>, String> {

    private Function<V, String> vertexSerializer;

    public PathSerializer(Function<V, String> vertexSerializer) {
        this.vertexSerializer = vertexSerializer;
    }

    @Override
    public String apply(G graph, List<E> edgesPath) {
        if (edgesPath == null || edgesPath.isEmpty()) return "";

        Stream.Builder<V> pathBuilder = Stream.builder();

        pathBuilder.add(graph.getEndpoints(edgesPath.get(0)).getFirst());
        edgesPath.stream().map(e -> graph.getEndpoints(e).getSecond())
                .forEach(pathBuilder);

        return pathBuilder.build().map(vertexSerializer).collect(Collectors.joining(" "));
    }
}
