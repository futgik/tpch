package com.sap.hana.tpch.types;

import com.sap.hana.tpch.exception.SFException;

import java.util.Arrays;

/**
 * Created by Alex on 20.09.2014.
 * Present scale factor.
 */
public class ScaleFactor {
    //available factor values
    int availableScaleFactors[] = {1,10,30,100,300,1000,3000,10000,30000,100000};

    int scaleFactor = 1;

    /**
     * Default initializer
     * Set scale factor to 1.
     */
    public ScaleFactor(){
    }

    /**
     * Copy constructor.
     * @param scaleFactor scale factor to copy.
     */
    public ScaleFactor(ScaleFactor scaleFactor){
        this.scaleFactor = scaleFactor.scaleFactor;
    }

    /**
     * Set scale factor
     * Available scale factors: 1,10,30,100,300,1000,3000,10000,30000,100000
     * @param scaleFactor value of scale factor.
     * @throws SFException
     */
    public ScaleFactor(int scaleFactor) throws SFException{
        setScaleFactor(scaleFactor);
    }

    /**
     * Get scale factor
     * @return scale factor.
     */
    public int getScaleFactorValue() {
        return scaleFactor;
    }

    /**
     * Set scale factor
     * Available scale factors values: 1,10,30,100,300,1000,3000,10000,30000,100000
     * @param scaleFactor value of scale factor.
     * @throws SFException
     */
    public void setScaleFactor(int scaleFactor) throws SFException {
        int pos = Arrays.binarySearch(availableScaleFactors,scaleFactor);
        if(pos <0) throw new SFException();
        this.scaleFactor = scaleFactor;
    }

    /**
     * Get stream count
     * @return stream count according to stream count.
     */
    public int getStreamCount(){
        return Arrays.binarySearch(availableScaleFactors,scaleFactor)+2;
    }

    @Override
    public String toString(){
        return Integer.toString(scaleFactor);
    }
}
