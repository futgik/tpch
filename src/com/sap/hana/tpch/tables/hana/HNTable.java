package com.sap.hana.tpch.tables.hana;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.tables.BaseTable;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 22/09/2014.
 */
public abstract class HNTable extends BaseTable {

    static String PARTITION = " PARTITION BY HASH (%s) PARTITIONS %d";

    //SQL Query pattern to import data from file.
    static String SQLPattern = "IMPORT FROM CSV FILE '"+ Configurations.SERVER_DBGEN_DIR+"/%s' INTO \""
            +Configurations.SCHEMA_NAME+"\".\"%s\" WITH RECORD DELIMITED BY '\\n' FIELD DELIMITED BY '|' THREADS "
            + HNCreatingScriptsService.getThreadsCount()+" BATCH "+ HNCreatingScriptsService.getBatchCount();

    private HNTable(){
        super();
    }

    public HNTable(ScaleFactor scaleFactor){
        super(scaleFactor);
    }

    abstract String getBaseCreationScript();

    abstract String partitionFieldName();

    /**
     * Get SQL script for creation table in schema.
     * @return SQL script for creation table in schema.
     */
    @Override
    public String getCreateScript(){
        int portionCount = HNCreatingScriptsService.getTablePartitionCount(getScaleFactor(), getBaseRowsNumber());
        String partitionString = "";
        if(portionCount>1){
            partitionString = String.format(PARTITION, partitionFieldName(),portionCount);
        }
        return  String.format("CREATE COLUMN TABLE %s ", getTableName())+getBaseCreationScript()+partitionString;
    }

    @Override
    public String getDeleteScript(){
        return String.format("DROP TABLE %s", getTableName());
    }

    /**
     * Get SQL script to export data from file to schema.
     * @return SQL script to export data from file to schema.
     */
    @Override
    public String getUploadingScript(){
        return String.format(SQLPattern,getGeneratedTableName(),getTableName());
    }
}
