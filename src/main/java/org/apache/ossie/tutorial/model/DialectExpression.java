package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dialect-specific SQL expression in Apache Ossie.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DialectExpression {

    @JsonProperty("dialect")
    private SqlDialect dialect = SqlDialect.ANSI_SQL;

    @JsonProperty("expression")
    private String expression;

    public DialectExpression() {}

    public DialectExpression(SqlDialect dialect, String expression) {
        this.dialect = dialect;
        this.expression = expression;
    }

    public SqlDialect getDialect() {
        return dialect;
    }

    public void setDialect(SqlDialect dialect) {
        this.dialect = dialect;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "[" + dialect + "] " + expression;
    }
}
