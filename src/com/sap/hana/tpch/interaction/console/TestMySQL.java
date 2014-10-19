package com.sap.hana.tpch.interaction.console;

import com.sap.hana.tpch.remoute.QueryDataPreparation;
import com.sap.hana.tpch.remoute.EnvPreparationMonitor;
import com.sap.hana.tpch.remoute.MachineEnvPreparation;
import com.sap.hana.tpch.remoute.mysql.MySQLGenPreparation;
import com.sap.hana.tpch.types.ScaleFactor;

import java.util.Scanner;

/**
 * Created by Alex on 09.10.2014.
 */
public class TestMySQL {

    static ScaleFactor scaleFactor;

    public static void main(String[] arg){
        start();
    }

    public static void start(){
        System.out.println("Hello to get list of available commands enter help.");
        while (true) {
            Scanner user_input = new Scanner(System.in);
            inputCommand();
            String command = user_input.next();
            switch (command){
                case "exit" :
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

    public static void inputCommand(){
        System.out.print("Enter command: ");
    }

    public static void endSuccess(){
        System.out.print("Bye!");
        System.exit(0);
    }

    public static void showHelp(){
        System.out.println("This is list of available commands:");
        System.out.println("help - show help");
        System.out.println("deploy - deploy data to server and to hana");
        System.out.println("exit - end program");
    }

    public static void deploy(){
        serverDeploy();
        if(yesNoAsk("Do you want to make mysql environment?"))
            mySQLDeploy();
//        if(yesNoAsk("Do you want to run tables test?"))
//            hanaTest();
    }

    public static void mySQLDeploy(){
        System.out.print("Do you want import data from binary, generate it, or you want to cancel? (b/g/c) ");
        while (true) {
            Scanner user_input = new Scanner(System.in);
            String answer = user_input.next();
            switch (answer) {
//                case "b":
//                    hanaDeployFromBinary();
//                    return;
                case "g":
                    mySQLDeployFromTables();
                    return;
                case "c":
                    return;
            }
            inputCommand();
        }
    }

    public static void mySQLDeployFromTables(){
        System.out.println("Deploying from tables.");
        while(true){
            System.out.print("Please enter scale factor (1/30/100/300/1000/3000/10000/30000/100000): ");
            Scanner user_input = new Scanner(System.in);
            String scaleFactor = user_input.next();
            try {
                TestMySQL.scaleFactor = new ScaleFactor(Integer.parseInt(scaleFactor));
                break;
            }
            catch (Exception e){
                System.out.println("Invalid scale factor value");
            }
        }
        try {
            QueryDataPreparation.prepareData(scaleFactor, new EnvPreparationMonitor() {
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
            });
            MySQLGenPreparation.prepareDBFromDBGen(scaleFactor, new EnvPreparationMonitor() {
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
            });
        }catch (Exception e){
            new ConsoleExceptionHandler(e).displayException();
        }
    }

    public static void serverDeploy(){
        System.out.println("Server data preparation...");
        try {
            MachineEnvPreparation.prepareEnvironment(new EnvPreparationMonitor() {
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
            });
        }catch (Exception e){
            new ConsoleExceptionHandler(e).displayException();
            ConsoleExceptionHandler.processStop();
        }
    }

    public static boolean yesNoAsk(String message){
        System.out.print(message+" (y/n) ");
        while (true) {
            Scanner user_input = new Scanner(System.in);
            String askUserResponse = user_input.next();
            if(askUserResponse.toLowerCase().equals("y")) return true;
            else if(askUserResponse.toLowerCase().equals("n")) return false;
            System.out.print("Enter command: ");
        }
    }

}
