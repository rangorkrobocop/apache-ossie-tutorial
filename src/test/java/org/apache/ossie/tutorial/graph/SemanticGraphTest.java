package org.apache.ossie.tutorial.graph;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SemanticGraphTest {

    private SemanticGraph graph;

    @BeforeEach
    void setUp() throws Exception {
        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");
        this.graph = new SemanticGraph(model);
    }

    @Test
    void testDirectJoinPath() {
        Optional<JoinPath> path = graph.findShortestPath("orders", "customers");
        assertThat(path).isPresent();
        assertThat(path.get().hopCount()).isEqualTo(1);
    }

    @Test
    void testMultiHopJoinPath() {
        // order_items -> orders -> customers
        Optional<JoinPath> path = graph.findShortestPath("order_items", "customers");
        assertThat(path).isPresent();
        assertThat(path.get().hopCount()).isEqualTo(2);
        assertThat(path.get().getEdges().get(0).getFromDataset()).isEqualTo("order_items");
        assertThat(path.get().getEdges().get(0).getToDataset()).isEqualTo("orders");
        assertThat(path.get().getEdges().get(1).getFromDataset()).isEqualTo("orders");
        assertThat(path.get().getEdges().get(1).getToDataset()).isEqualTo("customers");
    }

    @Test
    void testResolveJoinTreeForMultipleDatasets() {
        List<JoinEdge> joins = graph.resolveJoinTree("orders", Set.of("customers", "order_items", "products"));
        assertThat(joins).isNotEmpty();
        assertThat(joins.stream().map(JoinEdge::getToDataset).toList())
                .contains("customers", "order_items", "products");
    }
}
