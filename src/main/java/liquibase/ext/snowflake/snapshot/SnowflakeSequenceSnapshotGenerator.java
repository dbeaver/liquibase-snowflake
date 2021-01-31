package liquibase.ext.snowflake.snapshot;

import java.util.Locale;

import liquibase.database.Database;
import liquibase.ext.snowflake.database.SnowflakeDatabase;
import liquibase.snapshot.SnapshotGenerator;
import liquibase.snapshot.jvm.SequenceSnapshotGenerator;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Schema;

public class SnowflakeSequenceSnapshotGenerator extends SequenceSnapshotGenerator {

    @Override
    public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
        if (database instanceof SnowflakeDatabase) {
        	int priority = super.getPriority(objectType, database);
            return priority += PRIORITY_DATABASE;
        }
        return PRIORITY_NONE;
    }

    @Override
    public Class<? extends SnapshotGenerator>[] replaces() {
        return new Class[] { SequenceSnapshotGenerator.class };
    }

    @Override
    protected String getSelectSequenceSql(Schema schema, Database database) {
    	if (database instanceof SnowflakeDatabase) {
       	 return "SELECT\n" +
                    "SEQUENCE_NAME,\n" +
                    "START_VALUE,\n" +
                    "\"INCREMENT\" AS INCREMENT_BY,\n" +
                    "CYCLE_OPTION AS WILL_CYCLE\n" +
                    "FROM\n" + "\"" + schema.getCatalogName().toUpperCase(Locale.ENGLISH) + "\"." +
                    "INFORMATION_SCHEMA.\"SEQUENCES\" s\n" +
                    "WHERE\n" +
                    "s.SEQUENCE_SCHEMA ='" + schema.getName() + "'";
        }
    	
        return super.getSelectSequenceSql(schema, database);
    }

}