package com.rondaful.cloud.supplier.config.async;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.service.FileService;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.inventory.IneventoryExportDTO;
import com.rondaful.cloud.supplier.remote.RemoteUserService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import com.rondaful.cloud.supplier.service.impl.ExcelExportServerImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/9/4
 * @Description:
 */
@Component
public class AsyncExport {
    private final Logger logger = LoggerFactory.getLogger(AsyncExport.class);

    @Autowired
    private IExcelExportServer excelExportServer;
    @Autowired
    private FileService fileService;
    @Autowired
    private IWarehouseBasicsService basicsService;
    @Autowired
    private RemoteUserService userService;

    @Value("${spring.cloud.config.profile}")
    private String env;

    @Async
    public void inventoryExport(Map<String,Object> dataParams,Integer userId,Integer topUserId,Integer platformType){
        String url=null;
        Integer status=0;
        Integer jobId=null;
        Workbook workbook=null;
        if (ExcelExportServerImpl.EXPORT_INVENTORY.equals(dataParams.get("type"))){
            JSONObject jsonObject=JSONObject.parseObject(String.valueOf(dataParams.get("params")));
            String jobName="仓库列表-"+this.basicsService.getByWarehouseId(jsonObject.getInteger("warehouseId")).getWarehouseName();
            FeignResult<Integer> feignResult=this.userService.insertDown(jobName,userId,topUserId,platformType);
            jobId=feignResult.getData();
            if (!feignResult.getSuccess()){
                return;
            }
            ExportParams params=new ExportParams(null,"inventory", ExcelType.XSSF);
            IneventoryExportDTO dto=this.getInevntoryDTO(jsonObject.getString("i18n"));
            workbook= ExcelExportUtil.exportBigExcel(params, dto.getClass(),excelExportServer,dataParams);
        }
        try {
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            workbook.write(out);
            url=fileService.saveFile(ConstantAli.getEnv(env), ConstantAli.FolderType.EXPORT,System.currentTimeMillis()+".xlsx",out.toByteArray(),null,null,null);
            status=1;
        } catch (IOException e) {
            logger.error("导出异常:msg={}",e.getMessage());
            status=4;
        }finally {
            this.userService.updateDownStatus(jobId,url,status);
        }
    }

    private IneventoryExportDTO getInevntoryDTO(String languageType){
        IneventoryExportDTO dto=new IneventoryExportDTO();
        try {
            Field[] fields=dto.getClass().getDeclaredFields();
            for (int i = 1; i < fields.length; i++) {
                Field field=fields[i];
                String fileName=fields[i].getName();
                Excel excel=field.getAnnotation(Excel.class);
                System.out.println(excel.name());
                InvocationHandler invocationHandler=Proxy.getInvocationHandler(excel);
                Field declaredField =invocationHandler.getClass().getDeclaredField("memberValues");
                declaredField.setAccessible(true);
                Map memberValues = (Map) declaredField.get(invocationHandler);
                memberValues.put("name", Utils.i18nByLanguage("export.execl.inevntory."+fileName,languageType));
            }
        } catch (Exception e) {
            logger.error("导出时反射设置国家化异常");
        }
        return dto;
    }
}
