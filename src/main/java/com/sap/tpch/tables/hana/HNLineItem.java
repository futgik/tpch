package com.sap.tpch.tables.hana;

import com.sap.tpch.tables.base.BTLineItem;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class HNLineItem extends HNTable {

    BTLineItem<HNPartSupp> lineItem;

    public HNLineItem(ScaleFactor scaleFactor, HNPartSupp partSuppTable){
        super(scaleFactor);
        lineItem = new BTLineItem<>(partSuppTable);
    }

    @Override
    public int getBaseRowsNumber() {
        return lineItem.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        String baseScript = "(l_orderkey %s,l_partkey %s NOT NULL,l_suppkey %s NOT NULL,l_linenumber %s," +
                "l_quantity NUMERIC(12, 2),l_extendedprice NUMERIC(12, 2),l_discount NUMERIC(12, 2)," +
                "l_tax NUMERIC(12, 2) NOT NULL,l_returnflag VARCHAR(1),l_linestatus VARCHAR(1),l_shipdate DATE," +
                "l_commitdate DATE,l_receiptdate DATE\t,l_shipinstruct VARCHAR(25),l_shipmode VARCHAR(10)," +
                "l_comment VARCHAR(44),PRIMARY KEY (l_orderkey,l_linenumber))";
        return String.format(baseScript, getCustKeyType(),
                lineItem.getPartSuppTable().getPartKeyType(), lineItem.getPartSuppTable().getSuppKeyType(),
                HNCreatingScriptsService.getKeyFieldType(getScaleFactor(), getBaseRowsNumber()));
    }

    protected String getCustKeyType(){
        return HNCreatingScriptsService.getKeyFieldType(getScaleFactor(), getBaseRowsNumber()).toString();
    }

    /**
     * Get real row count that table must contain.
     * @return real row count that table must contain.
     */
    @Override
    public long getMinAllowableRowsCount(){
        return (long)(getScaleFactor().getScaleFactorValue()*(getBaseRowsNumber()-getBaseRowsNumber()*0.1));
    }

    @Override
    String partitionFieldName() {
        return lineItem.getPartitionFieldName();
    }
}
