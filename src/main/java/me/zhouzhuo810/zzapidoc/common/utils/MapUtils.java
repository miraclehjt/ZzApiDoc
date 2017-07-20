package me.zhouzhuo810.zzapidoc.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/5.
 */
public class MapUtils {

    private Map<String, Object> mMap;

    public MapUtils() {
        mMap = new HashMap<String, Object>();
    }

    public MapUtils put(String key, String value) {
        mMap.put(key, value == null ? "" : value);
        return this;
    }

    public MapUtils put(String key, Integer value) {
        mMap.put(key, value + "");
        return this;
    }

    public MapUtils put(String key, Boolean value) {
        mMap.put(key, value + "");
        return this;
    }

    public MapUtils put(String key, Float value) {
        mMap.put(key, value + "");
        return this;
    }

    public MapUtils put(String key, List value) {
        if (Utils.isEmpty(value))
            value = new ArrayList();
        mMap.put(key, value);
        return this;
    }

    public MapUtils put(String key, Object value) {
        mMap.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return mMap;
    }

    public static Map<String, Object> getKetValueMap(String title, String content) {
        MapUtils m = new MapUtils();
        m.put("title", title);
        m.put("content", content);
        return m.build();
    }
}
