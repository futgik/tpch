package com.sap.tpch.remoute;

/**
 * Created by Alex on 21/10/2014.
 * Contain possible database states.
 */
public enum  DatabaseStateType {
    /**
     * Database state not known.
     */
    UNKNOWN,
    /**
     * Database or some parts of database(tables) not exist.
     */
    NOT_EXIST,
    /**
     * Database and all parts of database exists, but database not ready to test.
     * This may be because there is no data in database or database not load into meamory.
     */
    EXIST,
    /**
     * Database and all parts of database exists, but database not ready to test.
     * This may be because for update function all data were used.
     */
    NOT_READY,
    /**
     * Database exist all data exist and actual, and database ready to test.
     */
    READY,
}
