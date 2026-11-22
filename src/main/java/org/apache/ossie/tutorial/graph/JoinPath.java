package org.apache.ossie.tutorial.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A multi-hop path of join edges connecting two or more datasets.
 */
public class JoinPath {

    private final List<JoinEdge> edges = new ArrayList<>();

    public JoinPath() {}

    public JoinPath(List<JoinEdge> edges) {
        this.edges.addAll(edges);
    }

    public void addEdge(JoinEdge edge) {
        this.edges.add(edge);
    }

    public List<JoinEdge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public boolean isEmpty() {
        return edges.isEmpty();
    }

    public int hopCount() {
        return edges.size();
    }

    public boolean containsDataset(String datasetName) {
        for (JoinEdge edge : edges) {
            if (edge.getFromDataset().equalsIgnoreCase(datasetName) ||
                edge.getToDataset().equalsIgnoreCase(datasetName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (edges.isEmpty()) return "(direct / no join)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < edges.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(edges.get(i).getFromDataset())
              .append(" -> ")
              .append(edges.get(i).getToDataset());
        }
        return sb.toString();
    }
}
