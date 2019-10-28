package com.rondaful.cloud.order.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * FREEMARKER 模板工具类
 * 
 * @author king.wyx@qq.com
 *
 */
public class FreeMarkerUtil {

	private static Logger log = LoggerFactory.getLogger(FreeMarkerUtil.class);
	/** 模板存储位置 */
//	private static final String TEMPLATES = "templates";
	private static final String TEMPLATES = "";
	/** 字体库位置 */
	private static final String FONTS = "fonts";
	/** 默认pdf生成位置 */
	public static final String PDF = "pdf";
	/** 页眉 */
	public static String PAGE_HEADER = "";
	/** 左边页脚 */
	public static String PAGE_LEFT_FOOTER = "";
	/** 右边页脚 */
	public static String PAGE_RIGHT_FOOTER = "";

//	public static String FONT_LIBRARY = "ping_fang_light.ttf";

	/**
	 * 获取模板内容
	 * 
	 * @param fileName 模板名称(例如hello.ftl)
	 * @param data     需要渲染的数据
	 * @return
	 */
	public static String getContent(String fileName, Object data) {
//		String templateFilePath = getTemplatePath(fileName);

		Configuration config = new Configuration(Configuration.VERSION_2_3_25);
		config.setDefaultEncoding("UTF-8");
		try {
			config.setClassForTemplateLoading(FreeMarkerUtil.class, "/template");
//			config.setDirectoryForTemplateLoading(new File(templateFilePath));
			config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			config.setLogTemplateExceptions(false);
			Template template = config.getTemplate(fileName);
			StringWriter writer = new StringWriter();
			template.process(data, writer);
			writer.flush();
			String html = writer.toString();
			return html;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取模板路径
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getTemplatePath(String fileName) {
		String classpath = FreeMarkerUtil.class.getClassLoader().getResource("").getPath();
		String templatePath = classpath + TEMPLATES;

		String filePath = templatePath + File.separator + fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			log.error("模板:" + templatePath + ",不存在");
			throw new RuntimeException("模板:" + templatePath + ",不存在");
		}

		return templatePath;
	}

	public static String getFontPath(String fontName) {
		return getFontPath() + File.separator + fontName;
	}

	/**
	 * 获取字体设置路径
	 * 
	 * @return
	 */
	public static String getFontPath() {
		String classpath = FreeMarkerUtil.class.getClassLoader().getResource("").getPath();
		String fontpath = classpath + FONTS;
		return fontpath;
	}

}
