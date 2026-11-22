package org.apache.ossie.tutorial.app;

import org.apache.ossie.tutorial.ai.AiGroundingService;
import org.apache.ossie.tutorial.ai.McpToolDefinition;
import org.apache.ossie.tutorial.converter.DbtMetricFlowConverter;
import org.apache.ossie.tutorial.converter.SnowflakeSemanticModelConverter;
import org.apache.ossie.tutorial.engine.InMemorySemanticEngine;
import org.apache.ossie.tutorial.engine.QueryResult;
import org.apache.ossie.tutorial.graph.JoinEdge;
import org.apache.ossie.tutorial.graph.JoinPath;
import org.apache.ossie.tutorial.graph.SemanticGraph;
import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.model.SqlDialect;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.apache.ossie.tutorial.parser.OssieModelSerializer;
import org.apache.ossie.tutorial.query.CompiledQuery;
import org.apache.ossie.tutorial.query.OrderByClause;
import org.apache.ossie.tutorial.query.SemanticQuery;
import org.apache.ossie.tutorial.query.WideTableQueryCompiler;
import org.apache.ossie.tutorial.validator.OssieModelValidator;
import org.apache.ossie.tutorial.validator.ValidationReport;

import java.util.List;
import java.util.Optional;

/**
 * Interactive master tutorial application for Apache Ossie (Incubating).
 * Demonstrates:
 * 1. Semantic Model parsing & serialization (YAML / JSON)
 * 2. Model validation according to official Apache Ossie rules
 * 3. Layer 2: Relational Semantic Graph & automatic join path resolution
 * 4. Layer 3: Wide-Table Query Compilation to multiple SQL dialects
 * 5. In-Memory Execution of semantic queries against relational database
 * 6. AI Grounding & MCP tooling (preventing LLM metric hallucinations)
 * 7. Cross-ecosystem interchange with dbt and Snowflake
 */
public class OssieTutorialApp {

    public static void main(String[] args) {
        printBanner();

        try {
            OssieModelParser parser = new OssieModelParser();
            OssieModelSerializer serializer = new OssieModelSerializer();
            OssieModelValidator validator = new OssieModelValidator();

            // ---------------------------------------------------------
            // STEP 1: Introduction to Apache Ossie
            // ---------------------------------------------------------
            printHeader("STEP 1: What is Apache Ossie (Incubating)?");
            System.out.println("""
                Apache Ossie (formerly Open Semantic Interchange / OSI) is a vendor-neutral
                open standard for defining and exchanging semantic metadata across the modern
                data, AI, and Business Intelligence (BI) stack.

                Key Value Propositions:
                  • Single Source of Truth: Uniform definitions for metrics (ARR, Revenue, Churn).
                  • Multi-Dialect Portability: Compiles to ANSI SQL, Snowflake, BigQuery, DuckDB, Postgres.
                  • Eliminates AI Hallucinations: ai_context blocks ground LLMs with instructions & synonyms.
                  • Interoperability: Seamless export to dbt MetricFlow, Looker LookML, and Snowflake.
                """);

            // ---------------------------------------------------------
            // STEP 2: Loading & Parsing Semantic Models
            // ---------------------------------------------------------
            printHeader("STEP 2: Loading & Parsing Apache Ossie YAML Models");
            SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");
            System.out.printf("Loaded Model: %s (v%s)%n", model.getName(), model.getVersion());
            System.out.println("Description: " + model.getDescription());
            System.out.printf("Datasets: %d | Relationships: %d | Metrics: %d%n%n",
                    model.getDatasets().size(),
                    model.getRelationships().size(),
                    model.getDatasets().stream().mapToInt(d -> d.getMetrics() != null ? d.getMetrics().size() : 0).sum()
            );

            System.out.println("Previewing Datasets & Primary Keys:");
            for (var ds : model.getDatasets()) {
                System.out.printf("  • %-12s (Source: %-12s, PK: %s, Fields: %d, Metrics: %d)%n",
                        ds.getName(), ds.getSource(), ds.getPrimaryKey(), ds.getFields().size(),
                        ds.getMetrics() != null ? ds.getMetrics().size() : 0);
            }

            // ---------------------------------------------------------
            // STEP 3: Model Validation Engine
            // ---------------------------------------------------------
            printHeader("STEP 3: Semantic Validation Engine");
            ValidationReport validReport = validator.validate(model);
            System.out.println("Validation for 'ecommerce_sales_analytics':");
            System.out.println(validReport.formatReport());

            System.out.println("Now testing validator diagnostics against an intentionally flawed model...");
            SemanticModel flawedModel = parser.parseFromResource("/models/invalid_model.yaml");
            ValidationReport flawedReport = validator.validate(flawedModel);
            System.out.println(flawedReport.formatReport());

            // ---------------------------------------------------------
            // STEP 4: Layer 2 Relational Semantic Graph & Join Resolution
            // ---------------------------------------------------------
            printHeader("STEP 4: Layer 2 Relational Graph & Multi-Hop Join Resolution");
            SemanticGraph graph = new SemanticGraph(model);

            System.out.println("Discovered Join Relationships in Graph:");
            for (var rel : model.getRelationships()) {
                System.out.printf("  • %s: %s -> %s [%s] ON %s = %s%n",
                        rel.getName(), rel.getFrom(), rel.getTo(), rel.getCardinality(),
                        rel.getFromColumns(), rel.getToColumns());
            }

            System.out.println("\nResolving Join Paths using Graph Traversal:");
            Optional<JoinPath> path1 = graph.findShortestPath("order_items", "customers");
            System.out.println("  • Path from 'order_items' to 'customers':");
            System.out.println("    " + path1.map(JoinPath::toString).orElse("NO PATH FOUND"));

            Optional<JoinPath> path2 = graph.findShortestPath("order_items", "products");
            System.out.println("  • Path from 'order_items' to 'products':");
            System.out.println("    " + path2.map(JoinPath::toString).orElse("NO PATH FOUND"));

            // ---------------------------------------------------------
            // STEP 5: Layer 3 Wide-Table Query Compilation
            // ---------------------------------------------------------
            printHeader("STEP 5: Layer 3 Wide-Table Query Compilation across Dialects");
            WideTableQueryCompiler compiler = new WideTableQueryCompiler(model);

            SemanticQuery semanticQuery = SemanticQuery.builder()
                    .metric("total_revenue")
                    .metric("order_count")
                    .metric("average_order_value")
                    .dimension("customers.country")
                    .dimension("orders.order_date[month]")
                    .filter("customers.status = 'ACTIVE'")
                    .orderBy("total_revenue", OrderByClause.Direction.DESC)
                    .limit(5)
                    .build();

            System.out.println("Input Semantic Query (High-Level Intent):");
            System.out.println("  Metrics:    " + semanticQuery.getMetrics());
            System.out.println("  Dimensions: " + semanticQuery.getDimensions());
            System.out.println("  Filters:    " + semanticQuery.getFilters());
            System.out.println("  Order By:   " + semanticQuery.getOrderBy());
            System.out.println("  Limit:      " + semanticQuery.getLimit());

            System.out.println("\n--> Compiling to ANSI_SQL (with DATE_TRUNC):");
            CompiledQuery ansiQuery = compiler.compile(semanticQuery, SqlDialect.ANSI_SQL);
            System.out.println(ansiQuery.getSql());

            System.out.println("\n--> Compiling same semantic query for SNOWFLAKE:");
            CompiledQuery snowflakeQuery = compiler.compile(semanticQuery, SqlDialect.SNOWFLAKE);
            System.out.println(snowflakeQuery.getSql());

            // ---------------------------------------------------------
            // STEP 6: In-Memory Database Execution
            // ---------------------------------------------------------
            printHeader("STEP 6: Executing Compiled Semantic Query against Real Database");
            System.out.println("Initializing in-memory database with realistic e-commerce seed data...");
            try (InMemorySemanticEngine engine = new InMemorySemanticEngine(model)) {
                engine.seedFromResource("/data/seed_ecommerce.sql");

                System.out.println("Executing query: Revenue & Order Count by Customer Country and Month...");
                QueryResult result = engine.execute(semanticQuery);
                System.out.println(result.toPrettyTable());

                System.out.println("Executing second semantic query: Total Units Sold by Product Category...");
                SemanticQuery productQuery = SemanticQuery.builder()
                        .metric("total_units_sold")
                        .dimension("products.category")
                        .orderBy("total_units_sold", OrderByClause.Direction.DESC)
                        .build();

                CompiledQuery compiledProductQuery = compiler.compile(productQuery, SqlDialect.ANSI_SQL);
                System.out.println("Generated SQL:");
                System.out.println(compiledProductQuery.getSql());
                System.out.println("\nResults:");
                QueryResult prodResult = engine.execute(productQuery);
                System.out.println(prodResult.toPrettyTable());
            }

            // ---------------------------------------------------------
            // STEP 7: AI Grounding & Preventing LLM Hallucinations
            // ---------------------------------------------------------
            printHeader("STEP 7: AI Grounding & MCP (Model Context Protocol) Integration");
            AiGroundingService aiService = new AiGroundingService(model);

            System.out.println("Generated LLM Grounding System Prompt (Excerpt):");
            String prompt = aiService.generateSystemPrompt();
            System.out.println(prompt.substring(0, Math.min(prompt.length(), 650)) + "...\n");

            System.out.println("Simulating Natural Language Question from User:");
            String userQuestion = "What was our monthly sales and GMV for active customers in the US?";
            System.out.println("  User Input: \"" + userQuestion + "\"");

            SemanticQuery agentQuery = aiService.simulateAgentQueryInterpretation(userQuestion);
            System.out.println("  Agent mapped input using synonyms:");
            System.out.println("    Resolved Metric:     " + agentQuery.getMetrics());
            System.out.println("    Resolved Dimensions: " + agentQuery.getDimensions());
            System.out.println("    Resolved Filters:    " + agentQuery.getFilters());

            System.out.println("\nExposed MCP Tool Definitions for AI Agents:");
            List<McpToolDefinition> tools = aiService.getMcpToolDefinitions();
            for (McpToolDefinition tool : tools) {
                System.out.println("  • " + tool.getName() + " -> " + tool.getDescription());
            }

            // ---------------------------------------------------------
            // STEP 8: Multi-Platform Interchange (dbt & Snowflake)
            // ---------------------------------------------------------
            printHeader("STEP 8: Multi-Platform Semantic Interchange");
            System.out.println("Converting Apache Ossie Model to dbt MetricFlow YAML:");
            DbtMetricFlowConverter dbtConverter = new DbtMetricFlowConverter();
            String dbtYaml = dbtConverter.convertToDbtSemanticLayerYaml(model);
            System.out.println(dbtYaml.substring(0, Math.min(dbtYaml.length(), 500)) + "...\n");

            System.out.println("Converting Apache Ossie Model to Snowflake Semantic View DDL:");
            SnowflakeSemanticModelConverter sfConverter = new SnowflakeSemanticModelConverter();
            String sfSql = sfConverter.convertToSnowflakeSemanticModel(model);
            System.out.println(sfSql.substring(0, Math.min(sfSql.length(), 400)) + "...\n");

            // ---------------------------------------------------------
            // SUMMARY
            // ---------------------------------------------------------
            printHeader("TUTORIAL COMPLETE");
            System.out.println("""
                Congratulations! You have completed the Apache Ossie (Incubating) tour.
                You have seen:
                  1. The Declarative Semantic Specification (YAML/JSON)
                  2. Semantic Integrity Validation
                  3. Layer 2 Relational Semantic Graph & Join Path Routing
                  4. Layer 3 Wide-Table Query Abstraction
                  5. Dialect Compilation & In-Memory Execution
                  6. AI Grounding & LLM Tool Interfaces
                  7. Cross-vendor Semantic Interchange
                """);

        } catch (Exception e) {
            System.err.println("Error during tutorial execution:");
            e.printStackTrace();
        }
    }

    private static void printBanner() {
        System.out.println("""
            ========================================================================
             _  _  ____   ____  ____  ____    __    ___  _  _  ____   __  ____  ___
            ( \\/ )(  _ \\ (  __)(  __)/ ___)  / _\\  / __)/ )( \\(  __) /  \\(  __)/ __)
             )  (  ) __/  ) _)  ) _) \\___ \\ /    \\( (   ) __ ( ) _) (  O )) _)( (__
            (_/\\_)(__)   (____)(____)(____/ \\_/\\_/ \\___)\\_)(_/(____) \\__/(__)  \\___)
                     APACHE OSSIE (INCUBATING) - COMPREHENSIVE JAVA GUIDE
            ========================================================================
            """);
    }

    private static void printHeader(String title) {
        System.out.println("\n------------------------------------------------------------------------");
        System.out.println(title);
        System.out.println("------------------------------------------------------------------------");
    }
}
