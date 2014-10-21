package com.sap.hana.tpch.benchmark.results;

import com.sap.hana.tpch.exception.GetQueryException;
import com.sap.hana.tpch.exception.LoadResultException;

/**
* Created by Alex on 07/10/2014.
*/
public class SingleQueryTestResults implements TestResult {
    Queries.PowerQueryTime queryTime;

    public SingleQueryTestResults(){
    }

    public SingleQueryTestResults(String fileName) throws LoadResultException {
        loadFromFile(fileName);
    }

    public void loadFromFile(String fileName) throws LoadResultException {
        String fileContent = TestResults.getTestResultContent(fileName);
        String queryRes[] = fileContent.split(";");
        try {
            queryTime = new Queries.PowerQueryTime(Queries.getQueryByFileName(queryRes[0]), Integer.parseInt(TestResults.clearString(queryRes[1])));
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
