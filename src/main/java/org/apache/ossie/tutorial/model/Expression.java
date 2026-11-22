package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Expression container supporting both simple SQL strings and dialect-specific expressions.
 * In Apache Ossie, an expression can specify multiple SQL dialects (ANSI_SQL, Snowflake, BigQuery, etc.)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Expression {

    @JsonProperty("sql")
    private String sql;

    @JsonProperty("dialects")
    private List<DialectExpression> dialects = new ArrayList<>();

    public Expression() {}

    public Expression(String sql) {
        this.sql = sql;
    }

    public static Expression ofSql(String sql) {
        return new Expression(sql);
    }

    public static Expression ofDialect(SqlDialect dialect, String expr) {
        Expression expression = new Expression();
        expression.getDialects().add(new DialectExpression(dialect, expr));
        return expression;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<DialectExpression> getDialects() {
        return dialects;
    }

    public void setDialects(List<DialectExpression> dialects) {
        this.dialects = dialects;
    }

    /**
     * Resolve expression for a target dialect, falling back to ANSI_SQL or default sql.
     */
    public String resolveForDialect(SqlDialect targetDialect) {
        if (dialects != null) {
            for (DialectExpression d : dialects) {
                if (d.getDialect() == targetDialect && d.getExpression() != null) {
                    return d.getExpression();
                }
            }
            for (DialectExpression d : dialects) {
                if (d.getDialect() == SqlDialect.ANSI_SQL && d.getExpression() != null) {
                    return d.getExpression();
                }
            }
            if (!dialects.isEmpty() && dialects.get(0).getExpression() != null) {
                return dialects.get(0).getExpression();
            }
        }
        return sql;
    }

    @Override
    public String toString() {
        if (sql != null) return sql;
        if (dialects != null && !dialects.isEmpty()) return dialects.toString();
        return "";
    }
}
