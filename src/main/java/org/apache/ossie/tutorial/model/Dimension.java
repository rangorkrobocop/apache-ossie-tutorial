package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Dimension metadata on a field.
 * Marks fields that can be used for slicing, grouping, or filtering.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dimension {

    @JsonProperty("is_time")
    private boolean isTime = false;

    @JsonProperty("time_grains")
    private List<String> timeGrains = new ArrayList<>();

    @JsonProperty("cardinality")
    private String cardinality; // e.g. "high", "low"

    @JsonProperty("synonyms")
    private List<String> synonyms = new ArrayList<>();

    @JsonProperty("ai_context")
    private AiContext aiContext;

    public Dimension() {}

    public Dimension(boolean isTime, List<String> timeGrains) {
        this.isTime = isTime;
        this.timeGrains = timeGrains;
    }

    public boolean isTime() {
        return isTime;
    }

    public void setTime(boolean time) {
        isTime = time;
    }

    public List<String> getTimeGrains() {
        return timeGrains;
    }

    public void setTimeGrains(List<String> timeGrains) {
        this.timeGrains = timeGrains;
    }

    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public AiContext getAiContext() {
        return aiContext;
    }

    public void setAiContext(AiContext aiContext) {
        this.aiContext = aiContext;
    }
}
