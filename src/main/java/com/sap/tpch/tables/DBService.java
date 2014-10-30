package com.sap.tpch.tables;

/**
 * Created by Alex on 21/10/2014.
 */
public class DBService {
    /**
     * Get name of table base on Class name.
     * Table class name is __[some_table_name]. Method get some_table_name and return it.
     * @param className name of class starting with T...
     * @return Name of table.
     */
    public static String getTableName(Class className){
        String[] arr = className.getName().split("\\.");
        if(arr.length<2) return "";
        return arr[arr.length-1].substring(2).toUpperCase();
    }
}
