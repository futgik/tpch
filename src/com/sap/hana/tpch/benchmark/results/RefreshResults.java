package com.sap.hana.tpch.benchmark.results;

import com.sap.hana.tpch.exception.LoadResultException;

/**
* Created by Alex on 07/10/2014.
*/
public class RefreshResults extends BaseQueriesResult{
    public RefreshResults(){
        super();
    }

    public RefreshResults(String fileName) throws LoadResultException {
        super();
        loadFromFile(fileName);
    }
}
