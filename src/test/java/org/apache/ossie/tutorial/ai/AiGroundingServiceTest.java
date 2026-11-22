package org.apache.ossie.tutorial.ai;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.apache.ossie.tutorial.query.SemanticQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AiGroundingServiceTest {

    private AiGroundingService aiService;

    @BeforeEach
    void setUp() throws Exception {
        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");
        this.aiService = new AiGroundingService(model);
    }

    @Test
    void testSystemPromptGeneration() {
        String prompt = aiService.generateSystemPrompt();
        assertThat(prompt).contains("Apache Ossie");
        assertThat(prompt).contains("total_revenue");
        assertThat(prompt).contains("customer_count");
        assertThat(prompt).contains("Guardrails");
    }

    @Test
    void testSynonymMatching() {
        Optional<String> matched = aiService.matchMetricFromSynonym("show me our gmv for this year");
        assertThat(matched).isPresent();
        assertThat(matched.get()).isEqualTo("total_revenue");
    }

    @Test
    void testAgentSimulation() {
        SemanticQuery query = aiService.simulateAgentQueryInterpretation("Monthly sales in USA");
        assertThat(query.getMetrics()).contains("total_revenue");
        assertThat(query.getDimensions()).contains("orders.order_date[month]");
        assertThat(query.getFilters()).contains("customers.country = 'USA'");
    }

    @Test
    void testMcpToolDefinitions() {
        List<McpToolDefinition> tools = aiService.getMcpToolDefinitions();
        assertThat(tools).hasSize(3);
        assertThat(tools.stream().map(McpToolDefinition::getName).toList())
                .contains("list_metrics", "list_dimensions", "execute_semantic_query");
    }
}
