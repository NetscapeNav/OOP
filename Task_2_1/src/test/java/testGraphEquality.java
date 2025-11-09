import graph.Graph;
import graphpackage.AdjList;
import graphpackage.Adjacency;
import graphpackage.Incidence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class testGraphEquality {
    @Test
    void testGraphEquality() {
        Graph<String> g1 = new AdjList<>();
        g1.addNode("A");
        g1.addNode("B");
        g1.addEdge("A", "B");

        Graph<String> g2 = new Adjacency<>();
        g2.addNode("A");
        g2.addNode("B");
        g2.addEdge("A", "B");

        Graph<String> g3 = new Incidence<>();
        g3.addNode("A");
        g3.addNode("B");
        g3.addEdge("A", "B");

        assertEquals(g1, g2);
        assertEquals(g2, g3);
        assertEquals(g1, g3);

        assertEquals(g1.hashCode(), g2.hashCode());
        assertEquals(g2.hashCode(), g3.hashCode());
        assertEquals(g1.hashCode(), g3.hashCode());
    }
}
