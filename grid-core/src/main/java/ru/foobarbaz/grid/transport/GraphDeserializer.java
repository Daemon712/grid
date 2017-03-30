package ru.foobarbaz.grid.transport;

import edu.uci.ics.jung.graph.Graph;

import java.util.function.Function;
import java.util.function.Supplier;

public class GraphDeserializer<G extends Graph<V, E>, V ,E> implements Function<String, G>{

    private Supplier<G> graphFactory;
    private Function<String, V> vertexDeserializer;
    private Function<String, E> edgeDeserializer;

    public GraphDeserializer() {
    }

    public GraphDeserializer(Supplier<G> graphFactory,
                             Function<String, V> vertexDeserializer,
                             Function<String, E> edgeDeserializer) {
        this.graphFactory = graphFactory;
        this.vertexDeserializer = vertexDeserializer;
        this.edgeDeserializer = edgeDeserializer;
    }

    @Override
    public G apply(String s) {
        G graph = graphFactory.get();

        for (String line : s.split("\n")) {
            String[] args = line.split(" ");

            if (args.length < 3) continue;

            V vertex1 = vertexDeserializer.apply(args[0]);
            V vertex2 = vertexDeserializer.apply(args[1]);
            E edge = edgeDeserializer.apply(args[2]);
            graph.addEdge(edge, vertex1, vertex2);
        }

        return graph;
    }

    public Function<String, V> getVertexDeserializer() {
        return vertexDeserializer;
    }

    public Function<String, E> getEdgeDeserializer() {
        return edgeDeserializer;
    }
}
