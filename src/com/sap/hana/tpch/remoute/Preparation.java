package com.sap.hana.tpch.remoute;

import com.sap.hana.tpch.exception.PrepException;

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
    public void prepare(EnvPreparationMonitor preparationMonitor) throws PrepException;
}
