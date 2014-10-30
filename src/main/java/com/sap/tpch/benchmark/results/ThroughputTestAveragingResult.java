package com.sap.tpch.benchmark.results;

import java.util.List;

/**
 * Created by Alex on 25/10/2014.
 * Create result by averaging list of throughput test results.
 */
public class ThroughputTestAveragingResult extends ThroughputTestResult {

    /**
     * Create result by averaging list of throughput test results.
     * @param throughputTestResults list of throughput results.
     */
    public ThroughputTestAveragingResult(List<ThroughputTestResult> throughputTestResults){
        getMaxQueryStreamTime = 0;
        for(ThroughputTestResult result : throughputTestResults){
            getMaxQueryStreamTime += result.getEstimation();
        }
        getMaxQueryStreamTime = getMaxQueryStreamTime / throughputTestResults.size();
    }



}
