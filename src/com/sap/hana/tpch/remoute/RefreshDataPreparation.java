package com.sap.hana.tpch.remoute;

import com.sap.hana.tpch.benchmark.*;
import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.ExecutionException;
import com.sap.hana.tpch.exception.PrepException;
import com.sap.hana.tpch.exec.RemoteExecutor;
import com.sap.hana.tpch.exec.SSHProcessMonitor;
import com.sap.hana.tpch.types.ScaleFactor;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Alex on 15/10/2014.
 * generate data files for refresh functions using dbgen.
 */
public class RefreshDataPreparation implements Preparation {

    //Scale factor.
    ScaleFactor scaleFactor;

    //Number of data sets in flat files for the update/delete functions.
    int dataSetNumbers;

    //Number of refresh files needed to pass test.
    int rfFilesCountPerTest;

    //When files generated contain index of current using data part file.
    RefreshFilePart refreshFilePart;

    /**
     *
     */
    public class RefreshFilePart implements Iterator<Integer>{
        final int MAX_RF_NUMBER;

        int currentRFNumber;
        int counter;

        private RefreshFilePart(int dataSetNumbers){
            currentRFNumber = 1;
            counter = 0;
            MAX_RF_NUMBER = dataSetNumbers;
        }

        @Override
        public boolean hasNext() {
            return counter<MAX_RF_NUMBER;
        }

        @Override
        public Integer next() {
            if(!hasNext()) throw new NoSuchElementException();
            counter++;
            int current = currentRFNumber;
            currentRFNumber += rfFilesCountPerTest;
            return current;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Constructor receive scale factor value and data sets numbers
     * @param scaleFactor scale factor value.
     */
    public RefreshDataPreparation(ScaleFactor scaleFactor, Class<HanaTest> testType, int testRunCount){
        this.scaleFactor = scaleFactor;
        this.rfFilesCountPerTest = getCountOfRefreshFilesPerTest(testType);
        this.dataSetNumbers = rfFilesCountPerTest*testRunCount;

    }

    private int getCountOfRefreshFilesPerTest(Class<HanaTest> testType){
        rfFilesCountPerTest = 1;
        if(testType.equals(LoadHanaTest.class))
            rfFilesCountPerTest = scaleFactor.getStreamCount();
        else if(testType.equals(TPCHBenchmark.class))
            rfFilesCountPerTest =(scaleFactor.getStreamCount()+1);
        return rfFilesCountPerTest;
    }

    /**
     * Static method make the same sa prepare method.
     * @param scaleFactor scale factor.
     * @param testType type of test for which generate data.
     * @param testRunCount test run count.
     * @param preparationMonitor monitor look after execution process.
     * @return number of current using refresh file.
     * @throws PrepException
     */
    public static RefreshFilePart prepareData(ScaleFactor scaleFactor, Class<HanaTest> testType, int testRunCount, EnvPreparationMonitor preparationMonitor) throws PrepException{
        if(!DatabaseState.isDatabaseReady()) throw new PrepException("Can't prepare refresh data, database not ready");
        RefreshDataPreparation preparation = new RefreshDataPreparation(scaleFactor, testType, testRunCount);
        preparation.prepare(preparationMonitor);
        return preparation.getRefreshFilePart();
    }

    /**
     * Generate set of data files using by refresh functions.
     * Method not working if database state not ready.
     * @param preparationMonitor using for monitor some process.
     * @throws PrepException
     */
    @Override
    public void prepare(final EnvPreparationMonitor preparationMonitor) throws PrepException {
        if(!DatabaseState.isDatabaseReady()){
            preparationMonitor.init("Can't start data preparation, database modified.");
            return;
        }
        preparationMonitor.init("Start data preparation.");
        preparationMonitor.process("Generate data for refresh tables...");
        generateDataForRefreshTable(new SSHProcessMonitor() {
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
        refreshFilePart = new RefreshFilePart(dataSetNumbers);
    }

    private String generateDataForRefreshTable(SSHProcessMonitor executionMonitor) throws PrepException{
        String result;
        try {
            result = RemoteExecutor.getRemoteCommandExecutor(RemoteExecutor.OutputStreamType.ErrorStream).execCommand(String.format(Configurations.CM_DATA_REFRESH_GENERATOR, dataSetNumbers, scaleFactor.getScaleFactorValue()), executionMonitor);
        }catch (ExecutionException e){
            throw new PrepException("Can't generate data for refresh table",e);
        }
        return result;
    }

    public RefreshFilePart getRefreshFilePart(){
        return refreshFilePart;
    }
}
