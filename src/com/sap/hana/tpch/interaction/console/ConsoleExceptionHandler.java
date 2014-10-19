package com.sap.hana.tpch.interaction.console;

/**
 * Created by Alex on 29/09/2014.
 */
public class ConsoleExceptionHandler {

    Exception exception;

    public ConsoleExceptionHandler(Exception e){
        exception = e;
    }

    public void displayException(){
        System.out.println(exception.getMessage());
        exception.printStackTrace();
    }

    public static boolean isContinue(){
        return TestHana.yesNoAsk("Continue work (y/n)? ");
    }

    public static void processStop(){
        System.exit(1);
    }

    public static void main(String[] arg){
    }
}
