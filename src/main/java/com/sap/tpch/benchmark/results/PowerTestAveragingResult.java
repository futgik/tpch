package com.sap.tpch.benchmark.results;

import java.util.List;

/**
 * Created by Alex on 25/10/2014.
 * Creating result of power test using averaging of list of power test results.
 */
public class PowerTestAveragingResult extends PowerTestResult {

    /**
     * Initialize using list of power test results.
     * Provide result by averaging all timing of all queries in list of test results.
     * @param results averaging list of test results.
     */
    public PowerTestAveragingResult(List<PowerTestResult> results){
        super();
        setQueriesAverageTime(results);
    }

    /**
     * Averaging procedure.
     * @param results averaging list of test results.
     */
    private void setQueriesAverageTime(List<PowerTestResult> results){
        for(Queries.Query q :Queries.getAvailableQueries()){
            queriesTime.put(q, new Queries.PowerQueryTime(q,0.0));
        }
        for(PowerTestResult result : results){
            for(Queries.QueryTime pqTime : result.getQueryTimeList()){
                queriesTime.put(pqTime.getQuery(),
                        new Queries.PowerQueryTime(pqTime.getQuery(),
                                queriesTime.get(pqTime.getQuery()).getInitialQueryTime()+
                                        pqTime.getInitialQueryTime()/results.size()));
            }
        }
        queriesTime.values().iterator().next().setLongestQuery(getLongestQuery());
    }
}
