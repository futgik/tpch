package com.sap.tpch.export;

import com.sap.tpch.exception.ExportException;

/**
 * Created by Alex on 19/10/2014.
 * Export interface.
 */
public interface IExport {

    /**
     * Export anything.
     * @throws ExportException
     */
    public void export() throws ExportException;
}
