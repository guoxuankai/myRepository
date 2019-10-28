package com.rondaful.cloud.supplier.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.*;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.FreightTrialDTO;
import com.rondaful.cloud.supplier.dto.LogisticsDTO;
import com.rondaful.cloud.supplier.entity.CostDetail;
import com.rondaful.cloud.supplier.entity.ErpFreightTrial;
import com.rondaful.cloud.supplier.entity.FreightTrial;
import com.rondaful.cloud.supplier.entity.GranaryFreightTrial;
import com.rondaful.cloud.supplier.entity.Logistics.LogisticsMapping;
import com.rondaful.cloud.supplier.mapper.LogisticsInfoMapper;
import com.rondaful.cloud.supplier.mapper.ThirdLogisticsMapper;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.WmsFreightDTO;
import com.rondaful.cloud.supplier.model.enums.ResponseErrorCode;
import com.rondaful.cloud.supplier.model.request.third.SkuDetail;
import com.rondaful.cloud.supplier.model.request.third.ThirdFreightReq;
import com.rondaful.cloud.supplier.remote.*;
import com.rondaful.cloud.supplier.service.IFreightService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import com.rondaful.cloud.supplier.utils.FreightUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Service
public class FreightServiceImpl implements IFreightService {

    private final Logger logger = LoggerFactory.getLogger(FreightServiceImpl.class);


    @Autowired
    private IWarehouseBasicsService warehouseBasicsService;

    @Autowired
    private ThirdLogisticsMapper thirdLogisticsMapper;

    @Autowired
    private RemoteGranaryService remoteGranaryService;

    @Autowired
    private RemoteOrderService remoteOrderService;

    @Autowired
    private RemoteCommodityService remoteCommodityService;

    @Autowired
    private LogisticsInfoMapper logisticsInfoMapper;

    @Autowired
    private FreightUtil freightUtil;

    @Autowired
    private RemoteSellerService remoteSellerService;

    @Autowired
    private RemoteWmsService remoteWmsService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Value("${oversea.warehouse}")
    private String overseaWarehouse;

    @Value("${granary.app_key}")
    private String default_app_key;

    @Value("${granary.app_token}")
    private String default_app_token;


    @Override
    public List<LogisticsDetailVo> getSuitLogisticsByType(SearchLogisticsListDTO param) {
        JSONObject jsonObject = JSONObject.parseObject(remoteOrderService.getRate("CNY","USD")); //获取汇率
        if(StringUtils.isEmpty(jsonObject.getString("data"))) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"获取不了汇率");
        BigDecimal rate = new BigDecimal(jsonObject.getString("data"));
        Integer shopId = null;
        if(null != param.getStoreId() && "1".equals(param.getPlatformType())) {
            shopId = this.isBrandslinkShop(param.getStoreId());
        }
        List<LogisticsDetailVo> result = new ArrayList<>();
        WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(Integer.parseInt(param.getWarehouseId()));
        if(StringUtils.isEmpty(warehouseDTO.getWarehouseCode())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"找不到该仓库相关信息");
        List skuList = new ArrayList();
        List<String> overseaWarehouseList = Arrays.asList(overseaWarehouse.split(","));
        for(SearchLogisticsListSku skuParam:param.getSearchLogisticsListSkuList()){
            Map<String,Object> map = new HashMap<String,Object>();
            String sku = skuParam.getSku();
            if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
                sku = sku + ":"+ skuParam.getSkuNumber();
                skuList.add(sku);
            }else if("RONDAFUL".equals(warehouseDTO.getFirmCode())){
                JSONObject dataJSON = JSONObject.parseObject(JSONObject.toJSONString(remoteCommodityService.getBySku(sku,null,null))).getJSONObject("data");
                if(null == dataJSON) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"sku错误");
                sku = dataJSON.getString("supplierSku");
                if(StringUtils.isEmpty(sku)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"sku错误");
                map.put("sku",sku);
                map.put("num",skuParam.getSkuNumber());
                skuList.add(map);
            }else if("GOODCANG".equals(warehouseDTO.getFirmCode())){
                sku = sku + ":"+ skuParam.getSkuNumber();
                skuList.add(sku);
            }else if("WMS".equals(warehouseDTO.getFirmCode())){
                map.put("sku",sku);
                map.put("quantity",skuParam.getSkuNumber());
                skuList.add(map);
            }
        }
        if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
            Map<String,Object> gcMap  = freightUtil.getGcFreightTrial(new FreightTrial(warehouseDTO.getWarehouseCode().split("_")[1],param.getCountryCode(),
                    param.getPostCode(),default_app_key,default_app_token,skuList));
            List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>)gcMap.get("data");
            if(CollectionUtils.isEmpty(granaryFreightTrialList)) throw  new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"谷仓无数据");
            granaryFreightTrialList = sortGranaryData(granaryFreightTrialList,param.getSearchType());
            for(GranaryFreightTrial granaryFreightTrial :granaryFreightTrialList){
                LogisticsDetailVo logisticsDetailVo = new LogisticsDetailVo();
                LogisticsMapping logisticsMapping = logisticsInfoMapper.selectLogisticsMapping(granaryFreightTrial.getSm_code(),null,Integer.parseInt(param.getWarehouseId()));
                LogisticsDTO erpLogistics;
                if(null != logisticsMapping && StringUtils.isNotEmpty(logisticsMapping.getErpLogisticsCode())){
                    erpLogistics = logisticsInfoMapper.getLogisticsByCode(Integer.parseInt(param.getWarehouseId()),logisticsMapping.getErpLogisticsCode());
                    if(null == erpLogistics) continue;
                    logisticsDetailVo.setLogisticsName(logisticsMapping.getErpLogisticsName() == null?"":logisticsMapping.getErpLogisticsName());
                    logisticsDetailVo.setLogisticsCode(logisticsMapping.getErpLogisticsCode() == null?"":logisticsMapping.getErpLogisticsCode());
                    String logisticsNameEn = Utils.i18n(logisticsMapping.getErpLogisticsName());
                    if(erpLogistics.getShortName().equals(logisticsNameEn)){
                        logisticsNameEn = Utils.translation(logisticsMapping.getErpLogisticsName());
                    }
                    logisticsDetailVo.setLogisticsNameEn(logisticsNameEn);
                }else{
                    continue;
                }
                if( 1 == param.getHandOrder()) {
                    if("edis".equalsIgnoreCase(erpLogistics.getOnlineLogistics())) continue;
                    if("aliexpress".equalsIgnoreCase(erpLogistics.getOnlineLogistics())) continue;
                }
                if( null == shopId || 0 == shopId){
                    if("edis".equalsIgnoreCase(erpLogistics.getOnlineLogistics())) continue;
                }
                if(!isAvailable(erpLogistics,param.getPlatformType())) continue;
                logisticsDetailVo.setOnlineLogistics(erpLogistics.getOnlineLogistics() == null ?"":erpLogistics.getOnlineLogistics());
                logisticsDetailVo.setAliExpressCode(erpLogistics.getAliexpressCode()==null?"":erpLogistics.getAliexpressCode());
                logisticsDetailVo.setAmazonCarrier(erpLogistics.getAmazonCarrier()==null?"":erpLogistics.getAmazonCarrier());
                logisticsDetailVo.setAmazonCode(erpLogistics.getAmazonCode()==null?"":erpLogistics.getAmazonCode());
                logisticsDetailVo.setEbayCarrier(erpLogistics.getEbayCarrier()==null?"":erpLogistics.getEbayCarrier());
                logisticsDetailVo.setCarrierCode(erpLogistics.getCarrierCode()==null?"":erpLogistics.getCarrierCode());
                logisticsDetailVo.setCarrierName(erpLogistics.getCarrierName()==null?"":erpLogistics.getCarrierName());

                BigDecimal totalCost = granaryFreightTrial.getTotal() == null?new BigDecimal(0.00):granaryFreightTrial.getTotal();
                if(!"USD".equals(gcMap.get("currency"))){
                    totalCost.multiply(rate);
                }
                logisticsDetailVo.setFreightCost(totalCost);
                logisticsDetailVo.setMaxDeliveryTime(granaryFreightTrial.getSm_delivery_time_max());
                logisticsDetailVo.setMinDeliveryTime(granaryFreightTrial.getSm_delivery_time_min());

                if(null != shopId && 3 == shopId){
                    logisticsDetailVo.setFreightCost(new BigDecimal(0));
                }
                result.add(logisticsDetailVo);
                if(result.size() == 30) return  result;
            }
        }else if("RONDAFUL".equals(warehouseDTO.getFirmCode())){
            FreightTrial freightTrial = new FreightTrial(warehouseDTO.getWarehouseCode(),param.getCountryCode(),param.getPlatformType(),skuList);
            freightTrial.setCity(param.getCity());
            freightTrial.setPostCode(param.getPostCode());
            List<ErpFreightTrial> erpFreightTrialList = freightUtil.getErpFreightTrial(freightTrial);
            if(CollectionUtils.isEmpty(erpFreightTrialList)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"erp无数据");
            erpFreightTrialList = sortErpData(erpFreightTrialList,param.getSearchType());
            for(ErpFreightTrial erpFreightTrial :erpFreightTrialList) {
                LogisticsDetailVo logisticsDetailVo = new LogisticsDetailVo();
                LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(Integer.valueOf(param.getWarehouseId()), erpFreightTrial.getShipping_code());
                if (null == logisticsDTO || !isAvailable(logisticsDTO,param.getPlatformType())) continue;
                if(1 == param.getHandOrder()) {
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                    if("aliexpress".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }
                if( null == shopId || 0 == shopId){
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }
                logisticsDetailVo.setAliExpressCode(logisticsDTO.getAliexpressCode() == null ? "" : logisticsDTO.getAliexpressCode());
                logisticsDetailVo.setAmazonCarrier(logisticsDTO.getAmazonCarrier() == null ? "" : logisticsDTO.getAmazonCarrier());
                logisticsDetailVo.setOnlineLogistics(logisticsDTO.getOnlineLogistics() == null ?"":logisticsDTO.getOnlineLogistics());
                logisticsDetailVo.setAmazonCode(logisticsDTO.getAmazonCode() == null ? "" : logisticsDTO.getAmazonCode());
                logisticsDetailVo.setEbayCarrier(logisticsDTO.getEbayCarrier() == null ? "" : logisticsDTO.getEbayCarrier());
                logisticsDetailVo.setCarrierCode(logisticsDTO.getCarrierCode() == null ? "" : logisticsDTO.getCarrierCode());
                logisticsDetailVo.setCarrierName(logisticsDTO.getCarrierName() == null ? "" : logisticsDTO.getCarrierName());
                logisticsDetailVo.setLogisticsCode(erpFreightTrial.getShipping_code());
                String logisticsNameEn = Utils.i18n(erpFreightTrial.getShipping_name());
                if(erpFreightTrial.getShipping_name().equals(logisticsNameEn)){
                    logisticsNameEn = Utils.translation(erpFreightTrial.getShipping_name());
                }
                logisticsDetailVo.setLogisticsNameEn(logisticsNameEn);
                logisticsDetailVo.setLogisticsName(erpFreightTrial.getShipping_name());
                logisticsDetailVo.setMaxDeliveryTime(erpFreightTrial.getLatest_days());
                logisticsDetailVo.setMinDeliveryTime(erpFreightTrial.getEarliest_days());
                logisticsDetailVo.setFreightCost(erpFreightTrial.getCny_amount() == null ? new BigDecimal(0.00) :erpFreightTrial.getCny_amount().multiply(rate));
                if(null != shopId && 3 == shopId){
                    logisticsDetailVo.setFreightCost(new BigDecimal(0));
                }
                result.add(logisticsDetailVo);
                if (result.size() == 30) return result;
            }
        }else if("GOODCANG".equals(warehouseDTO.getFirmCode())){
            Map<String,Object> gcMap  = freightUtil.getGcFreightTrial(new FreightTrial(warehouseDTO.getWarehouseCode(),param.getCountryCode()
                    ,param.getPostCode(),warehouseDTO.getAppKey(),warehouseDTO.getAppToken(),skuList));
            List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>)gcMap.get("data");
            if(CollectionUtils.isEmpty(granaryFreightTrialList)) throw  new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"谷仓无数据");
            granaryFreightTrialList = sortGranaryData(granaryFreightTrialList,param.getSearchType());
            for(GranaryFreightTrial granaryFreightTrial :granaryFreightTrialList){
                LogisticsDetailVo logisticsDetailVo = new LogisticsDetailVo();
                LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(Integer.valueOf(param.getWarehouseId()),granaryFreightTrial.getSm_code());
                if(null == logisticsDTO || !isAvailable(logisticsDTO,param.getPlatformType())) continue;
                if(0 == param.getHandOrder()) {
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                    if("aliexpress".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }
                if( null == shopId || 0 == shopId){
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }
                logisticsDetailVo.setAliExpressCode(logisticsDTO.getAliexpressCode()==null?"":logisticsDTO.getAliexpressCode());
                logisticsDetailVo.setAmazonCarrier(logisticsDTO.getAmazonCarrier()==null?"":logisticsDTO.getAmazonCarrier());
                logisticsDetailVo.setOnlineLogistics(logisticsDTO.getOnlineLogistics() == null ?"":logisticsDTO.getOnlineLogistics());
                logisticsDetailVo.setAmazonCode(logisticsDTO.getAmazonCode()==null?"":logisticsDTO.getAmazonCode());
                logisticsDetailVo.setEbayCarrier(logisticsDTO.getEbayCarrier()==null?"":logisticsDTO.getEbayCarrier());
                logisticsDetailVo.setCarrierCode(logisticsDTO.getCarrierCode()==null?"":logisticsDTO.getCarrierCode());
                logisticsDetailVo.setCarrierName(logisticsDTO.getCarrierName()==null?"":logisticsDTO.getCarrierName());
                logisticsDetailVo.setLogisticsName(logisticsDTO.getShortName());
                BigDecimal totalCost = granaryFreightTrial.getTotal() == null?new BigDecimal(0.00):granaryFreightTrial.getTotal();
                String logisticsNameEn = Utils.i18n(logisticsDTO.getShortName());
                if(logisticsDTO.getShortName().equals(logisticsNameEn)){
                    logisticsNameEn = Utils.translation(logisticsDTO.getShortName());
                }
                logisticsDetailVo.setLogisticsNameEn(logisticsNameEn);
                if(!"USD".equals(gcMap.get("currency"))){
                    totalCost.multiply(rate);
                }
                logisticsDetailVo.setFreightCost(totalCost);
                if (null != shopId && 3 == shopId) {
                    logisticsDetailVo.setFreightCost(new BigDecimal(0));
                }
                logisticsDetailVo.setMaxDeliveryTime(granaryFreightTrial.getSm_delivery_time_max());
                logisticsDetailVo.setMinDeliveryTime(granaryFreightTrial.getSm_delivery_time_min());
                logisticsDetailVo.setLogisticsCode(granaryFreightTrial.getSm_code());
                result.add(logisticsDetailVo);
                if(result.size() == 30) return  result;
            }
        }else if("WMS".equals(warehouseDTO.getFirmCode())){
            FreightTrial freightTrial = new FreightTrial(warehouseDTO.getWarehouseCode(),param.getCountryCode()
                    ,param.getPostCode(),warehouseDTO.getAppKey(),warehouseDTO.getAppToken(),skuList);
            freightTrial.setPlatformType(param.getPlatformType());
            freightTrial.setCity(param.getCity());
            String wmsDataStr = remoteWmsService.wmsFreight(freightTrial);
            List<WmsFreightDTO> wmsFreightDTOS = JSONObject.parseArray(wmsDataStr,WmsFreightDTO.class);
            wmsFreightDTOS = sorWmsData(wmsFreightDTOS,param.getSearchType());
            for(WmsFreightDTO wmsFreightDTO :wmsFreightDTOS){
                LogisticsDetailVo logisticsDetailVo = new LogisticsDetailVo();
                LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(Integer.valueOf(param.getWarehouseId()),wmsFreightDTO.getMethodCode());
                if(null == logisticsDTO || !isAvailable(logisticsDTO,param.getPlatformType())) continue;
                if(1 == param.getHandOrder()) {
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                    if("aliexpress".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }
                if( null == shopId || 0 == shopId){
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }
                logisticsDetailVo.setAliExpressCode(logisticsDTO.getAliexpressCode()==null?"":logisticsDTO.getAliexpressCode());
                logisticsDetailVo.setAmazonCarrier(logisticsDTO.getAmazonCarrier()==null?"":logisticsDTO.getAmazonCarrier());
                logisticsDetailVo.setOnlineLogistics(logisticsDTO.getOnlineLogistics() == null ?"":logisticsDTO.getOnlineLogistics());
                logisticsDetailVo.setAmazonCode(logisticsDTO.getAmazonCode()==null?"":logisticsDTO.getAmazonCode());
                logisticsDetailVo.setEbayCarrier(logisticsDTO.getEbayCarrier()==null?"":logisticsDTO.getEbayCarrier());
                logisticsDetailVo.setCarrierCode(logisticsDTO.getCarrierCode()==null?"":logisticsDTO.getCarrierCode());
                logisticsDetailVo.setCarrierName(logisticsDTO.getCarrierName()==null?"":logisticsDTO.getCarrierName());
                logisticsDetailVo.setLogisticsName(logisticsDTO.getShortName());
                String logisticsNameEn = Utils.i18n(logisticsDTO.getShortName());
                if(logisticsDTO.getShortName().equals(logisticsNameEn)){
                    logisticsNameEn = Utils.translation(logisticsDTO.getShortName());
                }
                BigDecimal totalCost = wmsFreightDTO.getAllFee() == null?new BigDecimal(0.00):wmsFreightDTO.getAllFee().multiply(rate);
                logisticsDetailVo.setLogisticsNameEn(logisticsNameEn);
                logisticsDetailVo.setFreightCost(totalCost);
                if (null != shopId && 3 == shopId) {
                    logisticsDetailVo.setFreightCost(new BigDecimal(0));
                }
                Map<String,Object> map =(Map<String,Object>)wmsFreightDTO.getPromiseDays();
                logisticsDetailVo.setMaxDeliveryTime((Integer)map.get("max"));
                logisticsDetailVo.setMinDeliveryTime((Integer)map.get("min"));
                logisticsDetailVo.setLogisticsCode(wmsFreightDTO.getMethodCode());
                result.add(logisticsDetailVo);
                if(result.size() == 30) return  result;
            }
        }

        return result;
    }

    @Override
    public FreightTrialDTO getFreightTrialByType(FreightTrial freightTrial) {
        JSONObject jsonObject = JSONObject.parseObject(remoteOrderService.getRate("CNY","USD")); //获取汇率
        if(StringUtils.isEmpty(jsonObject.getString("data"))) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"获取不了汇率");
        BigDecimal rate = new BigDecimal(jsonObject.getString("data"));
        FreightTrialDTO freightTrialDTO = new FreightTrialDTO();
        WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(freightTrial.getWarehouseId()); //获取仓库信息
        if(StringUtils.isEmpty(warehouseDTO.getWarehouseCode())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"找不到该仓库相关信息");
        List skuList = new ArrayList();
        List<String> overseaWarehouseList = Arrays.asList(overseaWarehouse.split(","));
        for(Map<String,Object> skuMap:freightTrial.getSkuList()){
            String sku = skuMap.get("sku").toString();
            String num = skuMap.get("num").toString();
            if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
                sku = sku + ":"+ num;
                skuList.add(sku);
                freightTrial.setAppKey(default_app_key);
                freightTrial.setAppToken(default_app_token);
            }else if("RONDAFUL".equals(warehouseDTO.getFirmCode())){
                Map<String,Object> supplierSkuMap = new HashMap<>();
                JSONObject dataJSON = JSONObject.parseObject(JSONObject.toJSONString(remoteCommodityService.getBySku(sku,null,null))).getJSONObject("data");
                if(null == dataJSON) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"sku错误");
                sku = dataJSON.getString("supplierSku");
                if(StringUtils.isEmpty(sku)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"sku错误");
                supplierSkuMap.put("sku",sku);
                supplierSkuMap.put("num",num);
                skuList.add(supplierSkuMap);
            }else if("GOODCANG".equals(warehouseDTO.getFirmCode())){
                sku = sku + ":" + num;
                skuList.add(sku);
                freightTrial.setAppKey(warehouseDTO.getAppKey());
                freightTrial.setAppToken(warehouseDTO.getAppToken());
            }else if("WMS".equals(warehouseDTO.getFirmCode())){
                Map<String,Object> map = new HashMap<>();
                map.put("sku",sku);
                map.put("quantity",num);
                skuList.add(map);
            }
        }
        freightTrial.setList(skuList);
        freightTrial.setWarehouseCode(warehouseDTO.getWarehouseCode());
        if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
            FeignResult result = remoteUserService.getArea(null,freightTrial.getCountryCode());
            if(!result.getSuccess() ||  null == result.getData()){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
            }
            String postCode = JSONObject.parseObject(JSONObject.toJSONString(result.getData())).getString("postCode");
            if(StringUtils.isEmpty(postCode)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
            }
            Map<String, Object>  gcMap = freightUtil.getGcFreightTrial(new FreightTrial(warehouseDTO.getWarehouseCode().split("_")[1], freightTrial.getCountryCode(),postCode,
                    default_app_key, default_app_token,skuList));
            List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>)gcMap.get("data");
            granaryFreightTrialList = sortGranaryData(granaryFreightTrialList,freightTrial.getSearchType());
            WarehouseDTO warehouse = warehouseBasicsService.getByAppTokenAndCode(default_app_token,warehouseDTO.getWarehouseCode().split("_")[1]);
            if(null == warehouse || null == warehouse.getWarehouseId()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取仓库id失败");
            for(GranaryFreightTrial granaryFreightTrial :granaryFreightTrialList){
                LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(warehouse.getWarehouseId(), granaryFreightTrial.getSm_code());
                if (null == logisticsDTO || !isAvailable(logisticsDTO,freightTrial.getPlatformType())) continue;
                if("1".equals(freightTrial.getPlatformType())){
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                    if("aliexpress".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }else if("4".equals(freightTrial.getPlatformType())){
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                    if("aliexpress".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }
                LogisticsMapping logisticsMapping = logisticsInfoMapper.selectLogisticsMapping(granaryFreightTrial.getSm_code(),null,freightTrial.getWarehouseId());
                if(StringUtils.isNotEmpty(logisticsMapping.getErpLogisticsCode())){
                    freightTrialDTO.setLogisticsName(logisticsMapping.getErpLogisticsName() == null?"":logisticsMapping.getErpLogisticsName());
                    freightTrialDTO.setLogisticsCode(logisticsMapping.getErpLogisticsCode() == null?"":logisticsMapping.getErpLogisticsCode());
                    String logisticsNameEn = Utils.i18n(logisticsMapping.getErpLogisticsName());
                    if(logisticsDTO.getShortName().equals(logisticsNameEn)){
                        logisticsNameEn = Utils.translation(logisticsMapping.getErpLogisticsName());
                    }
                    freightTrialDTO.setLogisticsNameEn(logisticsNameEn);
                }else{
                    continue;
                }
                freightTrialDTO.setLogisticsCode(granaryFreightTrial.getSm_code());
                String logisticsNameEn = Utils.i18n(granaryFreightTrial.getSm_name());
                if(logisticsDTO.getShortName().equals(logisticsNameEn)){
                    logisticsNameEn = Utils.translation(logisticsDTO.getShortName());
                }
                freightTrialDTO.setLogisticsNameEn(logisticsNameEn);
                freightTrialDTO.setMaxDeliveryTime(granaryFreightTrial.getSm_delivery_time_max());
                freightTrialDTO.setMinDeliveryTime(granaryFreightTrial.getSm_delivery_time_min());
                Map<String,Object> skuCost = new HashMap<>();
                freightTrial.setLogisticsCode(granaryFreightTrial.getSm_code());
                freightTrial.setPostCode(postCode);
                freightTrial.setWarehouseCode(freightTrial.getWarehouseCode().split("_")[1]);
                for(Map<String,Object> skuMap:freightTrial.getSkuList()){
                    String sku = skuMap.get("sku") + ":"+skuMap.get("num");
                    freightTrial.setList(Arrays.asList(sku));
                    List<GranaryFreightTrial> granaryFreightTrialOne = freightUtil.getGranaryFreightTrial(freightTrial);
                    BigDecimal totalCost = granaryFreightTrialOne.get(0).getTotal();
                    if(!"USD".equals(gcMap.get("currency"))){
                        totalCost.multiply(rate);
                    }
                    skuCost.put( skuMap.get("sku").toString(),totalCost);

                }
                freightTrialDTO.setSkuCost(skuCost);
                if (null != freightTrialDTO) return freightTrialDTO;
            }
        }else if("RONDAFUL".equals(warehouseDTO.getFirmCode())){
            List<ErpFreightTrial> erpFreightTrialList = freightUtil.getErpFreightTrial(freightTrial);
            if(CollectionUtils.isEmpty(erpFreightTrialList)) return  freightTrialDTO;
            erpFreightTrialList = sortErpData(erpFreightTrialList,freightTrial.getSearchType());
            for(ErpFreightTrial erpFreightTrial :erpFreightTrialList) {
                LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(Integer.valueOf(freightTrial.getWarehouseId()), erpFreightTrial.getShipping_code());
                if (null == logisticsDTO || !isAvailable(logisticsDTO,freightTrial.getPlatformType())) continue;
                freightTrialDTO.setLogisticsCode(erpFreightTrial.getShipping_code());
                String logisticsNameEn = Utils.i18n(logisticsDTO.getShortName());
                if(logisticsDTO.getShortName().equals(logisticsNameEn)){
                    logisticsNameEn = Utils.translation(logisticsDTO.getShortName());
                }
                freightTrialDTO.setLogisticsNameEn(logisticsNameEn);
                freightTrialDTO.setLogisticsName(erpFreightTrial.getShipping_name());
                freightTrialDTO.setMaxDeliveryTime(erpFreightTrial.getLatest_days());
                freightTrialDTO.setMinDeliveryTime(erpFreightTrial.getEarliest_days());
                Map<String,Object> skuCost = new HashMap<>();
                freightTrial.setLogisticsCode(erpFreightTrial.getShipping_code());
                for(Map<String,Object> skuMap:freightTrial.getSkuList()){
                    Map<String,Object> supplierSkuOne = new HashMap<>();
                    String sku = skuMap.get("sku").toString();
                    sku = JSONObject.parseObject(JSONObject.toJSONString(remoteCommodityService.getBySku(sku,null,null))).getJSONObject("data").getString("supplierSku");
                    if(StringUtils.isEmpty(sku)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"sku错误");
                    supplierSkuOne.put("sku",sku);
                    supplierSkuOne.put("num",skuMap.get("num"));
                    freightTrial.setList(Arrays.asList(supplierSkuOne));
                    List<ErpFreightTrial> erpFreightTrialOne = freightUtil.getErpFreightTrial(freightTrial);
                    if("1".equals(freightTrial.getPlatformType())){
                        if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                        if("aliexpress".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                    }else if("4".equals(freightTrial.getPlatformType())){
                        if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                        if("aliexpress".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                    }
                    skuCost.put(skuMap.get("sku").toString(),erpFreightTrialOne.get(0).getCny_amount() == null ? new BigDecimal(0.00):erpFreightTrialOne.get(0).getCny_amount().multiply(rate));
                }
                freightTrialDTO.setSkuCost(skuCost);
                if (null != freightTrialDTO) return freightTrialDTO;
            }
        }else if("GOODCANG".equals(warehouseDTO.getFirmCode())){
            FeignResult result = remoteUserService.getArea(null,freightTrial.getCountryCode());
            if(!result.getSuccess() ||  null == result.getData()){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
            }
            String postCode = JSONObject.parseObject(JSONObject.toJSONString(result.getData())).getString("postCode");
            if(StringUtils.isEmpty(postCode)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
            }
            Map<String, Object>  gcMap = freightUtil.getGcFreightTrial(new FreightTrial(warehouseDTO.getWarehouseCode(), freightTrial.getCountryCode(),postCode,
                    warehouseDTO.getAppKey(),warehouseDTO.getAppToken(), skuList));
            List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>)gcMap.get("data");
            granaryFreightTrialList = sortGranaryData(granaryFreightTrialList,freightTrial.getSearchType());
            for(GranaryFreightTrial granaryFreightTrial :granaryFreightTrialList){
                LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(Integer.valueOf(freightTrial.getWarehouseId()), granaryFreightTrial.getSm_code());
                if (null == logisticsDTO || !isAvailable(logisticsDTO,freightTrial.getPlatformType())) continue;
                freightTrialDTO.setLogisticsCode(granaryFreightTrial.getSm_code());
                String logisticsNameEn = Utils.i18n(granaryFreightTrial.getSm_name());
                if(logisticsDTO.getShortName().equals(logisticsNameEn)){
                    logisticsNameEn = Utils.translation(logisticsDTO.getShortName());
                }
                freightTrialDTO.setLogisticsNameEn(logisticsNameEn);
                freightTrialDTO.setMaxDeliveryTime(granaryFreightTrial.getSm_delivery_time_max());
                freightTrialDTO.setMinDeliveryTime(granaryFreightTrial.getSm_delivery_time_min());
                Map<String,Object> skuCost = new HashMap<>();
                freightTrial.setLogisticsCode(granaryFreightTrial.getSm_code());
                for(Map<String,Object> skuMap:freightTrial.getSkuList()){
                    String sku = skuMap.get("sku") + ":"+skuMap.get("num");
                    freightTrial.setList(Arrays.asList(sku));
                    List<GranaryFreightTrial> granaryFreightTrialOne = freightUtil.getGranaryFreightTrial(freightTrial);
                    BigDecimal totalCost = granaryFreightTrialOne.get(0).getTotal();
                    if(!"USD".equals(gcMap.get("currency"))){
                        totalCost.multiply(rate);
                    }
                    skuCost.put( skuMap.get("sku").toString(),totalCost);

                }
                freightTrialDTO.setSkuCost(skuCost);
                if (null != freightTrialDTO) return freightTrialDTO;
            }
        }else if("WMS".equals(warehouseDTO.getFirmCode())){
            freightTrial.setAppKey(warehouseDTO.getAppKey());
            freightTrial.setAppToken(warehouseDTO.getAppToken());
            String dateStr = remoteWmsService.wmsFreight(freightTrial);
            List<WmsFreightDTO> wmsFreightDTOS = JSONObject.parseArray(dateStr,WmsFreightDTO.class);
            wmsFreightDTOS = sorWmsData(wmsFreightDTOS,freightTrial.getSearchType());
            for(WmsFreightDTO wmsFreightDTO :wmsFreightDTOS){
                LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(Integer.valueOf(freightTrial.getWarehouseId()), wmsFreightDTO.getMethodCode());
                if(null == logisticsDTO || !isAvailable(logisticsDTO,freightTrial.getPlatformType())) continue;
                if("1".equals(freightTrial.getPlatformType())){
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                    if("aliexpress".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }else if("4".equals(freightTrial.getPlatformType())){
                    if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                    if("aliexpress".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())) continue;
                }
                freightTrialDTO.setLogisticsCode(wmsFreightDTO.getMethodCode());
                String logisticsNameEn = Utils.i18n(wmsFreightDTO.getMethodCnName());
                if(logisticsDTO.getShortName().equals(logisticsNameEn)){
                    logisticsNameEn = Utils.translation(logisticsDTO.getShortName());
                }
                freightTrialDTO.setLogisticsNameEn(logisticsNameEn);
                freightTrialDTO.setMaxDeliveryTime(wmsFreightDTO.getMaxDay().intValue());
                freightTrialDTO.setMinDeliveryTime(wmsFreightDTO.getMaxDay().intValue());
                Map<String,Object> skuCost = new HashMap<>();
                freightTrial.setLogisticsCode(wmsFreightDTO.getMethodCode());
                for(Map<String,Object> skuMap:freightTrial.getSkuList()){
                    String sku = skuMap.get("sku") + ":"+skuMap.get("num");
                    freightTrial.setList(Arrays.asList(sku));
                    List<GranaryFreightTrial> granaryFreightTrialOne = freightUtil.getGranaryFreightTrial(freightTrial);
                    BigDecimal totalCost = granaryFreightTrialOne.get(0).getTotal().multiply(rate);
                    skuCost.put( skuMap.get("sku").toString(),totalCost);
                }
                freightTrialDTO.setSkuCost(skuCost);
                if (null != freightTrialDTO) return freightTrialDTO;
            }
        }
        return freightTrialDTO;
    }



    @Override
    public LogisticsCostVo queryFreightByLogisticsCode(LogisticsCostVo param) {
        WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(Integer.parseInt(param.getWarehouseId()));  //查仓库
        if(StringUtils.isEmpty(warehouseDTO.getWarehouseCode())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"找不到该仓库相关信息");
        List<String> overseaWarehouseList = Arrays.asList(overseaWarehouse.split(","));
        LogisticsDTO logisticsDTO;
        if(overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){  //判断是否是erp海外仓
            LogisticsMapping mapping = logisticsInfoMapper.selectLogisticsMapping(null,param.getLogisticsCode(),Integer.parseInt(param.getWarehouseId()));
            if(null == mapping || StringUtils.isEmpty(mapping.getGranaryLogisticsCode())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取映射失败");
//            param.setLogisticsCode(mapping.getGranaryLogisticsCode());
//            logisticsDTO  = logisticsInfoMapper.getLogisticsByCode(Integer.parseInt(param.getWarehouseId()), mapping.getGranaryLogisticsCode());
//        }else{
        }
        logisticsDTO = logisticsInfoMapper.getLogisticsByCode(Integer.valueOf(param.getWarehouseId()), param.getLogisticsCode());
        if(null == logisticsDTO ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"查不到该物流信息");
        if(null != logisticsDTO.getOnlineLogistics()){
            if( 1 == param.getHandOrder() && "edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"手工单不能选择该物流方式");
            }
        }
        JSONObject jsonObject = JSONObject.parseObject(remoteOrderService.getRate("CNY", "USD")); //获取汇率
        BigDecimal rate = new BigDecimal(jsonObject.getString("data"));
        FreightTrial freightTrial = new FreightTrial();
        freightTrial.setPostCode(param.getPostCode() == null ?"":param.getPostCode());  //邮编
        freightTrial.setCity(param.getCity() == null ? "":param.getCity());  //城市
        freightTrial.setPlatformType(param.getPlatformType() == null ?"":param.getPlatformType());  //平台
        freightTrial.setCountryCode(param.getCountryCode());  //国家简码
        freightTrial.setLogisticsCode(param.getLogisticsCode() == null ?"":param.getLogisticsCode());  //物流方式code
        freightTrial.setWarehouseCode(warehouseDTO.getWarehouseCode() == null ?"":warehouseDTO.getWarehouseCode());
        freightTrial.setAppToken(warehouseDTO.getAppToken() == null ?"":warehouseDTO.getAppToken());
        freightTrial.setAppKey(warehouseDTO.getAppKey() == null ?"":warehouseDTO.getAppKey());
        boolean isPinlianEdis = false;
        if("edis".equalsIgnoreCase(logisticsDTO.getOnlineLogistics())){
            if(null != param.getStoreId() && 0 != param.getStoreId() && "1".equals(param.getPlatformType()) && 0 == param.getHandOrder()) {
                Integer edisType = this.isBrandslinkShop(param.getStoreId());
                if( 0 == edisType)  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"edis未授权");
                if(3 == edisType) isPinlianEdis = true;
            }
        }
        for (SupplierGroupVo sellers : param.getSellers()) {
            BigDecimal sellersTotalCost = new BigDecimal("0.00");
            List allSkuList = new ArrayList();
            if(isPinlianEdis){
                sellers.setSupplierCost(sellersTotalCost);
                break;
            }
            for (SkuGroupVo sellerSku : sellers.getItems()) {
                BigDecimal oneSellerCost = new BigDecimal("0.00");
                if(isPinlianEdis){
                    sellerSku.setSkuCost(oneSellerCost);
                    break;
                }
                if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
                    freightTrial.setAppToken(default_app_token);
                    freightTrial.setAppKey(default_app_key);
                    freightTrial.setWarehouseCode(warehouseDTO.getWarehouseCode().split("_")[1]);
                    LogisticsMapping logisticsMapping = logisticsInfoMapper.selectLogisticsMapping(null,param.getLogisticsCode(),Integer.parseInt(param.getWarehouseId()));
                    if( null != logisticsMapping && StringUtils.isNotEmpty(logisticsMapping.getGranaryLogisticsCode())){
                        freightTrial.setLogisticsCode(logisticsMapping.getGranaryLogisticsCode());
                    }else{
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"无映射关系");
                    }
                    Map<String, Object>  gcMap = this.queryGranaryFreightDetail(sellerSku.getSku(),sellerSku.getSkuNumber(),allSkuList,freightTrial);
                    List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) gcMap.get("data");
                    for (GranaryFreightTrial granaryFreightTrial : granaryFreightTrialList) {
                        if (null == granaryFreightTrial) {
                            sellerSku.setSkuCost(oneSellerCost);
                            continue;
                        }
                        oneSellerCost = granaryFreightTrial.getTotal() == null ? new BigDecimal(0.00) : granaryFreightTrial.getTotal();
                        if (!"USD".equals(gcMap.get("currency"))) {
                            oneSellerCost = oneSellerCost.multiply(rate);
                        }
                    }
                }else if ("RONDAFUL".equals(warehouseDTO.getFirmCode())) {
                    List<ErpFreightTrial> erpFreightTrials = this.queryErpFreightDetail(sellerSku.getSku(),sellerSku.getSkuNumber(),allSkuList,freightTrial);
                    for (ErpFreightTrial erpFreightTrial : erpFreightTrials) {
                        if (null == erpFreightTrial) {
                            sellerSku.setSkuCost(oneSellerCost);
                            continue;
                        }
                        oneSellerCost = erpFreightTrial.getCny_amount() == null ? new BigDecimal(0.00) : erpFreightTrial.getCny_amount().multiply(rate);
                    }
                } else if ("GOODCANG".equals(warehouseDTO.getFirmCode())) {
                    Map<String, Object>  gcMap= this.queryGranaryFreightDetail(sellerSku.getSku(),sellerSku.getSkuNumber(),allSkuList,freightTrial);
                    List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) gcMap.get("data");
                    for (GranaryFreightTrial granaryFreightTrial : granaryFreightTrialList) {
                        if (null == granaryFreightTrial) {
                            sellerSku.setSkuCost(oneSellerCost);
                            continue;
                        }
                        oneSellerCost = granaryFreightTrial.getTotal() == null ? new BigDecimal(0.00) : granaryFreightTrial.getTotal();
                        if (!"USD".equals(gcMap.get("currency"))) {
                            oneSellerCost = oneSellerCost.multiply(rate);
                        }
                    }
                } else if ("WMS".equals(warehouseDTO.getFirmCode())) {
                    List<WmsFreightDTO> wmsFreightDTOList = this.queryWmsFreightDetail(sellerSku.getSku(),sellerSku.getSkuNumber(),allSkuList,freightTrial);
                    for (WmsFreightDTO wmsFreightDTO : wmsFreightDTOList) {
                        oneSellerCost = wmsFreightDTO.getAllFee() == null ? new BigDecimal(0.00) : wmsFreightDTO.getAllFee().multiply(rate);
                    }
                }
                sellerSku.setSkuCost(oneSellerCost);
            }
            if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
                Map<String, Object>  gcMap= this.queryGranaryFreightDetail(null,null,allSkuList,freightTrial);
                List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) gcMap.get("data");
                for (GranaryFreightTrial granaryFreightTrial : granaryFreightTrialList) {
                    if (null == granaryFreightTrial) {
                        sellers.setSupplierCost(sellersTotalCost);
                        continue;
                    }
                    sellersTotalCost = granaryFreightTrial.getTotal() == null ? new BigDecimal(0.00) : granaryFreightTrial.getTotal();
                    if (!"USD".equals(gcMap.get("currency"))) {
                        sellersTotalCost = sellersTotalCost.multiply(rate);
                    }
                }
            }else if ("RONDAFUL".equals(warehouseDTO.getFirmCode())) {
                List<ErpFreightTrial> erpFreightTrials = this.queryErpFreightDetail(null,null,allSkuList,freightTrial);
                for (ErpFreightTrial erpFreightTrial : erpFreightTrials) {
                    if (null == erpFreightTrial) {
                        sellers.setSupplierCost(sellersTotalCost);
                        continue;
                    }
                    sellersTotalCost = erpFreightTrial.getCny_amount() == null ? new BigDecimal(0.00) : erpFreightTrial.getCny_amount().multiply(rate);
                }
            } else if ("GOODCANG".equals(warehouseDTO.getFirmCode())) {
                Map<String, Object>  gcMap = this.queryGranaryFreightDetail(null,null,allSkuList,freightTrial);
                List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) gcMap.get("data");
                for (GranaryFreightTrial granaryFreightTrial : granaryFreightTrialList) {
                    if (null == granaryFreightTrial) {
                        sellers.setSupplierCost(sellersTotalCost);
                        continue;
                    }
                    sellersTotalCost = granaryFreightTrial.getTotal() == null ? new BigDecimal(0.00) : granaryFreightTrial.getTotal();
                    if (!"USD".equals(gcMap.get("currency"))) {
                        sellersTotalCost = sellersTotalCost.multiply(rate);
                    }
                }
            } else if ("WMS".equals(warehouseDTO.getFirmCode())) {
                List<WmsFreightDTO> wmsFreightDTOList = this.queryWmsFreightDetail(null,null,allSkuList,freightTrial);
                for (WmsFreightDTO wmsFreightDTO : wmsFreightDTOList) {
                    sellersTotalCost = wmsFreightDTO.getAllFee() == null ? new BigDecimal(0.00) : wmsFreightDTO.getAllFee().multiply(rate);
                }
            }
            sellers.setSupplierCost(sellersTotalCost);
        }
        for (SupplierGroupVo suppliers : param.getSupplier()) {
            BigDecimal supplierTotalCost = new BigDecimal(0.00);
            List allSkuList = new ArrayList();
            for (SkuGroupVo supplierSku : suppliers.getItems()) {
                BigDecimal oneSupplierCost = new BigDecimal(0.00);
                if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
                    freightTrial.setAppToken(default_app_token);
                    freightTrial.setAppKey(default_app_key);
                    freightTrial.setWarehouseCode(warehouseDTO.getWarehouseCode().split("_")[1]);
                    LogisticsMapping logisticsMapping = logisticsInfoMapper.selectLogisticsMapping(null,param.getLogisticsCode(),Integer.parseInt(param.getWarehouseId()));
                    if(null != logisticsMapping && StringUtils.isNotEmpty(logisticsMapping.getGranaryLogisticsCode())){
                        freightTrial.setLogisticsCode(logisticsMapping.getGranaryLogisticsCode());
                    }else{
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"无映射关系");
                    }
                    Map<String, Object>  gcMap= this.queryGranaryFreightDetail(supplierSku.getSku(),supplierSku.getSkuNumber(),allSkuList,freightTrial);
                    List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) gcMap.get("data");
                    for (GranaryFreightTrial granaryFreightTrial : granaryFreightTrialList) {
                        if (null == granaryFreightTrial) {
                            supplierSku.setSkuCost(oneSupplierCost);
                            continue;
                        }
                        oneSupplierCost = granaryFreightTrial.getTotal() == null ? new BigDecimal(0.00) : granaryFreightTrial.getTotal();
                        if (!"USD".equals(gcMap.get("currency"))) {
                            oneSupplierCost = oneSupplierCost.multiply(rate);
                        }
                    }
                }else if ("RONDAFUL".equals(warehouseDTO.getFirmCode())) {
                    List<ErpFreightTrial> erpFreightTrials = this.queryErpFreightDetail(supplierSku.getSku(),supplierSku.getSkuNumber(),allSkuList,freightTrial);
                    for (ErpFreightTrial erpFreightTrial : erpFreightTrials) {
                        if (null == erpFreightTrial) {
                            supplierSku.setSkuCost(oneSupplierCost);
                            continue;
                        }
                        oneSupplierCost = erpFreightTrial.getCny_amount() == null ? new BigDecimal(0.00) : erpFreightTrial.getCny_amount().multiply(rate);
                    }
                } else if ("GOODCANG".equals(warehouseDTO.getFirmCode())) {
                    Map<String, Object>  gcMap= this.queryGranaryFreightDetail(supplierSku.getSku(),supplierSku.getSkuNumber(),allSkuList,freightTrial);
                    List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) gcMap.get("data");
                    for (GranaryFreightTrial granaryFreightTrial : granaryFreightTrialList) {
                        if (null == granaryFreightTrial) {
                            supplierSku.setSkuCost(oneSupplierCost);
                            continue;
                        }
                        oneSupplierCost = granaryFreightTrial.getTotal() == null ? new BigDecimal(0.00) : granaryFreightTrial.getTotal();
                        if (!"USD".equals(gcMap.get("currency"))) {
                            oneSupplierCost = oneSupplierCost.multiply(rate);
                        }
                    }
                } else if ("WMS".equals(warehouseDTO.getFirmCode())) {
                    List<WmsFreightDTO> wmsFreightDTOList = this.queryWmsFreightDetail(supplierSku.getSku(),supplierSku.getSkuNumber(),allSkuList,freightTrial);
                    for (WmsFreightDTO wmsFreightDTO : wmsFreightDTOList) {
                        oneSupplierCost = wmsFreightDTO.getAllFee() == null ? new BigDecimal(0.00) : wmsFreightDTO.getAllFee().multiply(rate);
                    }
                }
                supplierSku.setSkuCost(oneSupplierCost);
            }
            if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
                warehouseDTO.setAppToken(default_app_token);
                warehouseDTO.setAppKey(default_app_key);
                warehouseDTO.setWarehouseCode(warehouseDTO.getWarehouseCode().split("_")[1]);
                Map<String, Object>  gcMap = this.queryGranaryFreightDetail(null,null,allSkuList,freightTrial);
                List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) gcMap.get("data");
                for (GranaryFreightTrial granaryFreightTrial : granaryFreightTrialList) {
                    if (null == granaryFreightTrial) {
                        suppliers.setSupplierCost(supplierTotalCost);
                        continue;
                    }
                    supplierTotalCost = granaryFreightTrial.getTotal() == null ? new BigDecimal(0.00) : granaryFreightTrial.getTotal();
                    if (!"USD".equals(gcMap.get("currency"))) {
                        supplierTotalCost = supplierTotalCost.multiply(rate);
                    }
                }
            }else if ("RONDAFUL".equals(warehouseDTO.getFirmCode())) {
                List<ErpFreightTrial> erpFreightTrials = this.queryErpFreightDetail(null,null,allSkuList,freightTrial);
                for (ErpFreightTrial erpFreightTrial : erpFreightTrials) {
                    if (null == erpFreightTrial) {
                        suppliers.setSupplierCost(supplierTotalCost);
                        continue;
                    }
                    supplierTotalCost = erpFreightTrial.getCny_amount() == null ? new BigDecimal(0.00) : erpFreightTrial.getCny_amount().multiply(rate);
                }
            } else if ("GOODCANG".equals(warehouseDTO.getFirmCode())) {
                Map<String, Object>  gcMap= this.queryGranaryFreightDetail(null,null,allSkuList,freightTrial);
                List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) gcMap.get("data");
                for (GranaryFreightTrial granaryFreightTrial : granaryFreightTrialList) {
                    if (null == granaryFreightTrial) {
                        suppliers.setSupplierCost(supplierTotalCost);
                        continue;
                    }
                    supplierTotalCost = granaryFreightTrial.getTotal() == null ? new BigDecimal(0.00) : granaryFreightTrial.getTotal();
                    if (!"USD".equals(gcMap.get("currency"))) {
                        supplierTotalCost = supplierTotalCost.multiply(rate);
                    }
                }
            } else if ("WMS".equals(warehouseDTO.getFirmCode())) {
                List<WmsFreightDTO> wmsFreightDTOList = this.queryWmsFreightDetail(null,null,allSkuList,freightTrial);
                for (WmsFreightDTO wmsFreightDTO : wmsFreightDTOList) {
                    supplierTotalCost = wmsFreightDTO.getAllFee() == null ? new BigDecimal(0.00) : wmsFreightDTO.getAllFee().multiply(rate);
                }
            }
            suppliers.setSupplierCost(supplierTotalCost);
        }
        return param;
    }

    @Override
    public List<FreightTrialDTO> getFreight(ThirdFreightReq param) {
        FreightTrial freightTrial = new FreightTrial();
        List<FreightTrialDTO> result = new ArrayList<FreightTrialDTO>();
        WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(param.getWarehouseId()); //获取仓库信息
        List<String> overseaWarehouseList = Arrays.asList(overseaWarehouse.split(","));
        List list = new ArrayList();
        for (SkuDetail skuDetail : param.getSkuList()) {
            Map<String, Object> skuMap = new HashMap<>();
            String sku = skuDetail.getSku();
            String skuNum = skuDetail.getNum();
            if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
                sku = sku + ":" +skuNum;
                list.add(sku);
            }else if("RONDAFUL".equals(warehouseDTO.getFirmCode())) {
                sku = JSONObject.parseObject(JSONObject.toJSONString(remoteCommodityService.getBySku(sku, null, null))).getJSONObject("data").getString("supplierSku"); //品连sku转成供应商sku
                if (StringUtils.isEmpty(sku)) throw new GlobalException(ResponseErrorCode.GY_RETURN_CODE_200513);
                skuMap.put("sku",sku);
                skuMap.put("num",skuNum);
                list.add(skuMap);
            } else if("GOODCANG".equals(warehouseDTO.getFirmCode())) {
                sku = sku + ":" +skuNum;
                list.add(sku);
            }else if("WMS".equals(warehouseDTO.getFirmCode())){
                skuMap.put("sku",sku);
                skuMap.put("quantity",skuNum);
                list.add(skuMap);

            }
        }
        freightTrial.setAppToken(warehouseDTO.getAppToken() == null ?"":warehouseDTO.getAppToken());
        freightTrial.setAppKey(warehouseDTO.getAppKey() == null ?"":warehouseDTO.getAppKey());
        freightTrial.setPostCode(param.getPostCode() == null ? "" : param.getPostCode());// 邮编
        freightTrial.setWarehouseCode(warehouseDTO.getWarehouseCode()); //仓库code
        freightTrial.setList(list); //sku
        freightTrial.setCity(param.getCity() == null ? "" : param.getCity()); //城市
        freightTrial.setCountryCode(param.getCountryCode()); //国家简码
        freightTrial.setLogisticsCode(param.getLogisticsCode() == null ? "" : param.getLogisticsCode()); //物流方式
        if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
            if(StringUtils.isEmpty(param.getPostCode())){
                FeignResult feignResult = remoteUserService.getArea(null,freightTrial.getCountryCode());
                if(!feignResult.getSuccess() ||  null == feignResult.getData()){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
                }
                String postCode = JSONObject.parseObject(JSONObject.toJSONString(feignResult.getData())).getString("postCode");
                if(StringUtils.isEmpty(postCode)) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
                }
                freightTrial.setPostCode(postCode);
            }
            freightTrial.setAppKey(default_app_key);
            freightTrial.setAppToken(default_app_token);
            WarehouseDTO warehouse = warehouseBasicsService.getByAppTokenAndCode(default_app_token,warehouseDTO.getWarehouseCode().split("_")[1]);
            if(null == warehouse || null == warehouse.getWarehouseId()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取仓库id失败");
            freightTrial.setWarehouseCode(warehouseDTO.getWarehouseCode().split("_")[1]);
            Map<String, Object> map = freightUtil.getGcFreightTrial(freightTrial);
            List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) map.get("data");
            for (int i = 0; i < granaryFreightTrialList.size(); i++) {
                LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(warehouse.getWarehouseId(), granaryFreightTrialList.get(i).getSm_code());
                LogisticsMapping logisticsMapping = logisticsInfoMapper.selectLogisticsMapping(granaryFreightTrialList.get(i).getSm_code(),null,param.getWarehouseId());
                if(null != logisticsMapping && StringUtils.isNotEmpty(logisticsMapping.getErpLogisticsCode())){
                    granaryFreightTrialList.get(i).setSm_code(logisticsMapping.getErpLogisticsCode());
                    granaryFreightTrialList.get(i).setSm_name(logisticsMapping.getErpLogisticsName());
                }else{
                    continue;
                }
                if (null == logisticsDTO) continue;
                if (!isAvailable(logisticsDTO, param.getPlatformType())) {
                    granaryFreightTrialList.remove(i);
                }
            }
            result = transformGranaryData(granaryFreightTrialList, map.get("currency").toString());
        }else if("RONDAFUL".equals(warehouseDTO.getFirmCode())) {
            List<ErpFreightTrial> erpFreightTrialList = freightUtil.getErpFreightTrial(freightTrial);
            if (CollectionUtils.isNotEmpty(erpFreightTrialList)) {
                for (int i = 0; i < erpFreightTrialList.size(); i++) {
                    LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(param.getWarehouseId(), erpFreightTrialList.get(i).getShipping_code());
                    if (null == logisticsDTO) continue;
                    if (!isAvailable(logisticsDTO, param.getPlatformType())) {
                        erpFreightTrialList.remove(i);
                    }
                }
                result = transformErpData(erpFreightTrialList);
            }
        } else if("GOODCANG".equals(warehouseDTO.getFirmCode())){
            if(StringUtils.isEmpty(param.getPostCode())){
                FeignResult feignResult = remoteUserService.getArea(null,freightTrial.getCountryCode());
                if(!feignResult.getSuccess() ||  null == feignResult.getData()){
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
                }
                String postCode = JSONObject.parseObject(JSONObject.toJSONString(feignResult.getData())).getString("postCode");
                if(StringUtils.isEmpty(postCode)) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
                }
                freightTrial.setPostCode(postCode);
            }
            Map<String, Object> map = freightUtil.getGcFreightTrial(freightTrial);
            List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) map.get("data");
            for (int i = 0; i < granaryFreightTrialList.size(); i++) {
                LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(param.getWarehouseId(), granaryFreightTrialList.get(i).getSm_code());
                if (null == logisticsDTO) continue;
                if (!isAvailable(logisticsDTO, param.getPlatformType())) {
                    granaryFreightTrialList.remove(i);
                }
            }
            result = transformGranaryData(granaryFreightTrialList, map.get("currency").toString());
        }else if("WMS".equals(warehouseDTO.getFirmCode())){
            String dataStr = remoteWmsService.wmsFreight(freightTrial);
            List<WmsFreightDTO> wmsFreightDTOS = JSONObject.parseArray(dataStr,WmsFreightDTO.class);
            if (CollectionUtils.isNotEmpty(wmsFreightDTOS)) {
                for (int i = 0; i < wmsFreightDTOS.size(); i++) {
                    LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(param.getWarehouseId(), wmsFreightDTOS.get(i).getMethodCode());
                    if (null == logisticsDTO) continue;
                    if (!isAvailable(logisticsDTO, param.getPlatformType())) {
                        wmsFreightDTOS.remove(i);
                    }
                }
                result = transformWmsData(wmsFreightDTOS);
            }
        }
        return result;
    }


    /**
     * 谷仓返回实体转换
     *
     * @param granaryFreightTrialList
     * @return
     */
    private List<FreightTrialDTO> transformGranaryData(List<GranaryFreightTrial> granaryFreightTrialList, String currenyCode){
        List<FreightTrialDTO> freightTrialDTOList = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(remoteOrderService.getRate("CNY", "USD")); //获取汇率
        BigDecimal rate = new BigDecimal(jsonObject.getString("data"));
        for (GranaryFreightTrial granaryFreightTrial : granaryFreightTrialList) {
            FreightTrialDTO freightTrialDTO = new FreightTrialDTO();
            freightTrialDTO.setLogisticsCode(granaryFreightTrial.getSm_code());//配送方式代码
            freightTrialDTO.setLogisticsName(granaryFreightTrial.getSm_name_cn());//配送方式中文名
            freightTrialDTO.setMaxDeliveryTime(granaryFreightTrial.getSm_delivery_time_max());//最慢时效
            freightTrialDTO.setMinDeliveryTime(granaryFreightTrial.getSm_delivery_time_min());//最快时效
            BigDecimal totalCost = granaryFreightTrial.getTotal();
            if (!"USD".equals(currenyCode)) {
                totalCost = totalCost.multiply(rate).setScale(4, RoundingMode.UP);
            }
            freightTrialDTO.setTotalCost(totalCost);//总费用
            List<CostDetail> costDetails = granaryFreightTrial.getIncome();
            for (CostDetail costDetail : costDetails) {
                BigDecimal costDetailAmount = costDetail.getAmount();
                if (!"USD".equals(currenyCode)) {
                    costDetailAmount = costDetailAmount.multiply(rate).setScale(4, RoundingMode.UP);
                }
                costDetail.setAmount(costDetailAmount);//总费用
            }
            freightTrialDTO.setCostDetail(costDetails);//费用明细
            freightTrialDTO.setCurrency("USD");//费用币种
            freightTrialDTOList.add(freightTrialDTO);
        }
        return freightTrialDTOList;
    }

    /**
     * erp返回实体转换
     *
     * @param list
     * @return
     */
    private List<FreightTrialDTO> transformErpData(List<ErpFreightTrial > list) {
        List<FreightTrialDTO> FreightTrialDTOList = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(remoteOrderService.getRate("CNY", "USD")); //获取汇率
        BigDecimal rate = new BigDecimal(jsonObject.getString("data"));
        for (ErpFreightTrial erpFreightTrial : list) {
            List<CostDetail> costDetailList = new ArrayList<>();
            FreightTrialDTO freightTrialDTO = new FreightTrialDTO();
            freightTrialDTO.setLogisticsName(erpFreightTrial.getShipping_name());//配送方式中文名
            freightTrialDTO.setLogisticsCode(erpFreightTrial.getShipping_code());//配送方式代码
            freightTrialDTO.setTotalCost(erpFreightTrial.getCny_amount() == null ? new BigDecimal(0.00) : erpFreightTrial.getCny_amount().multiply(rate).setScale(4, RoundingMode.UP));//总费用
            freightTrialDTO.setCurrency("USD");//erp运费币种默认RMB
            freightTrialDTO.setMaxDeliveryTime(erpFreightTrial.getLatest_days());//最慢天数
            freightTrialDTO.setMinDeliveryTime(erpFreightTrial.getEarliest_days());//最快天数
            freightTrialDTO.setDiscount(erpFreightTrial.getShipping_fee_discount());//折扣
            if (null != erpFreightTrial.getHandle_fee()) {
                BigDecimal handleFee = new BigDecimal(erpFreightTrial.getHandle_fee());
                if (!"USD".equals(erpFreightTrial.getCurrency_code())) {
                    handleFee = handleFee.multiply(rate).setScale(4, RoundingMode.UP);
                }
                costDetailList.add(new CostDetail("操作费", handleFee));
            }
            if (null != erpFreightTrial.getRegistered_fee()) {
                BigDecimal handleFee = new BigDecimal(erpFreightTrial.getRegistered_fee());
                if (!"USD".equals(erpFreightTrial.getCurrency_code())) {
                    handleFee = handleFee.multiply(rate).setScale(4, RoundingMode.UP);
                }
                costDetailList.add(new CostDetail("挂号费金额", handleFee));
            }
            if (null != erpFreightTrial.getOli_additional_fee()) {
                BigDecimal handleFee = new BigDecimal(erpFreightTrial.getOli_additional_fee());
                if (!"USD".equalsIgnoreCase(erpFreightTrial.getCurrency_code())) {
                    handleFee = handleFee.multiply(rate).setScale(4, RoundingMode.UP);
                }
                costDetailList.add(new CostDetail("燃油附加费", handleFee));
            }
            freightTrialDTO.setCostDetail(costDetailList);
            FreightTrialDTOList.add(freightTrialDTO);
        }
        return FreightTrialDTOList;
    }



    /**
     * @Description
     * @Author  xieyanbin
     * @Param  list wms返回实体转换
     * @Return
     * @Exception
     */
    private List<FreightTrialDTO> transformWmsData(List<WmsFreightDTO> list){
        List<FreightTrialDTO> freightTrialDTOS = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(remoteOrderService.getRate("CNY", "USD")); //获取汇率
        BigDecimal rate = new BigDecimal(jsonObject.getString("data"));
        for(WmsFreightDTO wmsFreightDTO:list){
            FreightTrialDTO freightTrialDTO = new FreightTrialDTO();
            freightTrialDTO.setMaxDeliveryTime(wmsFreightDTO.getMaxDay().intValue());
            freightTrialDTO.setMinDeliveryTime(wmsFreightDTO.getMinDay().intValue());
            freightTrialDTO.setTotalCost(wmsFreightDTO.getAllFee() == null ? new BigDecimal(0.00):wmsFreightDTO.getAllFee().multiply(rate).setScale(4, RoundingMode.UP));
            freightTrialDTO.setLogisticsCode(wmsFreightDTO.getMethodCode());
            freightTrialDTO.setLogisticsName(wmsFreightDTO.getMethodCnName());
            freightTrialDTO.setCurrency("USD");
            freightTrialDTOS.add(freightTrialDTO);
        }
        return freightTrialDTOS;
    }

    /**
     * @Description
     * @Author xieyanbin
     * @Param logisticsCode 物理方式code, warehouseId 仓库id platformType 平台
     * @Return
     * @Exception 判断物流方式是否可用
     *
     */
    private boolean isAvailable (LogisticsDTO logisticsDTO, String platformType){
        boolean flag = true;
        if ("1".equals(platformType)) {//ebay订单，供应商ebay映射和后台ebay映射不能为空
            if (StringUtils.isEmpty(logisticsDTO.getEbayCarrier()) && StringUtils.isEmpty(logisticsDTO.getOtherEbayCarrier())) {
                flag = false;
            }
        } else if ("2".equals(platformType)) {//amazon订单，供应商amazon映射和后台amazon映射不能为空
            if (com.aliyuncs.utils.StringUtils.isEmpty(logisticsDTO.getAmazonCarrier()) || StringUtils.isEmpty(logisticsDTO.getAmazonCode())) {
                if (StringUtils.isEmpty(logisticsDTO.getOtherAmazonCarrier()) || StringUtils.isEmpty(logisticsDTO.getOtherAmazonCode())) {
                    flag = false;
                }
            }

        } else if ("4".equals(platformType)) {//aliexpress订单，供应商aliexpress映射和后台aliexpress映射不能为空
            if (StringUtils.isEmpty(logisticsDTO.getAliexpressCode())) {
                flag = false;
            }
        }
        return flag;
    }

    private Map<String, Object> getGranaryFreightTrial (FreightTrial param) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject json = null;
        try {
            if (StringUtils.isEmpty(param.getPostCode()))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "邮政编码不能为空");
            json = remoteGranaryService.getCalculateDeliveryFee(param, param.getAppKey(), param.getAppToken(), param.getWarehouseCode());
            if ("Failure".equals(json.getString("ask")))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, json.getString("message"));
            List<GranaryFreightTrial> GranaryFreightTrialList = JSONObject.parseArray(json.getString("data"), GranaryFreightTrial.class);
            if (CollectionUtils.isNotEmpty(GranaryFreightTrialList)) {
                result.put("data", GranaryFreightTrialList);
            }
            if (StringUtils.isNotEmpty(json.getString("currency"))) {
                result.put("currency", json.getString("currency"));
            }

        } catch (Exception e) {
            throw e;
        }
        return result;
    }


    public static List<ErpFreightTrial> sortErpData(List<ErpFreightTrial> erpFreightTrials, Integer type){
        List<ErpFreightTrial> result = new ArrayList<>();
        if (1 == type) {
            Collections.sort(erpFreightTrials, new Comparator<ErpFreightTrial>() {
                @Override
                public int compare(ErpFreightTrial o1, ErpFreightTrial o2) {
                    BigDecimal o1Param = o1.getCny_amount().multiply(new BigDecimal(0.99)).add(new BigDecimal(o1.getEarliest_days()).multiply(new BigDecimal(0.01)));
                    BigDecimal o2Param = o2.getCny_amount().multiply(new BigDecimal(0.99)).add(new BigDecimal(o2.getEarliest_days()).multiply(new BigDecimal(0.01)));
                    if (o1Param.compareTo(o2Param) == 0) {
                        return 0;
                    } else if (o1Param.compareTo(o2Param) == 1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        } else if (2 == type) {
            Collections.sort(erpFreightTrials, new Comparator<ErpFreightTrial>() {
                @Override
                public int compare(ErpFreightTrial o1, ErpFreightTrial o2) {
                    BigDecimal o1Param = o1.getCny_amount().multiply(new BigDecimal(0.50)).add(new BigDecimal(o1.getEarliest_days()).multiply(new BigDecimal(0.50)));
                    BigDecimal o2Param = o2.getCny_amount().multiply(new BigDecimal(0.50)).add(new BigDecimal(o2.getEarliest_days()).multiply(new BigDecimal(0.50)));
                    if (o1Param.compareTo(o2Param) == 0) {
                        return 0;
                    } else if (o1Param.compareTo(o2Param) == 1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        } else if (3 == type) {
            Collections.sort(erpFreightTrials, new Comparator<ErpFreightTrial>() {
                @Override
                public int compare(ErpFreightTrial o1, ErpFreightTrial o2) {
                    BigDecimal o1Param = o1.getCny_amount().multiply(new BigDecimal(0.01)).add(new BigDecimal(o1.getEarliest_days()).multiply(new BigDecimal(0.99)));
                    BigDecimal o2Param = o2.getCny_amount().multiply(new BigDecimal(0.01)).add(new BigDecimal(o2.getEarliest_days()).multiply(new BigDecimal(0.99)));
                    if (o1Param.compareTo(o2Param) == 0) {
                        return 0;
                    } else if (o1Param.compareTo(o2Param) == 1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }
//        erpFreightTrials = erpFreightTrials.stream().sorted(Collections.reverseOrder(Comparator.comparing(ErpFreightTrial::getCny_amount))).collect(Collectors.toList());
        return erpFreightTrials;
    }

    public static List<GranaryFreightTrial> sortGranaryData(List <GranaryFreightTrial> granaryFreightTrials, Integer type){
        if (1 == type) {
            Collections.sort(granaryFreightTrials, new Comparator<GranaryFreightTrial>() {
                @Override
                public int compare(GranaryFreightTrial o1, GranaryFreightTrial o2) {
                    BigDecimal o1Param = o1.getTotal().multiply(new BigDecimal(0.99)).add(new BigDecimal(o1.getSm_delivery_time_min()).multiply(new BigDecimal(0.01)));
                    BigDecimal o2Param = o2.getTotal().multiply(new BigDecimal(0.99)).add(new BigDecimal(o2.getSm_delivery_time_min()).multiply(new BigDecimal(0.01)));
                    if (o1Param.compareTo(o2Param) == 0) {
                        return 0;
                    } else if (o1Param.compareTo(o2Param) == 1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        } else if (2 == type) {
            Collections.sort(granaryFreightTrials, new Comparator<GranaryFreightTrial>() {
                @Override
                public int compare(GranaryFreightTrial o1, GranaryFreightTrial o2) {
                    BigDecimal o1Param = o1.getTotal().multiply(new BigDecimal(0.50)).add(new BigDecimal(o1.getSm_delivery_time_min()).multiply(new BigDecimal(0.50)));
                    BigDecimal o2Param = o2.getTotal().multiply(new BigDecimal(0.50)).add(new BigDecimal(o2.getSm_delivery_time_min()).multiply(new BigDecimal(0.50)));
                    if (o1Param.compareTo(o2Param) == 0) {
                        return 0;
                    } else if (o1Param.compareTo(o2Param) == 1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        } else if (3 == type) {
            Collections.sort(granaryFreightTrials, new Comparator<GranaryFreightTrial>() {
                @Override
                public int compare(GranaryFreightTrial o1, GranaryFreightTrial o2) {
                    BigDecimal o1Param = o1.getTotal().multiply(new BigDecimal(0.01)).add(new BigDecimal(o1.getSm_delivery_time_min()).multiply(new BigDecimal(0.99)));
                    BigDecimal o2Param = o2.getTotal().multiply(new BigDecimal(0.01)).add(new BigDecimal(o2.getSm_delivery_time_min()).multiply(new BigDecimal(0.99)));
                    if (o1Param.compareTo(o2Param) == 0) {
                        return 0;
                    } else if (o1Param.compareTo(o2Param) == 1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }
//        granaryFreightTrials = granaryFreightTrials.stream().sorted(Collections.reverseOrder(Comparator.comparing(GranaryFreightTrial::getTotal))).collect(Collectors.toList());
        return granaryFreightTrials;
    }


    public static List<WmsFreightDTO> sorWmsData (List<WmsFreightDTO> wmsFreightDTOS, Integer type){
        List<WmsFreightDTO> result = new ArrayList<>();
        if (1 == type) {
            Collections.sort(wmsFreightDTOS, new Comparator<WmsFreightDTO>() {
                @Override
                public int compare(WmsFreightDTO o1, WmsFreightDTO o2) {
                    BigDecimal o1Param = o1.getAllFee().multiply(new BigDecimal(0.99)).add(new BigDecimal(o1.getMinDay()).multiply(new BigDecimal(0.01)));
                    BigDecimal o2Param = o2.getAllFee().multiply(new BigDecimal(0.99)).add(new BigDecimal(o2.getMinDay()).multiply(new BigDecimal(0.01)));
                    if (o1Param.compareTo(o2Param) == 0) {
                        return 0;
                    } else if (o1Param.compareTo(o2Param) == 1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        } else if (2 == type) {
            Collections.sort(wmsFreightDTOS, new Comparator<WmsFreightDTO>() {
                @Override
                public int compare(WmsFreightDTO o1, WmsFreightDTO o2) {
                    BigDecimal o1Param = o1.getAllFee().multiply(new BigDecimal(0.50)).add(new BigDecimal(o1.getMinDay()).multiply(new BigDecimal(0.50)));
                    BigDecimal o2Param = o2.getAllFee().multiply(new BigDecimal(0.50)).add(new BigDecimal(o2.getMinDay()).multiply(new BigDecimal(0.50)));
                    if (o1Param.compareTo(o2Param) == 0) {
                        return 0;
                    } else if (o1Param.compareTo(o2Param) == 1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        } else if (3 == type) {
            Collections.sort(wmsFreightDTOS, new Comparator<WmsFreightDTO>() {
                @Override
                public int compare(WmsFreightDTO o1, WmsFreightDTO o2) {
                    BigDecimal o1Param = o1.getAllFee().multiply(new BigDecimal(0.01)).add(new BigDecimal(o1.getMinDay()).multiply(new BigDecimal(0.99)));
                    BigDecimal o2Param = o2.getAllFee().multiply(new BigDecimal(0.01)).add(new BigDecimal(o2.getMinDay()).multiply(new BigDecimal(0.99)));
                    if (o1Param.compareTo(o2Param) == 0) {
                        return 0;
                    } else if (o1Param.compareTo(o2Param) == 1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }
        return wmsFreightDTOS;
    }


    /**
     * @Description 判断是否品连edis
     * @Author  xieyanbin
     * @Param  bandId 店铺id
     * @Return      boolean
     * @Exception
     *
     */
    private Integer isBrandslinkShop(Integer bandId){
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("bindCode",Arrays.asList(bandId));
        jsonParam.put("platform",1);
        jsonParam.put("dataType",10);
        jsonParam.put("page",1);
        jsonParam.put("row",100);
        JSONObject jsonStr = remoteSellerService.getEmpowerSearchVO(jsonParam);
        if(!"true".equals(jsonStr.getString("success")) || CollectionUtils.isEmpty(jsonStr.getJSONArray("data"))) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用授权接口异常");
        }
        Integer edisType = jsonStr.getJSONArray("data").getJSONObject(0).getInteger("ebayEdis");
        if(null == edisType){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"获取不到eids类型");
        }
        return edisType;
    }

    private List<ErpFreightTrial> queryErpFreightDetail(String systemSku,Integer skuNum,List<Map<String, Object>> allSkuList,FreightTrial freightTrial){
        Map<String, Object> skuMap = new HashMap<>();
        if(StringUtils.isNotEmpty(systemSku)){
            JSONObject dataJSON = JSONObject.parseObject(JSONObject.toJSONString(remoteCommodityService.getBySku(systemSku,null,null))).getJSONObject("data");
            if(null == dataJSON) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"sku错误");
            String supplierSku = dataJSON.getString("supplierSku");
            if(StringUtils.isEmpty(supplierSku)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"sku错误");
            skuMap.put("sku", supplierSku);
            skuMap.put("num",skuNum);
            allSkuList.add(skuMap);
            freightTrial.setList(Arrays.asList(skuMap));
        }else{
            freightTrial.setList(allSkuList);
        }
        List<ErpFreightTrial> erpFreightTrialList = freightUtil.getErpFreightTrial(freightTrial);
        if (CollectionUtils.isEmpty(erpFreightTrialList)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"erp无数据");
        return erpFreightTrialList;
    }

    private Map<String,Object> queryGranaryFreightDetail(String systemSku,Integer skuNum,List<String> allSkuList,FreightTrial freightTrial){
        Map<String, Object>  gcMap = new HashMap<String,Object>();
        if(StringUtils.isNotEmpty(systemSku)){
            systemSku = systemSku + ":" + skuNum;
            allSkuList.add(systemSku);
            freightTrial.setList(Arrays.asList(systemSku));
            gcMap = freightUtil.getGcFreightTrial(freightTrial);
        }else{
            freightTrial.setList(allSkuList);
            gcMap = freightUtil.getGcFreightTrial(freightTrial);
        }
        List<GranaryFreightTrial> granaryFreightTrialList = (List<GranaryFreightTrial>) gcMap.get("data");
        if (CollectionUtils.isEmpty(granaryFreightTrialList)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"谷仓无数据");
        return  gcMap;
    }

    private List<WmsFreightDTO> queryWmsFreightDetail(String systemSku,Integer skuNum,List<Map<String, Object>> allSkuList,FreightTrial freightTrial){
        Map<String, Object> skuMap = new HashMap<>();
        if(StringUtils.isNotEmpty(systemSku)){
            skuMap.put("sku", systemSku);
            skuMap.put("quantity",skuNum);
            allSkuList.add(skuMap);
            freightTrial.setList(Arrays.asList(skuMap));
        }else{
            freightTrial.setList(allSkuList);
        }
        String dataStr = remoteWmsService.wmsFreight(freightTrial);
        List<WmsFreightDTO> wmsFreightDTOS = JSONObject.parseArray(dataStr,WmsFreightDTO.class);
        return wmsFreightDTOS;
    }
}


