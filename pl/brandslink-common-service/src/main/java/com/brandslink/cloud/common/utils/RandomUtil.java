package com.brandslink.cloud.common.utils;

public class RandomUtil {

	/**
	 * 随机数
	 * 
	 * @param size
	 * @return
	 */
	public static String randomNumberString(int size) {
		String data = "0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder();
		size = size > data.length() - 1 ? data.length() - 1 : size;
		for (int i = 0; i < size; i++) {
			String str = String.valueOf(data.charAt((int) Math.floor(Math.random() * data.length())));
			sb.append(str);
			data = data.replaceAll(str, "");
		}
		return sb.toString();
	}

}
