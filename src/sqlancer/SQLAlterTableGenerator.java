package sqlancer;

import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.common.schema.AbstractRelationalTable;
import sqlancer.postgres.gen.PostgresAlterTableGenerator;
import sqlancer.yugabyte.ysql.gen.YSQLCommon;

import java.util.List;

public abstract class SQLAlterTableGenerator<T extends AbstractRelationalTable<?, ?, ?>, G extends ExpandedGlobalState<?, ?>, A extends Enum<A>> {

    protected final T randomTable;
    protected final G globalState;
    protected final Randomly r;

    public SQLAlterTableGenerator(T randomTable, G globalState) {
        this.randomTable = randomTable;
        this.globalState = globalState;
        this.r = globalState.getRandomly();
    }

    public abstract List<A> getActions(ExpectedErrors errors);

    public SQLQueryAdapter generate() {
        ExpectedErrors errors = new ExpectedErrors();
        int i = 0;
        List<A> action = getActions(errors);
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        if (Randomly.getBoolean()) {
            sb.append(" ONLY");
            errors.add("cannot use ONLY for foreign key on partitioned table");
        }
        sb.append(" ");
        sb.append(randomTable.getName());
        sb.append(" ");
        for (A a : action) {
            if (i++ != 0) {
                sb.append(", ");
            }
            switch(a.name()) {
                case "ALTER_TABLE_DROP_COLUMN":
                    sb.append("DROP ");
                    if (Randomly.getBoolean()) {
                        sb.append(" IF EXISTS ");
                    }
                    sb.append(randomTable.getRandomColumn().getName());
                    errors.add("because other objects depend on it");
                    if (Randomly.getBoolean()) {
                        sb.append(" ");
                        sb.append(Randomly.fromOptions("RESTRICT", "CASCADE"));
                    }
                    errors.add("does not exist");
                    errors.add("cannot drop column");
                    errors.add("cannot drop key column");
                    errors.add("cannot drop inherited column");
                    break;
                case "ADD_TABLE_CONSTRAINT":
                    sb.append("ADD ");
                    sb.append("CONSTRAINT ").append(r.getAlphabeticChar()).append(" ");
                    YSQLCommon.addTableConstraint(sb, randomTable, globalState, errors);
                    errors.add("already exists");
                    errors.add("multiple primary keys for table");
                    errors.add("could not create unique index");
                    errors.add("contains null values");
                    errors.add("cannot cast type");
                    errors.add("unsupported PRIMARY KEY constraint with partition key definition");
                    errors.add("unsupported UNIQUE constraint with partition key definition");
                    errors.add("insufficient columns in UNIQUE constraint definition");
                    errors.add("which is part of the partition key");
                    errors.add("out of range");
                    errors.add("there is no unique constraint matching given keys for referenced table");
                    errors.add("constraints on temporary tables may reference only temporary tables");
                    errors.add("constraints on unlogged tables may reference only permanent or unlogged tables");
                    errors.add("constraints on permanent tables may reference only permanent tables");
                    errors.add("cannot reference partitioned table");
                    errors.add("cannot be implemented");
                    errors.add("violates foreign key constraint");
                    errors.add("unsupported ON COMMIT and foreign key combination");
                    errors.add("USING INDEX is not supported on partitioned tables");
                    if (Randomly.getBoolean()) {
                        sb.append(" NOT VALID");
                        errors.add("cannot be marked NOT VALID");
                        errors.add("cannot add NOT VALID foreign key on partitioned table");
                    } else {
                        errors.add("is violated by some row");
                    }
                    break;
                case "ADD_TABLE_CONSTRAINT_USING_INDEX":
                case "DISABLE_ROW_LEVEL_SECURITY":
                case "ENABLE_ROW_LEVEL_SECURITY":
                case "FORCE_ROW_LEVEL_SECURITY":
                case "NO_FORCE_ROW_LEVEL_SECURITY":
                default:
                    throw new AssertionError(a);
            }
        }

        return new SQLQueryAdapter(sb.toString(), errors, true);
    }


}
