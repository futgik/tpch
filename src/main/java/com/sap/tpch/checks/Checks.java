package com.sap.tpch.checks;

import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.exception.ExecutionException;
import com.sap.tpch.exec.RemoteExecutor;

/**
 * Created by Alex on 20.09.2014.
 * Contain some checks executing on remote machine.
 */
public class Checks {

    /**
     * Check if specified directory exist
     * @param dir Checking directory
     * @return @true if directory exist @false another case.
     */
    public static boolean isDirectoryExist(String dir)
    {
        try {
            return RemoteExecutor.getRemoteCommandExecutor().execSingleRowCommand(String.format(TPCHConfig.CM_CHECK_IS_DIR, dir)).equals("1");
        }
        catch (ExecutionException e){
            e.printStackTrace(); //todo
        }
        return false;
    }

    /**
     * Check if specified file exist
     * @param file Checking directory
     * @return @true if directory exist @false another case.
     */
    public static boolean isFileExist(String file)
    {
        try{
            return RemoteExecutor.getRemoteCommandExecutor().execSingleRowCommand(String.format(TPCHConfig.CM_CHECK_IS_FILE, file)).equals("1");

        } catch (ExecutionException e) {
            e.printStackTrace(); //todo
        }
        return false;
    }

    /**
     * Check if working directory on server exist
     * @return @true if direcotry exist @false another case.
     */
    public static boolean isWorkingDirectoryExist(){
        return Checks.isDirectoryExist(TPCHConfig.SERVER_WORK_DIR);
    }

    public static void main(String[] arg){
        System.out.println();
    }
}
