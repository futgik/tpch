package com.sap.hana.tpch.exception;

/**
 * Created by Alex on 04/10/2014.
 */
public class GetQueryException extends java.lang.Exception {
   public GetQueryException(){
       super();
   }

    public GetQueryException(String message){
        super(message);
    }
}
