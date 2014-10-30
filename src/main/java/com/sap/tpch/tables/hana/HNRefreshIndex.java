package com.sap.tpch.tables.hana;

import com.sap.tpch.benchmark.BenchmarkScaleFactor;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.db_interaction.DBExecutor;
import com.sap.tpch.db_interaction.DBInstance;
import com.sap.tpch.remoute.DatabaseState;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.remoute.PreparationMonitor;
import com.sap.tpch.tables.DBService;
import com.sap.tpch.types.ScaleFactor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Alex on 21/10/2014.
 * RefreshIndex table
 * RefreshIndex table contain actual index to provide refresh functions.
 */
public class HNRefreshIndex extends HNTable{

    /**
     * get available value that can be used for refresh function.
     * @return available index that can be used for refresh function.
     */
    public static int getAvailableMaxValue(){
        return 999;
    }

    static final String SQL_SELECT_INDEX = "SELECT MAX(%s) AS MAX_INDEX FROM %s.%s";

    private static int currentIndex=-1;

    public HNRefreshIndex() {
        super(BenchmarkScaleFactor.getScaleFactor());
    }

    /**
     * Get from database actual refresh index.
     * If getting index less then maximum available then set ready to test database status.
     * Else don't change status.
     * @throws PrepException
     */
    public static void updateRefreshIndex() throws PrepException{
        if(DatabaseState.isTablesDataExist()) {
            try {
                currentIndex = getCurrentRefreshPart();
            }
            catch (SQLException e){
                DatabaseState.resetState();
                throw new PrepException("can't get current refresh index from database",e);
            }
            if (currentIndex <= getAvailableMaxValue())
                DatabaseState.setReadyState();
            else DatabaseState.setNotReadyState();
        }
    }

    /**
     * Get current refresh index from database.
     * current index is maximum index in table.
     * @return current refresh index from database.
     */
    public static int getCurrentRefreshIndex(){
        if(currentIndex < 1){
            try{
                updateRefreshIndex();
            }catch (PrepException e){
                return -1;
            }
        }
        return currentIndex;
    }

    /**
     * Increment current refresh index using step value.
     * Incrementation results writes to database.
     * @param step value of incrementing.
     * @return new current refresh index value
     * @throws SQLException
     */
    public static int incrementCurrentIndex(int step) throws SQLException{
        currentIndex+=step;
        try {
            saveRefreshPart();
        }catch (SQLException e){
            currentIndex-=step;
            throw e;
        }
        return currentIndex;
    }

    /**
     * Update RefreshIndex table.
     * @param executor script executor.
     * @param preparationMonitor state monitor.
     * @throws PrepException
     */
    public static void prepareTable(DBExecutor executor, PreparationMonitor preparationMonitor) throws PrepException {
        if(DatabaseState.isDatabaseExist()) {
            HNRefreshIndex refreshIndex = new HNRefreshIndex();
            refreshIndex.dropTable(executor, preparationMonitor);
            refreshIndex.createTable(executor, preparationMonitor);
            refreshIndex.importData(executor, preparationMonitor);
            updateRefreshIndex();
        }
    }

    private static void saveRefreshPart() throws SQLException{
        try {
            DBExecutor executor = DBInstance.getDBInstance();
            executor.executeNoResultQuery(getInsertScript(currentIndex));
        }catch (SQLException e){
            DatabaseState.setNotReadyState();
            throw e;
        }
    }

    @Override
    public long getMinAllowableRowsCount(){
        return getBaseRowsNumber();
    }

    private static int getCurrentRefreshPart() throws SQLException{
        DBExecutor executor = DBInstance.getDBInstance();
        ResultSet rs = executor.executeQuery(String.format(SQL_SELECT_INDEX, "R_INDEX", TPCHConfig.SCHEMA_NAME, DBService.getTableName(HNRefreshIndex.class)));
        rs.next();
        return rs.getInt("MAX_INDEX");
    }

    @Override
    String getBaseCreationScript() {
        return String.format("CREATE COLUMN TABLE %s(R_INDEX INTEGER)", getTableName());
    }

    @Override
    public String getUploadingScript(){
        return getInsertScript(1);
    }

    private static String getInsertScript(int value){
        return String.format("Insert into %s.%s values(%d)", TPCHConfig.SCHEMA_NAME, DBService.getTableName(HNRefreshIndex.class),value);
    }

    @Override
    String partitionFieldName() {
        return "R_INDEX";
    }

    @Override
    public int getBaseRowsNumber() {
        return 1;
    }

    public String getCreateScript() {
        return  getBaseCreationScript();
    }
}
