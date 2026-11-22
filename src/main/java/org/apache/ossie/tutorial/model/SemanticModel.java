package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Root container for an Apache Ossie Semantic Model.
 * Aligns with the core specification at https://ossie.apache.org/
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SemanticModel {

    @JsonProperty("version")
    private String version = "0.3.0";

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("ai_context")
    private AiContext aiContext;

    @JsonProperty("datasets")
    private List<Dataset> datasets = new ArrayList<>();

    @JsonProperty("relationships")
    private List<Relationship> relationships = new ArrayList<>();

    @JsonProperty("metrics")
    private List<Metric> metrics = new ArrayList<>();

    public SemanticModel() {}

    public SemanticModel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public AiContext getAiContext() {
        return aiContext;
    }

    public void setAiContext(AiContext aiContext) {
        this.aiContext = aiContext;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public Optional<Dataset> findDataset(String datasetName) {
        if (datasets == null) return Optional.empty();
        return datasets.stream().filter(d -> d.getName().equalsIgnoreCase(datasetName)).findFirst();
    }

    public Optional<Metric> findMetric(String metricName) {
        // First check model-scoped metrics
        if (metrics != null) {
            Optional<Metric> found = metrics.stream().filter(m -> m.getName().equalsIgnoreCase(metricName)).findFirst();
            if (found.isPresent()) return found;
        }
        // Then search dataset-scoped metrics
        if (datasets != null) {
            for (Dataset dataset : datasets) {
                Optional<Metric> found = dataset.findMetric(metricName);
                if (found.isPresent()) return found;
            }
        }
        return Optional.empty();
    }

    public Optional<Dataset> findDatasetForMetric(String metricName) {
        if (datasets != null) {
            for (Dataset dataset : datasets) {
                if (dataset.findMetric(metricName).isPresent()) {
                    return Optional.of(dataset);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Dataset> findDatasetForField(String fieldName) {
        if (datasets != null) {
            for (Dataset dataset : datasets) {
                if (dataset.findField(fieldName).isPresent()) {
                    return Optional.of(dataset);
                }
            }
        }
        return Optional.empty();
    }
}
