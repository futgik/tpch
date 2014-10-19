package com.sap.hana.tpch.benchmark;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.LoadResultException;
import com.sap.hana.tpch.exception.TestException;

import javax.security.auth.login.Configuration;

/**
 * Created by Alex on 01/10/2014.
 * Refresh use two queries one of which adds new sales information to the database
 * and another remove old sale information from the database.
 */
public class RefreshTest extends BaseTest {

    int refreshDataIndex;

    RefreshTest(int refreshDataIndex){
        super();
        this.refreshDataIndex = refreshDataIndex;
    }

    @Override
    public void run(BenchmarkProcessMonitor monitor) throws TestException {
        super.run(monitor);
    }

    @Override
    public String getTestFileName() {
        return "refresh";
    }

    @Override
    public String getTestParams() {
        return String.format("%s %d %s", Configurations.SERVER_DBGEN_DIR, refreshDataIndex, Configurations.SCHEMA_NAME);
    }

    @Override
    public TestResults.RefreshResults getResults() throws TestException{
        try {
            return new TestResults.RefreshResults(getOutputFileName());

        } catch (LoadResultException e) {
            throw new TestException(String.format("Can't load test results from file %s",getOutputFileName()), e);
        }
    }

    @Override
    public String getTestName() {
        return "Refresh test";
    }
}
