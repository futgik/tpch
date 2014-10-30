package com.sap.tpch.tables.base;

/**
 * Created by Alex on 22/09/2014.
 * Constance Nation table facts base for all database.
 */
public class BTNation implements BaseTableInfo{

    public BTNation() {
    }

    @Override
    public int getBaseRowsNumber() {
        return 25;
    }

    @Override
    public String getPartitionFieldName() {
        return "n_nationkey";
    }
}
