package com.sap.hana.tpch.interaction.console;

import com.sap.hana.tpch.benchmark.*;
import com.sap.hana.tpch.benchmark.results.*;
import com.sap.hana.tpch.checks.Checks;
import com.sap.hana.tpch.exception.ExportException;
import com.sap.hana.tpch.exception.TestException;
import com.sap.hana.tpch.export.ExcelExport;
import com.sap.hana.tpch.remoute.*;
import com.sap.hana.tpch.remoute.hana.HDBBinaryPreparation;
import com.sap.hana.tpch.remoute.hana.HDBDBGenPreparation;
import com.sap.hana.tpch.types.ScaleFactor;

import java.util.*;

/**
 * Created by Alex on 29/09/2014.
 */
public class TestHana {

    static ScaleFactor scaleFactor = null;

    public static void main(String[] arg){
        start();
    }

    public static void showHelp(){
        System.out.println("This is list of available commands:");
        System.out.println("help - show help");
        System.out.println("deploy - deploy data to server and to hana");
        System.out.println("q - end program");
    }

    public static void deploy(){
        serverDeploy();
        if(yesNoAsk("Do you want to make hana environment?"))
            hanaDeploy();
        if(yesNoAsk("Do you want to run hana test?"))
            hanaTest();
    }

    public static void serverDeploy(){
        System.out.println("Server data preparation...");
        try {
            MachineEnvPreparation.prepareEnvironment(new ConsolePreparationMonitor());
        }catch (Exception e){
            new ConsoleExceptionHandler(e).displayException();
            ConsoleExceptionHandler.processStop();
        }
    }

    public static void hanaTest(){
        System.out.print("To show the list of available test enter \"list\", to exit enter \"q\":");
        while (true) {
            Scanner user_input = new Scanner(System.in);
            String answer = user_input.next();
            switch (answer) {
                case "q":
                    return;
                case "list":
                    showHanaTestList();
                    break;
                case "tpch":
                    makeTPCHTest();
                    break;
                case "power":
                    makePowerTest();
                    break;
                case "load":
                    makeLoadTest();
                    break;
                case "refresh":
                    makeRefreshTest();
                    break;
                case "single":
                    makeSingleTest();
                    break;
                case "tpch_series":
                    makeTPCHSeries();

            }
            inputCommand();
        }
    }

    private static void makeTPCHSeries() {
        System.out.println(String.format("Executing series of tpch tests"));
        try{
            if(scaleFactor == null)
                scaleFactor = setScaleFactor();
            int repetitionCount = setRepetitionCount();
            TPCHSeriesMetrics m = TestExecutor.getTestExecutor(scaleFactor, new ConsoleBenchmarkProcessMonitor()).doTPCHSeriesBenchmark(repetitionCount);
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
            System.out.print("Please enter query name (available names "+Service.join(", ",getQueriesFileNameList(Queries.getQueriesByType(Queries.QueryType.QUERY)) ) +"): ");
            Scanner user_input = new Scanner(System.in);
            Queries.Query q = Queries.getQueryByFileName(user_input.nextLine());
            if(scaleFactor == null)
                scaleFactor = setScaleFactor();
            SingleQueryTestResults results = TestExecutor.getTestExecutor(scaleFactor,new ConsoleBenchmarkProcessMonitor()).doSingleQueryTest(q);
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
            if(scaleFactor == null)
                scaleFactor = setScaleFactor();
            RefreshResults tr = TestExecutor.getTestExecutor(scaleFactor,new ConsoleBenchmarkProcessMonitor()).doRefreshTest();
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
            if(scaleFactor == null)
                scaleFactor = setScaleFactor();
            ThroughputTestResults tr = TestExecutor.getTestExecutor(scaleFactor,new ConsoleBenchmarkProcessMonitor()).doThroughputTest();
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
            if(scaleFactor == null)
                scaleFactor = setScaleFactor();
            PowerTestResults ptr = TestExecutor.getTestExecutor(scaleFactor, new ConsoleBenchmarkProcessMonitor()).doPowerTest();
            printQueriesTimes(ptr.getQueryTimeList());
            System.out.println(System.out.format("sum query time: %f s.", ptr.getSumQueryTime()));
        } catch (TestException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }

    private static void printQueriesTimes(List<Queries.PowerQueryTime> powerQueryTimes){
        Collections.sort(powerQueryTimes);
        for(Queries.PowerQueryTime pqt : powerQueryTimes){
            System.out.println(String.format("query %s executed in %f s.",pqt.getQuery().getQueryName(),pqt.getQueryTime()));
        }
    }

    private static void makeTPCHTest() {
        System.out.println(String.format("Executing tpch test"));
        try{
            if(scaleFactor == null)
                scaleFactor = setScaleFactor();

            TPCHMetrics m = TestExecutor.getTestExecutor(scaleFactor, new ConsoleBenchmarkProcessMonitor()).doTPCBenchmark();
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

    public static void hanaDeploy(){
        System.out.print("Do you want import data from binary, generate it, or you want to cancel? (b/g/c) ");
        while (true) {
            Scanner user_input = new Scanner(System.in);
            String answer = user_input.next();
            switch (answer) {
                case "b":
                    hanaDeployFromBinary();
                    return;
                case "g":
                    hanaDeployFromTables();
                    return;
                case "c":
                    return;
            }
            inputCommand();
        }
    }

    public static void hanaDeployFromBinary(){
        System.out.println("Binary deploying.");
        System.out.print("Please enter path to binary file on server: ");
        Scanner user_input = new Scanner(System.in);
        inputCommand();
        String filePath = user_input.next();
        if(!Checks.isFileExist(filePath)) {
            System.out.println("File not exist");
            return;
        }
        try {
            HDBBinaryPreparation.prepareDBFromBinary(filePath, new EnvPreparationMonitor() {
                @Override
                public void init(String startMessage) {

                }

                @Override
                public void process(String processOutput) {

                }

                @Override
                public void end(String endMessage) {

                }
            });
        }
        catch(Exception e){
            new ConsoleExceptionHandler(e).displayException();
            ConsoleExceptionHandler.processStop();
        }
    }

    public static void hanaDeployFromTables(){
        System.out.println("Deploying from tables.");
        try {
            scaleFactor = setScaleFactor();
            QueryDataPreparation.prepareData(scaleFactor, new ConsolePreparationMonitor());
            HDBDBGenPreparation.prepareDBFromDBGen(scaleFactor, new ConsolePreparationMonitor());
        }catch (Exception e){
            new ConsoleExceptionHandler(e).displayException();
        }
    }

    public static ScaleFactor setScaleFactor(){
        ScaleFactor sf;
        try{
            sf = new ScaleFactor(getIntFromPrint("Please enter scale factor (1/30/100/300/1000/3000/10000/30000/100000): ", "Invalid scale factor value"));
            return sf;
        }
        catch (Exception e){
            System.out.println("Invalid scale factor value");
        }
        setScaleFactor();
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

    public static void start(){
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
                case "h":
                    showHelp();
                    break;
                case "deploy":
                    deploy();
                    break;
            }
        }
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

    private static class ConsolePreparationMonitor implements EnvPreparationMonitor{

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
