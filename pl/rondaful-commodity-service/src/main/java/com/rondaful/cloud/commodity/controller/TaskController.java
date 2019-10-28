package com.rondaful.cloud.commodity.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.commodity.constant.CommonConstant;
import com.rondaful.cloud.commodity.entity.CommodityDetails;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.GoodCangCategory;
import com.rondaful.cloud.commodity.entity.SkuPushRecord;
import com.rondaful.cloud.commodity.enums.WarehouseFirmEnum;
import com.rondaful.cloud.commodity.mapper.CommodityDetailsMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.mapper.GoodCangMapper;
import com.rondaful.cloud.commodity.mapper.PublishPackRecordMapper;
import com.rondaful.cloud.commodity.rabbitmq.MQSender;
import com.rondaful.cloud.commodity.remote.RemoteSupplierService;
import com.rondaful.cloud.commodity.remote.RemoteUserService;
import com.rondaful.cloud.commodity.service.CommonJestIndexService;
import com.rondaful.cloud.commodity.service.GoodCangService;
import com.rondaful.cloud.commodity.service.ICommodityService;
import com.rondaful.cloud.commodity.service.ICommonService;
import com.rondaful.cloud.commodity.thread.InitEsThread;
import com.rondaful.cloud.commodity.vo.GoodCangProduct;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.service.FileService;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.RemoteUtil;

import io.searchbox.client.JestClient;
import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @Description:定时任务-任务调度中心调用
* @author:范津 
* @date:2019年4月24日 下午3:41:57
 */
@Api(description = "定时器任务接口")
@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(TaskController.class);
	
	@Autowired
    private ICommodityService commodityService;
	
	@Autowired
	private CommonJestIndexService commonJestIndexService;
	
	@Value("${wsdl.url}")
    private String goodCangUrl;
	
	@Autowired
	private GoodCangMapper goodCangMapper;
	
	@Autowired
    private CommoditySpecMapper commoditySpecMapper;
	
	@Autowired
	private MQSender mqSender;
	
	@Autowired
	private RemoteSupplierService remoteSupplierService;
	
	@Autowired
    private GranaryUtils granaryUtils;
	
	@Autowired
	private RemoteUserService remoteUserService;
	
	@Autowired
	private CommodityDetailsMapper commodityDetailsMapper;
	
	@Autowired
    private ICommonService commonService;
	
	@Autowired
	private GoodCangService goodCangService;

	@Resource
    private JestClient jestClient;
	
	@Value("${spring.elasticsearch.jest.uris}")
    private String esUrl;
	
	@Autowired
	private PublishPackRecordMapper publishPackRecordMapper;
	
	@Resource
	private FileService fileService;
	
	
	
	@GetMapping("/delPublishPack7DaysBefore")
	public void delPublishPack7DaysBefore(){
		logger.info("定时任务，删除7天前的刊登包 start------------------------------------------->");
		List<String> urls=publishPackRecordMapper.get7DaysBefore();
		if (urls != null && urls.size()>0) {
			int result=fileService.deleteList(urls);
			if (result==1) {
				publishPackRecordMapper.deleteByTask();
			}
		}
		
		logger.info("定时任务，删除7天前的刊登包 end------------------------------------------->");
	}
	

	@GetMapping("/initCommodityEsTask")
	public void initCommodityEs(){
		logger.info("定时任务，插入商品ES start------------------------------------------->");
		new InitEsThread(commodityService,commonJestIndexService).start();
		logger.info("定时任务，插入商品ES end------------------------------------------->");
	}
	
	@GetMapping("/delCommodityEsTask")
	public void delCommodityEsTask(){
		logger.info("定时任务，清空商品ES索引 start------------------------------------------->");
		Map<String, Object> json1=new HashMap<String, Object>();
		Map<String, Object> json2=new HashMap<String, Object>();
		json2.put("match_all", new JSONObject());
		json1.put("query", json2);
		try {
			HttpUtil.postJson(esUrl+"/commodity/search/_delete_by_query?refresh&slices=5&pretty", json1);
		} catch (Exception e) {
			logger.error("商品搜索-->ES查询异常",e);
		}
		logger.info("定时任务，清空商品ES索引  end------------------------------------------->");
	}
	
	@GetMapping("/updateGoodCangCategoryTask")
	public void updateGoodCangCategory(){
		logger.info("定时任务，更新谷仓商品分类 start------------------------------------------->");
		// 对应的接口请求参数
		try {
			String appToken="";
			String appKey="";

			//获取最新的推送记录,拿账号ID去获取appKey和appToken
			Map<String, Object> param=new HashMap<String, Object>();
			param.put("warehouseProviderCode",WarehouseFirmEnum.GOODCANG.getCode());
			Page.builder(1,1);
			List<SkuPushRecord> recordList=goodCangMapper.querySkuPushRecord(param);
			
			if (recordList!=null && recordList.size()>0) {
				SkuPushRecord record=recordList.get(0);
				Map<String, String> providerServiceInfo=goodCangService.getAppkeyAndTokenByAccountId(record.getAccountId());
				if (providerServiceInfo != null) {
					appToken=providerServiceInfo.get("appToken");
					appKey=providerServiceInfo.get("appKey");
				}
			}
			if (StringUtils.isNotBlank(appToken) && StringUtils.isNotBlank(appKey)) {
				JSONObject callService = JSONObject.parseObject(granaryUtils.getInstance(appToken,appKey, 
						goodCangUrl, null, CommonConstant.GC_GET_CATEGORY).getCallService());
				
				if ("Success".equals(callService.getString("ask"))) {
					List<GoodCangCategory> goodCangCategoryList = JSONObject.parseArray(callService.getString("data"),GoodCangCategory.class);
					goodCangMapper.deleteCategory();
					goodCangMapper.insertBatchCategory(goodCangCategoryList);
				}else {
					logger.debug("定时任务，更新谷仓商品分类返回错误",callService.getString("message"));
				}
			}
		} catch (Exception e) {
			logger.error("定时任务，更新谷仓商品分类异常",e);
		}
		logger.info("定时任务，更新谷仓商品分类  end------------------------------------------->");
	}
	
	@GetMapping("/updateGoodCangSkuStatusTask")
	public void getStatus() {
		logger.info("定时任务，更新谷仓商品状态 start------------------------------------------->");
		try {
			List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
			RemoteUtil.invoke(remoteSupplierService.getAuth(1));
			List<Map> result = RemoteUtil.getList();
			if (result != null && !result.isEmpty()) {
				for (int i = 0; i < result.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", ((Map) result.get(i)).get("id"));
					map.put("code", ((Map) result.get(i)).get("code"));
					map.put("appKey", ((Map) result.get(i)).get("appKey"));
					map.put("appToken", ((Map) result.get(i)).get("appToken"));
					accountList.add(map);
				}
			}
			if (accountList.size() > 0) {
				for (Map map : accountList) {
					if (map.get("code") != null && WarehouseFirmEnum.GOODCANG.getCode().equals(map.get("code"))) {
						// 查询出推送成功，且商品状态为空或者在D:草稿，W:审核中，R:审核不通过这几种状态中的记录
						Map<String, Object> param = new HashMap<String, Object>();
						param.put("accountId", map.get("id"));
						param.put("pushState", 1);
						param.put("isUpdate", 1);
						List<SkuPushRecord> recordList = goodCangMapper.querySkuPushRecord(param);
						if (recordList != null && recordList.size() > 0) {
							int size = recordList.size();
							JSONArray skuArray = new JSONArray();
							for (int i = 0; i < size; i++) {
								skuArray.add(recordList.get(i).getSystemSku());

								if ((i + 1) % 100 == 0 || (i + 1) == size) {// 满100请求一次
									JSONObject queryJson = new JSONObject();
									queryJson.put("page", 1);
									queryJson.put("pageSize", 100);// 谷仓限制一次最多获取100条
									queryJson.put("product_sku_arr", skuArray);
									logger.info("查询谷仓商品状态接口入参====>" + queryJson.toString());
									JSONObject callService = JSONObject
											.parseObject(granaryUtils.getInstance((String) map.get("appToken"),
													(String) map.get("appKey"), goodCangUrl, queryJson.toString(),
													CommonConstant.GC_GET_PRODUCT_SKU_LIST).getCallService());
									
									logger.info("谷仓接口返回数据====>" + callService);
									if ("Success".equals(callService.getString("ask"))) {
										// 谷仓返回参数 log
										List<GoodCangProduct> goodCangProductList = JSON.parseArray(callService.getString("data"), GoodCangProduct.class);
										for (GoodCangProduct product : goodCangProductList) {
											// 如果状态是审核通过，发MQ给供应商
											if ("S".equals(product.getProduct_status())) {
												List<String> list = new ArrayList<String>();
												list.add(product.getProduct_sku());
												List<CommoditySpec> commoditySpec = commoditySpecMapper.getSystemSkuBySystemSku(list);
												if (commoditySpec != null && commoditySpec.size() > 0) {
													logger.info("推送谷仓商品通过审核，发送MQ给供应商服务====>{}",JSON.toJSON(commoditySpec).toString());
													constructionSupplierForSpec(commoditySpec);// 先构造供应商信息
													mqSender.commoditySkuAdd(net.sf.json.JSONArray.fromObject(commoditySpec).toString());
												}
											}

											// 更新推送记录里商品的状态
											Map<String, Object> recordParam = new HashMap<String, Object>();
											recordParam.put("warehouseProviderCode", WarehouseFirmEnum.GOODCANG.getCode());
											recordParam.put("accountId", map.get("id"));
											recordParam.put("systemSku", product.getProduct_sku());
											List<SkuPushRecord> skuRecordList = goodCangMapper.querySkuPushRecord(recordParam);
											if (skuRecordList != null && skuRecordList.size() > 0) {
												SkuPushRecord skuPushRecord = new SkuPushRecord();
												skuPushRecord.setId(skuRecordList.get(0).getId());
												skuPushRecord.setVersion(skuRecordList.get(0).getVersion());
												skuPushRecord.setProductState(product.getProduct_status());
												goodCangMapper.updateSkuPushRecord(skuPushRecord);
											}
										}
									} else {
										logger.error("获取谷仓商品状态失败，" + callService.getString("message"));
									}
									skuArray.clear();
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("定时任务，查询谷仓商品状态接口异常", e);
		}
		logger.info("定时任务，更新谷仓商品状态  end------------------------------------------->");
	}
	
	@GetMapping("/replaceDetailImgUrl")
    public synchronized void replaceDetailImgUrl(){
		logger.info("erp商品详情图片替换处理开始====>");
    	try {
    		Map<String, Object> param1=new HashMap<String, Object>();
    		param1.put("supplierId", 100);
    		param1.put("isImg", "Yes");
    		int detailNum=commodityDetailsMapper.getAllDetailCountBySupplierId(param1);
    		if (detailNum>0) {
    			//ExecutorService executService = Executors.newFixedThreadPool(5);
    			param1.put("size", 1000);
    			int row=detailNum/1000==0 ? 1 : detailNum/1000;
    			for (int i = 0; i < row; i++) {
    				param1.put("startInx", i*1000);
    				List<CommodityDetails> detailList=commodityDetailsMapper.getAllDetailBySupplierId(param1);
    	    		for (CommodityDetails detail : detailList) {
    	    			//executService.execute(new Thread() {
    	                 //   public void run() {
    	                    	StringBuilder masterSb = null;
    	    	    			StringBuilder additSb = null;
    	    	    			StringBuilder proveSb = null;
    	    	    			
    	    					String[] masterPictureArr=detail.getMasterPicture().split("\\|");
    	    					
    	    					if (masterPictureArr != null && masterPictureArr.length>0) {
    	    						masterSb=commonService.uploadImg(masterPictureArr);
    	    					}
    	    					
    	    					if (StringUtils.isNotBlank(detail.getAdditionalPicture())) {
    	    						String[] additionalPictureArr=detail.getAdditionalPicture().split("\\|");
    	    						if (additionalPictureArr != null && additionalPictureArr.length>0) {
    	    							additSb=commonService.uploadImg(additionalPictureArr);
    	    						}
    	    					}
    	    					
    	    					if (StringUtils.isNotBlank(detail.getProvePicture())) {
    	    						String[] provePictureArr=detail.getProvePicture().split("\\|");
    	    						if (provePictureArr != null && provePictureArr.length>0) {
    	    							proveSb=commonService.uploadImg(provePictureArr);
    	    						}
    	    					}
    	    					
    	    					if (masterSb != null && masterSb.toString().length()>0) {
    	    						CommodityDetails newDetail=new CommodityDetails();
    	    						newDetail.setId(detail.getId());
    	    						newDetail.setVersion(detail.getVersion());
    	    						newDetail.setMasterPicture(masterSb.toString().substring(0, masterSb.toString().length()-1));
    	    						if (additSb != null && additSb.toString().length()>0) {
    	    							newDetail.setAdditionalPicture(additSb.toString().substring(0, additSb.toString().length()-1));
    	    						}
    	    						if (proveSb != null && proveSb.toString().length()>0) {
    	    							newDetail.setProvePicture(proveSb.toString().substring(0, proveSb.toString().length()-1));
    	    						}
    	    						commodityDetailsMapper.updateByPrimaryKeySelective(newDetail);
    	    					}
    	                //    }
    	               // });
    				}
    	    		detailList.clear();
				}
			}
		} catch (Exception e) {
			logger.error("上传并替换sku的详情图片异常",e);
		}
    	logger.info("<======erp商品详情图片替换处理结束====");
    }
    
    @GetMapping("/replaceSkuImgUrl")
    public synchronized void replaceSkuImgUrl(){
    	logger.info("erp商品sku图片替换处理开始====>");
    	try {
    		Map<String, Object> param2=new HashMap<String, Object>();
    		param2.put("supplierId", 100);
    		param2.put("isImg", "Yes");
    		int skuNum=commoditySpecMapper.getAllSkuCountBySupplierId(param2);
    		if (skuNum>0) {
    			//ExecutorService executService = Executors.newFixedThreadPool(5);
    			param2.put("size", 1000);
    			int row=skuNum/1000==0 ? 1 : skuNum/1000;
    			for (int i = 0; i < row; i++) {
    				param2.put("startInx", i*1000);
    				List<CommoditySpec> specList=commoditySpecMapper.getAllSkuBySupplierId(param2);
    	    		for (CommoditySpec commoditySpec : specList) {
    	    			//executService.execute(new Thread() {
    	                  //  public void run() {
    	                    	StringBuilder masterSb = null;
    	    	    			StringBuilder additSb = null;
    	    					String[] masterPictureArr=commoditySpec.getMasterPicture().split("\\|");
    	    					
    	    					if (masterPictureArr != null && masterPictureArr.length>0) {
    	    						masterSb=commonService.uploadImg(masterPictureArr);
    	    					}
    	    					
    	    					if (StringUtils.isNotBlank(commoditySpec.getAdditionalPicture())) {
    	    						String[] additionalPictureArr=commoditySpec.getAdditionalPicture().split("\\|");
    	    						if (additionalPictureArr != null && additionalPictureArr.length>0) {
    	    							additSb=commonService.uploadImg(additionalPictureArr);
    	    						}
    	    					}
    	    					
    	    					if (masterSb != null && masterSb.toString().length()>0) {
    	    						CommoditySpec newSpec=new CommoditySpec();
    	    						newSpec.setId(commoditySpec.getId());
    	    						newSpec.setVersion(commoditySpec.getVersion());
    	    						newSpec.setMasterPicture(masterSb.toString().substring(0, masterSb.toString().length()-1));
    	    						if (additSb != null && additSb.toString().length()>0) {
    	    							newSpec.setAdditionalPicture(additSb.toString().substring(0, additSb.toString().length()-1));
    	    						}
    	    						commoditySpecMapper.updateByPrimaryKeySelective(newSpec);
    	    					}
    	                    //}
    	               // });
    				}
    	    		specList.clear();
    			}
    		}
		} catch (Exception e) {
			logger.error("上传并替换所有sku的图片异常",e);
		}
    	
    	logger.info("<======erp商品sku图片替换处理结束====");
    }
	
	
	
	/**
     * 服务远程调用构造供应商信息
     *
     */
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
                    for (int i=0;i<result.size();i++) {
                        if (Long.valueOf((Integer) ((Map)result.get(i)).get("userId")) == Long.valueOf(cs.getSupplierId())) {
                            cs.setSupplierName((String) ((Map)result.get(i)).get("loginName"));
                            cs.setSupplierCompanyName((String) ((Map)result.get(i)).get("companyName"));
                        }
                    }
                }
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
    }
}


