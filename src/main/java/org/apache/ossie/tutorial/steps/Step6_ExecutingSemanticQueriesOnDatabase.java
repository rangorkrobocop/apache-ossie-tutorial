package org.apache.ossie.tutorial.steps;

import org.apache.ossie.tutorial.engine.InMemorySemanticEngine;
import org.apache.ossie.tutorial.engine.QueryResult;
import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.apache.ossie.tutorial.query.OrderByClause;
import org.apache.ossie.tutorial.query.SemanticQuery;

/**
 * STEP 6: Executing Semantic Queries on a Live Relational Engine
 *
 * Run with:
 *   ./gradlew step6
 */
public class Step6_ExecutingSemanticQueriesOnDatabase {

    public static void main(String[] args) throws Exception {
        System.out.println("""
            ========================================================================
             STEP 6: Executing Semantic Queries on a Live Relational Engine
            ========================================================================
            In this step, we prove that our Apache Ossie semantic layer actually works!
            We spin up an embedded in-memory SQL database (H2 in PostgreSQL mode),
            load real e-commerce transactional tables, and run Layer 3 semantic queries.
            """);

        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");

        try (InMemorySemanticEngine engine = new InMemorySemanticEngine(model)) {
            System.out.println("1. Seeding database with tables: customers, orders, order_items, products...");
            engine.seedFromResource("/data/seed_ecommerce.sql");
            System.out.println("   Database initialized and seeded successfully!\n");

            // Query 1: Sales & Orders by Country & Month
            System.out.println("------------------------------------------------------------------------");
            System.out.println("Query 1: Revenue, Order Count, and AOV by Country and Month");
            System.out.println("------------------------------------------------------------------------");
            SemanticQuery q1 = SemanticQuery.builder()
                    .metric("total_revenue")
                    .metric("order_count")
                    .metric("average_order_value")
                    .dimension("customers.country")
                    .dimension("orders.order_date[month]")
                    .filter("customers.status = 'ACTIVE'")
                    .orderBy("total_revenue", OrderByClause.Direction.DESC)
                    .build();

            System.out.println("Compiled SQL:");
            System.out.println(engine.compile(q1).getSql());
            System.out.println("\nQuery Results:");
            QueryResult r1 = engine.execute(q1);
            System.out.println(r1.toPrettyTable());

            // Query 2: Product Volume Across Multi-Hop Joins
            System.out.println("------------------------------------------------------------------------");
            System.out.println("Query 2: Total Units Sold by Product Category");
            System.out.println("(Requires traversing order_items -> products)");
            System.out.println("------------------------------------------------------------------------");
            SemanticQuery q2 = SemanticQuery.builder()
                    .metric("total_units_sold")
                    .dimension("products.category")
                    .orderBy("total_units_sold", OrderByClause.Direction.DESC)
                    .build();

            System.out.println("Compiled SQL:");
            System.out.println(engine.compile(q2).getSql());
            System.out.println("\nQuery Results:");
            QueryResult r2 = engine.execute(q2);
            System.out.println(r2.toPrettyTable());

            // Query 3: Cross-Table Customer Reach
            System.out.println("------------------------------------------------------------------------");
            System.out.println("Query 3: Registered Customer Count by Country");
            System.out.println("------------------------------------------------------------------------");
            SemanticQuery q3 = SemanticQuery.builder()
                    .metric("customer_count")
                    .dimension("customers.country")
                    .orderBy("customer_count", OrderByClause.Direction.DESC)
                    .build();

            QueryResult r3 = engine.execute(q3);
            System.out.println(r3.toPrettyTable());
        }

        System.out.println("""
            ========================================================================
             STEP 6 SUMMARY:
             • Notice how the semantic engine converted high-level metric intents into
               accurate, executable SQL queries with multi-table joins.
             • Results match exact ground-truth database rows!
             • Next: Run `./gradlew step7` to see how Ossie grounds AI & LLMs!
            ========================================================================
            """);
    }
}
