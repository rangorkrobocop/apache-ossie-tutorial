package org.apache.ossie.tutorial.steps;

import org.apache.ossie.tutorial.graph.JoinEdge;
import org.apache.ossie.tutorial.graph.JoinPath;
import org.apache.ossie.tutorial.graph.SemanticGraph;
import org.apache.ossie.tutorial.model.Relationship;
import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * STEP 4: Layer 2 - Relational Semantic Graph & Multi-Hop Joins
 *
 * Run with:
 *   ./gradlew step4
 */
public class Step4_Layer2_RelationalGraphAndJoins {

    public static void main(String[] args) throws Exception {
        System.out.println("""
            ========================================================================
             STEP 4: Layer 2 - Relational Semantic Graph & Multi-Hop Joins
            ========================================================================
            In traditional data stacks, users must manually write table JOINs, which
            frequently leads to:
              • Chasm Traps: Joining two 1-to-many tables across a shared entity.
              • Fan Traps: Joining an aggregate measure across a many-to-one relationship,
                accidentally multiplying and inflating totals (e.g. counting revenue 5x).
              • Broken Join Paths: Joining tables without primary/foreign key alignment.

            HOW APACHE OSSIE LAYER 2 SOLVES THIS:
            Ossie models datasets as vertices in a Semantic Graph and relationships as
            directed edges annotated with cardinality (ONE_TO_ONE, MANY_TO_ONE, etc.).
            When queries need data from multiple datasets, the Ossie Graph Engine
            traverses the graph using Breadth-First-Search (BFS) to compute the minimal,
            correct join path!
            """);

        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");
        SemanticGraph graph = new SemanticGraph(model);

        System.out.println("Discovered Relationships in Model:");
        for (Relationship rel : model.getRelationships()) {
            System.out.printf("  • [%s] %s -> %s%n    Join ON:   %s.%s = %s.%s%n    Type:      %s | Cardinality: %s%n%n",
                    rel.getName(), rel.getFrom(), rel.getTo(),
                    rel.getFrom(), rel.getFromColumns(),
                    rel.getTo(), rel.getToColumns(),
                    rel.getJoinType(), rel.getCardinality());
        }

        System.out.println("------------------------------------------------------------------------");
        System.out.println("Pathfinding Traversal Demo:");
        System.out.println("------------------------------------------------------------------------");

        // Single hop: orders -> customers
        System.out.println("1. Single-hop query: 'orders' to 'customers'");
        Optional<JoinPath> path1 = graph.findShortestPath("orders", "customers");
        System.out.println("   Route: " + path1.map(JoinPath::toString).orElse("NO PATH"));
        System.out.println("   Hops:  " + path1.map(JoinPath::hopCount).orElse(0));

        // Multi hop: order_items -> customers (order_items -> orders -> customers)
        System.out.println("\n2. Multi-hop query: 'order_items' to 'customers'");
        Optional<JoinPath> path2 = graph.findShortestPath("order_items", "customers");
        System.out.println("   Route: " + path2.map(JoinPath::toString).orElse("NO PATH"));
        System.out.println("   Hops:  " + path2.map(JoinPath::hopCount).orElse(0));

        // Multi-table Join Tree: Root = orders, Targets = {customers, order_items, products}
        System.out.println("\n3. Resolving Multi-Table Join Tree:");
        System.out.println("   Root Dataset: 'orders'");
        System.out.println("   Required Datasets: [customers, order_items, products]");
        List<JoinEdge> joinTree = graph.resolveJoinTree("orders", Set.of("customers", "order_items", "products"));

        System.out.println("   Generated Join Sequence:");
        for (int i = 0; i < joinTree.size(); i++) {
            JoinEdge edge = joinTree.get(i);
            System.out.printf("     Step %d: %s JOIN %s ON %s [%s]%n",
                    i + 1, edge.getJoinType(), edge.getToDataset(), edge.toJoinConditionSql(), edge.getCardinality());
        }

        System.out.println("""

            ========================================================================
             STEP 4 SUMMARY:
             • Layer 2 abstracts the physical schema into a directed semantic graph.
             • Join paths are automatically discovered and verified by graph traversal.
             • Next: Run `./gradlew step5` to see Layer 3 Wide-Table Query Compilation!
            ========================================================================
            """);
    }
}
