package com.sap.tpch.tables.base;

import com.sap.tpch.tables.BaseTable;

/**
 * Created by Alex on 22/09/2014.
 * Constance PartSupp table facts base for all database
 */
public class BTPartSupp<P extends BaseTable,S extends BaseTable> implements BaseTableInfo{

    /**
     * Database specific realization of part table.
     */
    P partTable;
    /**
     * Database specific realization of supplier table.
     */
    S supplierTable;

    /**
     * Constructor.
     * @param part realization of part table form which partSupp depend.
     * @param supplier realization of supplier table from which partSupp depend.
     */
    public BTPartSupp(P part, S supplier){
        partTable = part;
        supplierTable = supplier;
    }

    @Override
    public int getBaseRowsNumber() {
        return 800000;
    }

    @Override
    public String getPartitionFieldName() {
        return "ps_partkey";
    }

    /**
     * get realization of part table.
     * @return realization of part table from which partSupp depend.
     */
    public P getPartTable(){
        return partTable;
    }

    /**
     * get realization of supplier table.
     * @return realization of supplier table from which partSupp depend.
     */
    public S getSupplierTable(){
        return supplierTable;
    }
}
