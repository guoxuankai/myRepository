package com.rondaful.cloud.seller.common.aliexpress;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImgSet {
	public static void main(String[] ars){


	}

	/**
	 * 获取文本中的img的src地址
	 * @param imgStr
	 * @return
	 */
	public static Set<String> getImgSet(String imgStr){
		if(imgStr==null){
			return null;
		}
		Set<String> pics = new HashSet<>();
		String img = "";
		Pattern p_image;
		Matcher m_image;
		String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
		p_image = Pattern.compile
				(regEx_img, Pattern.CASE_INSENSITIVE);
		m_image = p_image.matcher(imgStr);
		while (m_image.find()) {
			// 得到<img />数据
			img = m_image.group();
			// 匹配<img>中的src数据
			Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
			while (m.find()) {
				pics.add(m.group(1));
			}
		}
		return pics;
	}

	/**
	 * 将网络图片编码为base64
	 *
	 * @param imgUrl
	 * @return
	 * @throws Exception
	 */
	public static String encodeImageToBase64(String imgUrl) {

		//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		//打开链接
		HttpURLConnection conn = null;
		try {
			URL url = new URL(imgUrl);
			conn = (HttpURLConnection) url.openConnection();
			//设置请求方式为"GET"
			conn.setRequestMethod("GET");
			//超时响应时间为5秒
			conn.setConnectTimeout(5 * 1000);
			//通过输入流获取图片数据
			InputStream inStream = conn.getInputStream();
			//得到图片的二进制数据，以二进制封装得到数据，具有通用性
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			//创建一个Buffer字符串
			byte[] buffer = new byte[1024];
			//每次读取的字符串长度，如果为-1，代表全部读取完毕
			int len = 0;
			//使用一个输入流从buffer里把数据读取出来
			while ((len = inStream.read(buffer)) != -1) {
				//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
				outStream.write(buffer, 0, len);
			}
			//关闭输入流
			inStream.close();
			//byte[] data = outStream.toByteArray();
			//对字节数组Base64编码
			//BASE64Encoder encoder = new BASE64Encoder();
			//返回Base64编码过的字节数组字符串
			//String base64 = encoder.encode(data);
			return Base64Utils.encodeToString(outStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
