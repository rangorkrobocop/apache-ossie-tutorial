package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * AI Context metadata embedded in Apache Ossie models.
 * Used by LLM Agents, Text-to-SQL systems, and MCP servers to ground AI prompts
 * and prevent hallucinations on business definitions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiContext {

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("synonyms")
    private List<String> synonyms = new ArrayList<>();

    @JsonProperty("description")
    private String description;

    @JsonProperty("examples")
    private List<String> examples = new ArrayList<>();

    public AiContext() {}

    public AiContext(String instructions) {
        this.instructions = instructions;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }

    @Override
    public String toString() {
        return "AiContext{" +
                "instructions='" + instructions + '\'' +
                ", synonyms=" + synonyms +
                '}';
    }
}
