package com.sap.hana.tpch.tables.base;

import com.sap.hana.tpch.tables.BaseTable;

/**
 * Created by Alex on 22/09/2014.
 * Constance Orders table facts base for all database
 */
public class BTOrders<C extends BaseTable> implements BaseTableInfo{

    /**
     * Database specific realization of customer table.
     */
    C customerTable;

    /**
     * Constructor.
     * @param customer database realization of customer table from which order table depend.
     */
    public BTOrders(C customer){
        customerTable = customer;
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
     * Get realization of customer table.
     * @return realization of customer table from which order table depend.
     */
    public C getCustomerTable(){
        return customerTable;
    }
}
