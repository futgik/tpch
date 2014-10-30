package com.sap.tpch.benchmark;

import com.sap.tpch.benchmark.results.TestResult;
import com.sap.tpch.exception.TestException;

/**
 * Created by Alex on 01/10/2014.
 * Define interface for carrying out tpch test.
 */
public interface HanaTest {

    /**
     * Run test.
     * @throws TestException exception in process of test execution.
     */
    public <T extends TestResult> T run(BenchmarkProcessMonitor monitor) throws TestException;

    /**
     * Get test results.
     * @return Results containing common data defined in interface TestResult.
     * Each implementation of interface can return specific data type which implement TestResult.
     * @throws TestException Exception while preparing test result;
     */
    public <T extends TestResult> T getResults() throws TestException;

    /**
     * Get name of tpch test.
     * @return Name of tpch test.
     */
    public String getTestName();

}
