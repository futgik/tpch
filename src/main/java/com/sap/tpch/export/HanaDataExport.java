package com.sap.tpch.export;

import com.sap.tpch.benchmark.BenchmarkScaleFactor;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.db_interaction.DBInstance;
import com.sap.tpch.exception.ExecutionException;
import com.sap.tpch.exception.ExportException;
import com.sap.tpch.exec.RemoteExecutor;
import com.sap.tpch.remoute.DatabaseState;
import com.sap.tpch.remoute.PreparationMonitor;
import com.sap.tpch.types.ScaleFactor;

import java.sql.SQLException;

/**
 * Created by Alex on 26/10/2014.
 * Export hana data from database to binary file.
 */
public class HanaDataExport implements IExport{

    private static String CM_HANA_EXPORT = "EXPORT %s.\"*\" AS BINARY INTO '%s'";

    /**
     * Destination directory name.
     */
    String directoryName;

    /**
     * Process monitor.
     */
    PreparationMonitor monitor;

    public HanaDataExport(String directoryName, PreparationMonitor monitor){
        if(!directoryName.substring(directoryName.length()-1,directoryName.length()).equals("/"))
            directoryName+="/";
        this.directoryName = directoryName;
        this.monitor = monitor;
    }

    /**
     * Export tpch database schema to file.
     * Output file location in output directory under [scale]gb_binary name.
     * @param directoryName output directory.
     * @return @true if export successful, @false another case.
     * @throws ExportException
     */
    public static void ExportHanaData(String directoryName, PreparationMonitor monitor) throws ExportException{
        HanaDataExport export = new HanaDataExport(directoryName, monitor);
        export.export();
    }

    @Override
    public void export() throws ExportException {
        monitor.init("Export process start.");
        if(DatabaseState.isDatabaseReady()){
            try {
                directoryName += String.format("%sgb_binary", BenchmarkScaleFactor.getScaleFactor().getScaleFactorValue());
                monitor.process("Prepare export directory "+directoryName);
                makeDirectoryPath(directoryName);
                monitor.process("Exporting schema "+TPCHConfig.SCHEMA_NAME);
                DBInstance.getDBInstance().executeNoResultQuery(String.format(CM_HANA_EXPORT, TPCHConfig.SCHEMA_NAME, directoryName));
            }
            catch (SQLException | ExecutionException e){
                throw new ExportException("Can't export database data. Can't execute export query",e);
            }
        }
        else
            throw new ExportException("Can't do export. Database not ready");
        monitor.end("Export process end.");
    }

    private void makeDirectoryPath(String directoryName) throws ExecutionException{
        RemoteExecutor.getRemoteCommandExecutor().execCommand(String.format(TPCHConfig.CM_MK_DIR,directoryName));
    }
}
