package org.apache.ossie.tutorial.steps;

import org.apache.ossie.tutorial.model.DialectExpression;
import org.apache.ossie.tutorial.model.Expression;
import org.apache.ossie.tutorial.model.SqlDialect;

/**
 * STEP 3: Layer 1 - Common Expression Language & Dialects
 *
 * Run with:
 *   ./gradlew step3
 */
public class Step3_Layer1_ExpressionsAndDialects {

    public static void main(String[] args) {
        System.out.println("""
            ========================================================================
             STEP 3: Layer 1 - Expressions & Multi-Dialect Portability
            ========================================================================
            Apache Ossie Layer 1 addresses dialect fragmentation in modern cloud warehouses:
            
            Every database engine has dialect nuances:
              • Date Truncation:
                  - ANSI SQL / DuckDB:  DATE_TRUNC('month', order_date)
                  - Snowflake:          DATE_TRUNC('MONTH', order_date)
                  - BigQuery:           DATE_TRUNC(order_date, MONTH)
              • String Concatenation:
                  - ANSI SQL:           first_name || ' ' || last_name
                  - BigQuery:           CONCAT(first_name, ' ', last_name)
              • Null-Safe Division:
                  - Snowflake:          DIV0(numerator, denominator)
                  - BigQuery:           IEEE_DIVIDE(numerator, denominator)
                  - ANSI SQL:           CASE WHEN d = 0 THEN NULL ELSE n / d END

            Apache Ossie enables authors to declare either portable ANSI expressions
            or dialect-specialized expressions within the same semantic definition!
            """);

        // 1. Defining a multi-dialect metric expression
        System.out.println("Defining a multi-dialect metric: 'safe_average_revenue'...");
        Expression expr = new Expression();
        expr.setSql("SUM(total_amount) / NULLIF(COUNT(order_id), 0)"); // Default ANSI SQL

        expr.getDialects().add(new DialectExpression(SqlDialect.ANSI_SQL,
                "SUM(total_amount) / NULLIF(COUNT(order_id), 0)"));
        expr.getDialects().add(new DialectExpression(SqlDialect.SNOWFLAKE,
                "DIV0(SUM(total_amount), COUNT(order_id))"));
        expr.getDialects().add(new DialectExpression(SqlDialect.BIGQUERY,
                "IEEE_DIVIDE(SUM(total_amount), COUNT(order_id))"));
        expr.getDialects().add(new DialectExpression(SqlDialect.DUCKDB,
                "SUM(total_amount) / NULLIF(COUNT(order_id), 0)"));

        System.out.println("\nResolving expression across target data platforms:");
        for (SqlDialect dialect : SqlDialect.values()) {
            String resolved = expr.resolveForDialect(dialect);
            System.out.printf("  • %-12s -> %s%n", dialect, resolved);
        }

        // 2. Time Grain Truncation Demonstration
        System.out.println("\nTime Grain Resolution for dimension 'orders.order_date[month]':");
        for (SqlDialect dialect : SqlDialect.values()) {
            String timeTrunc = switch (dialect) {
                case DUCKDB, POSTGRESQL -> "DATE_TRUNC('month', orders.order_date)";
                case BIGQUERY -> "DATE_TRUNC(orders.order_date, MONTH)";
                case SNOWFLAKE -> "DATE_TRUNC('MONTH', orders.order_date)";
                default -> "DATE_TRUNC('month', orders.order_date)";
            };
            System.out.printf("  • %-12s -> %s%n", dialect, timeTrunc);
        }

        System.out.println("""

            ========================================================================
             STEP 3 SUMMARY:
             • With Ossie Layer 1, business metrics are authored once, centrally,
               and resolved cleanly for any target warehouse dialect.
             • Next: Run `./gradlew step4` to see Layer 2 Relational Graph Joins!
            ========================================================================
            """);
    }
}
