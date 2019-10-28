package com.rondaful.cloud.commodity.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.constant.CommonConstant;
import com.rondaful.cloud.commodity.constant.RedisKeyConstant;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.GoodCangCategory;
import com.rondaful.cloud.commodity.entity.GoodCangCategoryBind;
import com.rondaful.cloud.commodity.entity.SkuPushLog;
import com.rondaful.cloud.commodity.entity.SkuPushRecord;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.enums.WarehouseFirmEnum;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.mapper.GoodCangMapper;
import com.rondaful.cloud.commodity.remote.RemoteSupplierService;
import com.rondaful.cloud.commodity.service.GoodCangService;
import com.rondaful.cloud.commodity.service.SkuImportService;
import com.rondaful.cloud.commodity.service.WmsPushService;
import com.rondaful.cloud.commodity.vo.CodeAndValueVo;
import com.rondaful.cloud.commodity.vo.GoodCangProduct;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RemoteUtil;
import com.rondaful.cloud.common.utils.Utils;

@Service
public class GoodCangServiceImpl implements GoodCangService{
	
	private final static Logger log = LoggerFactory.getLogger(GoodCangServiceImpl.class);

	@Autowired
	private GoodCangMapper goodCangMapper;
	
	@Autowired
	private CommoditySpecMapper commoditySpecMapper;
	
	@Value("${domain.url}")
	private String domainUrl;
	
	@Value("${wsdl.url}")
    private String goodCangUrl;
	
	@Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
	
	@Autowired
	private RemoteSupplierService remoteSupplierService;
	
	@Autowired
	GranaryUtils granaryUtils;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private WmsPushService wmsPushService;
	
	@Autowired
	private SkuImportService skuImportService;
	
	
	@Override
	public List<GoodCangCategory> findList() {
		 List<GoodCangCategory> list1 = goodCangMapper.pageCategory(new GoodCangCategory(){{
            this.setCategory_level(0);
        }});
        List<GoodCangCategory> list2 = goodCangMapper.pageCategory(new GoodCangCategory(){{
            this.setCategory_level(1);
        }});
        List<GoodCangCategory> list3 = goodCangMapper.pageCategory(new GoodCangCategory(){{
            this.setCategory_level(2);
        }});

        for (GoodCangCategory ca1 : list1) {
            ca1.setChildren(new ArrayList<>());
            
            for (GoodCangCategory ca2 : list2) {
                ca2.setChildren(new ArrayList<>());
                
                if (ca2.getParent_category_id().intValue() == ca1.getCategory_id().intValue()) {
                    ca1.getChildren().add(ca2);
                }
                for (GoodCangCategory ca3 : list3) {
                    if (ca3.getParent_category_id().intValue() == ca2.getCategory_id().intValue()) {
                        ca2.getChildren().add(ca3);
                    }
                }
            }
        }
        return list1;
	}

	
	@Transactional(rollbackFor={RuntimeException.class,Exception.class})
	@Override
	public void addOrUpdateCategoryBind(GoodCangCategoryBind bind) {
		GoodCangCategoryBind categoryBind=goodCangMapper.getCategoryBindByCategoryId(bind.getPinlianCategoty3Id());
		if (categoryBind == null) {
			goodCangMapper.insertCategoryBind(bind);
		}else {
			bind.setId(categoryBind.getId());
			bind.setVersion(categoryBind.getVersion());
			goodCangMapper.updateCategoryBind(bind);
		}
	}

	@Override
	public void addAllProductByPage(Integer accountId, int total, Long supplierId, String optUser,Integer status) {
		log.info("推送全部sku到谷仓开始====>{}",total);
		int pages=total%500==0 ? total/500 : total/500+1;
		for (int i = 1; i <= pages; i++) {
			Map<String, Object> skuMap=new HashMap<String, Object>();
	        skuMap.put("supplierId", supplierId);
	        skuMap.put("accountId", accountId);
	        skuMap.put("status", status);
	        Page.builder(String.valueOf(i), "500");
			List<CommoditySpec> commoditySpecs=commoditySpecMapper.getUnPushSystemSku(skuMap);
			if (commoditySpecs != null && commoditySpecs.size()>0) {
				pushSkusToGoodCang(accountId,1,commoditySpecs,optUser);
			}
		}
	}

	/**
	 * @Description:推送商品到谷仓
	 * @param accountId 账号ID
	 * @param type  接口类型:1新增，2编辑
	 * @param skuList 品连sku数组
	 * @return void
	 * @author:范津
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> pushSkusToGoodCang(Integer accountId,int type,List<CommoditySpec> commList,String optUser) {
		Map<String, Object> resultMap=new HashMap<String, Object>();
		AtomicInteger failNum=new AtomicInteger(0);//失败的数量
		AtomicInteger successNum=new AtomicInteger(0);//成功的数量
		AtomicInteger totalNum=new AtomicInteger(0);//推送的总数
		List<CodeAndValueVo> unPushSkuList=new ArrayList<CodeAndValueVo>();//未通过校验的推送数据，推送前拦截
	
		String appToken="";
		String appKey="";
		String warehouseProviderCode="";
		Map<String, String> providerServiceInfo=getAppkeyAndTokenByAccountId(accountId);
		if (providerServiceInfo != null) {
			appToken=providerServiceInfo.get("appToken");
			appKey=providerServiceInfo.get("appKey");
			warehouseProviderCode=providerServiceInfo.get("warehouseProviderCode");
		}
		
		if (StringUtils.isBlank(appToken) || StringUtils.isBlank(appKey)) {
			log.error("获取账号"+accountId+"的appToken或appKey为空");
			return null;
		}
		
		//进口国
		List<String> importCountryList=new ArrayList<String>();
		importCountryList.add("US");
		importCountryList.add("AU");
		importCountryList.add("GB");
		importCountryList.add("DE");
		importCountryList.add("CZ");
		importCountryList.add("ES");
		importCountryList.add("IT");
		importCountryList.add("FR");
		
		for (CommoditySpec comm : commList) {
			//过滤不是已上架、待上架的sku
			if (comm.getState().intValue() != 3 && comm.getState().intValue() != 1) {
				continue;
			}
			
			//如果type没有传，则查询推送记录 根据最新结果判断调哪个接口
			String serviceName = "";
			Integer optType=null;
			if (type==1) {
				serviceName=CommonConstant.GC_ADD_PRODUCT;
				optType=1;
				//如果是用新建接口推，则过滤已推过且推送成功o的
				Map<String, Object> recordParam=new HashMap<String, Object>();
				recordParam.put("accountId", accountId);
				recordParam.put("systemSku", comm.getSystemSku());
				recordParam.put("pushState", 1);
				List<SkuPushRecord> recordList=goodCangMapper.querySkuPushRecord(recordParam);
				if(recordList != null && recordList.size()>0) {
					continue;
				}
			}else if (type==2) {
				serviceName=CommonConstant.GC_EDIT_PRODUCT;
				optType=2;
			}else {
				log.error("调用类型type非法");
				return null;
			}
			
			totalNum.incrementAndGet();//开始统计总条数
			
			GoodCangProduct product=new GoodCangProduct();
			product.setProduct_sku(comm.getSystemSku());
			product.setProduct_name_cn(comm.getCommodityNameCn());
			product.setProduct_name_en(comm.getCommodityNameEn());
			
			//2019-05-28 杜典要求改为推送包装尺寸和重量
			if (comm.getPackingWeight() != null) {
				product.setProduct_weight(comm.getPackingWeight().divide(new BigDecimal(1000),3,BigDecimal.ROUND_DOWN).floatValue());//g转kg,最多三位小数
			}else {
				log.debug("包装重量为null，systemSku="+comm.getSystemSku()+",未进行推送，写入推送失败日志");
				CodeAndValueVo cv=new CodeAndValueVo();
				cv.setCode(comm.getSystemSku());
				cv.setValue("推送失败，sku重量不能为空");
				unPushSkuList.add(cv);
				failNum.incrementAndGet();
				continue;
			}
			if (comm.getPackingLength() != null) {
				product.setProduct_length(comm.getPackingLength().floatValue());
			}else {
				log.debug("包装长度为null，systemSku="+comm.getSystemSku()+",未进行推送，写入推送失败日志");
				CodeAndValueVo cv=new CodeAndValueVo();
				cv.setCode(comm.getSystemSku());
				cv.setValue("推送失败，sku长度不能为空");
				unPushSkuList.add(cv);
				failNum.incrementAndGet();
				continue;
			}
			if (comm.getPackingWidth() != null) {
				product.setProduct_width(comm.getPackingWidth().floatValue());
			}else {
				log.debug("包装宽度为null，systemSku="+comm.getSystemSku()+",未进行推送，写入推送失败日志");
				CodeAndValueVo cv=new CodeAndValueVo();
				cv.setCode(comm.getSystemSku());
				cv.setValue("推送失败，sku宽度不能为空");
				unPushSkuList.add(cv);
				failNum.incrementAndGet();
				continue;
			}
			if (comm.getPackingHeight() != null) {
				product.setProduct_height(comm.getPackingHeight().floatValue());
			}else {
				log.debug("包装高度为null，systemSku="+comm.getSystemSku()+",未进行推送，写入推送失败日志");
				CodeAndValueVo cv=new CodeAndValueVo();
				cv.setCode(comm.getSystemSku());
				cv.setValue("推送失败，sku高度不能为空");
				unPushSkuList.add(cv);
				failNum.incrementAndGet();
				continue;
			}
			// 2019-09-06 杜典 要求海关价没有就不给推 
			if (comm.getCustomsPrice() == null) {
				CodeAndValueVo cv=new CodeAndValueVo();
				cv.setCode(comm.getSystemSku());
				cv.setValue("推送失败，海关价格不能为空");
				unPushSkuList.add(cv);
				failNum.incrementAndGet();
				continue;
			}
			
			//货物属性，0普货，1含电池，2纯电池，3纺织品，4易碎品，默认为0
			if (comm.getProductLogisticsAttributes().contains("纯电池")){
				product.setContain_battery(2);
			}else if (comm.getProductLogisticsAttributes().contains("易碎品")) {
				product.setContain_battery(4);
			}else if (comm.getProductLogisticsAttributes().contains("电池内置") 
					|| comm.getProductLogisticsAttributes().contains("带纽扣电池") || comm.getProductLogisticsAttributes().contains("超大电池")) {
				product.setContain_battery(1);
			}else {
				product.setContain_battery(0);
			}
			//包裹类型。 0包裹，1信封
			product.setType_of_goods(0);
			//商品价转美元
    		product.setProduct_declared_value(comm.getCommodityPriceUs().floatValue());
			
			GoodCangCategoryBind bind=goodCangMapper.getCategoryBindByCategoryId(comm.getCategoryLevel3().intValue());
			if (bind==null) {
				log.debug("未能获取谷仓商品分类与品连商品分类的绑定关系，systemSku="+comm.getSystemSku()+",未进行推送，写入推送失败日志");
				CodeAndValueVo cv=new CodeAndValueVo();
				cv.setCode(comm.getSystemSku());
				cv.setValue("推送失败，分类映射不存在");
				unPushSkuList.add(cv);
				failNum.incrementAndGet();
				continue;
			}
			product.setCat_id_level2(bind.getGranaryCategoty3Id());
			product.setVerify(1);
			product.setBranded(0);
			product.setProduct_link(domainUrl+"/newGoodsDetail/"+comm.getCategoryLevel1()+"-"+comm.getCategoryLevel2()+"-"+comm.getCategoryLevel3()+"-"+comm.getCommodityId());
			
			/*if (comm.getCustomsPrice() == null) {
				//如果无海关报价，则用商品价格USD
				comm.setCustomsPrice(comm.getCommodityPriceUs());
			}*/
			//出口
			JSONArray export_country=new JSONArray();
			JSONObject export_country1=new JSONObject();
			export_country1.put("country_code", "CN");
			export_country1.put("declared_value", comm.getCustomsPrice());//海关报价原本已是USD，不用转
			export_country.add(export_country1);
			
			//进口
			JSONArray import_country=new JSONArray();
			for (String c : importCountryList) {
				JSONObject obj = new JSONObject();
				obj.put("country_code", c);
				obj.put("declared_value", comm.getCustomsPrice());
				import_country.add(obj);
			}

			product.setExport_country(export_country);
			product.setImport_country(import_country);
			
			
			//谷仓接口调用
			try {
				String jsonString = JSONObject.toJSONString(product);
				log.info("商品推送谷仓参数====>"+"账号="+accountId+"，请求参数json="+jsonString);
				JSONObject callService = JSONObject.parseObject(granaryUtils.getInstance(appToken,appKey, goodCangUrl, jsonString, serviceName).getCallService());
				
				int pushState=0;
				String returnMsg="";
				String productState="";
				if ("Success".equals(callService.getString("ask"))) {
					//推送成功，增加推送记录，有则更新，无则新增
					pushState=1;
					productState="W";
					successNum.incrementAndGet();
				}else {
					failNum.incrementAndGet();
					returnMsg=callService.getString("message");
				}
				
				if (callService != null) {
					//插入推送记录和日志，事务分离，边推，边返回结果
					skuImportService.inserPushRecordAndLog(warehouseProviderCode, accountId, 
							comm.getSystemSku(), pushState, optType, optUser, returnMsg,productState);
				}
			}catch (DuplicateKeyException de) {	
				log.error("推送商品到谷仓重复插入record，并发引起，不用作处理",de);
			} catch (Exception e) {
				log.error("推送商品到谷仓异常",e);
			}
		}
		
		//推前拦截数据插入记录和操作日志
		if (unPushSkuList.size()>0) {
			insertRecordAndLog(warehouseProviderCode,accountId,unPushSkuList,optUser);
		}
		
		resultMap.put("total", totalNum);
		resultMap.put("success", successNum);
		resultMap.put("fail", failNum);
		log.info("推送结果："+JSON.toJSONString(resultMap));
		return resultMap;
	}
	
	
	private void insertRecordAndLog(String warehouseProviderCode,Integer accountId,List<CodeAndValueVo> unPushSkuList,String optUser) {
		for (CodeAndValueVo cv : unPushSkuList) {
			SkuPushRecord record=new SkuPushRecord();
			record.setPushState(0);//推送失败
			Map<String, Object> recordParam=new HashMap<String, Object>();
			recordParam.put("warehouseProviderCode", warehouseProviderCode);
			recordParam.put("accountId", accountId);
			recordParam.put("systemSku", cv.getCode());
			List<SkuPushRecord> recordList=goodCangMapper.querySkuPushRecord(recordParam);
			if (recordList==null || recordList.size()==0) {
				record.setWarehouseProviderCode(warehouseProviderCode);
				record.setAccountId(accountId);
				record.setSystemSku(cv.getCode());
				goodCangMapper.insertSkuPushRecord(record);
			}else {
				record.setId(recordList.get(0).getId());
				record.setVersion(recordList.get(0).getVersion());
				goodCangMapper.updateSkuPushRecord(record);
			}
			
			//增加推送操作日志
			SkuPushLog pushLogg=new SkuPushLog();
			pushLogg.setRecordId(record.getId());
			pushLogg.setOptType(1);
			pushLogg.setOptUser(optUser);
			pushLogg.setContent(cv.getValue());
			goodCangMapper.insertSkuPushLog(pushLogg);
		}
	}


	@Override
	public Page<SkuPushRecord> getSkuPushRecordPage(Map<String, Object> param) {
		Page.builder((String) param.get("page"), (String) param.get("row"));
		List<SkuPushRecord> resultList = goodCangMapper.querySkuPushRecord(param);
		if (resultList != null && resultList.size() > 0) {
			for (SkuPushRecord record : resultList) {
				record.setWarehouseProviderName(Utils.translation(WarehouseFirmEnum.getNameByCode(record.getWarehouseProviderCode())));
				
				String account=(String) redisUtils.get(RedisKeyConstant.KEY_ACCOUNT_+record.getAccountId());
				if (StringUtils.isBlank(account)) {
					Map<String, String> map=getAppkeyAndTokenByAccountId(record.getAccountId());
					if (map != null) {
						account=map.get("account");
						redisUtils.set(RedisKeyConstant.KEY_ACCOUNT_+record.getAccountId(), account, 86400L);//1day
					}
				}
				record.setAccount(account);
			}
		}
		PageInfo pageInfo = new PageInfo(resultList);
		return new Page(pageInfo);
	}


	@Override
	public Page<SkuPushLog> querySkuPushLog(Map<String, Object> param) {
		 Page.builder((String) param.get("page"), (String) param.get("row"));
		 List<SkuPushLog> resultList=goodCangMapper.querySkuPushLog(param);
		 if (resultList != null && resultList.size()>0) {
			for (SkuPushLog skuPushLog : resultList) {
				skuPushLog.setContent(Utils.translation(skuPushLog.getContent()));
			}
		}
		 PageInfo pageInfo = new PageInfo(resultList);
	     return new Page(pageInfo);
	}


	@Override
	public Map<String, Object> pushAll(Integer accountId,Integer status) {
		Map<String, Object> skuMap=new HashMap<String, Object>();
		Long supplierId=null;
		String optUser="";
		UserDTO userDto=getLoginUserInformationByToken.getUserDTO();
        if (userDto!=null) {
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(userDto.getPlatformType())) {//供应商平台
        		if (!userDto.getManage()) {//非主账号
        			skuMap.put("supplierId", Long.valueOf(userDto.getTopUserId()));
        			supplierId=Long.valueOf(userDto.getTopUserId());
    			}else {
    				skuMap.put("supplierId", Long.valueOf(userDto.getUserId()));
    				supplierId=Long.valueOf(userDto.getUserId());
				}
        	}
        }else {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取当前登录供应商ID失败");
		}
        optUser=userDto.getLoginName();
        
        String appToken="";
		String appKey="";
		String warehouseProviderCode="";
		Map<String, String> providerServiceInfo=getAppkeyAndTokenByAccountId(accountId);
		if (providerServiceInfo != null) {
			appToken=providerServiceInfo.get("appToken");
			appKey=providerServiceInfo.get("appKey");
			warehouseProviderCode=providerServiceInfo.get("warehouseProviderCode");
		}
		
		if (StringUtils.isBlank(appToken) || StringUtils.isBlank(appKey)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取账号"+accountId+"的appToken或appKey为空");
		}
		
        skuMap.put("accountId", accountId);
        skuMap.put("status", status);
		int total=commoditySpecMapper.getUnPushSystemSkuNum(skuMap);
		
		if (total>0) {
			final Long finalSupplierId=supplierId;
			final String finalOptUser=optUser;
			final String finalWarehouseProviderCode=warehouseProviderCode;
			new Thread(){
				public void run() {
					if (WarehouseFirmEnum.WMS.getCode().equals(finalWarehouseProviderCode)) {
						wmsPushService.addAllProductByPage(accountId, total, finalSupplierId, finalOptUser,status);
					}else {
						addAllProductByPage(accountId, total, finalSupplierId, finalOptUser,status);
					}
				}
			}.start();
		}
		
		Map<String, Object> map=new HashMap<>();
		map.put("total", total);
		return map;
	}


	@Override
	public void pushBatch(List<Long> ids) {
		//获取当前登录用户
		String optUser=getLoginUserInformationByToken.getUserInfo().getUser().getUsername();
		for (Long id : ids) {
			Map<String, Object> param=new HashMap<String, Object>();
			param.put("id", id);
			List<SkuPushRecord> recordList=goodCangMapper.querySkuPushRecord(param);
			if (recordList != null && recordList.size()>0) {
				Integer accountId=recordList.get(0).getAccountId();
				String warehouseProviderCode="";
				Map<String, String> providerServiceInfo=getAppkeyAndTokenByAccountId(accountId);
				if (providerServiceInfo != null) {
					warehouseProviderCode=providerServiceInfo.get("warehouseProviderCode");
				}
				
				List<String> skuList=new ArrayList<String>();
				skuList.add(recordList.get(0).getSystemSku());
				List<CommoditySpec> commoditySpecs=commoditySpecMapper.selectCommoditySpecBySku(skuList);
				int type=recordList.get(0).getPushState()+1;//推送结果，0：推送失败，1：推送成功。推送失败的用新建接口(1)，推送成功的用编辑接口(2)
				try {
					if (WarehouseFirmEnum.WMS.getCode().equals(warehouseProviderCode)) {
						// 推送WMS直接是可用，且可以重复推，不管状态
						wmsPushService.addProduct(accountId, type, commoditySpecs, optUser);
					}else {
						// 草稿和审核不通过的才可以重复推
						if (StringUtils.isNotBlank(recordList.get(0).getProductState())) {
							if (!"D".equals(recordList.get(0).getProductState()) && !"R".equals(recordList.get(0).getProductState())) {
								continue;
							}
						}
						pushSkusToGoodCang(accountId,type,commoditySpecs,optUser);
					}
				} catch (Exception e) {
					// do nothing
				}
				
			}
		}
	}
	
	/**
	 * @Description:推送选中的商品到谷仓
	 * @param accountId 账号ID
	 * @param type  接口类型:1新增，2编辑
	 * @param skuList 品连sku数组
	 * @return void
	 * @author:范津
	 */
	@Override
	public Map<String, Object> pushSelectedSkusToGoodCang(Integer accountId, int type,List<String> skuList) {
		Map<String, Object> resultMap=new HashMap<String, Object>();
		int totalNum=0;//推送的总数
		
		if (skuList==null || skuList.size()==0) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
		}
		List<CommoditySpec> commList=commoditySpecMapper.selectCommoditySpecBySku(skuList);
		if(commList==null || commList.size()==0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku不存在");
		
		String warehouseProviderCode="";
		String appToken="";
		String appKey="";
		Map<String, String> providerServiceInfo=getAppkeyAndTokenByAccountId(accountId);
		if (providerServiceInfo != null) {
			appToken=providerServiceInfo.get("appToken");
			appKey=providerServiceInfo.get("appKey");
			warehouseProviderCode=providerServiceInfo.get("warehouseProviderCode");
		}
		
		if (StringUtils.isBlank(appToken) || StringUtils.isBlank(appKey)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取账号Id"+accountId+"的appToken或appKey为空");
		}
		
		//获取当前登录用户
		String optUser=getLoginUserInformationByToken.getUserInfo().getUser().getUsername();
		
		List<String> skuList2=new ArrayList<String>();
		for (CommoditySpec comm : commList) {
			//过滤不是已上架、待上架的sku
			if (comm.getState().intValue() != 3 && comm.getState().intValue() != 1) {
				continue;
			}
			
			if (type==1) {
				//如果是用新建接口推，则过滤已推过且推送成功的
				Map<String, Object> recordParam=new HashMap<String, Object>();
				recordParam.put("accountId", accountId);
				recordParam.put("systemSku", comm.getSystemSku());
				recordParam.put("pushState", 1);
				List<SkuPushRecord> recordList=goodCangMapper.querySkuPushRecord(recordParam);
				if(recordList != null && recordList.size()>0) {
					continue;
				}
			}
			skuList2.add(comm.getSystemSku());
			totalNum++;//统计总条数
		}
		if (totalNum>0) {
			List<CommoditySpec> commoditySpecs=commoditySpecMapper.selectCommoditySpecBySku(skuList2);
			final String finalOptUser=optUser;
			final String finalWarehouseProviderCode=warehouseProviderCode;
			new Thread(){
				public void run() {
					if (WarehouseFirmEnum.WMS.getCode().equals(finalWarehouseProviderCode)) {
						wmsPushService.addProduct(accountId, type, commoditySpecs, finalOptUser);
					}else {
						pushSkusToGoodCang(accountId,type,commoditySpecs,finalOptUser);
					}
				}
			}.start();
		}
		resultMap.put("total", totalNum);
		
		return resultMap;
	}
	
	/**
	 * @Description:根据仓库服务商ID获取服务商信息
	 * @param warehouseProviderId
	 * @return
	 * @author:范津
	 */
	@Override
	public Map<String, String> getAppkeyAndTokenByAccountId(Integer accountId) {
		Map<String, String> result=new HashMap<String, String>();
		String warehouseProviderCode="";
		String appKey="";
		String appToken="";
		String account="";
		
		RemoteUtil.invoke(remoteSupplierService.getByFirmId(accountId));
		Map map = RemoteUtil.getMap();
		if (map != null) {
			warehouseProviderCode=(String) map.get("firmCode");
			appKey=(String) map.get("appKey");
			appToken=(String) map.get("appToken");
			account=(String) map.get("name");
		}
		result.put("warehouseProviderCode", warehouseProviderCode);
		result.put("appKey", appKey);
		result.put("appToken", appToken);
		result.put("account", account);
		return result;
	}

}
