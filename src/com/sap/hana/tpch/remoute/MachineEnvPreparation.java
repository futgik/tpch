package com.sap.hana.tpch.remoute;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.ExecutionException;
import com.sap.hana.tpch.exception.FileDeliverException;
import com.sap.hana.tpch.exception.PrepException;
import com.sap.hana.tpch.exec.RemoteExecutor;

/**
 * Created by Alex on 21/09/2014.
 * Server preparations. Including creation directories, coping necessary files.
 */
public class MachineEnvPreparation implements Preparation{

    @Override
    public void prepare(EnvPreparationMonitor preparationMonitor) throws PrepException {
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
    public static void prepareEnvironment(EnvPreparationMonitor preparationMonitor) throws PrepException
    {
        MachineEnvPreparation envPreparation = new MachineEnvPreparation();
        envPreparation.prepare(preparationMonitor);
    }

    /**
     * Set execution writes for deploy.sh script and execute it for prepare environment.
     */
    private static boolean Expand() throws ExecutionException
    {
        return RemoteExecutor.getRemoteCommandExecutor().execSudoCommand(String.format(Configurations.CM_SET_EXEC_WRITES, Configurations.SERVER_WORK_DIR + "/deploy.sh")).equals("") &&
                (RemoteExecutor.getRemoteCommandExecutor().execSudoCommand(Configurations.SERVER_WORK_DIR + "/deploy.sh").charAt(0) == '0');
    }

    private static boolean createWorkingDirectory() throws ExecutionException
    {
        return RemoteExecutor.getRemoteCommandExecutor().execSudoCommand(String.format(Configurations.CM_MK_DIR, Configurations.SERVER_WORK_DIR)).equals("")
                && RemoteExecutor.getRemoteCommandExecutor().execSudoCommand(String.format(Configurations.CM_SET_ALL_WRITES, Configurations.SERVER_WORK_DIR)).equals("");
    }

    private static void sendFiles() throws FileDeliverException
    {
        RemoteExecutor.getRemoteDeliver().deliverFile(Configurations.CLIENT_DEPLOY_ARCHIVE);
    }

    private static void deployFiles() throws FileDeliverException
    {
        RemoteExecutor.getRemoteDeliver().deliverFile(Configurations.CLIENT_DEPLOY_COMMAND);
    }
}
