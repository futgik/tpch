package com.sap.tpch.exception;

/**
 * Created by Alex on 22/09/2014.
 */
public class ExecutionException extends java.lang.Exception{
    public ExecutionException() {
        super();
    }

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(java.lang.Throwable e) {
        super(e);
    }

    public  ExecutionException(String message, java.lang.Throwable e){
        super(message, e);
    }
}
