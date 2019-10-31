package com.brandslink.cloud.common.utils;

import com.brandslink.cloud.common.enums.OpenAPICodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

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


	/**
	 * md5加密
	 *
	 * @param plainText
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getMd5(String plainText) throws UnsupportedEncodingException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			//可以填UTF-8或GBK
			md.update(plainText.getBytes("GBK"));
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			//32位加密
			return buf.toString();
			// 16位的加密
			//return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取access_token
	 * @param sortMap treeMap 默认按照ascii升序排列
	 * @param appId 应用ID
	 * @param secret 应用密钥
	 * @return
	 */
	public static String getAccessToken(TreeMap<String,String> sortMap,String appId, String secret){
		return createSign(sortMap,secret,"")+appId;
	}

	/**
	 * 生成签名
	 * @param sortMap treeMap 默认按照ascii升序排列
	 * @param secret 密钥
	 * @param timestamp 时间戳
	 * @return
	 */
	public static String createSign(TreeMap<String,String> sortMap, String secret, String timestamp){
		StringBuilder sb=new StringBuilder();
		for(String key:sortMap.keySet()){
			sb.append(sortMap.get(key)); //.replaceAll("\\s","")
		}
		try{
			sb.append(secret);
			if(StringUtils.isNotEmpty(timestamp)){
				sb.append(timestamp);
			}
			System.out.println("加密前的字符串:"+sb.toString());
			return getMd5(sb.toString());
		}catch (UnsupportedEncodingException e){
			throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200401);
		}
	}

	public static void main(String[] args) {
		System.out.println(MD5.md5Password("123456"));
	}
}
