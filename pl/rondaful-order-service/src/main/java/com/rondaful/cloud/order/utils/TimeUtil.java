package com.rondaful.cloud.order.utils;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Administrator
 * java工具类_日期工具类集合
 */
public class TimeUtil {
    /**
     * 基本的常量配置
     */
    //无格式时间戳 分为 14位 12位 10位 8位...等
    public static final String TIMESTAMP_TIMEREGULAR = "yyyyMMddHHmmss";

    //标准日期格式 2017-01-01 19:30:29
    public static final String GENERAL_TIMEREGULAR = "yyyy-MM-dd HH:mm:ss";

    // 短日期格式
    public static String DATE_FORMAT = "yyyy-MM-dd";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 各种日期类型转换
     */
    public static void timeType() {
        System.out.println("Java Date日期格式____:" + new Date());
        System.out.println("java 标准日期格式______:" + new SimpleDateFormat(GENERAL_TIMEREGULAR).format(new Date()));
        //java的 new Date().getTime() 获取的是毫秒 而unix获取的是秒
        System.out.println("java unix日期格式____:" + new Date().getTime() / 1000L);
        System.out.println("java timestamp日期格式:" + new Timestamp(System.currentTimeMillis()));
        System.out.println("timestamp转date" + timestampToDate());
    }

    /**
     * 字符串格式的日期加减操作
     * @param dateString
     * @param n
     * @return
     * @throws ParseException
     */
    public static String stringAddSubtract(String dateString, int n) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //定义日期格式化的格式
        Date date = format.parse(dateString);//把字符串转化成指定格式的日期
        String dateStr = format.format(date.getTime() + n * 24 * 60 * 60 * 1000L);
        return dateStr;
    }

    /**
     * 将日期格式的字符串转换为长整型
     *
     * @param date
     * @return
     */
    public static long convert2long(String date) {
        try {
            if (StringUtils.isNotBlank(date)) {
                String format = GENERAL_TIMEREGULAR;
                SimpleDateFormat sf = new SimpleDateFormat(format);
                return sf.parse(date).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    /**
     * java.util.Date类型日期加减
     *
     * @param date java.util.Date
     * @param i    分钟
     * @return
     * @throws Exception
     */
    public static Date dateAddSubtract(Date date, int i) {
        return new Date(date.getTime() + (long) i * 60 * 1000);
    }

    /**
     * Date类型转string类型的格式
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String dateToString(Date date) throws Exception {
        if (date == null) {
            throw new Exception("Input parameter date is null !!!");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(GENERAL_TIMEREGULAR);
        return sdf.format(date);
    }

    /**
     * String 类型转 Date类型 java.util.Date
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static Date strToDate(String str) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(GENERAL_TIMEREGULAR);
        return sdf.parse(str);
    }

    /**
     * long类型转Date类型
     *
     * @param longtime
     * @return
     */
    public static Date longToDate(long longtime) {
        return new Date(longtime);
    }

    /**
     * Calendar类型转String类型
     * 格式：yyyy-MM-dd HH:mm:ss
     * @param calendar
     * @return
     */
    public static String calendarToStr(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(GENERAL_TIMEREGULAR);
        return sdf.format(calendar.getTime());
    }

    /**
     * String类型转Calendar类型
     * format: yyyy-MM-dd HH:mm:ss
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Calendar strToCalendar(String str) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);
        Calendar calendar = dateToCalendar(strtodate);
        return calendar;
    }

    /**
     * Calendar类型转Date类型
     *
     * @return
     */
    public static Date calendarToDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * 将Calendar对象转成北京时间
     *
     * @param calendar
     * @return
     */
    public static Date calendarToDate(Calendar calendar) {
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return calendar.getTime();
    }

    /**
     * date转calendar
     *
     * @param date
     * @return
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * TimeStamp类型转Date类型
     *
     * @return
     */
    public static Date timestampToDate() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date date = new Date();
        date = ts;
        return date;
    }

    /**
     * Date类型转String类型
     *
     * @param data       Date类型的时间
     * @param formatType 格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * @return
     */
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * long类型转String类型
     *
     * @param currentTime 要转换的long类型的时间
     * @param formatType  要转换的string类型的时间格式
     * @return
     * @throws ParseException
     */
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    /**
     * String类型转Date类型
     * strTime的时间格式必须要与formatType的时间格式相同
     *
     * @param strTime    要转换的string类型的时间，
     * @param formatType 要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    /**
     * long类型转Date类型
     *
     * @param currentTime 要转换的long类型的时间
     * @param formatType  要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * @return
     * @throws ParseException
     */
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    /**
     * string类型转换为long类型
     * strTime的时间格式和formatType的时间格式必须相同
     *
     * @param strTime    要转换的String类型的时间
     * @param formatType 时间格式
     * @return
     * @throws ParseException
     */
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    /**
     * @param date 类型转换为long类型
     * @return
     */
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * wujiachuang
     * @param timeList
     * @return
     * @throws ParseException
     */
    public  static String getMinTime(List<String> timeList) throws ParseException {
        String minTime = "";
        if (timeList.size() == 0) {
            return "";
        } else if (timeList.size() == 1) {
            return timeList.get(0);
        }else{
            for (String time : timeList) {
                if (timeList.indexOf(time) == 0) {
                    minTime=time;
                }else{
                    if (compare(time, minTime)) {
                        minTime=time;
                    }
                }
            }
            return minTime;
        }
    }

    /**
     * wujiachuang
     * @param time1
     * @param time2
     * @return
     * @throws ParseException
     * @throws ParseException
     */
    public static boolean compare(String time1,String time2) throws ParseException, ParseException {
        //如果想比较日期则写成"yyyy-MM-dd"就可以了
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //将字符串形式的时间转化为Date类型的时间
        Date a=sdf.parse(time1);
        Date b=sdf.parse(time2);
        //Date类的一个方法，如果a早于b返回true，否则返回false
        if(a.before(b))
            return true;
        else
            return false;
		/*
		 * 如果你不喜欢用上面这个太流氓的方法，也可以根据将Date转换成毫秒
		if(a.getTime()-b.getTime()<0)
			return true;
		else
			return false;
		*/
    }

    /**
     * wujiachuang
     * @param time
     * @param addDay
     * @return
     */
    public static String addTime(String time, int addDay) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(TimeUtil.stringToDate(time));
        instance.add(Calendar.DAY_OF_MONTH, addDay);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(instance.getTime());
    }

    /**
     * String(yyyy-MM-dd HH:mm:ss) 转 Date
     * exp: String date = "2010/05/04 12:34:23";
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String time) {
        Date date = new Date();
        // 注意format的格式要与日期String的格式相匹配
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = dateFormat.parse(time);
//			System.out.println(date.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * pattern: yyyy-MM-dd HH/mm/ss
     *
     * @param time
     * @return
     */
    public static String DateToString(Date time) {
        String dateStr = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH/mm/ss");
        try {
            dateStr = dateFormat.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * pattern: yyyy-MM-dd HH:mm:ss
     * @param time
     * @return
     */
    public static String DateToString2(Date time) {
        String dateStr = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateStr = dateFormat.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * String(yyyy-MM-dd HH:mm:ss)转10位时间戳
     *
     * @param time
     * @return
     */
    public static Integer StringToTimestamp(String time) {

        int times = 0;
        try {
            times = (int) ((Timestamp.valueOf(time).getTime()) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (times == 0) {
            System.out.println("String转10位时间戳失败");
        }
        return times;

    }

    /**
     * 10位int型的时间戳转换为String(yyyy-MM-dd HH:mm:ss)
     *
     * @param time
     * @return
     */
    public static String timestampToString(Integer time) {
        //int转long时，先进行转型再进行计算，否则会是计算结束后在转型
        long temp = (long) time * 1000;
        Timestamp ts = new Timestamp(temp);
        String tsStr = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //方法一
            tsStr = dateFormat.format(ts);
            System.out.println(tsStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tsStr;
    }

    /**
     * 10位时间戳转Date
     *
     * @param time
     * @return
     */
    public static Date TimestampToDate(Integer time) {
        long temp = (long) time * 1000;
        Timestamp ts = new Timestamp(temp);
        Date date = new Date();
        try {
            date = ts;
            //System.out.println(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Date类型转换为10位时间戳
     *
     * @param time
     * @return
     */
    public static Integer DateToTimestamp(Date time) {
        Timestamp ts = new Timestamp(time.getTime());

        return (int) ((ts.getTime()) / 1000);
    }


    /**
     * 获取过去或者未来 任意天内的日期数组
     *
     * @param intervals intervals天内
     * @return 日期数组
     */
    public static ArrayList<String> test(int intervals) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        ArrayList<String> fetureDaysList = new ArrayList<>();
        for (int i = 0; i < intervals; i++) {
            pastDaysList.add(getPastDate(i));
            fetureDaysList.add(getFetureDate(i));
        }
        return pastDaysList;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取未来 第 past 天的日期
     *
     * @param past
     * @return
     */
    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(today);
        return result;
    }
}