package org.apache.ossie.tutorial.converter;

import org.apache.ossie.tutorial.model.Dataset;
import org.apache.ossie.tutorial.model.Field;
import org.apache.ossie.tutorial.model.Metric;
import org.apache.ossie.tutorial.model.SemanticModel;

/**
 * Converter between Apache Ossie and dbt Semantic Layer (MetricFlow).
 * Demonstrates Ossie's role as a vendor-neutral intermediate semantic format.
 */
public class DbtMetricFlowConverter {

    public String convertToDbtSemanticLayerYaml(SemanticModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Generated from Apache Ossie Semantic Model: ").append(model.getName()).append("\n");
        sb.append("version: 2\n\n");
        sb.append("semantic_models:\n");

        if (model.getDatasets() != null) {
            for (Dataset ds : model.getDatasets()) {
                sb.append("  - name: ").append(ds.getName()).append("\n");
                sb.append("    description: \"").append(ds.getDescription() != null ? ds.getDescription() : "").append("\"\n");
                sb.append("    model: ref('").append(ds.getSource()).append("')\n");

                // Entities (Primary keys)
                if (ds.getPrimaryKey() != null && !ds.getPrimaryKey().isEmpty()) {
                    sb.append("    entities:\n");
                    for (String pk : ds.getPrimaryKey()) {
                        sb.append("      - name: ").append(pk).append("\n");
                        sb.append("        type: primary\n");
                    }
                }

                // Dimensions
                sb.append("    dimensions:\n");
                for (Field field : ds.getFields()) {
                    if (field.isDimension()) {
                        sb.append("      - name: ").append(field.getName()).append("\n");
                        if (field.getDimension().isTime()) {
                            sb.append("        type: time\n");
                            sb.append("        type_params:\n");
                            sb.append("          time_granularity: day\n");
                        } else {
                            sb.append("        type: categorical\n");
                        }
                    }
                }

                // Measures
                if (ds.getMetrics() != null && !ds.getMetrics().isEmpty()) {
                    sb.append("    measures:\n");
                    for (Metric m : ds.getMetrics()) {
                        sb.append("      - name: ").append(m.getName()).append("\n");
                        sb.append("        description: \"").append(m.getDescription() != null ? m.getDescription() : "").append("\"\n");
                        sb.append("        agg: sum\n");
                        String expr = m.getExpression() != null
                                ? m.getExpression().resolveForDialect(org.apache.ossie.tutorial.model.SqlDialect.ANSI_SQL)
                                : m.getName();
                        sb.append("        expr: \"").append(expr != null ? expr : m.getName()).append("\"\n");
                    }
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
