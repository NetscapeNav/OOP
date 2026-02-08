package graph;

import graphpackage.Incidence;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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

    default boolean areGraphesEqual(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Graph)) {
            return false;
        }
        Graph other = (Graph) obj;
        Set<N> thisNodes = this.getAllNodes();
        Set otherNodes = other.getAllNodes();
        if (!(thisNodes.equals(otherNodes))) {
            return false;
        }
        try {
            for (N node : thisNodes) {
                Set<N> thisNeighbours = new HashSet<>(this.getNodeNeighbours(node));
                Set otherNeighbours = new HashSet<>(other.getNodeNeighbours(node));
                if (!thisNeighbours.equals(otherNeighbours)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    default int calculateHashCode() {
        Set<N> nodes = getAllNodes();
        int hash = nodes.hashCode();
        int neighboursHashSum = 0;
        for (N node : nodes) {
            neighboursHashSum += new HashSet<>(this.getNodeNeighbours(node)).hashCode();
        }
        return 31 * hash + neighboursHashSum;
    }
}
