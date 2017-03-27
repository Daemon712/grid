package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.Graph;
import ru.foobarbaz.grid.entity.Task;

import java.util.List;

public interface ShortestPathFinder<G extends Graph<V, E>, V, E> {
    List<E> getShortestPath(Task<G, V, E> task);
}
