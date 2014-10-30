package com.sap.tpch.export;

import com.sap.tpch.benchmark.results.TPCHMetrics;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;

/**
* Created by Alex on 20/10/2014.
 * Export result of tpch tests execution into xlsx file
*/
class TPCHTemplateExporter extends BaseTPCHTemplateExporter{

    /**
     * result of execution single tpch test.
     */
    TPCHMetrics tpchMetrics;

    /**
     * Initialize with metric.
     * @param tpchMetrics tpch metric
     */
    public TPCHTemplateExporter(TPCHMetrics tpchMetrics){
        this.tpchMetrics=tpchMetrics;
    }

    /**
     * Workbook preparation include opening template, filling it with tpch result data and saving results.
     * @return ready workbook.
     * @throws Exception
     */
    protected XSSFWorkbook prepareWorkbook() throws Exception{
        InputStream iStream = new FileInputStream(ExportConf.CLIENT_TPCH_TEMPLATE_FILE);
        Workbook workbook = WorkbookFactory.create(iStream);
        Sheet sheet = workbook.getSheet(ExportConf.TEMPLATE_SHEET_NAME);
        exportToSheet(sheet, tpchMetrics.getScaleFactor(), tpchMetrics.getPowerTestResults(), tpchMetrics.getThroughputTestResult());
        return (XSSFWorkbook)workbook;
    }

    @Override
    protected String getResultFileName() {
        return "tpch";
    }
}
