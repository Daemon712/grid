package ru.foobarbaz.grid.logic;


import edu.uci.ics.jung.graph.Graph;
import ru.foobarbaz.grid.entity.Task;

import java.util.List;

public class SubTask<G extends Graph<V, E>, V, E> {
    private Task<G, V, E> task;
    private List<E> path;
    private int id;

    public SubTask(Task<G, V, E> task, List<E> path) {
        this.task = task;
        this.path = path;
    }

    public Task<G, V, E> getTask() {
        return task;
    }

    public List<E> getPath() {
        return path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
