package com.sap.tpch.benchmark.results;

import com.sap.tpch.exception.GetQueryException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 02.10.2014.
 * Contain list of all available queries, using in benchmark test.
 * include 22 queries + 2 refresh functions.S
 */
public class Queries {

    /**
     * Number of using queries.
     */
    private static final int QUERY_NUMBERS = 22;

    /**
     * List of available queries.
     */
    private static final List<Query> queries = generateAllQueries();

    /**
     * Queries initialization.
     */
    public Queries(){
    }

    /**
     * Return list of all available queries.
     * @return list of all available queries.
     */
    private static List<Query> generateAllQueries(){
        List<Query> result = new ArrayList<>(QUERY_NUMBERS);
        for(int i = 1; i<=QUERY_NUMBERS;i++){
            result.add(new Query("Query "+String.valueOf(i),"q"+String.valueOf(i),QueryType.QUERY));
        }
        result.add(new Query("Refresh 1","rf1",QueryType.REFRESH));
        result.add(new Query("Refresh 2","rf2",QueryType.REFRESH));
        return result;
    }


    public static List<Query> getAvailableQueries(){
        return queries;
    }


    /**
     * Try to find query by specified name.
     * @param queryName name of query to find.
     * @return query object with queryName.
     * @throws GetQueryException query wasn't found.
     */
    public static Query getQueryByName(String queryName) throws GetQueryException {
        for(Query query : queries){
            if(query.getQueryName().toLowerCase().equals(queryName.toLowerCase()))
                return query;
        }
        throw new GetQueryException(String.format("Query with name %s not exist.", queryName));
    }

    /**
     * Try to find query by specified file name
     * @param queryFileName file name of required query.
     * @return query object with queryFileName.
     * @throws GetQueryException query wasn't found.
     */
    public static Query getQueryByFileName(String queryFileName) throws GetQueryException {
        for(Query query : queries){
            if(query.getQueryFileName().equals(queryFileName.toLowerCase()))
                return query;
        }
        throw new GetQueryException(String.format("Query with file name %s not exist.", queryFileName));
    }

    /**
     * Get list of available queries.
     * @return list of available queries.
     */
    public static List<Query> getQueries(){
        return queries;
    }

    public static List<Query> getQueriesByType(QueryType queryType){
        List<Query> result = new ArrayList<>();
        for(Query query : queries){
            if (query.queryType==queryType)
                result.add(query);
        }
        return result;
    }

    /**
     * Represent type of query.
     * There are two types usual query an refresh query.
     */
    public static enum QueryType{
        /**
         * Select query type.
         */
        QUERY,
        /**
         * Modify something query type.
         */
        REFRESH
    }

    /**
     * Single query object
     */
    public static class Query implements Comparable<Query>{

        /**
         * name of query.
         */
        String queryName;

        /**
         * type of query.
         */
        QueryType queryType;

        /**
         * name of file containing query.
         * File has .sql extension.
         */
        String queryFileName;

        /**
         * Initialize query.
         * @param queryName query name.
         * @param queryFileName query file name.
         */
        private Query(String queryName, String queryFileName, QueryType queryType){
            this.queryName = queryName;
            this.queryFileName = queryFileName;
            this.queryType = queryType;
        }

        /**
         * get name of file which contain result of query execution.
         * file may not be exist if query not execute.
         * @return name of file which contain result of query execution.
         */
        public String getFetchResultFileName(){
            return "output"+queryName;
        }

        /**
         * Initialize query.
         * @param queryName query name the same as file name.
         */
        private Query(String queryName){
            this.queryName = this.queryFileName = queryName;
        }

        /**
         * Get name of query.
         * @return name of query.
         */
        public String getQueryName() {
            return queryName;
        }

        /**
         * Get name of query file name.
         * @return name of query file name.
         */
        public String getQueryFileName(){
            return queryFileName;
        }

        /**
         * Get type of query.
         * @return query type.
         */
        public QueryType getQueryType() {
            return queryType;
        }

        @Override
        public String toString(){
            return queryName;
        }

        @Override
        public int compareTo(Query o) {
            int r = getQueryText().compareTo(o.getQueryText());
            if(r==0){
                return getQueryNumber().compareTo(o.getQueryNumber());
            }
            return r;
        }

        private Integer getQueryNumber(){
            return Integer.parseInt(getQueryName().replaceAll("[^0-9]",""));
        }

        private String getQueryText(){
            return getQueryFileName().replaceAll("[0-9]","");
        }
    }



    public static class QueryTime implements Comparable<QueryTime>{
        /**
         * query
         */
        Query query;
        /**
         * execution time in milliseconds.
         */
        double queryTime;

        QueryTime(Query query, double queryTime) {
            this.query = query;
            this.queryTime = queryTime;
        }

        /**
         * Get executed query.
         * @return executed query.
         */
        public Query getQuery() {
            return query;
        }

        /**
         * Get not corrected registered time value of test execution.
         * @return not corrected time value.
         */
        protected double getInitialQueryTime(){
            return queryTime;
        }

        /**
         * Get execution time in seconds.
         * @return execution time in seconds rounded to the nearest 0.1 second.
         */
        public double getTime() {
            return TestResultsService.roundMilliseconds(queryTime);
        }

        /**
         * Get decimal logarithm for rounded query time value.
         * @return decimal logarithm for rounded query time value.
         */
        public double getLnTime(){
            return Math.log(getTime());
        }

        @Override
        public int compareTo(QueryTime o) {
            if(o != null){
                double diff = getInitialQueryTime() - o.getInitialQueryTime();
                return diff > 0 ? 1 : diff == 0 ? 0 : -1;
            }
            return 1;
        }
    }

    public static class PowerQueryTime extends QueryTime {

        private static QueryTime LONGEST_QUERY;

        PowerQueryTime(Query query, double queryTime) {
            super(query, queryTime);
            LONGEST_QUERY = this;
        }

        public void setLongestQuery(QueryTime longestQueryTime){
            LONGEST_QUERY = longestQueryTime;
        }

        /**
         * If the ratio between the longest query timing interval and current timing interval in power test is greater than 1000,
         * then return longuestQueryTime/1000.
         * @return corrected time value for power test.
         */
        @Override
        public double getTime(){
            double correctedValue = LONGEST_QUERY.getInitialQueryTime()/1000;
            correctedValue = correctedValue < getInitialQueryTime() ? getInitialQueryTime() : correctedValue;
            return TestResultsService.roundMilliseconds(correctedValue);
        }
    }

//    /**
//     * Contain query and time of query execution.
//     */
//    public static class PowerQueryTime implements Comparable<PowerQueryTime>{
//        /**
//         * query
//         */
//        Query query;
//        /**
//         * execution time in milliseconds.
//         */
//        double queryTime;
//
//        PowerQueryTime(Query query, double queryTime) {
//            this.query = query;
//            this.queryTime = queryTime;
//        }
//
//        /**
//         * Get executed query.
//         * @return executed query.
//         */
//        public Query getQuery() {
//            return query;
//        }
//
//        /**
//         * Get not corrected registered time value of test execution.
//         * @return not corrected time value.
//         */
//        public double getInitialQueryTime(){
//            return queryTime;
//        }
//
//        /**
//         * Get execution time in seconds.
//         * @return execution time in seconds rounded to the nearest 0.1 second.
//         */
//        public double getRoundedQueryTime() {
//            return TestResultsService.roundMilliseconds(queryTime);
//        }
//
//        /**
//         * If the ratio between the longest query timing interval and current timing interval in power test is greater than 1000,
//         * then return longuestQueryTime/1000.
//         * @param longestQueryTime query with longest time in power test.
//         * @return corrected time value for power test.
//         */
//        public double getPowerTestCorrectedTime(PowerQueryTime longestQueryTime){
//            double correctedValue = longestQueryTime.getInitialQueryTime()/1000;
//            correctedValue = correctedValue < getInitialQueryTime() ? getInitialQueryTime() : correctedValue;
//            return TestResultsService.roundMilliseconds(correctedValue);
//        }
//
//        /**
//         * Get decimal logarithm for rounded query time value.
//         * @return decimal logarithm for rounded query time value.
//         */
//        public double getLnTime(){
//            return Math.log(getRoundedQueryTime());
//        }
//
//        /**
//         * Get decimal logarithm for corrected for power test time value.
//         * @param longestQuery query with longest time in power test
//         * @return decimal logarithm for corrected for power test time value.
//         */
//        public double getLnPowerTestCorrectedTime(PowerQueryTime longestQuery){
//            return Math.log(getPowerTestCorrectedTime(longestQuery));
//        }
//
//        @Override
//        public int compareTo(PowerQueryTime o) {
//            if(o != null){
//                double diff = getInitialQueryTime() - o.getInitialQueryTime();
//                return diff > 0 ? 1 : diff == 0 ? 0 : -1;
//            }
//            return 1;
//        }
//    }
}
