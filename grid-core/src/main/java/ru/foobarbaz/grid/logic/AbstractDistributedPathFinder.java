package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.Graph;
import ru.foobarbaz.grid.entity.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractDistributedPathFinder<G extends Graph<V, E>, V, E> implements ShortestPathFinder<G, V, E> {
    private static final int DESIRED_SUB_TASKS = 8;
    private static final int EASY_GRAPH_SIZE = 8;

    protected List<SubTask<G, V, E>> generateSubTasks(Task<G, V, E> task) {
        int edgesLimit = Math.max(EASY_GRAPH_SIZE, task.getGraph().getEdgeCount() - DESIRED_SUB_TASKS);
        List<SubTask<G, V, E>> subTasks = generateSubTasks(new SubTask<>(task, new ArrayList<>()), edgesLimit);
        System.out.format("%s subTasks have been generated\n", subTasks.size());
        for (int i = 0; i < subTasks.size(); i++) subTasks.get(i).setId(i);
        return subTasks;
    }


    protected List<SubTask<G, V, E>> generateSubTasks(SubTask<G, V, E> subTask, int edgesLimit) {
        if (subTask.getTask().getGraph().getEdgeCount() <= edgesLimit)
            return Collections.singletonList(subTask);

        return subTask.getTask().getGraph().getOutEdges(subTask.getTask().getSource())
                .stream()
                .map(step -> createSubTask(subTask, step))
                .flatMap(newSubTask -> generateSubTasks(newSubTask, edgesLimit).stream())
                .collect(Collectors.toList());
    }

    protected SubTask<G, V, E> createSubTask(SubTask<G, V, E> originalSubTask, E step) {
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
        return new SubTask<>(newTask, newPath);
    }
}
