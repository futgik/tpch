package com.sap.tpch.tables.hana;

import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.exec.RemoteExecutor;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 * Contain some service functions for generate hana table scripts.
 */
public class HNCreatingScriptsService {

    static int MAX_PARTITION_ROWS_COUNT= 2000000000;

    /**
     * Get required partition count for specified scale factor
     * @param sf scale factor.
     * @param rowsCount base row count of table.
     * @return table partition count.
     */
    public static int getTablePartitionCount(ScaleFactor sf, int rowsCount){
        double factor = 0.25;
        int result = (int) (Math.ceil((rowsCount/(double)MAX_PARTITION_ROWS_COUNT)*sf.getScaleFactorValue()+factor));
        return result < 1 ? 1 : result;
    }

    /**
     * Get type of field required for keeping specified amount of rows.
     * @param sf scale factor.
     * @param rowsCount base row count of table.
     * @return field type.
     */
    public static KeyFieldType getKeyFieldType(ScaleFactor sf, int rowsCount){
        int result = (int) (Math.ceil((rowsCount/(double)MAX_PARTITION_ROWS_COUNT)*sf.getScaleFactorValue()));
        if(result<2) return KeyFieldType.INT;
        else return KeyFieldType.BIGINT;
    }

    public static int getThreadsCount(){
        int cpuCount = 0;
        try {
            cpuCount = Integer.parseInt(RemoteExecutor.getRemoteCommandExecutor().execCommand(TPCHConfig.CM_GET_CPU_COUNT).replace("\n",""));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return cpuCount;
    }

    public static int getBatchCount(){
        return getThreadsCount()*1000;
    }
}
