package com.rondaful.cloud.supplier.utils;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.entity.ErpFreightTrial;
import com.rondaful.cloud.supplier.entity.FreightTrial;
import com.rondaful.cloud.supplier.entity.GranaryFreightTrial;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseDTO;
import com.rondaful.cloud.supplier.model.enums.ResponseErrorCode;
import com.rondaful.cloud.supplier.remote.RemoteErpService;
import com.rondaful.cloud.supplier.remote.RemoteGranaryService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FreightUtil {

    private final Logger logger = LoggerFactory.getLogger(FreightUtil.class);

    @Autowired
    private RemoteErpService remoteErpService;

    @Autowired
    private RemoteGranaryService remoteGranaryService;

    @Autowired
    private IWarehouseBasicsService warehouseBasicsService;

    /**
     * 获取谷仓运费试算
     *
     * @param freightTrial
     * @return
     */
//    @Bean
    public List<GranaryFreightTrial> getGranaryFreightTrial(FreightTrial freightTrial) {
//        logger.info("freightTrial={}",freightTrial);
        List<GranaryFreightTrial> granaryFreightTrialList = new ArrayList<>();
        if (StringUtils.isEmpty(freightTrial.getPostCode()))  throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200403, "邮政编码不能为空");
        JSONObject  json = remoteGranaryService.getCalculateDeliveryFee(freightTrial, freightTrial.getAppKey(), freightTrial.getAppToken(), freightTrial.getWarehouseCode());
        if (!"Failure".equals(json.getString("ask")))  granaryFreightTrialList = JSONObject.parseArray(json.getString("data"), GranaryFreightTrial.class);
        if (CollectionUtils.isEmpty(granaryFreightTrialList)) throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200514);
        granaryFreightTrialList.stream().forEach(x -> x.setCurrency(json.getString("currency")));
        return granaryFreightTrialList;
    }

    /*
     * erp运费试算
     */
//    @Bean
    public List<ErpFreightTrial> getErpFreightTrial(FreightTrial freightTrial) {
        List<ErpFreightTrial> list = new ArrayList<>();
        if(StringUtils.isEmpty(freightTrial.getWarehouseCode())){
            WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(freightTrial.getWarehouseId());
            freightTrial.setWarehouseCode(warehouseDTO.getWarehouseCode());
        }
        String  result = remoteErpService.erpTrialBySKUS(freightTrial.getWarehouseCode(),freightTrial.getCountryCode(),
                StringUtils.isEmpty(freightTrial.getLogisticsCode()) ? null : Arrays.asList(freightTrial.getLogisticsCode()),
                freightTrial.getList(), freightTrial.getPlatformType(),freightTrial.getCity(),freightTrial.getPostCode());
        JSONObject json = null;
        try {
            json = JSONObject.parseObject(result);
        } catch (Exception e) {
            throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200515);
        }
        String message = json.getString("message");
        if (StringUtils.isNotEmpty(json.getString("data")) && message.equalsIgnoreCase("success")) {
            list = JSONObject.parseArray(json.getString("data"), ErpFreightTrial.class);
        } else {
            throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200515,message);
        }
        return list;
    }


    public Map<String,Object> getGcFreightTrial(FreightTrial param) {
        Map<String,Object> result = new HashMap<String,Object>();
        if(StringUtils.isEmpty(param.getPostCode())) throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200403,"邮政编码不能为空");
        JSONObject json = remoteGranaryService.getCalculateDeliveryFee(param,param.getAppKey(),param.getAppToken(),param.getWarehouseCode());
//         json=JSONObject.parseObject("{\"ask\":\"Success\",\"message\":\"\",\"data\":[{\"sm_code\":\"FEDEX-LARGEPARCEL\",\"sm_name\":\"FEDEX-LARGEPARCEL\",\"sm_name_cn\":\"FEDEX-大包\",\"sm_delivery_time_min\":\"4\",\"sm_delivery_time_max\":\"2\",\"total\":6.186,\"income\":[{\"name\":\"出库包装费\",\"amount\":\"0.100\"},{\"name\":\"偏远\",\"amount\":\"0.980\"},{\"name\":\"运输费\",\"amount\":\"4.240\"},{\"name\":\"操作费\",\"amount\":\"0.500\"},{\"name\":\"燃油附加费\",\"amount\":\"0.297\"},{\"name\":\"杂费燃油附加费\",\"amount\":\"0.069\"}]}],\"Error\":{\"errorMessage\":\"\",\"errCode\":\"\"},\"currency\":\"USD\"}");
        if ("Failure".equals(json.getString("ask")))	throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200515, json.getString("message"));
        List<GranaryFreightTrial> GranaryFreightTrialList = JSONObject.parseArray(json.getString("data"), GranaryFreightTrial.class);
        if(CollectionUtils.isEmpty(GranaryFreightTrialList)) throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200514);
        result.put("data",GranaryFreightTrialList);
        if(StringUtils.isNotEmpty(json.getString("currency"))) {
            result.put("currency",json.getString("currency"));
        }
        return result;
    }
}
