package com.sap.hana.tpch.tables.mysql;

import com.sap.hana.tpch.tables.ITableGenerator;
import com.sap.hana.tpch.types.ScaleFactor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Alex on 10.10.2014.
 */
public class MySQLTablesCreator {

    public static Map<String,ITableGenerator> createAllTables(ScaleFactor scaleFactor) {
        Map<String,ITableGenerator> tables = new LinkedHashMap<>();
        tables.put(MyCustomer.getClassTableName(MyCustomer.class),new MyCustomer(scaleFactor));
        tables.put(MyPart.getClassTableName(MyPart.class),new MyPart(scaleFactor));
        tables.put(MySupplier.getClassTableName(MySupplier.class),new MySupplier(scaleFactor));
        tables.put(MyNation.getClassTableName(MyNation.class),new MyNation());
        tables.put(MyRegion.getClassTableName(MyRegion.class),new MyRegion());
        tables.put(MyPartSupp.getClassTableName(MyPartSupp.class),new MyPartSupp(scaleFactor,
                (MyPart)tables.get(MyPart.getClassTableName(MyPart.class)),
                (MySupplier)tables.get(MySupplier.getClassTableName(MySupplier.class))));
        tables.put(MyOrders.getClassTableName(MyOrders.class),new MyOrders(scaleFactor,
                (MyCustomer)tables.get(MyCustomer.getClassTableName(MyCustomer.class))));
        tables.put(MyLineItem.getClassTableName(MyLineItem.class),new MyLineItem(scaleFactor,
                (MyOrders)tables.get(MyOrders.getClassTableName(MyOrders.class)),
                (MyPartSupp)tables.get(MyPartSupp.getClassTableName(MyPartSupp.class))));
        return tables;
    }
}
