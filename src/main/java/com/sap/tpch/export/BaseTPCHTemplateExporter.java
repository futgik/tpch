package com.sap.tpch.export;

import com.sap.tpch.benchmark.results.*;
import com.sap.tpch.exception.ExportException;
import com.sap.tpch.types.ScaleFactor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Alex on 20/10/2014.
 * Contain common methods to export tpch test result to xlsx file.
 */
abstract class BaseTPCHTemplateExporter implements IExport{

    public BaseTPCHTemplateExporter(){}

    /**
     * In sheet find position of cell which contain powerStart string
     * @param sheet sheet where to find
     * @return finding cell
     */
    private static Cell findPowerTestPosition(Sheet sheet){
        return findPosition("powerStart",sheet);
    }

    /**
     * In sheet find position of cell which contain refreshStart string
     * @param sheet sheet where to find
     * @return finding cell
     */
    private static Cell findRefreshTestPosition(Sheet sheet){
        return findPosition("refreshStart",sheet);
    }

    /**
     * In sheet find position of cell which contain throughputStart string
     * @param sheet sheet where to find
     * @return finding cell
     */
    private static Cell findThroughputTestPosition(Sheet sheet){
        return findPosition("throughputStart",sheet);
    }

    /**
     * In sheet find position of cell which contain scaleFactor string
     * @param sheet sheet where to find
     * @return finding cell
     */
    private static Cell findScaleFactorCell(Sheet sheet){
        return findPosition("scaleFactor",sheet);
    }

    private static Cell findPosition(String title, Sheet sheet){
        for(Row row : sheet){
            for(Cell cell : row){
                try {
                    if (cell.getStringCellValue().equals(title))
                        return cell;
                }catch (IllegalStateException ignored){}
            }
        }
        return null;
    }

    /**
     * export tpch constituents to sheet
     * @param templateSheet filling sheet.
     * @param scaleFactor tpch test scale factor.
     * @param powerTestResults power test results.
     * @param throughputTestResult throughput test results.
     * @return filled sheet.
     */
    protected Sheet exportToSheet(Sheet templateSheet, ScaleFactor scaleFactor, PowerTestResult powerTestResults,
                                  ThroughputTestResult throughputTestResult){
        setScaleFactorValue(findScaleFactorCell(templateSheet),scaleFactor);
        exportQueriesResults(findPowerTestPosition(templateSheet), powerTestResults);
        exportRefreshResults(findRefreshTestPosition(templateSheet), powerTestResults);
        exportThroughputResults(findThroughputTestPosition(templateSheet), throughputTestResult);
        return templateSheet;
    }

    /**
     * Export query execution results to sheet.
     * @param powerTestPosition start cell for export results.
     * @param powerTestResults results of power test execution.
     */
    private static void exportQueriesResults(Cell powerTestPosition, PowerTestResult powerTestResults){
        exportTestResults(powerTestPosition, powerTestResults.getQueryTime(Queries.QueryType.QUERY));
    }

    /**
     * Export refresh execution results to sheet
     * @param powerTestPosition start cell for refresh results.
     * @param powerTestResults results of power test execution.
     */
    private static void exportRefreshResults(Cell powerTestPosition, PowerTestResult powerTestResults){
        exportTestResults(powerTestPosition,powerTestResults.getQueryTime(Queries.QueryType.REFRESH));
    }

    private static void exportTestResults(Cell powerTestPosition, SortedMap<Queries.Query,Queries.PowerQueryTime> testResults){
        int rIndex = powerTestPosition.getRowIndex();
        int cIndex = powerTestPosition.getColumnIndex();
        for(Queries.Query q : testResults.keySet()){
            powerTestPosition.getSheet().getRow(rIndex).getCell(cIndex-1).setCellValue(q.getQueryName());
            powerTestPosition.getSheet().getRow(rIndex).getCell(cIndex).setCellValue(testResults.get(q).getTime());
            rIndex++;
        }
    }

    /**
     * Export result of throughput test execution.
     * @param throughputTestPosition position to fill throughput test results.
     * @param throughputTestResults throughput test results.
     */
    private static void exportThroughputResults(Cell throughputTestPosition, TestResult throughputTestResults){
        throughputTestPosition.setCellValue(throughputTestResults.getEstimation());
    }

    /**
     * Insert to sheet scale factor value.
     * @param scaleFactorCell position of insertion.
     * @param scaleFactor scale factor value.
     */
    private static void setScaleFactorValue(Cell scaleFactorCell, ScaleFactor scaleFactor){
        scaleFactorCell.setCellValue(scaleFactor.getScaleFactorValue());
    }

    /**
     * Overloading prepare workbook method.
     * @return prepared workbook.
     * @throws Exception
     */
    protected abstract XSSFWorkbook prepareWorkbook() throws Exception;

    /**
     * Export function prepare workbook then recalculate all formulas and save workbook to file.
     * @throws ExportException
     */
    @Override
    public void export() throws ExportException {
        try {
            XSSFWorkbook workbook = prepareWorkbook();
            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            // Write the output to a file
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
            FileOutputStream fileOut = new FileOutputStream(ExportConf.EXPORT_PATH+getResultFileName()+"_"+sdf.format(new Date())+".xlsx");
            workbook.write(fileOut);
            fileOut.close();
        }catch (Exception e){
            throw new ExportException("Can't export results to template",e);
        }
    }

    /**
     * Get result name of file.
     * @return result file name.
     */
    protected abstract String getResultFileName();
}
