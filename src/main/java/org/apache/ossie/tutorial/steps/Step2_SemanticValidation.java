package org.apache.ossie.tutorial.steps;

import org.apache.ossie.tutorial.model.SemanticModel;
import org.apache.ossie.tutorial.parser.OssieModelParser;
import org.apache.ossie.tutorial.validator.OssieModelValidator;
import org.apache.ossie.tutorial.validator.ValidationIssue;
import org.apache.ossie.tutorial.validator.ValidationReport;

/**
 * STEP 2: Semantic Integrity Validation
 *
 * Run with:
 *   ./gradlew step2
 */
public class Step2_SemanticValidation {

    public static void main(String[] args) throws Exception {
        System.out.println("""
            ========================================================================
             STEP 2: Apache Ossie Semantic Validation Engine
            ========================================================================
            A critical feature of Apache Ossie is strict validation.
            Before any semantic model can be deployed or queried, Ossie validates:
              1. Dataset uniqueness and valid physical table sources.
              2. Primary key declarations for deterministic entity resolution.
              3. Foreign key integrity in relationships (verifying that referenced
                 tables and columns actually exist).
              4. Metric expression syntax and circular dependency checks.
              5. AI context completeness (ensuring instructions exist for LLMs).
            """);

        OssieModelParser parser = new OssieModelParser();
        OssieModelValidator validator = new OssieModelValidator();

        // 1. Validate the correct e-commerce model
        System.out.println("--> Validating production model: 'ecommerce_model.yaml'...");
        SemanticModel validModel = parser.parseFromResource("/models/ecommerce_model.yaml");
        ValidationReport validReport = validator.validate(validModel);

        System.out.println("Is Valid? " + (validReport.isValid() ? "YES [PASSED]" : "NO [FAILED]"));
        System.out.println(validReport.formatReport());

        // 2. Validate the intentionally flawed model
        System.out.println("\n--> Validating intentionally flawed model: 'invalid_model.yaml'...");
        SemanticModel invalidModel = parser.parseFromResource("/models/invalid_model.yaml");
        ValidationReport invalidReport = validator.validate(invalidModel);

        System.out.println("Is Valid? " + (invalidReport.isValid() ? "YES [PASSED]" : "NO [FAILED]"));
        System.out.println(invalidReport.formatReport());

        System.out.println("Detailed Issue Analysis:");
        for (ValidationIssue issue : invalidReport.getIssues()) {
            System.out.printf("  [%s] In '%s':%n    Problem:    %s%n    Actionable: %s%n%n",
                    issue.getSeverity(), issue.getComponent(), issue.getMessage(), issue.getSuggestion());
        }

        System.out.println("""
            ========================================================================
             STEP 2 SUMMARY:
             • The Ossie validator prevents runtime SQL crashes and metric calculation errors
               by enforcing schema contracts at definition time.
             • Next: Run `./gradlew step3` to explore Layer 1 Expressions & Dialects!
            ========================================================================
            """);
    }
}
