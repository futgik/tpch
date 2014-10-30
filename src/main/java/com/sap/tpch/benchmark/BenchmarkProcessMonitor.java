package com.sap.tpch.benchmark;

/**
 * Created by Alex on 05/10/2014.
 * Interface implement three method which allow monitor test execution process.
 */
public interface BenchmarkProcessMonitor{

    /**
     * Calling before starting test.
     * @param testName name of starting test
     */
    public void prepare(String testName);

    /**
     * Calling in process of test execution
     * after each state changing of output stream.
     * @param processOutput new state of output stream.
     */
    public void process(String processOutput);

    /**
     * Call after execution remote command.
     */
    public void end();
}
