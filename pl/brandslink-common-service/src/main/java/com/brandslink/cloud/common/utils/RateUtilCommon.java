package com.brandslink.cloud.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author 
   *    汇率接口
 */

@Component
public class RateUtilCommon {
	
	private static final Logger logger = LoggerFactory.getLogger(RateUtilCommon.class);

	private static final String appkey = "38639";

	private static final String sign = "32cad2d7cbd7b7bd2f2630f036d4a518";

	private static final String url = "http://api.k780.com/?app=finance.rate&";
	
	public static RestTemplate rest;
	
	public static synchronized RestTemplate getRest() {
		if(rest == null) {
			rest = new RestTemplate();
		}
		return rest;
	}

	// scur 原币种编号,支持一对多、多对一、多对多查询.
	// 例如: String[] scur = {"cny","aed","cad"};
	// tcur 目标币种编号 支持一对多、多对一、多对多查询.
	// 例如： String[] tcur = {"usd","cny","aed"};
	public String getExchangeRate(String[] scur, String[] tcur) throws IOException {
		StringBuilder sources = new StringBuilder();
		StringBuilder purpose = new StringBuilder();
		int i = 0;

		StringBuilder sourcesResult = handleString(scur, sources, i);

		StringBuilder purposeResult = handleString(tcur, purpose, i);

		URL u = new URL(url + "scur=" + sourcesResult.toString() + "&tcur=" + purposeResult.toString() + "&appkey="
				+ appkey + "&sign=" + sign + "&format=json");
		InputStream in = u.openStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte buf[] = new byte[1024];
			int read = 0;
			while ((read = in.read(buf)) > 0) {
				out.write(buf, 0, read);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		byte b[] = out.toByteArray();
		String result = new String(b, "UTF-8");
		logger.info("汇率返回结果: " + result);
		return result;
	}

	private StringBuilder handleString(String[] scur, StringBuilder sources, int i) {
		for (String s : scur) {
			sources.append(s);

			if (i != scur.length - 1) {
				sources.append(',');
			}

			i++;
		}
		return sources;
	}
	
	
	
	
	public String getExchangeRateX(String[] scur, String[] tcur) {
		String path = url + "scur=" + String.join(",", scur) + "&tcur=" + String.join(",", tcur) + "&appkey=" + appkey + "&sign=" + sign + "&format=json";
		return getRest().getForObject(path, String.class);
	}

}
