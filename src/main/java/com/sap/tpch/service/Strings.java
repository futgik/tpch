package com.sap.tpch.service;

import java.util.Iterator;

/**
 * Created by Alex on 26/10/2014.
 * Service functions connecting with strings.
 */
public class Strings {

    /**
     * Clear string from escape symbols "\n\r".
     * @param string clearing string.
     * @return string without "\n\r" symbols.
     */
    public static String clearString(String string){
        return string.trim().replaceAll("[\n\r]", "");
    }

    /**
     * Join iterable sequence using join symbol
     * @param join join symbol
     * @param strings string sequence
     * @return joined string.
     */
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
}
