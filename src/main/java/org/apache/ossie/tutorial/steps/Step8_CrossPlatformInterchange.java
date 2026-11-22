package org.apache.ossie.tutorial.steps;

import org.apache.ossie.tutorial.converter.DbtMetricFlowConverter;
import org.apache.ossie.tutorial.converter.SnowflakeSemanticModelConverter;
import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;

/**
 * STEP 8: Cross-Platform Interoperability (dbt MetricFlow & Snowflake)
 *
 * Run with:
 *   ./gradlew step8
 */
public class Step8_CrossPlatformInterchange {

    public static void main(String[] args) throws Exception {
        System.out.println("""
            ========================================================================
             STEP 8: Cross-Platform Interoperability & Converters
            ========================================================================
            A major promise of Apache Ossie is "Define Once, Deploy Anywhere".
            
            Instead of getting locked into Looker LookML, dbt Semantic Layer, or
            Snowflake Semantic Views, organizations define their semantic models
            in Apache Ossie and compile or export them to any platform!
            """);

        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");

        // 1. Export to dbt MetricFlow
        System.out.println("------------------------------------------------------------------------");
        System.out.println("1. Exporting Apache Ossie Model to dbt Semantic Layer (MetricFlow YAML):");
        System.out.println("------------------------------------------------------------------------");
        DbtMetricFlowConverter dbtConverter = new DbtMetricFlowConverter();
        String dbtYaml = dbtConverter.convertToDbtSemanticLayerYaml(model);
        System.out.println(dbtYaml);

        // 2. Export to Snowflake Semantic Model Secure Views
        System.out.println("------------------------------------------------------------------------");
        System.out.println("2. Exporting Apache Ossie Model to Snowflake Semantic View DDL:");
        System.out.println("------------------------------------------------------------------------");
        SnowflakeSemanticModelConverter sfConverter = new SnowflakeSemanticModelConverter();
        String sfSql = sfConverter.convertToSnowflakeSemanticModel(model);
        System.out.println(sfSql);

        System.out.println("""
            ========================================================================
             CONGRATULATIONS! YOU HAVE COMPLETED THE FULL APACHE OSSIE GUIDE!
            ========================================================================
            You have now mastered:
              ✓ Step 1: Model Ingestion & AST (YAML/JSON schemas)
              ✓ Step 2: Semantic Integrity Validation & diagnostics
              ✓ Step 3: Layer 1 Common Expressions & Multi-Dialect Portability
              ✓ Step 4: Layer 2 Relational Semantic Graph & Join Path Routing
              ✓ Step 5: Layer 3 Wide-Table Query Compilation
              ✓ Step 6: Executing Queries on a Real Relational Engine
              ✓ Step 7: AI Grounding, Synonyms & MCP Tooling for LLMs
              ✓ Step 8: Cross-Platform Interchange with dbt and Snowflake
            ========================================================================
            """);
    }
}
