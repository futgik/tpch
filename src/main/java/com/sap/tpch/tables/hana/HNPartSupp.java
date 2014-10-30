package com.sap.tpch.tables.hana;

import com.sap.tpch.tables.base.BTPartSupp;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 * PartSupp table
 */
public class HNPartSupp extends HNTable {

    BTPartSupp<HNPart,HNSupplier> partSupp;

    public HNPartSupp(ScaleFactor scaleFactor, HNPart part, HNSupplier supplier){
        super(scaleFactor);
        partSupp = new BTPartSupp<>(part,supplier);
    }

    @Override
    public int getBaseRowsNumber() {
        return partSupp.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        String baseString = "(ps_partkey %s,ps_suppkey %s,ps_availqty INT,ps_supplycost NUMERIC(12, 2),ps_comment VARCHAR(199),PRIMARY KEY (ps_partkey,ps_suppkey))";
        return String.format(baseString,getPartKeyType(),getSuppKeyType());
    }

    protected String getPartKeyType(){
        return partSupp.getPartTable().getPartKeyType();
    }

    protected String getSuppKeyType(){
        return partSupp.getSupplierTable().getSuppKeyType();
    }

    @Override
    String partitionFieldName() {
        return partSupp.getPartitionFieldName();
    }
}
