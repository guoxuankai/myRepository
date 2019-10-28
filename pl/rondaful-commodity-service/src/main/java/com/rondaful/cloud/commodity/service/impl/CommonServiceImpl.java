package com.rondaful.cloud.commodity.service.impl;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.commodity.constant.CommonConstant;
import com.rondaful.cloud.commodity.constant.ComomodityIndexConst;
import com.rondaful.cloud.commodity.constant.RedisKeyConstant;
import com.rondaful.cloud.commodity.entity.*;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.enums.SkuOperateInfoEnum;
import com.rondaful.cloud.commodity.enums.WarehouseFirmEnum;
import com.rondaful.cloud.commodity.mapper.*;
import com.rondaful.cloud.commodity.rabbitmq.MQSender;
import com.rondaful.cloud.commodity.remote.RemoteOrderService;
import com.rondaful.cloud.commodity.remote.RemoteUserService;
import com.rondaful.cloud.commodity.service.CommodityJestService;
import com.rondaful.cloud.commodity.service.GoodCangService;
import com.rondaful.cloud.commodity.service.ICommodityService;
import com.rondaful.cloud.commodity.service.ICommonService;
import com.rondaful.cloud.commodity.service.MessageService;
import com.rondaful.cloud.commodity.service.SkuOperateLogService;
import com.rondaful.cloud.commodity.service.WmsPushService;
import com.rondaful.cloud.commodity.utils.DateUtil;
import com.rondaful.cloud.commodity.utils.SnowFlakeUtil;
import com.rondaful.cloud.commodity.utils.Utils;
import com.rondaful.cloud.commodity.utils.ValidatorUtil;
import com.rondaful.cloud.commodity.utils.WebFileUtils;
import com.rondaful.cloud.commodity.vo.ErpUpdateCommodityVo;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.FileService;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RemoteUtil;
import net.sf.json.JSONArray;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;

@Service
public class CommonServiceImpl implements ICommonService {

    private final static Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired
    private CommodityBaseMapper commodityBaseMapper;

    @Autowired
    private CommodityDetailsMapper commodityDetailsMapper;

    @Autowired
    private CommoditySpecMapper commoditySpecMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SystemSpuMapper systemSpuMapper;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private MQSender mqSender;

    //@Autowired
    //private CommodityOperateLogMapper commodityOperateLogMapper;
    
    @Autowired
    private ICommodityService commodityService;
    
    @Autowired
    private CommodityJestService commodityJestService;
    
    @Autowired
	private RedisUtils redisUtils;

    @Autowired
    private RemoteOrderService remoteOrderService;
    
    @Autowired
    private MessageService messageService;
    
    @Value("${rondaful.system.env}")
	public String env;

	@Resource
	private FileService fileService;
	
	@Autowired
	private BindAttributeMapper bindAttributeMapper;
	
	@Autowired
	private SkuOperateLogService skuOperateLogService;
	
	@Autowired
    private WmsPushService wmsPushService;
    
    @Autowired
    private GoodCangService goodCangService;
    
    @Autowired
   	private GoodCangMapper goodCangMapper;
	
    
    
    /**
     * 添加/编辑商品
     *
     * @param commodityBase
     * @param commodityDetails
     * @param commoditySpec
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
	public synchronized Map addCommodity(CommodityBase commodityBase, CommodityDetails commodityDetails,List<CommoditySpec> commoditySpec) {
		try {
			// 返回对象
			Map resultmap = new HashMap();
			List skuList = new ArrayList();

			BigDecimal rate = (BigDecimal) redisUtils.get(RedisKeyConstant.KEY_USD_RATE);
			if (rate == null) {
				Object obj = remoteOrderService.getRate("CNY", "USD");
				com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON.toJSON(obj);
				if (json != null) {
					String data = (String) json.get("data");
					if (data != null) {
						rate = new BigDecimal(data);
						redisUtils.set(RedisKeyConstant.KEY_USD_RATE, rate, 3600L);// 1小时
					} else {
						log.error("订单服务异常，获取美元利率为空");
					}
				}
			}

			// 分类校验
			Category category1 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel1());
			Category category2 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel2());
			if (category1 == null)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一级分类id不存在");
			if (category2 == null)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "二级分类id不存在");
			if (category2.getCategoryParentId().intValue() != category1.getId().intValue())
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id关联不匹配");
			if (commodityBase.getCategoryLevel3() != null) {
				Category category3 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel3());
				if (category3 == null)
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类id不存在");
				if (category3.getCategoryParentId().intValue() != category2.getId().intValue())
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id关联不匹配");
			} else {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类id不存在");
			}
			// 新增品牌
			List<Brand> brand = brandMapper.findBrandList(new Brand() {
				{
					this.setBrandName(commodityBase.getBrandName());
				}
			});
			if (brand.isEmpty()) {
				Brand b = new Brand();
				b.setCreatTime(DateUtil.getCurrentDate());
				b.setSupplierId(commodityBase.getSupplierId());
				b.setBrandName(commodityBase.getBrandName());
				b.setState(1);
				int result = brandMapper.insertSelective(b);
				if (result != 1)
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "新增品牌系统异常");
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

			// 根据供应商ID和供应商sku码判断是编辑还是新增
			boolean flag = false;
			CommodityBase cb = null;
			for (CommoditySpec comm : commoditySpec) {
				List<CommodityBase> cblist = null;
				if (StringUtils.isNotBlank(commodityBase.getSupplierSpu())) {
					cblist=commodityBaseMapper.selectBySupplierAndSpu(commodityBase.getSupplierId(),commodityBase.getSupplierSpu());
				}
				if (cblist == null) {
					cblist = commodityBaseMapper.queryBySupplierAndSku(commodityBase.getSupplierId(),comm.getSupplierSku().substring(0, 7));
					commodityBase.setSupplierSpu(comm.getSupplierSku().substring(0, 7));
				}
				
				if (cblist != null && cblist.size() > 0) {
					flag = true;
					cb = cblist.get(0);
					log.info("根据供应商Id：" + commodityBase.getSupplierId() + "+供应商sku：" + comm.getSupplierSku()
							+ "查询到CommodityBase，commodityId=" + cb.getId());
					break;
				}
			}

			if (flag && cb != null) {// 之前推过的商品，做编辑操作
				log.info("历史商品");

				Long commodityId = cb.getId();

				// SPU码
				SystemSpu systemSpu = systemSpuMapper.selectByPrimaryKey(cb.getSpuId());
				String SPU = systemSpu.getSpuValue();

				// 先删除所有detail
				commodityDetailsMapper.deleteCommodityDetailsByCommodityId(commodityId);

				// base更新
				commodityBase.setVersion(cb.getVersion());
				commodityBase.setId(commodityId);
				commodityBaseMapper.updateByPrimaryKeySelective(commodityBase);

				// 插入detail
				commodityDetails.setCommodityId(commodityId);
				commodityDetails.setId(null);
				commodityDetailsMapper.insertSelective(commodityDetails);
				
				// SKU处理
				List<String> skuImgList = null;
				SkuOperateLog skuLog=null;
				Map chekemap = new HashMap();
				for (CommoditySpec comm : commoditySpec) {
					String checkmsg=checkSpec(comm);
					if (StringUtils.isNotBlank(checkmsg)) {
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, checkmsg);
					}
					
					comm.setCommodityNameCn(comm.getCommodityNameCn().replaceAll("（","(").replaceAll("）", ")").replaceAll("\\(.*?）|（.*?\\)|\\(.*?\\)|（.*?）|\\[.*\\]", ""));

					if (StringUtils.isNotBlank(comm.getCommoditySpec())) {
						if (chekemap.containsKey(comm.getCommoditySpec())) {
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格属性值重复");
						} else {
							chekemap.put(comm.getCommoditySpec(), comm.getCommoditySpec());
						}
						
						//1022需求，要求erp的拼接用==，然后就可以把erp过来的属性里的冒号全去掉
						comm.setCommoditySpec(comm.getCommoditySpec().replaceAll(":", "").replaceAll("：", ""));

						StringBuilder cnBuilder = new StringBuilder();// 中文规格
						StringBuilder enBuilder = new StringBuilder();// 英文规格
						Map<String, String> mm = new HashMap<>();
						String[] spac = comm.getCommoditySpec().split("\\|");
						for (int j = 0; j < spac.length; j++) {

							String specName = spac[j].split(":")[0];
							String specValue = spac[j].split(":")[1];
							if (mm.containsKey(specName)) {
								throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格重复");
							}
							mm.put(specName, specName);

							// 查询属性映射，如果不存在，则中英都直接用erp的
							BindAttribute bindAttr = bindAttributeMapper.getByErpNameAndValue(specName, specValue);
							if (bindAttr != null) {
								cnBuilder.append(bindAttr.getAttrCnName()).append(":").append(bindAttr.getAttrCnValue()).append("|");
								enBuilder.append(bindAttr.getAttrEnName()).append(":").append(bindAttr.getAttrEnValue()).append("|");
							} else {
								cnBuilder.append(specName.replace("(", "").replace(")", "").replace(":", "").replace("|", ""))
									.append(":")
									.append(specValue.replace("(", "").replace(")", "").replace(":", "").replace("|", ""))
									.append("|");
								enBuilder.append(specName.replace("(", "").replace(")", "").replace(":", "").replace("|", ""))
									.append(":")
									.append(specValue.replace("(", "").replace(")", "").replace(":", "").replace("|", ""))
									.append("|");
							}
						}
						if (cnBuilder.length() > 0) {
							comm.setCommoditySpec(cnBuilder.substring(0, cnBuilder.length() - 1));
						}
						if (enBuilder.length() > 0) {
							comm.setCommoditySpecEn(enBuilder.substring(0, enBuilder.length() - 1));
						}
					}

					CommoditySpec spec = commoditySpecMapper.getSkuBySupplierIdAndSku(commodityBase.getSupplierId(),comm.getSupplierSku());
					if (spec != null) {
						if (spec.getState() == 3) {
							// 价格变动，发cms消息通知
							if (comm.getCommodityPrice().compareTo(spec.getCommodityPrice()) != 0) {
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
													comm.getSystemSku(),
													valueSet.toString().substring(valueSet.toString().indexOf("[") + 1,
															valueSet.toString().indexOf("]")));
										}
									}
								}
							}
						}
						log.info("供应商id：" + commodityBase.getSupplierId() + "+供应商sku：" + comm.getSupplierSku() + "的sku存在，进行更新操作");
						comm.setId(spec.getId());
						comm.setVersion(spec.getVersion());
						comm.setState(3);
						comm.setCommodityPrice(comm.getCommodityPrice().multiply(new BigDecimal(1.05)).setScale(2,BigDecimal.ROUND_DOWN));
						if (rate != null) {
							comm.setCommodityPriceUs(comm.getCommodityPrice().multiply(rate).setScale(2, BigDecimal.ROUND_DOWN));
						}
						if (spec.getFeeRate() != null) {
							comm.setFeeRate(spec.getFeeRate());
						}
						if (spec.getFeePrice() != null) {
							comm.setFeePrice(spec.getFeePrice());
						}
						
						//删除之前的图片
						skuImgList = new ArrayList<>();
						if (StringUtils.isNotBlank(spec.getMasterPicture())) {
							skuImgList.addAll(Arrays.asList(spec.getMasterPicture().split("\\|")));
						}
						if (StringUtils.isNotBlank(spec.getAdditionalPicture())) {
							skuImgList.addAll(Arrays.asList(spec.getAdditionalPicture().split("\\|")));
						}
						if (skuImgList.size()>0) {
							fileService.deleteList(skuImgList);
						}

						commoditySpecMapper.updateByPrimaryKeySelective(comm);
						
						//插入操作日志
		                skuLog=new SkuOperateLog();
		                skuLog.setOperateBy("ERP接口");
		                skuLog.setOperateInfo(SkuOperateInfoEnum.ERP_UPDATE.getMsg());
		                skuLog.setSystemSku(spec.getSystemSku());
		                skuOperateLogService.addSkuLog(skuLog);
		                
		                // 查询推送记录，推送成功的，则调用编辑接口
		                Map<String, Object> param=new HashMap<String, Object>();
		    			param.put("systemSku",comm.getSystemSku());
		    			param.put("pushState",1);
		    			List<SkuPushRecord> recordList=goodCangMapper.querySkuPushRecord(param);
		    			if (recordList != null && recordList.size()>0) {
		    				List<String> skuList2=new ArrayList<String>();
		    				skuList2.add(comm.getSystemSku());
		    				List<CommoditySpec> commoditySpecs=commoditySpecMapper.selectCommoditySpecBySku(skuList2);
							for (SkuPushRecord record : recordList) {
								if (WarehouseFirmEnum.WMS.getCode().equals(record.getWarehouseProviderCode())) {
									wmsPushService.addProduct(record.getAccountId(), 2, commoditySpecs, "ERP接口");
								}else if (WarehouseFirmEnum.GOODCANG.getCode().equals(record.getWarehouseProviderCode())) {
									if ("R".equals(record.getProductState())) {
										goodCangService.pushSkusToGoodCang(record.getAccountId(), 2, commoditySpecs, "ERP接口");
									}
								}
							}
						}

					} else {// 追加的sku
						log.info("供应商id：" + commodityBase.getSupplierId() + "+供应商sku：" + comm.getSupplierSku() + "的sku不存在，进行新增操作");
						int hasSpec = commoditySpecMapper.getSkuByCommodityIdAndSpec(commodityId,comm.getCommoditySpec());
						if (hasSpec > 0) {
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格重复");
						}

						// 取出已有sku的最大systemSku
						String maxSku = "";
						if (SPU.contains("-")) {
							String snow = String.valueOf(SnowFlakeUtil.nextId());
							maxSku = snow.substring(snow.length() - 8, snow.length())+"001";
						}else {
							maxSku = commoditySpecMapper.getMaxSystemSkuBySpu(SPU);
						}
						Integer maxSystemSkuInt = null;
						if (StringUtils.isNotBlank(maxSku)) {
							maxSystemSkuInt = Integer.parseInt(maxSku.substring(8));
						} else {
							maxSystemSkuInt = 0;
						}

						comm.setSystemSku(SPU + String.format(String.format("%%0%dd", 3), maxSystemSkuInt + 1));
						comm.setCommodityId(commodityId);
						// 商品价格提升5%,佣金设置为5%
						comm.setCommodityPrice(comm.getCommodityPrice().multiply(new BigDecimal(1.05)).setScale(2,BigDecimal.ROUND_DOWN));
						if (rate != null) {
							comm.setCommodityPriceUs(comm.getCommodityPrice().multiply(rate).setScale(2, BigDecimal.ROUND_DOWN));
						}
						comm.setFeeRate(5d);
						// 商品推过来，状态设置为已上架
						comm.setState(3);
						comm.setSupplierId(String.valueOf(commodityBase.getSupplierId()));
						commoditySpecMapper.insertSelective(comm);
						
						//插入操作日志
		                skuLog=new SkuOperateLog();
		                skuLog.setOperateBy("ERP接口");
		                skuLog.setOperateInfo(SkuOperateInfoEnum.ERP_ADD.getMsg());
		                skuLog.setSystemSku(comm.getSystemSku());
		                skuOperateLogService.addSkuLog(skuLog);
					}
					skuList.add(comm);
				}
				resultmap.put("commodityId", commodityId);
				resultmap.put("skuList", skuList);

				// 插入ES
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", commodityId);
				map.put("isUp", true);
				commodityService.initCommodityIndex(map);

			} else {// 新增
				log.info("根据供应商Id和供应商sku未能查询到已存在的CommodityBase，进入新增环节");
				if (commodityBase.getSupplierId() != null) {
					RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>() {
						{
							this.add(commodityBase.getSupplierId());
						}
					}, 0));
					List<Map> list = RemoteUtil.getList();
					if (list == null || list.isEmpty())
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不存在");
				}

				Long commodityId = null;
				String SPU = "";
				Integer maxSystemSkuInt = null;
				CommodityBase base = null;

				// 根据supplierId+supplierSpu查询base
				if (StringUtils.isNotBlank(commodityBase.getSupplierSpu())) {
					List<CommodityBase> cbList=commodityBaseMapper.selectBySupplierAndSpu(commodityBase.getSupplierId(),commodityBase.getSupplierSpu());
					if (cbList != null && cbList.size()>0) {
						base = cbList.get(0);
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
					log.info("erp推送商品，获取到新的SPU={}", SPU);

					SystemSpu ss = new SystemSpu();
					ss.setCategoryLevel1(category1.getId());
					ss.setCategoryLevel2(category2.getId());
					ss.setCreatTime(DateUtil.DateToString(new Date()));
					ss.setSpuValue(SPU);
					systemSpuMapper.insertSelective(ss);

					commodityBase.setSpuId(ss.getId());
					commodityBaseMapper.insertSelective(commodityBase);
					commodityId = commodityBase.getId();
				} else {
					log.info("根据供应商Id：" + commodityBase.getSupplierId() + "和供应商spu：" + commodityBase.getSupplierSpu()
							+ "查询到已存在的CommodityBase，commodityId=" + base.getId() + "，在base下追加sku");
					// base存在
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

				// SKU处理
				SkuOperateLog skuLog=null;
				Map chekemap = new HashMap();
				int i = maxSystemSkuInt == null ? 1 : maxSystemSkuInt + 1;
				for (CommoditySpec comm : commoditySpec) {
					String checkmsg=checkSpec(comm);
					if (StringUtils.isNotBlank(checkmsg)) {
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, checkmsg);
					}
					
					comm.setCommodityNameCn(comm.getCommodityNameCn().replaceAll("\\(.*?）|（.*?\\)|\\(.*?\\)|（.*?）|\\[.*\\]", ""));
					
					if (StringUtils.isNotBlank(comm.getCommoditySpec())) {
						if (chekemap.containsKey(comm.getCommoditySpec())) {
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格属性值重复");
						} else {
							chekemap.put(comm.getCommoditySpec(), comm.getCommoditySpec());
						}
						
						//1022需求，要求erp的拼接用==，然后就可以把erp过来的属性里的冒号全去掉
						comm.setCommoditySpec(comm.getCommoditySpec().replaceAll(":", "").replaceAll("：", ""));

						StringBuilder cnBuilder = new StringBuilder();// 中文规格
						StringBuilder enBuilder = new StringBuilder();// 英文规格
						Map<String, String> mm = new HashMap<>();
						String[] spac = comm.getCommoditySpec().split("\\|");
						for (int j = 0; j < spac.length; j++) {
							String specName = spac[j].split(":")[0];
							String specValue = spac[j].split(":")[1];
							if (mm.containsKey(specName)) {
								throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格重复");
							}
							mm.put(specName, specName);

							// 查询属性映射，如果不存在，则中英都直接用erp的
							BindAttribute bindAttr = bindAttributeMapper.getByErpNameAndValue(specName, specValue);
							if (bindAttr != null) {
								cnBuilder.append(bindAttr.getAttrCnName()).append(":").append(bindAttr.getAttrCnValue()).append("|");
								enBuilder.append(bindAttr.getAttrEnName()).append(":").append(bindAttr.getAttrEnValue()).append("|");
							} else {
								cnBuilder.append(specName.replace("(", "").replace(")", "").replace(":", "").replace("|", ""))
									.append(":")
									.append(specValue.replace("(", "").replace(")", "").replace(":", "").replace("|", ""))
									.append("|");
								enBuilder.append(specName.replace("(", "").replace(")", "").replace(":", "").replace("|", ""))
									.append(":")
									.append(specValue.replace("(", "").replace(")", "").replace(":", "").replace("|", ""))
									.append("|");
							}
						}
						if (cnBuilder.length() > 0) {
							comm.setCommoditySpec(cnBuilder.substring(0, cnBuilder.length() - 1));
						}
						if (enBuilder.length() > 0) {
							comm.setCommoditySpecEn(enBuilder.substring(0, enBuilder.length() - 1));
						}
					}

					int hasSpec = commoditySpecMapper.getSkuByCommodityIdAndSpec(commodityId, comm.getCommoditySpec());
					if (hasSpec > 0) {
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格重复");
					}

					// 增加系统sku
					comm.setSystemSku(SPU + String.format(String.format("%%0%dd", 3), i));
					comm.setCommodityId(commodityId);
					// 商品价格提升5%,佣金设置为5%
					comm.setCommodityPrice(comm.getCommodityPrice().multiply(new BigDecimal(1.05)).setScale(2, BigDecimal.ROUND_DOWN));
					// 设置美元
					if (rate != null) {
						comm.setCommodityPriceUs(comm.getCommodityPrice().multiply(rate).setScale(2, BigDecimal.ROUND_DOWN));
					}
					comm.setFeeRate(5d);
					// 商品推过来，状态设置为审核中
					comm.setState(3);
					comm.setSupplierId(String.valueOf(commodityBase.getSupplierId()));
					commoditySpecMapper.insertSelective(comm);
					skuList.add(comm);
					i++;
					
					//插入操作日志
					skuLog=new SkuOperateLog();
					skuLog.setOperateBy("ERP接口");
					skuLog.setOperateInfo(SkuOperateInfoEnum.ERP_ADD.getMsg());
					skuLog.setSystemSku(comm.getSystemSku());
					skuOperateLogService.addSkuLog(skuLog);
				}
				// MQ通知
				constructionSupplierForSpec(commoditySpec);// 先构造供应商信息
				mqSender.commoditySkuAdd(JSONArray.fromObject(commoditySpec).toString());

				resultmap.put("commodityId", commodityId);
				resultmap.put("skuList", skuList);
				
				// 插入ES
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", commodityId);
				map.put("isUp", true);
				commodityService.initCommodityIndex(map);
			}
			return resultmap;
		} catch (Exception e) {
			log.error("erp推送商品异常", e);
			throw e;
		}
	}
    
    
    private String checkSpec(CommoditySpec comm) {
    	String msg="";
    	if (StringUtils.isBlank(comm.getCommodityNameCn())) {
    		msg="商品中文名称不能为空";
		}
		if (StringUtils.isBlank(comm.getCommodityNameEn())) {
			msg="商品英文名称不能为空";
		}
		if (StringUtils.isBlank(comm.getMasterPicture())) {
			msg="SKU主图不能为空";
		}
		if (comm.getPackingHeight() != null && !ValidatorUtil.isMathFloat(comm.getPackingHeight().toString())) {
			msg="包装尺寸-高度小数点后最多两位";
		}
		if (comm.getPackingLength() != null && !ValidatorUtil.isMathFloat(comm.getPackingLength().toString())) {
			msg="包装尺寸-长度小数点后最多两位";
		}
		if (comm.getPackingWidth() != null && !ValidatorUtil.isMathFloat(comm.getPackingWidth().toString())) {
			msg="包装尺寸-宽度小数点后最多两位";
		}
		if (comm.getPackingWeight() != null && !ValidatorUtil.isMathFloat(comm.getPackingWeight().toString())) {
			msg="包装重量小数点后最多两位";
		}
		if (comm.getCommodityHeight() != null && !ValidatorUtil.isMathFloat(comm.getCommodityHeight().toString())) {
			msg="商品尺寸-高度小数点后最多两位";
		}
		if (comm.getCommodityLength() != null && !ValidatorUtil.isMathFloat(comm.getCommodityLength().toString())) {
			msg="商品尺寸-长度小数点后最多两位";
		}
		if (comm.getCommodityWidth() != null && !ValidatorUtil.isMathFloat(comm.getCommodityWidth().toString())) {
			msg="商品尺寸-宽度小数点后最多两位";
		}
		if (comm.getCommodityWeight() != null && !ValidatorUtil.isMathFloat(comm.getCommodityWeight().toString())) {
			msg="商品重量小数点后最多两位";
		}
		if (comm.getCommodityHeight() != null && (comm.getCommodityHeight().compareTo(comm.getPackingHeight())) > 0) {
			msg="商品尺寸-高度不能大于包装尺寸-高度";
		}
        if (comm.getCommodityLength() != null && (comm.getCommodityLength().compareTo(comm.getPackingLength())) > 0) {
        	msg="商品尺寸-长度不能大于包装尺寸-长度";
        }
        if (comm.getCommodityWidth() != null && (comm.getCommodityWidth().compareTo(comm.getPackingWidth())) > 0) {
        	msg="商品尺寸-宽度不能大于包装尺寸-宽度";
        }
        if (comm.getCommodityWeight() != null && (comm.getCommodityWeight().compareTo(comm.getPackingWeight())) > 0) {
        	msg="商品重量不能大于包装重量";
        }
		if (comm.getCommodityPrice() == null || !ValidatorUtil.isMathFloat(comm.getCommodityPrice().toString())) {
			msg="商品价格必须小数点后两位";
		}
		if (StringUtils.isBlank(comm.getSupplierSku())) {
			msg="供应商sku不能为空";
		}
    	return msg;
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


    @Transactional(rollbackFor={RuntimeException.class,Exception.class})
	@Override
	public void UpOrDownStateCommodity(ErpUpdateCommodityVo vo) {
    	log.debug("ERP调用商品上下架接口，参数===》{}",JSON.toJSON(vo).toString());
		for (String supplierSku : vo.getSupplierSkuList()) {
			CommoditySpec sku = commoditySpecMapper.getSkuBySupplierIdAndSku(Long.valueOf(vo.getSupplierId()),supplierSku);
			if (sku != null) {
				if ("1".equals(vo.getOptType())) {//上架
					sku.setState(3);//已上架
					sku.setDownStateType(0);
					mqSender.skuUp(sku.getSystemSku());
				}else {
					sku.setState(1);//下架改成待上架
					sku.setDownStateType(2);//ERP接口下架
	                mqSender.commodityLowerframes(sku.getSystemSku());
	                mqSender.skuDown(sku.getSystemSku());
	                
	                //cms通知
	                RemoteUtil.invoke(remoteOrderService.queryMapsNoLimit(sku.getSystemSku()));
	                List<Map> list = RemoteUtil.getList();
	                if (list != null && list.size() > 0) {
	                	Set<String> keySet=new HashSet<String>();
	                	for (Map map : list) {
	                		StringBuilder sb=new StringBuilder();
	                        String userId = (String) map.get("sellerPlId");
	                        String account = (String) map.get("sellerPlAccount");
	                        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(account)) {
	                        	keySet.add(sb.append(userId).append("==").append(account).toString());
	    					}
	                    }
	                	for (String key : keySet) {
	                		Set<String> valueSet=new HashSet<String>();
	                		for (Map map : list) {
	                			StringBuilder sb=new StringBuilder();
	                            String userId = (String) map.get("sellerPlId");
	                            String account = (String) map.get("sellerPlAccount");
	                            String platform = (String) map.get("platform");
	                            String key2=sb.append(userId).append("==").append(account).toString();
	                            if (key.equals(key2)) {
	                            	valueSet.add(platform);
								}
	                        }
	                		if (valueSet.size() > 0) {
	                			messageService.downStateMsg(key.split("==")[0], key.split("==")[1], sku.getSystemSku(), 
	                					valueSet.toString().substring(valueSet.toString().indexOf("[") + 1, valueSet.toString().indexOf("]")));
	                        }
						}
	                }
	        		
	                //下架的是最后一个上架状态的SKU
	                int count=commoditySpecMapper.getSpecCount(sku.getCommodityId(), sku.getId(),3);
	                if (count==0) {
	                	//删除ES文档
	                	commodityJestService.deleteItem(ComomodityIndexConst.INDEX_NAME, ComomodityIndexConst.TYPE_NAME, sku.getCommodityId()+"");
	    			}
				}
				commoditySpecMapper.updateByPrimaryKeySelective(sku);
				
				if ("1".equals(vo.getOptType())) {
					//插入ES
	            	CommodityBase base=commodityBaseMapper.selectByPrimaryKey(sku.getCommodityId());
	            	Map<String, Object> map=new HashMap<String, Object>();
	            	map.put("id", base.getId());
	            	map.put("isUp", true);
	            	commodityService.initCommodityIndex(map);
				}
				
				//记录操作日志
	            /*commodityOperateLogMapper.insertSelective(new CommodityOperateLog(){{
	                this.setOperateBy("ERP接口");
	                this.setCommodityId(sku.getCommodityId());
	                this.setCreatTime(DateUtil.getCurrentDate());
	                this.setCommodityStat("商品上下架操作");
	                if ("1".equals(vo.getOptType())) {
	                    this.setOperateInfo("SKU" + sku.getSystemSku() + "商品上架");
	                } else {
	                    this.setOperateInfo("SKU" + sku.getSystemSku() + "商品下架");
	                }
	            }});*/
	            
	            //插入操作日志
	            SkuOperateLog skuLog=new SkuOperateLog();
                skuLog.setOperateBy("ERP接口");
                if ("1".equals(vo.getOptType())) {
                	skuLog.setOperateInfo(SkuOperateInfoEnum.ERP_UP.getMsg());
                } else {
                	skuLog.setOperateInfo(SkuOperateInfoEnum.ERP_DOWN.getMsg());
                }
                skuLog.setSystemSku(sku.getSystemSku());
                skuOperateLogService.addSkuLog(skuLog);
			}
		}
	}


	@Override
	public StringBuilder uploadImg(String[] urlArr) {
		StringBuilder sb=new StringBuilder();
		Map<String, Object> map=null;
		for (int i = 0; i < urlArr.length; i++) {
			String masterPic = urlArr[i];
			int beginIndex=masterPic.lastIndexOf("/")+1;
			int endIndex=masterPic.length();
			if (masterPic.contains("?")) {
				endIndex=masterPic.indexOf("?");
			}
			String originalFilename=masterPic.substring(beginIndex, endIndex);
			InputStream inStream=WebFileUtils.getFileInputStreamByUrl(masterPic);
			if (inStream != null) {
				map = new HashMap<>();
				map.put(originalFilename, inStream);
				Map<String, String> afterMap=CommonConstant.getName(fileService.uploadMultipleFile(ConstantAli.getEnv(env), 
						ConstantAli.getFolder("product"), map, null, null, null));
				String afterReplaceUrl="";
				Iterator it = afterMap.entrySet().iterator();
				while (it.hasNext()){
					Map.Entry entry = (Map.Entry) it.next();
					afterReplaceUrl = (String) entry.getValue();
				}
				if (StringUtils.isNotBlank(afterReplaceUrl) && !"null".equals(afterReplaceUrl)) {
					sb.append(afterReplaceUrl).append("|");
				}
			}
		}
		return sb;
	}

}
