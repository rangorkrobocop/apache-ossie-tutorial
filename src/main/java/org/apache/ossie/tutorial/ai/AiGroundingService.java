package org.apache.ossie.tutorial.ai;

import org.apache.ossie.tutorial.model.Dataset;
import org.apache.ossie.tutorial.model.Field;
import org.apache.ossie.tutorial.model.Metric;
import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.query.SemanticQuery;

import java.util.*;

/**
 * AI Grounding Service:
 * Demonstrates how Apache Ossie eliminates AI hallucinations by:
 * 1. Generating rich system prompts from embedded ai_context.
 * 2. Exposing MCP (Model Context Protocol) and Function Calling tools to AI agents.
 * 3. Resolving natural language synonyms to canonical business metrics.
 */
public class AiGroundingService {

    private final SemanticModel model;

    public AiGroundingService(SemanticModel model) {
        this.model = model;
    }

    /**
     * Generates a grounded system prompt for an LLM agent based on the Ossie semantic model.
     */
    public String generateSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an AI Business Intelligence Analyst assistant grounded with Apache Ossie.\n");
        sb.append("Semantic Model: ").append(model.getName()).append(" (v").append(model.getVersion()).append(")\n");

        if (model.getAiContext() != null && model.getAiContext().getInstructions() != null) {
            sb.append("\n## Business Grounding Instructions:\n");
            sb.append(model.getAiContext().getInstructions()).append("\n");
        }

        sb.append("\n## Available Business Metrics:\n");
        for (Metric metric : getAllMetrics()) {
            sb.append("- **").append(metric.getName()).append("**: ")
              .append(metric.getDescription() != null ? metric.getDescription() : "No description")
              .append(" (Type: ").append(metric.getDatatype()).append(")");
            if (metric.getAiContext() != null && !metric.getAiContext().getSynonyms().isEmpty()) {
                sb.append(" [Synonyms: ").append(String.join(", ", metric.getAiContext().getSynonyms())).append("]");
            }
            sb.append("\n");
        }

        sb.append("\n## Available Dimensions for Slicing:\n");
        if (model.getDatasets() != null) {
            for (Dataset ds : model.getDatasets()) {
                if (ds.getFields() != null) {
                    for (Field field : ds.getFields()) {
                        if (field.isDimension()) {
                            sb.append("- **").append(ds.getName()).append(".").append(field.getName()).append("**");
                            if (field.getDimension().isTime()) {
                                sb.append(" (Time grains: ")
                                  .append(String.join(", ", field.getDimension().getTimeGrains()))
                                  .append(")");
                            }
                            if (field.getDimension().getSynonyms() != null && !field.getDimension().getSynonyms().isEmpty()) {
                                sb.append(" [Synonyms: ").append(String.join(", ", field.getDimension().getSynonyms())).append("]");
                            }
                            sb.append("\n");
                        }
                    }
                }
            }
        }

        sb.append("\n## Guardrails:\n");
        sb.append("1. Never invent or hallucinate metric names or formulas. Use only the declared Ossie metrics.\n");
        sb.append("2. When answering analytical queries, construct an Ossie Wide-Table semantic query with metrics and dimensions.\n");
        sb.append("3. For time-series analysis, append the appropriate time grain, e.g. `order_date[month]`.\n");

        return sb.toString();
    }

    /**
     * Resolves natural language user query terms to canonical metrics using synonyms and descriptions.
     */
    public Optional<String> matchMetricFromSynonym(String userInput) {
        String lower = userInput.toLowerCase();
        for (Metric metric : getAllMetrics()) {
            if (lower.contains(metric.getName().toLowerCase())) {
                return Optional.of(metric.getName());
            }
            if (metric.getAiContext() != null && metric.getAiContext().getSynonyms() != null) {
                for (String syn : metric.getAiContext().getSynonyms()) {
                    if (lower.contains(syn.toLowerCase())) {
                        return Optional.of(metric.getName());
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns MCP / LLM tool definitions for semantic querying.
     */
    public List<McpToolDefinition> getMcpToolDefinitions() {
        List<McpToolDefinition> tools = new ArrayList<>();

        Map<String, Object> listMetricsSchema = Map.of(
                "type", "object",
                "properties", Map.of()
        );
        tools.add(new McpToolDefinition(
                "list_metrics",
                "List all canonical metrics defined in the Apache Ossie semantic model with descriptions and types.",
                listMetricsSchema
        ));

        Map<String, Object> listDimsSchema = Map.of(
                "type", "object",
                "properties", Map.of()
        );
        tools.add(new McpToolDefinition(
                "list_dimensions",
                "List all dimensions and supported time grains for slicing metrics.",
                listDimsSchema
        ));

        Map<String, Object> querySchema = Map.of(
                "type", "object",
                "required", List.of("metrics"),
                "properties", Map.of(
                        "metrics", Map.of("type", "array", "items", Map.of("type", "string")),
                        "dimensions", Map.of("type", "array", "items", Map.of("type", "string")),
                        "filters", Map.of("type", "array", "items", Map.of("type", "string")),
                        "limit", Map.of("type", "integer")
                )
        );
        tools.add(new McpToolDefinition(
                "execute_semantic_query",
                "Execute a Layer 3 wide-table semantic query to retrieve verified business metric values.",
                querySchema
        ));

        return tools;
    }

    /**
     * Simulates an AI Agent converting a natural language question into an Ossie Semantic Query.
     */
    public SemanticQuery simulateAgentQueryInterpretation(String naturalLanguageQuestion) {
        String lower = naturalLanguageQuestion.toLowerCase();
        SemanticQuery.Builder builder = SemanticQuery.builder();

        // Detect metrics
        boolean foundMetric = false;
        for (Metric metric : getAllMetrics()) {
            if (lower.contains(metric.getName().toLowerCase())) {
                builder.metric(metric.getName());
                foundMetric = true;
            } else if (metric.getAiContext() != null && metric.getAiContext().getSynonyms() != null) {
                for (String syn : metric.getAiContext().getSynonyms()) {
                    if (lower.contains(syn.toLowerCase())) {
                        builder.metric(metric.getName());
                        foundMetric = true;
                        break;
                    }
                }
            }
        }

        if (!foundMetric) {
            // Default to first metric
            if (!getAllMetrics().isEmpty()) {
                builder.metric(getAllMetrics().get(0).getName());
            }
        }

        // Detect dimensions
        if (lower.contains("month") || lower.contains("monthly")) {
            builder.dimension("orders.order_date[month]");
        } else if (lower.contains("year") || lower.contains("annual")) {
            builder.dimension("orders.order_date[year]");
        }

        if (lower.contains("customer") || lower.contains("country")) {
            builder.dimension("customers.country");
        } else if (lower.contains("category") || lower.contains("product")) {
            builder.dimension("products.category");
        }

        // Detect filters
        if (lower.contains("active")) {
            builder.filter("customers.status = 'ACTIVE'");
        }
        if (lower.contains("usa") || lower.contains("us")) {
            builder.filter("customers.country = 'USA'");
        }

        return builder.build();
    }

    private List<Metric> getAllMetrics() {
        List<Metric> all = new ArrayList<>();
        if (model.getMetrics() != null) {
            all.addAll(model.getMetrics());
        }
        if (model.getDatasets() != null) {
            for (Dataset ds : model.getDatasets()) {
                if (ds.getMetrics() != null) {
                    all.addAll(ds.getMetrics());
                }
            }
        }
        return all;
    }
}
