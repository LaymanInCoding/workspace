package com.witmoon.xmb.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String getDateBeforeOrAfter(Date curDate, int iDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DAY_OF_MONTH, iDate);
        return formatter.format(cal.getTime());
    }

    public static String getDateBeforeOrAfterMD(Date curDate, int iDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("M月d日", Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DAY_OF_MONTH, iDate);
        return formatter.format(cal.getTime());
    }

    public static Date getCurrentDate(){
        return  new Date(System.currentTimeMillis());
    }

    public static Long dateToTimestamp(String idate){
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
        Date date= null;
        try {
            date = simpleDateFormat .parse(idate);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (ParseException e) {
            return 0L;
        }
    }

    public static String setCurrentDateTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    //字符串转日期
    public static Date strToDateMD(String str) {
        SimpleDateFormat format = new SimpleDateFormat("M月d日", Locale.CHINA);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //字符串转日期
    public static Date strToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String compareYMD(Date src, Date dst) {
        Calendar srcCal = Calendar.getInstance();
        srcCal.setTime(src);
        Calendar dstCal = Calendar.getInstance();
        dstCal.setTime(dst);

        // 比较年月日
        int year = dstCal.get(Calendar.YEAR) - srcCal.get(Calendar.YEAR);
        int month = dstCal.get(Calendar.MONTH) - srcCal.get(Calendar.MONTH);
        int day = dstCal.get(Calendar.DAY_OF_MONTH)
                - srcCal.get(Calendar.DAY_OF_MONTH);
        // 实际年份差：
        year = year
                - ((month > 0) ? 0 : ((month < 0) ? 1 : ((day >= 0 ? 0 : 1))));
        // 实际月份差：
        Log.e("month", month + "--" + day);
        month = (month <= 0) ? (day > 0 ? 12 + month : 12 + month - 1)
                : (day >= 0 ? month : month - 1);
        // 去除年月之后的剩余的实际天数差：
        dstCal.add(Calendar.MONTH, -1);
        day = (day <= 0) ? (perMonthDays(dstCal)) + day : day;
        String ages;
        if(year == 0){
             ages = month + "月" + day + "天";
        }else{
             ages = year + "年" + month + "月" + day + "天";
        }

        return ages;
    }

    /**
     * 判断一个时间所在月有多少天
     *
     * @param Calendar
     *            具体时间的日历对象
     * @throws ParseException
     */
    public static int perMonthDays(Calendar cal) {
        int maxDays = 0;
        int month = cal.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                maxDays = 31;
                break;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                maxDays = 30;
                break;
            case Calendar.FEBRUARY:
                if (isLeap(cal.get(Calendar.YEAR))) {
                    maxDays = 29;
                } else {
                    maxDays = 28;
                }
                break;
        }
        return maxDays;
    }

    /**
     * 判断某年是否是闰年
     *
     * @param year
     *            年份
     * @throws ParseException
     */
    public static boolean isLeap(int year) {
        boolean leap = false;
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            leap = true;
        }
        return leap;
    }

    //两个日期的时间间隔
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }
}
