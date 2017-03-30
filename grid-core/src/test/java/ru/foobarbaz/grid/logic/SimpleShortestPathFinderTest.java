package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.Graph;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.foobarbaz.grid.entity.Task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static ru.foobarbaz.grid.logic.GraphRepo.*;

@RunWith(Parameterized.class)
public class SimpleShortestPathFinderTest {
    private SimpleShortestPathFinder<Graph<Integer, Integer>, Integer, Integer> instance =
            new SimpleShortestPathFinder<>();

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{1, getOneEdgeGraph()},
                new Object[]{1, getTwoEdgeDeadEndDirGraph()},
                new Object[]{1, getTwoEdgeDeadEndGraph()},
                new Object[]{2, getTwoEdgeLongGraph()},
                new Object[]{1, getTreeEdgeLoopGraph()},
                new Object[]{3, getHardGraph()},
                new Object[]{16, getBigGraph()}
        );
    }

    public SimpleShortestPathFinderTest(int expectedPathLength, Graph<Integer, Integer> graph) {
        this.graph = graph;
        Comparator<List<Integer>> comparator = Comparator.comparingInt(Collection::size);
        this.task = new Task<>(graph, 1, 0, comparator);
        this.expectedPathLength = expectedPathLength;
    }

    private Graph<Integer, Integer> graph;
    private Task<Graph<Integer, Integer>, Integer, Integer> task;
    private int expectedPathLength;

    @Test()
    public void testGetShortestPath() throws Exception {
        System.out.format("graph: %s\n", GraphUtils.graphToString(graph));
        List<Integer> actualPath = instance.getShortestPath(task);
        System.out.format("path: %s\nsize: %s\n", GraphUtils.edgesPathToString(graph, actualPath), actualPath.size());
        Assert.assertEquals(expectedPathLength, actualPath.size());
    }
}