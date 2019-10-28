package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wh
 * @description 谷仓订单api测试
 * @date 2019/4/26
 */
@Service
public class GoodCangOrderInterceptService {
    @Autowired
    private GranaryUtils granaryUtils;
    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;

    private static Logger log = LoggerFactory.getLogger(GoodCangOrderInterceptService.class);

    @Autowired
    private RemoteSupplierService remoteSupplierService;
    /**
     * 调用谷仓取消订单api
     *
     * @param referenceId 对应reference_id推送至谷仓订单号
     * @param reason      拦截订单原因描述
     * @return 谷仓的api返回的json字符串
     * {"ask":"Failure","message":"订单已废弃","order_code":"G1149-190429-0031","errCode":"","Error":{"errMessage":"订单已废弃","errCode":10103099}} or
     * {"ask":"Success","message":"Success","order_code":"000011-160617-0001","cancel_status":2}
     */
    public String cancelOrder(String referenceId, String reason, String deliveryWarehouseId) {
        Map<String, WarehouseDTO> map = systemOrderCommonService.getGCAuthorizeByWarehouseId(new ArrayList<String>() {{
            add(deliveryWarehouseId);
        }});
        WarehouseDTO dto = map.get(deliveryWarehouseId);
//        Map<String, AuthorizeDTO> map = systemOrderCommonService.getGCAuthorizeByCompanyCode(new HashSet<String>() {{
//            this.add(warehouseCode);
//        }});
//        AuthorizeDTO dto = map.get(warehouseCode.split("_")[1]);
        String serviceName = "cancelOrder";
        HashMap paramMap = new HashMap();
        paramMap.put("order_code", referenceId);
        paramMap.put("reason", reason);
        String jsonString = FastJsonUtils.toJsonString(paramMap);
        GranaryUtils instance = granaryUtils.getInstance(dto.getAppToken(), dto.getAppKey(), null, jsonString, serviceName);
        log.info("==============================调用谷仓取消订单接口并入参");
        try {
            return instance.getCallService();
        } catch (Exception e) {
            log.error("异常：调用谷仓取消订单接口异常", referenceId, reason, e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统远程调用谷仓api异常");
        }
    }

    /**
     * 根据warehouseId判断仓库是否为谷仓仓库
     *
     * @param warehouseId 谷仓仓库ID
     * @return boolean
     */
    public boolean judgeGcWatrhouse(String warehouseId) {
        String result = remoteSupplierService.getWarehouseByIds(warehouseId);
        String data = Utils.returnRemoteResultDataString(result, "供应商服务异常");
        if (StringUtils.isBlank(data)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "供应商服务异常");
        }
        WarehouseDTO warehouseDTO = JSONObject.parseObject(data, WarehouseDTO.class);
        if (Objects.equals(warehouseDTO.getFirmCode(), "GOODCANG")) {
            return  true;
        }else{
            return false;
        }
/*
        if (null != warehouseCode && warehouseCode.length() > 1) {
            if ("GC".equals(warehouseCode.substring(0, 2))) {
                return true;
            }
        }
        return false;*/
    }
}
