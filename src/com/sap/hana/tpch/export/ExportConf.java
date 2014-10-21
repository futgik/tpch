package com.sap.hana.tpch.export;

/**
 * Created by Alex on 19/10/2014.
 * Contains settings of different export parameters.
 */
public class ExportConf {

    /**
     * template file which use to export tpch test results.
     */
    public static String CLIENT_TPCH_TEMPLATE_FILE = System.getProperty("user.dir")+"/templates/TPCH.xlsx";
    /**
     * template file which use to export tpch series test results.
     */
    public static String CLIENT_TPCH_SERIES_TEMPLATE_FILE = System.getProperty("user.dir")+"/templates/TPCH_series.xlsx";
    /**
     * export directory path where results of export will be pushed.
     */
    public static String EXPORT_PATH = System.getProperty("user.dir")+"/export/";
    /**
     * Name of single sheet.
     */
    public static String TEMPLATE_SHEET_NAME = "TPC-H Calculations";

}
