package com.sap.tpch.remoute;

import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.db_interaction.DBExecutor;
import com.sap.tpch.db_interaction.DBInstance;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.tables.TablesScriptGenerator;
import com.sap.tpch.tables.hana.HNRefreshIndex;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Alex on 07/10/2014.
 * Describe current condition of database.
 * There are list of conditions described in class DatabaseStateType.
 */
public class DatabaseState {

    static DatabaseStateType databaseState = DatabaseStateType.UNKNOWN;

    /**
     * Get database current state.
     * @return database current state.
     */
    public static DatabaseStateType getCurrentState(){return databaseState;}

    /**
     * Set database state.
     * @param state database state.
     */
    public  static void setDatabaseState(DatabaseStateType state){
        databaseState = state;
    }

    /**
     * Sate state that tell that table exist and tables exist.
     */
    public static void setExistState(){databaseState = DatabaseStateType.EXIST;}

    /**
     * set state that tell that table exist and tables exist and data in table exists.
     * But they may be not relevant.
     */
    public static void setNotReadyState(){databaseState = DatabaseStateType.NOT_READY;}

    /**
     * Set ready state. That tells that all data exist and they are relevant to run test.
     */
    public static void setReadyState(){
        databaseState = DatabaseStateType.READY;
    }

    /**
     * Set state that tell that database not exist or table not exist.
     */
    public static void resetState(){
        databaseState = DatabaseStateType.NOT_EXIST;
    }

    /**
     * return is database and tables exists.
     * @return @true - if database and tables exists, @false another case.
     */
    public static boolean isDatabaseExist(){
        return databaseState == DatabaseStateType.EXIST || databaseState == DatabaseStateType.NOT_READY || databaseState == DatabaseStateType.READY;
    }

    /**
     * return is table data exist.
     * @return @true - if data in database exist, @false another case.
     */
    public static boolean isTablesDataExist(){
        return databaseState == DatabaseStateType.READY || databaseState == DatabaseStateType.NOT_READY;
    }

    /**
     * return is table in relevant state to run tests.
     * @return @true - if table in relevant state, @false - another case.
     */
    public static boolean isDatabaseReady(){
        return databaseState == DatabaseStateType.READY;
    }

    /**
     * Define database state.
     * This function important when program run to define current database state.
     */
    public static void updateState(){
        try {
            DBExecutor executor = DBInstance.getDBInstance();
            if (isSchemaExist(executor) && TablesScriptGenerator.isAllTablesExist(executor)) setExistState();
            if(TablesScriptGenerator.isDataInTablesExist(executor)) setNotReadyState();
            HNRefreshIndex.updateRefreshIndex();
        }catch (SQLException | PrepException e){
            databaseState = DatabaseStateType.UNKNOWN;
        }
    }

    private static boolean isSchemaExist(DBExecutor executor) throws SQLException{
        String checkScript = String.format("select count(*) from \"PUBLIC\".\"M_TABLES\" where SCHEMA_NAME='%s'", TPCHConfig.SCHEMA_NAME);
        ResultSet rs = executor.executeQuery(checkScript);
        rs.next();
        return rs.getInt(1) > 0;
    }

}
