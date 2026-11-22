package org.apache.ossie.tutorial.graph;

import org.apache.ossie.tutorial.model.Relationship;
import org.apache.ossie.tutorial.model.SemanticModel;

import java.util.*;

/**
 * Layer 2 Relational Semantic Graph:
 * Models datasets as vertices and relationships as edges.
 * Resolves optimal join paths between datasets and handles multi-hop traversal.
 */
public class SemanticGraph {

    private final Map<String, List<JoinEdge>> adjacencyList = new HashMap<>();

    public SemanticGraph(SemanticModel model) {
        if (model.getDatasets() != null) {
            for (var ds : model.getDatasets()) {
                adjacencyList.put(ds.getName().toLowerCase(), new ArrayList<>());
            }
        }

        if (model.getRelationships() != null) {
            for (Relationship rel : model.getRelationships()) {
                if (rel.getFrom() != null && rel.getTo() != null) {
                    String from = rel.getFrom().toLowerCase();
                    String to = rel.getTo().toLowerCase();

                    JoinEdge forward = new JoinEdge(rel);
                    JoinEdge backward = forward.inverted();

                    adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(forward);
                    adjacencyList.computeIfAbsent(to, k -> new ArrayList<>()).add(backward);
                }
            }
        }
    }

    /**
     * Finds shortest join path between start and end dataset using BFS.
     */
    public Optional<JoinPath> findShortestPath(String startDataset, String endDataset) {
        String start = startDataset.toLowerCase();
        String end = endDataset.toLowerCase();

        if (start.equals(end)) {
            return Optional.of(new JoinPath());
        }

        Queue<String> queue = new ArrayDeque<>();
        Map<String, JoinEdge> edgeTo = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(end)) {
                List<JoinEdge> path = new ArrayList<>();
                String curr = end;
                while (edgeTo.containsKey(curr)) {
                    JoinEdge edge = edgeTo.get(curr);
                    path.add(0, edge);
                    curr = edge.getFromDataset().toLowerCase();
                }
                return Optional.of(new JoinPath(path));
            }

            for (JoinEdge edge : adjacencyList.getOrDefault(current, Collections.emptyList())) {
                String neighbor = edge.getToDataset().toLowerCase();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    edgeTo.put(neighbor, edge);
                    queue.add(neighbor);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Given a set of required datasets, finds the minimum set of join edges connecting them all
     * rooted at the base dataset (e.g., the primary fact table).
     */
    public List<JoinEdge> resolveJoinTree(String rootDataset, Set<String> targetDatasets) {
        Set<String> resolvedDatasets = new HashSet<>();
        resolvedDatasets.add(rootDataset.toLowerCase());

        List<JoinEdge> allEdges = new ArrayList<>();
        Set<String> addedEdgeKeys = new HashSet<>();

        for (String target : targetDatasets) {
            if (target.equalsIgnoreCase(rootDataset)) continue;

            Optional<JoinPath> path = findShortestPath(rootDataset, target);
            if (path.isPresent()) {
                for (JoinEdge edge : path.get().getEdges()) {
                    String key = edge.getFromDataset().toLowerCase() + "->" + edge.getToDataset().toLowerCase();
                    if (addedEdgeKeys.add(key)) {
                        allEdges.add(edge);
                        resolvedDatasets.add(edge.getToDataset().toLowerCase());
                    }
                }
            } else {
                throw new IllegalStateException("No join path exists in semantic graph between " + rootDataset + " and " + target);
            }
        }

        return allEdges;
    }

    public Set<String> getAllDatasets() {
        return adjacencyList.keySet();
    }
}
