package com.sap.hana.tpch.exception;

/**
 * Created by Alex on 04/10/2014.
 */
public class LoadResultException extends Exception{
    public LoadResultException() {
        super();
    }

    public LoadResultException(java.lang.Throwable e) {
        super(e);
    }

    public LoadResultException(String message){
        super(message);
    }

    public  LoadResultException(String message, java.lang.Throwable e){
        super(message, e);
    }

}
