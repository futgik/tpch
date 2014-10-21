package com.sap.hana.tpch.benchmark.results;

import com.sap.hana.tpch.exception.GetQueryException;
import com.sap.hana.tpch.exception.LoadResultException;
import com.sap.hana.tpch.service.Statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 07/10/2014.
 * Contain some common test methods and some common implementation
 */
abstract class BaseQueriesResult implements TestResult {

    public BaseQueriesResult(){
        queriesTime = new HashMap<>();
    }

    /**
     * contain query and result of query execution.
     */
    Map<Queries.Query,Queries.PowerQueryTime> queriesTime;

    /**
     * Get time of most slower query.
     * @return time in seconds of most lower query.
     */
    public double getMaxQueryTime(){
        return Statistic.getMax(TestResults.getPowerQueryArray(queriesTime.values()));
    }

    /**
     * Get time of most faster query.
     * @return time in seconds of most lower query.
     */
    public double getMinQueryTime(){
        return Statistic.getMin(TestResults.getPowerQueryArray(queriesTime.values()));
    }

    /**
     * get full time execution of all queries.
     * @return time in seconds of most lower query.
     */
    public double getSumQueryTime(){
        return Statistic.getSum(TestResults.getPowerQueryArray(queriesTime.values()));
    }

    /**
     * get time relation of the most slowest query to the most festers query.
     * @return ratio.
     */
    public double getRatioQueryTime(){
        return getMaxQueryTime()/getMinQueryTime();
    }

    /**
     * get decimal logarithm of sum query time.
     * @return decimal loarithm of summ query time.
     */
    public double getSumLnQueryTime(){
        return Statistic.getSum(TestResults.getLnPowerQueryArray(queriesTime.values()));
    }

    /**
     * get list of all queries and its execution time.
     * @return list of queries and its execution time.
     */
    public List<Queries.PowerQueryTime> getQueryTimeList(){
        return new ArrayList<>(queriesTime.values());
    }

    /**
     * Load results from file name.
     * File contain cvs structure without header and contain two columns.
     * first column is query name and second is execution time.
     * columns divided using ";".
     * @param fileName name of parse file.
     * @throws com.sap.hana.tpch.exception.LoadResultException
     */
    public void loadFromFile(String fileName) throws LoadResultException {
        String fileContent = TestResults.getTestResultContent(fileName);
        String queriesRes[] = fileContent.split("\\n");
        for(String queryResRow : queriesRes){
            String queryRes[] = queryResRow.split(";");
            try {
                queriesTime.put(Queries.getQueryByFileName(queryRes[0]),
                        new Queries.PowerQueryTime(Queries.getQueryByFileName(queryRes[0]), Integer.parseInt(TestResults.clearString(queryRes[1]))));
            }
            catch (GetQueryException e) {
                throw new LoadResultException("Can't load results from file", e);
            }
        }
    }

    /**
     * default estimation is sum of time of all queries.
     * @return summ of time of all queries.
     */
    @Override
    public double getEstimation(){
        return getSumLnQueryTime();
    }
}
