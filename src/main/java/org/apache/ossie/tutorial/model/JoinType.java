package org.apache.ossie.tutorial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * SQL Join type for relationships.
 */
public enum JoinType {
    INNER("inner"),
    LEFT_OUTER("left_outer"),
    RIGHT_OUTER("right_outer"),
    FULL_OUTER("full_outer");

    private final String value;

    JoinType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static JoinType fromValue(String value) {
        if (value == null) return LEFT_OUTER;
        for (JoinType j : values()) {
            if (j.value.equalsIgnoreCase(value) || j.name().equalsIgnoreCase(value)) {
                return j;
            }
        }
        return LEFT_OUTER;
    }
}
