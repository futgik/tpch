package com.sap.hana.tpch.remoute;

import com.sap.hana.tpch.benchmark.DatabaseState;
import com.sap.hana.tpch.exception.PrepException;
import com.sap.hana.tpch.types.ScaleFactor;

/**
 * Created by Alex on 11.10.2014.
 * Contain base prepare methods for all databases.
 */
abstract public class BaseEnvPreparation implements Preparation{

    /**
     * call prepare method for children and set ready database state.
     * @param preparationMonitor using for monitor some process.
     * @throws PrepException
     */
    final public void prepare(EnvPreparationMonitor preparationMonitor) throws PrepException {
        prepareDB(preparationMonitor);
        DatabaseState.setReadyState();
    }

    /**
     * prepare methods implementing by children classes.
     * @param preparationMonitor using for monitor execution process.
     * @throws PrepException
     */
    abstract protected void prepareDB(EnvPreparationMonitor preparationMonitor)throws PrepException;
}
