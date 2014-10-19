package com.sap.hana.tpch.remoute.mysql;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.db.DBExecutor;
import com.sap.hana.tpch.db.DBInstance;
import com.sap.hana.tpch.db.DBType;
import com.sap.hana.tpch.exception.PrepException;
import com.sap.hana.tpch.remoute.BaseDBGenPreparation;
import com.sap.hana.tpch.remoute.EnvPreparationMonitor;
import com.sap.hana.tpch.tables.TablesScriptGenerator;
import com.sap.hana.tpch.types.ScaleFactor;
import java.sql.SQLException;

/**
 * Created by Alex on 11.10.2014.
 * Make tables database preparations using generated data by dbgen.
 * First delete old schema if exist and create new one.
 * Second create necessary for test tables and import data to this tables.
 *
 * If some problems with importing scripts with message File '...' not found.
 * Than you must make follow things:
 * sudo aa-complain /usr/sbin/mysqld
 * out: Setting /usr/sbin/mysqld to complain mode.
 * sudo /etc/init.d/apparmor reload
 */
public class MySQLGenPreparation extends MySQLEnvPreparation{
    private ScaleFactor scaleFactor;

    /**
     * Initialize with scale factor.
     * @param scaleFactor scale factor.
     */
    public MySQLGenPreparation(ScaleFactor scaleFactor){
        this.scaleFactor = scaleFactor;
    }

    /**
     * the same as prepare method.
     * @param scaleFactor scale factor.
     * @param preparationMonitor process monitor.
     * @throws PrepException
     */
    public static void prepareDBFromDBGen(ScaleFactor scaleFactor, EnvPreparationMonitor preparationMonitor) throws PrepException{
        MySQLEnvPreparation preparation = new MySQLGenPreparation(scaleFactor);
        preparation.prepare(preparationMonitor);
    }

    @Override
    protected void prepareDB(EnvPreparationMonitor preparationMonitor) throws PrepException {
        TablesScriptGenerator tsGen = new TablesScriptGenerator(scaleFactor, DBType.MYSQL);
        try {
            DBExecutor executor = DBInstance.getDBInstance(DBType.MYSQL);
            preparationMonitor.init("Execution start");
            preparationMonitor.process("Creating shcema...");
            createSchema(executor);
            setWorkingSchema(executor);
            preparationMonitor.process("Creating necessary tables...");
            BaseDBGenPreparation.createTables(executor, tsGen, preparationMonitor);
            preparationMonitor.process("Importing data to tables...");
            BaseDBGenPreparation.importFromDBGen(executor, tsGen, preparationMonitor);
            preparationMonitor.end("Execution end");
        } catch (SQLException e) {
            throw new PrepException("Error occurs while preparing MySql database environment.",e);
        }
    }

    private void createSchema(DBExecutor executor) throws PrepException{
        try {
            dropSchema(executor);
            executor.executeNoResultQuery(String.format("create schema %s",Configurations.SCHEMA_NAME));
        }catch (Exception e){
            throw new PrepException("Can't create schema",e);
        }
    }

    private static void dropSchema(DBExecutor executor) throws SQLException{
        executor.executeNoResultQuery(String.format("drop schema if EXISTS %s",Configurations.SCHEMA_NAME));
    }

}
