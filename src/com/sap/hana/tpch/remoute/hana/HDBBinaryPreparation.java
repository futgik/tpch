package com.sap.hana.tpch.remoute.hana;

import com.sap.hana.tpch.checks.Checks;
import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.db.DBExecutor;
import com.sap.hana.tpch.db.DBInstance;
import com.sap.hana.tpch.db.DBType;
import com.sap.hana.tpch.exception.PrepException;
import com.sap.hana.tpch.tables.hana.HNCreatingScriptsService;
import com.sap.hana.tpch.remoute.EnvPreparationMonitor;

import java.sql.SQLException;

/**
 * Created by Alex on 08/10/2014.
 * Make tables database preparations using exported previously binary data.
 */
public class HDBBinaryPreparation extends HDBEnvPreparation{
    String filePath;

    public HDBBinaryPreparation(String binaryFilePath){
        filePath = binaryFilePath;
    }

    public static void prepareDBFromBinary(String binaryFilePath,EnvPreparationMonitor preparationMonitor) throws PrepException{
        HDBBinaryPreparation preparation = new HDBBinaryPreparation(binaryFilePath);
        preparation.prepare(preparationMonitor);
    }

    //todo. check this.
    @Override
    protected void prepareDB(EnvPreparationMonitor preparationMonitor) throws PrepException{
        if(!isImportingFileExist(filePath))
            throw new PrepException("file "+filePath+"not exist");
        try {
            DBExecutor executor = DBInstance.getDBInstance(DBType.HDB);
            dropSchema(executor);
            importBinaryData(executor,filePath);
        } catch (SQLException e) {
            throw new PrepException("Error occurs while importing data",e);
        }
    }

    private static boolean isImportingFileExist(String binaryPath){
        return Checks.isFileExist(binaryPath);
    }

    private static void dropSchema(DBExecutor executor) throws SQLException{
        if(isSchemaExist(executor))
            executor.executeNoResultQuery(String.format("drop schema %s cascade", Configurations.SCHEMA_NAME));
    }

    private static boolean isSchemaExist(DBExecutor executor) throws SQLException{
        try{
            setWorkingSchema(executor);
            return true;
        }
        catch (SQLException e) {
            if(e.getErrorCode()==362) return false;
            throw e;
        }
    }

    private static void importBinaryData(DBExecutor executor, String filePath) throws SQLException{
        String importRequest = "IMPORT \""+Configurations.SCHEMA_NAME+"\".\"*\" as BINARY FROM '%s' WITH REPLACE THREADS "
                + HNCreatingScriptsService.getThreadsCount()+" BATCH "+HNCreatingScriptsService.getBatchCount();
        executor.executeNoResultQuery(String.format(importRequest, filePath));
    }
}
