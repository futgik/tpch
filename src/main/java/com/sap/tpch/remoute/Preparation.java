package com.sap.tpch.remoute;

import com.sap.tpch.exception.PrepException;

/**
 * Created by Alex on 08/10/2014.
 * Using for make some preparation things.
 */
public interface Preparation {
    /**
     * Method executed for implement some preparatory work.
     * @param preparationMonitor using for monitor some process.
     * @throws PrepException exception.
     */
    public void prepare(PreparationMonitor preparationMonitor) throws PrepException;
}
