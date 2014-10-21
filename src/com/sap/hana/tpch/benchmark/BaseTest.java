package com.sap.hana.tpch.benchmark;

import com.sap.hana.tpch.benchmark.results.TestResult;
import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.ExecutionException;
import com.sap.hana.tpch.exception.TestException;
import com.sap.hana.tpch.exec.RemoteExecutor;
import com.sap.hana.tpch.exec.SSHProcessMonitor;

/**
 * Created by Alex on 04/10/2014.
 * Default realisation of interface HanaTest.
 * This realisation using prepared bash script which executing on server and
 * write execution result to file with the same as execution script name and .log extension.
 */
public abstract class BaseTest implements HanaTest{

    protected BaseTest(){ }

    /**
     * process of run check if monitor available.
     * If it's not available just execute prepared file.
     * In another case execute file and output preparing result.
     * @param monitor monitor of process execution, maybe @null
     * @throws TestException Exception in process.
     */
    @Override
    public <T extends TestResult> T run(final BenchmarkProcessMonitor monitor) throws TestException {
        if(!isReadyToRun()) throw new TestException("Database was modified and now is not ready");
        String executionString = String.format("cd %s && ./%s.sh %s %s %s",
                Configurations.SERVER_TEST_DIR, getTestFileName(), getTestParams(), Configurations.TPCH_USER, Configurations.TPCH_PWD);
        try {
            if(monitor == null){
                return runWithoutMonitor(executionString);
            }
            monitor.prepare(getTestName());
            RemoteExecutor.getRemoteCommandExecutor().execCommand(executionString, new SSHProcessMonitor() {
                @Override
                public void init(String executionCommand) {
                    monitor.process(executionCommand);
                }

                @Override
                public void process(String processOutput) {
                    monitor.process(processOutput);
                }

                @Override
                public void end(String fullOutput) {
                    monitor.process("Command execution end.");
                }
            });
        } catch (ExecutionException e) {
            throw new TestException(String.format("error occurred while making %s", getTestName().toLowerCase()),e);
        }
        monitor.end();
        return getResults();
    }

    private <T extends TestResult> T runWithoutMonitor(String executionCommand) throws TestException{
        try {
            RemoteExecutor.getRemoteCommandExecutor().execCommand(executionCommand);
        } catch (ExecutionException e) {
            throw new TestException(String.format("error occurred while making %s", getTestName().toLowerCase()),e);
        }
        return getResults();
    }

    /**
     * Is initial condition of running test satisfied
     * Initial conditions of running is specific for different tests.
     * @return @true - if database ready for running specific test, @false - if database not ready.
     */
    public boolean isReadyToRun(){
        return DatabaseState.isDatabaseReady();
    }

    /**
     * Get name of script containing test.
     * @return name of script containing test.
     */
    public abstract String getTestFileName();

    /**
     * Get params using in bash scripts.
     * @return params using in bash scripts.
     */
    protected abstract String getTestParams();

    /**
     * Get name of file containing result of test execution.
     * @return file containing result of test execution.
     */
    public String getOutputFileName(){
        return getTestFileName()+".log";
    }
}
