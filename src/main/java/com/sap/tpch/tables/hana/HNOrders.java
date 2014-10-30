package com.sap.tpch.tables.hana;

import com.sap.tpch.tables.base.BTOrders;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class HNOrders extends HNTable {

    BTOrders<HNLineItem,HNCustomer> orderDependTables;


    public HNOrders(ScaleFactor scaleFactor, HNLineItem lineItem, HNCustomer customer){
        super(scaleFactor);
        orderDependTables = new BTOrders<>(lineItem,customer);
    }

    @Override
    public int getBaseRowsNumber() {
        return orderDependTables.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        String baseString = "(o_orderkey %s,o_custkey %s NOT NULL,o_orderstatus VARCHAR(1),o_totalprice NUMERIC(12, 2)," +
                "o_orderdate DATE,o_orderpriority VARCHAR(15),o_clerk VARCHAR(15),o_shippriority INT," +
                "o_comment VARCHAR(79),PRIMARY KEY (o_orderkey))";
        return String.format(baseString, getOrderKeyType(), orderDependTables.getCustomerTable().getCustKeyType());
    }

    protected String getOrderKeyType(){
        return orderDependTables.getLineItemTable().getCustKeyType();
    }

    @Override
    String partitionFieldName() {
        return orderDependTables.getPartitionFieldName();
    }
}
