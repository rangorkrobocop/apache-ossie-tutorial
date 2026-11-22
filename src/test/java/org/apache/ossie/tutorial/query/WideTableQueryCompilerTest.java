package org.apache.ossie.tutorial.query;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.model.SqlDialect;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WideTableQueryCompilerTest {

    private WideTableQueryCompiler compiler;

    @BeforeEach
    void setUp() throws Exception {
        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");
        this.compiler = new WideTableQueryCompiler(model);
    }

    @Test
    void testCompileSimpleQuery() {
        SemanticQuery query = SemanticQuery.builder()
                .metric("total_revenue")
                .dimension("customers.country")
                .build();

        CompiledQuery compiled = compiler.compile(query, SqlDialect.ANSI_SQL);
        assertThat(compiled.getSql()).contains("SELECT");
        assertThat(compiled.getSql()).contains("SUM(orders.total_amount) AS total_revenue");
        assertThat(compiled.getSql()).contains("LEFT JOIN customers AS customers");
        assertThat(compiled.getSql()).contains("GROUP BY");
    }

    @Test
    void testCompileTimeGrainQueryDialects() {
        SemanticQuery query = SemanticQuery.builder()
                .metric("order_count")
                .dimension("orders.order_date[month]")
                .build();

        CompiledQuery ansi = compiler.compile(query, SqlDialect.ANSI_SQL);
        assertThat(ansi.getSql()).contains("DATE_TRUNC('month', orders.order_date)");

        CompiledQuery snowflake = compiler.compile(query, SqlDialect.SNOWFLAKE);
        assertThat(snowflake.getSql()).contains("DATE_TRUNC('MONTH', orders.order_date)");

        CompiledQuery bigquery = compiler.compile(query, SqlDialect.BIGQUERY);
        assertThat(bigquery.getSql()).contains("DATE_TRUNC(orders.order_date, MONTH)");
    }
}
