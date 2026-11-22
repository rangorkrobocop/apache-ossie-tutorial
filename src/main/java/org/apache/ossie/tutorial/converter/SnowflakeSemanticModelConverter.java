package org.apache.ossie.tutorial.converter;

import org.apache.ossie.tutorial.model.Dataset;
import org.apache.ossie.tutorial.model.Field;
import org.apache.ossie.tutorial.model.Metric;
import org.apache.ossie.tutorial.model.SemanticModel;

/**
 * Converter between Apache Ossie and Snowflake Semantic Model / Views.
 */
public class SnowflakeSemanticModelConverter {

    public String convertToSnowflakeSemanticModel(SemanticModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("-- Snowflake Semantic Model View generated from Apache Ossie: ").append(model.getName()).append("\n");
        sb.append("-- Specification Version: ").append(model.getVersion()).append("\n\n");

        if (model.getDatasets() != null) {
            for (Dataset ds : model.getDatasets()) {
                sb.append("CREATE OR REPLACE SECURE VIEW ").append(ds.getName()).append("_semantic_view AS\n");
                sb.append("SELECT\n");

                int fieldCount = ds.getFields().size();
                for (int i = 0; i < fieldCount; i++) {
                    Field f = ds.getFields().get(i);
                    sb.append("  ").append(f.getName());
                    if (f.getDescription() != null && !f.getDescription().isBlank()) {
                        sb.append(" COMMENT '").append(f.getDescription().replace("'", "''")).append("'");
                    }
                    if (i < fieldCount - 1 || (ds.getMetrics() != null && !ds.getMetrics().isEmpty())) {
                        sb.append(",");
                    }
                    sb.append("\n");
                }

                if (ds.getMetrics() != null) {
                    for (int i = 0; i < ds.getMetrics().size(); i++) {
                        Metric m = ds.getMetrics().get(i);
                        String expr = m.getExpression() != null ? m.getExpression().getSql() : "NULL";
                        sb.append("  ").append(expr).append(" AS ").append(m.getName());
                        if (i < ds.getMetrics().size() - 1) sb.append(",");
                        sb.append("\n");
                    }
                }

                sb.append("FROM ").append(ds.getSource()).append(";\n\n");
            }
        }

        return sb.toString();
    }
}
