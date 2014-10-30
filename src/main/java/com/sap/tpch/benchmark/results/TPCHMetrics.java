package com.sap.tpch.benchmark.results;

import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 01.10.2014.
 * Metrics containing productivity measuring as result of tpch test.
 * Metrics include power test metrics and throughput metrics.
 */
public class TPCHMetrics implements TestResult {
    /**
     * Power test metrics.
     */
    PowerTestResult powerTestResults;
    /**
     * Throughput metrics.
     */
    ThroughputTestResult throughputTestResult;
    /**
     * Scale factor using in test.
     */
    ScaleFactor scaleFactor;

    /**
     * Initialize
     * @param powerTestResults result of carrying out power test.
     * @param throughputTestResult result of caring out load test.
     * @param scaleFactor scale factor using in process of testing.
     */
    public TPCHMetrics(PowerTestResult powerTestResults, ThroughputTestResult throughputTestResult, ScaleFactor scaleFactor){
        this.powerTestResults = powerTestResults;
        this.throughputTestResult = throughputTestResult;
        this.scaleFactor = scaleFactor;
    }

    /**
     * Get result of power test.
     * @return result of power test.
     */
    public PowerTestResult getPowerTestResults(){
        return powerTestResults;
    }

    /**
     * Get result of throughput test.
     * @return result of throughput test.
     */
    public ThroughputTestResult getThroughputTestResult(){
        return throughputTestResult;
    }

    /**
     * Get scale factor using in test.
     * @return scale factor using in test.
     */
    public ScaleFactor getScaleFactor(){
        return scaleFactor;
    }

    /**
     * TPCH power size figure.
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
        return ((scaleFactor.getStreamCount()*22*3600)/ throughputTestResult.getEstimation())*scaleFactor.getScaleFactorValue();
    }

    /**
     * tpch QphH size figure.
     * @return QphH size figure.
     */
    public double getQphHSize(){
        return Math.sqrt(getPowerSize()*getThroughputSize());
    }

    /**
     * Estimation is QphH size figure.
     * @return QphH size figure.
     */
    @Override
    public double getEstimation() {
        return getQphHSize();
    }
}
