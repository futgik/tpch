package com.sap.tpch.remoute.hana;

import com.sap.tpch.benchmark.BenchmarkScaleFactor;
import com.sap.tpch.db_interaction.DBExecutor;
import com.sap.tpch.db_interaction.DBInstance;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.exception.SFException;
import com.sap.tpch.remoute.DatabaseState;
import com.sap.tpch.remoute.PreparationMonitor;
import com.sap.tpch.tables.TablesScriptGenerator;
import com.sap.tpch.types.ScaleFactor;

import java.sql.SQLException;

/**
 * Created by Alex on 08/10/2014.
 * Make tables database preparations using generated data by dbgen.
 * First check if user exist. if user exist drop it. then create new user.
 * With creating new user new schema also creating.
 * Second create necessary for test tables and import data to this tables.
 */
public class HDBDBGenPreparation extends HDBEnvPreparation{

    /**
     * Initialize with scale factor.
     */
    public HDBDBGenPreparation(){
    }

    public static void prepareDBFromDBGen(PreparationMonitor preparationMonitor) throws PrepException{
        HDBDBGenPreparation preparation = new HDBDBGenPreparation();
        preparation.prepare(preparationMonitor);
    }

    @Override
    protected void prepareDB(PreparationMonitor preparationMonitor) throws PrepException{
        try {
            BenchmarkScaleFactor.getScaleFactor().updateScaleFactorFromFile();
            DBExecutor executor = DBInstance.getDBInstance();
            preparationMonitor.init("Execution start");
            preparationMonitor.process("Creating user and shcema...");
            createUserSchema(executor);
            setWorkingSchema(executor);
            TablesScriptGenerator.createAllTables(executor, preparationMonitor);
            TablesScriptGenerator.importDataFromDBGen(executor,preparationMonitor);
            preparationMonitor.end("Execution end");
        } catch (SQLException | SFException e) {
            DatabaseState.resetState();
            throw new PrepException("Error occurs while preparing Hana database environment.",e);
        }
    }
}
