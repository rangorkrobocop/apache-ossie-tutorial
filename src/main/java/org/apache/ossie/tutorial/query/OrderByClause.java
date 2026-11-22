package org.apache.ossie.tutorial.query;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ordering clause for a semantic query.
 */
public class OrderByClause {

    public enum Direction {
        ASC, DESC
    }

    @JsonProperty("field")
    private String field;

    @JsonProperty("direction")
    private Direction direction = Direction.ASC;

    public OrderByClause() {}

    public OrderByClause(String field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public static OrderByClause asc(String field) {
        return new OrderByClause(field, Direction.ASC);
    }

    public static OrderByClause desc(String field) {
        return new OrderByClause(field, Direction.DESC);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return field + " " + direction;
    }
}
