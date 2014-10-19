package com.sap.hana.tpch.checks;

import com.jcraft.jsch.JSchException;
import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.ExecutionException;
import com.sap.hana.tpch.exec.RemoteExecutor;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 20.09.2014.
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
            return RemoteExecutor.getRemoteCommandExecutor().execCommand(String.format(Configurations.CM_CHECK_IS_DIR, dir)).equals("1");
        }
        catch (ExecutionException e){
            e.printStackTrace(); //todo
        }
        return false;
    }

    public static boolean isFileExist(String file)
    {
        try{
            return RemoteExecutor.getRemoteCommandExecutor().execCommand(String.format(Configurations.CM_CHECK_IS_FILE, file)).equals("1");

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
        return Checks.isDirectoryExist(Configurations.SERVER_WORK_DIR);
    }

    public static void main(String[] arg){
        System.out.println();
    }
}
