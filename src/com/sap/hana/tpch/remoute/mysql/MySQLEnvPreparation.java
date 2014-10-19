package com.sap.hana.tpch.remoute.mysql;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.db.DBExecutor;
import com.sap.hana.tpch.remoute.BaseEnvPreparation;

import java.sql.SQLException;

/**
 * Created by Alex on 11.10.2014.
 * Base preparations methods for importing from binary and from dbgen, in case of mysql.
 */
abstract public class MySQLEnvPreparation extends BaseEnvPreparation{

    /**
     * Set current working schema.
     * @param executor executor to make server request.
     * @throws SQLException
     */
    protected static void setWorkingSchema(DBExecutor executor) throws SQLException{
        executor.executeNoResultQuery(String.format("use %s", Configurations.SCHEMA_NAME));
    }

}
