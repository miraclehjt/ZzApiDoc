package me.zhouzhuo810.zzapidoc.common.utils;

/**
 * Created by Administrator on 2017/7/5.
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isEqual(String a, String b) {
        if (isEmpty(a) || isEmpty(b))
            return false;
        return a.equals(b);
    }
}
