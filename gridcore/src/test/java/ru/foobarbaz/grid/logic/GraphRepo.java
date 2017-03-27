package ru.foobarbaz.grid.logic;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

public class GraphRepo {


    public static Graph<Integer, Integer> getOneEdgeGraph(){
        Graph<Integer, Integer> oneEdgeGraph = new SparseGraph<>();
        oneEdgeGraph.addEdge(1, 1, 0);
        return oneEdgeGraph;
    }

    public static Graph<Integer, Integer> getHardGraph() {
        Graph<Integer, Integer> hardGraph = new SparseGraph<>();
        hardGraph.addEdge(1, 1, 2);
        hardGraph.addEdge(2, 1, 4);
        hardGraph.addEdge(3, 2, 4);
        hardGraph.addEdge(4, 2, 5);
        hardGraph.addEdge(5, 4, 5);
        hardGraph.addEdge(6, 4, 3);
        hardGraph.addEdge(7, 3, 0);
        return hardGraph;
    }

    public static Graph<Integer, Integer> getTreeEdgeLoopGraph() {
        Graph<Integer, Integer> threeEdgeLoopGraph = new SparseGraph<>();
        threeEdgeLoopGraph.addEdge(1, 1, 2);
        threeEdgeLoopGraph.addEdge(2, 2, 1);
        threeEdgeLoopGraph.addEdge(3, 1, 0);
        return threeEdgeLoopGraph;
    }

    public static Graph<Integer, Integer> getTwoEdgeDeadEndGraph() {
        Graph<Integer, Integer> twoEdgeDeadEndGraph = new SparseGraph<>();
        twoEdgeDeadEndGraph.addEdge(1, 1, 2);
        twoEdgeDeadEndGraph.addEdge(2, 1, 0);
        return twoEdgeDeadEndGraph;
    }

    public static Graph<Integer, Integer> getTwoEdgeDeadEndDirGraph() {
        Graph<Integer, Integer> twoEdgeDeadEndDirGraph = new DirectedSparseGraph<>();
        twoEdgeDeadEndDirGraph.addEdge(1, 1, 2);
        twoEdgeDeadEndDirGraph.addEdge(2, 1, 0);
        return twoEdgeDeadEndDirGraph;
    }

    public static Graph<Integer, Integer> getTwoEdgeLongGraph() {
        Graph<Integer, Integer> twoEdgeLongGraph = new SparseGraph<>();
        twoEdgeLongGraph.addEdge(1, 1, 2);
        twoEdgeLongGraph.addEdge(2, 2, 0);
        return twoEdgeLongGraph;
    }


    public static DirectedGraph<Integer, Integer> getBigGraph() {
        DirectedSparseGraph<Integer, Integer> graph = new DirectedSparseGraph<>();
        int edgeSequence = 0;
        graph.addEdge(++edgeSequence, 1, 20);
        graph.addEdge(++edgeSequence, 1, 19);
        graph.addEdge(++edgeSequence, 20, 21);
        graph.addEdge(++edgeSequence, 19, 21);
        graph.addEdge(++edgeSequence, 18, 20);
        graph.addEdge(++edgeSequence, 16, 18);
        graph.addEdge(++edgeSequence, 17, 16);
        graph.addEdge(++edgeSequence, 6, 17);
        graph.addEdge(++edgeSequence, 22, 83);
        graph.addEdge(++edgeSequence, 21, 15);
        graph.addEdge(++edgeSequence, 21, 6);
        graph.addEdge(++edgeSequence, 15, 22);
        graph.addEdge(++edgeSequence, 83, 6);
        graph.addEdge(++edgeSequence, 6, 13);
        graph.addEdge(++edgeSequence, 70, 13);
        graph.addEdge(++edgeSequence, 82, 83);
        graph.addEdge(++edgeSequence, 70, 84);
        graph.addEdge(++edgeSequence, 83, 70);
        graph.addEdge(++edgeSequence, 74, 85);
        graph.addEdge(++edgeSequence, 85, 75);
        graph.addEdge(++edgeSequence, 75, 88);
        graph.addEdge(++edgeSequence, 71, 73);
        graph.addEdge(++edgeSequence, 83, 14);
        graph.addEdge(++edgeSequence, 88, 87);
        graph.addEdge(++edgeSequence, 87, 86);
        graph.addEdge(++edgeSequence, 86, 84);
        graph.addEdge(++edgeSequence, 58, 84);
        graph.addEdge(++edgeSequence, 86, 58);
        graph.addEdge(++edgeSequence, 58, 25);
        graph.addEdge(++edgeSequence, 25, 82);
        graph.addEdge(++edgeSequence, 24, 60);
        graph.addEdge(++edgeSequence, 23, 60);
        graph.addEdge(++edgeSequence, 2, 23);
        graph.addEdge(++edgeSequence, 12, 2);
        graph.addEdge(++edgeSequence, 60, 68);
        graph.addEdge(++edgeSequence, 61, 60);
        graph.addEdge(++edgeSequence, 13, 61);
        graph.addEdge(++edgeSequence, 59, 13);
        graph.addEdge(++edgeSequence, 61, 59);
        graph.addEdge(++edgeSequence, 59, 24);
        graph.addEdge(++edgeSequence, 24, 61);
        graph.addEdge(++edgeSequence, 68, 70);
        graph.addEdge(++edgeSequence, 83, 70);
        graph.addEdge(++edgeSequence, 89, 82);
        graph.addEdge(++edgeSequence, 25, 89);
        graph.addEdge(++edgeSequence, 25, 91);
        graph.addEdge(++edgeSequence, 91, 69);
        graph.addEdge(++edgeSequence, 50, 25);
        graph.addEdge(++edgeSequence, 50, 90);
        graph.addEdge(++edgeSequence, 57, 90);
        graph.addEdge(++edgeSequence, 92, 39);
        graph.addEdge(++edgeSequence, 39, 57);
        graph.addEdge(++edgeSequence, 38, 39);
        graph.addEdge(++edgeSequence, 90, 38);
        graph.addEdge(++edgeSequence, 12, 38);
        graph.addEdge(++edgeSequence, 90, 12);
        graph.addEdge(++edgeSequence, 69, 3);
        graph.addEdge(++edgeSequence, 3, 12);
        graph.addEdge(++edgeSequence, 11, 12);
        graph.addEdge(++edgeSequence, 11, 37);
        graph.addEdge(++edgeSequence, 11, 5);
        graph.addEdge(++edgeSequence, 5, 36);
        graph.addEdge(++edgeSequence, 36, 11);
        graph.addEdge(++edgeSequence, 36, 4);
        graph.addEdge(++edgeSequence, 5, 55);
        graph.addEdge(++edgeSequence, 4, 35);
        graph.addEdge(++edgeSequence, 35, 34);
        graph.addEdge(++edgeSequence, 34, 10);
        graph.addEdge(++edgeSequence, 34, 55);
        graph.addEdge(++edgeSequence, 55, 70);
        graph.addEdge(++edgeSequence, 70, 40);
        graph.addEdge(++edgeSequence, 40, 26);
        graph.addEdge(++edgeSequence, 26, 92);
        graph.addEdge(++edgeSequence, 93, 40);
        graph.addEdge(++edgeSequence, 52, 41);
        graph.addEdge(++edgeSequence, 52, 93);
        graph.addEdge(++edgeSequence, 52, 27);
        graph.addEdge(++edgeSequence, 40, 27);
        graph.addEdge(++edgeSequence, 27, 54);
        graph.addEdge(++edgeSequence, 44, 54);
        graph.addEdge(++edgeSequence, 9, 27);
        graph.addEdge(++edgeSequence, 10, 43);
        graph.addEdge(++edgeSequence, 8, 44);
        graph.addEdge(++edgeSequence, 43, 8);
        graph.addEdge(++edgeSequence, 54, 42);
        graph.addEdge(++edgeSequence, 42, 53);
        graph.addEdge(++edgeSequence, 53, 9);
        graph.addEdge(++edgeSequence, 98, 42);
        graph.addEdge(++edgeSequence, 52, 67);
        graph.addEdge(++edgeSequence, 67, 98);
        graph.addEdge(++edgeSequence, 62, 98);
        graph.addEdge(++edgeSequence, 67, 99);
        graph.addEdge(++edgeSequence, 99, 76);
        graph.addEdge(++edgeSequence, 76, 75);
        graph.addEdge(++edgeSequence, 75, 97);
        graph.addEdge(++edgeSequence, 76, 97);
        graph.addEdge(++edgeSequence, 42, 7);
        graph.addEdge(++edgeSequence, 7, 28);
        graph.addEdge(++edgeSequence, 28, 62);
        graph.addEdge(++edgeSequence, 62, 29);
        graph.addEdge(++edgeSequence, 29, 30);
        graph.addEdge(++edgeSequence, 95, 29);
        graph.addEdge(++edgeSequence, 32, 31);
        graph.addEdge(++edgeSequence, 31, 33);
        graph.addEdge(++edgeSequence, 31, 7);
        graph.addEdge(++edgeSequence, 31, 94);
        graph.addEdge(++edgeSequence, 94, 32);
        graph.addEdge(++edgeSequence, 33, 95);
        graph.addEdge(++edgeSequence, 45, 29);
        graph.addEdge(++edgeSequence, 45, 96);
        graph.addEdge(++edgeSequence, 97, 46);
        graph.addEdge(++edgeSequence, 29, 97);
        graph.addEdge(++edgeSequence, 46, 96);
        graph.addEdge(++edgeSequence, 96, 49);
        graph.addEdge(++edgeSequence, 49, 65);
        graph.addEdge(++edgeSequence, 65, 47);
        graph.addEdge(++edgeSequence, 47, 0);
        graph.addEdge(++edgeSequence, 64, 0);
        graph.addEdge(++edgeSequence, 47, 64);
        graph.addEdge(++edgeSequence, 65, 64);
        graph.addEdge(++edgeSequence, 48, 65);
        graph.addEdge(++edgeSequence, 64, 80);
        graph.addEdge(++edgeSequence, 80, 81);
        graph.addEdge(++edgeSequence, 81, 79);
        graph.addEdge(++edgeSequence, 79, 66);
        graph.addEdge(++edgeSequence, 66, 50);
        graph.addEdge(++edgeSequence, 51, 78);
        graph.addEdge(++edgeSequence, 50, 51);
        graph.addEdge(++edgeSequence, 50, 78);
        graph.addEdge(++edgeSequence, 50, 77);
        graph.addEdge(++edgeSequence, 78, 77);
        graph.addEdge(++edgeSequence, 77, 63);
        graph.addEdge(++edgeSequence, 63, 0);
        graph.addEdge(++edgeSequence, 96, 0);
        graph.addEdge(++edgeSequence, 97, 0);
        graph.addEdge(++edgeSequence, 97, 63);
        graph.addEdge(++edgeSequence, 63, 76);
        graph.addEdge(++edgeSequence, 55, 39);
        graph.addEdge(++edgeSequence, 57, 56);
        graph.addEdge(++edgeSequence, 9, 52);


        return graph;
    }
}
