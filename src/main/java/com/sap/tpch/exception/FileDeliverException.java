package com.sap.tpch.exception;

/**
 * Created by Alex on 22/09/2014.
 */
public class FileDeliverException extends java.lang.Exception{
    public FileDeliverException() {
        super();
    }

    public FileDeliverException(String message) {
        super(message);
    }

    public FileDeliverException(java.lang.Throwable e) {
        super(e);
    }

    public  FileDeliverException(String message, java.lang.Throwable e){
        super(message, e);
    }
}
