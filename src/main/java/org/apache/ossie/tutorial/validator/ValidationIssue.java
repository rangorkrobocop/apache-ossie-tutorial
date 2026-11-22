package org.apache.ossie.tutorial.validator;

/**
 * Represents a validation problem found during semantic model checking.
 */
public class ValidationIssue {

    public enum Severity {
        ERROR,
        WARNING,
        INFO
    }

    private final Severity severity;
    private final String component;
    private final String message;
    private final String suggestion;

    public ValidationIssue(Severity severity, String component, String message, String suggestion) {
        this.severity = severity;
        this.component = component;
        this.message = message;
        this.suggestion = suggestion;
    }

    public static ValidationIssue error(String component, String message, String suggestion) {
        return new ValidationIssue(Severity.ERROR, component, message, suggestion);
    }

    public static ValidationIssue warning(String component, String message, String suggestion) {
        return new ValidationIssue(Severity.WARNING, component, message, suggestion);
    }

    public static ValidationIssue info(String component, String message, String suggestion) {
        return new ValidationIssue(Severity.INFO, component, message, suggestion);
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getComponent() {
        return component;
    }

    public String getMessage() {
        return message;
    }

    public String getSuggestion() {
        return suggestion;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s (Suggestion: %s)", severity, component, message, suggestion);
    }
}
