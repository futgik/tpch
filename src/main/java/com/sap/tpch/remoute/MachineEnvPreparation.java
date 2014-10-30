package com.sap.tpch.remoute;

import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.exception.ExecutionException;
import com.sap.tpch.exception.FileDeliverException;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.exec.RemoteExecutor;

/**
 * Created by Alex on 21/09/2014.
 * Server preparations. Including creation directories, coping necessary files on server.
 */
public class MachineEnvPreparation implements Preparation{



    @Override
    public void prepare(PreparationMonitor preparationMonitor) throws PrepException {
        preparationMonitor.init("Start server preparation.");
        try {
            preparationMonitor.process("Create working directory...");
            createWorkingDirectory();
            preparationMonitor.process("Sending files to server...");
            sendFiles();
            preparationMonitor.process("Deploy files on server...");
            deployFiles();
            if(!Expand()) throw new PrepException("Can't prepare environment.");
        }catch (ExecutionException e){
            throw new PrepException("Can't create working directory.",e);
        }catch (FileDeliverException e){
            throw new PrepException("Can't send files.",e);
        }
        preparationMonitor.end("Server preparation successful.");
    }

    /**
     * Create working directory, put scripts and archives here and execute script to make environment
     * @throws PrepException
     */
    public static void prepareEnvironment(PreparationMonitor preparationMonitor) throws PrepException
    {
        MachineEnvPreparation envPreparation = new MachineEnvPreparation();
        envPreparation.prepare(preparationMonitor);
    }

    /**
     * Set execution writes for deploy.sh script and execute it for prepare environment.
     */
    private static boolean Expand() throws ExecutionException
    {
        return RemoteExecutor.getRemoteCommandExecutor().execSudoCommand(String.format(TPCHConfig.CM_SET_EXEC_WRITES, TPCHConfig.SERVER_WORK_DIR + "/deploy.sh")).equals("") &&
                (RemoteExecutor.getRemoteCommandExecutor().execSudoCommand(TPCHConfig.SERVER_WORK_DIR + "/deploy.sh").charAt(0) == '0');
    }

    private static boolean createWorkingDirectory() throws ExecutionException
    {
        return RemoteExecutor.getRemoteCommandExecutor().execSudoCommand(String.format(TPCHConfig.CM_MK_DIR, TPCHConfig.SERVER_WORK_DIR)).equals("")
                && RemoteExecutor.getRemoteCommandExecutor().execSudoCommand(String.format(TPCHConfig.CM_SET_ALL_WRITES, TPCHConfig.SERVER_WORK_DIR)).equals("");
    }

    private static void sendFiles() throws FileDeliverException
    {
        RemoteExecutor.getRemoteDeliver().deliverFile(TPCHConfig.CLIENT_DEPLOY_ARCHIVE);
    }

    private static void deployFiles() throws FileDeliverException
    {
        RemoteExecutor.getRemoteDeliver().deliverFile(TPCHConfig.CLIENT_DEPLOY_COMMAND);
    }
}
