package com.sap.tpch.benchmark.results;

import com.sap.tpch.exception.TestException;
import com.sap.tpch.service.Statistic;
import com.sap.tpch.types.ScaleFactor;

import java.util.*;

/**
 * Results of series tpch tests.
 * series tpch test must carry out with equal scale factor values.
 */
public class TPCHSeriesMetrics implements TestResult, Iterable<TPCHMetrics>{
    /**
     * List of carry outed tests.
     */
    List<TPCHMetrics> seriesMetrics;

    /**
     * Scale factor.
     */
    ScaleFactor scaleFactor;

    public TPCHSeriesMetrics(){
        seriesMetrics = new LinkedList<>();
    }

    /**
     * Add result of tpch test to results.
     * @param metrics result of tpch test.
     */
    public void addTPCHMetrics(TPCHMetrics metrics) throws TestException{
        if(seriesMetrics.size()<1){
            scaleFactor = metrics.getScaleFactor();
        }
        if(scaleFactor != metrics.getScaleFactor())
            throw new TestException("Scale factor of tpch tests must be equals");
        seriesMetrics.add(metrics);
        Collections.sort(seriesMetrics,new Comparator<TPCHMetrics>() {
            @Override
            public int compare(TPCHMetrics o1, TPCHMetrics o2) {
                return o1.getQphHSize()-o2.getQphHSize() > 0 ? 1 : o1.getQphHSize()-o2.getQphHSize() == 0 ? 0 : -1;
            }
        });
    }

    /**
     * Get count of averaging tpch tests.
     * If results containing only 3 tpch test o less result include all tests.
     * If result contain more than 4 tpch test result exclude slowest and rapidest test.
     * @return count of averaging tpch tests.
     */
    public int getAveragingCount() {
        return getAveragingArray().size();
    }

    /**
     * Get count of test repetition.
     * @return count of test repetition.
     */
    public int getRepetitionCount(){
        return seriesMetrics.size();
    }

    /**
     * Get averaging array of tpch test results.
     * If results containing only 3 tpch test o less result include all tpch test results.
     * if result contain more than 4 tpch test result exclude slowest and rapidest test.
     * @return averaging array of tpch test results.
     */
    public List<TPCHMetrics> getAveragingArray(){
        if(getRepetitionCount() < 2) return seriesMetrics;
        else if(getRepetitionCount() < 3) return seriesMetrics.subList(1,seriesMetrics.size());
        else return seriesMetrics.subList(1,seriesMetrics.size()-1);
    }

    /**
     * tpch power size figure.
     * @return power size figure.
     */
    public double getAveragedPowerSize(){
        List<Double> aa = new ArrayList<>();
        for(TPCHMetrics metrics : getAveragingArray())
            aa.add(metrics.getPowerSize());
        return Statistic.getAverage(aa);
    }

    /**
     * tpch throughput
     * size figure.
     * @return throughput size figure.
     */
    public double getAveragedThroughputSize(){
        List<Double> aa = new ArrayList<>();
        for(TPCHMetrics metrics : getAveragingArray())
            aa.add(metrics.getThroughputSize());
        return Statistic.getAverage(aa);
    }

    /**
     * tpch QphH size figure.
     * @return QphH size figure.
     */
    public double getAveragedQphHSize(){
        List<Double> aa = new ArrayList<>();
        for(TPCHMetrics metrics : getAveragingArray())
            aa.add(metrics.getQphHSize());
        return Statistic.getAverage(aa);
    }

    /**
     * Get averaged TPCHMetrics which contain averaged power test results and throughput test results.
     * @return averaged TPCHMetrics.
     */
    public TPCHMetrics getAverageTPCHMetrics(){
        return new TPCHMetrics(
                new PowerTestAveragingResult(getPowerTestListResults()),
                new ThroughputTestAveragingResult(getThroughputTestListResults()),
                scaleFactor
                );
    }

    private List<PowerTestResult> getPowerTestListResults(){
        List<PowerTestResult> results = new LinkedList<>();
        for(TPCHMetrics metrics : getAveragingArray()){
            results.add(metrics.getPowerTestResults());
        }
        return results;
    }

    private List<ThroughputTestResult> getThroughputTestListResults(){
        List<ThroughputTestResult> results = new LinkedList<>();
        for(TPCHMetrics metrics : getAveragingArray()){
            results.add(metrics.getThroughputTestResult());
        }
        return results;
    }

    /**
     * Estimation is averaged QphH size figure.
     * @return averaged QphH size figure.
     */
    @Override
    public double getEstimation() {
        return getAveragedQphHSize();
    }

    @Override
    public Iterator<TPCHMetrics> iterator() {
        return getAveragingArray().iterator();
    }
}
