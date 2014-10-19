package com.sap.hana.tpch.tables.base;

import com.sap.hana.tpch.tables.hana.HNTable;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 * Constance Region table facts base for all database
 */
public class BTRegion implements BaseTableInfo{

    public BTRegion() {
    }

    @Override
    public int getBaseRowsNumber() {
        return 5;
    }

    @Override
    public String getPartitionFieldName() {
        return "r_regionkey";
    }

}
