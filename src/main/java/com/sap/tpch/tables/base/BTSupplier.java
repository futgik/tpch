package com.sap.tpch.tables.base;

/**
 * Created by Alex on 22/09/2014.
 * Constance Supplier table facts base for all database
 */
public class BTSupplier implements BaseTableInfo{

    public BTSupplier() {
    }

    @Override
    public int getBaseRowsNumber() {
        return 10000;
    }


    @Override
    public String getPartitionFieldName() {
        return "s_suppkey";
    }
}
