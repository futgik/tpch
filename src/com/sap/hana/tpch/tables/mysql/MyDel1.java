package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.tables.base.BTDel1;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 01/10/2014.
 */
public class MyDel1 extends MyTable {

    BTDel1 del1;

    public MyDel1() {
        super(new ScaleFactor());
        del1 = new BTDel1();
    }

    @Override
    public int getBaseRowsNumber() {
        return del1.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        return String.format("CREATE TABLE %s (D_ORDERKEY INTEGER)", getTableName());
    }

    @Override
    public String getCreateScript() {
        return getBaseCreationScript();
    }

    @Override
    public String getGeneratedTableName(){
        return del1.getGeneratedTableName();
    }
}
