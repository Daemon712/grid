package ru.foobarbaz.grid.transport;

import edu.uci.ics.jung.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PathDeserializer<G extends Graph<V, E>, V, E> implements BiFunction<G, String, List<E>> {
    private Function<String, V> vertexDeserializer;

    public PathDeserializer(Function<String, V> vertexDeserializer) {
        this.vertexDeserializer = vertexDeserializer;
    }

    @Override
    public List<E> apply(G graph, String path) {
        String[] vertices = path.split(" ");
        List<E> edgesPath = new ArrayList<>(vertices.length + 1);

        for (int i = 1; i < vertices.length; i++) {
            V source = vertexDeserializer.apply(vertices[i-1]);
            V target = vertexDeserializer.apply(vertices[i]);

            E edge = graph.getOutEdges(source).stream()
                    .filter(e -> target.equals(graph.getOpposite(source, e)))
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new);

            edgesPath.add(edge);
        }

        return edgesPath;
    }
}
