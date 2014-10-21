package com.sap.hana.tpch.export;

import com.sap.hana.tpch.benchmark.results.TPCHMetrics;
import com.sap.hana.tpch.benchmark.results.TPCHSeriesMetrics;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Alex on 20/10/2014.
 */
class TPCHSeriesTemplateExporter extends BaseTPCHTemplateExporter{

    TPCHSeriesMetrics tpchMetrics;

    public TPCHSeriesTemplateExporter(TPCHSeriesMetrics seriesMetrics) {
        super();
        this. tpchMetrics = seriesMetrics;
    }

    @Override
    protected XSSFWorkbook prepareWorkbook() throws Exception {
        InputStream iStream = new FileInputStream(ExportConf.CLIENT_TPCH_SERIES_TEMPLATE_FILE);
        Workbook workbook = WorkbookFactory.create(iStream);
        int index = 1;
        for(TPCHMetrics metrics : tpchMetrics){
            Sheet newSheet = workbook.cloneSheet(0);
            workbook.setSheetName(index,String.format("%s_%d",ExportConf.TEMPLATE_SHEET_NAME,index));
            exportToSheet(newSheet,metrics.getScaleFactor(), metrics.getPowerTestResults(),metrics.getThroughputTestResults());
            index++;
        }
        workbook.removeSheetAt(0);
        workbook.setActiveSheet(0);
        return (XSSFWorkbook)workbook;
    }

    @Override
    protected String getResultFileName() {
        return "tpch_series";
    }
}
