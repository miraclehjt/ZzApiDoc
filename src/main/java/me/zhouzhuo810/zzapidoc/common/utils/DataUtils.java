package me.zhouzhuo810.zzapidoc.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/7/4.
 */
public class DataUtils {

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static SimpleDateFormat formatMD = new SimpleDateFormat("M月-d日", Locale.CHINA);
    private static SimpleDateFormat formatWeekTime = new SimpleDateFormat("EEE a H:mm", Locale.CHINA);
    private static SimpleDateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

    public static String formatDate(Date date) {
        if (date == null)
            return "";
        return format.format(date);
    }

    public static String formatDataMD(Date date) {
        if (date == null)
            return "";
        return formatMD.format(date);
    }

    public static String formatDataToWeekTime(Date date) {
        if (date == null)
            return "";
        return formatWeekTime.format(date);
    }

    public static Date formatDate(String data) throws ParseException {
        return format.parse(data);
    }

    public static String formatDay(Date date) {
        if (date == null)
            return "";
        return formatDay.format(date);
    }

    public static String formatTime(Date date) {
        if (date == null)
            return "";
        return formatTime.format(date);
    }

}
