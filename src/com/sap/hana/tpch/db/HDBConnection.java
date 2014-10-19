package com.sap.hana.tpch.db;

import com.sap.hana.tpch.config.Configurations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sap.hana.tpch.db.DBExecutor;
import com.sap.hana.tpch.db.DBInstance;
import com.sap.hana.tpch.db.DBType;

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
        return getConnection(Configurations.ADM_USER, Configurations.ADM_PWD);
    }

    private static Connection getConnection(String user, String password) throws SQLException{
        if(connection == null){
            connection = DriverManager.getConnection(Configurations.HDB_URL,user, password);
        }
        return connection;
    }

    public static void main(String[] argv) throws ClassNotFoundException {
    }
}
