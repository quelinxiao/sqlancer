package sqlancer.yugabyte.ysql.ast;

import java.util.Collections;
import java.util.List;

import sqlancer.Randomly;
import sqlancer.common.ast.JoinBase;
import sqlancer.common.ast.SelectBase;
import sqlancer.common.ast.newast.Select;
import sqlancer.yugabyte.ysql.YSQLSchema.YSQLColumn;
import sqlancer.yugabyte.ysql.YSQLSchema.YSQLDataType;
import sqlancer.yugabyte.ysql.YSQLSchema.YSQLTable;
import sqlancer.yugabyte.ysql.YSQLVisitor;

public class YSQLSelect extends SelectBase<YSQLExpression>
        implements YSQLExpression, Select<YSQLJoin, YSQLExpression, YSQLTable, YSQLColumn> {

    private List<JoinBase<YSQLExpression>> joinClauses = Collections.emptyList();
    private YSQLExpression distinctOnClause;
    private ForClause forClause;

    public void setSelectType(SelectType fromOptions) {
        this.setSelectOption(fromOptions);
    }

    @Override
    public YSQLDataType getExpressionType() {
        return null;
    }

    @Override
    public List<JoinBase<YSQLExpression>> getJoinClauses() {
        return joinClauses;
    }

    @Override
    public void setJoinClauses(List<JoinBase<YSQLExpression>> joinStatements) {
        this.joinClauses = joinStatements;

    }

    @Override
    public YSQLExpression getDistinctOnClause() {
        return distinctOnClause;
    }

    public void setDistinctOnClause(YSQLExpression distinctOnClause) {
        if (selectOption != SelectType.DISTINCT) {
            throw new IllegalArgumentException();
        }
        this.distinctOnClause = distinctOnClause;
    }

    public ForClause getForClause() {
        return forClause;
    }

    public void setForClause(ForClause forClause) {
        this.forClause = forClause;
    }

    public enum ForClause {
        UPDATE("UPDATE"), NO_KEY_UPDATE("NO KEY UPDATE"), SHARE("SHARE"), KEY_SHARE("KEY SHARE");

        private final String textRepresentation;

        ForClause(String textRepresentation) {
            this.textRepresentation = textRepresentation;
        }

        public static ForClause getRandom() {
            return Randomly.fromOptions(values());
        }

        public String getTextRepresentation() {
            return textRepresentation;
        }
    }

    public static class YSQLFromTable implements YSQLExpression {
        private final YSQLTable t;
        private final boolean only;

        public YSQLFromTable(YSQLTable t, boolean only) {
            this.t = t;
            this.only = only;
        }

        public YSQLTable getTable() {
            return t;
        }

        public boolean isOnly() {
            return only;
        }

        @Override
        public YSQLDataType getExpressionType() {
            return null;
        }
    }

    public static class YSQLSubquery implements YSQLExpression {
        private final YSQLSelect s;
        private final String name;

        public YSQLSubquery(YSQLSelect s, String name) {
            this.s = s;
            this.name = name;
        }

        public YSQLSelect getSelect() {
            return s;
        }

        public String getName() {
            return name;
        }

        @Override
        public YSQLDataType getExpressionType() {
            return null;
        }
    }

    @Override
    public String asString() {
        return YSQLVisitor.asString(this);
    }
}
