package com.sap.tpch.benchmark.results;

import com.sap.tpch.service.Statistic;

import java.util.*;

/**
 * Created by Alex on 25/10/2014.
 * Provide results of power test.
 */
public class PowerTestResult implements TestResult {

    /**
     * Contain query and result of query execution.
     */
    protected Map<Queries.Query,Queries.PowerQueryTime> queriesTime;

    protected PowerTestResult(){
        queriesTime = new HashMap<>();
    }


    /**
     * Get query with most bigger query time.
     * @return query with most bigger query time.
     */
    public Queries.PowerQueryTime getLongestQuery(){
        Collection<Queries.PowerQueryTime> c = queriesTime.values();
        Queries.PowerQueryTime result = c.iterator().next();
        for(Queries.PowerQueryTime pqt : c){
            if(result.compareTo(pqt) < 0){
                result = pqt;
            }
        }
        return result;
    }

    /**
     * Get query with shortest query time.
     * @return query with shortest query time.
     */
    public Queries.PowerQueryTime getFestersQuery(){
        Collection<Queries.PowerQueryTime> c = queriesTime.values();
        Queries.PowerQueryTime result = c.iterator().next();
        for(Queries.PowerQueryTime pqt : c){
            if(result.compareTo(pqt) > 0){
                result = pqt;
            }
        }
        return result;
    }

    /**
     * get full time execution of all queries.
     * @return time in seconds of most lower query.
     */
    public double getSumQueryTime(){
        return Statistic.getSum(getPowerQueryArray(queriesTime.values()));
    }

    private static Iterable<Double> getPowerQueryArray(Iterable<Queries.PowerQueryTime> powerQueryTimeArray){
        List<Double> result= new ArrayList<>();
        for(Queries.PowerQueryTime t : powerQueryTimeArray){
            result.add(t.getTime());
        }
        return result;
    }

    /**
     * Get time relation of the most slowest query to the most festers query.
     * @return ratio.
     */
    public double getRatioQueryTime(){
        return getLongestQuery().getInitialQueryTime()/getFestersQuery().getInitialQueryTime();
    }

    /**
     * Get decimal logarithm of sum query time.
     * @return decimal loarithm of summ query time.
     */
    public double getSumLnQueryTime(){
        return Statistic.getSum(getLnPowerQueryArray(queriesTime.values()));
    }

    private Iterable<Double> getLnPowerQueryArray(Iterable<Queries.PowerQueryTime> powerQueryTimeArray){
        List<Double> result= new ArrayList<>();
        for(Queries.PowerQueryTime t : powerQueryTimeArray){
            result.add(t.getLnTime());
        }
        return result;
    }

    /**
     * Get list of all queries and its execution time.
     * List sorted by increase query number.
     * @return list of queries and its execution time.
     */
    public List<Queries.QueryTime> getQueryTimeList(){
        List<Queries.QueryTime> result = new ArrayList<>();
        for(Queries.QueryTime q : queriesTime.values())
            result.add(q);
        Collections.sort(result);
        return result;
    }

    /**
     * Default estimation is sum of time of all queries.
     * @return summ of time of all queries.
     */
    @Override
    public double getEstimation(){
        return getSumLnQueryTime();
    }

    /**
     * Get list of all queries and its execution time.
     * List sorted by increase query number.
     * @return list of queries and its execution time.
     */
    public SortedMap<Queries.Query,Queries.PowerQueryTime> getQueryTime(Queries.QueryType queryType){
        SortedMap<Queries.Query,Queries.PowerQueryTime> result = new TreeMap<>();
        for(Queries.Query q : queriesTime.keySet()){
            if(q.queryType == queryType)
                result.put(q,queriesTime.get(q));
        }
        return result;
    }
}
