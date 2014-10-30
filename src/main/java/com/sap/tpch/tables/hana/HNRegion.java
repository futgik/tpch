package com.sap.tpch.tables.hana;

import com.sap.tpch.benchmark.BenchmarkScaleFactor;
import com.sap.tpch.tables.base.BTRegion;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 * Region table.
 */
public class HNRegion extends HNTable {

    BTRegion region;

    public HNRegion() {
        super(BenchmarkScaleFactor.getScaleFactor());
        region = new BTRegion();
    }

    @Override
    public int getBaseRowsNumber() {
        return region.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        return String.format("CREATE COLUMN TABLE %s (r_regionkey INT,r_name VARCHAR(25),r_comment VARCHAR(152),PRIMARY KEY (r_regionkey))",getTableName());
    }

    @Override
    public long getMinAllowableRowsCount(){
        return getBaseRowsNumber();
    }

    @Override
    String partitionFieldName() {
        return region.getPartitionFieldName();
    }

    @Override
    public String getCreateScript() {
        return getBaseCreationScript();
    }
}
