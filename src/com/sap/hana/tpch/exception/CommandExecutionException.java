package com.sap.hana.tpch.exception;

/**
 * Created by Alex on 01.10.2014.
 */
public class CommandExecutionException extends ExecutionException {
    int errorCode;

    public CommandExecutionException(String message, int errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public CommandExecutionException(String message, int errorCode, Throwable t){
        super(message,t);
        this.errorCode = errorCode;
    }
}
