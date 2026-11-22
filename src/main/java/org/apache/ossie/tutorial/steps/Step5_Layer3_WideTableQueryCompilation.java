package org.apache.ossie.tutorial.steps;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.model.SqlDialect;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.apache.ossie.tutorial.query.CompiledQuery;
import org.apache.ossie.tutorial.query.OrderByClause;
import org.apache.ossie.tutorial.query.SemanticQuery;
import org.apache.ossie.tutorial.query.WideTableQueryCompiler;

/**
 * STEP 5: Layer 3 - Wide-Table Query Compilation
 *
 * Run with:
 *   ./gradlew step5
 */
public class Step5_Layer3_WideTableQueryCompilation {

    public static void main(String[] args) throws Exception {
        System.out.println("""
            ========================================================================
             STEP 5: Layer 3 - Wide-Table Query Compilation
            ========================================================================
            WHAT IS THE WIDE-TABLE QUERY INTERFACE?
            In Apache Ossie Layer 3, consumers (BI tools, AI agents, analysts) never
            write SQL joins or table names directly. Instead, they interact with the entire
            model as if it were a single, logical "Wide Table".

            The query expresses INTENT:
              - Which metrics do you want? (e.g., total_revenue, order_count)
              - By which dimensions? (e.g., customers.country, orders.order_date[month])
              - Filtered by what? (e.g., customers.status = 'ACTIVE')

            The Ossie Compiler automatically:
              1. Inspects required metrics and dimensions to find all involved datasets.
              2. Uses the Layer 2 Semantic Graph to build the join tree.
              3. Generates the GROUP BY, SELECT aliases, and time-grain functions.
              4. Emits clean, target-dialect SQL.
            """);

        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");
        WideTableQueryCompiler compiler = new WideTableQueryCompiler(model);

        // Define a semantic query
        SemanticQuery query = SemanticQuery.builder()
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
        System.out.println("  Metrics:    " + query.getMetrics());
        System.out.println("  Dimensions: " + query.getDimensions());
        System.out.println("  Filters:    " + query.getFilters());
        System.out.println("  Order By:   " + query.getOrderBy());
        System.out.println("  Limit:      " + query.getLimit());

        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("Generated SQL for ANSI_SQL / DuckDB / PostgreSQL:");
        System.out.println("------------------------------------------------------------------------");
        CompiledQuery ansi = compiler.compile(query, SqlDialect.ANSI_SQL);
        System.out.println(ansi.getSql());

        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("Generated SQL for SNOWFLAKE (uppercase time grains):");
        System.out.println("------------------------------------------------------------------------");
        CompiledQuery snowflake = compiler.compile(query, SqlDialect.SNOWFLAKE);
        System.out.println(snowflake.getSql());

        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("Generated SQL for GOOGLE BIGQUERY (DATE_TRUNC(col, MONTH)):");
        System.out.println("------------------------------------------------------------------------");
        CompiledQuery bigquery = compiler.compile(query, SqlDialect.BIGQUERY);
        System.out.println(bigquery.getSql());

        System.out.println("""
            ========================================================================
             STEP 5 SUMMARY:
             • With Layer 3, query authors focus solely on business concepts.
             • Physical tables, joins, and database dialects are compiled under the hood.
             • Next: Run `./gradlew step6` to execute these queries against a live database!
            ========================================================================
            """);
    }
}
