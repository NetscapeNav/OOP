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
}
