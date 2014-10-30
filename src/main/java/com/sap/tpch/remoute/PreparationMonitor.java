package com.sap.tpch.remoute;

/**
 * Created by Alex on 28/09/2014.
 * Allow to monitor preparation process.
 */
public interface PreparationMonitor {

    /**
     * Calling before starting of preparation.
     * @param startMessage Execution command
     */
    public void init(String startMessage);

    /**
     * Calling in process of command execution
     * after each state changing of output stream.
     * @param processOutput new state of output stream.
     */
    public void process(String processOutput);

    /**
     * Call after end of preparation.
     * @param endMessage output after command execution end.
     */
    public void end(String endMessage);
}
