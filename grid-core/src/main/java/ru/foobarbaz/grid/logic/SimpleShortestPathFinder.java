package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.Graph;
import ru.foobarbaz.grid.entity.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleShortestPathFinder<G extends Graph<V, E>, V, E> implements ShortestPathFinder<G, V, E> {
    @Override
    public List<E> getShortestPath(Task<G, V, E> task) {
        if (task.getSource().equals(task.getTarget())) return new ArrayList<>();

        G subGraph = GraphUtils.clone(task.getGraph());
        subGraph.removeVertex(task.getSource());

        return task.getGraph().getOutEdges(task.getSource()).stream()
                .map(step -> nextStep(task, subGraph, step))
                .filter(Objects::nonNull)
                .min(task.getPathComparator())
                .orElse(null);
    }

    private List<E> nextStep(Task<G, V, E> task, G subGraph, E step) {
        V newSource = task.getGraph().getOpposite(task.getSource(), step);

        Task<G, V, E> subTask = task.clone();
        subTask.setGraph(subGraph);
        subTask.setSource(newSource);

        List<E> path = getShortestPath(subTask);
        if (path != null) path.add(0, step);
        return path;
    }
}
