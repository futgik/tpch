package com.sap.tpch.tables.hana;

import com.sap.tpch.benchmark.BenchmarkScaleFactor;
import com.sap.tpch.tables.base.BTNation;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class HNNation extends HNTable {

    BTNation nation;

    public HNNation() {
        super(BenchmarkScaleFactor.getScaleFactor());
        nation = new BTNation();
    }

    @Override
    public int getBaseRowsNumber() {
        return nation.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        return String.format("CREATE COLUMN TABLE %s (n_nationkey INT,n_name VARCHAR(25),n_regionkey INT," +
                "n_comment VARCHAR(152),PRIMARY KEY (n_nationkey))",getTableName());
    }

    @Override
    public long getMinAllowableRowsCount() {
        return getBaseRowsNumber();
    }

    @Override
    String partitionFieldName() {
        return nation.getPartitionFieldName();
    }

    @Override
    public String getCreateScript() {
        return getBaseCreationScript();
    }
}
