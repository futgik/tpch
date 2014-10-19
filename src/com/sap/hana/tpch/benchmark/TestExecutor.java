package com.sap.hana.tpch.benchmark;

import com.sap.hana.tpch.exception.PrepException;
import com.sap.hana.tpch.exception.TestException;
import com.sap.hana.tpch.remoute.EnvPreparationMonitor;
import com.sap.hana.tpch.remoute.RefreshDataPreparation;
import com.sap.hana.tpch.remoute.hana.HDBRefresh;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 07/10/2014.
 * Executor for all available test.
 * For creating Test Executor use static method.
 * This method allow only one instance of test executor.
 */
public class TestExecutor {

    BenchmarkProcessMonitor processMonitor;
    ScaleFactor scaleFactor;
    RefreshDataPreparation.RefreshFilePart refreshFilePart;
    private static TestExecutor TE_INSTANCE = null;

    class InnerBenchmarkProcessMonitor implements BenchmarkProcessMonitor{

        @Override
        public void prepare(String testName) {
            processMonitor.process(testName);
        }

        @Override
        public void process(String processOutput) {
            processMonitor.process(processOutput);
        }

        @Override
        public void end() {
            processMonitor.process("Test execution end.");
        }
    }

    class InnerPreparationMonitor implements EnvPreparationMonitor{
        @Override
        public void init(String startMessage) {
            processMonitor.process(startMessage);
        }

        @Override
        public void process(String processOutput) {
            processMonitor.process(processOutput);
        }

        @Override
        public void end(String endMessage) {
            processMonitor.process(endMessage);
        }
    }

    public static TestExecutor getTestExecutor(ScaleFactor scaleFactor, BenchmarkProcessMonitor processMonitor){
        if(TE_INSTANCE == null){
            TE_INSTANCE = new TestExecutor(scaleFactor, processMonitor);
        }
        return TE_INSTANCE;
    }

    private TestExecutor(){}

    private TestExecutor(ScaleFactor scaleFactor, BenchmarkProcessMonitor processMonitor){
        this.scaleFactor = scaleFactor;
        this.processMonitor = processMonitor;
    }

    /**
     * full tpch benchmark test.
     * @return result of test execution.
     * @throws TestException
     */
    public TestResults.TPCHMetrics doTPCBenchmark() throws TestException{
        prepareTest(TPCHBenchmark.class);
        TPCHBenchmark benchmark = new TPCHBenchmark(scaleFactor, refreshFilePart.next());
        runModifiableTest(benchmark);
        return benchmark.getResults();
    }

    /**
     * test for single query execution.
     * @param query query to execute.
     * @return result of query execution.
     * @throws TestException
     */
    public TestResults.SingleQueryTestResults doSingleQueryTest(Queries.Query query) throws TestException{
        prepareTest(SingleQueryTest.class);
        SingleQueryTest queryTest = new SingleQueryTest(scaleFactor, query);
        runUnmodifiableTest(queryTest);
        return queryTest.getResults();
    }

    /**
     * Execute power test.
     * @return result of power test execution.
     * @throws TestException
     */
    public TestResults.PowerTestResults doPowerTest() throws TestException{
        prepareTest(PowerTest.class);
        PowerTest powerTest = new PowerTest(scaleFactor, refreshFilePart.next());
        runModifiableTest(powerTest);
        return powerTest.getResults();
    }

    /**
     * Execute throughput test.
     * @return result of test execution.
     * @throws TestException
     */
    public TestResults.ThroughputTestResults doThroughputTest() throws TestException{
        prepareTest(LoadHanaTest.class);
        LoadHanaTest loadHanaTest = new LoadHanaTest(scaleFactor, refreshFilePart.next());
        runModifiableTest(loadHanaTest);
        return loadHanaTest.getResults();
    }

    /**
     * Execute refresh test.
     * @return result of test execution.
     * @throws TestException
     */
    public TestResults.RefreshResults doRefreshTest() throws TestException{
        prepareTest(RefreshTest.class);
        RefreshTest refreshTest = new RefreshTest(refreshFilePart.next());
        runModifiableTest(refreshTest);
        return refreshTest.getResults();
    }

    private void runUnmodifiableTest(HanaTest test) throws TestException{
        test.run(new InnerBenchmarkProcessMonitor());
    }

    private void runModifiableTest(HanaTest test) throws TestException{
        try{
            test.run(new InnerBenchmarkProcessMonitor());
        }finally {
            DatabaseState.resetState();
        }
    }

    private void prepareDatabase() throws TestException{
        if(!DatabaseState.isDatabaseReady()) {
            try {
                HDBRefresh.refresh(scaleFactor, new InnerPreparationMonitor());
            }catch(PrepException e){
                throw new TestException(e);
            }
        }
    }

    /**
     * Some preparations of database for setting initial state and generating refresh data.
     * @param testType type of required test.
     * @param <T> contain type of test.
     * @return Object for carrying out test.
     */
    private <T extends HanaTest> void prepareTest(Class<T> testType) throws TestException{
        if(testType == SingleQueryTest.class){
            prepareDatabase();
        }
        else {
            prepareDatabase();
            prepareTestData((Class<HanaTest>) testType);
        }
    }

    private void prepareTestData(Class<HanaTest> testType) throws TestException{
        try {
            refreshFilePart = RefreshDataPreparation.prepareData(scaleFactor, testType, 1, new InnerPreparationMonitor());
        }catch(PrepException e){
            throw new TestException(e);
        }
    }
}
