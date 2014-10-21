package com.sap.hana.tpch.export;

import com.sap.hana.tpch.benchmark.results.PowerTestResults;
import com.sap.hana.tpch.benchmark.results.Queries;
import com.sap.hana.tpch.benchmark.results.ThroughputTestResults;
import com.sap.hana.tpch.exception.ExportException;
import com.sap.hana.tpch.types.ScaleFactor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 20/10/2014.
 */
abstract class BaseTPCHTemplateExporter implements IExport{

    public BaseTPCHTemplateExporter(){}

    private static Cell findPowerTestPosition(Sheet sheet){
        return findPosition("powerStart",sheet);
    }

    private static Cell findRefreshTestPosition(Sheet sheet){
        return findPosition("refreshStart",sheet);
    }

    private static Cell findThroughputTestPosition(Sheet sheet){
        return findPosition("throughputStart",sheet);
    }

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

    protected Sheet exportToSheet(Sheet templateSheet, ScaleFactor scaleFactor, PowerTestResults powerTestResults,
                                  ThroughputTestResults throughputTestResults){
        setScaleFactorValue(findScaleFactorCell(templateSheet),scaleFactor);
        exportPowerTestResults(findPowerTestPosition(templateSheet),powerTestResults);
        exportRefreshTestResults(findRefreshTestPosition(templateSheet),powerTestResults);
        exportThroughputResults(findThroughputTestPosition(templateSheet),throughputTestResults);
        return templateSheet;
    }

    private static void exportPowerTestResults(Cell powerTestPosition, PowerTestResults powerTestResults){
        int rIndex = powerTestPosition.getRowIndex();
        int cIndex = powerTestPosition.getColumnIndex();
        List<Queries.PowerQueryTime> pt = powerTestResults.getQueryTimeList(Queries.QueryType.QUERY);
        int qc = pt.size();
        for(int i = rIndex, index = 0 ; i<rIndex+qc;i++,index++){
            powerTestPosition.getSheet().getRow(i).getCell(cIndex).setCellValue(pt.get(index).getQueryTime());
        }
    }

    private static void exportRefreshTestResults(Cell powerTestPosition, PowerTestResults powerTestResults){
        int rIndex = powerTestPosition.getRowIndex();
        int cIndex = powerTestPosition.getColumnIndex();
        List<Queries.PowerQueryTime> rr = powerTestResults.getQueryTimeList(Queries.QueryType.REFRESH);
        int qc = rr.size();
        for(int i = rIndex, index = 0 ; i<rIndex+qc;i++,index++){
            powerTestPosition.getSheet().getRow(i).getCell(cIndex).setCellValue(rr.get(index).getQueryTime());
        }
    }

    private static void exportThroughputResults(Cell powerTestPosition, ThroughputTestResults throughputTestResults){
        powerTestPosition.setCellValue(throughputTestResults.getEstimation());
    }

    private static void setScaleFactorValue(Cell scaleFactorCell, ScaleFactor scaleFactor){
        scaleFactorCell.setCellValue(scaleFactor.getScaleFactorValue());
    }

    protected abstract XSSFWorkbook prepareWorkbook() throws Exception;

    @Override
    public void export() throws ExportException {
        try {
            XSSFWorkbook workbook = prepareWorkbook();
            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            // Write the output to a file
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd__hh_mm_ss");
            FileOutputStream fileOut = new FileOutputStream(ExportConf.EXPORT_PATH+getResultFileName()+"_"+sdf.format(new Date())+".xlsx");
            workbook.write(fileOut);
            fileOut.close();
        }catch (Exception e){
            throw new ExportException("Can't export results to template",e);
        }
    }

    protected abstract String getResultFileName();
}
