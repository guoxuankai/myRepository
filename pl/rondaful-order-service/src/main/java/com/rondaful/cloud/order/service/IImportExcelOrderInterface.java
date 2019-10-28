package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.order.entity.ExcelOrder;
import com.rondaful.cloud.order.entity.ExcelOrderStatisticsDTO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量导入服务接口
 *
 * @author liusiying
 */
public interface IImportExcelOrderInterface {
    /**
     * 用于生产使用
     *
     * @param workbook
     * @param userDTO
     * @return
     * @throws IOException
     */
    void resolverExcelAndSaveDate(XSSFWorkbook workbook, UserDTO userDTO) throws IOException, InterruptedException;

    /**
     * 用于本地测试
     *
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    Map<String, Object> resolverExcelAndSaveDate(File file) throws IOException, InvalidFormatException;

    /**
     * 处理订单详情信息
     * @param sysOrder
     * @param userDTO
     * @param toutalNum
     * @param concurrentHashMap
     */
    void fillOrderInfo(ExcelOrder sysOrder, UserDTO userDTO, AtomicInteger toutalNum, ConcurrentHashMap<String, ExcelOrderStatisticsDTO> concurrentHashMap);
}
