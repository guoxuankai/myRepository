package com.rondaful.cloud.user.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import com.rondaful.cloud.common.constant.UserConstants;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;

public class Base64Utils {
	
	public static void main(String[] args) {
//		String aaString = "dfdsfsd";
//		String encode = new Base64().encodeAsString(aaString.getBytes());
//		System.out.println(encode); // ZGZkc2ZzZA==
//		System.out.println(new String(new Base64().decode(encode)));
		
		String str = getBase64("123456789");
		System.out.println(str);
		
		String src = setBase64(str);
		System.out.println(src);
	}
	/**
	 * 进行base64编码
	 * @param str
	 * @return
	 */
	public static String getBase64(String str) {
		String aaString = str;
		String encode = null;
		try {
			encode = new Base64().encodeAsString(aaString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encode;
	}
	
	/**
	 * 进行base64解码
	 * @param encode
	 * @return
	 */
	public static String setBase64(String encode) {
		String setBase64 = new String(new Base64().decode(encode));
		if ( setBase64 != null ) {
			return setBase64;
		}
			return null;
	}
	

}
