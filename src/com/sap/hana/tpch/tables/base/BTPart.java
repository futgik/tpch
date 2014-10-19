package com.sap.hana.tpch.tables.base;

/**
 * Created by Alex on 22/09/2014.
 * Constance Part table facts base for all database.
 */
public class BTPart implements BaseTableInfo{

    public BTPart() {
    }

    @Override
    public int getBaseRowsNumber() {
       return 200000;
    }

    @Override
    public String getPartitionFieldName() {
        return "p_partkey";
    }
}
