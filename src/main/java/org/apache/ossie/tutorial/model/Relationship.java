package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a relationship (join edge) between two datasets in an Apache Ossie model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Relationship {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String to;

    @JsonProperty("from_columns")
    private List<String> fromColumns = new ArrayList<>();

    @JsonProperty("to_columns")
    private List<String> toColumns = new ArrayList<>();

    @JsonProperty("cardinality")
    private Cardinality cardinality = Cardinality.MANY_TO_ONE;

    @JsonProperty("join_type")
    private JoinType joinType = JoinType.LEFT_OUTER;

    @JsonProperty("ai_context")
    private AiContext aiContext;

    public Relationship() {}

    public Relationship(String name, String from, String to, List<String> fromColumns, List<String> toColumns, Cardinality cardinality) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.fromColumns = fromColumns;
        this.toColumns = toColumns;
        this.cardinality = cardinality;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<String> getFromColumns() {
        return fromColumns;
    }

    public void setFromColumns(List<String> fromColumns) {
        this.fromColumns = fromColumns;
    }

    public List<String> getToColumns() {
        return toColumns;
    }

    public void setToColumns(List<String> toColumns) {
        this.toColumns = toColumns;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public AiContext getAiContext() {
        return aiContext;
    }

    public void setAiContext(AiContext aiContext) {
        this.aiContext = aiContext;
    }

    @Override
    public String toString() {
        return name + " (" + from + " -> " + to + " via " + fromColumns + " = " + toColumns + ", " + cardinality + ")";
    }
}
