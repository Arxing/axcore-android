package org.arxing.axutils_android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public final static String PATTERN_FULL_1 = "yyyy-MM-dd HH:mm:ss";
    public final static String PATTERN_FULL_2 = "yyyy/MM/dd HH:mm:ss";
    public final static String PATTERN_DATE_1 = "yyyy-MM-dd";
    public final static String PATTERN_DATE_2 = "yyyy/MM/dd";
    public final static String PATTERN_TIME = "HH:mm:ss";

    /*
     * parse/format
     * */

    public static Date parse(String pattern, String source) {
        Date result = new Date();
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
            result = format.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String format(String pattern, Date date) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    public static String format(String pattern, long timeMills) {
        return format(pattern, new Date(timeMills));
    }

    public static String format(String pattern, String timeString) {
        return format(pattern, parse(pattern, timeString));
    }

    /*
     * specific date
     * */

    /**
     * 現在的時間
     */
    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 今天的起始時間
     */
    public static Date todayBegin() {
        return dateBegin(now());
    }

    /**
     * 今天的結束時間
     */
    public static Date todayEnd() {
        return dateEnd(now());
    }

    /**
     * 今天(等同今天起始時間)
     */
    public static Date today() {
        return todayBegin();
    }

    /**
     * 這個禮拜的頭尾
     */
    public static Date[] thisWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Date end = calendar.getTime();
        while (dayOfWeek != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        Date start = calendar.getTime();
        return new Date[]{start, end};
    }

    public static Date[] thisMonth() {
        Date d1, d2;
        Calendar calendar = Calendar.getInstance();
        int minDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, minDay);
        calendar = dateToCalendar(dateBegin(calendar.getTime()));
        d1 = calendar.getTime();

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        while (dayOfMonth != maxDay && !isDateAfterToady(calendar.getTime())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }
        calendar = dateToCalendar(dateEnd(calendar.getTime()));
        d2 = calendar.getTime();
        return new Date[]{d1, d2};
    }

    /*
     * date builder
     * */

    public static Date newDate(int year, int month, int date, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hourOfDay, minute, second);
        return calendar.getTime();
    }

    public static Date newDate(int year, int month, int date, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hourOfDay, minute);
        return calendar.getTime();
    }

    public static Date newDate(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        return calendar.getTime();
    }

    /*
     * date compare
     * */

    /**
     * 比較兩個日期
     */
    public static int compare(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return 0;
        return date1.compareTo(date2);
    }

    public static int compare(long date1, long date2) {
        return compare(millsToDate(date1), millsToDate(date2));
    }

    public static boolean isDateAfterToady(Date date) {
        date = dateBegin(date);
        Date today = todayBegin();
        return date.compareTo(today) >= 0;
    }

    /*
     * date compute
     * */

    public static Date add(Date date, int field, int amount) {
        Calendar calendar = dateToCalendar(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public static Date addMilliSeconds(Date date, int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    public static Date addSeconds(Date date, int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    public static Date addDays(Date date, int amount) {
        return add(date, Calendar.DAY_OF_YEAR, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    public static Date addYears(Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    /*
     * date field getter/setter
     * */

    public static int getField(Date date, int field) {
        return dateToCalendar(date).get(field);
    }

    public static int getYear(Date date) {
        return getField(date, Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        return getField(date, Calendar.MONTH);
    }

    public static int getDayOfYear(Date date) {
        return getField(date, Calendar.DAY_OF_YEAR);
    }

    public static int getDayOfMonth(Date date) {
        return getField(date, Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeek(Date date) {
        return getField(date, Calendar.DAY_OF_WEEK);
    }

    public static int getDayOfWeekInMonth(Date date) {
        return getField(date, Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    public static int getHour(Date date) {
        return getField(date, Calendar.HOUR);
    }

    public static int getHourOfDay(Date date) {
        return getField(date, Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date date) {
        return getField(date, Calendar.MINUTE);
    }

    public static int getSecond(Date date) {
        return getField(date, Calendar.SECOND);
    }

    public static int getMilliSecond(Date date) {
        return getField(date, Calendar.MILLISECOND);
    }

    public static int getWeekOfMonth(Date date) {
        return getField(date, Calendar.WEEK_OF_MONTH);
    }

    public static int getWeekOfYear(Date date) {
        return getField(date, Calendar.WEEK_OF_YEAR);
    }


    public static void setField(Date date, int field, int val) {
        Calendar calendar = dateToCalendar(date);
        calendar.set(field, val);
        date.setTime(calendar.getTimeInMillis());
    }

    public static void setYear(Date date, int val) {
        setField(date, Calendar.YEAR, val);
    }

    public static void setMonth(Date date, int val) {
        setField(date, Calendar.MONTH, val);
    }

    public static void setDayOfYear(Date date, int val) {
        setField(date, Calendar.DAY_OF_YEAR, val);
    }

    public static void setDayOfMonth(Date date, int val) {
        setField(date, Calendar.DAY_OF_MONTH, val);
    }

    public static void setDayOfWeek(Date date, int val) {
        setField(date, Calendar.DAY_OF_WEEK, val);
    }

    public static void setDayOfWeekInMonth(Date date, int val) {
        setField(date, Calendar.DAY_OF_WEEK_IN_MONTH, val);
    }

    public static void setHour(Date date, int val) {
        setField(date, Calendar.HOUR, val);
    }

    public static void setHourOfDay(Date date, int val) {
        setField(date, Calendar.HOUR_OF_DAY, val);
    }

    public static void setMinute(Date date, int val) {
        setField(date, Calendar.MINUTE, val);
    }

    public static void setSecond(Date date, int val) {
        setField(date, Calendar.SECOND, val);
    }

    public static void setMilliSecond(Date date, int val) {
        setField(date, Calendar.MILLISECOND, val);
    }

    public static void setWeekOfMonth(Date date, int val) {
        setField(date, Calendar.WEEK_OF_MONTH, val);
    }

    public static void setWeekOfYear(Date date, int val) {
        setField(date, Calendar.WEEK_OF_YEAR, val);
    }

    /*
     * other method
     * */

    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static long dateToMills(Date date) {
        return date.getTime();
    }

    public static Date millsToDate(long timeMills) {
        return new Date(timeMills);
    }

    public static Date dateBegin(Date date) {
        Calendar calendar = dateToCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    public static Date dateEnd(Date date) {
        Calendar calendar = dateToCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
        return calendar.getTime();
    }
}
