package com.sap.hana.tpch.remoute.hana;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.db.DBExecutor;
import com.sap.hana.tpch.remoute.BaseEnvPreparation;
import com.sap.hana.tpch.types.ScaleFactor;

import java.sql.SQLException;

/**
 * Created by Alex on 28/09/2014.
 * Base preparations methods for importing from binary and from dbgen, in case of hana.
 */
abstract public class HDBEnvPreparation extends BaseEnvPreparation {

    /**
     * Set current working schema.
     * @param executor executor to make server request.
     * @throws java.sql.SQLException
     */
    protected static void setWorkingSchema(DBExecutor executor) throws SQLException {
        executor.executeNoResultQuery(String.format("set schema %s", Configurations.SCHEMA_NAME));
    }
}
