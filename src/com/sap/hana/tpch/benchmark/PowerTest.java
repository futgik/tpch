package com.sap.hana.tpch.benchmark;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.LoadResultException;
import com.sap.hana.tpch.exception.TestException;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 01/10/2014.
 * Power test sequentially execute queries in specified order: 14,2,9,20,6,17,18,8,21,13,3,22,16,4,11,15,1,10,19,5,7,12
 */
public class PowerTest extends BaseTest {

    ScaleFactor scaleFactor;

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
    public void run(BenchmarkProcessMonitor monitor) throws TestException {
        super.run(monitor);
    }

    @Override
    public TestResults.PowerTestResults getResults() throws TestException{
        try {
            return new TestResults.PowerTestResults(getOutputFileName());

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
        return String.format("%s %d %s %d %s",Configurations.VALIDATION_QUERY, getScaleFactor().getScaleFactorValue(),
                Configurations.SERVER_DBGEN_DIR, refreshDataIndex, Configurations.SCHEMA_NAME);
    }
}
