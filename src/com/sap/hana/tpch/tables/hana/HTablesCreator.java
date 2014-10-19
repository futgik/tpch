package com.sap.hana.tpch.tables.hana;

import com.sap.hana.tpch.tables.ITableGenerator;
import com.sap.hana.tpch.types.ScaleFactor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Alex on 10.10.2014.
 */
public class HTablesCreator {

    public static Map<String,ITableGenerator> createAllTables(ScaleFactor scaleFactor){
        Map<String,ITableGenerator> tables = new LinkedHashMap<>();
        tables.put(HNCustomer.getClassTableName(HNCustomer.class),new HNCustomer(scaleFactor));
        tables.put(HNPart.getClassTableName(HNPart.class),new HNPart(scaleFactor));
        tables.put(HNSupplier.getClassTableName(HNSupplier.class),new HNSupplier(scaleFactor));
        tables.put(HNNation.getClassTableName(HNNation.class),new HNNation());
        tables.put(HNRegion.getClassTableName(HNRegion.class),new HNRegion());
        tables.put(HNPartSupp.getClassTableName(HNPartSupp.class),new HNPartSupp(scaleFactor,
                (HNPart)tables.get(HNPart.getClassTableName(HNPart.class)),
                (HNSupplier)tables.get(HNSupplier.getClassTableName(HNSupplier.class))));
        tables.put(HNOrders.getClassTableName(HNOrders.class),new HNOrders(scaleFactor,
                (HNCustomer)tables.get(HNCustomer.getClassTableName(HNCustomer.class))));
        tables.put(HNLineItem.getClassTableName(HNLineItem.class),new HNLineItem(scaleFactor,
                (HNOrders)tables.get(HNOrders.getClassTableName(HNOrders.class)),(HNPartSupp)tables.get(HNPartSupp.getClassTableName(HNPartSupp.class))));
        return tables;
    }
}
