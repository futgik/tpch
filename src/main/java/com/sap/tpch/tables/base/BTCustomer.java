package com.sap.tpch.tables.base;

/**
 * Created by Alex on 22/09/2014.
 * Constance customer table facts base for all database
 */
public class BTCustomer implements BaseTableInfo{

    public BTCustomer() {
    }

    @Override
    public int getBaseRowsNumber() {
        return 150000;
    }

    @Override
    public String getPartitionFieldName() {
        return "c_custkey";
    }
}
