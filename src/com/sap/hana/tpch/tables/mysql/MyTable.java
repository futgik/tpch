package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.tables.BaseTable;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 10.10.2014.
 */
public abstract class MyTable extends BaseTable {

    //SQL Query pattern to import data from file.
    static String SQLPattern = "LOAD DATA INFILE '"+ Configurations.SERVER_DBGEN_DIR+"/%s' INTO TABLE "
            +Configurations.SCHEMA_NAME+".%s FIELDS TERMINATED BY '|' ENCLOSED BY '\"' LINES TERMINATED BY '\\n'";

    public MyTable(ScaleFactor scaleFactor){
        super(scaleFactor);
    }

    /**
     * Get SQL script to export data from file to schema.
     * @return SQL script to export data from file to schema.
     */
    @Override
    public String getUploadingScript() {
        return String.format(SQLPattern,getGeneratedTableName(),getTableName());
    }

    abstract String getBaseCreationScript();

    /**
     * Get SQL script for creation table in schema.
     * @return SQL script for creation table in schema.
     */
    @Override
    public String getCreateScript() {
        return  String.format("CREATE TABLE %s ", getTableName())+getBaseCreationScript();
    }

    @Override
    public String getDeleteScript(){
        return String.format("DROP TABLE %s", getTableName());
    }
}
