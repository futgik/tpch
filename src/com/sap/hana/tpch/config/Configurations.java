package com.sap.hana.tpch.config;

import sun.security.krb5.Config;

import java.io.Console;

/**
 * Created by Alex on 18.09.2014.
 * Application configuration params.
 */
public class Configurations {

    //SSH connection settings.
    public static String SSH_USER = "hdbadm";
    public static String SSH_PWD = "hn123456";
    public static String HOST = "ILTLVL167";
    public static int SSH_PORT = 22;

//    public static String SSH_USER = "alex";
//    public static String SSH_PWD = "alex123";
//    public static String HOST = "192.168.56.1";
//    public static int SSH_PORT = 22;

    //TPCH test params
    //Indicate to generate data with random params if 0 or validation params if 1
    public static Integer VALIDATION_QUERY = 1;

    //Client deploy params
    //client directory where file for deploying keeping
    public static String CLIENT_DEPLOY_DIR = System.getProperty("user.dir")+"/deploy";
    //Archive file containing necessary files.
    public static String CLIENT_DEPLOY_ARCHIVE = CLIENT_DEPLOY_DIR+"/deploy.zip";
    //File containing code how to expand data.
    public static String CLIENT_DEPLOY_COMMAND = CLIENT_DEPLOY_DIR + "/deploy.sh";

    //Server deploy params
    //Directory on server which contain working files.
    public static String SERVER_WORK_DIR = "/hdb/tpch";
    //Directory on server where test scripts being kept.
    public static String SERVER_TEST_DIR = SERVER_WORK_DIR + "/tests";
    //Directory on server where dbgen program being kept.
    public static String SERVER_DBGEN_DIR = SERVER_WORK_DIR +"/dbgen";

    //get available disk size command.
    public static String CM_DISK_SIZE = "df \"%s\" | sed -n '2p' | awk -F \" \" '{print $4;}'";
    //check is directory exist: 1-yes, 0-no.
    public static String CM_CHECK_IS_DIR = "[ -d \"%s\" ] && echo 1 || echo 0";
    //check is file exist: 1-yes, 0-no.
    public static String CM_CHECK_IS_FILE = "[ -f \"%s\" ] && echo 1 || echo 0";
    //create nested directories.
    public static String CM_MK_DIR = "mkdir -p \"%s\"";
    //set rights to execute script.
    public static String CM_SET_EXEC_WRITES = "chmod 755 \"%s\"";
    //set rights 777
    public static String CM_SET_ALL_WRITES = "sudo chmod 777 \"%s\"";
    //generate data for tables using dbgen.
    public static String CM_DATA_GENERATOR = "cd "+SERVER_DBGEN_DIR+" && ./dbgen -vf -s %s";
    //generate data for table using in refresh function using dbgen.
    public static String CM_DATA_REFRESH_GENERATOR = "cd "+SERVER_DBGEN_DIR+" && ./dbgen -v -U %d -s %d";
    //get cpu count command.
    public static String CM_GET_CPU_COUNT = "grep --count processor /proc/cpuinfo";
    //get file content.
    public static String CM_READ_FILE_CONTENT = "cat %s";

    // Network Proxy - replace with your own network proxy or set the HAS_PROXY as false if you don't need to use proxy
    public static final boolean HAS_PROXY = true;
    public static final String PROXY_HOST = "proxy.blrl.sap.corp";
    public static final int PROXY_PORT = 8080;

    // HDB Connection Settings
    //JDBC connection url is "jdbc:sap://<host-id>:3 <instance no>15/?autocommit=false"
    //Assume that HANA Host ID is iltlvl167 and instance no is 00 then JDBC URL will be "jdbc:sap://iltlvl167:30015/?autocommit=false"
    public static final String HDB_URL = String.format("jdbc:sap://%s:30015/?autocommit=false",HOST);
    public static final String TPCH_USER = "TPCH";
    public static final String TPCH_PWD = "Hn1234567";
    public static final String ADM_USER = "SYSTEM";
    public static final String ADM_PWD = "manager";
    //Default HANA schema name
    public static String SCHEMA_NAME = "TPCH";

    //MySql Connection Settings
    public static final String MYSQL_ADM_USER = "root";
    public static final String MYSQL_ADM_PWD = "alex123";
    public static final String MYSQL_URL = "jdbc:mysql://%s/?user=%s&password=%s";

    public final static boolean DEBUG = java.lang.management.ManagementFactory.getRuntimeMXBean().
            getInputArguments().toString().contains("jdwp");
}
