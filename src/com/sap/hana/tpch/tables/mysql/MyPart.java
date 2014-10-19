package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.tables.base.BTPart;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public class MyPart extends MyTable {

    BTPart part;

    public MyPart(ScaleFactor scaleFactor) {
        super(scaleFactor);
        part = new BTPart();
    }

    @Override
    public int getBaseRowsNumber() {
       return part.getBaseRowsNumber();
    }

    @Override
    String getBaseCreationScript() {
        String baseScript = "(p_partkey %s,p_name VARCHAR(55),p_mfgr VARCHAR(25),p_brand VARCHAR(10)," +
                "p_type VARCHAR(25),p_size INT,p_container VARCHAR(10),p_retailprice NUMERIC(12, 2)," +
                "p_comment VARCHAR(23),PRIMARY KEY (p_partkey))";
        return String.format(baseScript, getPartKeyType());
    }

    protected String getPartKeyType(){
        return MyCreatingScriptsService.getKeyFieldType(getScaleFactor(), getBaseRowsNumber());
    }
}
