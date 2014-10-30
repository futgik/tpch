package com.sap.tpch.benchmark;

import com.sap.tpch.benchmark.results.TPCHMetrics;
import com.sap.tpch.benchmark.results.TPCHSeriesMetrics;
import com.sap.tpch.remoute.DatabaseState;
import com.sap.tpch.exception.TestException;
import com.sap.tpch.remoute.RefreshDataPreparation;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 20/10/2014.
 * Contain methods to execute tpch test several times.
 * Tests executed one after another. it's result averaging.
 */
public class TPCHSeriesBenchmark implements HanaTest{

    /**
     * Scale factor to carry out test.
     */
    private ScaleFactor scaleFactor;
    /**
     * Refresh part iterator to get actual refresh index.
     */
    RefreshDataPreparation.RefreshFilePart refreshFilePart;
    /**
     * How many times run test.
     */
    int repetitionCount;
    /**
     * Place to put results of test execution.
     */
    TPCHSeriesMetrics tpchSeriesMetrics;

    /**
     * TPCH benchmark initialization.
     * @param scaleFactor scale factor using in test.
     */
    TPCHSeriesBenchmark(ScaleFactor scaleFactor, int repetitionCount,RefreshDataPreparation.RefreshFilePart refreshDataIndex){
        this.scaleFactor = scaleFactor;
        this.repetitionCount = repetitionCount;
        this.refreshFilePart = refreshDataIndex;
    }

    @Override
    public TPCHSeriesMetrics run(BenchmarkProcessMonitor monitor) throws TestException {
        tpchSeriesMetrics = new TPCHSeriesMetrics();
        if(!DatabaseState.isDatabaseReady()) throw new TestException("Database was modified and now is not ready");
        for(int i=0;i<repetitionCount;i++) {
            int index = refreshFilePart.next();
            PowerTest powerTest = new PowerTest(scaleFactor, index);
            LoadHanaTest loadHanaTest = new LoadHanaTest(scaleFactor, index + 1);
            powerTest.run(monitor);
            loadHanaTest.run(monitor);
            tpchSeriesMetrics.addTPCHMetrics(new TPCHMetrics(powerTest.getResults(),loadHanaTest.getResults(),scaleFactor));
        }
        return tpchSeriesMetrics;
    }

    @Override
    public TPCHSeriesMetrics getResults() throws TestException {
        return tpchSeriesMetrics;
    }

    @Override
    public String getTestName() {
        return "TPCH series benchmark";
    }
}
