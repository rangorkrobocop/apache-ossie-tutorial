package org.apache.ossie.tutorial.validator;

import org.apache.ossie.tutorial.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Validates Apache Ossie models according to the specification rules:
 * - Dataset source and uniqueness
 * - Relationship endpoints (datasets and columns exist)
 * - Metric definitions and expressions
 * - AI Context completeness
 */
public class OssieModelValidator {

    public ValidationReport validate(SemanticModel model) {
        ValidationReport report = new ValidationReport();

        if (model == null) {
            report.addIssue(ValidationIssue.error("Root", "SemanticModel is null", "Provide a non-null model"));
            return report;
        }

        // 1. Model basics
        if (model.getName() == null || model.getName().isBlank()) {
            report.addIssue(ValidationIssue.error("Model", "Model name is required", "Add 'name: my_model'"));
        }
        if (model.getVersion() == null || model.getVersion().isBlank()) {
            report.addIssue(ValidationIssue.warning("Model", "Model version is missing", "Specify 'version: 0.3.0'"));
        }
        if (model.getAiContext() == null || model.getAiContext().getInstructions() == null) {
            report.addIssue(ValidationIssue.info("Model.ai_context", "Missing AI instructions",
                    "Add 'ai_context.instructions' to guide AI agents and prevent metric misinterpretation"));
        }

        // 2. Datasets
        if (model.getDatasets() == null || model.getDatasets().isEmpty()) {
            report.addIssue(ValidationIssue.error("Datasets", "Model has no datasets defined", "Define at least one dataset"));
            return report;
        }

        Set<String> datasetNames = new HashSet<>();
        for (Dataset dataset : model.getDatasets()) {
            String dName = dataset.getName();
            if (dName == null || dName.isBlank()) {
                report.addIssue(ValidationIssue.error("Dataset", "Dataset missing 'name'", "Provide a dataset name"));
                continue;
            }
            if (!datasetNames.add(dName.toLowerCase())) {
                report.addIssue(ValidationIssue.error("Dataset." + dName, "Duplicate dataset name: " + dName, "Ensure all dataset names are unique"));
            }

            // Check primary keys
            if (dataset.getPrimaryKey() == null || dataset.getPrimaryKey().isEmpty()) {
                report.addIssue(ValidationIssue.warning("Dataset." + dName,
                        "Dataset '" + dName + "' has no primary_key declared",
                        "Specify 'primary_key: [id_column]' for deterministic join resolution"));
            } else {
                for (String pk : dataset.getPrimaryKey()) {
                    if (dataset.findField(pk).isEmpty()) {
                        report.addIssue(ValidationIssue.error("Dataset." + dName + ".primary_key",
                                "Primary key field '" + pk + "' not defined in dataset fields",
                                "Add field '" + pk + "' under fields list"));
                    }
                }
            }

            // Check fields
            if (dataset.getFields() == null || dataset.getFields().isEmpty()) {
                report.addIssue(ValidationIssue.error("Dataset." + dName,
                        "Dataset '" + dName + "' has no fields defined", "Define fields for this dataset"));
            } else {
                Set<String> fieldNames = new HashSet<>();
                for (Field field : dataset.getFields()) {
                    if (field.getName() == null || field.getName().isBlank()) {
                        report.addIssue(ValidationIssue.error("Dataset." + dName + ".field",
                                "Field missing name", "Provide field name"));
                        continue;
                    }
                    if (!fieldNames.add(field.getName().toLowerCase())) {
                        report.addIssue(ValidationIssue.error("Dataset." + dName + "." + field.getName(),
                                "Duplicate field name: " + field.getName(), "Make field names unique within dataset"));
                    }
                }
            }

            // Check dataset-scoped metrics
            if (dataset.getMetrics() != null) {
                for (Metric m : dataset.getMetrics()) {
                    validateMetric(report, "Dataset." + dName + ".metric." + m.getName(), m);
                }
            }
        }

        // 3. Relationships
        if (model.getRelationships() != null) {
            Set<String> relNames = new HashSet<>();
            for (Relationship rel : model.getRelationships()) {
                String rName = rel.getName() != null ? rel.getName() : (rel.getFrom() + "->" + rel.getTo());
                if (!relNames.add(rName.toLowerCase())) {
                    report.addIssue(ValidationIssue.warning("Relationship." + rName,
                            "Duplicate relationship name or link: " + rName, "Use distinct relationship names"));
                }

                // Check endpoints exist
                Optional<Dataset> fromDs = model.findDataset(rel.getFrom());
                Optional<Dataset> toDs = model.findDataset(rel.getTo());

                if (fromDs.isEmpty()) {
                    report.addIssue(ValidationIssue.error("Relationship." + rName,
                            "Source dataset '" + rel.getFrom() + "' does not exist",
                            "Verify 'from: " + rel.getFrom() + "' matches an existing dataset"));
                }
                if (toDs.isEmpty()) {
                    report.addIssue(ValidationIssue.error("Relationship." + rName,
                            "Target dataset '" + rel.getTo() + "' does not exist",
                            "Verify 'to: " + rel.getTo() + "' matches an existing dataset"));
                }

                // Check join columns
                if (rel.getFromColumns() == null || rel.getFromColumns().isEmpty()) {
                    report.addIssue(ValidationIssue.error("Relationship." + rName,
                            "Missing from_columns in relationship", "Specify 'from_columns: [col]'"));
                } else if (fromDs.isPresent()) {
                    for (String col : rel.getFromColumns()) {
                        if (fromDs.get().findField(col).isEmpty()) {
                            report.addIssue(ValidationIssue.error("Relationship." + rName,
                                    "Column '" + col + "' does not exist in source dataset '" + rel.getFrom() + "'",
                                    "Ensure join column is declared in " + rel.getFrom()));
                        }
                    }
                }

                if (rel.getToColumns() == null || rel.getToColumns().isEmpty()) {
                    report.addIssue(ValidationIssue.error("Relationship." + rName,
                            "Missing to_columns in relationship", "Specify 'to_columns: [col]'"));
                } else if (toDs.isPresent()) {
                    for (String col : rel.getToColumns()) {
                        if (toDs.get().findField(col).isEmpty()) {
                            report.addIssue(ValidationIssue.error("Relationship." + rName,
                                    "Column '" + col + "' does not exist in target dataset '" + rel.getTo() + "'",
                                    "Ensure join column is declared in " + rel.getTo()));
                        }
                    }
                }
            }
        }

        // 4. Model-scoped metrics
        if (model.getMetrics() != null) {
            for (Metric m : model.getMetrics()) {
                validateMetric(report, "Model.metric." + m.getName(), m);
            }
        }

        return report;
    }

    private void validateMetric(ValidationReport report, String component, Metric metric) {
        if (metric.getName() == null || metric.getName().isBlank()) {
            report.addIssue(ValidationIssue.error(component, "Metric missing name", "Add 'name: my_metric'"));
            return;
        }
        if (metric.getExpression() == null) {
            report.addIssue(ValidationIssue.error(component, "Metric missing expression",
                    "Add 'expression: { sql: \"SUM(col)\" }'"));
        } else if (metric.getExpression().getSql() == null &&
                (metric.getExpression().getDialects() == null || metric.getExpression().getDialects().isEmpty())) {
            report.addIssue(ValidationIssue.error(component, "Metric expression has no SQL or dialects defined",
                    "Specify a SQL expression"));
        }
    }
}
