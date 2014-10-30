package com.sap.tpch.tables;

import com.sap.tpch.benchmark.BenchmarkProcessMonitor;
import com.sap.tpch.benchmark.BenchmarkScaleFactor;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.db_interaction.DBExecutor;
import com.sap.tpch.remoute.DatabaseState;
import com.sap.tpch.remoute.DatabaseStateType;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.remoute.PreparationMonitor;
import com.sap.tpch.tables.hana.*;
import com.sap.tpch.types.ScaleFactor;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Alex on 23.09.2014.
 * Contain table set.
 */
public class TablesScriptGenerator implements Iterable<ITableGenerator>{

    private static Map<String,ITableGenerator> tables = null;

    /**
     * Initialise table set.
    */
    static {
        BenchmarkScaleFactor.getScaleFactor().addListener(new ScaleFactor.ScaleFactorEventListener() {
            @Override
            public void scaleFactorChanged() {
                createTables();
            }
        });
    }

    private static Map<String,ITableGenerator> getTables(){
        if(tables == null) {
            createTables();
        }
        return tables;
    }

    private static void createTables(){
        switch (TPCHConfig.DATABASE_TYPE){
            case HDB:
                tables = HTablesCreator.createAllTables(BenchmarkScaleFactor.getScaleFactor());
                break;
        }
    }

    /**
     * Get TTable object of specified table name.
     * @param tableName Name of table.
     * @return TTable object.
     */
    public static ITableGenerator getTable(String tableName){
        return getTables().get(tableName);
    }

    /**
     * Get Iterable over all table names
     * @return Iterable over all table names.
     */
    public static Iterable<String> getTablesName(){
        return getTables().keySet();
    }

    /**
     * Get Iterable over all tables.
     * @return Iterable over all tables.
     */
    private static Iterable<ITableGenerator> getAllTables(){
        return getTables().values();
    }

    /**
     * Iterator over creating tables.
     * @return Iterator.
     */
    @Override
    public Iterator<ITableGenerator> iterator() {
        return getAllTables().iterator();
    }

    /**
     * Check if all tables exist in database.
     * @param executor database query executor.
     * @return @true if all tables exist, @false another case.
     */
    public static boolean isAllTablesExist(DBExecutor executor) throws PrepException {
        for (ITableGenerator table : getAllTables()) {
            if (!table.isTableExist(executor)) return false;
        }
        return true;
    }

    /**
     * Create tables in database.
     * Use database specific script for table creation, connect to database and execute it.
     * Using procedure for all tables.
     * @param executor database server script executor.
     * @param monitor monitor of process execution.
     * @throws PrepException
     */
    public static void createAllTables(DBExecutor executor, PreparationMonitor monitor) throws PrepException {
        monitor.init("Creating tables...");
        for(ITableGenerator table : getAllTables()){
            table.createTable(executor,monitor);
        }
        monitor.end("Creating tables complete");
        DatabaseState.setExistState();
    }

    public static void loadAllTablesIntoMemory(DBExecutor executor, PreparationMonitor monitor) throws PrepException{
        monitor.init("loading data into memory...");
        for(ITableGenerator table : getAllTables()){
            if(table.isTableExist(executor))
                table.loadTableDataIntoMemory(executor,monitor);
        }
        monitor.end("Loading complete");
        DatabaseState.setExistState();
    }

    /**
     * Check if all tables contains all data.
     * @param executor database query executor.
     * @return @true if all tables contain all data, @false another case.
     * @throws PrepException
     */
    public static boolean isDataInTablesExist(DBExecutor executor) throws PrepException{
        for(ITableGenerator table : getAllTables()){
            if(!table.isDataInTables(executor)) return false;
        }
        return true;
    }

    /**
     * Import data to database.
     * Use database specific script for importing data from csv file to table,
     * connect to database and execute script for all tables.
     * @param executor database server script executor.
     * @param monitor monitor of process execution.
     * @throws PrepException
     */
    public static void importDataFromDBGen(DBExecutor executor, PreparationMonitor monitor) throws PrepException{
        monitor.init("Importing data...");
        for(ITableGenerator table : getAllTables()){
            table.importData(executor,monitor);
        }
        monitor.end("Importing data complete");
        DatabaseState.setDatabaseState(DatabaseStateType.NOT_READY);
        HNRefreshIndex.updateRefreshIndex();
    }
}
