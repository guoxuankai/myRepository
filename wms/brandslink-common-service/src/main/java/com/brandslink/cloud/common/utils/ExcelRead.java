package com.brandslink.cloud.common.utils;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.brandslink.cloud.common.annotation.OutExcel;
import com.brandslink.cloud.common.entity.ResultExcelModel;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.DateUtils;

/**
 * Excel 导入 <br>
 * String、Integer、Long、Double、Boolean、Date
 * 
 * @param <T>
 */
public class ExcelRead<T> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String XLS = "xls";
	public static final String XLSX = "xlsx";

	public static final String SET = "set";
	public static final String FAILURE = "failure";
	public static final String INCORRECT_FORMAT = "格式有误!";
	public static final String ERROR_DATA = "数据有误!";
	public static final String MSG = "%s:%s;\n";

	protected HSSFWorkbook wb;
	protected XSSFWorkbook xwb;

	protected boolean hasHeader = false;
	protected InputStream inputStream;
	protected Class<?> clazz;
	protected int indexRows;
	protected int indexColumn;
	protected String suffix;
	protected Map<String, ClassModel> key;
	protected Map<String, Map<String, String>> result;
	protected Sheet sheet;
	protected LinkedList<Map<String, Row>> listMap;
	protected LinkedList<Row> header;
	protected boolean isErrorExcel;
	protected List<T> readData;

	protected XSSFWorkbook wbErroe = null;
	protected XSSFSheet sheetError = null;
	protected CellStyle styleError = null;
	protected Row rowErrorStyle = null;
	protected int columnSum = 0;
	protected List<? extends DataValidation> dataValidation;
	protected List<CellRangeAddress> cellRange;

	/**
	 * 读取Excel<br>
	 * 获取正确的数据、错误的数据、输出错误的Excel
	 * 
	 * @param inputStream
	 * @param suffix       [xls、xlsx]
	 * @param clazz        [对应的实例]
	 * @param indexRows    [从第几行开始]
	 * @param indexColumn  [从第几列开始]
	 * @param isErrorExcel [true、false] 是否需要回写错误到Excel
	 */
	public void readExcel(InputStream inputStream, String suffix, Class<T> clazz, int indexRows, int indexColumn, boolean isErrorExcel) {
		try {
			this.inputStream = inputStream;
			this.result = new HashMap<>();
			this.indexColumn = indexColumn < 0 ? 0 : indexColumn;
			this.clazz = clazz;
			this.indexRows = indexRows < 0 ? 0 : indexRows;
			this.suffix = suffix;
			this.isErrorExcel = isErrorExcel;
			checkType(inputStream);
			if (isErrorExcel && indexRows > 0) {
				this.isErrorExcel = isErrorExcel;
				this.header = new LinkedList<>();
				this.listMap = new LinkedList<>();
			}
			setMap(true);
			read(new ExcelMapRowImpl());
		} catch (IOException e) {
			logger.error("读取Excele: ", e);
		}

	}

	/**
	 * Excel模板下载,模板格式设置，不带返回值的下载
	 * 
	 * @param fileName  文件名
	 * @param path      文件路径
	 * @param suffix    文件后缀
	 * @param clazz     [对应的实例]----[不需要动态添加下拉选择此值可以为null]
	 * @param selected  下拉选择数据----[不需要动态添加下拉选择此值可以为null]
	 * @param setFormat [true/false]是否需要设置Excel的格式----[不需要动态添加下拉选择此值可以为null]
	 * @param indexRows 下拉填充从第几行开始---[不需要动态添加下拉选择此值可以为null]
	 */
	public void exportExcel(String fileName, String path, String suffix, Class<T> clazz, Map<String, String[]> selected, boolean setFormat, int indexRows) {
		try {
			ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (!setFormat)
				ExcelUtil.downloadTemplate(fileName, suffix, path, ra.getRequest(), ra.getResponse());
			this.clazz = clazz;
			this.suffix = suffix;
			checkType(new ClassPathResource(path).getInputStream());
			downloadTemplate(fileName, selected, ra.getRequest(), ra.getResponse(), indexRows, false);
		} catch (Exception e) {
			logger.error("模板读取: ", e);
		}
	}

	/**
	 * Excel模板下载,模板格式设置，带返回值的下载
	 * 
	 * @param fileName  文件名
	 * @param path      文件路径
	 * @param suffix    文件后缀
	 * @param clazz     [对应的实例]----[不需要动态添加下拉选择此值可以为null]
	 * @param selected  下拉选择数据----[不需要动态添加下拉选择此值可以为null]
	 * @param setFormat [true/false]是否需要设置Excel的格式----[不需要动态添加下拉选择此值可以为null]
	 * @param indexRows 下拉填充从第几行开始---[不需要动态添加下拉选择此值可以为null]
	 */
	public ResponseEntity<byte[]> exportExcelNeedReturn(String fileName, String path, String suffix, Class<T> clazz, Map<String, String[]> selected, boolean setFormat, int indexRows) {
		try {
			ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (!setFormat)
				return ExcelUtil.downloadTemplate(fileName, suffix, path, ra.getRequest());
			this.clazz = clazz;
			this.suffix = suffix;
			checkType(new ClassPathResource(path).getInputStream());
			return downloadTemplate(fileName, selected, ra.getRequest(), ra.getResponse(), indexRows, true);
		} catch (Exception e) {
			logger.error("模板读取: ", e);
		}
		return null;
	}

	/**
	 * 获取正确的数据
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<T> getSuccessfulDate() throws IOException {
		return readData;
	}

	/**
	 * 返回失败的 Key行 、 列
	 * 
	 * @return
	 */
	public Map<String, Map<String, String>> getFailure() {
		return result;
	}

	/**
	 * 获取错误的Excel<br>
	 * ExcelUtil.outHostExcel("test", er.getResultStream(), request, response);
	 * 
	 * @param data 自定义判断的错误对象
	 * @return
	 * @throws IOException
	 */
	public InputStream getResultStream(List<? extends ResultExcelModel> data) throws IOException {
		if (isErrorExcel && indexRows > 0) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			wbErroe = new XSSFWorkbook();
			sheetError = wbErroe.createSheet();
			dataValidation.forEach(x -> sheetError.addValidationData(x));
			cellRange.forEach(x -> sheetError.addMergedRegionUnsafe(x));
			setErrorFount();
			int theRowIndex = 0;
			for (int k = 0; k < header.size(); k++) {
				columnSum = 0;
				setCell(header.get(k), theRowIndex, false, null);
				theRowIndex++;
			}
			for (int x = 0; x < listMap.size(); x++) {
				for (Map.Entry<String, Row> entry : listMap.get(x).entrySet())
					setCell(entry.getValue(), theRowIndex, true, entry.getKey());
				theRowIndex++;
			}
			handleData(data);
			try {
				wbErroe.write(os);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (wbErroe != null)
					wbErroe.close();
			}
			return new ByteArrayInputStream(os.toByteArray());
		} else {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "错误数据为空!");
		}
	}

	private void setCell(Row r, int rx, boolean isAdd, String msg) {
		XSSFRow sourceRow = (XSSFRow) r;
		XSSFRow targetRow = sheetError.createRow(rx);
		for (int i = sourceRow.getFirstCellNum(); i < sourceRow.getLastCellNum(); i++) {
			copyCell(sourceRow.getCell(i), targetRow.createCell(i));
			if (!isAdd)
				columnSum++;
			if (isAdd && i + 1 == sourceRow.getLastCellNum()) {
				setErrorStyle(targetRow, msg);
			}
		}
	}

	/** 错误行字体设置 **/
	private void setErrorFount() {
		Font font = wbErroe.createFont();
		font.setColor(Font.COLOR_RED);
		CellStyle cellStyle = wbErroe.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setWrapText(true);
		this.styleError = cellStyle;
	}

	/** 错误行样式设置 **/
	private void setErrorStyle(XSSFRow row, String msg) {
		XSSFCell e = row.createCell(columnSum);
		e.setCellValue(msg);
		e.setCellStyle(styleError);
		sheetError.setColumnWidth(columnSum, 8000);
	}

	private void copyCell(XSSFCell sourceCell, XSSFCell targetCell) {
		if (sourceCell == null) {
			targetCell.setCellValue("");
			return;
		}
		targetCell.setCellType(sourceCell.getCellType());
		targetCell.getCellStyle().cloneStyleFrom(sourceCell.getCellStyle());
		CellStyle style = targetCell.getRow().getSheet().getWorkbook().createCellStyle();
		style.cloneStyleFrom(sourceCell.getCellStyle());
		targetCell.setCellStyle(style);
		switch (sourceCell.getCellType()) {
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(sourceCell)) {
				targetCell.setCellValue(sourceCell.getDateCellValue());
			} else {
				targetCell.setCellValue(sourceCell.getNumericCellValue());
			}
			break;
		case STRING:
			targetCell.setCellValue(sourceCell.getStringCellValue());
			break;
		case FORMULA:
			targetCell.setCellFormula(sourceCell.getCellFormula());
			break;
		case BLANK:
			targetCell.setBlank();
			break;
		case BOOLEAN:
			targetCell.setCellValue(sourceCell.getBooleanCellValue());
			break;
		case ERROR:
			targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
			break;
		default:
			throw new IllegalArgumentException("Invalid cell type " + sourceCell.getCellType());
		}
	}

	@SuppressWarnings("unchecked")
	private List<T> read(ExcelMapRow<T> mapRow) throws IOException {
		readData = new ArrayList<>();
		dataValidation = sheet.getDataValidations();
		cellRange = sheet.getMergedRegions();
		int rowIndex = indexRows <= 0 ? 0 : indexRows;
		if (isErrorExcel && rowIndex != 0) {
			for (int o = 0; o < rowIndex; o++)
				header.add(sheet.getRow(o));
		}
		for (; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
			T t = (T) mapRow.mapRow(sheet.getRow(rowIndex), rowIndex);
			if (t != null) {
				if (!(t instanceof String))
					readData.add(t);
			} else
				break;
		}
		if (inputStream != null) {
			inputStream.close();
		}
		return readData;
	}

	private ResponseEntity<byte[]> downloadTemplate(String fileName, Map<String, String[]> selected, HttpServletRequest request, HttpServletResponse response, int indexRows, boolean isNeedReturn) throws Exception {
		setMap(false);
		for (String k : key.keySet()) {
			ClassModel cm = key.get(k);
			if (selected.containsKey(cm.getSelectedKey())) {
				CellRangeAddressList cral = new CellRangeAddressList(indexRows, 100000, Integer.valueOf(cm.getColumnIndex()) - 1, Integer.valueOf(cm.getColumnIndex()) - 1);
				DataValidationHelper dvh = sheet.getDataValidationHelper();
				DataValidationConstraint dvc = dvh.createExplicitListConstraint(selected.get(cm.getSelectedKey()));
				DataValidation dv = dvh.createValidation(dvc, cral);
				dv.setSuppressDropDownArrow(wb != null ? false : true);
				if (cm.isNotNull()) {
					dv.createErrorBox("", ERROR_DATA);
					dv.setShowErrorBox(true);
					dv.createPromptBox("", cm.getMessage());
				}
				dv.setShowPromptBox(true);
				sheet.addValidationData(dv);
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (wb != null)
			wb.write(baos);
		else
			xwb.write(baos);
		if (isNeedReturn)
			return ExcelUtil.outExcel(fileName, suffix, new ByteArrayInputStream(baos.toByteArray()), request);
		ExcelUtil.outHostExcel(fileName, suffix, new ByteArrayInputStream(baos.toByteArray()), request, response);
		return null;
	}

	public class ExcelMapRowImpl implements ExcelRead.ExcelMapRow<T> {
		Map<String, String> rowMsg;
		boolean status;
		Object obj = null;

		@Override
		public Object mapRow(Row row, Integer rowIndex) {
			this.rowMsg = new HashMap<>();
			this.status = true;
			return getRows(row, rowIndex);
		}

		private Object getRows(Row row, int rowIndex) {
			try {
				obj = clazz.newInstance();
				for (int i = indexColumn; i < key.size(); i++) {
					ClassModel cm = key.get(String.valueOf(i + 1));
					Cell cell = row.getCell(i);
					if (cm.isValidation() && cell == null) {
						rowMsg.put(cm.getAlias(), cm.getMessage());
						status = false;
					} else
						valueCheck(cm, cell);
					if (isErrorExcel && (i + 1 == key.size())) {
						String rm = getData(rowMsg);
						if (StringUtils.isNotBlank(rm)) {
							Map<String, Row> rmap = new HashMap<>();
							rmap.put(rm, row);
							listMap.add(rmap);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (rowErrorStyle == null)
				rowErrorStyle = status ? row : null;
			return status ? obj : addResult(rowIndex, rowMsg);
		}

		private String addResult(Integer row, Map<String, String> rowMsg) {
			result.put(String.valueOf(row + 1), rowMsg);
			return FAILURE;
		}

		private String getData(Map<String, String> msg) {
			StringBuilder sb = new StringBuilder();
			msg.forEach((k, v) -> sb.append(String.format(MSG, k, v)));
			String ms = sb.toString();
			if (!msg.isEmpty())
				ms = ms.substring(0, ms.length() - 1);
			return ms;
		}

		/** 值校验,目前不做校验 **/
		private void valueCheck(ClassModel cm, Cell cell) throws NoSuchMethodException, SecurityException {
			Method m = clazz.getDeclaredMethod(cm.getMethod(), cm.type);
			try {
				if (cm.type.isAssignableFrom(String.class)) {
					m.invoke(obj, cell == null ? null : formatValidation(cell));
				} else if (cm.type.isAssignableFrom(Integer.class) || cm.type.isAssignableFrom(int.class)) {
					m.invoke(obj, cell == null ? null : (int) cell.getNumericCellValue());
				} else if (cm.type.isAssignableFrom(Long.class) || cm.type.isAssignableFrom(long.class)) {
					m.invoke(obj, cell == null ? null : (long) cell.getNumericCellValue());
				} else if (cm.type.isAssignableFrom(Double.class) || cm.type.isAssignableFrom(double.class)) {
					m.invoke(obj, cell == null ? null : cell.getNumericCellValue());
				} else if (cm.type.isAssignableFrom(Boolean.class) || cm.type.isAssignableFrom(boolean.class)) {
					m.invoke(obj, cell == null ? null : cell.getBooleanCellValue());
				} else if (cm.type.isAssignableFrom(Date.class)) {
					m.invoke(obj, cell == null ? null : DateUtils.strToDate(DateUtils.SDF.format(cell.getDateCellValue()), DateUtils.FORMAT_2));
				}
			} catch (Exception err) {
				logger.debug("Excel格式: ", err);
				status = false;
				rowMsg.put(cm.getAlias(), err instanceof NullPointerException ? cm.message : INCORRECT_FORMAT);
			}
		}

	}

	private String formatValidation(Cell cell) {
		String value = null;
		switch (cell.getCellType()) {
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				value = DateUtils.dateToString(cell.getDateCellValue(), DateUtils.FORMAT_2);
			} else {
				value = NumberFormat.getInstance().format(cell.getNumericCellValue());
			}
			break;
		case STRING:
			value = cell.getStringCellValue();
			break;
		case BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case BLANK:
			break;
		default:
			throw new IllegalArgumentException(INCORRECT_FORMAT);
		}
		return value;
	}

	/**
	 * 是否导出数据
	 * 
	 * @param isExportData
	 */
	private void setMap(boolean isExportData) {
		key = new HashMap<>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			OutExcel o = f.getAnnotation(OutExcel.class);
			if (o != null) {
				if (!isExportData) {
					if (StringUtils.isNotBlank(o.selectedKey())) {
						key.put(o.selectedKey(), new ClassModel(o.notNull(), o.selectedKey(), o.columnIndex(), o.messgae()));
					}
				} else {
					String a = StringUtils.isBlank(o.alias()) ? f.getName() : o.alias();
					key.put(o.columnIndex(), new ClassModel(f.getType(), f.getName(), getMethod(f), o.messgae(), a, o.notNull() ? true : false));
				}
			}
		}
	}

	private void handleData(List<? extends ResultExcelModel> data) {
		if (data == null || data.size() < 1)
			return;
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < data.size(); i++) {
			XSSFRow row = sheetError.createRow(sheetError.getLastRowNum() + i + 1);
			int ir = 0;
			for (Field f : fields) {
				OutExcel o = f.getAnnotation(OutExcel.class);
				if (o != null) {
					try {
						PropertyDescriptor pd = new PropertyDescriptor(f.getName(), clazz);
						Object obj = pd.getReadMethod().invoke(data.get(i));
						XSSFCell xc = row.createCell(ir);
						if (rowErrorStyle != null && rowErrorStyle.getCell(ir) != null) {
							xc.setCellType(rowErrorStyle.getCell(ir).getCellType());
							xc.getCellStyle().cloneStyleFrom(rowErrorStyle.getCell(ir).getCellStyle());
							CellStyle style = xc.getRow().getSheet().getWorkbook().createCellStyle();
							style.cloneStyleFrom(rowErrorStyle.getCell(ir).getCellStyle());
							xc.setCellStyle(style);
						}
						if (f.getType().isAssignableFrom(String.class) && obj != null) {
							xc.setCellValue(String.valueOf(obj));
						} else if ((f.getType().isAssignableFrom(Integer.class) || f.getType().isAssignableFrom(int.class)) && obj != null) {
							xc.setCellValue((Integer) obj);
						} else if ((f.getType().isAssignableFrom(Long.class) || f.getType().isAssignableFrom(long.class)) && obj != null) {
							xc.setCellValue((Long) obj);
						} else if ((f.getType().isAssignableFrom(Double.class) || f.getType().isAssignableFrom(double.class)) && obj != null) {
							xc.setCellValue((Double) obj);
						} else if ((f.getType().isAssignableFrom(Boolean.class) || f.getType().isAssignableFrom(boolean.class)) && obj != null) {
							xc.setCellValue((Boolean) obj);
						} else if (f.getType().isAssignableFrom(Date.class)) {
							CellStyle cs = wbErroe.createCellStyle();
							CreationHelper ch = wbErroe.getCreationHelper();
							cs.setDataFormat(ch.createDataFormat().getFormat(o.dateFormat()));
							if (obj != null)
								xc.setCellValue((Date) obj);
							xc.setCellStyle(cs);
						}
					} catch (Exception e) {
						logger.error("属性 " + f.getName(), e);
					}
					ir++;
				}
			}
			setErrorStyle(row, StringUtils.isBlank(data.get(i).getMessage()) ? "" : data.get(i).getMessage());
		}
	}

	private String getMethod(Field f) {
		return SET + f.getName().substring(0, 1).toUpperCase().concat(f.getName().substring(1));
	}

	public static interface ExcelMapRow<T> {
		Object mapRow(Row row, Integer rowIndex);
	}

	private void checkType(InputStream is) throws IOException {
		ByteArrayOutputStream baos = inputStreamToArray(is);
		try {
			if (XLS.equalsIgnoreCase(suffix)) {
				mXls(new ByteArrayInputStream(baos.toByteArray()));
			} else {
				mXlsx(new ByteArrayInputStream(baos.toByteArray()));
			}
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "EXCEL格式不对!");
		}
	}

	private void mXls(InputStream is) throws Exception {
		this.wb = new HSSFWorkbook(new POIFSFileSystem(is));
		this.sheet = wb.getSheetAt(0);
	}

	private void mXlsx(InputStream is) throws Exception {
		this.xwb = new XSSFWorkbook(is);
		this.sheet = xwb.getSheetAt(0);
	}

	private static ByteArrayOutputStream inputStreamToArray(InputStream input) throws IOException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int length;
			while ((length = input.read(buffer)) > -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();
			return baos;
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	public class ClassModel {
		Class<?> type;
		String name;
		String method;
		String message;
		boolean validation = false;
		String alias;

		boolean notNull;
		String selectedKey;
		String columnIndex;

		public ClassModel(Class<?> type, String name, String method, String message, String alias, boolean validation) {
			this.type = type;
			this.name = name;
			this.method = method;
			this.message = message;
			this.validation = validation;
			this.alias = alias;
		}

		public ClassModel(boolean notNull, String selectedKey, String columnIndex, String message) {
			this.notNull = notNull;
			this.selectedKey = selectedKey;
			this.columnIndex = columnIndex;
			this.message = message;
		}

		public Class<?> getType() {
			return type;
		}

		public void setType(Class<?> type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public boolean isValidation() {
			return validation;
		}

		public void setValidation(boolean validation) {
			this.validation = validation;
		}

		public boolean isNotNull() {
			return notNull;
		}

		public void setNotNull(boolean notNull) {
			this.notNull = notNull;
		}

		public String getSelectedKey() {
			return selectedKey;
		}

		public void setSelectedKey(String selectedKey) {
			this.selectedKey = selectedKey;
		}

		public String getColumnIndex() {
			return columnIndex;
		}

		public void setColumnIndex(String columnIndex) {
			this.columnIndex = columnIndex;
		}

	}

}
