package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import org.junit.Assert;
import org.junit.Test;
import ru.foobarbaz.grid.entity.Task;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ConcurrencyShortestPathFinderTest {
    private ConcurrencyShortestPathFinder<Graph<Integer, Integer>, Integer, Integer> instance =
            new ConcurrencyShortestPathFinder<>(new SimpleShortestPathFinder<>());
    private Comparator<List<Integer>> comparator = Comparator.comparingInt(Collection::size);

    @Test
    public void getShortestWay() throws Exception {
        DirectedGraph<Integer, Integer> graph = GraphRepo.getBigGraph();
        List<Integer> path = instance.getShortestPath(new Task<>(graph, 1, 0, comparator));
        Assert.assertNotNull(path);
        System.out.format("path: %s\n", GraphUtils.edgesPathToString(graph, path));
        Assert.assertEquals(16, path.size());
    }
}