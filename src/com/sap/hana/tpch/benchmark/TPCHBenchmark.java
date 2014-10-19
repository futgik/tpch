package com.sap.hana.tpch.benchmark;

import com.sap.hana.tpch.exception.TestException;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 01/10/2014.
 * Contain methods for carrying out tpch testing.
 */
public class TPCHBenchmark implements HanaTest{

    private ScaleFactor scaleFactor;
    PowerTest powerTest;
    LoadHanaTest loadHanaTest;

    /**
     * TPCH benchmark initialization.
     * @param scaleFactor scale factor using in test.
     */
    TPCHBenchmark(ScaleFactor scaleFactor, int refreshDataIndex){
        this.scaleFactor = scaleFactor;
        powerTest = new PowerTest(scaleFactor,refreshDataIndex);
        loadHanaTest = new LoadHanaTest(scaleFactor,refreshDataIndex+1);
    }

    @Override
    public void run(BenchmarkProcessMonitor monitor) throws TestException {
        if(!DatabaseState.isDatabaseReady()) throw new TestException("Database was modified and now is not ready");
        powerTest.run(monitor);
        loadHanaTest.run(monitor);
    }

    @Override
    public TestResults.TPCHMetrics getResults() throws TestException {
        return new TestResults.TPCHMetrics(powerTest.getResults(),loadHanaTest.getResults(),scaleFactor);
    }

    @Override
    public String getTestName() {
        return "TPCH Benchmark";
    }
}
