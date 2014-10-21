package com.sap.hana.tpch.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
     * return average of array without max and min value.
     * @param array analysing array.
     * @return average of array without max and min value.
     */
    public  static Double getCorrectedAverage(List<? extends Number> array){
        if(array.size() < 3) {
            if (array.size() < 2){
                if(array.size() < 1) return 0.0;
                return array.get(0).doubleValue();
            }
            return (array.get(0).doubleValue()+array.get(1).doubleValue())/2.0;
        }
        List<Double> da = new ArrayList<>();
        for(Number a : array) da.add(a.doubleValue());
        Collections.sort(da);
        return getAverage(da.subList(1,da.size()-1));
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
