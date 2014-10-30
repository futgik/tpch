package com.sap.tpch.tables.hana;

import com.sap.tpch.tables.DBService;
import com.sap.tpch.tables.ITableGenerator;
import com.sap.tpch.types.ScaleFactor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Alex on 10.10.2014.
 */
public class HTablesCreator {

    /**
     * Create all tables using scale factor.
     * @param scaleFactor scale factor;
     * @return map with table name and table generator.
     */
    public static Map<String,ITableGenerator> createAllTables(ScaleFactor scaleFactor){
        Map<String,ITableGenerator> tables = new LinkedHashMap<>();
        tables.put(DBService.getTableName(HNCustomer.class),new HNCustomer(scaleFactor));
        tables.put(DBService.getTableName(HNPart.class),new HNPart(scaleFactor));
        tables.put(DBService.getTableName(HNSupplier.class),new HNSupplier(scaleFactor));
        tables.put(DBService.getTableName(HNNation.class),new HNNation());
        tables.put(DBService.getTableName(HNRegion.class),new HNRegion());
        tables.put(DBService.getTableName(HNPartSupp.class),new HNPartSupp(scaleFactor,
                (HNPart)tables.get(DBService.getTableName(HNPart.class)),
                (HNSupplier)tables.get(DBService.getTableName(HNSupplier.class))));
        tables.put(DBService.getTableName(HNLineItem.class),new HNLineItem(scaleFactor,
                (HNPartSupp)tables.get(DBService.getTableName(HNPartSupp.class))));
        tables.put(DBService.getTableName(HNOrders.class),new HNOrders(scaleFactor,
                (HNLineItem)tables.get(DBService.getTableName(HNLineItem.class)),
                (HNCustomer)tables.get(DBService.getTableName(HNCustomer.class))));
        tables.put(DBService.getTableName(HNRefreshIndex.class),new HNRefreshIndex());
        return tables;
    }
}
