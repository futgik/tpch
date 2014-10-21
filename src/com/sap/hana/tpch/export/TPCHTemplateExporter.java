package com.sap.hana.tpch.export;

import com.sap.hana.tpch.benchmark.results.TPCHMetrics;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;

/**
* Created by Alex on 20/10/2014.
*/
class TPCHTemplateExporter extends BaseTPCHTemplateExporter{

    TPCHMetrics tpchMetrics;

    public TPCHTemplateExporter(TPCHMetrics tpchMetrics){
        this.tpchMetrics=tpchMetrics;
    }

    protected XSSFWorkbook prepareWorkbook() throws Exception{
        InputStream iStream = new FileInputStream(ExportConf.CLIENT_TPCH_TEMPLATE_FILE);
        Workbook workbook = WorkbookFactory.create(iStream);
        Sheet sheet = workbook.getSheet(ExportConf.TEMPLATE_SHEET_NAME);
        exportToSheet(sheet, tpchMetrics.getScaleFactor(), tpchMetrics.getPowerTestResults(), tpchMetrics.getThroughputTestResults());
        return (XSSFWorkbook)workbook;
    }

    @Override
    protected String getResultFileName() {
        return "tpch";
    }
}
