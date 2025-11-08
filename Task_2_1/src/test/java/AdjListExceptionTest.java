import graph.Graph;
import graphpackage.AdjList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import exceptions.NodeNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class AdjListExceptionTest {

    @Test
    public void testAddEdgeNodeNotFound() {
        Graph<String> graph = new AdjList<>();
        graph.addNode("A");
        assertThrows(NodeNotFoundException.class, () -> graph.addEdge("A", "B"));
    }

    @Test
    public void testTopologicalSortCycle() {
        Graph<String> graph = new AdjList<>();
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");
        assertThrows(IllegalStateException.class, graph::topologicalSort);
    }

    @Test
    public void testReadFromFileIOException() throws IOException {
        Graph<String> graph = new AdjList<>();
        File nonExistentFile = new File("nonExistentFile.txt");
        assertThrows(IOException.class, () -> graph.readFromFile(nonExistentFile));
    }
}
