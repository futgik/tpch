package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.tables.base.BTNation;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class MyNation extends MyTable {

    BTNation nation;

    public MyNation() {
        super(new ScaleFactor());
        nation = new BTNation();
    }

    @Override
    public int getBaseRowsNumber() {
        return nation.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        return "(n_nationkey SMALLINT,n_name VARCHAR(25),n_regionkey SMALLINT,n_comment VARCHAR(152),PRIMARY KEY (n_nationkey))";
    }
}
