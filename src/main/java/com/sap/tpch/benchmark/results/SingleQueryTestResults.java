package com.sap.tpch.benchmark.results;

import com.sap.tpch.exception.GetQueryException;
import com.sap.tpch.exception.LoadResultException;
import com.sap.tpch.service.Strings;

/**
* Created by Alex on 07/10/2014.
 * contain result of execution of single query.
*/
public class SingleQueryTestResults implements TestResult {
    /**
     * execution result.
     */
    Queries.PowerQueryTime queryTime;

    public SingleQueryTestResults(){
    }

    /**
     * Load result of single query from file.
     * File contain query name and query execution time divided by semicolon
     * @param fileName name of file containing result.
     * @throws LoadResultException
     */
    public SingleQueryTestResults(String fileName) throws LoadResultException {
        loadFromFile(fileName);
    }

    /**
     * Load result from file.
     * @param fileName name of file containing result.
     * @throws LoadResultException
     */
    public void loadFromFile(String fileName) throws LoadResultException {
        String fileContent = TestResultsService.getTestResultContent(fileName);
        String queryRes[] = fileContent.split(";");
        try {
            queryTime = new Queries.PowerQueryTime(Queries.getQueryByFileName(queryRes[0]), Integer.parseInt(Strings.clearString(queryRes[1])));
        }
        catch (GetQueryException e) {
            throw new LoadResultException("Can't load results from file", e);
        }
    }

    /**
     * Get execution time.
     * @return execution time.
     */
    @Override
    public double getEstimation() {
        return queryTime.getTime();
    }

    /**
     * Get execution query.
     * @return execution query.
     */
    public Queries.Query getExecutingQuery(){
        return queryTime.getQuery();
    }
}
