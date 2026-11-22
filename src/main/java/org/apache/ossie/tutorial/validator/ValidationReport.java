package org.apache.ossie.tutorial.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds all validation issues and provides summary status.
 */
public class ValidationReport {

    private final List<ValidationIssue> issues = new ArrayList<>();

    public void addIssue(ValidationIssue issue) {
        issues.add(issue);
    }

    public List<ValidationIssue> getIssues() {
        return issues;
    }

    public boolean isValid() {
        return issues.stream().noneMatch(i -> i.getSeverity() == ValidationIssue.Severity.ERROR);
    }

    public List<ValidationIssue> getErrors() {
        return issues.stream()
                .filter(i -> i.getSeverity() == ValidationIssue.Severity.ERROR)
                .collect(Collectors.toList());
    }

    public List<ValidationIssue> getWarnings() {
        return issues.stream()
                .filter(i -> i.getSeverity() == ValidationIssue.Severity.WARNING)
                .collect(Collectors.toList());
    }

    public String formatReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("          OSSIE MODEL VALIDATION REPORT             \n");
        sb.append("====================================================\n");
        sb.append("Status: ").append(isValid() ? "PASSED (Valid)" : "FAILED (Errors Detected)").append("\n");
        sb.append("Total Issues: ").append(issues.size())
          .append(" (Errors: ").append(getErrors().size())
          .append(", Warnings: ").append(getWarnings().size()).append(")\n\n");

        for (ValidationIssue issue : issues) {
            String prefix = switch (issue.getSeverity()) {
                case ERROR -> " [ERROR]   ";
                case WARNING -> " [WARNING] ";
                case INFO -> " [INFO]    ";
            };
            sb.append(prefix).append(issue.getComponent()).append(": ").append(issue.getMessage()).append("\n");
            if (issue.getSuggestion() != null && !issue.getSuggestion().isBlank()) {
                sb.append("           ↳ Fix: ").append(issue.getSuggestion()).append("\n");
            }
        }
        sb.append("====================================================\n");
        return sb.toString();
    }
}
