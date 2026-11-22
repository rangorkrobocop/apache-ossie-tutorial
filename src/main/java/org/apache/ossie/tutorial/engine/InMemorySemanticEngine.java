package org.apache.ossie.tutorial.engine;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.model.SqlDialect;
import org.apache.ossie.tutorial.query.CompiledQuery;
import org.apache.ossie.tutorial.query.SemanticQuery;
import org.apache.ossie.tutorial.query.WideTableQueryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory execution engine using H2 in ANSI/Postgres compatibility mode.
 * Loads sample DDL/data, compiles Ossie Layer 3 semantic queries, and executes them.
 */
public class InMemorySemanticEngine implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(InMemorySemanticEngine.class);

    private final Connection connection;
    private final WideTableQueryCompiler compiler;

    public InMemorySemanticEngine(SemanticModel model) throws SQLException {
        // H2 in PostgreSQL compatibility mode for DATE_TRUNC support
        String jdbcUrl = "jdbc:h2:mem:ossie_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
        this.connection = DriverManager.getConnection(jdbcUrl, "sa", "");
        this.compiler = new WideTableQueryCompiler(model);
    }

    /**
     * Executes seed SQL file from resources.
     */
    public void seedFromResource(String resourcePath) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Seed resource not found: " + resourcePath);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) continue;
                sb.append(line).append("\n");
                if (line.endsWith(";")) {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(sb.toString());
                    }
                    sb.setLength(0);
                }
            }
        }
    }

    public CompiledQuery compile(SemanticQuery query) {
        return compiler.compile(query, SqlDialect.ANSI_SQL);
    }

    public QueryResult execute(SemanticQuery query) throws SQLException {
        CompiledQuery compiled = compile(query);
        return executeSql(compiled.getSql());
    }

    public QueryResult executeSql(String sql) throws SQLException {
        long start = System.currentTimeMillis();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            List<String> colNames = new ArrayList<>(colCount);
            for (int i = 1; i <= colCount; i++) {
                colNames.add(meta.getColumnLabel(i));
            }

            List<List<Object>> rows = new ArrayList<>();
            while (rs.next()) {
                List<Object> row = new ArrayList<>(colCount);
                for (int i = 1; i <= colCount; i++) {
                    row.add(rs.getObject(i));
                }
                rows.add(row);
            }

            long elapsed = System.currentTimeMillis() - start;
            return new QueryResult(colNames, rows, elapsed);
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
