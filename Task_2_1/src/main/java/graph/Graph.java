package graph;

import graphpackage.Incidence;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface Graph<N> {
    void addNode(N node);
    void deleteNode(N node);
    void addEdge(N source, N destination);
    void deleteEdge(N source, N destination);
    List<N> getNodeNeighbours(N node);
    void readFromFile(File file) throws IOException;
    List<N> topologicalSort();
    String toString();

    Set<N> getAllNodes();
    @Override
    boolean equals(Object obj);
    @Override
    int hashCode();
}
