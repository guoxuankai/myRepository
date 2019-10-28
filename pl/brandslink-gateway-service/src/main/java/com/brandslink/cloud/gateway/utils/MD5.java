package com.brandslink.cloud.gateway.utils;

import java.security.MessageDigest;

public class MD5 {

	public static String md5Password(String password) {  
		  
        try {  
            //得到一个信息摘要器  
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes("UTF-8"));  
            StringBuffer buffer = new StringBuffer();  
            // 把没一个byte 做一个与运算 0xff;  
            for (byte b : result) {  
                // 与运算  
                int number = b & 0xff;// 加盐  
                String str = Integer.toHexString(number);  
                if (str.length() == 1) {  
                    buffer.append("0");  
                }  
                buffer.append(str);  
            }  
  
            // 标准的md5加密后的结果  
            return buffer.toString();  
        } catch (Exception e) {  
            //e.printStackTrace();  
            return "";  
        }  
    }
	
	
	public static String getMD5(byte[] source) {
		String s = null;
		char [] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
		'9', 'A', 'B', 'C', 'D', 'E', 'F'};
		final int temp = 0xf;
		final int arraySize = 32;
		final int strLen = 16;
		final int offset = 4;
		try {
			MessageDigest md = MessageDigest
			.getInstance("MD5");
			md.update(source);
			byte [] tmp = md.digest();
			char [] str = new char[arraySize];
			int k = 0;
			for (int i = 0; i < strLen; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> offset & temp];
				str[k++] = hexDigits[byte0 & temp];
			}
			s = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static void main(String[] args) {
		System.out.println(MD5.md5Password("123456"));
	}
}
