package ru.clevertec.ecl.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QueryConstructor {
    private static final String NAMED_PARAMETER = ":%s";
    private static final String ORDER_BY = "order by %s";
    private static final String ORDER_BY_NEXT = ", %s";
    private static final String LIMIT = "limit :limit offset :offset";
    private static final String WHERE = " where %s %s %s";
    private static final String WHERE_NEXT = " and  %s %s %s";
    private StringBuilder query = new StringBuilder();
    private StringBuilder whereClauses = new StringBuilder();
    private StringBuilder orderByClauses = new StringBuilder();
    private StringBuilder paginationParams = new StringBuilder();
    private MapSqlParameterSource queryParameters;

    public void setQuery(StringBuilder query) {
        this.query = query;
    }

    public void addWhereClause(String column, Object value, Relation relation) {
        if (whereClauses.toString().isEmpty()) {
            whereClauses.append(
                    String.format(WHERE, column, relation.val,
                            String.format(NAMED_PARAMETER, value))
            );
        } else {
            whereClauses.append(
                    String.format(WHERE_NEXT, column, relation.val,
                            String.format(NAMED_PARAMETER, value))
            );
        }
        queryParameters.addValue(column, value);
    }

    public void addOrderClauses(String column) {
        if (orderByClauses.toString().isEmpty()) {
            orderByClauses.append(
                    String.format(ORDER_BY, String.format(NAMED_PARAMETER, column))
            );
        } else {
            orderByClauses.append(
                    String.format(ORDER_BY_NEXT, String.format(NAMED_PARAMETER, column))
            );
        }
        queryParameters.addValue(column, column);
    }

    public void addPaginationParams(int limit, int offset) {
        queryParameters.addValue("limit", limit);
        queryParameters.addValue("offset", offset);
    }

    public enum Relation {
        EQUALS("="), NOT_EQUALS("!="), MORE_THAN(">"), LESS_THAN("<"),
        MORE_OR_EQUAL_THAN(">="), LESS_OR_EQUAL_THAN("<=");

        private String val;

        Relation(String val) {this.val = val;}

        public String getVal() {
            return val;
        }
    }

    public static String applySort(String original, String field, Sort.Direction direction, boolean isAlreadySorted) {
        return isAlreadySorted
                ? String.format(ORDER_BY_NEXT, original, field, direction.name())
                : String.format(ORDER_BY, original + " ", field, direction.name());
    }

    public static String applySort(String original, Sort orders) {
        StringBuilder clauses = new StringBuilder();
        boolean isFirst = true;
        for (Sort.Order order : orders) {
            clauses.append(applySort("", order.getProperty(), order.getDirection(), !isFirst));
            isFirst = false;
        }
        return original + clauses;
    }

    public static String applyPagination(String original, Pageable pageable) {
        return String.format(LIMIT, original, pageable.getPageSize(), pageable.getPageNumber());
    }

    public static String applyWhereClause(String original, Map<String, String> params) {
        boolean isFirsClause = true;
        StringBuilder clauses = new StringBuilder();
        for (Map.Entry<String, String> el : params.entrySet()) {
            if (el.getValue() != null) {
                clauses.append(
                        isFirsClause
                                ? String.format(WHERE, el.getKey(), el.getValue())
                                : String.format(WHERE_NEXT, el.getKey(), el.getValue())
                );
                isFirsClause = false;
            }
        }
        return original + clauses;
    }
}
