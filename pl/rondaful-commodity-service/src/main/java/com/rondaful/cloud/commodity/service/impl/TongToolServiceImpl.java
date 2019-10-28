package com.rondaful.cloud.commodity.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.commodity.constant.CommonConstant;
import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.CommodityDetails;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.SellerAuth;
import com.rondaful.cloud.commodity.entity.SpuPushRecord;
import com.rondaful.cloud.commodity.entity.SystemSpu;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.mapper.CommodityBaseMapper;
import com.rondaful.cloud.commodity.mapper.CommodityDetailsMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.mapper.SellerAuthMapper;
import com.rondaful.cloud.commodity.mapper.SpuPushRecordMapper;
import com.rondaful.cloud.commodity.mapper.SystemSpuMapper;
import com.rondaful.cloud.commodity.service.TongToolService;
import com.rondaful.cloud.commodity.utils.MD5;
import com.rondaful.cloud.commodity.vo.TongToolDetailDescriptions;
import com.rondaful.cloud.commodity.vo.TongToolGoods;
import com.rondaful.cloud.commodity.vo.TongToolGoodsVariation;
import com.rondaful.cloud.commodity.vo.TongToolProduct;
import com.rondaful.cloud.commodity.vo.TongToolResponse;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;

@Service
public class TongToolServiceImpl implements TongToolService {
	
	private final static Logger log = LoggerFactory.getLogger(TongToolServiceImpl.class);
	
	@Autowired
	private CommodityDetailsMapper commodityDetailsMapper;

	@Value("${tongtool.url}")
	private String tongtoolUrl;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CommodityBaseMapper commodityBaseMapper;
	
	@Autowired
	private CommoditySpecMapper commoditySpecMapper;
	
	@Autowired
	private SystemSpuMapper systemSpuMapper;
	
	@Autowired
	private SpuPushRecordMapper spuPushRecordMapper;
	
	@Autowired
	private SellerAuthMapper sellerAuthMapper;
	
	
	@Override
	public void pushAllByPage(Map<String,Object> param) {
		Long sellerId=(Long)param.get("sellerId");
		param.remove("sellerId");
		int total=commodityBaseMapper.selectExportCount(param);
		if (total>0) {
			int pages=total%100==0 ? total/100 : total/100+1;
			for (int i = 1; i <= pages; i++) {
				Page.builder(String.valueOf(i), "100");
				List<CommodityBase> baseList=commodityBaseMapper.selectCommodityListBySpec(param);
				if (baseList != null && baseList.size()>0) {
					pushToTongTool(baseList,sellerId);
				}
			}
		}
	}
	
	@Override
	public Map<String, Object> pushToTongTool(List<CommodityBase> baseList,Long sellerId) {
		Map<String, Object> resultMap=new HashMap<String, Object>();
		AtomicInteger failNum=new AtomicInteger(0);//失败的数量
		AtomicInteger successNum=new AtomicInteger(0);//成功的数量
		AtomicInteger totalNum=new AtomicInteger(0);//推送的总数
		try {
			SellerAuth authInfo=sellerAuthMapper.selectBySellerId(sellerId);
			if (authInfo == null || StringUtils.isBlank(authInfo.getAppKey()) || StringUtils.isBlank(authInfo.getAppSecret())) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请先进行通途授信息权的绑定");
			}
			String app_key=authInfo.getAppKey();
			String app_secret=authInfo.getAppSecret();
			Long timestamp=System.currentTimeMillis();
			String appToken=getAppToken(app_key,app_secret);
			String sign=createSign(appToken,timestamp,app_secret);
			
			String url=tongtoolUrl+CommonConstant.TONGTOOL_POST_CREATE_PRODUCT+"?app_token="+appToken+"&timestamp="+timestamp+"&sign="+sign;
			String merchantId=getMerchantId(timestamp,appToken,app_secret);
			HttpHeaders requestHeaders = new HttpHeaders();
	        requestHeaders.add("api_version", "3.0");
	        requestHeaders.add("Content-Type", "application/json");
	        
	        CommoditySpec specParam=null;
	        SpuPushRecord record=null;
			for (CommodityBase base : baseList) {
				SystemSpu spu=systemSpuMapper.selectByPrimaryKey(base.getSpuId());
				SpuPushRecord oldRecord=spuPushRecordMapper.selectBySpu(spu.getSpuValue(),sellerId);
				if (oldRecord != null && oldRecord.getPushState()==1) {
					//之前推送成功的，过滤
					continue;
				}
				
	        	CommodityDetails commodityDetail = commodityDetailsMapper.selectByCommodityId(base.getId());
	        	specParam=new CommoditySpec();
	        	specParam.setState(3);
	        	specParam.setCommodityId(base.getId());
	        	List<CommoditySpec> sepcList=commoditySpecMapper.page(specParam);
	        	if (commodityDetail != null && spu != null && sepcList != null && sepcList.size()>0) {
	        		TongToolProduct product=initParam(merchantId,spu,sepcList,commodityDetail);
	        		log.info(JSON.toJSONString(product));
		        	TongToolResponse response=restTemplate.postForObject(url, product, TongToolResponse.class);
		        	if (response != null) {
		        		totalNum.incrementAndGet();
		        		
		        		record=new SpuPushRecord();
		        		record.setSellerId(sellerId);
		        		record.setSpu(spu.getSpuValue());
		        		record.setSystemSku(product.getSkus());
		        		if (response.getCode()==200) {
		        			record.setPushState(1);
		        			record.setContent("推送成功");
		        			successNum.incrementAndGet();
						}else {
							record.setPushState(0);
		        			record.setContent("推送失败,"+response.getMessage());
		        			failNum.incrementAndGet();
						}
		        		//记录日志
		        		if (oldRecord == null) {
			        		spuPushRecordMapper.insert(record);
						}else {
			        		record.setId(oldRecord.getId());
			        		record.setVersion(oldRecord.getVersion());
			        		spuPushRecordMapper.updateByPrimaryKeySelective(record);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("推送通途异常",e);
		}
		resultMap.put("total", totalNum);
		resultMap.put("success", successNum);
		resultMap.put("fail", failNum);
		
		return resultMap;
	}
	
	private TongToolProduct initParam(String merchantId,SystemSpu spu,List<CommoditySpec> commoditySpecs,CommodityDetails commodityDetail) {
		TongToolProduct product=new TongToolProduct();
		product.setSalesType("1");
		product.setProductStatus("1");
		product.setProductPackingName(commoditySpecs.get(0).getCommodityNameCn());
		product.setProductPackingEnName(commoditySpecs.get(0).getCommodityNameEn());
		product.setProductName(commoditySpecs.get(0).getCommodityNameCn());
		product.setProductCode(spu.getSpuValue());
		product.setMerchantId(merchantId);
		List<String> imgUrls=new ArrayList<String>();
		if (StringUtils.isNotBlank(commodityDetail.getMasterPicture())) {
			String[] imgArr=commodityDetail.getMasterPicture().split("\\|");
			if (imgArr != null && imgArr.length>0) {
				imgUrls.addAll(Arrays.asList(imgArr));
			}
		}
		if (StringUtils.isNotBlank(commodityDetail.getAdditionalPicture())) {
			String[] imgArr=commodityDetail.getAdditionalPicture().split("\\|");
			if (imgArr != null && imgArr.length>0) {
				imgUrls.addAll(Arrays.asList(imgArr));
			}
		}
		product.setImgUrls(imgUrls);
		product.setHsCode(commoditySpecs.get(0).getCustomsCode());
		product.setDeclareCnName(commoditySpecs.get(0).getCustomsNameCn());
		product.setDeclareEnName(commoditySpecs.get(0).getCustomsNameEn());
		List<TongToolDetailDescriptions> detailDescriptions=new ArrayList<TongToolDetailDescriptions>();
		if (StringUtils.isNotBlank(commodityDetail.getCommodityDesc())) {
            String obj = commodityDetail.getCommodityDesc();
            String[] arr = obj.split(":::");
            TongToolDetailDescriptions description=null;
            if (arr.length > 0) {
                for (int i = 0; i < arr.length; i++) {
                    String key = arr[i].split("===")[0];
                    if (arr[i].split("===").length > 1) {
                    	String content=arr[i].split("===")[1];
                    	description=new TongToolDetailDescriptions();
                    	if (content.length()>90) {
                    		description.setTitle(content.substring(0, 87)+"...");
						}else {
							description.setTitle(content);
						}
                    	if ("CN".equals(key)) {
                        	description.setDescLanguage("zh-cn");
                        }else if ("EN".equals(key)) {
                        	description.setDescLanguage("en-gb");
                        }else if ("FR".equals(key)) {
                        	description.setDescLanguage("fr-fr");
                        }else if ("DE".equals(key)) {
                        	description.setDescLanguage("de-de");
                        }else if ("IT".equals(key)) {
                        	description.setDescLanguage("it-it");
                        }
                    	description.setContent(content);
                    	detailDescriptions.add(description);
					}
                }
            }
        }
		product.setDetailDescriptions(detailDescriptions);
		
		StringBuilder skuSb=new StringBuilder();
		List<TongToolGoods> goods=new ArrayList<TongToolGoods>();
		for (CommoditySpec spec : commoditySpecs) {
			TongToolGoods ttGood=new TongToolGoods();
			if (StringUtils.isNotBlank(spec.getWarehousePriceGroupRmb())) {
				List<BigDecimal> priceList=new ArrayList<BigDecimal>();
				String[] priceArr=spec.getWarehousePriceGroupRmb().split("\\|");
				if (priceArr != null && priceArr.length>0) {
					for (int i = 0; i < priceArr.length; i++) {
						priceList.add(new BigDecimal(priceArr[i].split(":")[1]));
					}
				}
				if (priceList.size()>0) {
					ttGood.setGoodsCurrentCost(Collections.max(priceList));
				}
			}else {
				ttGood.setGoodsCurrentCost(spec.getCommodityPrice());
			}
			ttGood.setGoodsSku(spec.getSystemSku());
			
			if (StringUtils.isNotBlank(spec.getCommoditySpec())) {
				String[] specArr=spec.getCommoditySpec().split("\\|");
				if (specArr != null && specArr.length>0) {
					List<TongToolGoodsVariation> goodsVariationList=new ArrayList<TongToolGoodsVariation>();
					for (int i = 0; i < specArr.length; i++) {
						String[] specGroup=specArr[i].split(":");
						if (specGroup.length>1) {
							TongToolGoodsVariation variation=new TongToolGoodsVariation();
							variation.setVariationName(specGroup[0]);
							variation.setVariationValue(specGroup[1]);
							goodsVariationList.add(variation);
						}
					}
					ttGood.setGoodsVariation(goodsVariationList);
				}
			}/*else {
				List<TongToolGoodsVariation> goodsVariationList=new ArrayList<TongToolGoodsVariation>();
				TongToolGoodsVariation variation=new TongToolGoodsVariation();
				variation.setVariationName("无属性");
				variation.setVariationValue("无属性");
				goodsVariationList.add(variation);
				ttGood.setGoodsVariation(goodsVariationList);
			}*/
			if (spec.getCommodityWeight() != null) {
				ttGood.setGoodsWeight(spec.getCommodityWeight().intValue());
			}
			goods.add(ttGood);
			
			skuSb.append(spec.getSystemSku()).append("|");
		}
		if (skuSb.length()>0) {
			product.setSkus(skuSb.substring(0, skuSb.length()-1));
		}
		
		product.setGoods(goods);
		return product;
	}
	
	
	private String getMerchantId(Long timestamp,String appToken,String app_secret) {
		String merchantId="";
		String sign=createSign(appToken,timestamp,app_secret);
		String url=tongtoolUrl+CommonConstant.TONGTOOL_GET_MERCHANTID+"?app_token="+appToken+"&timestamp="+timestamp+"&sign="+sign;
		try {
			String data=HttpUtil.get(url);
			if (data != null) {
				JSONObject json=JSONObject.parseObject(data);
				if (json != null && (Boolean)json.get("success")) {
					JSONArray datArray=(JSONArray) json.get("datas");
					if (datArray != null && datArray.size()>0) {
						merchantId=((JSONObject)datArray.get(0)).getString("partnerOpenId");
					}
				}
			}
		} catch (Exception e) {
			log.error("获取通途商户ID异常",e);
		}
		return merchantId;
	}
	
	private String getAppToken(String app_key,String app_secret) {
		String result="";
		String url=tongtoolUrl+CommonConstant.TONGTOOL_GET_APP_TOKEN+"?accessKey="+app_key+"&secretAccessKey="+app_secret;
		try {
			String data=HttpUtil.get(url);
			if (data != null) {
				JSONObject json=JSONObject.parseObject(data);
				if (json != null && (Boolean)json.get("success")) {
					result=(String) json.get("datas");
				}
			}
		} catch (Exception e) {
			log.error("获取通途appToken异常",e);
		}
		return result;
	}
	
	private String createSign(String appToken,Long timestamp,String app_secret) {
		String md5Key="app_token"+appToken+"timestamp"+timestamp+app_secret;
		return MD5.md5Password(md5Key);
	}
	
}
