# Apache Ossie (Incubating) Java Guide & Tutorial

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)]()
[![Gradle](https://img.shields.io/badge/Gradle-8.14-green.svg)]()
[![Spec](https://img.shields.io/badge/Apache%20Ossie-0.3.0-purple.svg)](https://ossie.apache.org/)

An interactive, end-to-end Java reference implementation and tutorial designed to give you a complete understanding of **[Apache Ossie (Incubating)](https://ossie.apache.org/)** (formerly known as **Open Semantic Interchange / OSI**).

---

## What is Apache Ossie?

**Apache Ossie** is an open-source, vendor-neutral specification and framework hosted by the Apache Software Foundation. It defines a universal semantic intermediate representation for business metrics, dimensions, datasets, joins, and AI metadata.

### The Problem: Semantic Fragmentation

In the contemporary data and AI ecosystem, business logic is fragmented:

```
           +-----------------------+     +-----------------------+
           | BI & Analytics Tools  |     | AI Agents / LLMs      |
           | (Looker, PowerBI,     |     | (Text-to-SQL, Copilot,|
           |  Tableau, Lightdash)  |     |  MCP Servers, ChatBI) |
           +-----------+-----------+     +-----------+-----------+
                       |                             |
                       v                             v
           +-----------------------------------------------------+
           |            Semantic Definition Drift               |
           |   (Different definitions for "Revenue", "ARR",      |
           |    duplicated metrics, SQL hallucinations)          |
           +-----------------------------------------------------+
                       |                             |
                       v                             v
           +-----------------------+     +-----------------------+
           | Transformation Layers |     | Cloud Data Warehouses |
           | (dbt MetricFlow, Cube)|     | (Snowflake, BigQuery, |
           |                       |     |  Databricks, DuckDB)  |
           +-----------------------+     +-----------------------+
```

1. **Metric Drift**: "Revenue" or "Monthly Active Users" is defined differently in LookML, dbt, SQL scripts, and PowerBI.
2. **AI Hallucinations**: LLMs and Text-to-SQL agents make wild guesses at join keys, table names, or metric aggregations without canonical context.
3. **Vendor Lock-in**: Semantic logic expressed in proprietary BI models cannot be easily migrated or shared across platforms.

### The Solution: Apache Ossie

Apache Ossie provides **a single, declarative standard** (YAML / JSON) that acts as the universal semantic bridge across warehouses, BI engines, and AI agents:

```
                            +-----------------------+
                            |   Apache Ossie Spec   |
                            | (Universal YAML/JSON) |
                            +-----------+-----------+
                                        |
               +------------------------+------------------------+
               |                        |                        |
               v                        v                        v
      +-----------------+      +-----------------+      +-----------------+
      |  Query Engines  |      |  AI Grounding   |      |  BI Converters  |
      | ANSI SQL, DuckDB|      | Instructions,   |      | dbt MetricFlow, |
      | Snowflake, BQ   |      | Synonyms & MCP  |      | Snowflake Views |
      +-----------------+      +-----------------+      +-----------------+
```

---

## The 3 Specification Layers of Apache Ossie

Apache Ossie structures semantic querying into three distinct layers:

| Layer | Name | Description |
|---|---|---|
| **Layer 1** | **Common Expression Language & Dialects** | Portable SQL scalar and aggregate expressions (`SUM`, `AVG`, `DATE_TRUNC`) with dialect-specific variants (ANSI SQL, Snowflake, BigQuery, DuckDB, Postgres). |
| **Layer 2** | **Relational Query Interface** | Models datasets as relations connected by directed relationships. Uses graph algorithms to determine deterministic join paths and prevent fan-traps/chasm-traps. |
| **Layer 3** | **Wide-Table Query Interface** | The high-level semantic query abstraction. Analysts and AI agents request *intent* (e.g. `[total_revenue]` by `[customers.country, orders.order_date[month]]`), and Ossie compiles the optimal multi-table SQL query. |

---

## Project Structure & Architecture

```
apache-ossie-tutorial/
├── build.gradle                              # Gradle build configuration (Java 21, Jackson, H2, JUnit 5)
├── settings.gradle
├── gradle.properties
├── gradlew                                   # Self-contained Gradle wrapper
├── src/
│   ├── main/
│   │   ├── java/org/apache/ossie/tutorial/
│   │   │   ├── model/                        # Ossie AST / Data Models
│   │   │   │   ├── SemanticModel.java        # Root Ossie document
│   │   │   │   ├── Dataset.java              # Physical/virtual dataset
│   │   │   │   ├── Field.java                # Columns & attributes
│   │   │   │   ├── Dimension.java            # Slicing & time grains
│   │   │   │   ├── Metric.java               # Measures & calculations
│   │   │   │   ├── Relationship.java         # Join definitions & cardinality
│   │   │   │   ├── Cardinality.java          # 1:1, 1:N, N:1, N:M
│   │   │   │   ├── JoinType.java             # LEFT, INNER, FULL
│   │   │   │   ├── Expression.java           # Multi-dialect SQL expressions
│   │   │   │   ├── DialectExpression.java    # Dialect mapping
│   │   │   │   ├── SqlDialect.java           # ANSI, Snowflake, BigQuery, DuckDB
│   │   │   │   └── AiContext.java            # Instructions & synonyms for LLMs
│   │   │   ├── parser/
│   │   │   │   ├── OssieModelParser.java     # Jackson YAML & JSON deserializer
│   │   │   │   └── OssieModelSerializer.java # Pretty YAML / JSON exporter
│   │   │   ├── validator/
│   │   │   │   ├── OssieModelValidator.java  # Semantic rule checker
│   │   │   │   ├── ValidationReport.java     # Diagnostic output formatter
│   │   │   │   └── ValidationIssue.java      # Error/Warning with suggestion
│   │   │   ├── graph/
│   │   │   │   ├── SemanticGraph.java        # Layer 2 graph pathfinding
│   │   │   │   ├── JoinEdge.java             # Join condition & ON clause
│   │   │   │   └── JoinPath.java             # Multi-hop join route
│   │   │   ├── query/
│   │   │   │   ├── SemanticQuery.java        # Layer 3 Wide-Table query intent
│   │   │   │   ├── WideTableQueryCompiler.java# Multi-dialect SQL compiler
│   │   │   │   ├── CompiledQuery.java        # Generated SQL & metadata
│   │   │   │   └── OrderByClause.java        # Sorting criteria
│   │   │   ├── engine/
│   │   │   │   ├── InMemorySemanticEngine.java# Embedded H2 SQL query runner
│   │   │   │   └── QueryResult.java          # Tabular formatter
│   │   │   ├── ai/
│   │   │   │   ├── AiGroundingService.java   # Prompt builder & synonym matcher
│   │   │   │   └── McpToolDefinition.java    # Model Context Protocol schemas
│   │   │   ├── converter/
│   │   │   │   ├── DbtMetricFlowConverter.java # Export to dbt Semantic Layer
│   │   │   │   └── SnowflakeSemanticModelConverter.java # Export to Snowflake Views
│   │   │   └── app/
│   │   │       └── OssieTutorialApp.java     # Interactive master CLI walkthrough
│   │   └── resources/
│   │       ├── models/
│   │       │   ├── ecommerce_model.yaml      # Complete E-Commerce retail model
│   │       │   ├── saas_subscription_model.yaml # B2B SaaS recurring revenue model
│   │       │   └── invalid_model.yaml        # Faulty model demonstrating diagnostics
│   │       └── data/
│   │           └── seed_ecommerce.sql        # Realistic relational seed data
│   └── test/
│       └── java/org/apache/ossie/tutorial/   # 17 comprehensive unit & integration tests
```

---

## Quickstart: Running the Tutorial

### Prerequisites
- macOS, Linux, or Windows with Java 21+.

### 1. Run the Interactive Tour
Execute the master tutorial application:

```bash
./gradlew run
```

This will run an 8-step guided demonstration:
1. **Introduction**: Principles and specification overview.
2. **Model Ingestion**: Parsing `ecommerce_model.yaml` (datasets, fields, metrics, joins).
3. **Validation Engine**: Validating model integrity and diagnosing intentional errors in `invalid_model.yaml`.
4. **Layer 2 Graph Resolution**: Breadth-First-Search multi-hop join pathfinding (e.g. `order_items` -> `orders` -> `customers`).
5. **Layer 3 Wide-Table Query Compilation**: Translating high-level metric intents into ANSI SQL, Snowflake, and BigQuery SQL.
6. **In-Memory SQL Execution**: Running queries against embedded H2 database with realistic data and ASCII table results.
7. **AI & MCP Grounding**: Generating LLM system prompts, synonym matching ("GMV" -> `total_revenue`), and tool schemas.
8. **Ecosystem Converters**: Exporting the model to dbt MetricFlow YAML and Snowflake Secure Views.

### 2. Run the Test Suite

```bash
./gradlew test
```

All 17 tests verify parser roundtrips, graph traversal, dialect compilation, validation rules, AI tools, and in-memory SQL execution.

---

## Code Walkthrough

### 1. Defining an Apache Ossie Semantic Model (`ecommerce_model.yaml`)

```yaml
version: "0.3.0"
name: "ecommerce_sales_analytics"
description: "Universal semantic model for Retail and E-Commerce sales"

ai_context:
  instructions: |
    Use this semantic model for all retail analytics inquiries.
    Always use 'total_revenue' for sales volume and 'customer_count' for reach.

datasets:
  - name: "customers"
    source: "customers"
    primary_key: ["customer_id"]
    fields:
      - name: "customer_id"
        datatype: "Integer"
        is_primary_key: true
      - name: "country"
        datatype: "String"
        dimension:
          cardinality: "low"
          synonyms: ["nation", "location", "geography"]

  - name: "orders"
    source: "orders"
    primary_key: ["order_id"]
    fields:
      - name: "order_id"
        datatype: "Integer"
        is_primary_key: true
      - name: "order_date"
        datatype: "Date"
        dimension:
          is_time: true
          time_grains: ["day", "week", "month", "year"]
      - name: "total_amount"
        datatype: "Decimal"
    metrics:
      - name: "total_revenue"
        description: "Total gross revenue generated from orders"
        datatype: "Decimal"
        expression:
          dialects:
            - dialect: ANSI_SQL
              expression: "SUM(source.total_amount)"
        ai_context:
          synonyms: ["sales", "gross merchandise value", "gmv"]

relationships:
  - name: "orders_to_customers"
    from: "orders"
    to: "customers"
    from_columns: ["customer_id"]
    to_columns: ["customer_id"]
    cardinality: "many_to_one"
```

### 2. Executing a Layer 3 Wide-Table Semantic Query

Instead of writing manual joins, users specify *business intent*:

```java
SemanticQuery query = SemanticQuery.builder()
    .metric("total_revenue")
    .metric("order_count")
    .dimension("customers.country")
    .dimension("orders.order_date[month]")
    .filter("customers.status = 'ACTIVE'")
    .orderBy("total_revenue", OrderByClause.Direction.DESC)
    .limit(5)
    .build();

// The compiler resolves joins and dialect SQL automatically:
CompiledQuery compiled = compiler.compile(query, SqlDialect.ANSI_SQL);
System.out.println(compiled.getSql());

// Execute directly against the engine:
QueryResult result = engine.execute(query);
System.out.println(result.toPrettyTable());
```

Output:
```
+-------------------+-------------------------+---------------+-------------+
| customers_country | orders_order_date_month | total_revenue | order_count |
+-------------------+-------------------------+---------------+-------------+
| USA               | 2026-01-01T00:00        | 570.00        | 2           |
| USA               | 2026-03-01T00:00        | 500.00        | 1           |
| Canada            | 2026-02-01T00:00        | 450.00        | 1           |
| USA               | 2026-02-01T00:00        | 120.00        | 1           |
| UK                | 2026-02-01T00:00        | 95.00         | 1           |
+-------------------+-------------------------+---------------+-------------+
5 rows returned in 8 ms
```

### 3. AI Grounding & MCP Function Calling

Apache Ossie eliminates AI hallucinations by converting `ai_context` into structured system prompts and function definitions:

```java
AiGroundingService ai = new AiGroundingService(model);

// 1. Generate grounded LLM system prompt
String systemPrompt = ai.generateSystemPrompt();

// 2. Resolve natural language inputs using synonyms
Optional<String> canonicalMetric = ai.matchMetricFromSynonym("show me our GMV");
// Returns: "total_revenue"

// 3. Expose MCP tools for AI Agents
List<McpToolDefinition> tools = ai.getMcpToolDefinitions();
// Exposes: list_metrics, list_dimensions, execute_semantic_query
```

---

## Official Apache Ossie Links

- **Official Website**: [https://ossie.apache.org/](https://ossie.apache.org/)
- **GitHub Repository**: [https://github.com/apache/ossie](https://github.com/apache/ossie)
- **Specification Overview**: [Core Specification Documents](https://github.com/apache/ossie/blob/main/core-spec/spec.md)
