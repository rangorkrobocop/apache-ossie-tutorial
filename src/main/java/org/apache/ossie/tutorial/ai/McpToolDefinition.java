package org.apache.ossie.tutorial.ai;

import java.util.Map;

/**
 * Representation of a tool schema formatted for LLM Function Calling and MCP (Model Context Protocol).
 */
public class McpToolDefinition {

    private final String name;
    private final String description;
    private final Map<String, Object> inputSchema;

    public McpToolDefinition(String name, String description, Map<String, Object> inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getInputSchema() {
        return inputSchema;
    }

    @Override
    public String toString() {
        return "Tool: " + name + " - " + description;
    }
}
