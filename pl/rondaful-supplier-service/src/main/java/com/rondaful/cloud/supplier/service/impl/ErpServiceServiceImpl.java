package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.rondaful.cloud.common.enums.ERPWarehouseEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.entity.basics.WarehouseList;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.logistics.EQueryDeliveryFeeDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.SkuNum;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsCostDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsDTO;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.remote.RemoteCommodityService;
import com.rondaful.cloud.supplier.remote.RemoteOrderService;
import com.rondaful.cloud.supplier.service.IErpServiceService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
@Service("erpServiceServiceImpl")
public class ErpServiceServiceImpl implements IErpServiceService {

    @Value("${erp.url}")
    private  String erpUrl;

    @Autowired
    private RemoteOrderService orderService;
    @Autowired
    private RemoteCommodityService commodityService;

    /**
     * 获取erp的仓库列表
     *
     * @return
     */
    @Override
    public List<WarehouseList> getsWarehouseList(Integer firmId) {
        List<WarehouseList> list=new ArrayList<>();
        String response= HttpUtil.postSendByFormData(this.erpUrl, ERPWarehouseEnum.methods.WAREHOUSE_LIST.getMethod(),null);
        if (StringUtils.isEmpty(response)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用erp服务异常");
        }
        JSONObject resJson=JSONObject.parseObject(response);
        if (!"success".equals(resJson.getString("message"))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),resJson.getString("message"));
        }
        resJson.getJSONArray("data");
        for (int i = 0; i <resJson.getJSONArray("data").size() ; i++) {
            JSONObject ob=resJson.getJSONArray("data").getJSONObject(i);
            WarehouseList listDO=new WarehouseList();
            listDO.setWarehouseName(ob.getString("name"));
            listDO.setCountryCode(ob.getString("country"));
            listDO.setWarehouseCode(ob.getString("code"));
            listDO.setFirmId(firmId);
            listDO.setStatus(StatusEnums.ACTIVATE.getStatus());
            list.add(listDO);
        }
        return list;
    }

    /**
     * 获取物流方式
     *
     * @param warehouseType 1-本地仓   7-谷仓
     * @return
     */
    @Override
    public List<TranLogisticsDTO> getsLogistics(Integer warehouseType) {
        Map<String,String> params=new HashMap<>(1);
        if (warehouseType!=null){
            params.put("warehouse_type",String.valueOf(warehouseType));
        }
        String response= HttpUtil.postSendByFormData(this.erpUrl, ERPWarehouseEnum.methods.LOGISTICS_LIST.getMethod(),params);
        if (StringUtils.isEmpty(response)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用erp服务异常");
        }
        JSONObject resJson=JSONObject.parseObject(response);
        if (!"success".equals(resJson.getString("message"))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),resJson.getString("message"));
        }
        List<TranLogisticsDTO> result=new ArrayList<>(20);
        for (int i = 0; i < resJson.getJSONArray("data").size(); i++) {
            JSONObject data=resJson.getJSONArray("data").getJSONObject(i);
            for (int j = 0; j < data.getJSONArray("use_warehouse_arr").size(); j++) {
                JSONObject warhouse= data.getJSONArray("use_warehouse_arr").getJSONObject(j);
                TranLogisticsDTO dto=new TranLogisticsDTO();
                dto.setCode(data.getString("code"));
                dto.setName(data.getString("shortname"));
                dto.setNameEn(Utils.translationTo(dto.getName(),null,null));
                dto.setSpCode(data.getString("carrier_code"));
                dto.setSpName(data.getString("carrier_name"));
                dto.setSpNameEn(data.getString("carrier_code"));
                dto.setWarehouseCode(warhouse.getString("code"));
                result.add(dto);
            }
        }
        return result;
    }

    /**
     * 运费试算
     *
     * @param dto
     * @return
     */
    @Override
    public List<TranLogisticsCostDTO> getCalculateFee(EQueryDeliveryFeeDTO dto) {
        Map<String,String> params=new HashMap<>(10);
        params=JSONObject.parseObject(JSONObject.toJSONString(dto, SerializerFeature.PrettyFormat),Map.class);
        if (CollectionUtils.isNotEmpty(dto.getSkus())){
            JSONArray skuNums=new JSONArray(dto.getSkus().size());
            dto.getSkus().forEach(skuNum -> {
                Object object=this.commodityService.getBySku(skuNum.getSku(),null,null);
                JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(object));
                if (jsonObject.getBoolean("success")){
                    skuNums.add(new SkuNum(jsonObject.getJSONObject("data").getString("supplierSku"),skuNum.getNum()));
                }
            });
            params.put("skus",skuNums.toJSONString());
        }else {
            params.remove("shipping_code_arr");
        }
        if (CollectionUtils.isNotEmpty(dto.getShipping_code_arr())){
            params.put("shipping_code_arr",JSONObject.toJSONString(dto.getShipping_code_arr()));
        }else {
            params.remove("shipping_code_arr");
        }
        String response= HttpUtil.postSendByFormData(this.erpUrl, ERPWarehouseEnum.methods.TRAIL.getMethod(),params);
        if (StringUtils.isEmpty(response)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用erp服务异常");
        }
        JSONObject resJson=JSONObject.parseObject(response);
        if (!"success".equals(resJson.getString("message"))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),resJson.getString("message"));
        }
        if (resJson.getJSONArray("data").isEmpty()){
            return null;
        }
        FeignResult<BigDecimal> feignResult= this.orderService.getRate1("CNY","USD");
        List<TranLogisticsCostDTO> result=new ArrayList<>(20);
        for (int i = 0; i < resJson.getJSONArray("data").size(); i++) {
            JSONObject data=resJson.getJSONArray("data").getJSONObject(i);
            TranLogisticsCostDTO costDTO=new TranLogisticsCostDTO();
            costDTO.setSmCode(data.getString("shipping_code"));
            costDTO.setSmName(data.getString("shipping_name"));
            costDTO.setSmNameEn(data.getString("shipping_code"));
            costDTO.setSmDeliveryTimeMin(data.getIntValue("earliest_days"));
            costDTO.setSmDeliveryTimeMax(data.getIntValue("latest_days"));
            costDTO.setTotal((data.getBigDecimal("cny_amount").multiply(feignResult.getData())).setScale(5,BigDecimal.ROUND_HALF_UP));
            result.add(costDTO);
        }
        return result;
    }
}
