package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 * Contain some service functions for generate hana table scripts.
 */
class MyCreatingScriptsService {

    /**
     * Get type of field required for keeping specified amount of rows.
     * @param sf scale factor.
     * @param rowsCount base row count of table.
     * @return field type.
     */
    public static String getKeyFieldType(ScaleFactor sf, int rowsCount){
        long MAX_UINT_SIZE = 4294967296L;
        int result = (int) (Math.ceil((((double)rowsCount)/MAX_UINT_SIZE)*sf.getScaleFactorValue()));
        if(result<2) return "INT UNSIGNED";
        else return "BIGINT UNSIGNED";
    }
}
