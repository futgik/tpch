package com.sap.tpch.tables.hana;

import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.db_interaction.DBExecutor;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.remoute.PreparationMonitor;
import com.sap.tpch.tables.BaseTable;
import com.sap.tpch.types.ScaleFactor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Alex on 22/09/2014.
 */
public abstract class HNTable extends BaseTable {

    private static String PARTITION = " PARTITION BY HASH (%s) PARTITIONS %d";

    private static String CHECK_TABLE_EXISTENCE ="select count(*) from \"PUBLIC\".\"M_TABLES\" where SCHEMA_NAME='%s' and TABLE_NAME='%s'";

    private static String CHECK_DATA_EXISTENCE = "select count(*) from \"%s\".\"%s\"";

    private static String LOAD_TABLE_INTO_MEMORY = "LOAD %s all";

    //SQL Query pattern to import data from file.
    private static String SQLPattern = "IMPORT FROM CSV FILE '"+ TPCHConfig.SERVER_DBGEN_DIR+"/%s' INTO \""
            + TPCHConfig.SCHEMA_NAME+"\".\"%s\" WITH RECORD DELIMITED BY '\\n' FIELD DELIMITED BY '|' THREADS "
            + HNCreatingScriptsService.getThreadsCount()+" BATCH "+ HNCreatingScriptsService.getBatchCount();

    private HNTable(){
        super();
    }

    public HNTable(ScaleFactor scaleFactor){
        super(scaleFactor);
    }

    abstract String getBaseCreationScript();

    abstract String partitionFieldName();

    /**
     * Get SQL script for creation table in schema.
     * @return SQL script for creation table in schema.
     */
    protected String getCreateScript(){
        int portionCount = HNCreatingScriptsService.getTablePartitionCount(getScaleFactor(), getBaseRowsNumber());
        String partitionString = "";
        if(portionCount>1){
            partitionString = String.format(PARTITION, partitionFieldName(),portionCount);
        }
        return  String.format("CREATE COLUMN TABLE %s ", getTableName())+getBaseCreationScript()+partitionString;
    }

    /**
     * Get SQL script to delete table from schema.
     * @return script to delete table from schema.
     */
    protected String getDeleteScript(){
        return String.format("DROP TABLE %s", getTableName());
    }

    /**
     * Get SQL script to export data from file to schema.
     * @return SQL script to export data from file to schema.
     */
    protected String getUploadingScript(){
        return String.format(SQLPattern,getGeneratedTableName(),getTableName());
    }

    /**
     * Import data from dbgen data files into hana tables.
     * @param executor hana database script executor.
     * @throws com.sap.tpch.exception.PrepException
     */
    public void importData(DBExecutor executor, PreparationMonitor preparationMonitor) throws PrepException {
        try{
            preparationMonitor.process(String.format("Importing data to table %s",getTableName()));
            executor.executeNoResultQuery(getUploadingScript());
        }
        catch (Exception e){
            throw new PrepException(String.format("Can't import data to table %s",getTableName()),e);
        }
    }

    protected String getLoadIntoMemoryScript(){
        return String.format(LOAD_TABLE_INTO_MEMORY,getTableName());
    }

    /**
     * load table in memory. Execute load into memory request to hana db.
     * @param executor hana database script executor.
     * @param preparationMonitor monitor to trace process.
     * @throws PrepException
     */
    public void loadTableDataIntoMemory(DBExecutor executor, PreparationMonitor preparationMonitor) throws PrepException {
        try{
            preparationMonitor.process(String.format("Loading table %s into memory",getTableName()));
            executor.executeNoResultQuery(getLoadIntoMemoryScript());
        }
        catch (Exception e){
            throw new PrepException(String.format("Can't load table data into memory"));
        }
    }

    /**
     * Is table exist in hana database.
     * @param executor DBExecutor.
     * @return @true if table exists, @false another case.
     * @throws PrepException
     */
    @Override
    public boolean isTableExist(DBExecutor executor) throws PrepException{
        try {
            ResultSet rs = executor.executeQuery(String.format(CHECK_TABLE_EXISTENCE, TPCHConfig.SCHEMA_NAME, getTableName()));
            rs.next();
            return rs.getInt(1) > 0;
        }catch(SQLException e){
            throw new PrepException("Can't check table existence");
        }
    }

    /**
     * Check if data in table.
     * @param executor database executor.
     * @return @true if table in database, @false another case.
     * @throws PrepException
     */
    @Override
    public boolean isDataInTables(DBExecutor executor) throws PrepException{
        try {
            ResultSet rs = executor.executeQuery(String.format(CHECK_DATA_EXISTENCE, TPCHConfig.SCHEMA_NAME, getTableName()));
            rs.next();
            Long qtrs = rs.getLong(1);
            return qtrs >= getMinAllowableRowsCount();
        }catch(SQLException e){
            throw new PrepException("Can't check table status");
        }
    }

    /**
     * Get real row count that table must contain.
     * @return real row count that table must contain.
     */
    public long getMinAllowableRowsCount(){
        return getScaleFactor().getScaleFactorValue()*getBaseRowsNumber();
    }

    /**
     * Drop table from database.
     * @param executor hana database script executor.
     * @throws PrepException
     */
    public void dropTable(DBExecutor executor, PreparationMonitor preparationMonitor) throws PrepException{
        try {
            preparationMonitor.process(String.format("Drop table %s",getTableName()));
            executor.executeNoResultQuery(getDeleteScript());
        }
        catch (Exception e){
            throw new PrepException(String.format("Can't drop table %s",getTableName()),e);
        }
    }

    /**
     * Create table in hana database.
     * @param executor hana database script executor.
     * @throws PrepException
     */
    public void createTable(DBExecutor executor, PreparationMonitor preparationMonitor) throws PrepException{
        try{
            preparationMonitor.process(String.format("Create table %s",getTableName()));
            executor.executeNoResultQuery(getCreateScript());
        }
        catch (Exception e){
            throw new PrepException(String.format("Can't create table %s",getTableName()),e);
        }
    }
}
