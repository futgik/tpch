package com.sap.tpch.exception;

/**
 * Created by Alex on 21/09/2014.
 */
public class PrepException extends java.lang.Exception{
    public PrepException() {
        super();
    }

    public PrepException(java.lang.Throwable e) {
        super(e);
    }

    public PrepException(String message){
        super(message);
    }

    public  PrepException(String message, java.lang.Throwable e){
        super(message, e);
    }
}
