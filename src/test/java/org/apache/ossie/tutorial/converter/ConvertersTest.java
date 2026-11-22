package org.apache.ossie.tutorial.converter;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConvertersTest {

    private SemanticModel model;

    @BeforeEach
    void setUp() throws Exception {
        OssieModelParser parser = new OssieModelParser();
        this.model = parser.parseFromResource("/models/ecommerce_model.yaml");
    }

    @Test
    void testDbtMetricFlowConverter() {
        DbtMetricFlowConverter converter = new DbtMetricFlowConverter();
        String dbtYaml = converter.convertToDbtSemanticLayerYaml(model);

        assertThat(dbtYaml).contains("semantic_models:");
        assertThat(dbtYaml).contains("name: customers");
        assertThat(dbtYaml).contains("name: orders");
        assertThat(dbtYaml).contains("measures:");
        assertThat(dbtYaml).contains("name: total_revenue");
    }

    @Test
    void testSnowflakeConverter() {
        SnowflakeSemanticModelConverter converter = new SnowflakeSemanticModelConverter();
        String sfSql = converter.convertToSnowflakeSemanticModel(model);

        assertThat(sfSql).contains("CREATE OR REPLACE SECURE VIEW");
        assertThat(sfSql).contains("orders_semantic_view");
        assertThat(sfSql).contains("customers_semantic_view");
    }
}
