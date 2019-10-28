package com.rondaful.cloud.commodity.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.dto.SkuExportDto;
import com.rondaful.cloud.commodity.entity.BindCategoryAliexpress;
import com.rondaful.cloud.commodity.entity.Brand;
import com.rondaful.cloud.commodity.entity.Category;
import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.CommodityBelongSeller;
import com.rondaful.cloud.commodity.entity.CommodityDetails;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.GoodCangCategoryBind;
import com.rondaful.cloud.commodity.entity.SkuImport;
import com.rondaful.cloud.commodity.entity.SkuImportErrorRecord;
import com.rondaful.cloud.commodity.entity.SkuPushRecord;
import com.rondaful.cloud.commodity.entity.SystemSpu;
import com.rondaful.cloud.commodity.enums.WarehouseFirmEnum;
import com.rondaful.cloud.commodity.listen.HandleSkuExcelListen;
import com.rondaful.cloud.commodity.mapper.BindCategoryAliexpressMapper;
import com.rondaful.cloud.commodity.mapper.BrandMapper;
import com.rondaful.cloud.commodity.mapper.CategoryMapper;
import com.rondaful.cloud.commodity.mapper.CommodityBaseMapper;
import com.rondaful.cloud.commodity.mapper.CommodityBelongSellerMapper;
import com.rondaful.cloud.commodity.mapper.CommodityDetailsMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.mapper.GoodCangMapper;
import com.rondaful.cloud.commodity.mapper.SkuImportErrorRecordMapper;
import com.rondaful.cloud.commodity.mapper.SkuImportMapper;
import com.rondaful.cloud.commodity.mapper.SystemSpuMapper;
import com.rondaful.cloud.commodity.rabbitmq.MQSender;
import com.rondaful.cloud.commodity.remote.RemoteOrderService;
import com.rondaful.cloud.commodity.remote.RemoteUserService;
import com.rondaful.cloud.commodity.service.GoodCangService;
import com.rondaful.cloud.commodity.service.ICommodityService;
import com.rondaful.cloud.commodity.service.MessageService;
import com.rondaful.cloud.commodity.service.SkuImportService;
import com.rondaful.cloud.commodity.service.SkuOtherService;
import com.rondaful.cloud.commodity.service.WmsPushService;
import com.rondaful.cloud.commodity.utils.DateUtil;
import com.rondaful.cloud.commodity.utils.SnowFlakeUtil;
import com.rondaful.cloud.commodity.utils.Utils;
import com.rondaful.cloud.commodity.utils.ValidatorUtil;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.FileService;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RemoteUtil;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import net.sf.json.JSONArray;

@Service
public class SkuOtherServiceImpl implements SkuOtherService {
	
	private final static Logger log = LoggerFactory.getLogger(SkuOtherServiceImpl.class);
	
	@Autowired
    private RemoteOrderService remoteOrderService;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	@Autowired
    private CommodityBaseMapper commodityBaseMapper;

    @Autowired
    private CommodityDetailsMapper commodityDetailsMapper;

    @Autowired
    private CommoditySpecMapper commoditySpecMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SystemSpuMapper systemSpuMapper;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private MQSender mqSender;
    
    @Autowired
    private ICommodityService commodityService;
    
    @Autowired
    private SkuImportErrorRecordMapper skuImportErrorRecordMapper;
    
    @Resource
	private FileService fileService;
	
	@Value("${rondaful.system.env}")
	public String env;
	
	@Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
	
	@Autowired
	private SkuImportMapper skuImportMapper;
	
	@Autowired
	private HandleSkuExcelListen handleSkuExcelListen;

	@Autowired
	private CommodityBelongSellerMapper commodityBelongSellerMapper;
	
	@Autowired
	private BindCategoryAliexpressMapper bindCategoryAliexpressMapper;
	
	@Autowired
	private SkuImportService skuImportService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private WmsPushService wmsPushService;
	
	@Autowired
	private GoodCangService goodCangService;
	
	@Autowired
	private GoodCangMapper goodCangMapper;
	

	@Override
	@CacheEvict(value = "commodityListCache", allEntries = true)
	@Transactional(rollbackFor=Exception.class)
	public synchronized String addSkuByExcel(List<CommodityBase> baseList, String optUser, Long supplierId,Long importId,boolean isAdmin) {
		List<SkuImportErrorRecord> errorList = new ArrayList<SkuImportErrorRecord>();
		List<SkuImportErrorRecord> successList = new ArrayList<SkuImportErrorRecord>();
		try {
			
			//BigDecimal rate = new BigDecimal("0.149253");
			BigDecimal rate = null;
			Object obj = remoteOrderService.getRate("CNY", "USD");
			com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON.toJSON(obj);
			if (json != null) {
				String data = (String) json.get("data");
				if (data != null) {
					rate = new BigDecimal(data);
				} else {
					log.error("订单服务异常，获取美元利率为空");
					//更新导入记录为导入失败，
					SkuImport record=skuImportMapper.selectByPrimaryKey(importId);
					record.setStatus(2);
					record.setTaskDetail("导入失败，订单服务异常，获取美元利率为空");
					skuImportMapper.updateByPrimaryKeySelective(record);
					return null;
				}
			}
			

			SkuImportErrorRecord record = null;
			log.info("批量导入sku开始");
			for (CommodityBase commodityBase : baseList) {
				commodityBase.setSupplierId(supplierId);
				
				List<CommoditySpec> commoditySpec = commodityBase.getCommoditySpecList();
				CommodityDetails commodityDetails = commodityBase.getCommodityDetails();
				if (commoditySpec == null || commoditySpec.size() == 0) {
					continue;
				}
				
				if (StringUtils.isBlank(commodityBase.getSupplierSpu())) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "供应商SPU为空", errorList,optUser, importId);
					continue;
				}
				if (StringUtils.isBlank(commodityBase.getCategoryName1())) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "一级分类名称为空", errorList,optUser, importId);
					continue;
				}
				if (StringUtils.isBlank(commodityBase.getCategoryName2())) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "二级分类名称为空", errorList, optUser, importId);
					continue;
				}
				if (StringUtils.isBlank(commodityBase.getCategoryName3())) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "三级分类名称为空", errorList, optUser, importId);
					continue;
				}
				
				// 分类校验
				List<Category> category1 = categoryMapper.selectCategoryByName(commodityBase.getCategoryName1(), 1);
				List<Category> category2 = categoryMapper.selectCategoryByName(commodityBase.getCategoryName2(), 2);
				List<Category> category3 = categoryMapper.selectCategoryByName(commodityBase.getCategoryName3(), 3);
				if (category1 == null || category1.size() == 0) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "一级分类不存在", errorList, optUser,importId);
					continue;
				}
				if (category2 == null || category2.size() == 0) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "二级分类不存在", errorList, optUser, importId);
					continue;
				}
				if (category3 == null || category3.size() == 0) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "三级分类不存在", errorList, optUser, importId);
					continue;
				}
				if (category2.get(0).getCategoryParentId().intValue() != category1.get(0).getId().intValue()) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "一二级分类关联不匹配", errorList, optUser, importId);
					continue;
				}
				if (category3.get(0).getCategoryParentId().intValue() != category2.get(0).getId().intValue()) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "二三级分类关联不匹配", errorList, optUser, importId);
					continue;
				}

				commodityBase.setCategoryLevel1(category1.get(0).getId());
				commodityBase.setCategoryLevel2(category2.get(0).getId());
				commodityBase.setCategoryLevel3(category3.get(0).getId());
				//佣金率
	            Integer feeRate=category1.get(0).getFeeRate();
				
				// 判断是编辑还是新增
				CommodityBase cb = null;
				List<CommodityBase> cblist = null;
				cblist=commodityBaseMapper.selectBySupplierAndSpu(commodityBase.getSupplierId(),commodityBase.getSupplierSpu());
				if (cblist != null && cblist.size() > 0) {
					cb = cblist.get(0);
				}

				// 已存在的SKU，做编辑操作
				if (cb != null) {
					Long commodityId = cb.getId();

					// SPU码
					SystemSpu systemSpu = systemSpuMapper.selectByPrimaryKey(cb.getSpuId());
					String SPU = systemSpu.getSpuValue();

					// 更新detail
					CommodityDetails oldDetail=commodityDetailsMapper.selectByCommodityId(commodityId);
					commodityDetails.setCommodityId(commodityId);
					commodityDetails.setId(oldDetail.getId());
					commodityDetails.setVersion(oldDetail.getVersion());
					commodityDetailsMapper.updateByPrimaryKeySelective(commodityDetails);

					// base更新
					commodityBase.setVersion(cb.getVersion());
					commodityBase.setId(commodityId);
					commodityBaseMapper.updateByPrimaryKeySelective(commodityBase);

					// 指定可售卖家
					if (commodityBase.getBelongSeller() != null && commodityBase.getBelongSeller().size() > 0) {
						commodityBelongSellerMapper.deleteByCommodityId(commodityId);

						CommodityBelongSeller belongSeller = null;
						List<CommodityBelongSeller> list = new ArrayList<CommodityBelongSeller>();
						for (String seller : commodityBase.getBelongSeller()) {
							belongSeller = new CommodityBelongSeller();
							belongSeller.setCommodityId(commodityBase.getId());
							belongSeller.setSellerId(Long.parseLong(seller));
							list.add(belongSeller);
						}
						commodityBelongSellerMapper.insertBatch(list);
					}
					
					boolean addEsFlag = false;
					// SKU处理
					Map<String, String> chekemap = new HashMap<>();
					for (CommoditySpec comm : commoditySpec) {
						if (StringUtils.isBlank(comm.getSupplierSku())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), "", "失败", "供应商sku为空", errorList, optUser, importId);
							continue;
						}
						
						//根据供应商ID和供应商sku判断sku是新增还是编辑
						CommoditySpec spec = commoditySpecMapper.getSkuBySupplierIdAndSku(commodityBase.getSupplierId(),comm.getSupplierSku());
						if (spec != null) {
							//sku编辑
							if (spec.getState() == 3) {
								addEsFlag = true;
								
								// 价格变动，发cms消息通知
								if (comm.getCommodityPrice() != null && comm.getCommodityPrice().compareTo(spec.getCommodityPrice()) != 0) {
									RemoteUtil.invoke(remoteOrderService.queryMapsNoLimit(comm.getSystemSku()));
									List<Map> list = RemoteUtil.getList();
									if (list != null && list.size() > 0) {
										Set<String> keySet = new HashSet<String>();
										for (Map map : list) {
											StringBuilder sb = new StringBuilder();
											String userId = (String) map.get("sellerPlId");
											String account = (String) map.get("sellerPlAccount");
											if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(account)) {
												keySet.add(sb.append(userId).append("==").append(account).toString());
											}
										}
										for (String key : keySet) {
											Set<String> valueSet = new HashSet<String>();
											for (Map map : list) {
												StringBuilder sb = new StringBuilder();
												String userId = (String) map.get("sellerPlId");
												String account = (String) map.get("sellerPlAccount");
												String platform = (String) map.get("platform");
												String key2 = sb.append(userId).append("==").append(account).toString();
												if (key.equals(key2)) {
													valueSet.add(platform);
												}
											}
											if (valueSet.size() > 0) {
												messageService.priceChangeMsg(key.split("==")[0], key.split("==")[1],
														spec.getSystemSku(),
														valueSet.toString().substring(valueSet.toString().indexOf("[") + 1,
																valueSet.toString().indexOf("]")));
											}
										}
									}
								}
							}
							comm.setId(spec.getId());
							comm.setVersion(spec.getVersion());
							comm.setState(spec.getState());
							if (spec.getFeeRate() != null) {
								comm.setFeeRate(spec.getFeeRate());
							}
							if (spec.getFeePrice() != null) {
								comm.setFeePrice(spec.getFeePrice());
							}
							
							// 设置美元
							if (rate != null) {
								if (comm.getCommodityPrice() != null) {
									comm.setCommodityPriceUs(comm.getCommodityPrice().multiply(rate).setScale(2, BigDecimal.ROUND_DOWN));
								}
								
								//仓库价格
								if (StringUtils.isNotBlank(comm.getWarehousePriceGroup())) {
									comm.setWarehousePriceGroup(comm.getWarehousePriceGroup().replaceAll("：", ":"));//防止他们填有中文冒号
									StringBuilder newWarehousePriceGroup=new StringBuilder();
									BigDecimal newPrice=null;
									String[] priceGroupArry=comm.getWarehousePriceGroup().split("\\|");
									if (priceGroupArry != null && priceGroupArry.length>0) {
										for (int k = 0; k < priceGroupArry.length; k++) {
											newPrice=new BigDecimal(priceGroupArry[k].split(":")[1]).multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
											newWarehousePriceGroup.append(priceGroupArry[k].split(":")[0]).append(":").append(newPrice).append("|");
										}
									}
									if (newWarehousePriceGroup.length()>0) {
										comm.setWarehousePriceGroup(newWarehousePriceGroup.substring(0, newWarehousePriceGroup.length()-1));
									}
								}
							}

							int n = commoditySpecMapper.updateByPrimaryKeySelective(comm);
							if (n > 0) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "成功", "商品修改成功", successList, optUser, importId);
							}
							
							// 查询推送记录，推送成功的，则调用编辑接口
			                Map<String, Object> param=new HashMap<String, Object>();
			    			param.put("systemSku",comm.getSystemSku());
			    			param.put("pushState",1);
			    			List<SkuPushRecord> recordList=goodCangMapper.querySkuPushRecord(param);
			    			if (recordList != null && recordList.size()>0) {
			    				List<String> skuList=new ArrayList<String>();
			    				skuList.add(comm.getSystemSku());
			    				List<CommoditySpec> commoditySpecs=commoditySpecMapper.selectCommoditySpecBySku(skuList);
								for (SkuPushRecord pushRecord : recordList) {
									if (WarehouseFirmEnum.WMS.getCode().equals(pushRecord.getWarehouseProviderCode())) {
										wmsPushService.addProduct(pushRecord.getAccountId(), 2, commoditySpecs, "商品导入");
									}else if (WarehouseFirmEnum.GOODCANG.getCode().equals(pushRecord.getWarehouseProviderCode())) {
										if ("R".equals(pushRecord.getProductState())) {
											goodCangService.pushSkusToGoodCang(pushRecord.getAccountId(), 2, commoditySpecs, "商品导入");
										}
									}
								}
							}
			    			
						} else {
							// 追加的sku
							int hasSpec = commoditySpecMapper.getSkuByCommodityIdAndSpec(commodityId,comm.getCommoditySpec());
							if (hasSpec > 0) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "规格重复",errorList, optUser, importId);
								continue;
							}

							if (StringUtils.isBlank(comm.getCommodityNameCn())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品中文名称为空", errorList, optUser, importId);
								continue;
							}
							if (StringUtils.isBlank(comm.getCommodityNameEn())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品英文名称为空", errorList, optUser, importId);
								continue;
							}
							if (comm.getMasterPicture() == null) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "SKU主图为空", errorList, optUser, importId);
								continue;
							}

							if (comm.getPackingHeight() != null && !ValidatorUtil.isMathFloat(comm.getPackingHeight().toString())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装尺寸-高度小数点后最多两位", errorList, optUser, importId);
								continue;
							}
							if (comm.getPackingLength() != null && !ValidatorUtil.isMathFloat(comm.getPackingLength().toString())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装尺寸-长度小数点后最多两位", errorList, optUser, importId);
								continue;
							}
							if (comm.getPackingWidth() != null && !ValidatorUtil.isMathFloat(comm.getPackingWidth().toString())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装尺寸-宽度小数点后最多两位", errorList, optUser, importId);
								continue;
							}
							if (comm.getPackingWeight() != null && !ValidatorUtil.isMathFloat(comm.getPackingWeight().toString())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装重量小数点后最多两位", errorList, optUser, importId);
								continue;
							}

							if (comm.getCommodityHeight() != null && !ValidatorUtil.isMathFloat(comm.getCommodityHeight().toString())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品尺寸-高度小数点后最多两位", errorList, optUser, importId);
								continue;
							}
							if (comm.getCommodityLength() != null && !ValidatorUtil.isMathFloat(comm.getCommodityLength().toString())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品尺寸-长度小数点后最多两位", errorList, optUser, importId);
								continue;
							}
							if (comm.getCommodityWidth() != null && !ValidatorUtil.isMathFloat(comm.getCommodityWidth().toString())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品尺寸-宽度小数点后最多两位", errorList, optUser, importId);
								continue;
							}
							if (comm.getCommodityWeight() != null && !ValidatorUtil.isMathFloat(comm.getCommodityWeight().toString())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品重量小数点后最多两位", errorList, optUser, importId);
								continue;
							}
							
							if (comm.getCommodityHeight() != null && (comm.getCommodityHeight().compareTo(comm.getPackingHeight())) > 0) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品高度不能大于包装高度", errorList, optUser, importId);
								continue;
							}
			                if (comm.getCommodityLength() != null && (comm.getCommodityLength().compareTo(comm.getPackingLength())) > 0) {
			                	addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品长度不能大于包装长度", errorList, optUser, importId);
								continue;
			                }
			                if (comm.getCommodityWidth() != null && (comm.getCommodityWidth().compareTo(comm.getPackingWidth())) > 0) {
			                	addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品宽度不能大于包装宽度", errorList, optUser, importId);
								continue;
			                }
			                if (comm.getCommodityWeight() != null && (comm.getCommodityWeight().compareTo(comm.getPackingWeight())) > 0) {
			                	addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品重量不能大于包装重量", errorList, optUser, importId);
								continue;
			                }

							if (comm.getCommodityPrice() == null || !ValidatorUtil.isMathFloat(comm.getCommodityPrice().toString())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品价格必须小数点后两位", errorList, optUser, importId);
								continue;
							}

							boolean specError=false;
							if (StringUtils.isNotBlank(comm.getCommoditySpec())) {
								if (chekemap.containsKey(comm.getCommoditySpec())) {
									addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "规格属性值重复", errorList, optUser, importId);
									continue;
								} else {
									chekemap.put(comm.getCommoditySpec(), comm.getCommoditySpec());
								}

								StringBuilder cnBuilder = new StringBuilder();// 中文规格
								StringBuilder enBuilder = new StringBuilder();// 英文规格
								Map<String, String> mm = new HashMap<>();
								String[] spac = comm.getCommoditySpec().split("\\|");
								for (int j = 0; j < spac.length; j++) {
									if (!spac[j].contains("):") || !spac[j].endsWith(")")) {
										addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(),"失败", "商品sku所选规格格式错误", errorList, optUser, importId);
										
										specError=true;
										break;
									}

									String str = spac[j].split(":")[0];
									if (mm.containsKey(str)) {
										addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(),"失败", "规格重复", errorList, optUser, importId);
										specError=true;
										break;
									}
									mm.put(str, str);

									String[] arr = spac[j].split(":");
									cnBuilder.append(arr[0].substring(0, arr[0].indexOf("("))).append(":")
											.append(arr[1].substring(0, arr[1].indexOf("("))).append("|");

									enBuilder.append(arr[0].substring(arr[0].indexOf("(") + 1, arr[0].indexOf(")")))
											.append(":")
											.append(arr[1].substring(arr[1].indexOf("(") + 1, arr[1].indexOf(")")))
											.append("|");

									if (cnBuilder.length() > 0) {
										comm.setCommoditySpec(cnBuilder.substring(0, cnBuilder.length() - 1));
									}
									if (enBuilder.length() > 0) {
										comm.setCommoditySpecEn(enBuilder.substring(0, enBuilder.length() - 1));
									}
								}
							} else {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "规格属性值重复为空",errorList, optUser, importId);
								continue;
							}
							if (specError) {
								continue;
							}
							

							// 取出已有sku的最大systemSku
							String maxSku = commoditySpecMapper.getMaxSystemSkuBySpu(SPU);
							Integer maxSystemSkuInt = null;
							if (StringUtils.isNotBlank(maxSku)) {
								maxSystemSkuInt = Integer.parseInt(maxSku.substring(8));
							} else {
								maxSystemSkuInt = 0;
							}

							comm.setSystemSku(SPU + String.format(String.format("%%0%dd", 3), maxSystemSkuInt + 1));
							comm.setCommodityId(commodityId);
							
							// 设置美元
							if (rate != null) {
								comm.setCommodityPriceUs(comm.getCommodityPrice().multiply(rate).setScale(2, BigDecimal.ROUND_DOWN));
								
								//仓库价格
								if (StringUtils.isNotBlank(comm.getWarehousePriceGroup())) {
									comm.setWarehousePriceGroup(comm.getWarehousePriceGroup().replaceAll("：", ":"));//防止他们填有中文冒号
									StringBuilder newWarehousePriceGroup=new StringBuilder();
									BigDecimal newPrice=null;
									String[] priceGroupArry=comm.getWarehousePriceGroup().split("\\|");
									if (priceGroupArry != null && priceGroupArry.length>0) {
										for (int k = 0; k < priceGroupArry.length; k++) {
											newPrice=new BigDecimal(priceGroupArry[k].split(":")[1]).multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
											newWarehousePriceGroup.append(priceGroupArry[k].split(":")[0]).append(":").append(newPrice).append("|");
										}
									}
									if (newWarehousePriceGroup.length()>0) {
										comm.setWarehousePriceGroup(newWarehousePriceGroup.substring(0, newWarehousePriceGroup.length()-1));
									}
								}
							}
							
							//佣金率取分类设置的
							comm.setFeeRate(Double.parseDouble(feeRate+""));
							
							//状态设置,如果是管理员导入，则设置上架，否则为审核中 
							if (isAdmin) {
								comm.setState(3);
							}else {
								comm.setState(0);
							}
							
							comm.setSupplierId(String.valueOf(commodityBase.getSupplierId()));
							int n = commoditySpecMapper.insertSelective(comm);
							if (n > 0) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "成功", "商品创建成功", successList, optUser, importId);
							}
						}
					}

					if (addEsFlag) {
						// 插入ES
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", commodityId);
						map.put("isUp", true);
						commodityService.initCommodityIndex(map);
					}

				} else {
					// 新的SPU
					if (commodityBase.getSupplierId() != null) {
						RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>() {{
								this.add(commodityBase.getSupplierId());
							}}, 0));
						List<Map> list = RemoteUtil.getList();
						if (list == null || list.isEmpty()) {
							addErrorLogList(record, commodityBase.getSupplierSpu(),
									commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "用户服务返回供应商信息为空", errorList, optUser, importId);
							continue;
						}
					}
					
					if (StringUtils.isBlank(commodityBase.getBrandName())) {
						addErrorLogList(record, commodityBase.getSupplierSpu(),
								commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "品牌名称为空", errorList, optUser, importId);
						continue;
					}
					if (StringUtils.isBlank(commodityBase.getDefaultRepository())) {
						addErrorLogList(record, commodityBase.getSupplierSpu(),
								commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "默认仓库为空", errorList, optUser, importId);
						continue;
					}
					if (commodityBase.getIsPrivateModel() == null) {
						addErrorLogList(record, commodityBase.getSupplierSpu(),
								commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "是否私模为空", errorList, optUser, importId);
						continue;
					}
					if (StringUtils.isBlank(commodityBase.getProductLogisticsAttributes())) {
						addErrorLogList(record, commodityBase.getSupplierSpu(),
								commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "产品物流属性为空", errorList, optUser, importId);
						continue;
					}
					if (StringUtils.isBlank(commodityBase.getVendibilityPlatform())) {
						addErrorLogList(record, commodityBase.getSupplierSpu(),
								commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "可售平台为空", errorList, optUser, importId);
						continue;
					}
					if (StringUtils.isBlank(commodityDetails.getMasterPicture())) {
						addErrorLogList(record, commodityBase.getSupplierSpu(),
								commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "SPU主图为空", errorList, optUser, importId);
						continue;
					}

					// 新增品牌
					List<Brand> brand = brandMapper.findBrandList(new Brand() {{
							this.setBrandName(commodityBase.getBrandName());
						}});
					if (brand.isEmpty()) {
						Brand b = new Brand();
						b.setCreatTime(DateUtil.getCurrentDate());
						b.setSupplierId(commodityBase.getSupplierId());
						b.setBrandName(commodityBase.getBrandName());
						b.setState(1);
						int result = brandMapper.insertSelective(b);
						if (result != 1) {
							addErrorLogList(record, commodityBase.getSupplierSpu(),
									commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "新增品牌系统异常", errorList, optUser, importId);
							continue;
						}
						commodityBase.setBrandId(b.getId());
					} else {
						commodityBase.setBrandId(brand.get(0).getId());
					}
					
					// 设置默认站点
					if (commodityBase.getVendibilityPlatform().contains("eBay")) {
						commodityBase.setEbaySite("US,Canada,CanadaFrench,UK,Germany,Australia,France,eBayMotors,Italy,Netherlands,Spain,HongKong,Singapore,Poland,Belgium_Dutch,Belgium_French,Austria,Switzerland,Ireland");
					}
					if (commodityBase.getVendibilityPlatform().contains("Amazon")) {
						commodityBase.setAmazonSite("AU,CA,DE,ES,FR,GB,IT,JP,MX,US");
					}

					Long commodityId = null;
					String SPU = "";
					Integer maxSystemSkuInt = null;
					CommodityBase base = null;

					// 根据supplierId+supplierSpu查询base
					if (StringUtils.isNotBlank(commodityBase.getSupplierSpu())) {
						List<CommodityBase> commoList=commodityBaseMapper.selectBySupplierAndSpu(commodityBase.getSupplierId(),commodityBase.getSupplierSpu());
						if (commoList != null && commoList.size()>0) {
							base = commoList.get(0);
						}
					}

					if (base == null) {// 新的base
						commodityBase.setCreatTime(DateUtil.getCurrentDate());
						String snow = String.valueOf(SnowFlakeUtil.nextId());
						SPU = snow.substring(snow.length() - 8, snow.length());
						int spuCount = systemSpuMapper.getSpuCount(SPU);
						while (spuCount > 0) {
							snow = String.valueOf(SnowFlakeUtil.nextId());
							SPU = snow.substring(snow.length() - 8, snow.length());
							spuCount = systemSpuMapper.getSpuCount(SPU);
						}
						log.info("导入商品，获取到新的SPU={}", SPU);

						SystemSpu ss = new SystemSpu();
						ss.setCategoryLevel1(category1.get(0).getId());
						ss.setCategoryLevel2(category2.get(0).getId());
						ss.setCreatTime(DateUtil.DateToString(new Date()));
						ss.setSpuValue(SPU);
						systemSpuMapper.insertSelective(ss);

						commodityBase.setSpuId(ss.getId());
						commodityBaseMapper.insertSelective(commodityBase);
						commodityId = commodityBase.getId();
					} else {// base存在

						commodityId = base.getId();
						SPU = base.getSPU();
						// 取出已有sku的最大systemSku
						String maxSku = commoditySpecMapper.getMaxSystemSkuBySpu(SPU);
						maxSystemSkuInt = Integer.parseInt(maxSku.substring(8));

						// 先删除所有detail
						commodityDetailsMapper.deleteCommodityDetailsByCommodityId(commodityId);
					}

					// detail插入
					commodityDetails.setCommodityId(commodityId);
					commodityDetailsMapper.insertSelective(commodityDetails);

					// 指定可售卖家
					if (commodityBase.getBelongSeller() != null && commodityBase.getBelongSeller().size() > 0) {
						commodityBelongSellerMapper.deleteByCommodityId(commodityId);

						CommodityBelongSeller belongSeller = null;
						List<CommodityBelongSeller> list = new ArrayList<CommodityBelongSeller>();
						for (String seller : commodityBase.getBelongSeller()) {
							belongSeller = new CommodityBelongSeller();
							belongSeller.setCommodityId(commodityBase.getId());
							belongSeller.setSellerId(Long.parseLong(seller));
							list.add(belongSeller);
						}
						commodityBelongSellerMapper.insertBatch(list);
					}

					// SKU处理
					Map<String,String> chekemap = new HashMap<>();
					int i = maxSystemSkuInt == null ? 1 : maxSystemSkuInt + 1;
					for (CommoditySpec comm : commoditySpec) {
						if (StringUtils.isBlank(comm.getSupplierSku())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), "", "失败", "供应商sku为空", errorList, optUser,importId);
							continue;
						}
						if (StringUtils.isBlank(comm.getCommodityNameCn())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品中文名称为空",errorList, optUser, importId);
							continue;
						}
						if (StringUtils.isBlank(comm.getCommodityNameEn())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品英文名称为空",errorList, optUser, importId);
							continue;
						}
						if (comm.getMasterPicture() == null) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "SKU主图为空",errorList, optUser, importId);
							continue;
						}

						if (comm.getPackingHeight() != null && !ValidatorUtil.isMathFloat(comm.getPackingHeight().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装尺寸-高度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getPackingLength() != null
								&& !ValidatorUtil.isMathFloat(comm.getPackingLength().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装尺寸-长度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getPackingWidth() != null
								&& !ValidatorUtil.isMathFloat(comm.getPackingWidth().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装尺寸-宽度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getPackingWeight() != null
								&& !ValidatorUtil.isMathFloat(comm.getPackingWeight().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装重量小数点后最多两位", errorList, optUser, importId);
							continue;
						}

						if (comm.getCommodityHeight() != null
								&& !ValidatorUtil.isMathFloat(comm.getCommodityHeight().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品尺寸-高度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getCommodityLength() != null
								&& !ValidatorUtil.isMathFloat(comm.getCommodityLength().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品尺寸-长度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getCommodityWidth() != null
								&& !ValidatorUtil.isMathFloat(comm.getCommodityWidth().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品尺寸-宽度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getCommodityWeight() != null
								&& !ValidatorUtil.isMathFloat(comm.getCommodityWeight().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品重量小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						
						if (comm.getCommodityHeight() != null && (comm.getCommodityHeight().compareTo(comm.getPackingHeight())) > 0) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品高度不能大于包装高度", errorList, optUser, importId);
							continue;
						}
		                if (comm.getCommodityLength() != null && (comm.getCommodityLength().compareTo(comm.getPackingLength())) > 0) {
		                	addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品长度不能大于包装长度", errorList, optUser, importId);
							continue;
		                }
		                if (comm.getCommodityWidth() != null && (comm.getCommodityWidth().compareTo(comm.getPackingWidth())) > 0) {
		                	addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品宽度不能大于包装宽度", errorList, optUser, importId);
							continue;
		                }
		                if (comm.getCommodityWeight() != null && (comm.getCommodityWeight().compareTo(comm.getPackingWeight())) > 0) {
		                	addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品重量不能大于包装重量", errorList, optUser, importId);
							continue;
		                }

						if (comm.getCommodityPrice() == null
								|| !ValidatorUtil.isMathFloat(comm.getCommodityPrice().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品价格必须小数点后两位", errorList, optUser, importId);
							continue;
						}

						boolean specError=false;
						if (StringUtils.isNotBlank(comm.getCommoditySpec())) {
							if (chekemap.containsKey(comm.getCommoditySpec())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(),"失败", "规格属性值重复", errorList, optUser, importId);
								continue;
							} else {
								chekemap.put(comm.getCommoditySpec(), comm.getCommoditySpec());
							}

							StringBuilder cnBuilder = new StringBuilder();// 中文规格
							StringBuilder enBuilder = new StringBuilder();// 英文规格
							Map<String, String> mm = new HashMap<>();
							String[] spac = comm.getCommoditySpec().split("\\|");
							for (int j = 0; j < spac.length; j++) {
								if (!spac[j].contains("):") || !spac[j].endsWith(")")) {
									addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(),"失败", "商品sku所选规格格式错误", errorList, optUser, importId);
									
									specError=true;
									break;
								}

								String str = spac[j].split(":")[0];
								if (mm.containsKey(str)) {
									addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(),"失败", "规格重复", errorList, optUser, importId);
									
									specError=true;
									break;
								}
								mm.put(str, str);

								String[] arr = spac[j].split(":");
								cnBuilder.append(arr[0].substring(0, arr[0].indexOf("("))).append(":")
										.append(arr[1].substring(0, arr[1].indexOf("("))).append("|");

								enBuilder.append(arr[0].substring(arr[0].indexOf("(") + 1, arr[0].indexOf(")")))
										.append(":")
										.append(arr[1].substring(arr[1].indexOf("(") + 1, arr[1].indexOf(")")))
										.append("|");

								if (cnBuilder.length() > 0) {
									comm.setCommoditySpec(cnBuilder.substring(0, cnBuilder.length() - 1));
								}
								if (enBuilder.length() > 0) {
									comm.setCommoditySpecEn(enBuilder.substring(0, enBuilder.length() - 1));
								}
							}
						}
						if (specError) {
							continue;
						}

						// 增加系统sku
						comm.setSystemSku(SPU + String.format(String.format("%%0%dd", 3), i));
						comm.setCommodityId(commodityId);
						// 设置美元
						if (rate != null) {
							comm.setCommodityPriceUs(comm.getCommodityPrice().multiply(rate).setScale(2, BigDecimal.ROUND_DOWN));
							
							//仓库价格
							if (StringUtils.isNotBlank(comm.getWarehousePriceGroup())) {
								comm.setWarehousePriceGroup(comm.getWarehousePriceGroup().replaceAll("：", ":"));//防止他们填有中文冒号
								StringBuilder newWarehousePriceGroup=new StringBuilder();
								BigDecimal newPrice=null;
								String[] priceGroupArry=comm.getWarehousePriceGroup().split("\\|");
								if (priceGroupArry != null && priceGroupArry.length>0) {
									for (int k = 0; k < priceGroupArry.length; k++) {
										newPrice=new BigDecimal(priceGroupArry[k].split(":")[1]).multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
										newWarehousePriceGroup.append(priceGroupArry[k].split(":")[0]).append(":").append(newPrice).append("|");
									}
								}
								if (newWarehousePriceGroup.length()>0) {
									comm.setWarehousePriceGroup(newWarehousePriceGroup.substring(0, newWarehousePriceGroup.length()-1));
								}
							}
						}
						
						//佣金率取分类设置的
						comm.setFeeRate(Double.parseDouble(feeRate+""));
						
						//状态设置,如果是管理员导入，则设置上架，否则为审核中 
						if (isAdmin) {
							comm.setState(3);
						}else {
							comm.setState(0);
						}
						
						comm.setSupplierId(String.valueOf(commodityBase.getSupplierId()));
						int n = commoditySpecMapper.insertSelective(comm);
						if (n > 0) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "成功", "商品创建成功",successList, optUser, importId);
						}
						i++;
					}

					// 是erp商品，发送MQ通知
					if (supplierId == 100) {
						constructionSupplierForSpec(commoditySpec);// 先构造供应商信息
						mqSender.commoditySkuAdd(JSONArray.fromObject(commoditySpec).toString());
					}
				}
				
			}
		} catch (Exception e) {
			log.error("导入商品异常", e);
			
			//另外开一个类，并且方法新开事务，不然这里的更新也会回滚
			//更新导入记录为导入失败
			skuImportService.updateImportRecord(importId, "导入失败，"+e.getMessage());
			throw e;
		}
		log.info("批量导入sku结束");

		// 批量插入导入明细
		if (errorList.size() > 0) {
			log.info("存在错误的记录{}条", errorList.size());
			skuImportErrorRecordMapper.insertBatch(errorList);
		}
		if (successList.size() > 0) {
			log.info("插入成功的记录{}条", successList.size());
			skuImportErrorRecordMapper.insertBatch(successList);
		}

		StringBuilder sb = new StringBuilder();
		int total = baseList.size();
		int errorNum = errorList.size();
		int successNum = successList.size();
		sb.append("此次导入了").append(total).append("个商品，").append("成功").append(successNum).append("个商品，").append("失败").append(errorNum).append("个商品");
		return sb.toString();
	}
	
	private void addErrorLogList(SkuImportErrorRecord record,String spu,String sku,String state,String content,List<SkuImportErrorRecord> list,String optUser,Long importId) {
		 record=new SkuImportErrorRecord();
    	 record.setSupplierSpu(spu);
    	 record.setSupplierSku(sku);
    	 record.setState(state);
    	 record.setContent(content);
    	 record.setOptUser(optUser);
    	 record.setCreateTime(new Date());
    	 record.setImportId(importId);
    	 list.add(record);
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
            log.error(e.getMessage());
        }
    }
    
    /**
     	* 返回导出的流
     */
    @Override
    public InputStream exportSkuExcel(Map<String, Object> param, boolean isEn) {
    	InputStream in = null;
        try {
        	int total=commodityBaseMapper.selectExportCount(param);
        	int row=total/10000 + 1;
        	
        	Workbook workbook =null;
        	List<Map<String, Object>> sheetsList = new ArrayList<>();
        	for (int i = 1; i <= row; i++) {
        		List<SkuExportDto> exportList = new ArrayList<SkuExportDto>();
        		
        		Page.builder(String.valueOf(i), String.valueOf(10000));
        		List<CommodityBase> list = commodityBaseMapper.selectCommodityListBySpec(param);
                if (!list.isEmpty()) {
                	constructionSupplier(list);//构造供应商
                	
                    param.put("list", list);
                    List<CommoditySpec> listspc = commoditySpecMapper.selectCommoditySpecByCommodityId(param);
                    
                    for (CommodityBase cb : list) {
                        cb.setCommoditySpecList(new ArrayList<>());
                        for (CommoditySpec cs : listspc) {
                            //sku与spu匹配
                            if (cs.getCommodityId().intValue() == cb.getId().intValue()) {
                                cb.getCommoditySpecList().add(cs);

                                //SKU的spu码、分类、供应商取SPU的
                                cs.setSPU(cb.getSPU());
                                cs.setCategoryName(cb.getCategoryName1());//中文分类
                                cs.setCategoryName2(cb.getCategoryName2());//英文分类
                                cs.setSupplierCompanyName(cb.getSupplierName());//供应商公司名称
                            }
                        }

                        if (cb.getCommoditySpecList().size() > 0) {
                            for (CommoditySpec cs : cb.getCommoditySpecList()) {
                                SkuExportDto dto = new SkuExportDto();
                                dto.setSPU(cs.getSPU());
                                dto.setSystemSku(cs.getSystemSku());
                                dto.setSupplierSku(cs.getSupplierSku());
                                dto.setCommodityNameCn(cs.getCommodityNameCn());
                                dto.setCommodityNameEn(cs.getCommodityNameEn());
                                if (isEn) {
                                    dto.setCategoryName(cs.getCategoryName2());
                                } else {
                                    dto.setCategoryName(cs.getCategoryName());
                                }
                                dto.setCommodityPriceUs(cs.getCommodityPriceUs());
                                if (cs.getFeeRate() != null) {
                                    dto.setFee(cs.getFeeRate() + "%");
                                } else if (cs.getFeePriceUs() != null) {
                                    dto.setFee(cs.getFeePriceUs() + "");
                                }
                                dto.setCommoditySpec(cs.getCommoditySpec());
                                dto.setCommodityLength(cs.getCommodityLength());
                                dto.setCommodityWidth(cs.getCommodityWidth());
                                dto.setCommodityHeight(cs.getCommodityHeight());
                                dto.setCommodityWeight(cs.getCommodityWeight());
                                dto.setPackingLength(cs.getPackingLength());
                                dto.setPackingWidth(cs.getPackingWidth());
                                dto.setPackingHeight(cs.getPackingHeight());
                                dto.setPackingWeight(cs.getPackingWeight());
                                if (cs.getState() == -1) {
                                    dto.setState(com.rondaful.cloud.common.utils.Utils.translation("待提交"));
                                } else if (cs.getState() == 0) {
                                    dto.setState(com.rondaful.cloud.common.utils.Utils.translation("待审核"));
                                } else if (cs.getState() == 1) {
                                    dto.setState(com.rondaful.cloud.common.utils.Utils.translation("待上架"));
                                } else if (cs.getState() == 2) {
                                    dto.setState(com.rondaful.cloud.common.utils.Utils.translation("审核失败"));
                                } else if (cs.getState() == 3) {
                                    dto.setState(com.rondaful.cloud.common.utils.Utils.translation("已上架"));
                                }
                                dto.setSupplierCompanyName(cs.getSupplierCompanyName());
                                dto.setCreatTime(cb.getCreatTime());
                                exportList.add(dto);
                            }
                        }
                    }
                }
                ExportParams exportParams = new ExportParams();
                exportParams.setSheetName(new StringBuilder().append("第").append(i).append("页").toString());
                Map<String, Object> exportMap = new HashMap<>();
                exportMap.put("title", exportParams);
                exportMap.put("entity", SkuExportDto.class);
                exportMap.put("data", exportList);
                sheetsList.add(exportMap);
        	}
            workbook = ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
           
            //转InputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte [] bookByteAry = out.toByteArray();
            in = new ByteArrayInputStream(bookByteAry);
            out.close();
            workbook.close();
        } catch (Exception e) {
            log.error("导出sku异常", e);
        }
        return in;
    }
    
    /**
     * 服务远程调用构造供应商信息
     *
     * @param commodityBases
     */
    public void constructionSupplier(List<CommodityBase> commodityBases) {
        try {
            Set<Long> list = new HashSet<Long>() {{
                for (CommodityBase cb : commodityBases) {
                    this.add(cb.getSupplierId());
                }
            }};
            RemoteUtil.invoke(remoteUserService.getSupplierList(list, 0));
            List<Map> result = RemoteUtil.getList();
            if (result != null && !result.isEmpty()) {
                for (CommodityBase cb : commodityBases) {
                    for (int i = 0; i < result.size(); i++) {
                        if (Long.valueOf((Integer) ((Map) result.get(i)).get("userId")) == cb.getSupplierId().longValue()) {
                            //cb.setSupplierName((String) ((Map)result.get(i)).get("loginName"));
                            String companyName = (String) ((Map) result.get(i)).get("companyName");
                            String supplyChainCompanyName = (String) ((Map) result.get(i)).get("supplyChainCompanyName");
                            cb.setSupplierName(com.rondaful.cloud.common.utils.Utils.translation(companyName));
                            cb.setSupplierCompanyName(com.rondaful.cloud.common.utils.Utils.translation(supplyChainCompanyName));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void addImportTask(MultipartFile[] files,Long supplierId,Integer imType) {
		String optUser="";
		boolean isAdmin=false;
    	//判断是否登录
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	optUser=user.getUsername();
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
        		if (user.getTopUserId() == 0) {//主账号
        			supplierId=Long.parseLong(user.getUserid()+"");
    			}else {
    				supplierId=Long.parseLong(user.getTopUserId()+"");
				}
        	}else if (UserEnum.platformType.CMS.getPlatformType().equals(user.getPlatformType())) { //管理员
        		isAdmin=true;
			}
		}
        if (supplierId==null) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不能为空");
		}
        
		MultipartFile file = files[0];
		String taskName=file.getOriginalFilename();
		String fileName=Utils.createFileName(file.getOriginalFilename());
		
		String url ="";
		try {
			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put(fileName, file.getBytes());
			Map<String, String> urlMap=fileService.uploadMultipleFile(ConstantAli.getEnv(env), ConstantAli.getFolder("product"), fileMap, null, null, null);
			if (urlMap != null) {
				Iterator it = urlMap.entrySet().iterator();
				Map.Entry entry = (Map.Entry) it.next();
				url = (String) entry.getValue();
			}
		} catch (Exception e2) {
			log.error("上传导入sku的Excel异常", e2);
		}
		
		try {
			SkuImport im=new SkuImport();
			im.setTaskName(taskName);
			im.setSupplierId(supplierId);
			im.setOptUser(optUser);
			im.setFileUrl(url);
			im.setStatus(0);
			im.setImType(imType);
			int n=skuImportMapper.insert(im);
			
			if (n>0) {
				//加入待处理队列
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("id", im.getId());
				map.put("url", url);
				map.put("isAdmin", isAdmin);
				handleSkuExcelListen.getQueue().add(map);
			}
		} catch (Exception e2) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "导入sku上传Excel文件异常");
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void delTask(Long id) {
		SkuImport skuImport=skuImportMapper.selectByPrimaryKey(id);
		if (skuImport != null) {
			fileService.deleteFile(skuImport.getFileUrl(), null, null, null);
			
			skuImportErrorRecordMapper.deleteByImportId(id);
			skuImportMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public Page<SkuImport> querySkuImportList(Map<String, Object> param) {
		Page.builder((String) param.get("page"), (String) param.get("row"));
		List<SkuImport> resultList = skuImportMapper.page(param);
		try {
			Set<Long> list = new HashSet<Long>();
			for (SkuImport skuImport : resultList) {
				skuImport.setTaskDetail(com.rondaful.cloud.common.utils.Utils.translation(skuImport.getTaskDetail()));
				list.add(skuImport.getSupplierId());
			}
			RemoteUtil.invoke(remoteUserService.getSupplierList(list, 0));
			List<Map> result = RemoteUtil.getList();
			if (result != null && !result.isEmpty()) {
				for (SkuImport skuImport : resultList) {
					for (int i = 0; i < result.size(); i++) {
						if (Long.valueOf((Integer) ((Map) result.get(i)).get("userId")) == Long.valueOf(skuImport.getSupplierId())) {
							String companyName = (String) ((Map) result.get(i)).get("companyName");
							skuImport.setSupplierName(com.rondaful.cloud.common.utils.Utils.translation(companyName));
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		PageInfo pageInfo = new PageInfo(resultList);
		return new Page(pageInfo);
	}

	@Override
	public void exportImportLogExcel(Long importId, HttpServletResponse response) {
		try {
			SkuImport skuImport=skuImportMapper.selectByPrimaryKey(importId);
			List<SkuImportErrorRecord> list=skuImportErrorRecordMapper.selectByImportId(importId);
			if (list != null && list.size()>0) {
				String fileName = skuImport.getTaskName();
	            Workbook workbook = ExcelExportUtil.exportBigExcel(new ExportParams(null, "Sheet1", ExcelType.HSSF), SkuImportErrorRecord.class, list);
	            ExcelExportUtil.closeExportBigExcel();
	            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
	            response.setContentType("application/x-download");
	            response.setCharacterEncoding("UTF-8");
	            try (OutputStream outputStream = response.getOutputStream()) {
	                workbook.write(outputStream);
	                workbook.close();
	            } catch (Exception e) {
	                log.error("导出异常", e.getMessage(), e);
	            }
			}
        } catch (Exception e) {
            log.error("sku导入结果明细导出", e);
        }
	}

	@Transactional(rollbackFor={RuntimeException.class,Exception.class})
	@Override
	public void addOrUpdateCategoryBindAli(BindCategoryAliexpress bind) {
		BindCategoryAliexpress categoryBind=bindCategoryAliexpressMapper.getBindByCategoryId(bind.getPinlianCategoty3Id());
		if (categoryBind == null) {
			bindCategoryAliexpressMapper.insert(bind);
		}else {
			bind.setId(categoryBind.getId());
			bind.setVersion(categoryBind.getVersion());
			bind.setUpdateTime(new Date());
			bindCategoryAliexpressMapper.updateByPrimaryKeySelective(bind);
		}
	}

	@Override
	public void asynExportSku(Map<String, Object> param, boolean isEn,Integer userId,Integer topUserId,Integer platformType,String optUser) {
		String taskname = com.rondaful.cloud.common.utils.Utils.translation("商品导出") + "-" + DateUtil.DateToNo(new Date())+"-"+optUser+".xlsx";
		Integer jobId=null;
		String fileUrl="";
		Integer status=0;
		try {
			// 先调用用户服务，新建任务
			Object obj=remoteUserService.insertDown(taskname, userId, topUserId, platformType);
			if (obj != null) {
				JSONObject json=(JSONObject) JSONObject.toJSON(obj);
				if (json != null && (Boolean)json.get("success") && json.get("data") != null) {
					jobId=(Integer) json.get("data");
				}
			}
			// 将文件上传OSS,再调用户服务更新任务状态
			InputStream inputStream = exportSkuExcel(param,isEn);
			if (inputStream != null && jobId != null) {
				fileUrl=fileService.saveFile(ConstantAli.getEnv(env), ConstantAli.FolderType.EXPORT,taskname,inputStream,null,null,null);
				status=1;
			}
		} catch (Exception e) {
			log.error("商品异步导出异常",e);
			status=4;
		}finally {
			remoteUserService.updateDownStatus(jobId, fileUrl, status);
		}
	}

	@CacheEvict(value = "commodityListCache", allEntries = true)
	@Transactional(rollbackFor=Exception.class)
	@Override
	public String updateSkuByExcel(List<CommodityBase> baseList, String optUser, Long supplierId, Long importId) {
		List<SkuImportErrorRecord> errorList = new ArrayList<SkuImportErrorRecord>();
		List<SkuImportErrorRecord> successList = new ArrayList<SkuImportErrorRecord>();
		try {
			//BigDecimal rate = new BigDecimal("0.149253");
			BigDecimal rate = null;
			Object obj = remoteOrderService.getRate("CNY", "USD");
			com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON.toJSON(obj);
			if (json != null) {
				String data = (String) json.get("data");
				if (data != null) {
					rate = new BigDecimal(data);
				} else {
					log.error("订单服务异常，获取美元利率为空");
					//更新导入记录为导入失败，
					SkuImport record=skuImportMapper.selectByPrimaryKey(importId);
					record.setStatus(2);
					record.setTaskDetail("导入失败，订单服务异常，获取美元利率为空");
					skuImportMapper.updateByPrimaryKeySelective(record);
					return null;
				}
			}
			
			SkuImportErrorRecord record = null;
			for (CommodityBase commodityBase : baseList) {
				commodityBase.setSupplierId(supplierId);
				
				List<CommoditySpec> commoditySpec = commodityBase.getCommoditySpecList();
				CommodityDetails commodityDetails = commodityBase.getCommodityDetails();
				if (commoditySpec == null || commoditySpec.size() == 0) {
					continue;
				}
				
				if (StringUtils.isBlank(commodityBase.getSupplierSpu())) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "供应商SPU为空", errorList,optUser, importId);
					continue;
				}
				if (StringUtils.isBlank(commodityBase.getCategoryName1())) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "一级分类名称为空", errorList,optUser, importId);
					continue;
				}
				if (StringUtils.isBlank(commodityBase.getCategoryName2())) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "二级分类名称为空", errorList, optUser, importId);
					continue;
				}
				if (StringUtils.isBlank(commodityBase.getCategoryName3())) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "三级分类名称为空", errorList, optUser, importId);
					continue;
				}
				
				// 分类校验
				List<Category> category1 = categoryMapper.selectCategoryByName(commodityBase.getCategoryName1(), 1);
				List<Category> category2 = categoryMapper.selectCategoryByName(commodityBase.getCategoryName2(), 2);
				List<Category> category3 = categoryMapper.selectCategoryByName(commodityBase.getCategoryName3(), 3);
				if (category1 == null || category1.size() == 0) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "一级分类不存在", errorList, optUser,importId);
					continue;
				}
				if (category2 == null || category2.size() == 0) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "二级分类不存在", errorList, optUser, importId);
					continue;
				}
				if (category3 == null || category3.size() == 0) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "三级分类不存在", errorList, optUser, importId);
					continue;
				}
				if (category2.get(0).getCategoryParentId().intValue() != category1.get(0).getId().intValue()) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "一二级分类关联不匹配", errorList, optUser, importId);
					continue;
				}
				if (category3.get(0).getCategoryParentId().intValue() != category2.get(0).getId().intValue()) {
					addErrorLogList(record, commodityBase.getSupplierSpu(),
							commodityBase.getCommoditySpecList().get(0).getSupplierSku(), "失败", "二三级分类关联不匹配", errorList, optUser, importId);
					continue;
				}

				commodityBase.setCategoryLevel1(category1.get(0).getId());
				commodityBase.setCategoryLevel2(category2.get(0).getId());
				commodityBase.setCategoryLevel3(category3.get(0).getId());
				//佣金率
	            Integer feeRate=category1.get(0).getFeeRate();
				
				// 判断是否存在
				CommodityBase cb = null;
				List<CommodityBase> cblist = null;
				cblist=commodityBaseMapper.selectBySupplierAndSpu(commodityBase.getSupplierId(),commodityBase.getSupplierSpu());
				if (cblist != null && cblist.size() > 0) {
					cb = cblist.get(0);
				}
				if (cb != null) {
					Long commodityId = cb.getId();

					// SPU码
					SystemSpu systemSpu = systemSpuMapper.selectByPrimaryKey(cb.getSpuId());
					String SPU = systemSpu.getSpuValue();

					// 更新detail
					CommodityDetails oldDetail=commodityDetailsMapper.selectByCommodityId(commodityId);
					commodityDetails.setCommodityId(commodityId);
					commodityDetails.setId(oldDetail.getId());
					commodityDetails.setVersion(oldDetail.getVersion());
					commodityDetailsMapper.updateByPrimaryKeySelective(commodityDetails);

					// base更新
					commodityBase.setVersion(cb.getVersion());
					commodityBase.setId(commodityId);
					commodityBaseMapper.updateByPrimaryKeySelective(commodityBase);

					// 指定可售卖家
					if (commodityBase.getBelongSeller() != null && commodityBase.getBelongSeller().size() > 0) {
						commodityBelongSellerMapper.deleteByCommodityId(commodityId);

						CommodityBelongSeller belongSeller = null;
						List<CommodityBelongSeller> list = new ArrayList<CommodityBelongSeller>();
						for (String seller : commodityBase.getBelongSeller()) {
							belongSeller = new CommodityBelongSeller();
							belongSeller.setCommodityId(commodityBase.getId());
							belongSeller.setSellerId(Long.parseLong(seller));
							list.add(belongSeller);
						}
						commodityBelongSellerMapper.insertBatch(list);
					}
					
					boolean addEsFlag = false;
					// SKU处理
					Map<String, String> chekemap = new HashMap<>();
					for (CommoditySpec comm : commoditySpec) {
						if (StringUtils.isBlank(comm.getSupplierSku())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), "", "失败", "供应商sku为空", errorList, optUser, importId);
							continue;
						}
						if (StringUtils.isBlank(comm.getCommodityNameCn())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品中文名称为空",errorList, optUser, importId);
							continue;
						}
						if (StringUtils.isBlank(comm.getCommodityNameEn())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品英文名称为空",errorList, optUser, importId);
							continue;
						}
						if (comm.getMasterPicture() == null) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "SKU主图为空",errorList, optUser, importId);
							continue;
						}
						if (comm.getPackingHeight() != null && !ValidatorUtil.isMathFloat(comm.getPackingHeight().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装尺寸-高度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getPackingLength() != null
								&& !ValidatorUtil.isMathFloat(comm.getPackingLength().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装尺寸-长度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getPackingWidth() != null
								&& !ValidatorUtil.isMathFloat(comm.getPackingWidth().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装尺寸-宽度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getPackingWeight() != null
								&& !ValidatorUtil.isMathFloat(comm.getPackingWeight().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "包装重量小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getCommodityHeight() != null
								&& !ValidatorUtil.isMathFloat(comm.getCommodityHeight().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品尺寸-高度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getCommodityLength() != null
								&& !ValidatorUtil.isMathFloat(comm.getCommodityLength().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品尺寸-长度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getCommodityWidth() != null
								&& !ValidatorUtil.isMathFloat(comm.getCommodityWidth().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品尺寸-宽度小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getCommodityWeight() != null
								&& !ValidatorUtil.isMathFloat(comm.getCommodityWeight().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品重量小数点后最多两位", errorList, optUser, importId);
							continue;
						}
						if (comm.getCommodityHeight() != null && (comm.getCommodityHeight().compareTo(comm.getPackingHeight())) > 0) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品高度不能大于包装高度", errorList, optUser, importId);
							continue;
						}
		                if (comm.getCommodityLength() != null && (comm.getCommodityLength().compareTo(comm.getPackingLength())) > 0) {
		                	addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品长度不能大于包装长度", errorList, optUser, importId);
							continue;
		                }
		                if (comm.getCommodityWidth() != null && (comm.getCommodityWidth().compareTo(comm.getPackingWidth())) > 0) {
		                	addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品宽度不能大于包装宽度", errorList, optUser, importId);
							continue;
		                }
		                if (comm.getCommodityWeight() != null && (comm.getCommodityWeight().compareTo(comm.getPackingWeight())) > 0) {
		                	addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品重量不能大于包装重量", errorList, optUser, importId);
							continue;
		                }
						if (comm.getCommodityPrice() == null
								|| !ValidatorUtil.isMathFloat(comm.getCommodityPrice().toString())) {
							addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "商品价格必须小数点后两位", errorList, optUser, importId);
							continue;
						}
						boolean specError=false;
						if (StringUtils.isNotBlank(comm.getCommoditySpec())) {
							if (chekemap.containsKey(comm.getCommoditySpec())) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(),"失败", "规格属性值重复", errorList, optUser, importId);
								continue;
							} else {
								chekemap.put(comm.getCommoditySpec(), comm.getCommoditySpec());
							}

							StringBuilder cnBuilder = new StringBuilder();// 中文规格
							StringBuilder enBuilder = new StringBuilder();// 英文规格
							Map<String, String> mm = new HashMap<>();
							String[] spac = comm.getCommoditySpec().split("\\|");
							for (int j = 0; j < spac.length; j++) {
								if (!spac[j].contains("):") || !spac[j].endsWith(")")) {
									addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(),"失败", "商品sku所选规格格式错误", errorList, optUser, importId);
									
									specError=true;
									break;
								}

								String str = spac[j].split(":")[0];
								if (mm.containsKey(str)) {
									addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(),"失败", "规格重复", errorList, optUser, importId);
									
									specError=true;
									break;
								}
								mm.put(str, str);

								String[] arr = spac[j].split(":");
								cnBuilder.append(arr[0].substring(0, arr[0].indexOf("("))).append(":")
										.append(arr[1].substring(0, arr[1].indexOf("("))).append("|");

								enBuilder.append(arr[0].substring(arr[0].indexOf("(") + 1, arr[0].indexOf(")")))
										.append(":")
										.append(arr[1].substring(arr[1].indexOf("(") + 1, arr[1].indexOf(")")))
										.append("|");

								if (cnBuilder.length() > 0) {
									comm.setCommoditySpec(cnBuilder.substring(0, cnBuilder.length() - 1));
								}
								if (enBuilder.length() > 0) {
									comm.setCommoditySpecEn(enBuilder.substring(0, enBuilder.length() - 1));
								}
							}
						}
						if (specError) {
							continue;
						}
						
						
						//旧数据存在，拿旧数据的品连sku赋值给新数据
						CommoditySpec spec = commoditySpecMapper.getSkuBySupplierIdAndSku(commodityBase.getSupplierId(),comm.getSupplierSku());
						if (spec != null) {
							if (spec.getState() == 3) {
								addEsFlag = true;
							}
							
							comm.setCommodityId(commodityId);
							comm.setSystemSku(spec.getSystemSku());
							comm.setVersion(spec.getVersion());
							comm.setState(spec.getState());
							if (spec.getFeeRate() != null) {
								comm.setFeeRate(spec.getFeeRate());
							}
							if (spec.getFeePrice() != null) {
								comm.setFeePrice(spec.getFeePrice());
							}
							
							// 设置美元
							if (rate != null) {
								if (comm.getCommodityPrice() != null) {
									comm.setCommodityPriceUs(comm.getCommodityPrice().multiply(rate).setScale(2, BigDecimal.ROUND_DOWN));
								}
								
								//仓库价格
								if (StringUtils.isNotBlank(comm.getWarehousePriceGroup())) {
									comm.setWarehousePriceGroup(comm.getWarehousePriceGroup().replaceAll("：", ":"));//防止他们填有中文冒号
									StringBuilder newWarehousePriceGroup=new StringBuilder();
									BigDecimal newPrice=null;
									String[] priceGroupArry=comm.getWarehousePriceGroup().split("\\|");
									if (priceGroupArry != null && priceGroupArry.length>0) {
										for (int k = 0; k < priceGroupArry.length; k++) {
											newPrice=new BigDecimal(priceGroupArry[k].split(":")[1]).multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
											newWarehousePriceGroup.append(priceGroupArry[k].split(":")[0]).append(":").append(newPrice).append("|");
										}
									}
									if (newWarehousePriceGroup.length()>0) {
										comm.setWarehousePriceGroup(newWarehousePriceGroup.substring(0, newWarehousePriceGroup.length()-1));
									}
								}
							}
							
							//删除旧的，插入新的
							commoditySpecMapper.deleteByPrimaryKey(spec.getId());
							int n = commoditySpecMapper.insertSelective(comm);
							if (n > 0) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "成功", "商品修改成功", successList, optUser, importId);
							}
						} else {
							// 追加的sku
							int hasSpec = commoditySpecMapper.getSkuByCommodityIdAndSpec(commodityId,comm.getCommoditySpec());
							if (hasSpec > 0) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "失败", "规格重复",errorList, optUser, importId);
								continue;
							}

							// 取出已有sku的最大systemSku
							String maxSku = commoditySpecMapper.getMaxSystemSkuBySpu(SPU);
							Integer maxSystemSkuInt = null;
							if (StringUtils.isNotBlank(maxSku)) {
								maxSystemSkuInt = Integer.parseInt(maxSku.substring(8));
							} else {
								maxSystemSkuInt = 0;
							}

							comm.setSystemSku(SPU + String.format(String.format("%%0%dd", 3), maxSystemSkuInt + 1));
							comm.setCommodityId(commodityId);
							
							// 设置美元
							if (rate != null) {
								comm.setCommodityPriceUs(comm.getCommodityPrice().multiply(rate).setScale(2, BigDecimal.ROUND_DOWN));
								
								//仓库价格
								if (StringUtils.isNotBlank(comm.getWarehousePriceGroup())) {
									comm.setWarehousePriceGroup(comm.getWarehousePriceGroup().replaceAll("：", ":"));//防止他们填有中文冒号
									StringBuilder newWarehousePriceGroup=new StringBuilder();
									BigDecimal newPrice=null;
									String[] priceGroupArry=comm.getWarehousePriceGroup().split("\\|");
									if (priceGroupArry != null && priceGroupArry.length>0) {
										for (int k = 0; k < priceGroupArry.length; k++) {
											newPrice=new BigDecimal(priceGroupArry[k].split(":")[1]).multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
											newWarehousePriceGroup.append(priceGroupArry[k].split(":")[0]).append(":").append(newPrice).append("|");
										}
									}
									if (newWarehousePriceGroup.length()>0) {
										comm.setWarehousePriceGroup(newWarehousePriceGroup.substring(0, newWarehousePriceGroup.length()-1));
									}
								}
							}
							
							//佣金率取分类设置的
							comm.setFeeRate(Double.parseDouble(feeRate+""));
							comm.setState(3);
							
							comm.setSupplierId(String.valueOf(commodityBase.getSupplierId()));
							int n = commoditySpecMapper.insertSelective(comm);
							if (n > 0) {
								addErrorLogList(record, commodityBase.getSupplierSpu(), comm.getSupplierSku(), "成功", "商品创建成功", successList, optUser, importId);
							}
						}
					}

					if (addEsFlag) {
						// 插入ES
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", commodityId);
						map.put("isUp", true);
						commodityService.initCommodityIndex(map);
					}

				} else {
					addErrorLogList(record, commodityBase.getSupplierSpu(),"", "失败", "商品不存在", errorList,optUser, importId);
					continue;
				}
			}
			
		} catch (Exception e) {
			log.error("导入商品异常", e);
			//另外开一个类，并且方法新开事务，不然这里的更新也会回滚
			//更新导入记录为导入失败
			skuImportService.updateImportRecord(importId, "导入失败，"+e.getMessage());
			throw e;
		}

		// 批量插入导入明细
		if (errorList.size() > 0) {
			log.info("存在错误的记录{}条", errorList.size());
			skuImportErrorRecordMapper.insertBatch(errorList);
		}
		if (successList.size() > 0) {
			log.info("插入成功的记录{}条", successList.size());
			skuImportErrorRecordMapper.insertBatch(successList);
		}

		StringBuilder sb = new StringBuilder();
		int total = baseList.size();
		int errorNum = errorList.size();
		int successNum = successList.size();
		sb.append("此次导入了").append(total).append("个商品，").append("成功").append(successNum).append("个商品，").append("失败").append(errorNum).append("个商品");
		return sb.toString();
	}

}
