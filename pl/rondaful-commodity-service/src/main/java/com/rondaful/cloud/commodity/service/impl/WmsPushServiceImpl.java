package com.rondaful.cloud.commodity.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.commodity.constant.CommonConstant;
import com.rondaful.cloud.commodity.dto.WmsCategory;
import com.rondaful.cloud.commodity.dto.WmsProduct;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.remote.RemoteUserService;
import com.rondaful.cloud.commodity.service.GoodCangService;
import com.rondaful.cloud.commodity.service.SkuImportService;
import com.rondaful.cloud.commodity.service.WmsPushService;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.RemoteUtil;

@Service
public class WmsPushServiceImpl implements WmsPushService {
	private final static Logger log = LoggerFactory.getLogger(WmsPushServiceImpl.class);
	
	@Autowired
	private CommoditySpecMapper commoditySpecMapper;
	
	@Value("${domain.url}")
	private String domainUrl;
	
	@Autowired
	private GoodCangService goodCangService;
	
	@Autowired
    private RemoteUserService remoteUserService;
	
	@Value("${brandslink.wms.url}")
	private String wmsUrl;
	
	@Autowired
	private SkuImportService skuImportService;
	
	
	@Override
	public void addCategory(String appKey,String appToken,List<WmsCategory> categoryList) {
		log.info("推送分类到wms入参--->{}", JSON.toJSON(categoryList).toString());
		try {
			if (StringUtils.isNotBlank(appToken) && StringUtils.isNotBlank(appKey)) {
				String methodType=CommonConstant.WMS_ADD_CATEGORY;
				URIBuilder uri=new URIBuilder(wmsUrl+methodType);
				uri.addParameter("customerAppId", appKey);
				uri.addParameter("sign", appToken);
				String resp=HttpUtil.wmsPost(uri.toString(), categoryList);
				if (resp != null) {
					JSONObject resultJson=JSONObject.parseObject(resp);
					if (resultJson != null) {
						if (ResponseCodeEnum.RETURN_CODE_100200.getCode().equals(resultJson.get("errorCode"))) {
							log.info("推送分类到wms成功");
						}else {
							log.info("推送分类到wms失败{}",resultJson.get("msg"));
						}
					}
				} else {
					log.info("推送分类到wms返回null");
				}
			}
		} catch (Exception e) {
			log.error("推送分类到wms异常", e);
		}
	}

	@Override
	public void updateCategory(String appKey,String appToken,WmsCategory category) {
		log.info("更新分类到wms入参--->{}", JSON.toJSON(category).toString());
		try {
			if (StringUtils.isNotBlank(appToken) && StringUtils.isNotBlank(appKey)) {
				String methodType=CommonConstant.WMS_UPDATE_CATEGORY;
				URIBuilder uri=new URIBuilder(wmsUrl+methodType);
				uri.addParameter("customerAppId", appKey);
				uri.addParameter("sign", appToken);
				String resp=HttpUtil.wmsPost(uri.toString(), category);
				if (resp != null) {
					JSONObject resultJson=JSONObject.parseObject(resp);
					if (resultJson != null) {
						if (ResponseCodeEnum.RETURN_CODE_100200.getCode().equals(resultJson.get("errorCode"))) {
							log.info("更新分类到wms成功");
						}else {
							log.info("更新分类到wms失败{}",resultJson.get("msg"));
						}
					}
				} else {
					log.info("更新分类到wms返回null");
				}
			}
		} catch (Exception e) {
			log.error("更新分类到wms异常", e);
		}
	}
	
	@Override
	public void addAllProductByPage(Integer accountId,int total,Long supplierId,String optUser,Integer status) {
		log.info("推送全部sku到wms开始====>{}",total);
		int pages=total%500==0 ? total/500 : total/500+1;
		for (int i = 1; i <= pages; i++) {
			Map<String, Object> skuMap=new HashMap<String, Object>();
	        skuMap.put("supplierId", supplierId);
	        skuMap.put("accountId", accountId);
	        skuMap.put("status", status);
	        Page.builder(String.valueOf(i), "500");
			List<CommoditySpec> commoditySpecs=commoditySpecMapper.getUnPushSystemSku(skuMap);
			if (commoditySpecs != null && commoditySpecs.size()>0) {
				addProduct(accountId,1,commoditySpecs,optUser);
			}
		}
	}
	

	@Override
	public Map<String, Object> addProduct(Integer accountId,int type,List<CommoditySpec> commoditySpecs,String optUser) {
		Map<String, Object> resultMap=new HashMap<String, Object>();
		AtomicInteger failNum=new AtomicInteger(0);//失败的数量
		AtomicInteger successNum=new AtomicInteger(0);//成功的数量
		AtomicInteger totalNum=new AtomicInteger(0);//推送的总数
		try {
			String appToken="";
			String appKey="";
			String warehouseProviderCode="";
			Map<String, String> providerServiceInfo=goodCangService.getAppkeyAndTokenByAccountId(accountId);
			if (providerServiceInfo != null) {
				appToken=providerServiceInfo.get("appToken");
				appKey=providerServiceInfo.get("appKey");
				warehouseProviderCode=providerServiceInfo.get("warehouseProviderCode");
			}
			
			if (StringUtils.isBlank(appToken) || StringUtils.isBlank(appKey)) {
				log.error("获取账号"+accountId+"的appToken或appKey为空");
				return null;
			}
			
			if (commoditySpecs != null && commoditySpecs.size()>0) {
				//获取供应商名称
				//constructionSupplierForSpec(commoditySpecs);
				for (CommoditySpec commoditySpec : commoditySpecs) {
					//过滤不是已上架、待上架的sku
					if (commoditySpec.getState().intValue() != 3 && commoditySpec.getState().intValue() != 1) {
						continue;
					}
					//组装接口参数
					List<WmsProduct> productList=initAddProductParam(commoditySpec);
					if (productList != null && productList.size()>0) {
						Integer optType=null;
						String methodType="";
						String resp = null;
						log.info("推送商品到wms入参--->{}", JSON.toJSON(productList).toString());
						totalNum.incrementAndGet();//开始统计总条数
						
						// wms返回信息，新增是返回在data,更新是返回在msg
						if (type==1) {//新增
							optType=1;
							methodType=CommonConstant.WMS_ADD_PRODUCT;
							
							URIBuilder uri=new URIBuilder(wmsUrl+methodType);
							uri.addParameter("customerAppId", appKey);
							uri.addParameter("sign", appToken);
							resp=HttpUtil.wmsPost(uri.toString(), productList);
							
							if (resp != null) {
								int pushState=0;
								String productState="";
								JSONObject resultJson=JSONObject.parseObject(resp);
								if (resultJson != null) {
									if (ResponseCodeEnum.RETURN_CODE_100200.getCode().equals(resultJson.get("errorCode")) 
											&& resultJson.get("data")==null) {
										//成功
										pushState=1;
										successNum.incrementAndGet();
										productState="S";
									}else {
										//失败
										failNum.incrementAndGet();
									}
									String resultData="";
									if (resultJson.get("data") != null) {
										resultData=resultJson.get("data").toString();
									}else if (resultJson.get("msg") != null) {
										resultData=resultJson.get("msg").toString();
									}
									
									//插入推送记录和日志，事务分离，边推，边返回结果
									skuImportService.inserPushRecordAndLog(warehouseProviderCode, accountId, 
											commoditySpec.getSystemSku(), pushState, optType, optUser, resultData,productState);
								}
							} else {
								log.info("推送商品到wms返回null");
							}
							
						}else if (type==2) {//编辑
							optType=2;
							methodType=CommonConstant.WMS_UPDATE_PRODUCT;
							
							URIBuilder uri=new URIBuilder(wmsUrl+methodType);
							uri.addParameter("customerAppId", appKey);
							uri.addParameter("sign", appToken);
							resp=HttpUtil.wmsPost(uri.toString(), productList.get(0));
							
							if (resp != null) {
								int pushState=0;
								String productState="";
								JSONObject resultJson=JSONObject.parseObject(resp);
								if (resultJson != null) {
									if (ResponseCodeEnum.RETURN_CODE_100200.getCode().equals(resultJson.get("errorCode"))) {
										//成功
										pushState=1;
										successNum.incrementAndGet();
										productState="S";
									}else {
										//失败
										failNum.incrementAndGet();
									}
									String resultData=resultJson.get("msg").toString();
									//插入推送记录和日志，事务分离，边推，边返回结果
									skuImportService.inserPushRecordAndLog(warehouseProviderCode, accountId, 
											commoditySpec.getSystemSku(), pushState, optType, optUser, resultData,productState);
								}
							} else {
								log.info("推送商品到wms返回null");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("推送商品到wms异常", e);
		}
		
		resultMap.put("total", totalNum);
		resultMap.put("success", successNum);
		resultMap.put("fail", failNum);
		return resultMap;
	}
	
	private List<WmsProduct> initAddProductParam(CommoditySpec commoditySpec) {
		List<WmsProduct> productList=new ArrayList<WmsProduct>();
		WmsProduct product=new WmsProduct();
		product.setProductSku(commoditySpec.getSystemSku());
		product.setProductPictures(commoditySpec.getMasterPicture().split("\\|")[0]);
		product.setProductName(commoditySpec.getCommodityNameCn());
		if (product.getProductName().length()>500) {
			product.setProductName(product.getProductName().substring(0, 500));
		}
		if (commoditySpec.getCommodityWeight() != null) {
			product.setProductWeight(commoditySpec.getCommodityWeight().floatValue());
		}else {
			product.setProductWeight(commoditySpec.getPackingWeight().floatValue());
		}
		product.setProductBrand(StringUtils.isBlank(commoditySpec.getBrandName()) ? "无品牌" : commoditySpec.getBrandName());
		product.setLogisticsAttribute(commoditySpec.getProductLogisticsAttributes());
		if (commoditySpec.getCustomsPrice() != null) {
			product.setCustomsPrice(commoditySpec.getCustomsPrice());
		}
		product.setProductLink(domainUrl+"/newGoodsDetail/"
				+commoditySpec.getCategoryLevel1()+"-"
				+commoditySpec.getCategoryLevel2()+"-"
				+commoditySpec.getCategoryLevel3()+"-"
				+commoditySpec.getCommodityId());
		if (StringUtils.isNotBlank(commoditySpec.getCustomsNameCn())) {
			product.setDeclareCustomsCn(commoditySpec.getCustomsNameCn());
		}else {
			product.setDeclareCustomsCn(commoditySpec.getCommodityNameCn());
		}
		if (product.getDeclareCustomsCn().length()>500) {
			product.setDeclareCustomsCn(product.getDeclareCustomsCn().substring(0, 500));
		}
		if (StringUtils.isNotBlank(commoditySpec.getCustomsNameEn())) {
			product.setDeclareCustomsEn(commoditySpec.getCustomsNameEn());
		}else {
			product.setDeclareCustomsEn(commoditySpec.getCommodityNameEn());
		}
		if (product.getDeclareCustomsEn().length()>500) {
			product.setDeclareCustomsEn(product.getDeclareCustomsEn().substring(0, 500));
		}
		product.setCustomsCode(commoditySpec.getCustomsCode());
		product.setProductSpu(commoditySpec.getSPU());
		product.setCategoryCode(commoditySpec.getCategoryLevel3());
		product.setPackageWeight(commoditySpec.getPackingWeight().doubleValue());
		if (commoditySpec.getCommodityLength() != null) {
			product.setProductLength(commoditySpec.getCommodityLength().doubleValue()*10);
		}else {
			product.setProductLength(commoditySpec.getPackingLength().doubleValue()*10);
		}
		if (commoditySpec.getCommodityWidth() != null) {
			product.setProductWidth(commoditySpec.getCommodityWidth().doubleValue()*10);
		}else {
			product.setProductWidth(commoditySpec.getPackingWidth().doubleValue()*10);
		}
		if (commoditySpec.getCommodityHeight() != null) {
			product.setProductHeight(commoditySpec.getCommodityHeight().doubleValue()*10);
		}else {
			product.setProductHeight(commoditySpec.getPackingHeight().doubleValue()*10);
		}
		product.setPackageLength(commoditySpec.getPackingLength().doubleValue()*10);
		product.setPackageWidth(commoditySpec.getPackingWidth().doubleValue()*10);
		product.setPackageHeight(commoditySpec.getPackingHeight().doubleValue()*10);
		product.setProductAttribute(commoditySpec.getCommoditySpec());
		product.setDataSources("1");
		//product.setShipper(commoditySpec.getSupplierId());
		//product.setShipperName(commoditySpec.getSupplierCompanyName());
		productList.add(product);
		return productList;
	}
	
	 public void constructionSupplierForSpec(List<CommoditySpec> commoditySpecs) {
        try {
            Set<Long> list = new HashSet<Long>() {{
                for (CommoditySpec cs : commoditySpecs) {
                    this.add(Long.valueOf(cs.getSupplierId()));
                }
            }};
            RemoteUtil.invoke(remoteUserService.getSupplierList(list, 0));
            List<Map> result = RemoteUtil.getList();
            if (result != null && !result.isEmpty()) {
                for (CommoditySpec cs : commoditySpecs) {
                    for (int i = 0; i < result.size(); i++) {
                        if (Integer.valueOf(cs.getSupplierId()).equals((Integer) ((Map) result.get(i)).get("userId"))) {
                            cs.setSupplierName((String) ((Map) result.get(i)).get("loginName"));
                            cs.setSupplierCompanyName((String) ((Map) result.get(i)).get("companyName"));
                            String supplyChainCompany = (String) ((Map) result.get(i)).get("supplyChainCompany");
                            if (StringUtils.isNotBlank(supplyChainCompany)) {
                                cs.setSupChainCompanyId(Integer.parseInt(supplyChainCompany));
                            }
                            cs.setSupChainCompanyName((String) ((Map) result.get(i)).get("supplyChainCompanyName"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
