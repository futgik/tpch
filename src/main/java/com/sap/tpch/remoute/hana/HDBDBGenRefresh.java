package com.sap.tpch.remoute.hana;

import com.sap.tpch.db_interaction.DBExecutor;
import com.sap.tpch.db_interaction.DBInstance;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.remoute.BaseEnvPreparation;
import com.sap.tpch.remoute.DatabaseState;
import com.sap.tpch.remoute.PreparationMonitor;
import com.sap.tpch.tables.DBService;
import com.sap.tpch.tables.TablesScriptGenerator;
import com.sap.tpch.tables.hana.HNLineItem;
import com.sap.tpch.tables.hana.HNOrders;
import com.sap.tpch.tables.hana.HNRefreshIndex;
import com.sap.tpch.types.ScaleFactor;

import java.sql.SQLException;

/**
 * Created by Alex on 19/10/2014.
 * Refresh hana database after refresh functions modification.
 * Functions modify two tables: table LINEITEM and table ORDERS.
 * That's why we need to update this two tables.
 */
public class HDBDBGenRefresh extends BaseEnvPreparation {
    ScaleFactor scaleFactor;

    /**
     * initialise with scale factor.
     * @param scaleFactor scale factor.
     */
    public HDBDBGenRefresh(ScaleFactor scaleFactor){
        this.scaleFactor = scaleFactor;
    }

    /**
     * Execute prepareDB method.
     * @param scaleFactor using scale factor.
     * @param preparationMonitor  using for monitor execution process.
     * @throws PrepException
     */
    public static void refresh(ScaleFactor scaleFactor, PreparationMonitor preparationMonitor) throws PrepException{
        HDBDBGenRefresh refresh = new HDBDBGenRefresh(scaleFactor);
        refresh.prepare(preparationMonitor);
    }

    /**
     * Hana dbgen preparation consist of delete two old tables LINEITEM and ORDERS,
     * creating it again and import new source data to it.
     * @param preparationMonitor using for monitor execution process.
     * @throws PrepException
     */
    @Override
    protected void prepareDB(PreparationMonitor preparationMonitor) throws PrepException {
        if(!DatabaseState.isTablesDataExist())
            throw new PrepException("Database not ready for refreshing. Try to create database again.");
        try {
            DBExecutor executor = DBInstance.getDBInstance();
            preparationMonitor.init("Start refresh database procedure.");
            TablesScriptGenerator.getTable(DBService.getTableName(HNLineItem.class)).dropTable(executor, preparationMonitor);
            TablesScriptGenerator.getTable(DBService.getTableName(HNOrders.class)).dropTable(executor,preparationMonitor);
            TablesScriptGenerator.getTable(DBService.getTableName(HNOrders.class)).createTable(executor, preparationMonitor);
            TablesScriptGenerator.getTable(DBService.getTableName(HNLineItem.class)).createTable(executor,preparationMonitor);
            TablesScriptGenerator.getTable(DBService.getTableName(HNOrders.class)).importData(executor, preparationMonitor);
            TablesScriptGenerator.getTable(DBService.getTableName(HNLineItem.class)).importData(executor,preparationMonitor);
            HNRefreshIndex.prepareTable(executor,preparationMonitor);
            preparationMonitor.end("Refresh end successful");
        } catch (SQLException e) {
            throw new PrepException("Error occurs while preparing Hana database environment.",e);
        }
    }
}
