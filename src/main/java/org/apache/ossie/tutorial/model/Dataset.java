package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Dataset in Apache Ossie.
 * Represents a physical or logical table or view in a data warehouse or lakehouse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataset {

    @JsonProperty("name")
    private String name;

    @JsonProperty("source")
    private String source;

    @JsonProperty("description")
    private String description;

    @JsonProperty("primary_key")
    private List<String> primaryKey = new ArrayList<>();

    @JsonProperty("fields")
    private List<Field> fields = new ArrayList<>();

    @JsonProperty("metrics")
    private List<Metric> metrics = new ArrayList<>();

    @JsonProperty("ai_context")
    private AiContext aiContext;

    public Dataset() {}

    public Dataset(String name, String source) {
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source != null ? source : name;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(List<String> primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public AiContext getAiContext() {
        return aiContext;
    }

    public void setAiContext(AiContext aiContext) {
        this.aiContext = aiContext;
    }

    public Optional<Field> findField(String fieldName) {
        if (fields == null) return Optional.empty();
        return fields.stream().filter(f -> f.getName().equalsIgnoreCase(fieldName)).findFirst();
    }

    public Optional<Metric> findMetric(String metricName) {
        if (metrics == null) return Optional.empty();
        return metrics.stream().filter(m -> m.getName().equalsIgnoreCase(metricName)).findFirst();
    }
}
