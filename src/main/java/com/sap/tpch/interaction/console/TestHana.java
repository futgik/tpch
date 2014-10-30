package com.sap.tpch.interaction.console;

import com.sap.tpch.benchmark.*;
import com.sap.tpch.benchmark.results.*;
import com.sap.tpch.checks.Checks;
import com.sap.tpch.config.TPCHConfig;
import com.sap.tpch.export.HanaDataExport;
import com.sap.tpch.remoute.DatabaseState;
import com.sap.tpch.exception.ExportException;
import com.sap.tpch.exception.TestException;
import com.sap.tpch.export.ExcelExport;
import com.sap.tpch.remoute.*;
import com.sap.tpch.remoute.hana.HDBBinaryPreparation;
import com.sap.tpch.remoute.hana.HDBDBGenPreparation;
import com.sap.tpch.service.Strings;
import com.sap.tpch.types.ScaleFactor;

import java.util.*;

/**
 * Created by Alex on 29/09/2014.
 */
public class TestHana {

    public static void main(String[] arg){
        start();
    }

    public static void start(){
        System.out.println("Preparing...");
        serverDeploy();
        System.out.println("Getting information about current state...");
        DatabaseState.updateState();
        System.out.println("Hello to get list of available commands enter help.");
        while (true) {
            Scanner user_input = new Scanner(System.in);
            inputCommand();
            String command = user_input.next();
            switch (command){
                case "q" :
                    endSuccess();
                    return;
                case "help":
                    showHelp();
                    break;
                case "generate":
                    generateTableData();
                    break;
                case "importTables":
                    importDataToTables();
                    break;
                case "importBinary":
                    importDataFromBinary();
                    break;
                case "exportBinary":
                    exportTableDataToBinary();
                    break;
                case "test":
                    hanaTest();
                    break;
            }
        }
    }

    private static void showHelp(){
        System.out.println("This is list of available commands:");
        System.out.println("help - show help");
        System.out.println("generate - generate table data using dbgen");
        System.out.println("importTables - import data to tables");
        System.out.println("importBinary - import data to tables from binary file");
        System.out.println("exportBinary - export schema to binary format");
        System.out.println("test - open tpch tests");
        System.out.println("q - end program");
    }

    private static void generateTableData(){
        try {
            ScaleFactor scaleFactor = getScaleFactor();
            QueryDataPreparation.prepareData(scaleFactor, new ConsolePreparationMonitor());
        }catch (Exception e){
            new ConsoleExceptionHandler(e).displayException();
        }
    }

    private static void exportTableDataToBinary(){
        System.out.println("Exporting schema "+ TPCHConfig.SCHEMA_NAME);
        System.out.println("Please enter path to save data: ");
        Scanner user_input = new Scanner(System.in);
        inputCommand();
        String filePath = user_input.next();
        try {
            HanaDataExport.ExportHanaData(filePath, new ConsolePreparationMonitor());
        }
        catch (Exception e){
            new ConsoleExceptionHandler(e).displayException();
        }
    }

    private static void importDataToTables(){
        if(!ServerState.isDBGenDataExists()){
            System.out.println("Before import data, you must generate it.");
            return;
        }
        System.out.println("Deploying from tables.");
        try {
            HDBDBGenPreparation.prepareDBFromDBGen(new ConsolePreparationMonitor());
        }catch (Exception e){
            new ConsoleExceptionHandler(e).displayException();
        }
    }

    private static void importDataFromBinary(){
        System.out.println("Binary deploying.");
        System.out.println("Please enter path to binary file on server: ");
        Scanner user_input = new Scanner(System.in);
        inputCommand();
        String filePath = user_input.next();
        if(!Checks.isDirectoryExist(filePath)) {
            System.out.println("File not exist");
            return;
        }
        System.out.println("Exporting file into database");
        try{
            HDBBinaryPreparation.prepareDBFromBinary(filePath,new ConsolePreparationMonitor());
        }catch (Exception e){
            new ConsoleExceptionHandler(e).displayException();
        }
    }

    private static void serverDeploy(){
        System.out.println("Server data preparation...");
        try {
            MachineEnvPreparation.prepareEnvironment(new ConsolePreparationMonitor());
        }catch (Exception e){
            new ConsoleExceptionHandler(e).displayException();
            ConsoleExceptionHandler.processStop();
        }
    }

    private static void hanaTest(){
        if(!DatabaseState.isTablesDataExist()){
            System.out.println("Before running test you must have database with tables and table data");
            return;
        }
        System.out.print("To show the list of available test enter \"list\", to exit enter \"q\":");
        while (true) {
            Scanner user_input = new Scanner(System.in);
            String answer = user_input.next();
            switch (answer){
                case "q":
                    return;
                case "list":
                    showHanaTestList();
                    break;
                case "tpch":
                    makeTPCHTest();
                    return;
                case "power":
                    makePowerTest();
                    return;
                case "load":
                    makeLoadTest();
                    return;
                case "refresh":
                    makeRefreshTest();
                    return;
                case "single":
                    makeSingleTest();
                    return;
                case "tpch_series":
                    makeTPCHSeries();
                    return;
            }
            inputCommand();
        }
    }

    private static void makeTPCHSeries() {
        System.out.println(String.format("Executing series of tpch tests"));
        try{
            int repetitionCount = setRepetitionCount();
            TPCHSeriesMetrics m = TestExecutor.getTestExecutor(BenchmarkScaleFactor.getScaleFactor(), new ConsoleBenchmarkProcessMonitor()).doTPCHSeriesBenchmark(repetitionCount);
            System.out.println(String.format("Power size: %f",m.getAveragedPowerSize()));
            System.out.println(String.format("Throughput size: %f",m.getAveragedThroughputSize()));
            System.out.println(String.format("QphH size: %f",m.getAveragedQphHSize()));
            ExcelExport.exportTPCHSeriesToTemplate(m);
        } catch (TestException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        }catch (ExportException e){
            e.printStackTrace();
        }
    }

    private static List<String> getQueriesFileNameList(List<Queries.Query> queries){
        List<String> result = new ArrayList<>();
        for(Queries.Query q : queries){
            result.add(q.getQueryFileName());
        }
        return result;
    }

    private static void makeSingleTest() {
        try {
            System.out.print("Please enter query name (available names "+ Strings.join(", ", getQueriesFileNameList(Queries.getQueriesByType(Queries.QueryType.QUERY))) +"): ");
            Scanner user_input = new Scanner(System.in);
            Queries.Query q = Queries.getQueryByFileName(user_input.nextLine());
            SingleQueryTestResults results = TestExecutor.getTestExecutor(BenchmarkScaleFactor.getScaleFactor(),new ConsoleBenchmarkProcessMonitor()).doSingleQueryTest(q);
            System.out.println("Test "+q.getQueryName()+" executed in: "+results.getEstimation()+" s.");
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }

    private static void makeRefreshTest() {
        System.out.println(String.format("Executing refresh test"));
        try{
            PowerTestResult tr = TestExecutor.getTestExecutor(BenchmarkScaleFactor.getScaleFactor(),new ConsoleBenchmarkProcessMonitor()).doRefreshTest();
            printQueriesTimes(tr.getQueryTimeList());
            System.out.println(String.format("sum query time: %f s.",tr.getSumQueryTime()));
        } catch (TestException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }

    private static void makeLoadTest() {
        System.out.println(String.format("Executing load test"));
        try{
            ThroughputTestResult tr = TestExecutor.getTestExecutor(BenchmarkScaleFactor.getScaleFactor(),new ConsoleBenchmarkProcessMonitor()).doThroughputTest();
            System.out.println(String.format("query execute in %f s.",tr.getEstimation()));
        } catch (TestException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }

    private static void makePowerTest() {
        System.out.println(String.format("Executing power test"));
        try {
            PowerTestResult ptr = TestExecutor.getTestExecutor(BenchmarkScaleFactor.getScaleFactor(), new ConsoleBenchmarkProcessMonitor()).doPowerTest();
            printQueriesTimes(ptr.getQueryTimeList());
            System.out.println(System.out.format("sum query time: %f s.", ptr.getSumQueryTime()));
        } catch (TestException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }

    private static void printQueriesTimes(List<Queries.QueryTime> powerQueryTimes){
        Collections.sort(powerQueryTimes);
        for(Queries.QueryTime pqt : powerQueryTimes){
            System.out.println(String.format("query %s executed in %f s.",pqt.getQuery().getQueryName(),pqt.getTime()));
        }
    }

    private static void makeTPCHTest() {
        System.out.println(String.format("Executing tpch test"));
        try{
            TPCHMetrics m = TestExecutor.getTestExecutor(BenchmarkScaleFactor.getScaleFactor(), new ConsoleBenchmarkProcessMonitor()).doTPCBenchmark();
            System.out.println(String.format("Power size: %f",m.getPowerSize()));
            System.out.println(String.format("Throughput size: %f",m.getThroughputSize()));
            System.out.println(String.format("QphH size: %f",m.getQphHSize()));
            ExcelExport.exportTPCHToTemplate(m);
        } catch (TestException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        } catch (ExportException e){
            e.printStackTrace();
        }
    }

    private static void showHanaTestList() {
        System.out.println("q - exit test mode");
        System.out.println("Available Hana test list:");
        System.out.println("tpch - run tpch single test");
        System.out.println("power - run power test");
        System.out.println("load - run load test");
        System.out.println("refresh - run refresh test");
        System.out.println("single  - run single query");
        System.out.println("tpch_series - run tpch test sequence");
    }

    public static ScaleFactor getScaleFactor(){
        ScaleFactor sf;
        try{
            sf = new ScaleFactor(getIntFromPrint("Please enter scale factor (1/30/100/300/1000/3000/10000/30000/100000): ", "Invalid scale factor value"));
            return sf;
        }
        catch (Exception e){
            System.out.println("Invalid scale factor value");
        }
        getScaleFactor();
        return null;
    }

    public static int setRepetitionCount(){
        return getIntFromPrint("Please enter how much run test: ", "You enter invalid value");
    }

    public static int getIntFromPrint(String helloMessage, String invalidPrintMessage){
        int value;
        while(true){
            System.out.print(helloMessage);
            Scanner user_input = new Scanner(System.in);
            try {
                value = Integer.parseInt(user_input.next());
                break;
            }
            catch (Exception e){
                System.out.println(invalidPrintMessage);
            }
        }
        return value;
    }

    public static void endSuccess(){
        System.out.print("Bye!");
        System.exit(0);
    }

    public static void inputCommand(){
            System.out.print("Enter command: ");
        }

    public static boolean yesNoAsk(String message){
        System.out.print(message + " (y/n) ");
        while (true) {
            Scanner user_input = new Scanner(System.in);
            String askUserResponse = user_input.next();
            if(askUserResponse.toLowerCase().equals("y")) return true;
            else if(askUserResponse.toLowerCase().equals("n")) return false;
            System.out.print("Enter command: ");
        }
    }

    private static class ConsolePreparationMonitor implements PreparationMonitor {

        @Override
        public void init(String startMessage) {
            System.out.println(startMessage);
        }

        @Override
        public void process(String processOutput) {
            System.out.println(processOutput);
        }

        @Override
        public void end(String endMessage) {
            System.out.println(endMessage);
        }
    }

    private static class ConsoleBenchmarkProcessMonitor implements BenchmarkProcessMonitor{
        @Override
        public void prepare(String testName) {
            System.out.println(String.format("Prepare test %s", testName));
        }

        @Override
        public void process(String processOutput) {
            System.out.println(processOutput);
        }

        @Override
        public void end() {
            System.out.println("");
        }
    }
}
