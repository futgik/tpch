package com.sap.tpch.benchmark.results;

import com.sap.tpch.exception.GetQueryException;
import com.sap.tpch.exception.LoadResultException;
import com.sap.tpch.service.Strings;

/**
 * Created by Alex on 07/10/2014.
 * Provide power test results by loading them from file.
 */
public class PowerTestImportResult extends PowerTestResult {

    /**
     * Initializer loads results from file name.
     * File contain cvs structure without header and contain two columns.
     * first column is query name and second is execution time.
     * columns divided using ";".
     * @param fileName name of parse file.
     * @throws LoadResultException
     */
    public PowerTestImportResult(String fileName) throws LoadResultException{
        super();
        loadFromFile(fileName);
    }

    /**
     * Load results from file name.
     * File contain cvs structure without header and contain two columns.
     * first column is query name and second is execution time.
     * columns divided using ";".
     * @param fileName name of parse file.
     * @throws com.sap.tpch.exception.LoadResultException
     */
    public void loadFromFile(String fileName) throws LoadResultException {
        String fileContent = TestResultsService.getTestResultContent(fileName);
        String queriesRes[] = fileContent.split("\\n");
        for(String queryResRow : queriesRes){
            String queryRes[] = queryResRow.split(";");
            try {
                queriesTime.put(Queries.getQueryByFileName(queryRes[0]),
                        new Queries.PowerQueryTime(Queries.getQueryByFileName(queryRes[0]), Integer.parseInt(Strings.clearString(queryRes[1]))));
            }
            catch (GetQueryException e) {
                throw new LoadResultException("Can't load results from file", e);
            }
        }
        queriesTime.values().iterator().next().setLongestQuery(getLongestQuery());
    }
}
