package org.apache.ossie.tutorial.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.ossie.tutorial.model.SemanticModel;

import java.io.IOException;

/**
 * Serializer for exporting Apache Ossie Semantic Models to formatted YAML or JSON.
 */
public class OssieModelSerializer {

    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;

    public OssieModelSerializer() {
        YAMLFactory yamlFactory = new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);

        this.yamlMapper = new ObjectMapper(yamlFactory);
        this.yamlMapper.registerModule(new JavaTimeModule());
        this.yamlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.registerModule(new JavaTimeModule());
        this.jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String toYaml(SemanticModel model) throws IOException {
        return yamlMapper.writeValueAsString(model);
    }

    public String toJson(SemanticModel model) throws IOException {
        return jsonMapper.writeValueAsString(model);
    }
}
