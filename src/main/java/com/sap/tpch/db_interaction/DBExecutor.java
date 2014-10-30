package com.sap.tpch.db_interaction;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Alex on 28/09/2014.
 * Interface to communicate with database.
 */
public interface DBExecutor {

    /**
     * execute query which return results;
     * @param query executing query.
     * @return set of rows.
     * @throws SQLException sql execution exception.
     */
    public ResultSet executeQuery(String query) throws SQLException;

    /**
     * execute query which not return results;
     * @param query executing query.
     * @return @true - query executed successfully, @false - another case.
     * @throws SQLException sql execution exception.
     */
    public boolean executeNoResultQuery(String query) throws SQLException;
}
