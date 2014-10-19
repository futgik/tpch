package com.sap.hana.tpch.benchmark;

/**
 * Created by Alex on 07/10/2014.
 */
public class DatabaseState {

    static boolean databaseReady = false;

    public static void setReadyState(){
        databaseReady = true;
    }

    public static void resetState(){
        databaseReady = false;
    }

    public static boolean isDatabaseReady(){
        return databaseReady;
    }
}
