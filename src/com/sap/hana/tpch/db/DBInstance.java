package com.sap.hana.tpch.db;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 28/09/2014.
 */
public class DBInstance implements Closeable{

    /**
     * map with instances and name of databases accordingly.
     */
    static Map<DBType,SingleInstance> instance = new HashMap<>();

    @Override
    public void close(){
        closeAllConnection();
    }

    /**
     * contain single connection to one database.
     */
    private static class SingleInstance implements DBExecutor, Closeable{
        //Connection to database
        Connection connection = null;

        /**
         * Create connection to specified database.
         * @param connectionType type of connection database.
         */
        public SingleInstance(DBType connectionType) throws SQLException{
            try {
                connection = DBConnection.GetDBConnection(connectionType);
            }catch (SQLException e){
                connection = null;
                throw e;
            }
        }

        @Override
        public ResultSet executeQuery(String query) throws SQLException{
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        }

        @Override
        public boolean executeNoResultQuery(String query) throws SQLException{
            Statement statement = connection.createStatement();
            return statement.execute(query);
        }

        /**
         * close database connection.
         */
        private void closeConnection(){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }

        @Override
        public void close(){
            closeConnection();
        }
    }

    /**
     * not available constructor without params.
     */
    private DBInstance(){}

    /**
     * get instance of specified type.
     * @param connectionType type of database connection.
     * @return instance of specified type.
     */
    public static DBExecutor getDBInstance(DBType connectionType) throws SQLException{
        if(instance.containsKey(connectionType))
            return instance.get(connectionType);
        else {
            SingleInstance si = new SingleInstance(connectionType);
            instance.put(connectionType,si);
            return si;
        }
    }

    /**
     * close all opened connections
     */
    public static void closeAllConnection(){
        for (DBType dbType : instance.keySet()) {
            instance.get(dbType).close();
            instance.remove(dbType);
        }
    }
}
