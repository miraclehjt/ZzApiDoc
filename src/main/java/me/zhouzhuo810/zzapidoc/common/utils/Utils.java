package me.zhouzhuo810.zzapidoc.common.utils;

import java.util.Collection;

/**
 * Created by Administrator on 2017/6/30.
 */
public class Utils {

    public static String makePassword(String account, String password, String secretKey) {
        return Md5Util.getMd5(account + "-" + password + "-" + secretKey);
    }

    public static boolean isEmpty(Collection list) {
        return list == null || list.size() < 1;
    }
}
