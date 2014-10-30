package com.sap.tpch.benchmark.results;

import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.exception.ExecutionException;
import com.sap.tpch.exec.RemoteExecutor;

/**
 * Created by Alex on 25/10/2014.
 * Service methods for process result.
 */
public class TestResultsService {

    /**
     * Get content of file on remote machine which contain results of test execution.
     * @param fileName name of file containing results of test execution.
     * @return content of file.
     */
    static String getTestResultContent(String fileName){
        try {
            return RemoteExecutor.getRemoteCommandExecutor().execCommand(String.format(TPCHConfig.CM_READ_FILE_CONTENT, TPCHConfig.SERVER_TEST_DIR + "/" + fileName));
        }
        catch (ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Transform milliseconds to seconds,and round to tenth.
     * @param milliseconds value in milliseconds to transform.
     * @return transformed result.
     */
    public static double roundMilliseconds(double milliseconds){
        if(milliseconds < 50) return 0.1;
        return (Math.round(milliseconds/100.0)/10.0);
    }
}
