package com.brandslink.cloud.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ExcelUtil<T> {
	private String[] header;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	private OutputStream oStream;

	private JSONArray array;
	private static String suffix = ".xlsx";

	/**
	 * JsonArray生成Excel数据<br>
	 * Array中的每一个json内的key对应Excel每一列
	 * 
	 * @param jsonArray 数据
	 * @param map       width_每一列宽度、header_excel表头、key_json里面key
	 * @throws IOException
	 */

	public static InputStream fileStream(JSONArray jsonArray, Map<String, String[]> map) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		int[] w = StringToInt((String[]) map.get("width"));
		String[] h = (String[]) map.get("header");
		String[] k = (String[]) map.get("key");

		ExcelUtil<JSONObject> excelRead = new ExcelUtil<JSONObject>(h, jsonArray, outStream, w);
		excelRead.jsonWrite(new ExcelUtil.ExcelMapToRow<JSONObject>() {
			@Override
			public void toRow(JSONObject json, XSSFRow row) {
				for (int i = 0; i < h.length; i++) {
					String str = json.getString(k[i]);
					row.createCell(i).setCellValue(StringUtils.isEmpty(str) ? "" : str);
				}
			}
		});
		return new ByteArrayInputStream(outStream.toByteArray());
	}

	/**
	 * 本地项目导出调用
	 * 
	 * @param name
	 * @param suffixs     [xlsx、xls]
	 * @param inputStream
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void outHostExcel(String name, String suffixs, InputStream in, HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.reset();
		response.setContentType("application/octet-stream");
		String s = StringUtils.isBlank(suffixs) ? suffix : "." + suffixs;
		if ("firefox".equals(getExplorerType(request))) {
			String excelName = new String((name + s).getBytes("GB2312"), "ISO-8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=" + excelName);
		} else {
			String excelName = URLEncoder.encode(name + s, "UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + excelName);
		}
		ServletOutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(in);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
	}

	/**
	 * 远程+本地 导出都可调用
	 * 
	 * @param name
	 * @param suffixs [xlsx、xls]
	 * @param in
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static ResponseEntity<byte[]> outExcel(String name, String suffixs, InputStream in, HttpServletRequest request) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/octet-stream");
		String s = StringUtils.isBlank(suffixs) ? suffix : "." + suffixs;
		if ("firefox".equals(getExplorerType(request))) {
			String excelName = new String((name + s).getBytes("GB2312"), "ISO-8859-1");
			headers.add("Content-Disposition", "attachment; filename=" + excelName);
		} else {
			String excelName = URLEncoder.encode(name + s, "UTF-8");
			headers.add("Content-Disposition", "attachment;filename=" + excelName);
		}
		return new ResponseEntity<byte[]>(inputTobyte(in), headers, HttpStatus.OK);
	}

	private ExcelUtil(String[] header, JSONArray array, OutputStream oStream, int[] width) {
		this.header = header;
		this.array = array;
		this.oStream = oStream;
		wb = new XSSFWorkbook();
		sheet = wb.createSheet();
		if (width != null) {
			for (int i = 0; i < width.length; i++) {
				sheet.setColumnWidth(i, width[i]);
			}
		}
	}

	private void setHeader() {
		XSSFRow row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++)
			row.createCell(i).setCellValue(header[i]);
	}

	private void jsonWrite(ExcelMapToRow<T> excelMapToRow) throws IOException {
		setHeader();
		for (int i = 0; i < array.size(); i++) {
			XSSFRow row = sheet.createRow(i + 1);
			excelMapToRow.toRow(array.getJSONObject(i), row);
		}
		this.wb.write(this.oStream);
	}

	public XSSFWorkbook getWork() {
		return this.wb;
	}

	public static interface ExcelMapToRow<T> {
		void toRow(JSONObject json, XSSFRow row);
	}

	private static int[] StringToInt(String[] str) {
		if (str == null)
			return null;
		int[] w = new int[str.length];
		for (int i = 0; i < str.length; i++) {
			w[i] = Integer.parseInt(str[i]);
		}
		return w;
	}

	private static byte[] inputTobyte(InputStream inStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[2048];
		int bytesRead = 0;
		while ((bytesRead = inStream.read(buff, 0, buff.length)) > 0) {
			baos.write(buff, 0, bytesRead);
		}
		return baos.toByteArray();
	}

	/**
	 * 设置表基本信息
	 * 
	 * @param header EXCEL表头
	 * @param key    json中key
	 * @param width  列宽-根据自己需求来写，默认可以不用
	 * @return
	 */
	public static Map<String, String[]> createMap(String[] header, String[] key, String[] width) {
		Map<String, String[]> title = new HashMap<>();
		title.put("header", header);
		title.put("key", key);
		title.put("width", width);
		return title;
	}

	/**
	 * 获取浏览器类型
	 * 
	 * @param request
	 * @return
	 */
	public static String getExplorerType(HttpServletRequest request) {
		String agent = request.getHeader("USER-AGENT");
		if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
			return "firefox";
		} else if (agent != null && agent.toLowerCase().indexOf("msie") > 0) {
			return "ie";
		} else if (agent != null && agent.toLowerCase().indexOf("chrome") > 0) {
			return "chrome";
		} else if (agent != null && agent.toLowerCase().indexOf("opera") > 0) {
			return "opera";
		} else if (agent != null && agent.toLowerCase().indexOf("safari") > 0) {
			return "safari";
		}
		return "others";
	}

	/**
	 * 不带返回值的下载<br>
	 * resources目录<br>
	 * Excel 模板下载[用xlsx格式的文件]
	 * 
	 * @param suffix [xlsx、xls]
	 * @param path
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void downloadTemplate(String fileName, String suffix, String path, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
		outHostExcel(fileName, suffix, new ClassPathResource(path).getInputStream(), request, response);
	}

	/**
	 * 
	 * 带返回值的下载<br>
	 * resources目录<br>
	 * Excel 模板下载[用xlsx格式的文件]
	 * 
	 * @param suffix [xlsx、xls]
	 * @param path
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static ResponseEntity<byte[]> downloadTemplate(String fileName, String suffix, String path, HttpServletRequest request) throws FileNotFoundException, IOException {
		return outExcel(fileName, suffix, new ClassPathResource(path).getInputStream(), request);
	}

}
