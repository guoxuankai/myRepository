package com.rondaful.cloud.finance.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单相关工具
 *
 */
public class OrderUtils {

	/**
	 * 获取订单编号
	 * 
	 * @return
	 *
	 */
	public static String getOrderSn() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String ret = formatter.format(LocalDateTime.now()) + getRomdon(4, 10);
		return ret;
	}

	/**
	 * 获取随机数
	 * 
	 * @param size
	 * @param rang
	 * @return
	 *
	 */
	public static String getRomdon(int size, int rang) {
		String ret = "";
		for (int i = 0; i < size; i++) {
			ret += (int) (Math.random() * rang);
		}
		return ret;
	}
	
}
