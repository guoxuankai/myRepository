package com.rondaful.cloud.user.service.impl;

import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.user.service.ISellerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/7/24
 * @Description:
 */
@Service("excelExportServerImpl")
public class ExcelExportServerImpl implements IExcelExportServer {

    public static String EXPORT_SELLER_USER="seller";

    @Autowired
    private ISellerUserService sellerUserService;

    @Override
    public List<Object> selectListForExcelExport(Object o, int i) {
        List<Object> result=null;
        Map<String,String> map=(Map<String, String>) o;
        JSONObject params=JSONObject.parseObject(map.get("params"));
        switch (map.get("type")){
            case "seller":
            result=this.sellerUserService.export(i,params.getInteger("userId"),params.getDate("satartDate"),params.getDate("endDtate"));
            break;
            default:
                return null;
        }

        return result;
    }


}
