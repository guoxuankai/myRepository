package com.rondaful.cloud.commodity.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Excel工具类
 */
public class ExcelUtil {

	// 显示的导出表的标题
	private String title;
	// 导出表的列名
	private String[] colsName;

	// 导出文件名称
	private String fileName;

	private List<Object[]> dataList = new ArrayList<Object[]>();

	private HttpServletResponse response;
	// private HttpServletRequest request;
	private XSSFWorkbook workbook;

	// 构造方法，传入要导出的数据
	public ExcelUtil(String title, String[] colName, String fileName, List<Object[]> dataList,
			HttpServletResponse response) {
		this.dataList = dataList;
		this.colsName = colName;
		this.title = title;
		this.response = response;
		this.fileName = fileName;
	}

	/*
	 * 导出数据
	 */
	public void export() throws Exception {
		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(title); // 创建工作表

			// sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面 - 可扩展】
			XSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);// 获取列头样式对象
			// 定义所需列数
			int colNums = colsName.length;
			// 创建第一行
			XSSFRow rowRowName = sheet.createRow(0); // 在索引=的位置创建行(最顶端的行开始的第一行)
			// 将列头设置到sheet的单元格中
			for (int i = 0; i < colsName.length; i++) {
				XSSFCell cellRowName = rowRowName.createCell(i); // 创建列头对应个数的单元格
				XSSFRichTextString text = new XSSFRichTextString(colsName[i]);
				cellRowName.setCellValue(text);
				cellRowName.setCellStyle(columnTopStyle);
			}
			// 将查询出的数据设置到sheet对应的单元格中
			for (int i = 0; i < dataList.size(); i++) {

				Object[] obj = dataList.get(i);// 遍历每个对象
				XSSFRow row = sheet.createRow(i + 1);// 创建所需的行数

				for (int j = 0; j < obj.length; j++) {
					XSSFCell cell = row.createCell(j);
					if (!"".equals(obj[j]) && obj[j] != null) {
						cell.setCellValue(String.valueOf(obj[j])); // 设置单元格的值
					}
				}
			}
			// 让列宽随着导出的列长自动适应
			for (int colNum = 0; colNum < colNums; colNum++) {
				// int columnWidth = sheet.getColumnWidth(colNum) / 256;
				int colWidth = sheet.getColumnWidth(colNum) * 5;
				if (colWidth < 255 * 256) {
					sheet.setColumnWidth(colNum, colWidth < 5000 ? 5000 : colWidth);
				} else {
					sheet.setColumnWidth(colNum, 60000);
				}
			}
			if (workbook != null) {
				// response.reset();
				ServletOutputStream out = response.getOutputStream();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				workbook.write(os);
				byte[] bytes = os.toByteArray();
				response.setHeader("content-Type", "application/vnd.ms-excel");
				response.setHeader("Content-Disposition",
						"attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
				response.setHeader("Content-Length", String.valueOf(bytes.length));
				out.write(bytes);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 列头单元格样式
	 */
	public XSSFCellStyle getColumnTopStyle(XSSFWorkbook workbook) {

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(HSSFColorPredefined.PALE_BLUE.getIndex());
		style.getFillPatternEnum();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.getAlignmentEnum();
		style.setAlignment(HorizontalAlignment.CENTER);
		// 生成一个字体
		XSSFFont font = workbook.createFont();
		font.setColor(HSSFColorPredefined.BLACK.getIndex2());
		font.setFontHeightInPoints((short) 12);
		// 把字体应用到当前的样式
		style.setFont(font);
		return style;
	}

	/**
	 * 导出excel
	 * 
	 * @param list
	 * @param title
	 * @param sheetName
	 * @param pojoClass
	 * @param fileName
	 * @param isCreateHeader
	 * @param response
	 */
	public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
			boolean isCreateHeader, HttpServletResponse response) {
		ExportParams exportParams = new ExportParams(title, sheetName);
		exportParams.setCreateHeadRows(isCreateHeader);
		defaultExport(list, pojoClass, fileName, response, exportParams);
	}

	/**
	 * 导出excel
	 * 
	 * @param list
	 * @param title
	 * @param sheetName
	 * @param pojoClass
	 * @param fileName
	 * @param response
	 */
	public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
			HttpServletResponse response) {
		defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
	}

	/**
	 * 导出excel
	 * 
	 * @param list
	 * @param fileName
	 * @param response
	 */
	public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
		defaultExport(list, fileName, response);
	}

	private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response,
			ExportParams exportParams) {
		Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
		if (workbook != null)
			;
		downLoadExcel(fileName, response, workbook);
	}

	private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
		try {
			ServletOutputStream out = response.getOutputStream();
			response.setCharacterEncoding("UTF-8");
			response.setHeader("content-Type", "application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
		Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
		if (workbook != null)
			;
		downLoadExcel(fileName, response, workbook);
	}

	public static void getExport(List list, String title, String sheetName, Class<?> pojoClass, FileOutputStream out) {
		Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(title, sheetName), pojoClass, list);
		if (workbook != null)
			;
		try {
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导入excel
	 * 
	 * @param filePath
	 * @param titleRows
	 * @param headerRows
	 * @param pojoClass
	 * @param            <T>
	 * @return
	 */
	public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
		if (StringUtils.isBlank(filePath)) {
			return null;
		}
		ImportParams params = new ImportParams();
		params.setTitleRows(titleRows);
		params.setHeadRows(headerRows);
		List<T> list = null;
		try {
			list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
		} catch (NoSuchElementException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 导入excel
	 * 
	 * @param file
	 * @param titleRows
	 * @param headerRows
	 * @param pojoClass
	 * @param            <T>
	 * @return
	 */
	public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows,
			Class<T> pojoClass) {
		if (file == null) {
			return null;
		}
		ImportParams params = new ImportParams();
		params.setTitleRows(titleRows);
		params.setHeadRows(headerRows);
		List<T> list = null;
		try {
			list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
