package com.rondaful.cloud.order.common;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ExportUtil {

	// 显示的导出表的标题
	private  String title;
	// 导出表的列名
	private  String[] colsName;
	
	//导出文件名称
	private String fileName;

	private  List<Object[]> dataList = new ArrayList<Object[]>();

	private  HttpServletResponse response;
	//private  HttpServletRequest request;
	private  XSSFWorkbook workbook;

	// 构造方法，传入要导出的数据
	public ExportUtil(String title, String[] colName, String fileName, List<Object[]> dataList, HttpServletResponse response) {
		this.dataList = dataList;
		this.colsName = colName;
		this.title = title;
		this.response=response;
		this.fileName=fileName;
	}

	/*
	 * 导出数据
	 */
	public  void export() throws Exception {
		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(title); // 创建工作表

			// sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面 - 可扩展】
		        XSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);//获取列头样式对象
		       //HSSFCellStyle style = this.getStyle(workbook);					//单元格样式对象

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
			//让列宽随着导出的列长自动适应
			for (int colNum = 0; colNum < colNums; colNum++) {
	           // int columnWidth = sheet.getColumnWidth(colNum) / 256;
	            int colWidth = sheet.getColumnWidth(colNum)*2;
	            if(colWidth<255*256){
	                sheet.setColumnWidth(colNum, colWidth < 3000 ? 3000 : colWidth);    
	            }else{
	                sheet.setColumnWidth(colNum,6000 );
	            }
			}
			if (workbook != null) {
				//response.reset();
				ServletOutputStream out = response.getOutputStream();
	            ByteArrayOutputStream os = new ByteArrayOutputStream();
	            workbook.write(os);
	            byte[] bytes = os.toByteArray();
	            response.setHeader("content-Type", "application/vnd.ms-excel");
	            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
	            response.setHeader("Content-Length", String.valueOf(bytes.length));
	            out.write(bytes);
	            out.flush();
	            out.close();
			}
			/*if (workbook != null) {
				try {
					String excelName = URLEncoder.encode(fileName + ".xlsx", "UTF-8");
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-disposition", "attachment;filename=" +excelName); 
					OutputStream out = response.getOutputStream();
					workbook.write(out);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 获取浏览器类型
	 * @param request
	 * @return
	 */
	public static String getExplorerType(HttpServletRequest request){		
		String agent = request.getHeader("USER-AGENT");
        if(agent != null && agent.toLowerCase().indexOf("firefox") > 0){
        	return "firefox"; 
        }else if(agent != null && agent.toLowerCase().indexOf("msie") > 0){
        	return "ie";
        }else if(agent != null && agent.toLowerCase().indexOf("chrome") > 0){
        	return "chrome";  
        }else if(agent != null && agent.toLowerCase().indexOf("opera") > 0){
        	return "opera";
        }else if(agent != null && agent.toLowerCase().indexOf("safari") > 0){
        	return "safari";
        }
        return "others";
	}
	
	/* 
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

}
