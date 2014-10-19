package com.sap.hana.tpch.remoute.hana;

import com.sap.hana.tpch.db.DBExecutor;
import com.sap.hana.tpch.db.DBInstance;
import com.sap.hana.tpch.db.DBType;
import com.sap.hana.tpch.exception.PrepException;
import com.sap.hana.tpch.remoute.BaseEnvPreparation;
import com.sap.hana.tpch.remoute.EnvPreparationMonitor;
import com.sap.hana.tpch.tables.TablesScriptGenerator;
import com.sap.hana.tpch.types.ScaleFactor;

import java.sql.SQLException;

/**
 * Created by Alex on 19/10/2014.
 * Refresh hana database after refresh functions modification.
 * Functions modify two tables: table LINEITEM and table ORDERS.
 * That's why we need to update this two tables.
 */
public class HDBRefresh extends BaseEnvPreparation {
    ScaleFactor scaleFactor;

    public HDBRefresh(ScaleFactor scaleFactor){
        this.scaleFactor = scaleFactor;
    }

    public static void refresh(ScaleFactor scaleFactor, EnvPreparationMonitor preparationMonitor) throws PrepException{
        HDBRefresh refresh = new HDBRefresh(scaleFactor);
        refresh.prepare(preparationMonitor);
    }

    @Override
    protected void prepareDB(EnvPreparationMonitor preparationMonitor) throws PrepException {
        TablesScriptGenerator tsGen = new TablesScriptGenerator(scaleFactor, DBType.HDB);
        try {
            DBExecutor executor = DBInstance.getDBInstance(DBType.HDB);
            preparationMonitor.init("Start refresh database procedure.");
            preparationMonitor.process("Drop table LINEITEM");
            dropTable("LINEITEM", tsGen, executor);
            preparationMonitor.process("Drop table ORDERS");
            dropTable("ORDERS",tsGen,executor);
            preparationMonitor.process("Create table ORDERS");
            createTable("ORDERS",tsGen,executor);
            preparationMonitor.process("Create table LINEITEM");
            createTable("LINEITEM",tsGen, executor);
            preparationMonitor.process("Importing data to tables ORDERS and LINEITEM");
            importData("ORDERS", tsGen, executor);
            importData("LINEITEM", tsGen, executor);
            preparationMonitor.end("Refresh end successful");
        } catch (SQLException e) {
            throw new PrepException("Error occurs while preparing Hana database environment.",e);
        }
    }

    private void importData(String tableName, TablesScriptGenerator tsGen, DBExecutor executor) throws PrepException{
        try{
            executor.executeNoResultQuery(tsGen.getTable(tableName).getUploadingScript());
        }
        catch (Exception e){
            throw new PrepException(String.format("Can't import data to table %s",tableName),e);
        }
    }

    private static void dropTable(String tableName, TablesScriptGenerator tsGen,DBExecutor executor) throws PrepException{
        try {
            executor.executeNoResultQuery(tsGen.getTable(tableName).getDeleteScript());
        }
        catch (Exception e){
            throw new PrepException(String.format("Can't drop table %s",tableName),e);
        }
    }

    private static void createTable(String tableName, TablesScriptGenerator tsGen, DBExecutor executor) throws PrepException{
        try{
            executor.executeNoResultQuery(tsGen.getTable(tableName).getCreateScript());
        }
        catch (Exception e){
            throw new PrepException(String.format("Can't create table %s",tableName),e);
        }
    }

}
