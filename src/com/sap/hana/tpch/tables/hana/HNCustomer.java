package com.sap.hana.tpch.tables.hana;

import com.sap.hana.tpch.tables.base.BTCustomer;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class HNCustomer extends HNTable {

    BTCustomer customer;

    public HNCustomer(ScaleFactor scaleFactor) {
        super(scaleFactor);
        customer = new BTCustomer();
    }

    @Override
    public int getBaseRowsNumber() {
        return customer.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        String baseScript = "(c_custkey %s,c_name VARCHAR(25),c_address VARCHAR(40),c_nationkey INT,c_phone VARCHAR(15),c_acctbal NUMERIC(12, 2),c_mktsegment VARCHAR(10),c_comment VARCHAR(117),PRIMARY KEY (c_custkey))";
        return String.format(baseScript, getCustKeyType());
    }

    protected String getCustKeyType(){
        return HNCreatingScriptsService.getKeyFieldType(getScaleFactor(), getBaseRowsNumber()).toString();
    }

    @Override
    String partitionFieldName() {
        return customer.getPartitionFieldName();
    }
}
