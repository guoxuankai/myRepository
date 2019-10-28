package com.rondaful.cloud.finance.utils;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {

	/**
	 * 获取下月1号的时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextMonth1st() {
		int maxCurrentMonthDay = 0;
		Calendar cal = Calendar.getInstance();
		maxCurrentMonthDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.add(Calendar.DAY_OF_MONTH, maxCurrentMonthDay);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取本月15号的时间
	 * 
	 * @param date
	 * @return
	 */
	private static Date getThisMonth15th() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取下月15号的时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNext15th() {
		Date thisMonth15th = getThisMonth15th();
		if (new Date().before(thisMonth15th)) {
			return thisMonth15th;
		}
		int maxCurrentMonthDay = 0;
		Calendar cal = Calendar.getInstance();
		maxCurrentMonthDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.add(Calendar.DAY_OF_MONTH, maxCurrentMonthDay);
		cal.set(Calendar.DAY_OF_MONTH, 15);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取下周一的时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextWeekMonday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

}
