package ru.foobarbaz.grid.entity;

import java.util.Objects;

public class Edge {
    private int id;
    private int weight;

    public Edge(int id) {
        this.id = id;
    }

    public Edge(int id, int weight) {
        this.id = id;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        return id == edge.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", weight=" + weight +
                '}';
    }
}
