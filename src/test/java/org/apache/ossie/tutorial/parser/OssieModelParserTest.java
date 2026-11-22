package org.apache.ossie.tutorial.parser;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OssieModelParserTest {

    @Test
    void testParseYamlFromResource() throws Exception {
        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");

        assertThat(model).isNotNull();
        assertThat(model.getName()).isEqualTo("ecommerce_sales_analytics");
        assertThat(model.getVersion()).isEqualTo("0.3.0");
        assertThat(model.getDatasets()).hasSize(4);
        assertThat(model.getRelationships()).hasSize(3);

        // Verify dataset details
        var ordersDs = model.findDataset("orders");
        assertThat(ordersDs).isPresent();
        assertThat(ordersDs.get().getPrimaryKey()).containsExactly("order_id");
        assertThat(ordersDs.get().getMetrics()).hasSize(3);

        // Verify AI Context
        assertThat(model.getAiContext()).isNotNull();
        assertThat(model.getAiContext().getInstructions()).contains("Use this semantic model");
    }

    @Test
    void testParseAndSerializeRoundtrip() throws Exception {
        OssieModelParser parser = new OssieModelParser();
        OssieModelSerializer serializer = new OssieModelSerializer();

        SemanticModel model = parser.parseFromResource("/models/saas_subscription_model.yaml");
        String yaml = serializer.toYaml(model);
        assertThat(yaml).contains("saas_subscription_analytics");

        SemanticModel roundtrip = parser.parseYaml(yaml);
        assertThat(roundtrip.getName()).isEqualTo(model.getName());
        assertThat(roundtrip.getDatasets()).hasSize(2);
    }
}
