package com.sap.hana.tpch.benchmark.results;

import com.sap.hana.tpch.service.Statistic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Results of series tpch tests.
 */
public class TPCHSeriesMetrics implements TestResult, Iterable<TPCHMetrics>{
    List<TPCHMetrics> seriesMetrics;

    public TPCHSeriesMetrics(){
        seriesMetrics = new ArrayList<>();
    }

    public void addTPCHMetrics(TPCHMetrics metrics){
        seriesMetrics.add(metrics);
    }

    /**
     * Get count of test repetition.
     * @return count of test repetition.
     */
    public int getRepetitionCount(){
        return seriesMetrics.size();
    }

    /**
     * tpch power size figure.
     * @return power size figure.
     */
    public double getAveragedPowerSize(){
        List<Double> aa = new ArrayList<>();
        for(TPCHMetrics metrics : seriesMetrics)
            aa.add(metrics.getPowerSize());
        return Statistic.getCorrectedAverage(aa);
    }

    /**
     * tpch throughput size figure.
     * @return throughput size figure.
     */
    public double getAveragedThroughputSize(){
        List<Double> aa = new ArrayList<>();
        for(TPCHMetrics metrics : seriesMetrics)
            aa.add(metrics.getThroughputSize());
        return Statistic.getCorrectedAverage(aa);
    }

    /**
     * tpch QphH size figure.
     * @return QphH size figure.
     */
    public double getAveragedQphHSize(){
        List<Double> aa = new ArrayList<>();
        for(TPCHMetrics metrics : seriesMetrics)
            aa.add(metrics.getQphHSize());
        return Statistic.getCorrectedAverage(aa);
    }

    @Override
    public double getEstimation() {
        return getAveragedQphHSize();
    }

    @Override
    public Iterator<TPCHMetrics> iterator() {
        return seriesMetrics.iterator();
    }
}
