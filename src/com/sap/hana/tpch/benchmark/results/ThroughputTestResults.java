package com.sap.hana.tpch.benchmark.results;

import com.sap.hana.tpch.exception.LoadResultException;
import com.sap.hana.tpch.interaction.console.Service;

/**
* Created by Alex on 07/10/2014.
*/
public class ThroughputTestResults implements TestResult {
    double getMaxQueryStreamTime;

    public ThroughputTestResults(){
    }

    public ThroughputTestResults(String fileName) throws LoadResultException {
        loadFromFile(fileName);
    }

    public void loadFromFile(String fileName) throws LoadResultException{
        String fileContent = TestResults.getTestResultContent(fileName);
        String queryRes[] = fileContent.split(";");
        getMaxQueryStreamTime = Service.roundMilliseconds(Integer.parseInt(TestResults.clearString(queryRes[1])));
    }

    @Override
    public double getEstimation() {
        return getMaxQueryStreamTime;
    }
}
