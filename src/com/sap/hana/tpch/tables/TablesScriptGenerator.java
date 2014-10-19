package com.sap.hana.tpch.tables;

import com.sap.hana.tpch.db.DBType;
import com.sap.hana.tpch.tables.hana.*;
import com.sap.hana.tpch.tables.mysql.MySQLTablesCreator;
import com.sap.hana.tpch.types.ScaleFactor;

import java.util.*;

/**
 * Created by Alex on 23.09.2014.
 * Contain table set.
 */
public class TablesScriptGenerator implements Iterable<ITableGenerator>{

    private Map<String,ITableGenerator> tables;
    private ScaleFactor scaleFactor;

    /**
     * Initialise table set.
     * @param scaleFactor scale factor setting for tables.
     */
    public TablesScriptGenerator(ScaleFactor scaleFactor, DBType dbType){
        switch (dbType){
            case HDB:
                tables = HTablesCreator.createAllTables(scaleFactor);
                break;
            case MYSQL:
                tables = MySQLTablesCreator.createAllTables(scaleFactor);
                break;
        }
        this.scaleFactor = new ScaleFactor(scaleFactor);
    }

    public void removeFromGen(String tableName){
        tables.remove(tableName);
    }

    /**
     * Get scale factor set for tables.
     * @return scale factor.
     */
    public ScaleFactor getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Get TTable object of specified table name.
     * @param tableName Name of table.
     * @return TTable object.
     */
    public ITableGenerator getTable(String tableName){
        return tables.get(tableName);
    }

    /**
     * Get Iterable over all table names
     * @return Iterable over all table names.
     */
    public Iterable<String> getTablesName(){
        return tables.keySet();
    }

    /**
     * Get Iterable over all tables.
     * @return Iterable over all tables.
     */
    private Iterable<ITableGenerator> getAllTables(){
        return tables.values();
    }

    /**
     * Iterator over creating tables.
     * @return Iterator.
     */
    @Override
    public Iterator<ITableGenerator> iterator() {
        return getAllTables().iterator();
    }

    public static void main(String[] arg){
    }
}
