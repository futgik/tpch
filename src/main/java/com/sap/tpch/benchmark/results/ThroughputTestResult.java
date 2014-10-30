package com.sap.tpch.benchmark.results;

/**
* Created by Alex on 07/10/2014.
 * Results providing from throughput test.
*/
public class ThroughputTestResult implements TestResult {
    /**
     * Time of test execution.
     */
    protected double getMaxQueryStreamTime;

    /**
     * not available default constructor.
     */
    protected ThroughputTestResult(){
    }

    /**
     * Return time of test execution.
     * @return time of test execution.
     */
    public double getGetMaxQueryStreamTime(){
        return getMaxQueryStreamTime;
    }

    /**
     * Return time of test execution.
     * @return time of test execution.
     */
    @Override
    public double getEstimation() {
        return getMaxQueryStreamTime;
    }
}
