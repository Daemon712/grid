package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import ru.foobarbaz.grid.entity.Edge;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphGenerator {
    public static DirectedGraph<Integer, Edge> generateDirectedGraph(int vertices ) {
        DirectedGraph<Integer, Edge> graph = new DirectedSparseGraph<>();
        IntStream.range(1, vertices + 1).forEach(graph::addVertex);
        for (int i = 1; i < vertices + 1; i++) {
            addInEdge(graph, i);
            addOutEdge(graph, i);
        }
        return graph;
    }

    private static void addOutEdge(DirectedGraph<Integer, Edge> graph, int vertex) {
        Collection<Integer> successors = graph.getSuccessors(vertex);
        List<Integer> options = graph.getVertices().stream()
                .filter(v -> v != vertex && !successors.contains(v))
                .collect(Collectors.groupingBy(v -> graph.getInEdges(v).size()))
                .entrySet().stream()
                .min(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .orElse(Collections.emptyList());

        if (options.size() == 0) return;

        int index = (int)Math.floor(Math.random() * options.size());
        int target = options.get(index);
        int w = (int) Math.floor(1 + Math.random() * 10);
        graph.addEdge(new Edge(graph.getEdgeCount(), w), vertex, target);
    }

    private static void addInEdge(DirectedGraph<Integer, Edge> graph, int vertex) {
        Collection<Integer> predecessors = graph.getPredecessors(vertex);
        List<Integer> options = graph.getVertices().stream()
                .filter(v -> v != vertex && !predecessors.contains(v))
                .collect(Collectors.groupingBy(v -> graph.getOutEdges(v).size()))
                .entrySet().stream()
                .min(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .orElse(Collections.emptyList());

        if (options.size() == 0) return;

        int index = (int)Math.floor(Math.random() * options.size());
        int source = options.get(index);
        int w = (int) Math.floor(1 + Math.random() * 10);
        graph.addEdge(new Edge(graph.getEdgeCount(), w), source, vertex);
    }
}
