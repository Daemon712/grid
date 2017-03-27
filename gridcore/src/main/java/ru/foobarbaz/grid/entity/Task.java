package ru.foobarbaz.grid.entity;

import edu.uci.ics.jung.graph.Graph;

import java.util.Comparator;
import java.util.List;

public class Task<G extends Graph<V, E>, V, E> implements Cloneable {
    private G graph;
    private V source;
    private V target;
    private Comparator<List<E>> pathComparator;

    public Task() {
    }

    public Task(G graph, V source, V target, Comparator<List<E>> pathComparator) {
        this.graph = graph;
        this.source = source;
        this.target = target;
        this.pathComparator = pathComparator;
    }

    public G getGraph() {
        return graph;
    }

    public void setGraph(G graph) {
        this.graph = graph;
    }

    public V getSource() {
        return source;
    }

    public void setSource(V source) {
        this.source = source;
    }

    public V getTarget() {
        return target;
    }

    public void setTarget(V target) {
        this.target = target;
    }

    public Comparator<List<E>> getPathComparator() {
        return pathComparator;
    }

    public void setPathComparator(Comparator<List<E>> pathComparator) {
        this.pathComparator = pathComparator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Task<G, V, E> clone() {
        try {
            return (Task<G, V, E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
