package org.example;

import file.SubstringSearch;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<Long> results = SubstringSearch.find("scherlock.txt", "Alice");
            System.out.println(results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}