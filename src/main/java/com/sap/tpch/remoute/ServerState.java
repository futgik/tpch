package com.sap.tpch.remoute;

import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.exception.ExecutionException;
import com.sap.tpch.exec.RemoteExecutor;

/**
 * Created by Alex on 22/10/2014.
 */
public class  ServerState {

    /**
     * Check if files with dbgen data exists.
     * @return @true - if files with import data exists, @false another case.
     */
    public static boolean isDBGenDataExists(){
        try {
            return Integer.parseInt(RemoteExecutor.getRemoteCommandExecutor().execSingleRowCommand(TPCHConfig.CHECK_TABLE_EXISTENCE)) > 0;
        }catch (ExecutionException ignored){}
        return false;
    }
}
