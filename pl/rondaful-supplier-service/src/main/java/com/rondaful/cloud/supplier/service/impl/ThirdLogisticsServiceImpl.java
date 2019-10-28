package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.StringUtils;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.supplier.dto.FreightTrialDTO;
import com.rondaful.cloud.supplier.dto.LogisticsDTO;
import com.rondaful.cloud.supplier.entity.*;
import com.rondaful.cloud.supplier.entity.Logistics.LogisticsMapping;
import com.rondaful.cloud.supplier.mapper.LogisticsInfoMapper;
import com.rondaful.cloud.supplier.mapper.ThirdLogisticsMapper;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseInitDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseListDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.WmsFreightDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.WmsLogisticsDTO;
import com.rondaful.cloud.supplier.remote.*;
import com.rondaful.cloud.supplier.service.IThirdLogisticsService;
import com.rondaful.cloud.supplier.service.IWarehouseBasicsService;
import com.rondaful.cloud.supplier.utils.FreightUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
public class ThirdLogisticsServiceImpl implements IThirdLogisticsService {

	private final Logger logger = LoggerFactory.getLogger(ThirdLogisticsServiceImpl.class);

	@Autowired
	private ThirdLogisticsMapper thirdLogisticsMapper;

	@Autowired
	private LogisticsInfoMapper logisticsInfoMapper;

	@Autowired
	private IWarehouseBasicsService warehouseBasicsService;

	@Autowired
	private RemoteErpService remoteErpService;

	@Autowired
	private RemoteOrderService remoteOrderService;

	@Autowired
	private RemoteCommodityService remoteCommodityService;

	@Autowired
	private RemoteGranaryService remoteGranaryService;

	@Autowired
	private RemoteWmsService remoteWmsService;

	@Autowired
	private  FreightUtil freightUtil;

	@Autowired
	private RemoteUserService remoteUserService;

	@Value("${oversea.warehouse}")
	private String overseaWarehouse;

	@Value("${granary.app_key}")
	private String default_app_key;

	@Value("${granary.app_token}")
	private String default_app_token;

	@Override
	public void insertErpLogistics() throws Exception {
		JSONObject carrier = remoteErpService.getCarrier();
		String json = carrier.getString("data");
		logger.info("json={}",json);
		List<ErpLogistics> erpLogisticsList = JSONObject.parseArray(json, ErpLogistics.class);
		List<LogisticsInfo> logisticsInfoList = new ArrayList<LogisticsInfo>();
		for(ErpLogistics erpLogistics : erpLogisticsList) {
			for (ErpWarehouse erpWarehouse : erpLogistics.getUse_warehouse_arr()) {
				LogisticsInfo logisticsInfo = new LogisticsInfo();
				logisticsInfo = setterValue(logisticsInfo,erpLogistics);//实体转换
				Integer warehouseId = warehouseBasicsService.codeToId(erpWarehouse.getCode());
				if(null == warehouseId) { continue;}
				logisticsInfo.setWarehouseId(warehouseId.toString());
				logisticsInfoList.add(logisticsInfo);
			}
		}
		logger.info("logisticsInfoList={}",logisticsInfoList);
		logisticsInfoMapper.insertLogisticsInfoList(logisticsInfoList);
		thirdLogisticsMapper.insertErpLogistics(erpLogisticsList);//插入t_third_logistics
	}

	/*
	 * 插入谷仓物流信息
	 */
	@Override
	@Async
	public void insertGuLogisticsList(Integer serviceId) {
		WarehouseInitDTO warehouseInitDTO = warehouseBasicsService.getByFirmId(serviceId);
		if("GOODCANG".equals(warehouseInitDTO.getFirmCode())){
			Map<String,Object> paramMap = new HashMap<String,Object>();
			try {
				if(null == warehouseInitDTO || CollectionUtils.isEmpty(warehouseInitDTO.getList())) logger.error("仓库无数据");
				List<WarehouseListDTO> warehouseListDTOList = warehouseInitDTO.getList();
				for(WarehouseListDTO warehouseListDTO : warehouseListDTOList){
					paramMap.put("warehouseCode",warehouseListDTO.getWarehouseCode());
					JSONObject jsonResult = remoteGranaryService.getShippingMethod(paramMap, warehouseInitDTO.getAppToken(), warehouseInitDTO.getAppKey());
					if(StringUtils.isNotEmpty(jsonResult.getString("data"))){
						List<GranaryLogistics> GuLogisticsList = JSONObject.parseArray(jsonResult.getString("data"), GranaryLogistics.class);
						List<LogisticsInfo> logisticsInfoList = new ArrayList<LogisticsInfo>();
						if(CollectionUtils.isNotEmpty(GuLogisticsList)) {
							for(GranaryLogistics guLogistics:GuLogisticsList) {
								guLogistics.setApp_key(warehouseInitDTO.getAppKey());
								guLogistics.setApp_token(warehouseInitDTO.getAppToken());
								LogisticsInfo logisticsInfo = new LogisticsInfo();
								logisticsInfo = setterValue(logisticsInfo, guLogistics);
								logisticsInfo.setWarehouseId(warehouseListDTO.getId().toString());
								logisticsInfoList.add(logisticsInfo);
							}
							logisticsInfoMapper.insertLogisticsInfoList(logisticsInfoList);//插入t_loggistics_info主表
							thirdLogisticsMapper.insertGuLogisticsList(GuLogisticsList);//插入t_third_logistics
						}
					}
				}
			}catch (Exception e){
				logger.error("插入谷仓物流方式异常",e);
			}
		}else if("WMS".equals(warehouseInitDTO.getFirmCode())){
			for(WarehouseListDTO warehouseListDTO : warehouseInitDTO.getList()) {
				String dataStr = remoteWmsService.selectLogisticsMethod(warehouseListDTO.getWarehouseCode());
				String data = JSONObject.parseObject(dataStr).getJSONObject("data").getJSONObject("pageInfo").getString("list");
				List<WmsLogisticsDTO> wmsLogisticsDTOS = JSONObject.parseArray(data, WmsLogisticsDTO.class);
				if (CollectionUtils.isEmpty(wmsLogisticsDTOS)) break;
				List<LogisticsInfo> logisticsInfos = new ArrayList<>();
				for (WmsLogisticsDTO wmsLogisticsDTO : wmsLogisticsDTOS) {
					LogisticsInfo logisticsInfo = new LogisticsInfo();
					logisticsInfo.setStatus("0");
					logisticsInfo.setType("1");
					logisticsInfo.setWarehouseId(warehouseListDTO.getId().toString());
					logisticsInfo.setCode(wmsLogisticsDTO.getMethodCode());
					logisticsInfo.setShortName(wmsLogisticsDTO.getMethodCnName());
					logisticsInfo.setCarrierCode(wmsLogisticsDTO.getProviderCode());
					logisticsInfo.setCarrierName(wmsLogisticsDTO.getProviderShortened());
					logisticsInfo.setCreateTime(DateUtils.dateToString(new Date(), DateUtils.FORMAT_2));
					logisticsInfo.setLastUpdateTime(DateUtils.dateToString(new Date(), DateUtils.FORMAT_2));
					logisticsInfos.add(logisticsInfo);
				}
				logisticsInfoMapper.insertLogisticsInfoList(logisticsInfos);
			}
		}


	}


	@Override
	public void insertAliexpressLogistics(List<AliexpressLogistics> list) {
		thirdLogisticsMapper.insertAliexpressLogistics(list);
	}

	@Override
	public void updateLogisticsMappingList(List<LogisticsInfo> logisticsInfo) {
		thirdLogisticsMapper.updateLogisticsMappingList(logisticsInfo);
	}

	@Override
	public void updateErpLogistics()  throws Exception{
		JSONObject carrier = remoteErpService.getCarrier();
		List<ErpLogistics> erpLogisticsList = JSONObject.parseArray(carrier.getString("data"), ErpLogistics.class);
		for (ErpLogistics erpLogistics : erpLogisticsList){
			for(ErpWarehouse erpWarehouse : erpLogistics.getUse_warehouse_arr()){
				Integer warehouseId = warehouseBasicsService.codeToId(erpWarehouse.getCode());
				if(null == warehouseId) continue;
				Integer logisticsCount = thirdLogisticsMapper.queryLogisticsCountByCode(warehouseId,erpLogistics.getCode());
				if(0 == logisticsCount){
					LogisticsInfo logisticsInfo = setterLogisticsValue(erpLogistics);
					logisticsInfo.setWarehouseId(warehouseId.toString());
					logisticsInfoMapper.insertLogisticsInfo(logisticsInfo);
				}
			}
		}
	}

	@Override
	public void updateGranaryLogistics() {

	}

	@Override
	public void importCountry(Workbook wb) {
		Sheet sheet = wb.getSheetAt(0);
		for(int i = 1;i<=sheet.getLastRowNum();i++){
			CountryMap countryMap = new CountryMap();
			countryMap.setCountryName(sheet.getRow(i).getCell(1).toString());
			countryMap.setCountryNameEn(sheet.getRow(i).getCell(2).toString());
			countryMap.setCountryCode(sheet.getRow(i).getCell(3).toString());
			countryMap.setPostCode(sheet.getRow(i).getCell(4).toString());
			List<CountryMap> countryMapList = thirdLogisticsMapper.queryCountryByCode(countryMap);
			if(CollectionUtils.isNotEmpty(countryMapList)){
				thirdLogisticsMapper.updateCountryByCode(countryMap);
			}else{
				thirdLogisticsMapper.insertCountry(countryMap);
			}
		}
	}

	@Override
	public List<CountryMap> getCountry(CountryMap countryMap) {
		return thirdLogisticsMapper.queryCountryByCode(countryMap);
	}

	@Override
	@Async
	public void initWmsLogistics(Integer warehouseId) {
		WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(warehouseId);
		if(null == warehouseDTO) 	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该仓库未授权！请重新选择仓库");
		String dataStr = remoteWmsService.selectLogisticsMethod(warehouseDTO.getWarehouseCode());
		String data = JSONObject.parseObject(dataStr).getJSONObject("data").getJSONObject("pageInfo").getString("list");
		List<WmsLogisticsDTO> wmsLogisticsDTOS = JSONObject.parseArray(data,WmsLogisticsDTO.class);
		if(CollectionUtils.isEmpty(wmsLogisticsDTOS)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"wms物流方式为空");
		List<LogisticsInfo> logisticsInfos = new ArrayList<>();
		for(WmsLogisticsDTO wmsLogisticsDTO :wmsLogisticsDTOS){
			LogisticsInfo logisticsInfo = new LogisticsInfo();
			logisticsInfo.setStatus("0");
			logisticsInfo.setType("1");
			logisticsInfo.setWarehouseId(warehouseId.toString());
			logisticsInfo.setCode(wmsLogisticsDTO.getMethodCode());
			logisticsInfo.setShortName(wmsLogisticsDTO.getMethodCnName());
			logisticsInfo.setCarrierCode(wmsLogisticsDTO.getProviderCode());
			logisticsInfo.setCarrierName(wmsLogisticsDTO.getProviderShortened());
			logisticsInfo.setCreateTime(DateUtils.dateToString(new Date(),DateUtils.FORMAT_2));
			logisticsInfo.setLastUpdateTime(DateUtils.dateToString(new Date(),DateUtils.FORMAT_2));
			logisticsInfos.add(logisticsInfo);
		}
		logisticsInfoMapper.insertLogisticsInfoList(logisticsInfos);
	}

	@Override
	public List<FreightTrialDTO> queryWmsFreight(FreightTrial freightTrial) {
		List<FreightTrialDTO> freightTrialDTOS = new ArrayList<FreightTrialDTO>();
		WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(freightTrial.getWarehouseId());
		if("WMS".equals(warehouseDTO.getFirmCode())){
			List<Map<String,Object>> skuList = new ArrayList<>();
			for(Map<String,Object> paramSku:freightTrial.getSkuList()){
				Map<String,Object> skuMap = new HashMap<>();
				skuMap.put("sku",paramSku.get("sku"));
				skuMap.put("quantity",paramSku.get("num"));
				skuList.add(skuMap);
			}
			freightTrial.setList(skuList);
			freightTrial.setAppToken(warehouseDTO.getAppToken());
			freightTrial.setAppKey(warehouseDTO.getAppKey());
			freightTrial.setWarehouseCode(warehouseDTO.getWarehouseCode());
			String dataStr = remoteWmsService.wmsFreight(freightTrial);
			List<WmsFreightDTO> wmsFreightDTOS = JSONObject.parseArray(dataStr,WmsFreightDTO.class);
			freightTrialDTOS = transformWmsData(wmsFreightDTOS);
		}
		return freightTrialDTOS;
	}

	@Override
	public void updateWmsLogistics() {
//		WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(warehouseId);
//		if(null == warehouseDTO) 	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该仓库未授权！请重新选择仓库");
//		String dataStr = remoteWmsService.selectLogisticsMethod(warehouseDTO.getWarehouseCode());
//		String data = JSONObject.parseObject(dataStr).getJSONObject("data").getJSONObject("pageInfo").getString("list");
//		List<WmsLogisticsDTO> wmsLogisticsDTOS = JSONObject.parseArray(data,WmsLogisticsDTO.class);
//		if(CollectionUtils.isEmpty(wmsLogisticsDTOS)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"wms物流方式为空");
//		List<LogisticsInfo> logisticsInfos = new ArrayList<>();
//		for(WmsLogisticsDTO wmsLogisticsDTO :wmsLogisticsDTOS){
//			LogisticsDTO logisticsDTO = logisticsInfoMapper.queryLogisticsByCode(wmsLogisticsDTO.getMethodCode(),warehouseId);
//			if(null == logisticsDTO){
//				LogisticsInfo logisticsInfo = new LogisticsInfo();
//				logisticsInfo.setStatus("0");
//				logisticsInfo.setType("1");
//				logisticsInfo.setWarehouseId(warehouseId.toString());
//				logisticsInfo.setCode(wmsLogisticsDTO.getMethodCode());
//				logisticsInfo.setShortName(wmsLogisticsDTO.getMethodName());
//				logisticsInfo.setCarrierCode(wmsLogisticsDTO.getProviderCode());
//				logisticsInfo.setCarrierName(wmsLogisticsDTO.getProviderShortened());
//				logisticsInfo.setCreateTime(DateUtils.dateToString(new Date(),DateUtils.FORMAT_2));
//				logisticsInfo.setLastUpdateTime(DateUtils.dateToString(new Date(),DateUtils.FORMAT_2));
//				logisticsInfos.add(logisticsInfo);
//			}
//		}
//		logisticsInfoMapper.insertLogisticsInfoList(logisticsInfos);
	}

	@Override
	public void noticeLogisticsDiscard(List<LogisticsInfo> param) {
		for(LogisticsInfo logisticsInfo:param){
			WarehouseDTO warehouseDTO = warehouseBasicsService.getByAppTokenAndCode(logisticsInfo.getWarehouseCode());
			logisticsInfo.setWarehouseId(warehouseDTO.getWarehouseId().toString());
			logisticsInfo.setStatus("0");
			logisticsInfoMapper.updateStatusById(logisticsInfo);
		}
	}


	/*
	 * 获取谷仓运费试算
	 */
	private Map<String,Object> getGranaryFreightTrial(FreightTrial param) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		JSONObject json = null;
		try {
			if(StringUtils.isEmpty(param.getPostCode())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"邮政编码不能为空");
			WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(param.getWarehouseId());
			if(null == warehouseDTO) 	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该仓库未授权！请重新选择仓库");
//			if(StringUtils.isEmpty(warehouseDTO.getAppKey()) && StringUtils.isEmpty(warehouseDTO.getAppToken()))
//				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"appKey和appToken没找到");
			if(StringUtils.isEmpty(param.getAppKey()) || StringUtils.isEmpty(param.getAppToken())){
				json = remoteGranaryService.getCalculateDeliveryFee(param,warehouseDTO.getAppKey(),warehouseDTO.getAppToken(),warehouseDTO.getWarehouseCode());
			}else{
				json = remoteGranaryService.getCalculateDeliveryFee(param,param.getAppKey(),param.getAppToken(),param.getWarehouseCode());
			}
			logger.info("谷仓返回数据：json={}",json);
			if ("Failure".equals(json.getString("ask")))	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, json.getString("message"));
			List<GranaryFreightTrial> GranaryFreightTrialList = JSONObject.parseArray(json.getString("data"), GranaryFreightTrial.class);
			if(CollectionUtils.isNotEmpty(GranaryFreightTrialList)) {
				result.put("data",GranaryFreightTrialList);
			}
			if(StringUtils.isNotEmpty(json.getString("currency"))) {
				result.put("currency",json.getString("currency"));
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}



	/*
	 * 匹配物流方式
	 * 调用运费试算，查出物流方式code，根据code遍历查出表中的物流信息，在进行过滤
	 */
	@Override
	public List<LogisticsDTO> matchLogistics(FreightTrial param) throws Exception {
		//通过运费试算查出物流方式code,再通过code查找表中的数据进行过滤
		List<LogisticsDTO> result = new ArrayList<LogisticsDTO>();
		JSONObject jsonObject = JSONObject.parseObject(remoteOrderService.getRate("CNY","USD")); //获取汇率
		BigDecimal rate = new BigDecimal(jsonObject.getString("data"));
		WarehouseDTO warehouseDTO = warehouseBasicsService.getByWarehouseId(param.getWarehouseId()); //获取仓库信息
		List<String> overseaWarehouseList = Arrays.asList(overseaWarehouse.split(","));
		List list = new ArrayList();
		for(Map<String,Object> skuParam:param.getSkuList()){
			Map<String,Object> skuMap = new HashMap<>();
			String sku = skuParam.get("sku").toString();
			String num = skuParam.get("num").toString();
			JSONObject dataJSON = JSONObject.parseObject(JSONObject.toJSONString(remoteCommodityService.getBySku(sku,null,null))).getJSONObject("data");
			if(null == dataJSON) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"查不到该sku信息");
			if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
				sku = dataJSON.getString("systemSku");
				if(StringUtils.isEmpty(sku)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"sku错误");
				sku = sku + ":"+ num;
				list.add(sku);
			}else if("RONDAFUL".equals(warehouseDTO.getFirmCode())){
				sku = dataJSON.getString("supplierSku"); //品连sku转成供应商sku
				if(StringUtils.isEmpty(sku)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"sku错误");
				skuMap.put("sku",sku);
				skuMap.put("num",num);
				list.add(skuMap);
			}else if("GOODCANG".equals(warehouseDTO.getFirmCode())){
				sku = dataJSON.getString("systemSku");
				if(StringUtils.isEmpty(sku)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"sku错误");
				sku = sku + ":"+ num;
				list.add(sku);
			}else if("WMS".equals(warehouseDTO.getFirmCode())){
				sku = dataJSON.getString("systemSku");
				if(StringUtils.isEmpty(sku)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"sku错误");
				skuMap.put("sku",sku);
				skuMap.put("quantity",num);
				list.add(skuMap);

			}
		}
		param.setList(list);
		param.setAppKey(warehouseDTO.getAppKey());
		param.setAppToken(warehouseDTO.getAppToken());
		param.setWarehouseCode(warehouseDTO.getWarehouseCode());
		if("RONDAFUL".equals(warehouseDTO.getFirmCode()) && overseaWarehouseList.contains(warehouseDTO.getWarehouseCode())){
			if(StringUtils.isEmpty(param.getPostCode())){
				FeignResult feignResult = remoteUserService.getArea(null,param.getCountryCode());
				if(!feignResult.getSuccess() ||  null == feignResult.getData()){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
				}
				String postCode = JSONObject.parseObject(JSONObject.toJSONString(feignResult.getData())).getString("postCode");
				if(StringUtils.isEmpty(postCode)) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
				}
				param.setPostCode(postCode);
			}
			param.setWarehouseCode(warehouseDTO.getWarehouseCode().split("_")[1]);
			param.setAppToken(default_app_token);
			param.setAppKey(default_app_key);
			Map<String, Object> map = getGranaryFreightTrial(param);
			List<GranaryFreightTrial> GranaryFreightTrialList = (List<GranaryFreightTrial>) map.get("data");
			for (GranaryFreightTrial granaryFreightTrial : GranaryFreightTrialList) {
				LogisticsMapping logisticsMapping = logisticsInfoMapper.selectLogisticsMapping(granaryFreightTrial.getSm_code(),null,warehouseDTO.getWarehouseId());
				if( null != logisticsMapping && StringUtils.isNotEmpty(logisticsMapping.getGranaryLogisticsCode())){
					LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(param.getWarehouseId(), logisticsMapping.getErpLogisticsCode());
					if (null != logisticsDTO) {
						BigDecimal total = granaryFreightTrial.getTotal();
						if ("CNY".equals(map.get("currency"))) {
							total= total.multiply(rate).setScale(4, BigDecimal.ROUND_HALF_UP);
						}
						logisticsDTO.setTotalCost(total);
						setForeignValue(logisticsDTO);
						if (null != getlogisticsMsg(logisticsDTO, param)) {
							result.add(logisticsDTO);
						}
						logisticsDTO.setCode(logisticsMapping.getErpLogisticsCode());
						logisticsDTO.setShortName(logisticsMapping.getErpLogisticsName());
					}
				}else{
					continue;
				}
			}
		}else if("RONDAFUL".equals(warehouseDTO.getFirmCode())){
			List<ErpFreightTrial> erpFreightTrialList = freightUtil.getErpFreightTrial(param);
			for (ErpFreightTrial erpFreightTrial : erpFreightTrialList) {
				LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(param.getWarehouseId(), erpFreightTrial.getShipping_code());
				if (null != logisticsDTO) {
					logisticsDTO.setTotalCost(erpFreightTrial.getCny_amount().multiply(rate).setScale(4, BigDecimal.ROUND_HALF_UP));
					setForeignValue(logisticsDTO);
					if (null != getlogisticsMsg(logisticsDTO, param)) {
						result.add(logisticsDTO);
					}
				}
			}
		}else if("GOODCANG".equals(warehouseDTO.getFirmCode())){
			if(StringUtils.isEmpty(param.getPostCode())){
				FeignResult feignResult = remoteUserService.getArea(null,param.getCountryCode());
				if(!feignResult.getSuccess() ||  null == feignResult.getData()){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
				}
				String postCode = JSONObject.parseObject(JSONObject.toJSONString(feignResult.getData())).getString("postCode");
				if(StringUtils.isEmpty(postCode)) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"获取邮编异常");
				}
				param.setPostCode(postCode);
			}
			Map<String, Object> map = getGranaryFreightTrial(param);
			List<GranaryFreightTrial> GranaryFreightTrialList = (List<GranaryFreightTrial>) map.get("data");
			for (GranaryFreightTrial granaryFreightTrial : GranaryFreightTrialList) {
				LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(param.getWarehouseId(), granaryFreightTrial.getSm_code());
				if (null != logisticsDTO) {
					BigDecimal total = granaryFreightTrial.getTotal();
					if ("CNY".equals(map.get("currency"))) {
						total= total.multiply(rate).setScale(4, BigDecimal.ROUND_HALF_UP);
					}
					logisticsDTO.setTotalCost(total);
					setForeignValue(logisticsDTO);
					if (null != getlogisticsMsg(logisticsDTO, param)) {
						result.add(logisticsDTO);
					}
				}
			}
		}else if("WMS".equals(warehouseDTO.getFirmCode())){
			String dataStr = remoteWmsService.wmsFreight(param);
			List<WmsFreightDTO> wmsFreightDTOS = JSONObject.parseArray(dataStr,WmsFreightDTO.class);
			for (WmsFreightDTO wmsFreightDTO : wmsFreightDTOS) {
				LogisticsDTO logisticsDTO = logisticsInfoMapper.getLogisticsByCode(param.getWarehouseId(), wmsFreightDTO.getMethodCode());
				if (null != logisticsDTO) {
					logisticsDTO.setTotalCost(wmsFreightDTO.getAllFee().multiply(rate).setScale(4, BigDecimal.ROUND_HALF_UP));
					setForeignValue(logisticsDTO);
					if (null != getlogisticsMsg(logisticsDTO, param)) {
						result.add(logisticsDTO);
					}
				}
			}
		}
		return result;
	}


	private LogisticsDTO getlogisticsMsg(LogisticsDTO logisticsDTO,FreightTrial param) {
		//通过物流cod和仓库codee查表，在进行过滤
		if(null != logisticsDTO) {
			if ("1".equals(param.getPlatformType())) {//ebay订单，供应商ebay映射和后台ebay映射不能为空
				if (StringUtils.isEmpty(logisticsDTO.getEbayCarrier()) && StringUtils.isEmpty(logisticsDTO.getOtherEbayCarrier())) {
					return null;
				}
			} else if ("2".equals(param.getPlatformType())) {//amazon订单，供应商amazon映射和后台amazon映射不能为空
				if (StringUtils.isEmpty(logisticsDTO.getAmazonCarrier()) || StringUtils.isEmpty(logisticsDTO.getAmazonCode())) {
					if (StringUtils.isEmpty(logisticsDTO.getOtherAmazonCarrier()) || StringUtils.isEmpty(logisticsDTO.getOtherAmazonCode())) {
						return null;
					}
				}
			} else if ("4".equals(param.getPlatformType())) {//aliexpress订单，供应商aliexpress映射和后台aliexpress映射不能为空
				if (StringUtils.isEmpty(logisticsDTO.getAliexpressCode())) {
					return null;
				}
			}
			setForeignValue(logisticsDTO);
		}

		return logisticsDTO;
	}

	private LogisticsInfo setterLogisticsValue(ErpLogistics erpLogistics){
		LogisticsInfo logisticsInfo = new LogisticsInfo();
		logisticsInfo.setShortName(erpLogistics.getShortname());
		logisticsInfo.setType("2");
		logisticsInfo.setCarrierName(erpLogistics.getCarrier_name());
		logisticsInfo.setCarrierCode(erpLogistics.getCarrier_code());
		logisticsInfo.setCode(erpLogistics.getCode());

		return logisticsInfo;
	}

	private void setForeignValue(LogisticsDTO logisticsDTO) {
		if(logisticsDTO.getShortName().equals(Utils.i18n(logisticsDTO.getShortName()))){
			logisticsDTO.setForeignShortName(Utils.translation(logisticsDTO.getShortName()));
		}else{
			logisticsDTO.setForeignShortName(Utils.i18n(logisticsDTO.getShortName()));
		}
		if(logisticsDTO.getCarrierName().equals(Utils.i18n(logisticsDTO.getCarrierName()))){
			logisticsDTO.setForeignCarrierName(Utils.translation(logisticsDTO.getCarrierName()));
		}else{
			logisticsDTO.setForeignCarrierName(Utils.i18n(logisticsDTO.getCarrierName()));
		}

	}

	private LogisticsInfo setterValue(LogisticsInfo logisticsInfo,ErpLogistics erpLogistics) {
		if(StringUtils.isNotEmpty(erpLogistics.getShortname()))  logisticsInfo.setShortName(erpLogistics.getShortname());
		if(StringUtils.isNotEmpty(erpLogistics.getCarrier_code()))  logisticsInfo.setCarrierCode(erpLogistics.getCarrier_code());
		if(StringUtils.isNotEmpty(erpLogistics.getCarrier_name())) logisticsInfo.setCarrierName(erpLogistics.getCarrier_name());
		if(StringUtils.isNotEmpty(erpLogistics.getCode())) {
			logisticsInfo.setCode(erpLogistics.getCode());
		}else {
			return new LogisticsInfo();
		}
		logisticsInfo.setType("2");
		logisticsInfo.setLastUpdateTime(DateUtils.dateToString(new Date(),DateUtils.FORMAT_2));
		logisticsInfo.setCreateTime(DateUtils.dateToString(new Date(),DateUtils.FORMAT_2));
		return logisticsInfo;
	}

	private LogisticsInfo setterValue(LogisticsInfo logisticsInfo,GranaryLogistics guLogistics) {
		if(StringUtils.isNotEmpty(guLogistics.getSp_code())) {
			logisticsInfo.setCarrierCode(guLogistics.getSp_code());//物流商code
			logisticsInfo.setCarrierName(guLogistics.getSp_code());//物流商名称
		}
		if(StringUtils.isNotEmpty(guLogistics.getName()))  logisticsInfo.setShortName(guLogistics.getName());//物流方式名称
		if(StringUtils.isNotEmpty(guLogistics.getCode())) {//物流方式code
			logisticsInfo.setCode(guLogistics.getCode());
		}else {
			return new LogisticsInfo();
		}
		logisticsInfo.setType("1");
		logisticsInfo.setLastUpdateTime(DateUtils.dateToString(new Date(),DateUtils.FORMAT_2));
		logisticsInfo.setCreateTime(DateUtils.dateToString(new Date(),DateUtils.FORMAT_2));
		return logisticsInfo;
	}


	private List<FreightTrialDTO> transformWmsData(List<WmsFreightDTO> wmsFreightDTOS){
		List<FreightTrialDTO> result = new ArrayList<>();
		for(WmsFreightDTO wmsFreightDTO:wmsFreightDTOS){
			FreightTrialDTO freightTrialDTO = new FreightTrialDTO();
			freightTrialDTO.setLogisticsName(wmsFreightDTO.getMethodCnName());
			freightTrialDTO.setLogisticsCode(wmsFreightDTO.getMethodCode());
			freightTrialDTO.setTotalCost(wmsFreightDTO.getAllFee());
			Map map = (Map)wmsFreightDTO.getPromiseDays();
			freightTrialDTO.setMinDeliveryTime((Integer)map.get("min"));
			freightTrialDTO.setMaxDeliveryTime((Integer)map.get("max"));
			result.add(freightTrialDTO);
		}
		return result;
	}

}
