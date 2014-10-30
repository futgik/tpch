package com.sap.tpch.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 02.10.2014.
 * Some statistic methods.
 */
public class Statistic {

    /**
     * Get max value
     * @param array Iterable array of values.
     * @return max array value.
     */
    public static Double getMax(Iterable<? extends Number> array){
        Double max = Double.MIN_VALUE;
        for(Number value : array){
            double cv = value.doubleValue();
            if(max.compareTo(cv)<0) max = cv;
        }
        return max;
    }

    /**
     * get average of array.
     * @param array analysing array.
     * @return average of array.
     */
    public static Double getAverage(Iterable<? extends Number> array){
        Double sum = 0.0;
        int count=0;
        for(Number value : array){
            sum += value.doubleValue();
            count++;
        }
        return sum/count;
    }

    /**
     * Get min value
     * @param array Iterable array of values.
     * @return min array value.
     */
    public static Double getMin(Iterable<? extends Number> array){
        Double min = Double.MAX_VALUE;
        for(Number value : array){
            double cv = value.doubleValue();
            if(min.compareTo(cv)>0) min = cv;
        }
        return min;
    }

    /**
     * Get sum of values in array.
     * @param array summing array.
     * @return sum of values in array.
     */
    public static Double getSum(Iterable<? extends Number> array){
        Double sum=0.0;
        for(Number v : array){
            sum+=v.doubleValue();
        }
        return sum;
    }
}
