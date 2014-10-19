package com.sap.hana.tpch.exec;

/**
 * Created by Alex on 24.09.2014.
 * Interface implement three method which allow monitor of process command execution on remote computer.
 */
public interface SSHProcessMonitor {

    /**
     * Calling before starting of command execution.
     * @param executionCommand Execution command
     */
    public void init(String executionCommand);

    /**
     * Calling in process of command execution
     * after each state changing of output stream.
     * @param processOutput new state of output stream.
     */
    public void process(String processOutput);

    /**
     * Call after execution remote command.
     * @param fullOutput full output after command execution.
     */
    public void end(String fullOutput);
}
