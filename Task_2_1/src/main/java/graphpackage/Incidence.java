package graphpackage;

import exceptions.NodeNotFoundException;
import graph.Graph;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Incidence<N> implements Graph<N> {
    private final List<N> nodes = new ArrayList<>();
    private final List<Edge<N>> edges = new ArrayList<>();
    private final List<List<Integer>> matrix = new ArrayList<>();

    private class Edge<T> {
        final T source;
        final T dest;

        Edge(T source, T dest) {
            this.source = source;
            this.dest = dest;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Edge<?> edge = (Edge<?>) obj;
            return Objects.equals(source, edge.source) && Objects.equals(dest, edge.dest);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, dest);
        }
    }

    @Override
    public void addNode(N node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
        }
    }

    @Override
    public void deleteNode(N node) {
        nodes.remove(node);
        edges.removeIf(edge -> edge.source.equals(node) || edge.dest.equals(node));
    }

    @Override
    public void addEdge(N source, N destination) {
        if (!nodes.contains(source) || !nodes.contains(destination)) {
            throw new NodeNotFoundException("Source or destination doesn't exist.");
        }
        edges.add(new Edge<>(source, destination));
    }

    @Override
    public void deleteEdge(N source, N destination) {
        edges.remove(new Edge<>(source, destination));
    }

    @Override
    public List<N> getNodeNeighbours(N node) {
        if (!nodes.contains(node)) {
            return Collections.emptyList();
        }
        List<N> neighbors = new ArrayList<>();
        for (Edge<N> edge : edges) {
            if (edge.source.equals(node)) {
                neighbors.add(edge.dest);
            }
        }
        return neighbors;
    }

    @Override
    public void readFromFile(File file) throws IOException {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    N sourceNode = (N) parts[0];
                    N destinationNode = (N) parts[1];
                    addNode(sourceNode);
                    addNode(destinationNode);
                    addEdge(sourceNode, destinationNode);
                }
            }
        }
    }

    @Override
    public List<N> topologicalSort() {
        Map<N, Integer> inDegree = new HashMap<>();
        for (N node : nodes) {
            inDegree.put(node, 0);
        }
        for (Edge<N> edge : edges) {
            inDegree.put(edge.dest, inDegree.get(edge.dest) + 1);
        }
        Queue<N> queue = new LinkedList<>();
        for (Map.Entry<N, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }
        List<N> sortedList = new ArrayList<>();
        while (!queue.isEmpty()) {
            N node = queue.poll();
            sortedList.add(node);
            for (N neighbor : getNodeNeighbours(node)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }
        if (sortedList.size() != nodes.size()) {
            throw new IllegalStateException("Cycle detected. Sort is impossible");
        }
        return sortedList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Incidence (Edge List):\n");
        sb.append("Nodes: ").append(nodes).append("\n");
        sb.append("Edges: \n");
        for (Edge<N> edge : edges) {
            sb.append("  (").append(edge.source).append(" -> ").append(edge.dest).append(")\n");
        }
        return sb.toString();
    }

    @Override
    public Set<N> getAllNodes() {
        return new HashSet<>(nodes);
    }

    @Override
    public boolean equals(Object obj) {
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

    @Override
    public int hashCode() {
        Set<N> nodes = getAllNodes();
        int hash = nodes.hashCode();
        int neighboursHashSum = 0;
        for (N node : nodes) {
            neighboursHashSum += new HashSet<>(this.getNodeNeighbours(node)).hashCode(); //
        }
        return 31 * hash + neighboursHashSum;
    }
}
