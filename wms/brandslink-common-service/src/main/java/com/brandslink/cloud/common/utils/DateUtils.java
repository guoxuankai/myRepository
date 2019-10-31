package com.brandslink.cloud.common.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期常用格式
 *
 */
public class DateUtils {

	/** yyyyMMddHHmmss **/
	public static final String FORMAT_1 = "yyyyMMddHHmmss";

	/** yyyy-MM-dd HH:mm:ss **/
	public static final String FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

	/** yyyy-MM-dd **/
	public static final String FORMAT_3 = "yyyy-MM-dd";

	/** HH:mm:ss **/
	public static final String FORMAT_4 = "HH:mm:ss";

	public static final SimpleDateFormat SDF = new SimpleDateFormat(DateUtils.FORMAT_3);

	public static String dateToString(Date date, String format) {
		return DateFormatUtils.format(date, format);
	}

	public static String calendarToString(Calendar calendar, String format) {
		return DateFormatUtils.format(calendar, format);
	}

	public static String longToString(Long millis, String format) {
		return DateFormatUtils.format(millis, format);
	}

	public static Date strToDate(String strDate, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 获取前几天
	 * 
	 * @param date 当前时间
	 * @param day  天数
	 * @return
	 */
	public static Date getBeforeDateByDay(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - day);
		return calendar.getTime();
	}

	/**
	 * 获取后几天
	 * 
	 * @param date 当前时间
	 * @param day  天数
	 * @return
	 */
	public static Date getAfterDateByDay(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + day);
		return calendar.getTime();
	}

	/**
	 * 判断时间是否超出对应天数
	 * 
	 * @param date 时间
	 * @param day  天数
	 * @return true没有超出 false超出
	 */
	public static boolean checkDay(Date date, int day) {
		return LocalDateTime.now().isBefore(getAfterDateByDay(date, day).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

	/**
	 * 判断时间是否超出对应天数
	 * 
	 * @param date 时间
	 * @param day  天数
	 * @return true没有超出 false超出
	 */
	public static boolean checkDay(String date, int day) {
		return LocalDateTime.now().isBefore(getAfterDateByDay(strToDate(date, FORMAT_2), day).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

	/**
	 * 添加 时分秒
	 * 
	 * @param time 日期
	 * @param type [start、end]=[00:00:00、23:59:59]
	 */
	public static String time(String time, String type) {
		if (StringUtils.isBlank(time))
			return time;
		if ("start".equalsIgnoreCase(type)) {
			return SDF.format(SDF.parse(time, new ParsePosition(0))) + " 00:00:00";
		} else {
			return SDF.format(SDF.parse(time, new ParsePosition(0))) + " 23:59:59";
		}
	}

	/**
	 * 开始日期添加时分秒
	 * 
	 * @param startTime
	 * @param endTime
	 */
	public static void formatTime(String startTime, String endTime) {
		if (StringUtils.isBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			startTime = SDF.format(SDF.parse(endTime, new ParsePosition(0))) + " 00:00:00";
			endTime = SDF.format(SDF.parse(endTime, new ParsePosition(0))) + " 23:59:59";
		} else if (StringUtils.isBlank(endTime) && StringUtils.isNotBlank(startTime)) {
			startTime = SDF.format(SDF.parse(startTime, new ParsePosition(0))) + " 00:00:00";
			endTime = SDF.format(SDF.parse(startTime, new ParsePosition(0))) + " 23:59:59";
		} else {
			startTime = StringUtils.isBlank(startTime) ? startTime : SDF.format(SDF.parse(startTime, new ParsePosition(0))) + " 00:00:00";
			endTime = StringUtils.isBlank(endTime) ? endTime : SDF.format(SDF.parse(endTime, new ParsePosition(0))) + " 23:59:59";
		}
	}

}
