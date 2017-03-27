package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.Graph;
import ru.foobarbaz.grid.entity.Task;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ConcurrencyShortestPathFinder<G extends Graph<V, E>, V, E> implements ShortestPathFinder<G, V, E> {
    private static final int DESIRED_SUB_TASKS = 8;
    private ShortestPathFinder<G, V, E> simpleShortestPathFinder;
    private int maxEdgesPerThread;

    public ConcurrencyShortestPathFinder(ShortestPathFinder<G, V, E> simpleShortestPathFinder) {
        this.simpleShortestPathFinder = simpleShortestPathFinder;
    }

    @Override
    public List<E> getShortestPath(Task<G, V, E> task) {
        maxEdgesPerThread = task.getGraph().getEdgeCount() - DESIRED_SUB_TASKS;
        Collection<SubTask> subTasks = generateSubTasks(new SubTask(task, new ArrayList<>()));
        System.out.format("%s subTasks have been generated\n", subTasks.size());
        AtomicInteger sequence = new AtomicInteger(1);
        return subTasks.parallelStream()
                .peek(subTask -> subTask.setId(sequence.getAndIncrement()))
                .map(this::completeTask)
                .filter(Objects::nonNull)
                .min(task.getPathComparator())
                .orElse(null);
    }


    private Collection<SubTask> generateSubTasks(SubTask subTask) {
        if (subTask.getTask().getGraph().getEdgeCount() <= maxEdgesPerThread)
            return Collections.singleton(subTask);

        return subTask.getTask().getGraph().getOutEdges(subTask.getTask().getSource())
                .stream()
                .map(step -> createSubTask(subTask, step))
                .flatMap(newSubTask -> generateSubTasks(newSubTask).stream())
                .collect(Collectors.toList());
    }


    private SubTask createSubTask(SubTask originalSubTask, E step) {
        Task<G, V, E> originalTask = originalSubTask.getTask();
        V newSource = originalTask.getGraph().getOpposite(originalTask.getSource(), step);

        G subGraph;
        if (newSource.equals(originalTask.getTarget())){
            subGraph = GraphUtils.newInstance(originalTask.getGraph());
        } else {
            subGraph = GraphUtils.clone(originalTask.getGraph());
            subGraph.removeVertex(originalTask.getSource());
        }

        Task<G, V, E> newTask = originalTask.clone();
        newTask.setGraph(subGraph);
        newTask.setSource(newSource);

        List<E> newPath = new ArrayList<>(originalSubTask.getPath());
        newPath.add(step);
        return new SubTask(newTask, newPath);
    }

    private List<E> completeTask(SubTask subTask){
        List<E> shortestPath = simpleShortestPathFinder.getShortestPath(subTask.getTask());

        if (shortestPath == null) {
            System.out.format("SubTask #%s completed. Path not found\n", subTask.getId());
            return null;
        }

        subTask.getPath().addAll(shortestPath);
        System.out.format("SubTask #%s completed. Path size: %s\n", subTask.getId(), subTask.getPath().size());
        return subTask.getPath();
    }

    class SubTask {
        private Task<G, V, E> task;
        private List<E> path;
        private int id;

        SubTask(Task<G, V, E> task, List<E> path) {
            this.task = task;
            this.path = path;
        }

        Task<G, V, E> getTask() {
            return task;
        }

        List<E> getPath() {
            return path;
        }

        int getId() {
            return id;
        }

        void setId(int id) {
            this.id = id;
        }
    }
}
