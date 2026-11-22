package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Cardinality of a relationship in Apache Ossie.
 * Essential for preventing fan-traps and chasm-traps during semantic query compilation.
 */
public enum Cardinality {
    ONE_TO_ONE("one_to_one"),
    ONE_TO_MANY("one_to_many"),
    MANY_TO_ONE("many_to_one"),
    MANY_TO_MANY("many_to_many");

    private final String value;

    Cardinality(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Cardinality fromValue(String value) {
        if (value == null) return MANY_TO_ONE;
        for (Cardinality c : values()) {
            if (c.value.equalsIgnoreCase(value) || c.name().equalsIgnoreCase(value)) {
                return c;
            }
        }
        return MANY_TO_ONE;
    }
}
