package org.apache.ossie.tutorial.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Layer 3 Wide-Table Semantic Query definition.
 * Consumers (BI tools, AI agents, analysts) request metrics and dimensions by intent,
 * completely decoupled from physical joins, table schemas, or underlying dialects.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SemanticQuery {

    @JsonProperty("metrics")
    private List<String> metrics = new ArrayList<>();

    @JsonProperty("dimensions")
    private List<String> dimensions = new ArrayList<>();

    @JsonProperty("filters")
    private List<String> filters = new ArrayList<>();

    @JsonProperty("order_by")
    private List<OrderByClause> orderBy = new ArrayList<>();

    @JsonProperty("limit")
    private Integer limit;

    public SemanticQuery() {}

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public List<String> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<String> dimensions) {
        this.dimensions = dimensions;
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    public List<OrderByClause> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<OrderByClause> orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public static class Builder {
        private final SemanticQuery query = new SemanticQuery();

        public Builder metric(String metricName) {
            query.metrics.add(metricName);
            return this;
        }

        public Builder dimension(String dimensionName) {
            query.dimensions.add(dimensionName);
            return this;
        }

        public Builder filter(String filterExpression) {
            query.filters.add(filterExpression);
            return this;
        }

        public Builder orderBy(String field, OrderByClause.Direction direction) {
            query.orderBy.add(new OrderByClause(field, direction));
            return this;
        }

        public Builder limit(int limit) {
            query.limit = limit;
            return this;
        }

        public SemanticQuery build() {
            return query;
        }
    }
}
