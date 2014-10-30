package com.sap.tpch.remoute.hana;

import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.db_interaction.DBExecutor;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.exec.RemoteExecutor;
import com.sap.tpch.remoute.BaseEnvPreparation;

import java.sql.ResultSet;
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
        executor.executeNoResultQuery(String.format("set schema %s", TPCHConfig.SCHEMA_NAME));
    }

    /**
     * Create new user and schema into which import results.
     * @param executor executor to make server request.
     * @throws PrepException
     */
    protected static void createUserSchema(DBExecutor executor) throws PrepException {
        try {
            dropUser(executor);
            executor.executeNoResultQuery(String.format("CREATE USER %s PASSWORD %s", TPCHConfig.TPCH_USER, TPCHConfig.TPCH_PWD.substring(0, 8)));
            executor.executeNoResultQuery(String.format("GRANT IMPORT to %s", TPCHConfig.TPCH_USER));
            RemoteExecutor.getRemoteCommandExecutor().execCommand(String.format(TPCHConfig.SERVER_TEST_DIR + "/alter_user.sh %s %s", TPCHConfig.TPCH_USER, TPCHConfig.TPCH_PWD));
        }
        catch (Exception e){
            throw new PrepException("Can't create user or schema",e);
        }
    }

    private static void dropUser(DBExecutor executor) throws SQLException{
        if(isUserExist(executor))
            executor.executeNoResultQuery(String.format("drop user %s cascade", TPCHConfig.TPCH_USER));
    }

    private static boolean isUserExist(DBExecutor executor) throws SQLException{
        ResultSet s = executor.executeQuery(String.format("select count(*) from \"SYS\".\"P_USERS_\" where UPPER(NAME) like '%s'", TPCHConfig.TPCH_USER));
        s.next();
        return s.getInt(1) > 0;
    }
}
