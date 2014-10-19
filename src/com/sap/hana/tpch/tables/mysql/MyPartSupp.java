package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.tables.base.BTPartSupp;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class MyPartSupp extends MyTable {

    BTPartSupp<MyPart,MySupplier> partSupp;

    public MyPartSupp(ScaleFactor scaleFactor, MyPart part, MySupplier supplier){
        super(scaleFactor);
        partSupp = new BTPartSupp<>(part,supplier);
    }

    @Override
    public int getBaseRowsNumber() {
        return partSupp.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        String baseString = "(ps_partkey %s,ps_suppkey %s,ps_availqty INT,ps_supplycost NUMERIC(12, 2)," +
                "ps_comment VARCHAR(199),PRIMARY KEY (ps_partkey,ps_suppkey))";
        return String.format(baseString,getPartKeyType(),getSuppKeyType());
    }

    protected String getPartKeyType(){
        return partSupp.getPartTable().getPartKeyType();
    }

    protected String getSuppKeyType(){
        return partSupp.getSupplierTable().getSuppKeyType();
    }
}
