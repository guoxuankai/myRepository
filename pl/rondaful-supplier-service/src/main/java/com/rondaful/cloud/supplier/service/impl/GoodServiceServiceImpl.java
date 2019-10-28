package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.supplier.entity.basics.WarehouseList;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.logistics.GQueryDeliveryFeeDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsCostDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsDTO;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.remote.RemoteOrderService;
import com.rondaful.cloud.supplier.service.IGoodServiceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/9/2
 * @Description:
 */
@Service("goodServiceServiceImpl")
public class GoodServiceServiceImpl implements IGoodServiceService {
    private final Logger logger = LoggerFactory.getLogger(GoodServiceServiceImpl.class);

    @Autowired
    private GranaryUtils granaryUtils;
    @Autowired
    private RemoteOrderService orderService;

    @Value("${wsdl.url}")
    private String wsdlUrl;

    /**
     * 获取谷仓的仓库列表
     *
     * @param appkey
     * @param appToken
     * @return
     */
    @Override
    public List<WarehouseList> getsWarehouseList(String appkey, String appToken,Integer firmId) {
        List<WarehouseList> list=new ArrayList<>();
        JSONObject params=new JSONObject();
        try {
            String response=this.granaryUtils.getInstance(appToken,appkey,this.wsdlUrl,params.toJSONString(),"getWarehouse").getCallService();
            if (StringUtils.isEmpty(response)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用谷仓服务异常");
            }
            JSONObject resJson=JSONObject.parseObject(response);
            if ("Failure".equals(resJson.getString("ask"))){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),resJson.getString("message"));
            }
            if (resJson.getInteger("count")<1){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"没有对应的仓库列表");
            }
            for (int i = 0; i < resJson.getJSONArray("data").size(); i++) {
                WarehouseList listDO=JSONObject.parseObject(resJson.getJSONArray("data").getString(i),WarehouseList.class);
                listDO.setFirmId(firmId);
                listDO.setStatus(StatusEnums.ACTIVATE.getStatus());
                list.add(listDO);
            }
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"谷仓服务异常");
        }
        return list;
    }

    /**
     * 获取所有物流方式
     *
     * @param appkey
     * @param appToken
     * @return
     */
    @Override
    public List<TranLogisticsDTO> getsLogistics(String appkey, String appToken,String warehouseCode) {
        JSONObject params=new JSONObject();
        params.put("warehouseCode",warehouseCode);
        JSONObject resJson=new JSONObject();
        try {
            String response=this.granaryUtils.getInstance(appToken,appkey,this.wsdlUrl,params.toJSONString(),"getShippingMethod").getCallService();
            resJson=JSONObject.parseObject(response);
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"谷仓服务异常");
        }
        if (StringUtils.isNotEmpty(resJson.getString("message"))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),resJson.getString("message"));
        }
        List<TranLogisticsDTO> result=new ArrayList<>(resJson.getJSONArray("data").size());
        for (int i = 0; i < resJson.getJSONArray("data").size(); i++) {
            JSONObject data=resJson.getJSONArray("data").getJSONObject(i);
            TranLogisticsDTO dto=JSONObject.parseObject(data.toJSONString(),TranLogisticsDTO.class);
            dto.setSpNameEn(dto.getSpCode());
            dto.setSpName(dto.getSpCode());
            result.add(dto);
        }
        return result;
    }

    /**
     * 运费试算
     *
     * @param appkey
     * @param appToken
     * @param dto
     * @return
     */
    @Override
    public List<TranLogisticsCostDTO> getCalculateFee(String appkey, String appToken, GQueryDeliveryFeeDTO dto) {
        JSONObject resJson=new JSONObject();
        try {
            String response=this.granaryUtils.getInstance(appToken,appkey,this.wsdlUrl,JSONObject.toJSONString(dto, true),"getCalculateDeliveryFee").getCallService();
            resJson=JSONObject.parseObject(response);
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"谷仓服务异常");
        }
        if (StringUtils.isNotEmpty(resJson.getString("message"))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),resJson.getString("message"));
        }
        if (resJson.getJSONArray("data").isEmpty()){
            return null;
        }
        List<TranLogisticsCostDTO> result=new ArrayList<>();
        for (int i = 0; i < resJson.getJSONArray("data").size(); i++) {
            JSONObject data=resJson.getJSONArray("data").getJSONObject(i);
            TranLogisticsCostDTO costDTO=new TranLogisticsCostDTO();
            costDTO.setSmCode(data.getString("sm_code"));
            costDTO.setSmDeliveryTimeMax(data.getIntValue("sm_delivery_time_max"));
            costDTO.setSmDeliveryTimeMin(data.getIntValue("sm_delivery_time_min"));
            costDTO.setSmName(data.getString("sm_name_cn"));
            costDTO.setSmNameEn(data.getString("sm_code"));
            costDTO.setTotal(data.getBigDecimal("total"));
            if (!"USD".equals(resJson.getString("currency"))){
                FeignResult<BigDecimal> feignResult= this.orderService.getRate1(resJson.getString("currency"),"USD");
                costDTO.setTotal(costDTO.getTotal().multiply(feignResult.getData()).setScale(5,BigDecimal.ROUND_HALF_UP));
            }
            result.add(costDTO);
        }
        return result;
    }

}
