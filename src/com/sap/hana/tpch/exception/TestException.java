package com.sap.hana.tpch.exception;

/**
 * Created by Alex on 02/10/2014.
 */
public class TestException extends Exception {

    public TestException() {
        super();
    }

    public TestException(java.lang.Throwable e) {
        super(e);
    }

    public TestException(String message){
        super(message);
    }

    public  TestException(String message, java.lang.Throwable e){
        super(message, e);
    }
}
