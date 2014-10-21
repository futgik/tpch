package com.sap.hana.tpch.benchmark.results;

import com.sap.hana.tpch.exception.LoadResultException;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Alex on 07/10/2014.
*/
public class PowerTestResults extends BaseQueriesResult {
    public PowerTestResults(){
        super();
    }

    public PowerTestResults(String fileName) throws LoadResultException {
        super();
        loadFromFile(fileName);
    }

    /**
     * get list of all queries and its execution time.
     * @return list of queries and its execution time.
     */
    public List<Queries.PowerQueryTime> getQueryTimeList(Queries.QueryType queryType){
        List<Queries.PowerQueryTime> result = new ArrayList<>();
        for(Queries.Query q : queriesTime.keySet()){
            if(q.queryType == queryType)
                result.add(queriesTime.get(q));
        }
        return result;
    }
}
