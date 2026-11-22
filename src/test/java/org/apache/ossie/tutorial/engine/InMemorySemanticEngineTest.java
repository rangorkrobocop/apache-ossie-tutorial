package org.apache.ossie.tutorial.engine;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.apache.ossie.tutorial.query.OrderByClause;
import org.apache.ossie.tutorial.query.SemanticQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemorySemanticEngineTest {

    private InMemorySemanticEngine engine;

    @BeforeEach
    void setUp() throws Exception {
        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");
        this.engine = new InMemorySemanticEngine(model);
        this.engine.seedFromResource("/data/seed_ecommerce.sql");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (engine != null) {
            engine.close();
        }
    }

    @Test
    void testExecuteRevenueByCountry() throws Exception {
        SemanticQuery query = SemanticQuery.builder()
                .metric("total_revenue")
                .metric("order_count")
                .dimension("customers.country")
                .orderBy("total_revenue", OrderByClause.Direction.DESC)
                .build();

        QueryResult result = engine.execute(query);
        assertThat(result.getRowCount()).isGreaterThan(0);
        assertThat(result.getColumnNames()).contains("customers_country", "total_revenue", "order_count");

        // USA has Alice (250+120) and Bob (320+500) = 1190.00
        assertThat(result.getRows().get(0).get(0)).isEqualTo("USA");
    }

    @Test
    void testExecuteProductCategoryUnitsSold() throws Exception {
        SemanticQuery query = SemanticQuery.builder()
                .metric("total_units_sold")
                .dimension("products.category")
                .build();

        QueryResult result = engine.execute(query);
        assertThat(result.getRowCount()).isGreaterThan(0);
        assertThat(result.getColumnNames()).contains("products_category", "total_units_sold");
    }
}
