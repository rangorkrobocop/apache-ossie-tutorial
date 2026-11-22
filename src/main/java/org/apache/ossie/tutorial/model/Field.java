package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a column or attribute in an Apache Ossie Dataset.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("datatype")
    private String datatype = "String";

    @JsonProperty("expression")
    private Expression expression;

    @JsonProperty("is_primary_key")
    private boolean isPrimaryKey = false;

    @JsonProperty("dimension")
    private Dimension dimension;

    @JsonProperty("ai_context")
    private AiContext aiContext;

    public Field() {}

    public Field(String name, String datatype) {
        this.name = name;
        this.datatype = datatype;
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

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public AiContext getAiContext() {
        return aiContext;
    }

    public void setAiContext(AiContext aiContext) {
        this.aiContext = aiContext;
    }

    public boolean isDimension() {
        return dimension != null;
    }
}
