package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import ru.foobarbaz.grid.transport.PathSerializer;

import java.util.List;
import java.util.stream.Collectors;

public class GraphUtils {
    @SuppressWarnings("unchecked")
    public static <G extends Graph<V, E>, V, E> G clone(G graph) {
        G clone = newInstance(graph);
        graph.getVertices().forEach(clone::addVertex);
        graph.getEdges().forEach(edge -> clone.addEdge(edge, graph.getIncidentVertices(edge)));
        return clone;
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(T object) {
        try {
            return (T) object.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static <G extends Graph<V, E>, V, E> String graphToString(G graph){
        return graph.getEdges()
                .stream()
                .map(edge -> edgeToString(graph, edge))
                .collect(Collectors.joining(", "));
    }

    private static <V, E> String edgeToString(Graph<V, E> graph, E edge){
        Pair<V> endpoints = graph.getEndpoints(edge);
        return endpoints.getFirst() + "-" + endpoints.getSecond();
    }

    public static <V, E> List<V> pathFromEdgesToVertices(DirectedGraph<V, E> graph, List<E> edgesPath){
        List<V> verticesPath = edgesPath.stream()
                .map(e -> graph.getEndpoints(e).getSecond())
                .collect(Collectors.toList());
        verticesPath.add(0, graph.getEndpoints(edgesPath.get(0)).getFirst());
        return verticesPath;
    }

    public static <V, E> String edgesPathToString(DirectedGraph<V, E> graph, List<E> edgesPath){
        return new PathSerializer<DirectedGraph<V, E>, V, E>(Object::toString).apply(graph, edgesPath);
    }

    public static <V, E> String edgesPathToString(Graph<V, E> graph, List<E> edgesPath){
        return edgesPath.stream().map(e -> edgeToString(graph, e)).collect(Collectors.joining(">"));
    }

    public static <V, E> String verticesPathToString(List<V> path) {
        return path.stream().map(Object::toString).collect(Collectors.joining(">"));
    }
}
