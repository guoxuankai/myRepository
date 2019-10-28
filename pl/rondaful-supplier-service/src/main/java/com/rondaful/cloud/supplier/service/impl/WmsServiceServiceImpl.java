package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.supplier.entity.basics.WarehouseList;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsCostDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.TranLogisticsDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.WQueryDeliveryFeeDTO;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.service.IWmsServiceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/9/2
 * @Description:
 */
@Service("wmsServiceServiceImpl")
public class WmsServiceServiceImpl implements IWmsServiceService {

    @Value("${brandslink.wms.url}")
    private String wmsUrl;

    /**
     * 获取wms的仓库列表
     *
     * @param appkey
     * @param appToken
     * @param firmId
     * @return
     */
    @Override
    public List<WarehouseList> getsWarehouseList(String appkey, String appToken, Integer firmId) {
        List<WarehouseList> list=new ArrayList<>();
        String response=null;
        try {
            URIBuilder uri=new URIBuilder(this.wmsUrl+"/center/warehouse/allList");
            uri.addParameter("customerAppId",appkey);
            uri.addParameter("sign",appToken);
            response= HttpUtil.wmsGet(uri.toString());
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用WMS服务异常");
        }
        if (StringUtils.isEmpty(response)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用WMS服务异常");
        }
        JSONObject jsonObject=JSONObject.parseObject(response);
        if (!jsonObject.getBoolean("success")){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),jsonObject.getString("msg"));
        }
        for (int i = 0; i < jsonObject.getJSONArray("data").size(); i++) {
            JSONObject data=jsonObject.getJSONArray("data").getJSONObject(i);
            WarehouseList listDO=new WarehouseList();
            listDO.setStatus(StatusEnums.ACTIVATE.getStatus());
            listDO.setWarehouseCode(data.getString("warehouseCode"));
            listDO.setWarehouseName(data.getString("warehouseName"));
            listDO.setCountryCode("CN");
            listDO.setFirmId(firmId);
            list.add(listDO);
        }
        return list;
    }

    @Override
    public List<TranLogisticsDTO> getLogisticsList(String appkey, String appToken, String warehouseCode) {
        List<TranLogisticsDTO> tranLogisticsDTOList = new ArrayList<>();
        String response = null;
        try {
            URIBuilder uri=new URIBuilder(this.wmsUrl+"/logistics/centre/selectLogisticsMethod");
            uri.addParameter("page","1");
            uri.addParameter("row","500");
            uri.addParameter("customerAppId",appkey);
            uri.addParameter("sign",appToken);
            uri.addParameter("warehouse",warehouseCode == null?"":warehouseCode);
            response= HttpUtil.wmsGet(uri.toString());
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用WMS服务异常");
        }
        if (StringUtils.isEmpty(response)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用WMS服务异常");
        }
        JSONObject jsonObject=JSONObject.parseObject(response);
        if (!jsonObject.getBoolean("success")){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),jsonObject.getString("msg"));
        }
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject("pageInfo").getJSONArray("list");
        if(CollectionUtils.isNotEmpty(jsonArray)) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject data = jsonArray.getJSONObject(i);
                //            if(StringUtils.isEmpty(data.getString("code"))) continue;
                TranLogisticsDTO tranLogisticsDTO = new TranLogisticsDTO();
                tranLogisticsDTO.setSpName(data.getString("providerShortened") == null ? "" : data.getString("providerShortened"));
                tranLogisticsDTO.setSpCode(data.getString("providerCode") == null ? "" : data.getString("providerCode"));
                tranLogisticsDTO.setCode(data.getString("methodCode") == null ? "" : data.getString("methodCode"));
                tranLogisticsDTO.setName(data.getString("methodCnName") == null ? "" : data.getString("methodCnName"));
                tranLogisticsDTOList.add(tranLogisticsDTO);
            }
        }
        return tranLogisticsDTOList;
    }

    @Override
    public List<TranLogisticsCostDTO> getFreight(String appKey,String appToken,WQueryDeliveryFeeDTO wQueryDeliveryFeeDTO) {
        List<TranLogisticsCostDTO> tranLogisticsCostDTOList = new ArrayList<>();
        if(StringUtils.isEmpty(wQueryDeliveryFeeDTO.getPlatform())){
            wQueryDeliveryFeeDTO.setPlatform("OTHER");
        }else if("1".equals(wQueryDeliveryFeeDTO.getPlatform())){
            wQueryDeliveryFeeDTO.setPlatform("EBAY");
        }else if("2".equals(wQueryDeliveryFeeDTO.getPlatform())){
            wQueryDeliveryFeeDTO.setPlatform("AMAZON");
        }else if("3".equals(wQueryDeliveryFeeDTO.getPlatform())){
            wQueryDeliveryFeeDTO.setPlatform("WISH");
        }else if("4".equals(wQueryDeliveryFeeDTO.getPlatform())){
            wQueryDeliveryFeeDTO.setPlatform("ALIEXPRESS");
        }
        String response = null;
        try {
            URIBuilder uri=new URIBuilder(wmsUrl+"/logistics/centre/freight");
            uri.addParameter("customerAppId", appKey);
            uri.addParameter("sign", appToken);
            response = HttpUtil.wmsPost(uri.toString(),wQueryDeliveryFeeDTO);
        }catch (Exception e){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用WMS服务异常");
        }
        if (StringUtils.isEmpty(response)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"调用WMS服务异常");
        }
        JSONObject jsonObject=JSONObject.parseObject(response);
        if (!jsonObject.getBoolean("success")){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),jsonObject.getString("msg"));
        }
        for (int i = 0; i < jsonObject.getJSONArray("data").size(); i++) {
            JSONObject data = jsonObject.getJSONArray("data").getJSONObject(i);
            TranLogisticsCostDTO tranLogisticsDTO = new TranLogisticsCostDTO();
            tranLogisticsDTO.setTotal(data.getBigDecimal("allFee"));
            tranLogisticsDTO.setSmDeliveryTimeMax(data.getInteger("minDay"));
            tranLogisticsDTO.setSmDeliveryTimeMin(data.getInteger("maxDay"));
            tranLogisticsDTO.setSmCode(data.getString("methodCode"));
            tranLogisticsDTO.setSmName(data.getString("methodCnName"));
            tranLogisticsDTO.setSmNameEn(data.getString("methodEnName"));
            tranLogisticsCostDTOList.add(tranLogisticsDTO);
        }
        return tranLogisticsCostDTOList;
    }


}
