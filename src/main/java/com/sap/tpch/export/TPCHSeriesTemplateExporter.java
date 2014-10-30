package com.sap.tpch.export;

import com.sap.tpch.benchmark.results.*;
import com.sap.tpch.types.ScaleFactor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Alex on 20/10/2014.
 * Export result of series of tpch tests into xlsx file
 */
class TPCHSeriesTemplateExporter extends BaseTPCHTemplateExporter{

    /**
     * result of execution series of tpch tests.
     */
    TPCHSeriesMetrics tpchMetrics;

    public TPCHSeriesTemplateExporter(TPCHSeriesMetrics seriesMetrics) {
        super();
        this. tpchMetrics = seriesMetrics;
    }

    /**
     * Preparation include open template clone it several time, inserting single tpch test result into separate sheet,
     * and creating averaged sheet.
     * @return workbook with exported params.
     * @throws Exception
     */
    @Override
    protected XSSFWorkbook prepareWorkbook() throws Exception {
        InputStream iStream = new FileInputStream(ExportConf.CLIENT_TPCH_SERIES_TEMPLATE_FILE);
        Workbook workbook = WorkbookFactory.create(iStream);
        int index = 1;
        for(TPCHMetrics metrics : tpchMetrics){
            Sheet newSheet = workbook.cloneSheet(0);
            workbook.setSheetName(index,String.format("%s_%d",ExportConf.TEMPLATE_SHEET_NAME,index));
            exportToSheet(newSheet,metrics.getScaleFactor(), metrics.getPowerTestResults(),metrics.getThroughputTestResult());
            index++;
        }
        TPCHMetrics averageMetrics = tpchMetrics.getAverageTPCHMetrics();
        Sheet averagingSheet = workbook.cloneSheet(0);
        workbook.setSheetName(index,ExportConf.TEMPLATE_SHEET_NAME+" average");
        exportToSheet(averagingSheet,averageMetrics.getScaleFactor(),averageMetrics.getPowerTestResults(),averageMetrics.getThroughputTestResult());
        workbook.removeSheetAt(0);
        workbook.setActiveSheet(0);
        return (XSSFWorkbook)workbook;
    }

    @Override
    protected String getResultFileName() {
        return "tpch_series";
    }
}
