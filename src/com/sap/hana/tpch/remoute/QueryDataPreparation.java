package com.sap.hana.tpch.remoute;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.ExecutionException;
import com.sap.hana.tpch.exception.PrepException;
import com.sap.hana.tpch.exec.RemoteExecutor;
import com.sap.hana.tpch.exec.SSHProcessMonitor;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 09.10.2014.
 * generate dbgen table data on server.
 */
public class QueryDataPreparation implements Preparation {

    ScaleFactor scaleFactor;

    /**
     * Constructor receive scale factor value.
     * @param scaleFactor scale factor value.
     */
    public QueryDataPreparation(ScaleFactor scaleFactor){
        this.scaleFactor = scaleFactor;
    }

    /**
     * Static method make the same sa prepare method.
     * @param scaleFactor scale factor.
     * @param preparationMonitor monitor look after execution process.
     * @throws PrepException
     */
    public static void prepareData(ScaleFactor scaleFactor, EnvPreparationMonitor preparationMonitor) throws PrepException{
        QueryDataPreparation preparation = new QueryDataPreparation(scaleFactor);
        preparation.prepare(preparationMonitor);
    }

    @Override
    public void prepare(final EnvPreparationMonitor preparationMonitor) throws PrepException {
        preparationMonitor.init("Start data preparation.");
        preparationMonitor.process("Check available disk space...");
        if(isEnoughDiskSpace()){
            preparationMonitor.process("Generate data for tables...");
            generateDataForTables(scaleFactor, new SSHProcessMonitor() {
                @Override
                public void init(String executionCommand) {
                    preparationMonitor.process(executionCommand);
                }

                @Override
                public void process(String processOutput) {
                    preparationMonitor.process(processOutput);
                }

                @Override
                public void end(String fullOutput) {
                    preparationMonitor.end("Command execution end");
                }
            });
        }
        else{
            throw new PrepException("Not enough disk space size");
        }
    }

    /**
     * Check if it's enough space on server to generate table data.
     * @return @true if enough space @false in other case.
     */
    private boolean isEnoughDiskSpace(){
        double requiredDiskSpace = scaleFactor.getScaleFactorValue() * 1048576;
        double availableDiskSpace = getAvailableDiskSpace();
        return requiredDiskSpace < availableDiskSpace;
    }

    /**
     * return evaluable disk space on remote machine where working directory being.
     * @return evaluable disk space in kB.
     */
    private static double getAvailableDiskSpace(){
        try {
            String result = RemoteExecutor.getRemoteCommandExecutor().execCommand(String.format(Configurations.CM_DISK_SIZE, Configurations.SERVER_WORK_DIR));
            return new Double(result);
        } catch (ExecutionException e){
            e.printStackTrace();
        }
        return -1;
    }

    private static String generateDataForTables(ScaleFactor scaleFactor,SSHProcessMonitor executionMonitor) throws PrepException{
        String result = "";
        try {
            result = RemoteExecutor.getRemoteCommandExecutor(RemoteExecutor.OutputStreamType.ErrorStream).execCommand(String.format(Configurations.CM_DATA_GENERATOR, scaleFactor.toString()), executionMonitor);
        }catch (ExecutionException e){
            throw new PrepException("Can't generate table data",e);
        }
        return result;
    }
}
