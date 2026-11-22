package org.apache.ossie.tutorial.model;

/**
 * SQL Dialects supported in Apache Ossie expression definitions.
 */
public enum SqlDialect {
    ANSI_SQL,
    SNOWFLAKE,
    BIGQUERY,
    DUCKDB,
    POSTGRESQL;

    public static SqlDialect fromString(String val) {
        if (val == null) return ANSI_SQL;
        for (SqlDialect dialect : values()) {
            if (dialect.name().equalsIgnoreCase(val) || dialect.name().replace("_", "").equalsIgnoreCase(val)) {
                return dialect;
            }
        }
        return ANSI_SQL;
    }
}
