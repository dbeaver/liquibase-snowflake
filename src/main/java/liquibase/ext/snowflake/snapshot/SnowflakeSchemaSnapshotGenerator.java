package liquibase.ext.snowflake.snapshot;

import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.ext.snowflake.database.SnowflakeDatabase;
import liquibase.snapshot.jvm.SchemaSnapshotGenerator;
import liquibase.structure.DatabaseObject;
import liquibase.util.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SnowflakeSchemaSnapshotGenerator extends SchemaSnapshotGenerator {
    @Override
    public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
        if (database instanceof SnowflakeDatabase) {
        	int priority = super.getPriority(objectType, database);
            return priority += PRIORITY_DATABASE;
        }
        return PRIORITY_NONE;
    }

    @Override
    protected String[] getDatabaseSchemaNames(Database database) throws SQLException, DatabaseException {
        List<String> returnList = new ArrayList<>();

        ResultSet schemas = null;
        try {
            schemas = ((JdbcConnection) database.getConnection()).getMetaData().getSchemas(database.getDefaultCatalogName(), null);
            while (schemas.next()) {
                returnList.add(JdbcUtils.getValueForColumn(schemas, "TABLE_SCHEM", database));
            }
        } finally {
            if (schemas != null) {
                schemas.close();
            }
        }

        return returnList.toArray(new String[returnList.size()]);
    }
}
