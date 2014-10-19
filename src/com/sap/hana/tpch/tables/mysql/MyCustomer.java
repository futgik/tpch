package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.tables.base.BTCustomer;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class MyCustomer extends MyTable {

    BTCustomer customer;

    public MyCustomer(ScaleFactor scaleFactor) {
        super(scaleFactor);
        customer = new BTCustomer();
    }

    @Override
    public int getBaseRowsNumber() {
        return customer.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        String baseScript = "(c_custkey %s,c_name VARCHAR(25),c_address VARCHAR(40),c_nationkey SMALLINT,c_phone VARCHAR(15),c_acctbal NUMERIC(12, 2),c_mktsegment VARCHAR(10),c_comment VARCHAR(117),PRIMARY KEY (c_custkey))";
        return String.format(baseScript, getCustKeyType());
    }

    protected String getCustKeyType(){
        return MyCreatingScriptsService.getKeyFieldType(getScaleFactor(), getBaseRowsNumber());
    }
}
