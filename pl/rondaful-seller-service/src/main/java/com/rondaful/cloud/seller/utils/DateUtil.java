package com.rondaful.cloud.seller.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {
	
	public static String dateString = "yyyy-MM-dd HH:mm:ss";
	
	public static String dateStringNo = "yyyyMMddHHmmss";

	public static String dateStringDir = "yyyyMMdd";

	/**
	 * 日期转字符串
	 * @param date
	 * @return
	 */
	public static String DateToString(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat(dateString);
		String dateString = formatter.format(date);
		return dateString;
	}


	/**
	 * 获取当前日期时间戳字符串
	 * @return
	 */
	public static String getCurrentDate(){
		return DateToString(new Date());
	}
	
	
	public static String DateToNo(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat(dateStringNo);
		String dateString = formatter.format(date);
		return dateString;
	}
	
	
	public static long DateStringToLong(String str) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat(dateString);
		Date date = formatter.parse(str);
		return date.getTime();
	}

	public static String DateToDir(Date date) throws ParseException{

		SimpleDateFormat formatter = new SimpleDateFormat(dateStringDir);
		String dateString = formatter.format(date);
		return dateString;
	}


	public static void main(String[] args) throws ParseException {
		System.out.println(DateToDir(new Date()));
	}
	
}
