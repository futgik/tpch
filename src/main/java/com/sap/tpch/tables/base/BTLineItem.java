package com.sap.tpch.tables.base;

import com.sap.tpch.tables.BaseTable;

/**
 * Created by Alex on 22/09/2014.
 * Constance LineItem table facts base for all database
 */
public class BTLineItem<P extends BaseTable> implements BaseTableInfo{

    /**
     * Database realization of partSupp table.
     */
    P partSuppTable;

    /**
     * Constructor.
     * @param partSuppTable Database realization of partSupp table, from which LineItem depend.
     */
    public BTLineItem(P partSuppTable){
        this.partSuppTable = partSuppTable;
    }

    @Override
    public int getBaseRowsNumber() {
        return 6000000;
    }

    @Override
    public String getPartitionFieldName() {
        return "l_orderkey";
    }

    /**
     * get partSupp table.
     * @return realization partSupp table from which LineItem depend.
     */
    public P getPartSuppTable(){
        return partSuppTable;
    }
}
