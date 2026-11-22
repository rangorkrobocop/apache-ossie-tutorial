package org.apache.ossie.tutorial.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tabular result of an executed semantic query.
 */
public class QueryResult {

    private final List<String> columnNames;
    private final List<List<Object>> rows;
    private final long executionTimeMs;

    public QueryResult(List<String> columnNames, List<List<Object>> rows, long executionTimeMs) {
        this.columnNames = columnNames;
        this.rows = rows;
        this.executionTimeMs = executionTimeMs;
    }

    public List<String> getColumnNames() {
        return Collections.unmodifiableList(columnNames);
    }

    public List<List<Object>> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getRowCount() {
        return rows.size();
    }

    /**
     * Formats results as an ASCII table for terminal display.
     */
    public String toPrettyTable() {
        if (columnNames.isEmpty()) {
            return "(Empty query result)";
        }

        int[] colWidths = new int[columnNames.size()];
        for (int i = 0; i < columnNames.size(); i++) {
            colWidths[i] = columnNames.get(i).length();
        }

        for (List<Object> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                Object val = row.get(i);
                String s = val != null ? val.toString() : "NULL";
                if (s.length() > colWidths[i]) {
                    colWidths[i] = s.length();
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        // Top border
        sb.append("+");
        for (int w : colWidths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        sb.append("\n");

        // Headers
        sb.append("|");
        for (int i = 0; i < columnNames.size(); i++) {
            sb.append(" ").append(padRight(columnNames.get(i), colWidths[i])).append(" |");
        }
        sb.append("\n");

        // Header separator
        sb.append("+");
        for (int w : colWidths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        sb.append("\n");

        // Data rows
        for (List<Object> row : rows) {
            sb.append("|");
            for (int i = 0; i < columnNames.size(); i++) {
                Object val = i < row.size() ? row.get(i) : null;
                String s = val != null ? val.toString() : "NULL";
                sb.append(" ").append(padRight(s, colWidths[i])).append(" |");
            }
            sb.append("\n");
        }

        // Bottom border
        sb.append("+");
        for (int w : colWidths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        sb.append("\n");
        sb.append(rows.size()).append(" rows returned in ").append(executionTimeMs).append(" ms\n");

        return sb.toString();
    }

    private String padRight(String s, int width) {
        if (s.length() >= width) return s;
        return s + " ".repeat(width - s.length());
    }
}
