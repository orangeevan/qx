package com.haipaite.common.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.scheduling.support.CronSequenceGenerator;


public class DateUtils extends org.apache.commons.lang.time.DateUtils {
    public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_SHORT_TIME = "HH:mm";
    public static final Date LONG_BEFORE_TIME = string2Date("1970-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");

    public static final Date LONG_AFTER_TIME = string2Date("2048-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");


    public static boolean isSameWeek(int year, int week, int firstDayOfWeek) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(firstDayOfWeek);
        return (year == cal.get(1) && week == cal.get(3));
    }


    public static boolean isSameWeek(Date time, int firstDayOfWeek) {
        if (time == null) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.setFirstDayOfWeek(firstDayOfWeek);
        return isSameWeek(cal.get(1), cal.get(3), firstDayOfWeek);
    }


    public static Date firstTimeOfWeek(int firstDayOfWeek, Date time) {
        Calendar cal = Calendar.getInstance();
        if (time != null) {
            cal.setTime(time);
        }

        cal.setFirstDayOfWeek(firstDayOfWeek);
        int day = cal.get(7);
        if (day == firstDayOfWeek) {
            day = 0;
        } else if (day < firstDayOfWeek) {
            day += 7 - firstDayOfWeek;
        } else if (day > firstDayOfWeek) {
            day -= firstDayOfWeek;
        }

        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);

        cal.add(5, -day);
        return cal.getTime();
    }


    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        return isSameDay(date, new Date());
    }


    public static boolean isBetweenHourOfDay1(int hourOfDay, Date date) {
        if (date == null) {
            return false;
        }
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        Calendar nowCal = Calendar.getInstance();
        dateCal.add(11, -hourOfDay);
        nowCal.add(11, -hourOfDay);

        return !isSameDay(dateCal, nowCal);
    }


    public static String date2String(Date date, String pattern) {
        return (new SimpleDateFormat(pattern)).format(date);
    }


    public static Date string2Date(String string, String pattern) {
        try {
            return (new SimpleDateFormat(pattern)).parse(string);
        } catch (ParseException e) {
            throw new IllegalArgumentException("无法将字符串[" + string + "]按格式[" + pattern + "]转换为日期", e);
        }
    }


    public static Date addTime(Date source, int hours, int minutes, int second) {
        if (source == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(source);
        cal.add(11, hours);
        cal.add(12, minutes);
        cal.add(13, second);
        return cal.getTime();
    }


    public static Date getFirstTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }


    public static Date getNextDayFirstTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime() + 86400000L);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        return calendar.getTime();
    }


    public static int calcIntervalDays(Date startDate, Date endDate) {
        int value = 0;
        if (startDate != null && endDate != null) {
            Date startDate0AM = getFirstTime(startDate);
            Date endDate0AM = getFirstTime(endDate);
            long subValue = startDate0AM.getTime() - endDate0AM.getTime();
            value = Math.abs((int) MathUtils.divideAndRoundUp(subValue, 8.64E7D, 0));
        }
        return value;
    }


    public static Date getNextTime(String cron, Date now) {
        CronSequenceGenerator gen = new CronSequenceGenerator(cron, TimeZone.getDefault());
        Date time = gen.next(now);
        return time;
    }

    public static int getSysTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }


    public static int getDateYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(1);
    }

    public static int getDateMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(2) + 1;
    }

    public static int getDateDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(5);
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\DateUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */