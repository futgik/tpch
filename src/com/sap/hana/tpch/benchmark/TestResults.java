package com.sap.hana.tpch.benchmark;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.ExecutionException;
import com.sap.hana.tpch.exception.GetQueryException;
import com.sap.hana.tpch.exception.LoadResultException;
import com.sap.hana.tpch.exec.RemoteExecutor;
import com.sap.hana.tpch.interaction.console.Service;
import com.sap.hana.tpch.service.Statistic;
import com.sap.hana.tpch.types.ScaleFactor;

import java.util.*;

/**
 * Created by Alex on 07/10/2014.
 * Container which include results of execution for all available tests.
 */
public class TestResults {

    /**
     * Created by Alex on 07/10/2014.
     * Contain common methods for working with different tests.
     */
    public static interface TestResult {
        /**
         * get some numerical estimation of executed test.
         * @return numerical estimation of test.
         */
        public double getEstimation();
    }

    /**
     * Created by Alex on 07/10/2014.
     * Contain some common test methods and some common implementation
     */
    abstract static class BaseQueriesResult implements TestResult {

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
            return Statistic.getMax(getPowerQueryArray(queriesTime.values()));
        }

        /**
         * Get time of most faster query.
         * @return time in seconds of most lower query.
         */
        public double getMinQueryTime(){
            return Statistic.getMin(getPowerQueryArray(queriesTime.values()));
        }

        /**
         * get full time execution of all queries.
         * @return time in seconds of most lower query.
         */
        public double getSumQueryTime(){
            return Statistic.getSum(getPowerQueryArray(queriesTime.values()));
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
            return Statistic.getSum(getLnPowerQueryArray(queriesTime.values()));
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
         * @throws LoadResultException
         */
        public void loadFromFile(String fileName) throws LoadResultException {
            String fileContent = getTestResultContent(fileName);
            String queriesRes[] = fileContent.split("\\n");
            for(String queryResRow : queriesRes){
                String queryRes[] = queryResRow.split(";");
                try {
                    queriesTime.put(Queries.getQueryByFileName(queryRes[0]),
                            new Queries.PowerQueryTime(Queries.getQueryByFileName(queryRes[0]), Integer.parseInt(clearString(queryRes[1]))));
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

    /**
    * Created by Alex on 07/10/2014.
    */
    public static class ThroughputTestResults implements TestResult {
        double getMaxQueryStreamTime;

        public ThroughputTestResults(){
        }

        public ThroughputTestResults(String fileName) throws LoadResultException {
            loadFromFile(fileName);
        }

        public void loadFromFile(String fileName) throws LoadResultException{
            String fileContent = getTestResultContent(fileName);
            String queryRes[] = fileContent.split(";");
            getMaxQueryStreamTime = Service.roundMilliseconds(Integer.parseInt(clearString(queryRes[1])));
        }

        @Override
        public double getEstimation() {
            return getMaxQueryStreamTime;
        }
    }

    /**
    * Created by Alex on 07/10/2014.
    */
    public static class SingleQueryTestResults implements TestResult {
        Queries.PowerQueryTime queryTime;

        public SingleQueryTestResults(){
        }

        public SingleQueryTestResults(String fileName) throws LoadResultException {
            loadFromFile(fileName);
        }

        public void loadFromFile(String fileName) throws LoadResultException {
            String fileContent = getTestResultContent(fileName);
            String queryRes[] = fileContent.split(";");
            try {
                queryTime = new Queries.PowerQueryTime(Queries.getQueryByFileName(queryRes[0]), Integer.parseInt(clearString(queryRes[1])));
            }
            catch (GetQueryException e) {
                throw new LoadResultException("Can't load results from file", e);
            }
        }

        @Override
        public double getEstimation() {
            return queryTime.getQueryTime();
        }

        public Queries.Query getExecutingQuery(){
            return queryTime.getQuery();
        }
    }

    private static String clearString(String string){
        return string.trim().replaceAll("[\n\r]", "");
    }

    /**
    * Created by Alex on 07/10/2014.
    */
    public static class RefreshResults extends BaseQueriesResult{
        public RefreshResults(){
            super();
        }

        public RefreshResults(String fileName) throws LoadResultException {
            super();
            loadFromFile(fileName);
        }
    }

    /**
    * Created by Alex on 07/10/2014.
    */
    public static class PowerTestResults extends BaseQueriesResult {
        public PowerTestResults(){
            super();
        }

        public PowerTestResults(String fileName) throws LoadResultException {
            super();
            loadFromFile(fileName);
        }
    }

    private static String getTestResultContent(String fileName){
        try {
            return RemoteExecutor.getRemoteCommandExecutor().execCommand(String.format(Configurations.CM_READ_FILE_CONTENT, Configurations.SERVER_TEST_DIR + "/" + fileName));
        }
        catch (ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Iterable<Double> getPowerQueryArray(Iterable<Queries.PowerQueryTime> powerQueryTimeArray){
        List<Double> result= new ArrayList<>();
        for(Queries.PowerQueryTime t : powerQueryTimeArray){
            result.add(t.getQueryTime());
        }
        return result;
    }

    public static Iterable<Double> getLnPowerQueryArray(Iterable<Queries.PowerQueryTime> powerQueryTimeArray){
        List<Double> result= new ArrayList<>();
        for(Queries.PowerQueryTime t : powerQueryTimeArray){
            result.add(t.getLnQueryTime());
        }
        return result;
    }

    /**
     * Created by Alex on 01.10.2014.
     * Metrics containing productivity measuring using tpch test.
     */
    public static class TPCHMetrics implements TestResult{
        PowerTestResults powerTestResults;
        ThroughputTestResults throughputTestResults;
        ScaleFactor scaleFactor;

        /**
         * Initialize
         * @param powerTestResults result of carrying out power test.
         * @param throughputTestResults result of caring out load test.
         * @param scaleFactor scale factor using in process of testing.
         */
        public TPCHMetrics(PowerTestResults powerTestResults, ThroughputTestResults throughputTestResults, ScaleFactor scaleFactor){
            this.powerTestResults = powerTestResults;
            this.throughputTestResults = throughputTestResults;
            this.scaleFactor = scaleFactor;
        }

        /**
         * tpch power size figure.
         * @return power size figure.
         */
        public double getPowerSize(){
            return 3600*Math.exp(-((powerTestResults.getSumLnQueryTime())/24))*scaleFactor.getScaleFactorValue();
        }

        /**
         * tpch throughput size figure.
         * @return throughput size figure.
         */
        public double getThroughputSize(){
            return ((scaleFactor.getStreamCount()*22*3600)/throughputTestResults.getEstimation())*scaleFactor.getScaleFactorValue();
        }

        /**
         * tpch QphH size figure.
         * @return QphH size figure.
         */
        public double getQphHSize(){
            return Math.sqrt(getPowerSize()*getThroughputSize());
        }

        @Override
        public double getEstimation() {
            return getQphHSize();
        }
    }
}
