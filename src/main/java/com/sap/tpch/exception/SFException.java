package com.sap.tpch.exception;

/**
 * Created by Alex on 20.09.2014.
 */
public class SFException extends java.lang.Exception {

    public SFException() {
        super();
    }

    public SFException(java.lang.Throwable e) {
        super(e);
    }

    public SFException(String message) {
        super(message);
    }

    public SFException(String message, java.lang.Throwable e) {
        super(message, e);
    }

}
