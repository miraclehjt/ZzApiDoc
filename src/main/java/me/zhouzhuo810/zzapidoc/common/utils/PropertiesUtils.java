package me.zhouzhuo810.zzapidoc.common.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zz on 2017/7/10.
 */
public class PropertiesUtils {

    public static Properties getProperties(String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(fileName+".properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
