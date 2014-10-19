package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.tables.base.BTOrders;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class MyOrders extends MyTable {

    BTOrders<MyCustomer> orders;

    public MyOrders(ScaleFactor scaleFactor, MyCustomer customer){
        super(scaleFactor);
        orders = new BTOrders<>(customer);
    }

    @Override
    public int getBaseRowsNumber() {
        return orders.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        String baseString = "(o_orderkey %s,o_custkey %s NOT NULL,o_orderstatus VARCHAR(1),o_totalprice NUMERIC(12, 2)," +
                "o_orderdate DATE,o_orderpriority VARCHAR(15),o_clerk VARCHAR(15),o_shippriority INT," +
                "o_comment VARCHAR(79),PRIMARY KEY (o_orderkey))";
        return String.format(baseString, getOrderKeyType(),orders.getCustomerTable().getCustKeyType());
    }

    protected String getOrderKeyType(){
        return MyCreatingScriptsService.getKeyFieldType(getScaleFactor(), getBaseRowsNumber());
    }
}
