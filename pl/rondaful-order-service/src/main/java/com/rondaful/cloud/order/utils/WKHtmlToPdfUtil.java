package com.rondaful.cloud.order.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class WKHtmlToPdfUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(WKHtmlToPdfUtil.class);

	/**
	 * 将HTML文件内容输出为PDF文件
	 *
	 * @param htmlFilePath HTML文件路径
	 * @param pdfFilePath  PDF文件路径
	 */
	public static void htmlToPdf(String htmlFilePath, String pdfFilePath) {
		Process process = null;
		try {
			// 注意命令调用路径与安装路径保持一致
			String command = getCommand(htmlFilePath, pdfFilePath);
			LOGGER.info("运行的命令为: {}", command);

			process = Runtime.getRuntime().exec(command);
			// 为了防止waitFor因为流缓存而阻塞，启用两个线程进行流的读取
			new Thread(new ClearBufferThread("Input", process.getInputStream())).start();
			new Thread(new ClearBufferThread("Error", process.getErrorStream())).start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			process.destroy();
		}
	}

	/**
	 * 将HTML字符串转换为HTML文件
	 *
	 * @param htmlStr HTML字符串
	 * @return HTML文件的绝对路径
	 */
	public static String strToHtmlFile(String htmlStr, String path) {
		OutputStream outputStream = null;
		try {
			LOGGER.debug("html path = {}, html content = {}", path, htmlStr);
			outputStream = new FileOutputStream(path);
			outputStream.write(htmlStr.getBytes("UTF-8"));
			return path;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 获得HTML转PDF的命令语句,注意命令调用路径与安装路径保持一致 （一些命令参数可以自行去做修改，或者使用）
	 * 
	 * @param htmlFilePath HTML文件路径
	 * @param pdfFilePath  PDF文件路径
	 * @return HTML转PDF的命令语句
	 */
	private static String getCommand(String htmlFilePath, String pdfFilePath) {
		String osName = System.getProperty("os.name");
		LOGGER.info("运行的系统为: {}", osName);
		StringBuilder cmd = new StringBuilder();
		cmd.append("D:/tools/wkhtmltox/bin/wkhtmltopdf.exe");
		cmd.append(" ");
//        cmd.append(" --header-line");//页眉下面的线
		// cmd.append(" --header-center 这里是页眉这里是页眉这里是页眉这里是页眉 ");//页眉中间内容
		cmd.append(" --margin-top 1.8cm ");// 设置页面上边距 (default 10mm)
		cmd.append(" --margin-right 1.5cm ");// 设置页面下边距 (default 10mm)
		cmd.append(" --margin-bottom 1.8cm ");// 设置页面下边距 (default 10mm)
		cmd.append(" --margin-left 1.5cm ");// 设置页面下边距 (default 10mm)
		cmd.append(" --page-size Letter ");// 纸张大小A4, Letter, etc.
//        cmd.append(" --header-html file:///"+WebUtil.getServletContext().getRealPath("")+FileUtil.convertSystemFilePath("\\style\\pdf\\head.html"));// (添加一个HTML页眉,后面是网址)
//        cmd.append(" --header-spacing 6 ");// (设置页眉和内容的距离,默认0)
		// cmd.append(" --footer-center (设置在中心位置的页脚内容)");//设置在中心位置的页脚内容
//        cmd.append(" --footer-html file:///"+WebUtil.getServletContext().getRealPath("")+FileUtil.convertSystemFilePath("\\style\\pdf\\foter.html"));// (添加一个HTML页脚,后面是网址)
//        cmd.append(" --footer-line");//* 显示一条线在页脚内容上)
//        cmd.append(" --footer-spacing 6 ");// (设置页脚和内容的距离)
		cmd.append(" %s %s");
		// Windows
		if (osName.startsWith("Windows")) {// C:/Program Files/
//            return String.format("C:/Program Files/wkhtmltopdf/bin/wkhtmltopdf.exe %s %s", htmlFilePath, pdfFilePath);
//            return String.format("wkhtmltopdf/bin/wkhtmltopdf.exe %s %s", htmlFilePath, pdfFilePath);
			return String.format(cmd.toString(), htmlFilePath, pdfFilePath);
		}
		// Linux
		else {
//			return String.format("/opt/wkhtmltopdf/bin/wkhtmltopdf %s %s", htmlFilePath, pdfFilePath);
			return String.format("/usr/bin/wkhtmltopdf --encoding utf-8 %s %s", htmlFilePath, pdfFilePath);
		}
	}
}
