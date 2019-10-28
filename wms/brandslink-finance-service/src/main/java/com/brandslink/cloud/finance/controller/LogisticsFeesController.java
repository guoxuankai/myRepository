package com.brandslink.cloud.finance.controller;


import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.finance.constants.LogisticsFeesStatusConstant;
import com.brandslink.cloud.finance.pojo.entity.LogisticsFees;
import com.brandslink.cloud.finance.pojo.vo.LogisticsFeesVO;
import com.brandslink.cloud.finance.service.LogisticsFeesService;
import com.brandslink.cloud.finance.utils.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


/**
 * 物流商收费
 */
@RestController
@RequestMapping(value = "/logisticsFees")
@Api("物流商收费")
public class LogisticsFeesController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LogisticsFeesController.class);


    @Autowired
    private LogisticsFeesService logisticsFeesService;


    @PostMapping("/list")
    @ApiOperation(value = "物流商收费列表", notes = "物流商收费列表")
    public Page<LogisticsFees> list(@RequestBody LogisticsFeesVO logisticsFeesQuery) {
        Page.builder(logisticsFeesQuery.getPageNum(), logisticsFeesQuery.getPageSize());
        List<LogisticsFees> list = logisticsFeesService.list(logisticsFeesQuery);
        Page resultPage = new Page(list);
        return resultPage;
    }

    @GetMapping("/exportExcel")
    @ApiOperation(value = "导出", notes = "导出")
    public void exportExcel() {
        List<LogisticsFees> list = logisticsFeesService.list(new LogisticsFeesVO());
        ExcelUtil.exportExcel(list, "物流商收费", "Sheet1", LogisticsFees.class, "物流商收费.xlsx", response);
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "导入", notes = "导入")
    public void importExcel(@RequestParam("file") MultipartFile file) {
        List<LogisticsFees> logisticsFees = ExcelUtil.importExcel(file, 1, 1, LogisticsFees.class);
        logisticsFeesService.importData(logisticsFees);

    }

    @PutMapping("/affirm/{ids}")
    @ApiOperation(value = "确认", notes = "确认")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "ids", value = "主键id集合", required = true)
    })
    public void affirm(@PathVariable("ids") String ids) {
        String[] split = ids.split(",");
        int[] idArray = Arrays.asList(split).stream().mapToInt(Integer::parseInt).toArray();
        logisticsFeesService.updateStatus(idArray, LogisticsFeesStatusConstant.CONFIRMED);
    }

    @PutMapping("/clear/{ids}")
    @ApiOperation(value = "清空导入数据", notes = "清空导入数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "ids", value = "主键id集合", required = true)
    })
    public void clear(@PathVariable("ids") String ids) {
        String[] split = ids.split(",");
        int[] idArray = Arrays.asList(split).stream().mapToInt(Integer::parseInt).toArray();
        logisticsFeesService.updateStatus(idArray, LogisticsFeesStatusConstant.FOR_IMPORT);
    }


    @PutMapping("/downloadExcel")
    @ApiOperation(value = "下载模板", notes = "下载模板")
    public void downloadExcel() {
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            String filename = "logisticsFees.xlsx";
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource("classpath:template/logisticsFees.xlsx");
            inputStream = resource.getInputStream();
            outputStream = response.getOutputStream();
            byte[] b = new byte[2048];
            int i;
            while ((i = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, i);
            }
        } catch (Exception e) {
            logger.error("下载Excel模板失败:", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "下载Excel模板异常");
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }


    }


}
