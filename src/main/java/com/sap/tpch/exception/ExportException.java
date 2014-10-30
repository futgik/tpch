package com.sap.tpch.exception;

/**
 * Created by Alex on 19/10/2014.
 */
public class ExportException extends Exception {
    public ExportException() {
        super();
    }

    public ExportException(String message) {
        super(message);
    }

    public ExportException(java.lang.Throwable e) {
        super(e);
    }

    public  ExportException(String message, java.lang.Throwable e){
        super(message, e);
    }
}
