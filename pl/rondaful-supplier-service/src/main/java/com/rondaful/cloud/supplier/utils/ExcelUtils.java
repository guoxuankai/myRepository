package com.rondaful.cloud.supplier.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

public class ExcelUtils {

	
	public static Workbook importExcel(String fileName) throws Exception{
		ClassPathResource classPathResource = new ClassPathResource("template/"+fileName);
		InputStream in = classPathResource.getInputStream();
		Workbook wb = null;
        if (fileName.endsWith("xlsx")){
        	wb = new XSSFWorkbook(in);//Excel 2007
        }else if (fileName.endsWith("xls")){
        	wb = new HSSFWorkbook(in);//Excel 2003
        }
        return wb;
	}
}
