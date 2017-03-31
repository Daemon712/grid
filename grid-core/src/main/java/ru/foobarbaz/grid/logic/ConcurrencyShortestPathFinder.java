package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.Graph;
import ru.foobarbaz.grid.entity.Task;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyShortestPathFinder<G extends Graph<V, E>, V, E> extends AbstractDistributedPathFinder<G, V, E> {
    private final ShortestPathFinder<G, V, E> simpleShortestPathFinder;

    public ConcurrencyShortestPathFinder(ShortestPathFinder<G, V, E> simpleShortestPathFinder) {
        this.simpleShortestPathFinder = simpleShortestPathFinder;
    }

    @Override
    public List<E> getShortestPath(Task<G, V, E> task) {
        Collection<SubTask<G, V, E>> subTasks = generateSubTasks(task);
        System.out.format("%s subTasks have been generated\n", subTasks.size());
        AtomicInteger sequence = new AtomicInteger(1);
        return subTasks.parallelStream()
                .peek(subTask -> subTask.setId(sequence.getAndIncrement()))
                .map(this::completeTask)
                .filter(Objects::nonNull)
                .min(task.getPathComparator())
                .orElse(null);
    }

    private List<E> completeTask(SubTask<G, V, E> subTask){
        List<E> shortestPath = simpleShortestPathFinder.getShortestPath(subTask.getTask());

        if (shortestPath == null) {
            System.out.format("SubTask #%s completed. Path not found\n", subTask.getId());
            return null;
        }

        subTask.getPath().addAll(shortestPath);
        System.out.format("SubTask #%s completed. Path size: %s\n", subTask.getId(), subTask.getPath().size());
        return subTask.getPath();
    }
}
