package org.apache.ossie.tutorial.query;

import org.apache.ossie.tutorial.graph.JoinEdge;
import org.apache.ossie.tutorial.model.SqlDialect;

import java.util.List;

/**
 * Result of compiling an Apache Ossie Layer 3 semantic query into executable SQL.
 */
public class CompiledQuery {

    private final String sql;
    private final SqlDialect dialect;
    private final String baseDataset;
    private final List<JoinEdge> joinsUsed;
    private final List<String> selectedMetrics;
    private final List<String> selectedDimensions;

    public CompiledQuery(String sql, SqlDialect dialect, String baseDataset,
                         List<JoinEdge> joinsUsed, List<String> selectedMetrics, List<String> selectedDimensions) {
        this.sql = sql;
        this.dialect = dialect;
        this.baseDataset = baseDataset;
        this.joinsUsed = joinsUsed;
        this.selectedMetrics = selectedMetrics;
        this.selectedDimensions = selectedDimensions;
    }

    public String getSql() {
        return sql;
    }

    public SqlDialect getDialect() {
        return dialect;
    }

    public String getBaseDataset() {
        return baseDataset;
    }

    public List<JoinEdge> getJoinsUsed() {
        return joinsUsed;
    }

    public List<String> getSelectedMetrics() {
        return selectedMetrics;
    }

    public List<String> getSelectedDimensions() {
        return selectedDimensions;
    }

    @Override
    public String toString() {
        return "-- Compiled Ossie Semantic Query [" + dialect + "]\n" + sql;
    }
}
