package com.sap.hana.tpch.tables.hana;

import com.sap.hana.tpch.tables.base.BTRegion;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class HNRegion extends HNTable {

    BTRegion region;

    public HNRegion() {
        super(new ScaleFactor());
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
    String partitionFieldName() {
        return region.getPartitionFieldName();
    }

    @Override
    public String getCreateScript() {
        return getBaseCreationScript();
    }
}
