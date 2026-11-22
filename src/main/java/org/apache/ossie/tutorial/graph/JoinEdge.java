package org.apache.ossie.tutorial.graph;

import org.apache.ossie.tutorial.model.Cardinality;
import org.apache.ossie.tutorial.model.JoinType;
import org.apache.ossie.tutorial.model.Relationship;

import java.util.List;

/**
 * An edge in the Semantic Graph representing a join relationship between datasets.
 */
public class JoinEdge {

    private final String fromDataset;
    private final String toDataset;
    private final List<String> fromColumns;
    private final List<String> toColumns;
    private final Cardinality cardinality;
    private final JoinType joinType;
    private final Relationship relationship;

    public JoinEdge(Relationship rel) {
        this.fromDataset = rel.getFrom();
        this.toDataset = rel.getTo();
        this.fromColumns = rel.getFromColumns();
        this.toColumns = rel.getToColumns();
        this.cardinality = rel.getCardinality();
        this.joinType = rel.getJoinType();
        this.relationship = rel;
    }

    public JoinEdge(String fromDataset, String toDataset, List<String> fromColumns, List<String> toColumns,
                    Cardinality cardinality, JoinType joinType, Relationship relationship) {
        this.fromDataset = fromDataset;
        this.toDataset = toDataset;
        this.fromColumns = fromColumns;
        this.toColumns = toColumns;
        this.cardinality = cardinality;
        this.joinType = joinType;
        this.relationship = relationship;
    }

    public JoinEdge inverted() {
        Cardinality invertedCard = switch (cardinality) {
            case ONE_TO_MANY -> Cardinality.MANY_TO_ONE;
            case MANY_TO_ONE -> Cardinality.ONE_TO_MANY;
            default -> cardinality;
        };
        return new JoinEdge(toDataset, fromDataset, toColumns, fromColumns, invertedCard, joinType, relationship);
    }

    public String getFromDataset() {
        return fromDataset;
    }

    public String getToDataset() {
        return toDataset;
    }

    public List<String> getFromColumns() {
        return fromColumns;
    }

    public List<String> getToColumns() {
        return toColumns;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    /**
     * Generates the ON condition SQL for this edge.
     */
    public String toJoinConditionSql() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fromColumns.size(); i++) {
            if (i > 0) sb.append(" AND ");
            sb.append(fromDataset).append(".").append(fromColumns.get(i))
              .append(" = ")
              .append(toDataset).append(".").append(toColumns.get(i));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return fromDataset + " -[" + cardinality + "]-> " + toDataset + " ON " + toJoinConditionSql();
    }
}
