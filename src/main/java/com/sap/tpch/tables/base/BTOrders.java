package com.sap.tpch.tables.base;

import com.sap.tpch.tables.BaseTable;

/**
 * Created by Alex on 22/09/2014.
 * Constance Orders table facts base for all database
 */
public class BTOrders<L extends BaseTable, C extends BaseTable> implements BaseTableInfo{

    /**
     * Database specific realization of lineItemTable table.
     */
    L lineItemTable;

    /**
     * Database specific realization of customer table.
     */
    C customerTable;

    /**
     * Constructor.
     * @param customerTable database realization of customer table from which order table depend.
     */
    public BTOrders(L lineItemTable, C customerTable){
        this.lineItemTable = lineItemTable;
        this.customerTable = customerTable;
    }

    @Override
    public int getBaseRowsNumber() {
        return 1500000;
    }

    @Override
    public String getPartitionFieldName() {
        return "o_orderkey";
    }

    /**
     * Get realization of lineItem table.
     * @return realization of lineItem table.
     */
    public L getLineItemTable(){
        return lineItemTable;
    }

    /**
     * Get realization of customer table.
     * @return realization of customer table from which order table depend.
     */
    public C getCustomerTable(){
        return customerTable;
    }
}
