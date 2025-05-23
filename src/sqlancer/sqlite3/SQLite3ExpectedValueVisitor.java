package sqlancer.sqlite3;

import sqlancer.common.ast.JoinBase;
import sqlancer.sqlite3.ast.SQLite3Aggregate;
import sqlancer.sqlite3.ast.SQLite3BetweenOperation;
import sqlancer.sqlite3.ast.SQLite3BinaryComparisonOperation;
import sqlancer.sqlite3.ast.SQLite3BinaryOperation;
import sqlancer.sqlite3.ast.SQLite3Case.CasePair;
import sqlancer.sqlite3.ast.SQLite3Case.SQLite3CaseWithBaseExpression;
import sqlancer.sqlite3.ast.SQLite3Case.SQLite3CaseWithoutBaseExpression;
import sqlancer.sqlite3.ast.SQLite3CollateOperation;
import sqlancer.sqlite3.ast.SQLite3ColumnName;
import sqlancer.sqlite3.ast.SQLite3Constant;
import sqlancer.sqlite3.ast.SQLite3Distinct;
import sqlancer.sqlite3.ast.SQLite3Exist;
import sqlancer.sqlite3.ast.SQLite3Expression;
import sqlancer.sqlite3.ast.SQLite3ExpressionCast;
import sqlancer.sqlite3.ast.SQLite3ExpressionFunction;
import sqlancer.sqlite3.ast.SQLite3Function;
import sqlancer.sqlite3.ast.SQLite3InOperation;
import sqlancer.sqlite3.ast.SQLite3Join;
import sqlancer.sqlite3.ast.SQLite3MatchOperation;
import sqlancer.sqlite3.ast.SQLite3OrderingTerm;
import sqlancer.sqlite3.ast.SQLite3PostfixText;
import sqlancer.sqlite3.ast.SQLite3PostfixUnaryOperation;
import sqlancer.sqlite3.ast.SQLite3RowValueExpression;
import sqlancer.sqlite3.ast.SQLite3Select;
import sqlancer.sqlite3.ast.SQLite3SetClause;
import sqlancer.sqlite3.ast.SQLite3Subquery;
import sqlancer.sqlite3.ast.SQLite3TableReference;
import sqlancer.sqlite3.ast.SQLite3Text;
import sqlancer.sqlite3.ast.SQLite3TypeLiteral;
import sqlancer.sqlite3.ast.SQLite3UnaryOperation;
import sqlancer.sqlite3.ast.SQLite3WindowFunction;
import sqlancer.sqlite3.ast.SQLite3WindowFunctionExpression;
import sqlancer.sqlite3.ast.SQLite3WindowFunctionExpression.SQLite3WindowFunctionFrameSpecBetween;
import sqlancer.sqlite3.ast.SQLite3WindowFunctionExpression.SQLite3WindowFunctionFrameSpecTerm;

public class SQLite3ExpectedValueVisitor implements SQLite3Visitor {

    private final StringBuilder sb = new StringBuilder();
    private int nrTabs;

    private void print(SQLite3Expression expr) {
        SQLite3ToStringVisitor v = new SQLite3ToStringVisitor();
        v.visit(expr);
        for (int i = 0; i < nrTabs; i++) {
            sb.append("\t");
        }
        sb.append(v.get());
        sb.append(" -- ");
        sb.append(expr.getExpectedValue());
        sb.append(" explicit collate: ");
        sb.append(expr.getExplicitCollateSequence());
        sb.append(" implicit collate: ");
        sb.append(expr.getImplicitCollateSequence());
        sb.append("\n");
    }

    @Override
    public void visit(SQLite3Expression expr) {
        nrTabs++;
        SQLite3Visitor.super.visit(expr);
        nrTabs--;
    }

    @Override
    public void visit(SQLite3BinaryOperation op) {
        print(op);
        visit(op.getLeft());
        visit(op.getRight());
    }

    @Override
    public void visit(SQLite3BetweenOperation op) {
        print(op);
        visit(op.getTopNode());
    }

    @Override
    public void visit(SQLite3ColumnName c) {
        print(c);
    }

    @Override
    public void visit(SQLite3Constant c) {
        print(c);
    }

    @Override
    public void visit(SQLite3ExpressionFunction f) {
        print(f);
        for (SQLite3Expression expr : f.getArguments()) {
            visit(expr);
        }
    }

    @Override
    public void visit(SQLite3Select s, boolean inner) {
        for (SQLite3Expression expr : s.getFetchColumns()) {
            if (expr instanceof SQLite3Aggregate) {
                visit(expr);
            }
        }
        for (JoinBase<SQLite3Expression> expr : s.getJoinClauses()) {
            visit((SQLite3Join) expr);
        }
        visit(s.getWhereClause());
        if (s.getHavingClause() != null) {
            visit(s.getHavingClause());
        }
    }

    @Override
    public void visit(SQLite3OrderingTerm term) {
        sb.append("(");
        print(term);
        visit(term.getExpression());
        sb.append(")");
    }

    @Override
    public void visit(SQLite3UnaryOperation exp) {
        print(exp);
        visit(exp.getExpression());
    }

    @Override
    public void visit(SQLite3PostfixUnaryOperation exp) {
        print(exp);
        visit(exp.getExpression());
    }

    @Override
    public void visit(SQLite3CollateOperation op) {
        print(op);
        visit(op.getExpression());
    }

    @Override
    public void visit(SQLite3ExpressionCast cast) {
        print(cast);
        visit(cast.getExpression());
    }

    @Override
    public void visit(SQLite3TypeLiteral literal) {
    }

    @Override
    public void visit(SQLite3InOperation op) {
        print(op);
        visit(op.getLeft());
        if (op.getRightExpressionList() != null) {
            for (SQLite3Expression expr : op.getRightExpressionList()) {
                visit(expr);
            }
        } else {
            visit(op.getRightSelect());
        }
    }

    @Override
    public void visit(SQLite3Subquery query) {
        print(query);
        if (query.getExpectedValue() != null) {
            visit(query.getExpectedValue());
        }
    }

    @Override
    public void visit(SQLite3Exist exist) {
        print(exist);
        visit(exist.getExpression());
    }

    @Override
    public void visit(SQLite3Join join) {
        print(join);
        visit(join.getOnClause());
    }

    @Override
    public void visit(SQLite3BinaryComparisonOperation op) {
        print(op);
        visit(op.getLeft());
        visit(op.getRight());
    }

    public String get() {
        return sb.toString();
    }

    @Override
    public void visit(SQLite3Function func) {
        print(func);
        for (SQLite3Expression expr : func.getArgs()) {
            visit(expr);
        }
    }

    @Override
    public void visit(SQLite3Distinct distinct) {
        print(distinct);
        visit(distinct.getExpression());
    }

    @Override
    public void visit(SQLite3CaseWithoutBaseExpression caseExpr) {
        for (CasePair cExpr : caseExpr.getPairs()) {
            print(cExpr.getCond());
            visit(cExpr.getCond());
            print(cExpr.getThen());
            visit(cExpr.getThen());
        }
        if (caseExpr.getElseExpr() != null) {
            print(caseExpr.getElseExpr());
            visit(caseExpr.getElseExpr());
        }
    }

    @Override
    public void visit(SQLite3CaseWithBaseExpression caseExpr) {
        print(caseExpr);
        visit(caseExpr.getBaseExpr());
        for (CasePair cExpr : caseExpr.getPairs()) {
            print(cExpr.getCond());
            visit(cExpr.getCond());
            print(cExpr.getThen());
            visit(cExpr.getThen());
        }
        if (caseExpr.getElseExpr() != null) {
            print(caseExpr.getElseExpr());
            visit(caseExpr.getElseExpr());
        }
    }

    @Override
    public void visit(SQLite3Aggregate aggr) {
        print(aggr);
        visit(aggr.getExpectedValue());
    }

    @Override
    public void visit(SQLite3PostfixText op) {
        print(op);
        if (op.getExpression() != null) {
            visit(op.getExpression());
        }
    }

    @Override
    public void visit(SQLite3WindowFunction func) {
        print(func);
        for (SQLite3Expression expr : func.getArgs()) {
            visit(expr);
        }
    }

    @Override
    public void visit(SQLite3MatchOperation match) {
        print(match);
        visit(match.getLeft());
        visit(match.getRight());
    }

    @Override
    public void visit(SQLite3RowValueExpression rw) {
        print(rw);
        for (SQLite3Expression expr : rw.getExpressions()) {
            visit(expr);
        }
    }

    @Override
    public void visit(SQLite3Text func) {
        print(func);
    }

    @Override
    public void visit(SQLite3WindowFunctionExpression windowFunction) {

    }

    @Override
    public void visit(SQLite3WindowFunctionFrameSpecTerm term) {

    }

    @Override
    public void visit(SQLite3WindowFunctionFrameSpecBetween between) {

    }

    @Override
    public void visit(SQLite3TableReference tableReference) {

    }

    @Override
    public void visit(SQLite3SetClause set) {
        print(set);
        visit(set.getLeft());
        visit(set.getRight());
    }

}
