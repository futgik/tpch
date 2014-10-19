package com.sap.hana.tpch.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Alex on 28/09/2014.
 * Get opportunity to get connection for different databases
 */
public class DBConnection {

    /**
     * Get connection for different databases
     * @param connectionType type of database.
     * @return connection (session) with a specific database
     *
     */
    static Connection GetDBConnection(DBType connectionType) throws SQLException{
        switch (connectionType){
            case HDB:
                return HDBConnection.getADMConnection();
            case MYSQL:
                return MySQLConnection.getADMConnection();
        }
        return null;
    }
}
