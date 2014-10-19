package com.sap.hana.tpch.remoute.hana;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.db.DBExecutor;
import com.sap.hana.tpch.db.DBInstance;
import com.sap.hana.tpch.db.DBType;
import com.sap.hana.tpch.exception.PrepException;
import com.sap.hana.tpch.exec.RemoteExecutor;
import com.sap.hana.tpch.remoute.BaseDBGenPreparation;
import com.sap.hana.tpch.remoute.EnvPreparationMonitor;
import com.sap.hana.tpch.tables.BaseTable;
import com.sap.hana.tpch.tables.TablesScriptGenerator;
import com.sap.hana.tpch.tables.base.BTDel1;
import com.sap.hana.tpch.types.ScaleFactor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Alex on 08/10/2014.
 * Make tables database preparations using generated data by dbgen.
 * First check if user exist. if user exist drop it. then create new user.
 * With creating new user new schema also creating.
 * Second create necessary for test tables and import data to this tables.
 */
public class HDBDBGenPreparation extends HDBEnvPreparation{
    private ScaleFactor scaleFactor;

    /**
     * Initialize with scale factor.
     * @param scaleFactor scale factor.
     */
    public HDBDBGenPreparation(ScaleFactor scaleFactor){
        this.scaleFactor = scaleFactor;
    }

    public static void prepareDBFromDBGen(ScaleFactor scaleFactor, EnvPreparationMonitor preparationMonitor) throws PrepException{
        HDBDBGenPreparation preparation = new HDBDBGenPreparation(scaleFactor);
        preparation.prepare(preparationMonitor);
    }

    @Override
    protected void prepareDB(EnvPreparationMonitor preparationMonitor) throws PrepException{
        TablesScriptGenerator tsGen = new TablesScriptGenerator(scaleFactor,DBType.HDB);
        try {
            DBExecutor executor = DBInstance.getDBInstance(DBType.HDB);
            preparationMonitor.init("Execution start");
            preparationMonitor.process("Creating user and shcema...");
            createUserSchema(executor);
            setWorkingSchema(executor);
            preparationMonitor.process("Creating necessary tables...");
            BaseDBGenPreparation.createTables(executor, tsGen, preparationMonitor);
            preparationMonitor.process("Importing data to tables...");
            BaseDBGenPreparation.importFromDBGen(executor, tsGen, preparationMonitor);
            preparationMonitor.end("Execution end");
        } catch (SQLException e) {
            throw new PrepException("Error occurs while preparing Hana database environment.",e);
        }
    }

        private static void createUserSchema(DBExecutor executor) throws PrepException{
        try {
            dropUser(executor);
            executor.executeNoResultQuery(String.format("CREATE USER %s PASSWORD %s", Configurations.TPCH_USER, Configurations.TPCH_PWD.substring(0, 8)));
            executor.executeNoResultQuery(String.format("GRANT IMPORT to %s", Configurations.TPCH_USER));
            RemoteExecutor.getRemoteCommandExecutor().execCommand(String.format(Configurations.SERVER_TEST_DIR + "/alter_user.sh %s %s", Configurations.TPCH_USER, Configurations.TPCH_PWD));
        }
        catch (Exception e){
            throw new PrepException("Can't create user or schema",e);
        }
    }

    private static void dropUser(DBExecutor executor) throws SQLException{
        if(isUserExist(executor))
            executor.executeNoResultQuery(String.format("drop user %s cascade",Configurations.TPCH_USER));
    }

    private static boolean isUserExist(DBExecutor executor) throws SQLException{
        ResultSet s = executor.executeQuery(String.format("select count(*) from \"SYS\".\"P_USERS_\" where UPPER(NAME) like '%s'",Configurations.TPCH_USER));
        s.next();
        return s.getInt(1) > 0;
    }
}
