package org.example;

import graph.Graph;
import graphpackage.AdjList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Graph<String> graph = new AdjList<>();
        try {
            File graphFile = new File("graph.txt");
            graph.readFromFile(graphFile);
            System.out.println("Neighbours of node 'A': " + graph.getNodeNeighbours("A"));
            List<String> sortedList = graph.topologicalSort();
            System.out.println(sortedList);
        }catch (IOException e) {
            System.err.println("File reading error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}