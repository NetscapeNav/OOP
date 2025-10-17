package graphpackage;

import graph.Graph;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Adjacency<N> implements Graph<N> {
    private List<List<Integer>> matrix = new ArrayList<>();
    private final Map<N, Integer> nodeToIndex = new HashMap<>();
    private final List<N> indexToNode = new ArrayList<>();

    @Override
    public void addNode(N node) {
        if (!nodeToIndex.containsKey(node)) {
            int newIndex = indexToNode.size();
            nodeToIndex.put(node, newIndex);
            indexToNode.add(node);

            List<Integer> newRow = new ArrayList<>(Collections.nCopies(indexToNode.size(), 0));
            matrix.add(newRow);

            for (int i = 0; i < indexToNode.size() - 1; i++) {
                matrix.get(i).add(0);
            }
        }
    }

    @Override
    public void deleteNode(N node) {
        Integer indexToRemove = nodeToIndex.get(node);
        if (indexToRemove == null) {
            return;
        }
        matrix.remove((int) indexToRemove);
        for (List<Integer> row : matrix) {
            row.remove((int) indexToRemove);
        }
        nodeToIndex.remove(node);
        indexToNode.remove((int) indexToRemove);
        for (int i = indexToRemove; i < indexToNode.size(); i++) {
            N currentNode = indexToNode.get(i);
            nodeToIndex.put(currentNode, i);
        }
    }

    @Override
    public void addEdge(N source, N destination) {
        Integer sourceIndex = nodeToIndex.get(source);
        Integer destIndex = nodeToIndex.get(destination);
        if (sourceIndex == null || destIndex == null) {
            throw new IllegalArgumentException("Source or destination doesn't exist.");
        }

        matrix.get(sourceIndex).set(destIndex, 1);
    }

    @Override
    public void deleteEdge(N source, N destination) {
        Integer sourceIndex = nodeToIndex.get(source);
        Integer destIndex = nodeToIndex.get(destination);
        if (sourceIndex != null && destIndex != null) {
            matrix.get(sourceIndex).set(destIndex, 0);
        }
    }

    @Override
    public List<N> getNodeNeighbours(N node) {
        Integer nodeIndex = nodeToIndex.get(node);
        if (nodeIndex == null) {
            return Collections.emptyList();
        }

        List<N> neighbors = new ArrayList<>();
        List<Integer> nodeRow = matrix.get(nodeIndex);
        for (int j = 0; j < nodeRow.size(); j++) {
            if (nodeRow.get(j) == 1) {
                neighbors.add(indexToNode.get(j));
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

                    addNode((N) sourceNode);
                    addNode((N) destinationNode);
                    addEdge(sourceNode, destinationNode);
                }
            }
        }
    }

    @Override
    public List<N> topologicalSort() {
        int numNodes = indexToNode.size();
        int[] inDegree = new int[indexToNode.size()];
        for (int j = 0; j < numNodes; j++) {
            for (int i = 0; i < numNodes; i++) {
                if (matrix.get(i).get(j) == 1) {
                    inDegree[j]++;
                }
            }
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numNodes; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }
        List<N> sortedList = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            sortedList.add(indexToNode.get(u));
            for (int v = 0; v < numNodes; v++) {
                if (matrix.get(u).get(v) == 1) {
                    inDegree[v]--;
                    if (inDegree[v] == 0) {
                        queue.add(v);
                    }
                }
            }
        }

        if (sortedList.size() != numNodes) {
            throw new IllegalStateException("Cycle detected. Sort is impossible");
        }

        return sortedList;
    }
}
