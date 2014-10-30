package com.sap.tpch.benchmark.results;

/**
 * Created by Alex on 07/10/2014.
 * Contain common methods for working with different tests.
 */
public interface TestResult {

    /**
     * get some numerical estimation of executed test.
     * @return numerical estimation of test.
     */
    public double getEstimation();
}
