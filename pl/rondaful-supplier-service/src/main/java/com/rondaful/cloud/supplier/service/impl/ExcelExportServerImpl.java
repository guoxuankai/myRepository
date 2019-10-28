package com.rondaful.cloud.supplier.service.impl;

import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.supplier.service.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/7/25
 * @Description:
 */
@Service("excelExportServerImpl")
public class ExcelExportServerImpl implements IExcelExportServer {

    public static String EXPORT_INVENTORY="inventory";
    @Autowired
    private IInventoryService inventoryService;

    @Override
    public List<Object> selectListForExcelExport(Object o, int i) {
        Map<String,String> map=(Map<String, String>) o;
        JSONObject params=JSONObject.parseObject(map.get("params"));
        switch (map.get("type")){
            case "inventory":
                return this.inventoryService.export(params.getInteger("warehouseId"),params.getInteger("userId"),i,params.getString("i18n"));
            default:
                return null;
        }
    }
}
