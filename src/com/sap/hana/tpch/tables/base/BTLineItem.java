package com.sap.hana.tpch.tables.base;

import com.sap.hana.tpch.tables.BaseTable;

/**
 * Created by Alex on 22/09/2014.
 * Constance LineItem table facts base for all database
 */
public class BTLineItem<O extends BaseTable,P extends BaseTable> implements BaseTableInfo{

    /**
     * Database realization of order table
     */
    O ordersTable;
    /**
     * Database realization of partSupp table.
     */
    P partSuppTable;

    /**
     * Constructor.
     * @param ordersTable Database realization of order table, from which LineItem depend.
     * @param partSuppTable Database realization of partSupp table, from which LineItem depend.
     */
    public BTLineItem(O ordersTable, P partSuppTable){
        this.ordersTable = ordersTable;
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
     * Get order table.
     * @return realization order table from which LineItem depend.
     */
    public O getOrderTable(){
        return ordersTable;
    }

    /**
     * get partSupp table.
     * @return realization partSupp table from which LineItem depend.
     */
    public P getPartSuppTable(){
        return partSuppTable;
    }
}
