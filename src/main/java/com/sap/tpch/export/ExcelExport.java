package com.sap.tpch.export;


import com.sap.tpch.benchmark.results.TPCHMetrics;
import com.sap.tpch.benchmark.results.TPCHSeriesMetrics;
import com.sap.tpch.exception.ExportException;

/**
 * Created by Alex on 19/10/2014.
 * Allow export result of test into excel file.
 */
public class ExcelExport {

    /**
     * Allow to export result of tpch test execution into excel template.
     * template file contain spacial named cells to insert results.
     * this places has following names:
     * scaleFactor - to insert scale factor value.
     * powerStart - top first cell to insert power test result values.
     * refreshStart - top first cell to insert refresh result values.
     * throughputStart -place to insert result of throughput test result.
     * @param tpchMetrics result of execution tpch test.
     * @throws ExportException
     */
    public static void exportTPCHToTemplate(TPCHMetrics tpchMetrics) throws ExportException{
        TPCHTemplateExporter exporter = new TPCHTemplateExporter(tpchMetrics);
        exporter.export();
    }

    /**
     * Allow to export result of series tpch tests into excel template.
     * template file contain special named cells to insert results.
     * this places has following names:
     * scaleFactor - to insert scale factor value.
     * powerStart - top first cell to insert power test result values.
     * refreshStart - top first cell to insert refresh result values.
     * throughputStart -place to insert result of throughput test result.
     * Excel file include sheets each contain result of execution single tpch test.
     * Final sheet contain averaging results of tpch tests.
     * @param tpchSeriesMetrics result of execution series tpch tests.
     * @throws ExportException
     */
    public static void exportTPCHSeriesToTemplate(TPCHSeriesMetrics tpchSeriesMetrics) throws ExportException{
        TPCHSeriesTemplateExporter exporter = new TPCHSeriesTemplateExporter(tpchSeriesMetrics);
        exporter.export();
    }

}
