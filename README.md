# Apache Ossie (Incubating) - Step-by-Step Hands-On Guide

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)]()
[![Gradle](https://img.shields.io/badge/Gradle-8.14-green.svg)]()
[![Spec](https://img.shields.io/badge/Apache%20Ossie-0.3.0-purple.svg)](https://ossie.apache.org/)

This project is a step-by-step, hands-on learning environment for **[Apache Ossie (Incubating)](https://ossie.apache.org/)** (formerly *Open Semantic Interchange / OSI*).

Instead of a single monolithic output, this tutorial provides **8 standalone executable classes**, each with its own `main` method. You can run them one by one, inspecting the code, inputs, outputs, and internal mechanics at each step.

---

## What is Apache Ossie?

**Apache Ossie** is a vendor-neutral, open standard hosted by the Apache Software Foundation for defining and exchanging semantic metadata across the modern data, AI, and Business Intelligence (BI) stack.

It provides:
1. **A Single Source of Truth**: Uniform business definitions for metrics (`total_revenue`, `arr`, `churn_rate`).
2. **Layered Architecture**:
   - **Layer 1: Expressions & Dialects**: Multi-dialect SQL mapping (ANSI SQL, Snowflake, BigQuery, DuckDB, Postgres).
   - **Layer 2: Relational Query Interface**: Directed Semantic Graph that resolves multi-hop join paths and prevents fan-traps and chasm-traps.
   - **Layer 3: Wide-Table Query Interface**: High-level semantic query interface where users request *intent* (metrics and dimensions) without writing SQL joins.
3. **AI Grounding & MCP**: Metadata (`ai_context`), business instructions, and synonyms that prevent LLM SQL hallucinations.
4. **Interoperability**: Bidirectional translation between Ossie, dbt MetricFlow, and Snowflake Semantic Models.

---

## The 8 Step-by-Step Hands-on Modules

You can run each step directly via Gradle (`./gradlew step<N>`) or by right-clicking and running the `main` method of each class in IntelliJ IDEA / VS Code:

```
src/main/java/org/apache/ossie/tutorial/steps/
├── Step1_CoreConceptsAndModelLoading.java       # ./gradlew step1
├── Step2_SemanticValidation.java               # ./gradlew step2
├── Step3_Layer1_ExpressionsAndDialects.java    # ./gradlew step3
├── Step4_Layer2_RelationalGraphAndJoins.java   # ./gradlew step4
├── Step5_Layer3_WideTableQueryCompilation.java # ./gradlew step5
├── Step6_ExecutingSemanticQueriesOnDatabase.java# ./gradlew step6
├── Step7_AiGroundingAndMcpTools.java           # ./gradlew step7
└── Step8_CrossPlatformInterchange.java         # ./gradlew step8
```

---

### Step 1: Core Concepts & Model Ingestion
**Command:**
```bash
./gradlew step1
```
* **Class:** [`Step1_CoreConceptsAndModelLoading.java`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/java/org/apache/ossie/tutorial/steps/Step1_CoreConceptsAndModelLoading.java)
* **What you learn:**
  - How an Apache Ossie model is structured (YAML / JSON).
  - Understanding datasets, primary keys, fields, time dimensions, and metrics.
  - Parsing [`ecommerce_model.yaml`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/resources/models/ecommerce_model.yaml) into Java objects and serializing round-trips.

---

### Step 2: Semantic Integrity Validation
**Command:**
```bash
./gradlew step2
```
* **Class:** [`Step2_SemanticValidation.java`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/java/org/apache/ossie/tutorial/steps/Step2_SemanticValidation.java)
* **What you learn:**
  - How Ossie validates semantic contracts before queries run.
  - Detecting orphaned relationships, invalid foreign keys, missing primary keys, and missing AI context.
  - Comparing a valid production model against an intentionally flawed model ([`invalid_model.yaml`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/resources/models/invalid_model.yaml)).

---

### Step 3: Layer 1 - Expressions & Multi-Dialect Portability
**Command:**
```bash
./gradlew step3
```
* **Class:** [`Step3_Layer1_ExpressionsAndDialects.java`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/java/org/apache/ossie/tutorial/steps/Step3_Layer1_ExpressionsAndDialects.java)
* **What you learn:**
  - How Apache Ossie abstracts SQL dialects (ANSI SQL, Snowflake, BigQuery, DuckDB, Postgres).
  - Handling dialect differences: date truncation (`DATE_TRUNC`), null-safe division (`DIV0`, `IEEE_DIVIDE`), string concatenation.
  - Resolving a single metric expression dynamically for different cloud warehouses.

---

### Step 4: Layer 2 - Relational Semantic Graph & Multi-Hop Joins
**Command:**
```bash
./gradlew step4
```
* **Class:** [`Step4_Layer2_RelationalGraphAndJoins.java`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/java/org/apache/ossie/tutorial/steps/Step4_Layer2_RelationalGraphAndJoins.java)
* **What you learn:**
  - Modeling datasets as vertices and relationships as directed edges in a Semantic Graph.
  - Understanding relationship cardinalities (`one_to_one`, `many_to_one`, `one_to_many`).
  - Breadth-First-Search (BFS) pathfinding to resolve multi-hop joins (e.g., `order_items` -> `orders` -> `customers`).
  - How Ossie avoids fan-traps and chasm-traps.

---

### Step 5: Layer 3 - Wide-Table Query Compilation
**Command:**
```bash
./gradlew step5
```
* **Class:** [`Step5_Layer3_WideTableQueryCompilation.java`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/java/org/apache/ossie/tutorial/steps/Step5_Layer3_WideTableQueryCompilation.java)
* **What you learn:**
  - How analysts, BI tools, and AI agents write queries expressing *intent* (metrics, dimensions, time grains, filters).
  - How the compiler determines the fact table, assembles the join tree, builds GROUP BY clauses, and formats time-grain expressions like `orders.order_date[month]`.
  - Comparing generated SQL across ANSI SQL, Snowflake, and BigQuery.

---

### Step 6: Executing Semantic Queries on a Live Relational Engine
**Command:**
```bash
./gradlew step6
```
* **Class:** [`Step6_ExecutingSemanticQueriesOnDatabase.java`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/java/org/apache/ossie/tutorial/steps/Step6_ExecutingSemanticQueriesOnDatabase.java)
* **What you learn:**
  - End-to-end execution on an embedded relational SQL engine (H2 in PostgreSQL mode).
  - Seeding relational tables ([`seed_ecommerce.sql`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/resources/data/seed_ecommerce.sql)) with real data.
  - Executing compiled queries and rendering formatted ASCII result tables for revenue, orders, and product volumes.

---

### Step 7: AI Grounding, Synonyms & MCP Tool Integration
**Command:**
```bash
./gradlew step7
```
* **Class:** [`Step7_AiGroundingAndMcpTools.java`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/java/org/apache/ossie/tutorial/steps/Step7_AiGroundingAndMcpTools.java)
* **What you learn:**
  - Why direct Text-to-SQL without a semantic layer causes LLM hallucinations.
  - Generating rich LLM grounding system prompts from `ai_context`.
  - Translating natural language business terms and synonyms (e.g. "GMV", "sales", "basket size") to canonical metrics (`total_revenue`, `average_order_value`).
  - Exposing Model Context Protocol (MCP) function calling tools to AI agents.

---

### Step 8: Cross-Platform Interoperability (dbt & Snowflake)
**Command:**
```bash
./gradlew step8
```
* **Class:** [`Step8_CrossPlatformInterchange.java`](file:///Users/rboorgapally/code/apache-ossie-tutorial/src/main/java/org/apache/ossie/tutorial/steps/Step8_CrossPlatformInterchange.java)
* **What you learn:**
  - The "Define Once, Deploy Anywhere" philosophy of Apache Ossie.
  - Exporting Ossie models into **dbt Semantic Layer (MetricFlow YAML)**.
  - Exporting Ossie models into **Snowflake Secure Semantic Views**.

---

## Running the Automated Test Suite

To verify all components across parsing, validation, pathfinding, compilation, and execution:

```bash
./gradlew test
```

*(All 17 tests verify every layer and pass cleanly).*

---

## Official Apache Ossie Links

- **Website**: [https://ossie.apache.org/](https://ossie.apache.org/)
- **GitHub**: [https://github.com/apache/ossie](https://github.com/apache/ossie)
- **Specification Documentation**: [Apache Ossie Spec](https://github.com/apache/ossie/blob/main/core-spec/spec.md)
