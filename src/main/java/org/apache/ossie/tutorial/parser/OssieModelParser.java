package org.apache.ossie.tutorial.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.ossie.tutorial.model.SemanticModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Parser for Apache Ossie Semantic Model documents in YAML or JSON format.
 */
public class OssieModelParser {

    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;

    public OssieModelParser() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.yamlMapper.registerModule(new JavaTimeModule());

        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.registerModule(new JavaTimeModule());
    }

    public SemanticModel parseYaml(String yaml) throws IOException {
        return yamlMapper.readValue(yaml, SemanticModel.class);
    }

    public SemanticModel parseYaml(File file) throws IOException {
        return yamlMapper.readValue(file, SemanticModel.class);
    }

    public SemanticModel parseYaml(InputStream inputStream) throws IOException {
        return yamlMapper.readValue(inputStream, SemanticModel.class);
    }

    public SemanticModel parseJson(String json) throws IOException {
        return jsonMapper.readValue(json, SemanticModel.class);
    }

    public SemanticModel parseFromResource(String resourcePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            if (resourcePath.endsWith(".json")) {
                return jsonMapper.readValue(is, SemanticModel.class);
            }
            return yamlMapper.readValue(is, SemanticModel.class);
        }
    }
}
