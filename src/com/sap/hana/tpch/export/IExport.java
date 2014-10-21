package com.sap.hana.tpch.export;

import com.sap.hana.tpch.exception.ExportException;

/**
 * Created by Alex on 19/10/2014.
 */
public interface IExport {

    public void export() throws ExportException;
}
