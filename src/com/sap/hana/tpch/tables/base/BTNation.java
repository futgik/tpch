package com.sap.hana.tpch.tables.base;

import com.sap.hana.tpch.tables.hana.HNTable;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 * Constance Nation table facts base for all database.
 */
public class BTNation implements BaseTableInfo{

    public BTNation() {
    }

    @Override
    public int getBaseRowsNumber() {
        return 25;
    }

    @Override
    public String getPartitionFieldName() {
        return "n_nationkey";
    }
}
