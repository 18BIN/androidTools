package com.tools.os;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018/10/31.
 * 类描述:日期 时间工具类
 */

public class DateUtil {

    //将时间戳转换为时间(传入时间戳和转换的格式，比如：yyyy-MM-dd HH:mm)
    public static String stampToDate(String time, String time_format) {
        String res = "";
        String[] formats = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyy-MM", "MM-dd",
                "MM-dd HH:mm", "yyyy-MM HH:mm"};
        if (!Arrays.asList(formats).contains(time_format)) {
            return res;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(time_format, Locale.getDefault());
        if (time.length() == 10) {
            long lt1 = Long.valueOf(time);
            Date date1 = new Date(lt1 * 1000);
            res = simpleDateFormat.format(date1);
            return res;
        } else if (time.length() == 13) {
            long lt2 = Long.valueOf(time);
            Date date2 = new Date(lt2);
            res = simpleDateFormat.format(date2);
            return res;
        } else {
            return res;
        }
    }

    //时间转为时间戳(传入字符串格式的时间和日期格式)
    public static long dateToStamp(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //转换成字符串(单位:毫秒)
        String stampStr = String.valueOf(date.getTime());
        //截取前10位(单位:秒)
        return Long.valueOf(stampStr.substring(0, 10));
    }

    //获取当前日期(2018-10-28)
    public static String getCurrentDate() {
        String date;
        Calendar calendar = Calendar.getInstance();
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH) + 1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar.get(Calendar.MINUTE);
        //秒
        int second = calendar.get(Calendar.SECOND);
        date = year + "-" + formatTimeUnit(month) + "-" + formatTimeUnit(day) + " ";
        return date;
    }

    //获取当前日期(2018-10-28)
    public static String getCurrentDate(Calendar calendar) {
        String date;
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH) + 1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar.get(Calendar.MINUTE);
        //秒
        int second = calendar.get(Calendar.SECOND);
        date = year + "-" + formatTimeUnit(month) + "-" + formatTimeUnit(day) + " ";
        return date;
    }

    //获取当前时间的后一天时间
    public static Calendar getAfterDay() {
        Calendar cl = Calendar.getInstance();
        //使用roll方法进行回滚到后一天的时间
        //cl.roll(Calendar.DATE, 1);
        //使用set方法直接设置时间值
        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day + 1);
        return cl;
    }

    //获取当前时间的前一天时间
    public static Calendar getBeforeDay() {
        Calendar cl = Calendar.getInstance();
        //使用roll方法进行回滚到前一天的时间
        //cl.roll(Calendar.DATE, 1);
        //使用set方法直接设置时间值
        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day - 1);
        return cl;
    }

    //根据传入的数字判断，小于10前面加0返回，大于10转换成字符串返回
    public static String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    //根据传入的时间判断是否在同一天
    public static String judgeOneDay(String startTime, String endTime) {
        String time_str = "";
        String time1 = stampToDate(startTime, "yyyy-MM-dd HH:mm");
        String time2 = stampToDate(endTime, "yyyy-MM-dd HH:mm");
        if (!TextUtils.isEmpty(time1) && !TextUtils.isEmpty(time2)) {
            //截取年月日部分进行比较
            String date1 = time1.substring(0, 10);
            String date2 = time2.substring(0, 10);
            if (date1.equals(date2)) {
                //同一天
                time_str = time_str + date1 + " " + time1.substring(11) + "-" + time2.substring(11);
                return time_str;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    //获取传入的时间的当天的最后一秒
    public static String getCurrentDayToLast(String currentTime) {
        if (!TextUtils.isEmpty(currentTime)) {
            //截取年月日部分
            String date1 = currentTime.substring(0, 10);
            long date1Long = dateToStamp(date1, "yyyy-MM-dd") + 86400;
            return stampToDate(String.valueOf(date1Long), "yyyy-MM-dd HH:mm");
        } else {
            return "";
        }
    }

    //获取传入的时间的当天的第一秒
    public static String getCurrentDayToFirst(String currentTime) {
        if (!TextUtils.isEmpty(currentTime)) {
            //截取年月日部分
            String date1 = currentTime.substring(0, 10);
            long date1Long = dateToStamp(date1, "yyyy-MM-dd");
            return stampToDate(String.valueOf(date1Long), "yyyy-MM-dd HH:mm");
        } else {
            return "";
        }
    }

}
