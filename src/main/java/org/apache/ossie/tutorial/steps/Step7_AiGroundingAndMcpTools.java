package org.apache.ossie.tutorial.steps;

import org.apache.ossie.tutorial.ai.AiGroundingService;
import org.apache.ossie.tutorial.ai.McpToolDefinition;
import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.apache.ossie.tutorial.query.SemanticQuery;

import java.util.List;
import java.util.Optional;

/**
 * STEP 7: AI Grounding, Synonyms & MCP Tool Integration
 *
 * Run with:
 *   ./gradlew step7
 */
public class Step7_AiGroundingAndMcpTools {

    public static void main(String[] args) throws Exception {
        System.out.println("""
            ========================================================================
             STEP 7: AI Grounding, Synonyms & MCP (Model Context Protocol) Tools
            ========================================================================
            WHY TEXT-TO-SQL FAILS WITHOUT APACHE OSSIE:
            When LLMs write SQL directly against raw tables:
              • They hallucinate formulas (e.g. inventing their own formula for churn).
              • They join on the wrong foreign keys.
              • They confuse business terms (e.g. "sales" vs "gross revenue" vs "GMV").

            HOW APACHE OSSIE SOLVES THIS:
            1. 'ai_context' blocks: Instructions and synonyms embedded in the spec.
            2. System Prompt Generation: Rich contextual cards provided to the LLM.
            3. Tool / MCP Interface: The LLM calls 'execute_semantic_query' instead
               of generating raw SQL!
            """);

        OssieModelParser parser = new OssieModelParser();
        SemanticModel model = parser.parseFromResource("/models/ecommerce_model.yaml");
        AiGroundingService aiService = new AiGroundingService(model);

        // 1. Generate System Prompt
        System.out.println("------------------------------------------------------------------------");
        System.out.println("1. Generated LLM System Prompt Grounding Card:");
        System.out.println("------------------------------------------------------------------------");
        String prompt = aiService.generateSystemPrompt();
        System.out.println(prompt);

        // 2. Demonstrate Synonym Resolution
        System.out.println("------------------------------------------------------------------------");
        System.out.println("2. Business Synonym Matching (No Metric Drift):");
        System.out.println("------------------------------------------------------------------------");
        String[] userPhrases = {
                "What was our GMV in Europe?",
                "How many registered buyers do we have?",
                "What is our average basket size?",
                "Show me product volume sold by category"
        };

        for (String phrase : userPhrases) {
            Optional<String> matched = aiService.matchMetricFromSynonym(phrase);
            System.out.printf("  User says:    \"%s\"%n  Ossie maps to: %s%n%n",
                    phrase, matched.map(m -> "'" + m + "' [Canonical Metric]").orElse("UNKNOWN"));
        }

        // 3. Simulate AI Agent Query Interpretation
        System.out.println("------------------------------------------------------------------------");
        System.out.println("3. Simulating AI Agent Translating Natural Language to Semantic Query:");
        System.out.println("------------------------------------------------------------------------");
        String agentPrompt = "What was our monthly sales and GMV for active customers in the US?";
        System.out.println("Natural Language Question: \"" + agentPrompt + "\"");

        SemanticQuery agentQuery = aiService.simulateAgentQueryInterpretation(agentPrompt);
        System.out.println("Agent Constructed Semantic Query:");
        System.out.println("  Metrics:    " + agentQuery.getMetrics());
        System.out.println("  Dimensions: " + agentQuery.getDimensions());
        System.out.println("  Filters:    " + agentQuery.getFilters());

        // 4. MCP Tools
        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("4. Model Context Protocol (MCP) Tools Provided to AI Agents:");
        System.out.println("------------------------------------------------------------------------");
        List<McpToolDefinition> tools = aiService.getMcpToolDefinitions();
        for (McpToolDefinition tool : tools) {
            System.out.printf("  • %-24s : %s%n", tool.getName(), tool.getDescription());
        }

        System.out.println("""

            ========================================================================
             STEP 7 SUMMARY:
             • With Apache Ossie, AI agents are strictly grounded in canonical metrics.
             • Business synonyms bridge natural language to verified data models.
             • Next: Run `./gradlew step8` to see multi-platform export (dbt & Snowflake)!
            ========================================================================
            """);
    }
}
