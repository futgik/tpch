package com.sap.tpch.exception;

import com.sap.tpch.remoute.DatabaseStateType;

/**
 * Created by Alex on 22/10/2014.
 */
public class DatabaseStateException extends ExecutionException  {
    DatabaseStateType dbType;

    public DatabaseStateException() {
        super();
    }

    public DatabaseStateException(String message) {
        super(message);
    }

    public DatabaseStateException(DatabaseStateType dbType) {
        this.dbType = dbType;
    }

    public  DatabaseStateException(String message, DatabaseStateType dbType){
        super(message);
        this.dbType = dbType;
    }

    public  DatabaseStateException(String message, DatabaseStateType dbType, java.lang.Throwable e){
        super(message,e);
        this.dbType = dbType;
    }

    public DatabaseStateType getDBType(){
        return dbType;
    }
}
