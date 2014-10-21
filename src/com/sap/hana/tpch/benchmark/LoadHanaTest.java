package com.sap.hana.tpch.benchmark;

import com.sap.hana.tpch.benchmark.results.ThroughputTestResults;
import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.LoadResultException;
import com.sap.hana.tpch.exception.TestException;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 01/10/2014.
 * This test using streams order matrix to run simultaneously queries.
 */
public class LoadHanaTest extends BaseTest{
    /**
     * Test scale factor.
     */
    ScaleFactor scaleFactor;

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

    @Override
    public ThroughputTestResults run(BenchmarkProcessMonitor monitor) throws TestException {
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

    @Override
    public ThroughputTestResults getResults() throws TestException {
        try {
            return new ThroughputTestResults(getOutputFileName());

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
        return String.format("%d %s %d %s %d %s",getInstances(), Configurations.VALIDATION_QUERY,
                getScaleFactor().getScaleFactorValue(), Configurations.SERVER_DBGEN_DIR,
                refreshDataIndex,  Configurations.SCHEMA_NAME);
    }
}
