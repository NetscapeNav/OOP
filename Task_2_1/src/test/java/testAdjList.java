import graph.Graph;
import graphpackage.AdjList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class testAdjList {
    private Graph<String> graph;

    @BeforeEach
    void setUp() {
        graph = new AdjList<>();
    }

    @Test
    void testAddNodeAndEdge() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addEdge("A", "B");

        List<String> neighborsOfA = graph.getNodeNeighbours("A");
        assertNotNull(neighborsOfA);
        assertEquals(1, neighborsOfA.size());

        assertTrue(neighborsOfA.contains("B"));
        List<String> neighborsOfB = graph.getNodeNeighbours("B");
        assertTrue(neighborsOfB.isEmpty());
    }

    @Test
    void testDeleteEdge() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addEdge("A", "B");
        graph.deleteEdge("A", "B");

        List<String> neighborsOfA = graph.getNodeNeighbours("A");
        assertTrue(neighborsOfA.isEmpty());
    }

    @Test
    void testDeleteNode() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        graph.deleteNode("B");

        List<String> neighborsOfA = graph.getNodeNeighbours("A");
        assertTrue(neighborsOfA.isEmpty());

        List<String> neighborsOfC = graph.getNodeNeighbours("C");
        assertTrue(neighborsOfC.isEmpty());
    }

    @Test
    void testTopologicalSort() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");

        List<String> sorted = graph.topologicalSort();

        assertTrue(sorted.indexOf("A") < sorted.indexOf("B"));
        assertTrue(sorted.indexOf("A") < sorted.indexOf("C"));
        assertTrue(sorted.indexOf("B") < sorted.indexOf("D"));
        assertTrue(sorted.indexOf("C") < sorted.indexOf("D"));
    }

    @Test
    void testTopologicalSortWithCycle() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addEdge("A", "B");
        graph.addEdge("B", "A");

        assertThrows(IllegalStateException.class, () -> {
            graph.topologicalSort();
        });
    }

    @Test
    void testToString() {
        graph.addNode("A");
        graph.addNode("B");
        graph.addEdge("A", "B");
        String s = graph.toString();

        assertNotNull(s);
        assertTrue(s.contains("A"));
        assertTrue(s.contains("B"));
    }
}
