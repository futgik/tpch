package com.sap.hana.tpch.benchmark.results;

import com.sap.hana.tpch.config.Configurations;
import com.sap.hana.tpch.exception.ExecutionException;
import com.sap.hana.tpch.exec.RemoteExecutor;
import com.sap.hana.tpch.service.Statistic;

import java.util.*;

/**
 * Created by Alex on 07/10/2014.
 * Container which include results of execution for all available tests.
 */
public class TestResults {

    static String clearString(String string){
        return string.trim().replaceAll("[\n\r]", "");
    }

    static String getTestResultContent(String fileName){
        try {
            return RemoteExecutor.getRemoteCommandExecutor().execCommand(String.format(Configurations.CM_READ_FILE_CONTENT, Configurations.SERVER_TEST_DIR + "/" + fileName));
        }
        catch (ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }

    static Iterable<Double> getPowerQueryArray(Iterable<Queries.PowerQueryTime> powerQueryTimeArray){
        List<Double> result= new ArrayList<>();
        for(Queries.PowerQueryTime t : powerQueryTimeArray){
            result.add(t.getQueryTime());
        }
        return result;
    }

    static Iterable<Double> getLnPowerQueryArray(Iterable<Queries.PowerQueryTime> powerQueryTimeArray){
        List<Double> result= new ArrayList<>();
        for(Queries.PowerQueryTime t : powerQueryTimeArray){
            result.add(t.getLnQueryTime());
        }
        return result;
    }
}
