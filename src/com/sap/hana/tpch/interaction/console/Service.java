package com.sap.hana.tpch.interaction.console;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Alex on 12/10/2014.
 */
public class Service {

    public static String join(String join, Iterable strings) {
        Iterator i = strings.iterator();
        if (!i.hasNext()) {
            return "";
        } else {
            String f = i.next().toString();
            StringBuilder sb = new StringBuilder();
            sb.append(f);
            while (i.hasNext()) {
                sb.append(join).append(i.next());
            }
            return sb.toString();
        }
    }

    public static void main(String[] arg){
        String[] a = {"1","2","3"};
        System.out.print(join(",", Arrays.asList(a)));
    }

    public static double roundMilliseconds(int milliseconds){
        if(milliseconds < 50) return 0.1;
        return (Math.round(milliseconds/100.0)/10.0);
    }
}
