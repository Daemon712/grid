package ru.foobarbaz.grid.transport;

import ru.foobarbaz.grid.entity.Edge;

import java.util.function.Function;

public class EdgeWeightDeserializer implements Function<String, Edge> {
    private int sequence;

    @Override
    public Edge apply(String s) {
        return new Edge(++sequence, Integer.valueOf(s));
    }
}
