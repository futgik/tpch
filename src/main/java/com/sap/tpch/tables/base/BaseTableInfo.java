package com.sap.tpch.tables.base;

/**
 * Created by Alex on 11.10.2014.
 * Constance methods with base for all database information about the table.
 */
public interface BaseTableInfo {

    /**
     * Get base count of rows which table contain.
     * @return base count of rows which table contain.
     */
    public int getBaseRowsNumber();

    /**
     * Get field name using for partition creation
     * @return field name using for partition creation.
     */
    public String getPartitionFieldName();
}
