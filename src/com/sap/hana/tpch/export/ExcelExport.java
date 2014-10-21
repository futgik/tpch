package com.sap.hana.tpch.export;


import com.sap.hana.tpch.benchmark.results.PowerTestResults;
import com.sap.hana.tpch.benchmark.results.TPCHMetrics;
import com.sap.hana.tpch.benchmark.results.TPCHSeriesMetrics;
import com.sap.hana.tpch.benchmark.results.ThroughputTestResults;
import com.sap.hana.tpch.exception.ExportException;
import com.sap.hana.tpch.types.ScaleFactor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

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

    public static void exportTPCHSeriesToTemplate(TPCHSeriesMetrics tpchSeriesMetrics) throws ExportException{
        TPCHSeriesTemplateExporter exporter = new TPCHSeriesTemplateExporter(tpchSeriesMetrics);
        exporter.export();
    }

    public static void test(){
        try {
            Workbook wb = new XSSFWorkbook();
            FileOutputStream fileOut = new FileOutputStream(ExportConf.EXPORT_PATH+"workbook.xlsx");
            Sheet sheet = wb.createSheet("new sheet");
            // Create a row and put some cells in it. Rows are 0 based.
            Row row = sheet.createRow((short)0);
            // Create a cell and put a value in it.
            Cell cell = row.createCell(0);
            cell.setCellValue(1);
            wb.cloneSheet(0);
            wb.write(fileOut);
            fileOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] arg){
        test();
    }
}
