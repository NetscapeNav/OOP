package graphpackage;

import graph.Graph;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AdjList<N> implements Graph<N> {
    private final Map<N, List<N>> adjacencyList = new HashMap<>();

    @Override
    public void addNode(N node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    @Override
    public void deleteNode(N node) {
        adjacencyList.remove(node);
        adjacencyList.values().forEach(neighbors -> neighbors.remove(node));
    }

    @Override
    public void addEdge(N source, N destination) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            throw new IllegalArgumentException("Source or destination doesn't exist.");
        }
        adjacencyList.get(source).add(destination);
    }

    @Override
    public void deleteEdge(N source, N destination) {
        if (adjacencyList.containsKey(source)) {
            adjacencyList.get(source).remove(destination);
        }
    }

    @Override
    public java.util.List<N> getNodeNeighbours(N node) {
        return new ArrayList<>(adjacencyList.getOrDefault(node, Collections.emptyList()));
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
        for (N node : adjacencyList.keySet()) {
            inDegree.put(node, 0);
        }
        for (List<N> neighbors : adjacencyList.values()) {
            for (N neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
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
            for (N neighbor : adjacencyList.getOrDefault(node, Collections.emptyList())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }
        if (sortedList.size() != adjacencyList.size()) {
            throw new IllegalStateException("Cycle detected. Sort is impossible");
        }

        return sortedList;
    }
}
