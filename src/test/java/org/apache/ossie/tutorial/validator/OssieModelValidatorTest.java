package org.apache.ossie.tutorial.validator;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OssieModelValidatorTest {

    @Test
    void testValidateValidEcommerceModel() throws Exception {
        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");

        OssieModelValidator validator = new OssieModelValidator();
        ValidationReport report = validator.validate(model);

        assertThat(report.isValid()).isTrue();
        assertThat(report.getErrors()).isEmpty();
    }

    @Test
    void testValidateFlawedModelDetectsErrors() throws Exception {
        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/invalid_model.yaml");

        OssieModelValidator validator = new OssieModelValidator();
        ValidationReport report = validator.validate(model);

        assertThat(report.isValid()).isFalse();
        assertThat(report.getErrors()).isNotEmpty();
        assertThat(report.formatReport()).contains("warehouse");
    }
}
