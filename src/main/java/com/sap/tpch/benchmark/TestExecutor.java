package com.sap.tpch.benchmark;

import com.sap.tpch.benchmark.results.*;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.db_interaction.DBInstance;
import com.sap.tpch.exception.PrepException;
import com.sap.tpch.exception.TestException;
import com.sap.tpch.exec.RemoteExecutor;
import com.sap.tpch.remoute.PreparationMonitor;
import com.sap.tpch.remoute.RefreshDataPreparation;
import com.sap.tpch.types.ScaleFactor;

import java.sql.ResultSet;

/**
 * Created by Alex on 07/10/2014.
 * Executor for all available test.
 * For creating Test Executor use static method.
 * This method allow only one instance of test executor.
 */
public class TestExecutor {

    BenchmarkProcessMonitor processMonitor;
    /**
     * Scale factor.
     */
    ScaleFactor scaleFactor;
    /**
     * Current actual index for refresh query.
     */
    RefreshDataPreparation.RefreshFilePart refreshFilePart;
    /**
     * Instance of test executor.
     * Test executor created only once.
     */
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

    class InnerPreparationMonitor implements PreparationMonitor {
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

    /**
     * Get test executor object. If first time create test executor object, else return saved instance.
     * @param scaleFactor scale factor.
     * @param processMonitor process monitor to get process status.
     * @return test executor object.
     */
    public static TestExecutor getTestExecutor(ScaleFactor scaleFactor, BenchmarkProcessMonitor processMonitor){
        if(TE_INSTANCE == null){
            TE_INSTANCE = new TestExecutor(scaleFactor, processMonitor);
        }
        return TE_INSTANCE;
    }

    private TestExecutor(){}

    /**
     * Not available directly.
     * @param scaleFactor scale factor.
     * @param processMonitor process monitor.
     */
    private TestExecutor(ScaleFactor scaleFactor, BenchmarkProcessMonitor processMonitor){
        this.scaleFactor = scaleFactor;
        this.processMonitor = processMonitor;
    }

    /**
     * Full tpch benchmark test.
     * @return result of test execution.
     * @throws TestException
     */
    public TPCHMetrics doTPCBenchmark() throws TestException{
        prepareTest(TPCHBenchmark.class);
        TPCHBenchmark benchmark = new TPCHBenchmark(scaleFactor, refreshFilePart.next());
        return runModifiableTest(benchmark);
    }

    /**
     * Series of tpch benchmark test
     * @param testRunCount repetition count to run test.
     * @return timing test results
     * @throws TestException
     */
    public TPCHSeriesMetrics doTPCHSeriesBenchmark(int testRunCount) throws  TestException{
        prepareTest(TPCHBenchmark.class,testRunCount);
        TPCHSeriesBenchmark benchmark = new TPCHSeriesBenchmark(scaleFactor,testRunCount,refreshFilePart);
        return runModifiableTest(benchmark);
    }

    /**
     * Test for single query execution.
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
    public PowerTestResult doPowerTest() throws TestException{
        prepareTest(PowerTest.class);
        PowerTest powerTest = new PowerTest(scaleFactor, refreshFilePart.next());
        return runModifiableTest(powerTest);
    }

    /**
     * Execute throughput test.
     * @return result of test execution.
     * @throws TestException
     */
    public ThroughputTestResult doThroughputTest() throws TestException{
        prepareTest(LoadHanaTest.class);
        LoadHanaTest loadHanaTest = new LoadHanaTest(scaleFactor, refreshFilePart.next());
        return runModifiableTest(loadHanaTest);
    }

    /**
     * Execute refresh test.
     * @return result of test execution.
     * @throws TestException
     */
    public PowerTestResult doRefreshTest() throws TestException{
        prepareTest(RefreshTest.class);
        RefreshTest refreshTest = new RefreshTest(refreshFilePart.next());
        return runModifiableTest(refreshTest);
    }

    private <T extends TestResult> T runUnmodifiableTest(HanaTest test) throws TestException{
        return (T)test.run(new InnerBenchmarkProcessMonitor());
    }

    private <T extends TestResult> T runModifiableTest(HanaTest test) throws TestException{
        T r = (T)test.run(new InnerBenchmarkProcessMonitor());
        doTestCleaning();
        return r;
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
        if(testType != SingleQueryTest.class){
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

    /**
     * Clear del tables from database and delete.#, lineitem.tbl.u# and orders.tbl.u# from filesystem.
     */
    private void doTestCleaning() {
        (new Thread(
        new Runnable() {
            @Override
            public void run() {
                if(TPCHConfig.DEBUG) System.out.println("Clear start.");
                try {
                    String HDB_DEL_TABLE_LIST = String.format("SELECT TABLE_NAME FROM \"PUBLIC\".\"M_TABLES\" WHERE SCHEMA_NAME='%s' AND TABLE_NAME LIKE 'DEL%%'", TPCHConfig.SCHEMA_NAME);
                    String HDB_DROP_TABLE = "DROP TABLE %s.%s";
                    ResultSet rs = DBInstance.getDBInstance().executeQuery(HDB_DEL_TABLE_LIST);
                    while (rs.next()) {
                        String dropTableName = rs.getString(1);
                        DBInstance.getDBInstance().executeNoResultQuery(String.format(HDB_DROP_TABLE, TPCHConfig.SCHEMA_NAME, dropTableName));
                    }
                    RemoteExecutor.getRemoteCommandExecutor().execSudoCommand(TPCHConfig.CM_CLEAR_DBGEN_REFRESH_DATA);
                } catch (Exception ignored) {
                    if(TPCHConfig.DEBUG) ignored.printStackTrace();
                }
                if(TPCHConfig.DEBUG) System.out.println("Clear end.");
            }
        })).start();
    }
}
