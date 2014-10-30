package com.sap.tpch.benchmark;

import com.sap.tpch.benchmark.results.ThroughputTestImportResult;
import com.sap.tpch.benchmark.results.ThroughputTestResult;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.exception.LoadResultException;
import com.sap.tpch.exception.TestException;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 01/10/2014.
 * This test using streams order matrix to run simultaneously queries.
 */
public class LoadHanaTest extends BaseTest{
    /**
     * Test scale factor.
     */
    ScaleFactor scaleFactor;

    /**
     * Index of actual refresh data.
     */
    int refreshDataIndex;

    /**
     * Default constructor.
     * @param scaleFactor scale factor.
     */
    LoadHanaTest(ScaleFactor scaleFactor, int refreshDataIndex){
        super();
        this.scaleFactor = scaleFactor;
        this.refreshDataIndex = refreshDataIndex;
    }

    /**
     *  Execute test and return result of execution.
     * @param monitor monitor of process execution, maybe @null
     * @return result of test execution.
     * @throws TestException
     */
    @Override
    public ThroughputTestResult run(BenchmarkProcessMonitor monitor) throws TestException {
        return super.run(monitor);
    }

    /**
     * get number of streams using in test.
     * @return number of stream using in test.
     */
    public int getInstances() {
        return scaleFactor.getStreamCount();
    }

    private ScaleFactor getScaleFactor(){
        return scaleFactor;
    }

    /**
     * Get result of throughput test.
     * @return result of throughput test.
     * @throws TestException
     */
    @Override
    public ThroughputTestResult getResults() throws TestException {
        try {
            return new ThroughputTestImportResult(getOutputFileName());

        } catch (LoadResultException e) {
            throw new TestException(String.format("Can't load test results from file %s",getOutputFileName()), e);
        }
    }

    @Override
    public String getTestName() {
        return "Load test";
    }

    @Override
    public String getTestFileName() {
        return "load_hana";
    }

    @Override
    public String getTestParams() {
        return String.format("%d %s %d %s %d %s",getInstances(), TPCHConfig.VALIDATION_QUERY,
                getScaleFactor().getScaleFactorValue(), TPCHConfig.SERVER_DBGEN_DIR,
                refreshDataIndex,  TPCHConfig.SCHEMA_NAME);
    }
}
