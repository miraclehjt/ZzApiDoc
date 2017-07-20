package me.zhouzhuo810.zzapidoc.common.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class JsonUtil {

    /**
     * json化
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static String object2String(Object data) throws IOException {
        ObjectMapper om = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
        om.setFilters(filterProvider);
        return om.writeValueAsString(data);
    }

    public static List<LinkedHashMap<String, String>> string2ListObj(String json) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(json, new TypeReference<List<LinkedHashMap<String, String>>>() {
        });
    }

    public static List<String> string2ListString(String json) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(json, new TypeReference<List<String>>() {
        });
    }
}
