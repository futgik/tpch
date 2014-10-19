package com.sap.hana.tpch.db;

import com.sap.hana.tpch.config.Configurations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Alex on 09.10.2014.
 */
public class MySQLConnection {

    static boolean DriverInit = false;
    public static Connection connection = null;

    public static Connection getADMConnection() throws SQLException{
        return getConnection(Configurations.MYSQL_ADM_USER, Configurations.MYSQL_ADM_PWD);
    }

    private static Connection getConnection(String user, String password) throws SQLException{
        if(connection == null){
            loadDriver();
            connection = DriverManager.getConnection(String.format(Configurations.MYSQL_URL,Configurations.HOST, user,password));
        }
        return connection;
    }

    private static void loadDriver(){
        if(!DriverInit){
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                DriverInit = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
