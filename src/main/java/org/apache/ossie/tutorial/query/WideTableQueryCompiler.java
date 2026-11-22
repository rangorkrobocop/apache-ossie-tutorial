package org.apache.ossie.tutorial.query;

import org.apache.ossie.tutorial.graph.JoinEdge;
import org.apache.ossie.tutorial.graph.SemanticGraph;
import org.apache.ossie.tutorial.model.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compiler for Apache Ossie Layer 3 Wide-Table Queries.
 * Resolves required datasets, discovers join paths via the Semantic Graph,
 * handles time grains (e.g. order_date[month]), translates dialect-specific functions,
 * and emits executable SQL.
 */
public class WideTableQueryCompiler {

    private final SemanticModel model;
    private final SemanticGraph graph;

    // Pattern for dimension with time grain: e.g. orders.order_date[month] or order_date[month]
    private static final Pattern TIME_GRAIN_PATTERN = Pattern.compile("([^.\\[\\]]+(?:\\.[^.\\[\\]]+)?)\\[([a-zA-Z]+)\\]");

    public WideTableQueryCompiler(SemanticModel model) {
        this.model = model;
        this.graph = new SemanticGraph(model);
    }

    public CompiledQuery compile(SemanticQuery query, SqlDialect targetDialect) {
        if (query.getMetrics().isEmpty() && query.getDimensions().isEmpty()) {
            throw new IllegalArgumentException("Semantic query must specify at least one metric or dimension");
        }

        Set<String> requiredDatasets = new LinkedHashSet<>();
        List<String> selectExpressions = new ArrayList<>();
        List<String> groupByExpressions = new ArrayList<>();

        // 1. Resolve Dimensions
        for (String dimSpec : query.getDimensions()) {
            ResolvedDimension resolved = resolveDimension(dimSpec, targetDialect);
            requiredDatasets.add(resolved.datasetName);
            selectExpressions.add(resolved.sqlSelectExpression + " AS " + resolved.alias);
            groupByExpressions.add(resolved.sqlSelectExpression);
        }

        // 2. Resolve Metrics
        for (String metricName : query.getMetrics()) {
            ResolvedMetric resolved = resolveMetric(metricName, targetDialect);
            if (resolved.datasetName != null) {
                requiredDatasets.add(resolved.datasetName);
            }
            selectExpressions.add(resolved.sqlExpression + " AS " + resolved.metricName);
        }

        // 3. Resolve Filters
        List<String> resolvedFilters = new ArrayList<>();
        if (query.getFilters() != null) {
            for (String filter : query.getFilters()) {
                resolvedFilters.add(filter);
                // Extract possible dataset prefix (e.g. customers.status -> customers)
                int dotIdx = filter.indexOf('.');
                if (dotIdx > 0) {
                    String prefix = filter.substring(0, dotIdx).trim();
                    if (model.findDataset(prefix).isPresent()) {
                        requiredDatasets.add(prefix);
                    }
                }
            }
        }

        if (requiredDatasets.isEmpty()) {
            // Default to first dataset in model if none specified
            if (!model.getDatasets().isEmpty()) {
                requiredDatasets.add(model.getDatasets().get(0).getName());
            } else {
                throw new IllegalStateException("No datasets available in model");
            }
        }

        // 4. Select Root (Fact) Dataset - prioritize dataset owning the queried metrics
        String rootDataset = selectRootDataset(requiredDatasets, query.getMetrics());

        // 5. Build Joins using Semantic Graph
        List<JoinEdge> joins = graph.resolveJoinTree(rootDataset, requiredDatasets);

        // 6. Assemble SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n  ");
        sql.append(String.join(",\n  ", selectExpressions));

        Dataset rootDs = model.findDataset(rootDataset).orElseThrow();
        sql.append("\nFROM ").append(rootDs.getSource()).append(" AS ").append(rootDataset);

        Set<String> joinedAliases = new HashSet<>();
        joinedAliases.add(rootDataset.toLowerCase());

        for (JoinEdge edge : joins) {
            String targetDsName = edge.getToDataset();
            if (joinedAliases.add(targetDsName.toLowerCase())) {
                Dataset targetDs = model.findDataset(targetDsName).orElseThrow();
                String joinSqlType = switch (edge.getJoinType()) {
                    case INNER -> "INNER JOIN";
                    case FULL_OUTER -> "FULL OUTER JOIN";
                    case RIGHT_OUTER -> "RIGHT OUTER JOIN";
                    default -> "LEFT JOIN";
                };
                sql.append("\n").append(joinSqlType).append(" ")
                   .append(targetDs.getSource()).append(" AS ").append(targetDsName)
                   .append(" ON ").append(edge.toJoinConditionSql());
            }
        }

        if (!resolvedFilters.isEmpty()) {
            sql.append("\nWHERE ");
            sql.append(String.join(" AND ", resolvedFilters));
        }

        if (!groupByExpressions.isEmpty()) {
            sql.append("\nGROUP BY\n  ");
            sql.append(String.join(",\n  ", groupByExpressions));
        }

        if (query.getOrderBy() != null && !query.getOrderBy().isEmpty()) {
            List<String> orderClauses = new ArrayList<>();
            for (OrderByClause ob : query.getOrderBy()) {
                orderClauses.add(ob.getField() + " " + ob.getDirection());
            }
            sql.append("\nORDER BY ").append(String.join(", ", orderClauses));
        }

        if (query.getLimit() != null && query.getLimit() > 0) {
            sql.append("\nLIMIT ").append(query.getLimit());
        }

        return new CompiledQuery(
                sql.toString(),
                targetDialect,
                rootDataset,
                joins,
                query.getMetrics(),
                query.getDimensions()
        );
    }

    private String selectRootDataset(Set<String> requiredDatasets, List<String> queryMetrics) {
        // Prioritize dataset that owns one of the metrics requested in the query
        if (queryMetrics != null) {
            for (String mName : queryMetrics) {
                Optional<Dataset> ds = model.findDatasetForMetric(mName);
                if (ds.isPresent() && requiredDatasets.contains(ds.get().getName())) {
                    return ds.get().getName();
                }
            }
        }
        // Fallback: prefer dataset with metrics
        for (String ds : requiredDatasets) {
            Dataset dataset = model.findDataset(ds).orElse(null);
            if (dataset != null && dataset.getMetrics() != null && !dataset.getMetrics().isEmpty()) {
                return ds;
            }
        }
        return requiredDatasets.iterator().next();
    }

    private ResolvedDimension resolveDimension(String dimSpec, SqlDialect dialect) {
        Matcher matcher = TIME_GRAIN_PATTERN.matcher(dimSpec);
        String fieldCandidate = dimSpec;
        String timeGrain = null;

        if (matcher.matches()) {
            fieldCandidate = matcher.group(1);
            timeGrain = matcher.group(2).toLowerCase();
        }

        final String rawField = fieldCandidate;
        String datasetName;
        String fieldName;

        if (rawField.contains(".")) {
            String[] parts = rawField.split("\\.", 2);
            datasetName = parts[0];
            fieldName = parts[1];
        } else {
            Dataset ds = model.findDatasetForField(rawField)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown dimension field: " + rawField));
            datasetName = ds.getName();
            fieldName = rawField;
        }

        String columnRef = datasetName + "." + fieldName;
        String alias = (datasetName + "_" + fieldName + (timeGrain != null ? "_" + timeGrain : "")).toLowerCase();

        if (timeGrain != null) {
            String timeExpr = switch (dialect) {
                case DUCKDB, POSTGRESQL -> "DATE_TRUNC('" + timeGrain + "', " + columnRef + ")";
                case BIGQUERY -> "DATE_TRUNC(" + columnRef + ", " + timeGrain.toUpperCase() + ")";
                case SNOWFLAKE -> "DATE_TRUNC('" + timeGrain.toUpperCase() + "', " + columnRef + ")";
                default -> "DATE_TRUNC('" + timeGrain + "', " + columnRef + ")";
            };
            return new ResolvedDimension(datasetName, fieldName, timeExpr, alias);
        }

        return new ResolvedDimension(datasetName, fieldName, columnRef, alias);
    }

    private ResolvedMetric resolveMetric(String metricName, SqlDialect dialect) {
        Metric metric = model.findMetric(metricName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown metric: " + metricName));

        Dataset parentDataset = model.findDatasetForMetric(metricName).orElse(null);
        String datasetName = parentDataset != null ? parentDataset.getName() : null;

        String exprStr = metric.getExpression().resolveForDialect(dialect);
        if (exprStr == null) {
            exprStr = metric.getExpression().getSql();
        }

        // Replace 'source.' prefix with dataset alias if present
        if (datasetName != null && exprStr.contains("source.")) {
            exprStr = exprStr.replace("source.", datasetName + ".");
        }

        return new ResolvedMetric(metricName, datasetName, exprStr);
    }

    private record ResolvedDimension(String datasetName, String fieldName, String sqlSelectExpression, String alias) {}
    private record ResolvedMetric(String metricName, String datasetName, String sqlExpression) {}
}
