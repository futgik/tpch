package com.sap.tpch.db_interaction;

import com.sap.tpch.config.TPCHConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Alex on 16/09/2014.
 * Connection to tables database.
 */
public class HDBConnection {

    public static Connection connection = null;

    /**
     * Get connection to tables database using admin rights.
     * @return connection to tables database.
     */
    public static Connection getADMConnection() throws SQLException{
        return getConnection(TPCHConfig.ADM_USER, TPCHConfig.ADM_PWD);
    }

    private static Connection getConnection(String user, String password) throws SQLException{
        if(connection == null){
            try {
                Class.forName("com.sap.db.jdbc.Driver");
            }catch(ClassNotFoundException ignored){}
            connection = DriverManager.getConnection(TPCHConfig.HDB_URL,user, password);
        }
        return connection;
    }

    public static void main(String[] argv) throws ClassNotFoundException {
    }
}
