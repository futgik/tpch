package com.sap.tpch.benchmark.results;

import com.sap.tpch.exception.LoadResultException;
import com.sap.tpch.service.Strings;

/**
 * Created by Alex on 25/10/2014.
 * Import results of test execution.
 */
public class ThroughputTestImportResult extends ThroughputTestResult {

    /**
     * Import from csv file. File contain one row with number of stream and execution time.
     * @param fileName name of parse file.
     * @throws LoadResultException
     */
    public ThroughputTestImportResult(String fileName) throws LoadResultException {
        super();
        loadFromFile(fileName);
    }

    /**
     * Load result of throughput test from file. File contain one row with number of stream and execution time.
     * @param fileName file name containing result.
     * @throws LoadResultException
     */
    public void loadFromFile(String fileName) throws LoadResultException {
        String fileContent = TestResultsService.getTestResultContent(fileName);
        String queryRes[] = fileContent.split(";");
        getMaxQueryStreamTime = TestResultsService.roundMilliseconds(Integer.parseInt(Strings.clearString(queryRes[1])));
    }
}
