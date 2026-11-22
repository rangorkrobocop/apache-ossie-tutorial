package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Metric definition in Apache Ossie.
 * Metrics represent aggregations (e.g. SUM, COUNT, AVG) or derived calculations
 * (e.g. revenue / orders) with business context and optional filters.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metric {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("datatype")
    private String datatype = "Decimal";

    @JsonProperty("expression")
    private Expression expression;

    @JsonProperty("filter")
    private String filter;

    @JsonProperty("ai_context")
    private AiContext aiContext;

    public Metric() {}

    public Metric(String name, String description, Expression expression) {
        this.name = name;
        this.description = description;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public AiContext getAiContext() {
        return aiContext;
    }

    public void setAiContext(AiContext aiContext) {
        this.aiContext = aiContext;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "name='" + name + '\'' +
                ", expression=" + expression +
                '}';
    }
}
