package com.sap.tpch.benchmark;

import com.sap.tpch.benchmark.results.PowerTestImportResult;
import com.sap.tpch.benchmark.results.PowerTestResult;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.exception.LoadResultException;
import com.sap.tpch.exception.TestException;

/**
 * Created by Alex on 01/10/2014.
 * Refresh use two queries one of which adds new sales information to the database
 * and another remove old sale information from the database.
 */
public class RefreshTest extends BaseTest {

    /**
     * Index of actual refresh data.
     */
    int refreshDataIndex;

    RefreshTest(int refreshDataIndex){
        super();
        this.refreshDataIndex = refreshDataIndex;
    }

    @Override
    public PowerTestResult run(BenchmarkProcessMonitor monitor) throws TestException {
        return super.run(monitor);
    }

    @Override
    public String getTestFileName() {
        return "refresh";
    }

    @Override
    public String getTestParams() {
        return String.format("%s %d %s", TPCHConfig.SERVER_DBGEN_DIR, refreshDataIndex, TPCHConfig.SCHEMA_NAME);
    }

    @Override
    public PowerTestResult getResults() throws TestException{
        try {
            return new PowerTestImportResult(getOutputFileName());

        } catch (LoadResultException e) {
            throw new TestException(String.format("Can't load test results from file %s",getOutputFileName()), e);
        }
    }

    @Override
    public String getTestName() {
        return "Refresh test";
    }
}
