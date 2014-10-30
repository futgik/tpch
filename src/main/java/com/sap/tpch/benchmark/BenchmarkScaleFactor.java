package com.sap.tpch.benchmark;

import com.sap.tpch.types.ScaleFactor;

/**
 * Created by Alex on 27/10/2014.
 */
public class BenchmarkScaleFactor {

    static ScaleFactor scaleFactor = null;

    private BenchmarkScaleFactor(){
    }

    private static ScaleFactor Initialize(){
        scaleFactor = new ScaleFactor();
        return scaleFactor;
    }

    public static ScaleFactor getScaleFactor(){
        if(scaleFactor == null)
            scaleFactor = Initialize();
        return scaleFactor;
    }
}
