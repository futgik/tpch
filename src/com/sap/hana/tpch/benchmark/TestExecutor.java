package com.sap.hana.tpch.benchmark;

import com.sap.hana.tpch.benchmark.results.*;
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
    public TPCHMetrics doTPCBenchmark() throws TestException{
        prepareTest(TPCHBenchmark.class);
        TPCHBenchmark benchmark = new TPCHBenchmark(scaleFactor, refreshFilePart.next());
        return runModifiableTest(benchmark);
    }

    public TPCHSeriesMetrics doTPCHSeriesBenchmark(int testRunCount) throws  TestException{
        prepareTest(TPCHBenchmark.class,testRunCount);
        TPCHSeriesBenchmark benchmark = new TPCHSeriesBenchmark(scaleFactor,testRunCount,refreshFilePart);
        return runModifiableTest(benchmark);
    }

    /**
     * test for single query execution.
     * @param query query to execute.
     * @return result of query execution.
     * @throws TestException
     */
    public SingleQueryTestResults doSingleQueryTest(Queries.Query query) throws TestException{
        prepareTest(SingleQueryTest.class);
        SingleQueryTest queryTest = new SingleQueryTest(scaleFactor, query);
        return runUnmodifiableTest(queryTest);
    }

    /**
     * Execute power test.
     * @return result of power test execution.
     * @throws TestException
     */
    public PowerTestResults doPowerTest() throws TestException{
        prepareTest(PowerTest.class);
        PowerTest powerTest = new PowerTest(scaleFactor, refreshFilePart.next());
        return runModifiableTest(powerTest);
    }

    /**
     * Execute throughput test.
     * @return result of test execution.
     * @throws TestException
     */
    public ThroughputTestResults doThroughputTest() throws TestException{
        prepareTest(LoadHanaTest.class);
        LoadHanaTest loadHanaTest = new LoadHanaTest(scaleFactor, refreshFilePart.next());
        return runModifiableTest(loadHanaTest);
    }

    /**
     * Execute refresh test.
     * @return result of test execution.
     * @throws TestException
     */
    public RefreshResults doRefreshTest() throws TestException{
        prepareTest(RefreshTest.class);
        RefreshTest refreshTest = new RefreshTest(refreshFilePart.next());
        runModifiableTest(refreshTest);
        return refreshTest.getResults();
    }

    private <T extends TestResult> T runUnmodifiableTest(HanaTest test) throws TestException{
        return (T)test.run(new InnerBenchmarkProcessMonitor());
    }

    private <T extends TestResult> T runModifiableTest(HanaTest test) throws TestException{
        try{
            return (T)test.run(new InnerBenchmarkProcessMonitor());
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
        prepareTest(testType,1);
    }

    /**
     * Some preparations of database for setting initial state and generating refresh data.
     * @param testType type of required test.
     * @param <T> contain type of test.
     * @return Object for carrying out test.
     */
    private <T extends HanaTest> void prepareTest(Class<T> testType, int testRunCount) throws TestException{
        if(testType == SingleQueryTest.class){
            prepareDatabase();
        }
        else {
            prepareDatabase();
            prepareTestData((Class<HanaTest>) testType, testRunCount);
        }
    }

    private void prepareTestData(Class<HanaTest> testType, int testRunCount) throws TestException{
        try {
            refreshFilePart = RefreshDataPreparation.prepareData(scaleFactor, testType, testRunCount, new InnerPreparationMonitor());
        }catch(PrepException e){
            throw new TestException(e);
        }
    }
}
