package com.sap.hana.tpch.tables.base;

/**
 * Created by Alex on 11.10.2014.
 * Constance del table facts base for all database
 */
public class BTDel1 implements BaseTableInfo{

    public BTDel1() {
    }

    @Override
    public int getBaseRowsNumber() {
        return 0;
    }

    @Override
    public String getPartitionFieldName() {
        return "d_orderkey";
    }

    /**
     * File name which contain import cvs data.
     * @return
     */
    public String getGeneratedTableName(){
        return "delete.1";
    }

}
