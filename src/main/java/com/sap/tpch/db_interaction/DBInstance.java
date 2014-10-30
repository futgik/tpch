package com.sap.tpch.db_interaction;

import com.sap.tpch.config.TPCHConfig;

import java.io.Closeable;
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
     * Map with name of databases and instances accordingly.
     */
    static Map<DBType,SingleInstance> instance = new HashMap<>();

    @Override
    public void close(){
        closeAllConnection();
    }

    /**
     * Contain single connection to one database.
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
            try {
                int result = statement.executeUpdate(query);
                connection.commit();
                return  result < 1;
            }catch (SQLException e){
                connection.rollback();
                throw  e;
            }
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
     * Not available constructor without params.
     */
    private DBInstance(){}

    /**
     * Get instance of specified type.
     * @return instance of specified type.
     */
    public static DBExecutor getDBInstance() throws SQLException{
        if(instance.containsKey(TPCHConfig.DATABASE_TYPE))
            return instance.get(TPCHConfig.DATABASE_TYPE);
        else {
            SingleInstance si = new SingleInstance(TPCHConfig.DATABASE_TYPE);
            instance.put(TPCHConfig.DATABASE_TYPE,si);
            return si;
        }
    }

    /**
     * Close all opened connections
     */
    public static void closeAllConnection(){
        for (DBType dbType : instance.keySet()) {
            instance.get(dbType).close();
            instance.remove(dbType);
        }
    }
}
