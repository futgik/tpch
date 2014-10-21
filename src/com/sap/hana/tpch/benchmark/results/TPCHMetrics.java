package com.sap.hana.tpch.benchmark.results;

import com.sap.hana.tpch.exception.ExportException;
import com.sap.hana.tpch.export.ExcelExport;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 01.10.2014.
 * Metrics containing productivity measuring using tpch test.
 */
public class TPCHMetrics implements TestResult {
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

    public PowerTestResults getPowerTestResults(){
        return powerTestResults;
    }

    public ThroughputTestResults getThroughputTestResults(){
        return throughputTestResults;
    }

    public ScaleFactor getScaleFactor(){
        return scaleFactor;
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
