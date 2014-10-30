package com.sap.tpch.benchmark;

import com.sap.tpch.benchmark.results.PowerTestImportResult;
import com.sap.tpch.benchmark.results.PowerTestResult;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.exception.LoadResultException;
import com.sap.tpch.exception.TestException;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 01/10/2014.
 * Power test sequentially execute queries in specified order: 14,2,9,20,6,17,18,8,21,13,3,22,16,4,11,15,1,10,19,5,7,12
 */
public class PowerTest extends BaseTest {

    /**
     * Scale factor value to carry out test.
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
    PowerTest(ScaleFactor scaleFactor, int refreshDataIndex){
        super();
        this.scaleFactor = scaleFactor;
        this.refreshDataIndex = refreshDataIndex;
    }

    @Override
    public PowerTestResult run(BenchmarkProcessMonitor monitor) throws TestException {
        return super.run(monitor);
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
        return "Power test";
    }

    @Override
    public String getTestFileName(){
        return "power_test";
    }

    private ScaleFactor getScaleFactor(){
        return scaleFactor;
    }

    @Override
    public String getTestParams() {
        return String.format("%s %d %s %d %s", TPCHConfig.VALIDATION_QUERY, getScaleFactor().getScaleFactorValue(),
                TPCHConfig.SERVER_DBGEN_DIR, refreshDataIndex, TPCHConfig.SCHEMA_NAME);
    }
}
