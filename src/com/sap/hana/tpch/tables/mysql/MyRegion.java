package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.tables.base.BTRegion;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class MyRegion extends MyTable {

    BTRegion region;

    public MyRegion() {
        super(new ScaleFactor());
        region = new BTRegion();
    }

    @Override
    public int getBaseRowsNumber() {
        return region.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        return "(r_regionkey SMALLINT,r_name VARCHAR(25),r_comment VARCHAR(152),PRIMARY KEY (r_regionkey))";
    }
}
