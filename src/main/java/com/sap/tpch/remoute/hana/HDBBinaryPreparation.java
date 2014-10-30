package com.sap.tpch.remoute.hana;

import com.sap.tpch.benchmark.BenchmarkScaleFactor;
import com.sap.tpch.checks.Checks;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.db_interaction.DBExecutor;
import com.sap.tpch.db_interaction.DBInstance;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.exception.SFException;
import com.sap.tpch.remoute.DatabaseState;
import com.sap.tpch.remoute.PreparationMonitor;
import com.sap.tpch.tables.BaseTable;
import com.sap.tpch.tables.DBService;
import com.sap.tpch.tables.ITableGenerator;
import com.sap.tpch.tables.TablesScriptGenerator;
import com.sap.tpch.tables.hana.HNCreatingScriptsService;
import com.sap.tpch.tables.hana.HNRefreshIndex;
import com.sap.tpch.tables.hana.HNTable;
import com.sap.tpch.types.ScaleFactor;

import java.sql.SQLException;

/**
 * Created by Alex on 08/10/2014.
 * Make tables database preparations using exported previously binary data.
 */
public class HDBBinaryPreparation extends HDBEnvPreparation{

    /**
     * Path with binary data.
     */
    String filePath;

    /**
     * Initialising with path with file.
     * @param binaryFilePath path contain binary instance.
     */
    public HDBBinaryPreparation(String binaryFilePath){
        filePath = binaryFilePath;
    }

    /**
     * Prepare database using binary file.
     * @param binaryFilePath path to binary file.
     * @param preparationMonitor status monitor.
     * @throws PrepException
     */
    public static void prepareDBFromBinary(String binaryFilePath, PreparationMonitor preparationMonitor) throws PrepException{
        HDBBinaryPreparation preparation = new HDBBinaryPreparation(binaryFilePath);
        preparation.prepare(preparationMonitor);
    }

    /**
     * Import data from binary tpch template.
     * @param preparationMonitor using for monitor execution process.
     * @throws PrepException
     */
    @Override
    protected void prepareDB(PreparationMonitor preparationMonitor) throws PrepException{
        if(!isImportingPathExist(filePath))
            throw new PrepException("Binary directory "+filePath+"not exist");
        try {
            DBExecutor executor = DBInstance.getDBInstance();
            createUserSchema(executor);
            importBinaryData(executor,filePath, preparationMonitor);
            BenchmarkScaleFactor.getScaleFactor().updateScaleFactorFromDatabase();
            checkRefreshIndexTable(executor, preparationMonitor);
        } catch (SQLException | SFException e) {
            DatabaseState.resetState();
            throw new PrepException("Error occurs while importing data",e);
        }
    }

    /**
     * Some importing files may not contain refreshIndexTable.
     * Method check this and if necessary create table.
     * @param executor executor
     * @param preparationMonitor monitor.
     * @throws PrepException
     */
    private void checkRefreshIndexTable(DBExecutor executor, PreparationMonitor preparationMonitor) throws PrepException{
        try {
            ITableGenerator tableGenerator = TablesScriptGenerator.getTable(DBService.getTableName(HNRefreshIndex.class));
            if(!tableGenerator.isTableExist(executor)) {
                tableGenerator.createTable(executor, preparationMonitor);
                tableGenerator.importData(executor, preparationMonitor);
            }
        }catch (PrepException e){
            throw new PrepException("can't check refreshIndex table status",e);
        }
    }

    /**
     * Is path to export file exist.
     * @param binaryPath checking path.
     * @return @true if checking path exist @false another way.
     */
    private static boolean isImportingPathExist(String binaryPath) {
        return Checks.isDirectoryExist(binaryPath);
    }

    private static void importBinaryData(DBExecutor executor, String filePath, PreparationMonitor preparationMonitor) throws PrepException{
        String importRequest = "IMPORT \""+ TPCHConfig.SCHEMA_NAME+"\".\"*\" as BINARY FROM '%s' WITH REPLACE";
        try {
            executor.executeNoResultQuery(String.format(importRequest, filePath));
        }catch (SQLException e){
            throw new PrepException("Can't import data to database", e);
        }
        loadDataToMemory(executor, preparationMonitor);
    }

    private static void loadDataToMemory(DBExecutor executor, PreparationMonitor preparationMonitor) throws PrepException{
        TablesScriptGenerator.loadAllTablesIntoMemory(executor, preparationMonitor);
    }
}
