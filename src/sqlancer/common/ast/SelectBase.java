package sqlancer.common.ast;

import java.util.Collections;
import java.util.List;

import sqlancer.Randomly;
import sqlancer.common.ast.newast.Expression;

public class SelectBase<T extends Expression<?>> {

    List<T> fetchColumns;
    List<T> groupByExpressions = Collections.emptyList();
    List<T> orderByExpressions = Collections.emptyList();
    List<T> joinList = Collections.emptyList();
    List<T> fromList;
    T whereClause;
    T havingClause;
    T limitClause;
    T offsetClause;
    public SelectType selectOption = SelectType.ALL; // default value

    public void setFetchColumns(List<T> fetchColumns) {
        if (fetchColumns == null || fetchColumns.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.fetchColumns = fetchColumns;
    }

    public List<T> getFetchColumns() {
        if (fetchColumns == null) {
            throw new IllegalStateException();
        }
        return fetchColumns;
    }

    public void setFromList(List<T> fromList) {
        if (fromList == null /* || fromList.size() == 0 TODO: refactor the CockroachDB implementation */) {
            throw new IllegalArgumentException();
        }
        this.fromList = fromList;
    }

    public void setFromTables(List<T> tables) {
        setFromList(tables);
    }

    public List<T> getFromList() {
        if (fromList == null) {
            throw new IllegalStateException();
        }
        return fromList;
    }

    public void setGroupByExpressions(List<T> groupByExpressions) {
        if (groupByExpressions == null) {
            throw new IllegalArgumentException();
        }
        this.groupByExpressions = groupByExpressions;
    }

    public void clearGroupByExpressions() {
        this.groupByExpressions = Collections.emptyList();
    }

    public List<T> getGroupByExpressions() {
        assert groupByExpressions != null;
        return groupByExpressions;
    }

    public void setOrderByClauses(List<T> orderByExpressions) {
        if (orderByExpressions == null) {
            throw new IllegalArgumentException();
        }
        this.orderByExpressions = orderByExpressions;
    }

    public List<T> getOrderByClauses() {
        assert orderByExpressions != null;
        return orderByExpressions;
    }

    public void setWhereClause(T whereClause) {
        this.whereClause = whereClause;
    }

    public T getWhereClause() {
        return whereClause;
    }

    public void setHavingClause(T havingClause) {
        this.havingClause = havingClause;
    }

    public T getHavingClause() {
        return havingClause;
    }

    public void clearHavingClause() {
        this.havingClause = null;
    }

    public void setLimitClause(T limitClause) {
        this.limitClause = limitClause;
    }

    public T getLimitClause() {
        return limitClause;
    }

    public void setOffsetClause(T offsetClause) {
        this.offsetClause = offsetClause;
    }

    public T getOffsetClause() {
        return offsetClause;
    }

    public List<T> getJoinList() {
        return joinList;
    }

    public void setJoinList(List<T> joinList) {
        this.joinList = joinList;
    }

    public List<T> getGroupByClause() {
        return getGroupByExpressions();
    }

    public void setGroupByClause(List<T> groupByExpressions) {
        setGroupByExpressions(groupByExpressions);
    }

    public boolean isDistinct() {
        return selectOption == SelectType.DISTINCT;
    }

    public List<JoinBase<T>> getJoinClauses() {
        return null;
    }

    public T getDistinctOnClause() {
        return null;
    }

    public enum SelectType {
        DISTINCT, ALL;

        public static SelectType getRandom() {
            return Randomly.fromOptions(values());
        }
    }

    public SelectType getSelectOption() {
        return selectOption;
    }

    public void setSelectOption(SelectType option) {
        this.selectOption = option;
    }
}
