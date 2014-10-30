package com.sap.tpch.benchmark;

import com.sap.tpch.benchmark.results.Queries;
import com.sap.tpch.benchmark.results.SingleQueryTestResults;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.exception.LoadResultException;
import com.sap.tpch.exception.TestException;
import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 01/10/2014.
 * Test allow to run single query.
 */
public class SingleQueryTest extends BaseTest {

    /**
     * query object.
     */
    Queries.Query executingQuery;

    /**
     * Scale factor
     */
    ScaleFactor scaleFactor;

    /**
     * Initialization using test params.
     * @param executingQuery executing query.
     * @param scaleFactor scale factor.
     */
    SingleQueryTest(ScaleFactor scaleFactor, Queries.Query executingQuery){
        super();
        setExecutingQuery(executingQuery);
        this.scaleFactor = scaleFactor;
    }

    private ScaleFactor getScaleFactor(){
        return scaleFactor;
    }

    /**
     * get execution query.
     * @return execution query.
     */
    public Queries.Query getExecutingQuery() {
        return executingQuery;
    }

    private void setExecutingQuery(Queries.Query executingQuery) {
        this.executingQuery = executingQuery;
    }

    @Override
    public String getTestFileName() {
        return "count";
    }

    @Override
    public String getTestParams() {
        return String.format("%s %s %d",executingQuery.getQueryFileName(), TPCHConfig.VALIDATION_QUERY, getScaleFactor().getScaleFactorValue());
    }

    @Override
    public SingleQueryTestResults run(BenchmarkProcessMonitor monitor) throws TestException {
        return super.run(monitor);
    }

    @Override
    public SingleQueryTestResults getResults() throws TestException {
        try {
            return new SingleQueryTestResults(getOutputFileName());

        } catch (LoadResultException e) {
            throw new TestException(String.format("Can't load test results from file %s",getOutputFileName()), e);
        }
    }

    @Override
    public String getTestName() {
        return "Single query";
    }
}
