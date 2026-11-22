package org.apache.ossie.tutorial.steps;

import org.apache.ossie.tutorial.model.Dataset;
import org.apache.ossie.tutorial.model.Field;
import org.apache.ossie.tutorial.model.Metric;
import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.apache.ossie.tutorial.parser.OssieModelSerializer;

/**
 * STEP 1: Understanding Apache Ossie Core Concepts & Model Loading
 *
 * Run with:
 *   ./gradlew step1
 */
public class Step1_CoreConceptsAndModelLoading {

    public static void main(String[] args) throws Exception {
        System.out.println("""
            ========================================================================
             STEP 1: Apache Ossie Core Concepts & Model Ingestion
            ========================================================================
            Welcome to the Apache Ossie (Incubating) hands-on tutorial!
            
            WHAT IS APACHE OSSIE?
            Formerly known as Open Semantic Interchange (OSI), Apache Ossie is an open
            specification that acts as a universal intermediate representation for data
            semantics. It eliminates "metric drift" and AI hallucinations across BI tools,
            warehouses, and LLMs.

            ANATOMY OF AN OSSIE SEMANTIC MODEL:
              1. version:        Specification version (e.g., "0.3.0")
              2. name:           Unique model identifier
              3. ai_context:     Metadata, synonyms & instructions for AI/LLMs
              4. datasets:       Physical or logical tables/views
                 - primary_key:  Unique row identifiers
                 - fields:       Columns, types, and dimension properties
                 - metrics:      Dataset-scoped aggregations (e.g. SUM, COUNT)
              5. relationships:  Join edges connecting datasets with cardinality
              6. metrics:        Model-wide or derived cross-dataset metrics
            """);

        // 1. Initialize Parser
        OssieModelParser parser = new OssieModelParser();
        OssieModelSerializer serializer = new OssieModelSerializer();

        System.out.println("Loading sample model: /models/ecommerce_model.yaml...");
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");

        System.out.println("\n------------------------------------------------------------------------");
        System.out.printf("Model Name:        %s%n", model.getName());
        System.out.printf("Spec Version:      %s%n", model.getVersion());
        System.out.printf("Description:       %s%n", model.getDescription());
        System.out.println("------------------------------------------------------------------------");

        // 2. Inspect AI Context
        if (model.getAiContext() != null) {
            System.out.println("\n[AI Context - Business Instructions for LLMs]:");
            System.out.println(model.getAiContext().getInstructions().trim());
        }

        // 3. Inspect Datasets
        System.out.println("\n[Datasets Defined in Model (" + model.getDatasets().size() + " total)]:");
        for (Dataset ds : model.getDatasets()) {
            System.out.printf("  • Dataset: %-12s | Source Table: %-12s | PK: %s%n",
                    ds.getName(), ds.getSource(), ds.getPrimaryKey());
            System.out.println("    Fields (" + ds.getFields().size() + "):");
            for (Field f : ds.getFields()) {
                String dimInfo = "";
                if (f.isDimension()) {
                    dimInfo = f.getDimension().isTime()
                            ? " [Time Dimension, Grains: " + f.getDimension().getTimeGrains() + "]"
                            : " [Categorical Dimension]";
                }
                System.out.printf("      - %-15s : %-8s%s%n", f.getName(), f.getDatatype(), dimInfo);
            }

            if (ds.getMetrics() != null && !ds.getMetrics().isEmpty()) {
                System.out.println("    Metrics (" + ds.getMetrics().size() + "):");
                for (Metric m : ds.getMetrics()) {
                    String exprStr = m.getExpression().resolveForDialect(org.apache.ossie.tutorial.model.SqlDialect.ANSI_SQL);
                    System.out.printf("      - %-20s -> %s (Type: %s)%n",
                            m.getName(), exprStr, m.getDatatype());
                }
            }
            System.out.println();
        }

        // 4. Test YAML/JSON Round-trip
        System.out.println("Testing JSON serialization of the model...");
        String jsonPreview = serializer.toJson(model);
        System.out.println("JSON representation snippet (first 350 chars):");
        System.out.println(jsonPreview.substring(0, Math.min(jsonPreview.length(), 350)) + "...\n");

        System.out.println("""
            ========================================================================
             STEP 1 SUMMARY:
             • Apache Ossie documents define datasets, columns, dimensions, and metrics
               in a clean, declarative YAML/JSON format.
             • Every entity can be enriched with ai_context for LLM grounding.
             • Next: Run `./gradlew step2` to learn how Ossie validates semantic integrity!
            ========================================================================
            """);
    }
}
