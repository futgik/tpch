package com.sap.hana.tpch.remoute;

import com.sap.hana.tpch.db.DBExecutor;
import com.sap.hana.tpch.tables.ITableGenerator;
import com.sap.hana.tpch.tables.TablesScriptGenerator;

import java.sql.SQLException;

/**
 * Created by Alex on 11.10.2014.
 * Contain common methods using in case of importing from dbgen.
 */
public class BaseDBGenPreparation {

    /**
     * Create tables in database.
     * Use database specific script for table creation, connect to database and execute it.
     * Using procedure for all tables.
     * @param executor database server script executor.
     * @param tsGen generator of tables script.
     * @param monitor monitor of process execution.
     * @throws SQLException
     */
    public static void createTables(DBExecutor executor, TablesScriptGenerator tsGen, EnvPreparationMonitor monitor) throws SQLException {
        for(ITableGenerator table : tsGen){
            executor.executeNoResultQuery(table.getCreateScript());
            monitor.process("Table "+table.getTableName()+" created.");
        }
    }

    /**
     * Import data to database.
     * Use database specific script for importing data from csv file to table,
     * connect to database and execute script for all tables.
     * @param executor database server script executor.
     * @param tsGen generator of tables script.
     * @param monitor monitor of process execution.
     * @throws SQLException
     */
    public static void importFromDBGen(DBExecutor executor, TablesScriptGenerator tsGen, EnvPreparationMonitor monitor) throws SQLException{
        for(ITableGenerator table : tsGen){
            executor.executeNoResultQuery(table.getUploadingScript());
            monitor.process("Import data to table "+table.getTableName()+" completed successfully");
        }
    }
}
