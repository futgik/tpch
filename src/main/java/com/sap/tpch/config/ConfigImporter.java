package com.sap.tpch.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;

import java.io.File;

/**
 * Created by Alex on 26/10/2014.
 * Import configuration params from .properties file.
 */
public class ConfigImporter {

    private static Config c = null;

    private ConfigImporter(){}

    private static void ImportConfig(String configFileName){
        if(c==null)
            c = ConfigFactory.parseFile(new File(configFileName),
                    ConfigParseOptions.defaults().setSyntax(ConfigSyntax.PROPERTIES).setAllowMissing(true));
    }

    /**
     * Get string property value.
     * @param paramName property name.
     * @return string property value.
     */
    public static String getStringValue(String paramName){
        ImportConfig(TPCHConfig.CONFIG_FILE_NAME);
        return c.getString(paramName);
    }

    /**
     * Get Integer property value.
     * @param paramName property name.
     * @return Integer property value.
     */
    public static Integer getIntValue(String paramName){
        ImportConfig(TPCHConfig.CONFIG_FILE_NAME);
        return c.getInt(paramName);
    }

    /**
     * Get Boolean property value.
     * @param paramName property name.
     * @return Boolean property value.
     */
    public static Boolean getBooleanValue(String paramName){
        ImportConfig(TPCHConfig.CONFIG_FILE_NAME);
        return c.getBoolean(paramName);
    }
}
