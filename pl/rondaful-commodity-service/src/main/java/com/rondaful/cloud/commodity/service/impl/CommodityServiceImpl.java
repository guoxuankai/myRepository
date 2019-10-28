package com.rondaful.cloud.commodity.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.common.Constant;
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
import com.rondaful.cloud.commodity.remote.RemoteSupplierService;
import com.rondaful.cloud.commodity.remote.RemoteUserService;
import com.rondaful.cloud.commodity.service.CommodityJestService;
import com.rondaful.cloud.commodity.service.GoodCangService;
import com.rondaful.cloud.commodity.service.ICommodityService;
import com.rondaful.cloud.commodity.service.MessageService;
import com.rondaful.cloud.commodity.service.SkuOperateLogService;
import com.rondaful.cloud.commodity.service.WmsPushService;
import com.rondaful.cloud.commodity.utils.*;
import com.rondaful.cloud.commodity.vo.CodeAndValueVo;
import com.rondaful.cloud.commodity.vo.CommoditySearchVo;
import com.rondaful.cloud.commodity.vo.QuerySkuBelongSellerVo;
import com.rondaful.cloud.commodity.vo.SkuInventoryVo;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.rabbitmq.MessageSender;
import com.rondaful.cloud.common.service.FileService;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RemoteUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.ZipOutputStream;

@Service
public class CommodityServiceImpl implements ICommodityService {

    private final static Logger log = LoggerFactory.getLogger(CommodityServiceImpl.class);

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
    private AttributeMapper attributeMapper;

    @Autowired
    private SystemSpuMapper systemSpuMapper;

    @Autowired
    private CommodityFocusMapper commodityFocusMapper;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Autowired
    private RemoteSupplierService remoteSupplierService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private SpuCategoryMapper spuCategoryMapper;

    @Autowired
    private SiteCategoryMapper siteCategoryMapper;

    @Autowired
    private CommodityJestService commodityJestService;

    @Autowired
    private RemoteOrderService remoteOrderService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CommodityLimitSaleMapper commodityLimitSaleMapper;

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private CommodityBelongSellerMapper commodityBelongSellerMapper;
    
    @Resource
	private FileService fileService;
    
    @Value("${rondaful.system.env}")
	public String env;
    
    @Autowired
    private BindAttributeMapper bindAttributeMapper;
    
    @Autowired
    private SkuTortRecordMapper skuTortRecordMapper;
    
    @Autowired
    private BindCategoryAliexpressMapper bindCategoryAliexpressMapper;
    
    @Autowired
    private PublishPackRecordMapper publishPackRecordMapper;
    
    @Autowired
	private SkuOperateLogService skuOperateLogService;
    
    @Autowired
	private GoodCangMapper goodCangMapper;
    
    @Autowired
    private WmsPushService wmsPushService;
    
    @Autowired
    private GoodCangService goodCangService;
    
    //@Autowired
    //private  SkuWarehouseInfoMapper skuWarehouseInfoMapper;


    /**
     * 添加商品
     *
     * @param commodityBase
     * @param commodityDetails
     * @param commoditySpec
     */
    @Override
    public synchronized void addCommodity(CommodityBase commodityBase, CommodityDetails commodityDetails, List<CommoditySpec> commoditySpec) {
        try {
        	//日志操作人
        	String optUser="";
            UserAll userAll=getLoginUserInformationByToken.getUserInfo();
            if (userAll!=null) {
            	UserCommon user = userAll.getUser();
            	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
            		optUser="【供应商】"+user.getUsername();
            	}else if (UserEnum.platformType.CMS.getPlatformType().equals(user.getPlatformType())) {//管理平台
            		optUser="【品连】"+user.getUsername();
            	}
            }
            
            //获取美元利率
            BigDecimal rate = null;
            Object obj = remoteOrderService.getRate("CNY", "USD");
			com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON.toJSON(obj);
			if (json != null) {
				String data = (String) json.get("data");
				if (data != null) {
					rate = new BigDecimal(data);
				} else {
					 throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单服务异常，获取美元利率为空");
				}
			}
            
            Long supplierId = null;
            if (commodityBase.getSupplierId() != null) {
                RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>() {{
                    this.add(commodityBase.getSupplierId());
                }}, 0));
                List<Map> list = RemoteUtil.getList();
                if (list == null || list.isEmpty())
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不存在");
                Map user = list.get(0);
                if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商不存在");
                Integer type = (Integer) user.get("platformType");
                Integer userId = (Integer) user.get("userId");
                Integer parentId = (Integer) user.get("topUserId");
                //0供应商平台  1卖家平台  2管理平台
                if (type != null && type.intValue() == 0 && userId != null) {
                    if (parentId.intValue() != 0) {
                        supplierId = Long.valueOf(parentId);
                    } else {
                        supplierId = Long.valueOf(userId);
                    }
                }
            }
            commodityBase.setSupplierId(supplierId);
            SystemSpu ss = null;
            Category category1 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel1());
            Category category2 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel2());
            if (category1 == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一级分类id不存在");
            if (category2 == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "二级分类id不存在");
            if (category2.getCategoryParentId().intValue() != category1.getId().intValue())
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id关联不匹配");
            if (commodityBase.getCategoryLevel3() != null) {
                Category category3 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel3());
                if (category3 == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类id不存在");
                if (category3.getCategoryParentId().intValue() != category2.getId().intValue())
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id关联不匹配");
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类id不存在");
            }
            
            //佣金率
            Integer feeRate=category1.getFeeRate();
            
            String snow = String.valueOf(SnowFlakeUtil.nextId());
            String SPU = snow.substring(snow.length() - 8, snow.length());
            ss = new SystemSpu();
            ss.setCategoryLevel1(category1.getId());
            ss.setCategoryLevel2(category2.getId());
            ss.setCreatTime(DateUtil.DateToString(new Date()));
            ss.setSpuValue(SPU);
            systemSpuMapper.insertSelective(ss);
            //无品牌的商品，前端传-1
            if (commodityBase.getBrandId() > 0) {
                Brand brand = brandMapper.selectByPrimaryKey(commodityBase.getBrandId());
                if (brand == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌id不存在");
            }
            commodityBase.setSpuId(ss.getId());
            commodityBaseMapper.insertSelective(commodityBase);
            commodityDetails.setCommodityId(commodityBase.getId());
            commodityDetailsMapper.insertSelective(commodityDetails);

            //禁售国家
            if (commodityBase.getLimitCountry() != null && commodityBase.getLimitCountry().size() > 0) {
                CommodityLimitSale limitSale = null;
                List<CommodityLimitSale> limitSaleList = new ArrayList<CommodityLimitSale>();
                for (String country : commodityBase.getLimitCountry()) {
                    limitSale = new CommodityLimitSale();
                    limitSale.setCommodityId(commodityBase.getId());
                    limitSale.setCode(country);
                    limitSale.setCodeType(1);
                    limitSaleList.add(limitSale);
                }
                commodityLimitSaleMapper.insertBatch(limitSaleList);
            }
            
            //指定可售卖家
            if (commodityBase.getBelongSeller() != null && commodityBase.getBelongSeller().size() > 0) {
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

            List<String> skuList=new ArrayList<String>();
            SkuOperateLog skuLog=null;
            Map<String,String> chekemap = new HashMap<>();
            int i = 1;
            for (CommoditySpec comm : commoditySpec) {
            	String checkmsg=checkSpec(comm);
				if (StringUtils.isNotBlank(checkmsg)) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, checkmsg);
				}
                if (rate != null) {
                    comm.setCommodityPrice(comm.getCommodityPriceUs().divide(rate, 2, BigDecimal.ROUND_DOWN));
                }

                if (StringUtils.isNotBlank(comm.getSupplierSku())) {
                    List<CommodityBase> cblist = commodityBaseMapper.selectBySupplierAndSku(commodityBase.getSupplierId(), comm.getSupplierSku());
                    if (cblist != null && !cblist.isEmpty()) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "同一个供应商的sku码不能重复");
                    }
                    if (ValidatorUtil.containChinese(comm.getSupplierSku())) {
                    	 throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商sku不能包含汉字");
					}
                    if (comm.getSupplierSku().length()<3 || comm.getSupplierSku().length()>16) {
                   	 	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商sku长度范围3~16");
					}
                }

                if (StringUtils.isNotBlank(comm.getCommoditySpec())) {
                	if (chekemap.containsKey(comm.getCommoditySpec())) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格属性值重复");
                    } else {
                        chekemap.put(comm.getCommoditySpec(), comm.getCommoditySpec());
                    }
                	
                	//拆分中英属性组合
                	StringBuilder cnBuilder=new StringBuilder();
           		 	StringBuilder enBuilder=new StringBuilder();
           		 	String[] specArray = comm.getCommoditySpec().split("\\|");
                    for (int j = 0; j < specArray.length; j++) {
                        String[] arr = specArray[j].split(":");
                        cnBuilder.append(arr[0].substring(0, arr[0].indexOf("(")))
                        	.append(":")
                        	.append(arr[1].substring(0,arr[1].indexOf("(")))
                        	.append("|");
                        
                        enBuilder.append(arr[0].substring(arr[0].indexOf("(") + 1, arr[0].lastIndexOf(")")))
                     		.append(":")
                     		.append(arr[1].substring(arr[1].indexOf("(") + 1, arr[1].lastIndexOf(")")))
                     		.append("|");
                    }
                    
                    comm.setCommoditySpec(cnBuilder.substring(0, cnBuilder.length()-1));
                    comm.setCommoditySpecEn(enBuilder.substring(0, enBuilder.length()-1));
                    
                    //校验是否有重复
                    Map<String,String> mm = new HashMap<>();
                    String[] spac = comm.getCommoditySpec().split("\\|");
                    for (int j = 0; j < spac.length; j++) {
                        String str = spac[j].split(":")[0];
                        if (mm.containsKey(str)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格重复");
                        mm.put(str, str);
                    }
                }
                
                if (StringUtils.isNotBlank(comm.getCommoditySpecEn())) {
                    Map<String,String> mm = new HashMap<>();
                    String[] spac = comm.getCommoditySpecEn().split("\\|");
                    for (int j = 0; j < spac.length; j++) {
                        String str = spac[j].split(":")[0];
                        if (mm.containsKey(str)) {
                        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "英文规格重复");
                        }
                        mm.put(str, str);
                    }
                }
                
                //新增商品时没设置佣金率，取分类的
                if (comm.getFeeRate() == null && comm.getFeePrice() == null) {
                	comm.setFeeRate(Double.parseDouble(String.valueOf(feeRate)));
				}
                
                //多仓库价格转RMB存一份
                if (StringUtils.isNotBlank(comm.getWarehousePriceGroup())) {
					StringBuilder newWarehousePriceGroup=new StringBuilder();
					BigDecimal newPrice=null;
					String[] priceGroupArry=comm.getWarehousePriceGroup().split("\\|");
					if (priceGroupArry != null && priceGroupArry.length>0) {
						for (int k = 0; k < priceGroupArry.length; k++) {
							newPrice=new BigDecimal(priceGroupArry[k].split(":")[1]).divide(rate, 2, BigDecimal.ROUND_DOWN);
							newWarehousePriceGroup.append(priceGroupArry[k].split(":")[0]).append(":").append(newPrice).append("|");
						}
					}
					if (newWarehousePriceGroup.length()>0) {
						comm.setWarehousePriceGroupRmb(newWarehousePriceGroup.substring(0, newWarehousePriceGroup.length()-1));
					}
				}

                
                //生成sku
                String systemSku=SPU + String.format(String.format("%%0%dd", 3), i);
                
                //仓库价格等信息处理,前端只需传仓库ID，备货天数，附加费用
                /*if (comm.getSkuWarehouseInfoList()==null || comm.getSkuWarehouseInfoList().size()==0) {
                	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库不能为空");
				}else {
					List<SkuWarehouseInfo> warehouseInfos=new ArrayList<SkuWarehouseInfo>();
					for (SkuWarehouseInfo warehouseInfo : comm.getSkuWarehouseInfoList()) {
						if (warehouseInfo.getWarehouseId()==null) {
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库不能为空");
						}
						if (warehouseInfo.getStockDay()==null || warehouseInfo.getStockDay()<=0 || warehouseInfo.getStockDay()>99) {
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "备货天数必须在0~99");
						}
						String warehousePriceStr=BigDecimalUtil.formatMoney(
								BigDecimalUtil.add(warehouseInfo.getAdditionalCost().doubleValue(), comm.getCommodityPriceUs().doubleValue()));
						BigDecimal warehousePriceUs=new BigDecimal(warehousePriceStr);
						BigDecimal warehousePrice=warehousePriceUs.divide(rate, 2, BigDecimal.ROUND_DOWN);
						warehouseInfo.setSystemSku(systemSku);
						warehouseInfo.setWarehousePriceUs(warehousePriceUs);
						warehouseInfo.setWarehousePrice(warehousePrice);
						warehouseInfos.add(warehouseInfo);
					}
					if (warehouseInfos.size()>0) {
						skuWarehouseInfoMapper.insertBatch(warehouseInfos); 
					}
				}*/
                
                String pic = comm.getAdditionalPicture();
                if (pic.contains("|")) {
                    comm.setMasterPicture(pic.substring(0, pic.indexOf("|")));
                    comm.setAdditionalPicture(pic.substring(pic.indexOf("|") + 1));
                } else {
                    comm.setMasterPicture(pic);
                    comm.setAdditionalPicture("");
                }

                //增加系统sku
                comm.setSystemSku(systemSku);
                comm.setCommodityId(commodityBase.getId());
                comm.setState(-1);//默认待提交
                commoditySpecMapper.insertSelective(comm);
                i++;
                
                //插入操作日志
                skuLog=new SkuOperateLog();
                skuLog.setOperateBy(optUser);
                skuLog.setOperateInfo(SkuOperateInfoEnum.ADD.getMsg());
                skuLog.setSystemSku(comm.getSystemSku());
                skuOperateLogService.addSkuLog(skuLog);
                
                skuList.add(comm.getSystemSku());
            }
            
            if (commodityBase.getBelongSeller() != null && commodityBase.getBelongSeller().size() > 0) {
            	// 写入供应商端
                remoteSupplierService.bindSeller(skuList);
            }
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, e.getMessage());
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
		if (BigDecimal.ZERO.compareTo(comm.getPackingHeight()) == 0 || BigDecimal.ZERO.compareTo(comm.getPackingHeight()) > 0) {
			msg="包装尺寸-高度必须大于0";
		}
	    if (BigDecimal.ZERO.compareTo(comm.getPackingLength()) == 0 || BigDecimal.ZERO.compareTo(comm.getPackingLength()) > 0) {
	    	msg="包装尺寸-长度必须大于0";
	    }
	    if (BigDecimal.ZERO.compareTo(comm.getPackingWidth()) == 0 || BigDecimal.ZERO.compareTo(comm.getPackingWidth()) > 0) {
	    	msg="包装尺寸-宽度必须大于0";
	    }
	    if (BigDecimal.ZERO.compareTo(comm.getPackingWeight()) == 0 || BigDecimal.ZERO.compareTo(comm.getPackingWeight()) > 0) {
	    	msg="包装重量必须大于0";
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
		if (comm.getCommodityPriceUs() == null || !ValidatorUtil.isMathFloat(comm.getCommodityPriceUs().toString())) {
			msg="商品价格必须小数点后两位";
		}
		if (BigDecimal.ZERO.compareTo(comm.getCommodityPriceUs()) == 0 || BigDecimal.ZERO.compareTo(comm.getCommodityPriceUs()) > 0) {
	    	msg="商品价格必须大于0";
	    }
		if (comm.getFeeRate() != null && !ValidatorUtil.isMathFloat(comm.getFeeRate().toString())) {
			msg="商品佣金最多保留小数点后两位";
        }
        if (comm.getFeePrice() != null && !ValidatorUtil.isMathFloat(comm.getFeePrice().toString())) {
        	msg="商品佣金最多保留小数点后两位";
        }
        if (StringUtils.isNotBlank(comm.getCustomsCode()) && comm.getCustomsCode().length()>13) {
        	msg="海关编码最多13位";
        }
		if (StringUtils.isBlank(comm.getSupplierSku())) {
			msg="供应商sku不能为空";
		}
    	return msg;
    }


    /**
     * 修改商品
     *
     * @param commodityBase
     * @param commodityDetails
     * @param commoditySpec
     */
    @Override
    @Transactional(rollbackFor={GlobalException.class,RuntimeException.class,Exception.class})
    public synchronized void updateCommodity(CommodityBase commodityBase, CommodityDetails commodityDetails, List<CommoditySpec> commoditySpec) {
        try {
        	//日志操作人
        	String optUser="";
            UserAll userAll=getLoginUserInformationByToken.getUserInfo();
            if (userAll!=null) {
            	UserCommon user = userAll.getUser();
            	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
            		optUser="【供应商】"+user.getUsername();
            	}else if (UserEnum.platformType.CMS.getPlatformType().equals(user.getPlatformType())) {//管理平台
            		optUser="【品连】"+user.getUsername();
            	}
            }
            
            //获取美元利率
            BigDecimal rate = null;
            Object obj = remoteOrderService.getRate("CNY", "USD");
			com.alibaba.fastjson.JSONObject json = (com.alibaba.fastjson.JSONObject) JSON.toJSON(obj);
			if (json != null) {
				String data = (String) json.get("data");
				if (data != null) {
					rate = new BigDecimal(data);
				} else {
					 throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单服务异常，获取美元利率为空");
				}
			}
        	
            Long supplierId = null;
            if (commodityBase.getSupplierId() != null) {
                RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>() {{
                    this.add(commodityBase.getSupplierId());
                }}, 0));
                List<Map> list = RemoteUtil.getList();
                if (list == null || list.isEmpty())
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不存在");
                Map user = list.get(0);
                if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商不存在");
                Integer type = (Integer) user.get("platformType");
                Integer userId = (Integer) user.get("userId");
                Integer parentId = (Integer) user.get("topUserId");
                if (type != null && type.intValue() == 0 && userId != null) {
                    if (parentId.intValue() != 0) {
                        supplierId = Long.valueOf(parentId);
                        commodityBase.setSupplierId(supplierId);
                    } else {
                        supplierId = Long.valueOf(userId);
                        commodityBase.setSupplierId(supplierId);
                    }
                }
            }
            Category category1 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel1());
            Category category2 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel2());
            if (category1 == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一级分类id不存在");
            if (category2 == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "二级分类id不存在");
            if (category2.getCategoryParentId().intValue() != category1.getId().intValue())
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id关联不匹配");
            if (commodityBase.getCategoryLevel3() != null) {
                Category category3 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel3());
                if (category3 == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类id不存在");
                if (category3.getCategoryParentId().intValue() != category2.getId().intValue())
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "分类id关联不匹配");
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类id不存在");
            }
            //无品牌的商品，前端传-1
            if (commodityBase.getBrandId() > 0) {
                Brand brand = brandMapper.selectByPrimaryKey(commodityBase.getBrandId());
                if (brand == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌id不存在");
            }

            CommodityBase cb = commodityBaseMapper.selectByPrimaryKey(commodityBase.getId());
            commodityBase.setVersion(cb.getVersion());

            List<String> skuList=new ArrayList<String>();
            boolean flag = false;
            for (CommoditySpec comm : commoditySpec) {
                if (comm.getSystemSku() != null) {
                	skuList.add(comm.getSystemSku());
                	
                    List<CommoditySpec> cc = commoditySpecMapper.page(new CommoditySpec() {{
                        this.setCommodityId(commodityBase.getId());
                        this.setSystemSku(comm.getSystemSku());
                    }});
                    if (cc.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连sku错误");

                    if (cc.get(0).getState() == 3) {
                        flag = true;

                        //价格变动，发cms消息通知
                        if (comm.getCommodityPriceUs().compareTo(cc.get(0).getCommodityPriceUs()) != 0) {
                        	//cms通知
			                RemoteUtil.invoke(remoteOrderService.queryMapsNoLimit(comm.getSystemSku()));
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
			                			messageService.priceChangeMsg(key.split("==")[0], key.split("==")[1], comm.getSystemSku(), 
			                					valueSet.toString().substring(valueSet.toString().indexOf("[") + 1, valueSet.toString().indexOf("]")));
			                        }
								}
			                }
                        }
                    }
                }
            }
            commodityDetailsMapper.deleteCommodityDetailsByCommodityId(commodityBase.getId());//先删除所有detail
            commoditySpecMapper.deleteCommoditySpecWhereNoUpByCommodityId(commodityBase.getId());//先删除所有的sku
            commodityLimitSaleMapper.deleteByCommodityId(commodityBase.getId());//先删除所有的禁售
            commodityBelongSellerMapper.deleteByCommodityId(commodityBase.getId());//先删除所有的指定可售
            commodityBaseMapper.updateByPrimaryKeySelective(commodityBase);
            commodityDetails.setCommodityId(commodityBase.getId());
            commodityDetails.setId(null);
            commodityDetailsMapper.insertSelective(commodityDetails);

            //禁售国家
            if (commodityBase.getLimitCountry() != null && commodityBase.getLimitCountry().size() > 0) {
                CommodityLimitSale limitSale = null;
                List<CommodityLimitSale> limitSaleList = new ArrayList<CommodityLimitSale>();
                for (String country : commodityBase.getLimitCountry()) {
                    limitSale = new CommodityLimitSale();
                    limitSale.setCommodityId(commodityBase.getId());
                    limitSale.setCode(country);
                    limitSale.setCodeType(1);
                    limitSaleList.add(limitSale);
                }
                commodityLimitSaleMapper.insertBatch(limitSaleList);
            }
            
            //指定可售卖家
            if (commodityBase.getBelongSeller() != null && commodityBase.getBelongSeller().size() > 0) {
            	CommodityBelongSeller belongSeller = null;
                List<CommodityBelongSeller> list = new ArrayList<CommodityBelongSeller>();
                for (String seller : commodityBase.getBelongSeller()) {
                	belongSeller = new CommodityBelongSeller();
                	belongSeller.setCommodityId(commodityBase.getId());
                	belongSeller.setSellerId(Long.parseLong(seller));
                    list.add(belongSeller);
                }
                commodityBelongSellerMapper.insertBatch(list);
                
                // 写入供应商端
                remoteSupplierService.bindSeller(skuList);
            }


            List<SystemSpu> spulist = systemSpuMapper.page(new SystemSpu() {{
                this.setId(commodityBase.getSpuId());
            }});
            if (spulist.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "修改数据不存在关联的spu");

            //SKU处理
            SkuOperateLog skuLog=null;
            Map<String,String> chekemap = new HashMap<>();
            int i = 1;
            for (CommoditySpec comm : commoditySpec) {
            	String checkmsg=checkSpec(comm);
				if (StringUtils.isNotBlank(checkmsg)) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, checkmsg);
				}

                if (rate != null) {
                    comm.setCommodityPrice(comm.getCommodityPriceUs().divide(rate, 2, BigDecimal.ROUND_DOWN));
                }

                if (StringUtils.isNotBlank(comm.getCommoditySpec())) {
                	if (chekemap.containsKey(comm.getCommoditySpec())) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格属性值重复");
                    } else {
                        chekemap.put(comm.getCommoditySpec(), comm.getCommoditySpec());
                    }
                	
                	//拆分中英属性组合
                	StringBuilder cnBuilder=new StringBuilder();
           		 	StringBuilder enBuilder=new StringBuilder();
           		 	String[] specArray = comm.getCommoditySpec().split("\\|");
                    for (int j = 0; j < specArray.length; j++) {
                        String[] arr = specArray[j].split(":");
                        cnBuilder.append(arr[0].substring(0, arr[0].indexOf("(")))
                        	.append(":")
                        	.append(arr[1].substring(0,arr[1].indexOf("(")))
                        	.append("|");
                        
                        enBuilder.append(arr[0].substring(arr[0].indexOf("(") + 1, arr[0].indexOf(")")))
                     		.append(":")
                     		.append(arr[1].substring(arr[1].indexOf("(") + 1, arr[1].indexOf(")")))
                     		.append("|");
                    }
                    
                    comm.setCommoditySpec(cnBuilder.substring(0, cnBuilder.length()-1));
                    comm.setCommoditySpecEn(enBuilder.substring(0, enBuilder.length()-1));
                    
                    //校验是否有重复
                    Map<String,String> mm = new HashMap<>();
                    String[] spac = comm.getCommoditySpec().split("\\|");
                    for (int j = 0; j < spac.length; j++) {
                        String str = spac[j].split(":")[0];
                        if (mm.containsKey(str)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格重复");
                        mm.put(str, str);
                    }
                }
                
                if (StringUtils.isNotBlank(comm.getCommoditySpecEn())) {
                    Map<String,String> mm = new HashMap<>();
                    String[] spac = comm.getCommoditySpecEn().split("\\|");
                    for (int j = 0; j < spac.length; j++) {
                        String str = spac[j].split(":")[0];
                        if (mm.containsKey(str)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "英文规格重复");
                        mm.put(str, str);
                    }
                }
                
                if (StringUtils.isNotBlank(comm.getSupplierSku())) {
                    List<CommodityBase> cblist = commodityBaseMapper.selectBySupplierAndSku(commodityBase.getSupplierId(), comm.getSupplierSku());
                    if (cblist != null && !cblist.isEmpty()) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "同一个供应商的sku码不能重复");
                    }
                    if (ValidatorUtil.containChinese(comm.getSupplierSku())) {
                    	 throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商sku不能包含汉字");
					}
                    if (comm.getSupplierSku().length()<3 || comm.getSupplierSku().length()>16) {
                   	 	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商sku长度范围3~16");
					}
                }else {
                	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商sku不能为空");
				}
                
                String pic = comm.getAdditionalPicture();
                if (pic.contains("|")) {
                    comm.setMasterPicture(pic.substring(0, pic.indexOf("|")));
                    comm.setAdditionalPicture(pic.substring(pic.indexOf("|") + 1));
                } else {
                    comm.setMasterPicture(pic);
                    comm.setAdditionalPicture("");
                }
                
                //多仓库价格转RMB存一份
                if (StringUtils.isNotBlank(comm.getWarehousePriceGroup())) {
					StringBuilder newWarehousePriceGroup=new StringBuilder();
					BigDecimal newPrice=null;
					String[] priceGroupArry=comm.getWarehousePriceGroup().split("\\|");
					if (priceGroupArry != null && priceGroupArry.length>0) {
						for (int k = 0; k < priceGroupArry.length; k++) {
							newPrice=new BigDecimal(priceGroupArry[k].split(":")[1]).divide(rate, 2, BigDecimal.ROUND_DOWN);
							newWarehousePriceGroup.append(priceGroupArry[k].split(":")[0]).append(":").append(newPrice).append("|");
						}
					}
					if (newWarehousePriceGroup.length()>0) {
						comm.setWarehousePriceGroupRmb(newWarehousePriceGroup.substring(0, newWarehousePriceGroup.length()-1));
					}
				}

                if (StringUtils.isBlank(comm.getSystemSku())) {
                    //增加系统sku
                    comm.setSystemSku(spulist.get(0).getSpuValue() + String.format(String.format("%%0%dd", 3), i));
                    comm.setCommodityId(commodityBase.getId());
                    comm.setState(-1);//默认待提交
                    i++;
                }
                commoditySpecMapper.insertSelective(comm);
                
                //删除之前的仓库价格等信息，重新插入
                //skuWarehouseInfoMapper.deleteBySku(comm.getSystemSku());
                //仓库价格等信息处理,前端只需传仓库ID，备货天数，附加费用
                /*if (comm.getSkuWarehouseInfoList()==null || comm.getSkuWarehouseInfoList().size()==0) {
                	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库不能为空");
				}else {
					List<SkuWarehouseInfo> warehouseInfos=new ArrayList<SkuWarehouseInfo>();
					for (SkuWarehouseInfo warehouseInfo : comm.getSkuWarehouseInfoList()) {
						if (warehouseInfo.getWarehouseId()==null) {
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库不能为空");
						}
						if (warehouseInfo.getStockDay()==null || warehouseInfo.getStockDay()<=0 || warehouseInfo.getStockDay()>99) {
							throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "备货天数必须在0~99");
						}
						String warehousePriceStr=BigDecimalUtil.formatMoney(
								BigDecimalUtil.add(warehouseInfo.getAdditionalCost().doubleValue(), comm.getCommodityPriceUs().doubleValue()));
						BigDecimal warehousePriceUs=new BigDecimal(warehousePriceStr);
						BigDecimal warehousePrice=warehousePriceUs.divide(rate, 2, BigDecimal.ROUND_DOWN);
						warehouseInfo.setSystemSku(comm.getSystemSku());
						warehouseInfo.setWarehousePriceUs(warehousePriceUs);
						warehouseInfo.setWarehousePrice(warehousePrice);
						warehouseInfos.add(warehouseInfo);
					}
					if (warehouseInfos.size()>0) {
						skuWarehouseInfoMapper.insertBatch(warehouseInfos); 
					}
				}*/
                
                //插入操作日志
                skuLog=new SkuOperateLog();
                skuLog.setOperateBy(optUser);
                skuLog.setOperateInfo(SkuOperateInfoEnum.UPDATE.getMsg());
                skuLog.setSystemSku(comm.getSystemSku());
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
							wmsPushService.addProduct(record.getAccountId(), 2, commoditySpecs, optUser.substring(optUser.indexOf("】")+1));
						}else if (WarehouseFirmEnum.GOODCANG.getCode().equals(record.getWarehouseProviderCode())) {
							if ("R".equals(record.getProductState())) {
								goodCangService.pushSkusToGoodCang(record.getAccountId(), 2, commoditySpecs, optUser.substring(optUser.indexOf("】")+1));
							}
						}
					}
				}
            }

            //插入ES
            if (flag) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", commodityBase.getId());
                map.put("isUp", true);
                initCommodityIndex(map);
            }
        } catch (Exception e) {
            log.error("更新商品出错 {}", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, e.getMessage());
        }
    }


    /***
     * 查询商品详情
     * @param commodityBase
     * @return
     */
    @Override
    public Map detailsCommodity(CommodityBase commodityBase) {
        Map map = new HashMap();
        List<Attribute> list = new ArrayList();
        List<CommodityDetails> cdlist = commodityDetailsMapper.page(new CommodityDetails() {{
            this.setCommodityId(commodityBase.getId());
        }});
        CommodityDetails detail = null;
        if (cdlist != null && cdlist.size() > 0) {
            detail = cdlist.get(0);
            if (StringUtils.isNotBlank(detail.getAdditionalPicture())) {
                detail.setAdditionalPicture(detail.getMasterPicture() + "|" + detail.getAdditionalPicture());
            } else {
                detail.setAdditionalPicture(detail.getMasterPicture());
            }
        }
        List<CommoditySpec> cslist = commoditySpecMapper.page(new CommoditySpec() {{
            this.setCommodityId(commodityBase.getId());
            this.setIsUp(commodityBase.getIsUp());
        }});
        if (cslist != null && cslist.size() > 0) {
            //商品名称和标题取第一个SKU的中文名和英文名
            commodityBase.setCommodityNameCn(cslist.get(0).getCommodityNameCn());
            commodityBase.setCommodityNameEn(cslist.get(0).getCommodityNameEn());
            commodityBase.setTitle(cslist.get(0).getCommodityNameEn());
            //规格数组
            List<Map<String, String>> specList = new ArrayList<Map<String, String>>();

            boolean isUpdateEs=false;
            int multiPriceFlag=0;
            for (CommoditySpec spec : cslist) {
            	if (spec.getState()==3) {
            		isUpdateEs=true;
				}
            	
            	if (StringUtils.isNotBlank(spec.getWarehousePriceGroup())) {
            		multiPriceFlag=1;
				}
            	
                //sku图片处理
                if (StringUtils.isNotBlank(spec.getAdditionalPicture())) {
                    spec.setAdditionalPicture(spec.getMasterPicture() + "|" + spec.getAdditionalPicture());
                } else {
                    spec.setAdditionalPicture(spec.getMasterPicture());
                }
                
                //如果英文属性有，则需要将中英拼接返回
                if (StringUtils.isNotBlank(spec.getCommoditySpecEn())) {
                	StringBuilder specBuilder=new StringBuilder();
                    String[] specArray = spec.getCommoditySpec().split("\\|");
                    String[] specEnArray = spec.getCommoditySpecEn().split("\\|");
                    for (int i = 0; i < specArray.length; i++) {
                    	String name=specArray[i].split(":")[0];
                    	String value=specArray[i].split(":")[1];
                    	BindAttribute bab=bindAttributeMapper.getByPlNameAndValue(name,value);
                    	if (bab != null) {
                    		specBuilder.append(name).append("(").append(bab.getAttrEnName()).append(")")
                    			.append(":")
                    			.append(value).append("(").append(bab.getAttrEnValue()).append(")")
                    			.append("|");
						}else {
							specBuilder.append(name).append("(").append(specEnArray[i].split(":")[0]).append(")")
                				.append(":")
                				.append(value).append("(").append(specEnArray[i].split(":")[1]).append(")")
                				.append("|");
						}
                    }
                    if (specBuilder.length()>0) {
                    	spec.setCommoditySpec(specBuilder.substring(0, specBuilder.length()-1));
					}
                }

                //sku属性返回处理
                if (StringUtils.isNotBlank(spec.getCommoditySpec())) {
                    if (!spec.getCommoditySpec().contains("|")) {
                        Map<String, String> sepcMap = new HashMap<String, String>();
                        sepcMap.put(spec.getCommoditySpec().split(":")[0], spec.getCommoditySpec().split(":")[1]);
                        specList.add(sepcMap);
                    } else {
                        String[] arry = spec.getCommoditySpec().split("\\|");
                        for (int i = 0; i < arry.length; i++) {
                            Map<String, String> sepcMap = new HashMap<String, String>();
                            sepcMap.put(arry[i].split(":")[0], arry[i].split(":")[1]);
                            specList.add(sepcMap);
                        }
                    }
                }
                
                //多仓库信息
                //List<SkuWarehouseInfo> skuWarehouseInfoList=skuWarehouseInfoMapper.selectBySku(spec.getSystemSku());
                //spec.setSkuWarehouseInfoList(skuWarehouseInfoList);
            }
            
            //构造库存
            inventoryForDetail(cslist);
            
            commodityBase.setMultiPriceFlag(multiPriceFlag);
            
            //所有sku已选属性
            if (specList.size() > 0) {
                commodityBase.setSelectedAttrMap(mapCombine(specList));
            }
            
            //更新ES
            if (isUpdateEs) {
            	commodityJestService.deleteItem(ComomodityIndexConst.INDEX_NAME, ComomodityIndexConst.TYPE_NAME, commodityBase.getId() + "");
            	Map<String, Object> esMapap = new HashMap<String, Object>();
            	esMapap.put("id", commodityBase.getId());
            	esMapap.put("isUp", true);
                initCommodityIndex(esMapap);
			}
            
        }

        String focusId = null;
        //判断是否登录
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        if (userAll != null) {
            UserCommon user = userAll.getUser();
            if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
                Integer sellerId = null;
                if (user.getTopUserId() == 0) {//主账号
                    sellerId = user.getUserid();
                } else {
                    sellerId = user.getTopUserId();
                }

                Integer finalSellerId = sellerId;
                List<CommodityFocus> ll = commodityFocusMapper.page(new CommodityFocus() {{
                    this.setSellerId(Long.valueOf(finalSellerId));
                    this.setCommodityId(String.valueOf(commodityBase.getId()));
                }});
                if (ll != null && ll.size() > 0) {
                    focusId = String.valueOf(ll.get(0).getId());
                }
            }
        }

        if (commodityBase.getCategoryLevel3() != null) {
            Category category3 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel3());
            if (category3 != null) {
                String[] attrarry = category3.getBindAttributeIds().split(",");
                if (attrarry.length > 0) {
                    for (int i = 0; i < attrarry.length; i++) {
                        Attribute attr = attributeMapper.selectByPrimaryKey(Long.valueOf(attrarry[i]));
                        if (attr != null) {
                            list.add(attr);
                        }
                    }
                }
            }
        } else {
            Category category2 = categoryMapper.selectByPrimaryKey(commodityBase.getCategoryLevel2());
            if (category2 != null) {
                String[] attrarry = category2.getBindAttributeIds().split(",");
                if (attrarry.length > 0) {
                    for (int i = 0; i < attrarry.length; i++) {
                        Attribute attr = attributeMapper.selectByPrimaryKey(Long.valueOf(attrarry[i]));
                        if (attr != null) {
                            list.add(attr);
                        }
                    }
                }
            }
        }
        //分类属性和自定义属性处理放数组
        if (StringUtils.isNotBlank(commodityBase.getCategoryAttr())) {
            List<Map<String, String>> categoryAttrList = new ArrayList<Map<String, String>>();
            if (commodityBase.getCategoryAttr().contains("|")) {
                String[] attrArray = commodityBase.getCategoryAttr().split("\\|");
                for (int i = 0; i < attrArray.length; i++) {
                    Map<String, String> map2 = new HashMap<String, String>();
                    map2.put("attributeNameCn", attrArray[i].split(":")[0]);
                    if (attrArray[i].split(":").length>1) {
                    	map2.put("seletedAttr", attrArray[i].split(":")[1]);
					}else {
						map2.put("seletedAttr", "");
					}
                    categoryAttrList.add(map2);
                }
            } else {
                Map<String, String> map2 = new HashMap<String, String>();
                String[] attrArry=commodityBase.getCategoryAttr().split(":");
                map2.put("attributeNameCn", attrArry[0]);
                String seletedAttr="";
                if (attrArry.length>1) {
                	seletedAttr=attrArry[1];
				}
                map2.put("seletedAttr", seletedAttr);
                categoryAttrList.add(map2);
            }

            commodityBase.setCategoryAttrList(categoryAttrList);
        }

        if (StringUtils.isNotBlank(commodityBase.getCustomAttr())) {
            List<Map<String, String>> customAttrList = new ArrayList<Map<String, String>>();
            if (commodityBase.getCustomAttr().contains("|")) {
                String[] attrArray = commodityBase.getCustomAttr().split("\\|");
                for (int i = 0; i < attrArray.length; i++) {
                    Map<String, String> map2 = new HashMap<String, String>();
                    map2.put("customAttrName", attrArray[i].split(":")[0]);
                    map2.put("customAttrValue", attrArray[i].split(":")[1]);
                    customAttrList.add(map2);
                }
            } else {
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("attributeNameCn", commodityBase.getCustomAttr().split(":")[0]);
                map2.put("seletedAttr", commodityBase.getCustomAttr().split(":")[1]);
                customAttrList.add(map2);
            }

            commodityBase.setCustomAttrList(customAttrList);
        }
        //翻译
        if (commodityBase.getBrandId() == -1) {
            commodityBase.setBrandName("无品牌");
        }
        commodityBase.setBrandName(com.rondaful.cloud.common.utils.Utils.translation(commodityBase.getBrandName()));
        commodityBase.setDefaultRepository(com.rondaful.cloud.common.utils.Utils.translation(commodityBase.getDefaultRepository()));
        if (commodityBase.getLimitCountry() != null && commodityBase.getLimitCountry().size()>0) {
			List<String> counttryList=new ArrayList<String>();
			for (String c : commodityBase.getLimitCountry()) {
				counttryList.add(com.rondaful.cloud.common.utils.Utils.translation(c));
			}
			commodityBase.setLimitCountry(counttryList);
		}
        commodityBase.setProductLogisticsAttributes(com.rondaful.cloud.common.utils.Utils.translation(commodityBase.getProductLogisticsAttributes()));
        
        //wish匹配
        commodityBase.setVendibilityPlatform(commodityBase.getVendibilityPlatform().replace("wish", "Wish"));
        
        map.put("base", commodityBase);
        map.put("details", detail);
       // map.put("logList", lop);
        map.put("spec", cslist);
        map.put("attribute", list);
        map.put("focusId", focusId);
        return map;
    }


    private Map<String, List<String>> mapCombine(List<Map<String, String>> list) {
        Map<String, List<String>> map = new HashMap<>();

        for (Map<String, String> m : list) {
            Iterator<String> it = m.keySet().iterator();

            while (it.hasNext()) {
                String key = it.next();

                if (!map.containsKey(key)) {
                    List<String> newList = new ArrayList<>();
                    newList.add(m.get(key));
                    map.put(key, newList);
                } else {
                    map.get(key).add(m.get(key));
                }
            }
        }
        return map;
    }


    @Override
    @Cacheable(value = "commodityListCache", keyGenerator = "keyGenerator")
    public Page selectCommodityListBySpec(Map map) {
        Page.builder((String) map.get("page"), String.valueOf(Integer.valueOf((String) map.get("row")) > 200 ? 200 : (String) map.get("row")));
        List<CommodityBase> list = commodityBaseMapper.selectCommodityListBySpec(map);

        if (!list.isEmpty()) {
            map.put("list", list);
            Page.builder("1", String.valueOf(Integer.MAX_VALUE));
            List<CommoditySpec> listspc = commoditySpecMapper.selectCommoditySpecByCommodityId(map);
            if (map.containsKey("isUp")) {
                constructionSku(listspc);//构造库存
            }
            constructionSupplier(list);//构造供应商
            for (CommodityBase cb : list) {
                int totalInventory = 0;
                cb.setCommoditySpecList(new ArrayList<>());
                Set<String> set = new HashSet<String>();
                int multiPriceFlag=0;
                for (CommoditySpec cs : listspc) {
                    //sku与spu匹配
                    if (cs.getCommodityId().intValue() == cb.getId().intValue()) {
                        cb.getCommoditySpecList().add(cs);
                        
                        totalInventory += cs.getInventory();
                        
                        //List<SkuWarehouseInfo> skuWarehouseInfoList=skuWarehouseInfoMapper.selectBySku(cs.getSystemSku());
                        //cs.setSkuWarehouseInfoList(skuWarehouseInfoList);
                        
                        //是否多仓库价格
                        if (StringUtils.isNotBlank(cs.getWarehousePriceGroup())) {
                    		multiPriceFlag=1;
        				}

                        //SKU的spu码、分类、供应商取SPU的
                        cs.setSPU(cb.getSPU());
                        cs.setCategoryName(cb.getCategoryName1());
                        cs.setCategoryName2(cb.getCategoryName2());
                        cs.setSupplierId(cb.getSupplierId() + "");
                        cs.setSupplierName(cb.getSupplierName());
                       
                        //英文规格值
                        StringBuilder specValueEn=new StringBuilder();
                        //中文规格值
                        StringBuilder specValueCn=new StringBuilder();
                        
                        //SPU的英文规格项,亚马逊刊登用到
                        if (StringUtils.isNotBlank(cs.getCommoditySpecEn())) {
                            String[] specArray = cs.getCommoditySpecEn().split("\\|");
                            for (int i = 0; i < specArray.length; i++) {
                                String[] arr = specArray[i].split(":");
                                set.add(arr[0]);
                                if (arr.length>1) {
                                	specValueEn.append(arr[1]).append(";");
								}
                            }
                            if (set.size() > 0) {
                                cb.setCommoditySpecKeys(set.toString().substring(set.toString().indexOf("[") + 1, set.toString().indexOf("]")));
                            }
                            if (specValueEn.length() > 0) {
                            	cs.setSpecValueEn(specValueEn.substring(0,specValueEn.length()-1));
                            }
                            
                            if (StringUtils.isNotBlank(cs.getCommoditySpec())) {
                                String[] specCnArray = cs.getCommoditySpec().split("\\|");
                                for (int i = 0; i < specCnArray.length; i++) {
                                    String[] arr = specCnArray[i].split(":");
                                    if (arr.length>1) {
                                    	specValueCn.append(arr[1]).append(";");
                                    }
                                }
                                if (specValueCn.length() > 0) {
                                	cs.setSpecValueCn(specValueCn.substring(0,specValueCn.length()-1));
                                }
                            }
                        }else {//兼容历史数据
                        	if(StringUtils.isNotBlank(cs.getCommoditySpec())) {
                        		String[] specArray = cs.getCommoditySpec().split("\\|");
                                for (int i = 0; i < specArray.length; i++) {
                                    String[] arr = specArray[i].split(":");
                                    if (arr[0].contains("(") && arr[0].contains(")")) {
                                        set.add(arr[0].substring(arr[0].indexOf("(") + 1, arr[0].lastIndexOf(")")));
                                    } else {
                                        set.add(arr[0]);
                                    }
                                    
                                    if (arr.length>1 && arr[1].contains("(") && arr[1].contains(")")) {
                                    	specValueCn.append(arr[1].substring(0, arr[1].indexOf("("))).append(";");
                                    	specValueEn.append(arr[1].substring(arr[1].indexOf("(") + 1, arr[1].lastIndexOf(")"))).append(";");
                                    }
                                }
                                
                                if (specValueCn.length() > 0) {
                                	cs.setSpecValueCn(specValueCn.substring(0,specValueCn.length()-1));
                                }
                                
                                if (specValueEn.length() > 0) {
                                	cs.setSpecValueEn(specValueEn.substring(0,specValueEn.length()-1));
                                }
                        	}
						}
                    }
                }
                cb.setMultiPriceFlag(multiPriceFlag);
                cb.setInventory(totalInventory);
                //设置商品类型,状态
                if (cb.getCommoditySpecList().size() > 1) {
                    cb.setSpuType(com.rondaful.cloud.common.utils.Utils.translation("多SKU"));
                    cb.setState(-9);
                    for (CommoditySpec comm : cb.getCommoditySpecList()) {
						if (comm.getState().intValue()==3) {
							cb.setTortFlag(1);
							break;
						}
					}
                } else {
                    cb.setSpuType(com.rondaful.cloud.common.utils.Utils.translation("单SKU"));
                    cb.setState(cb.getCommoditySpecList().get(0).getState());
                    if (cb.getState().intValue()==3) {
						cb.setTortFlag(1);
					}
                }
                //设置商品的标题，sku，商品价，商品中英文名称，商品佣金，取第一个SKU的
                cb.setTitle(cb.getCommoditySpecList().get(0).getCommodityNameEn());
                cb.setSystemSku(cb.getCommoditySpecList().get(0).getSystemSku());
                cb.setCommodityPrice(cb.getCommoditySpecList().get(0).getCommodityPrice());
                cb.setCommodityPriceUs(cb.getCommoditySpecList().get(0).getCommodityPriceUs());
                cb.setCommodityNameCn(cb.getCommoditySpecList().get(0).getCommodityNameCn());
                cb.setCommodityNameEn(cb.getCommoditySpecList().get(0).getCommodityNameEn());
                cb.setFeePrice(cb.getCommoditySpecList().get(0).getFeePrice());
                cb.setFeeRate(cb.getCommoditySpecList().get(0).getFeeRate());
                cb.setSupplierSku(cb.getCommoditySpecList().get(0).getSupplierSku());
                //品牌翻译
                if (cb.getBrandId() == -1) {
                    cb.setBrandName("无品牌");
                }
                cb.setBrandName(com.rondaful.cloud.common.utils.Utils.translation(cb.getBrandName()));
                cb.setDefaultRepository(com.rondaful.cloud.common.utils.Utils.translation(cb.getDefaultRepository()));
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return new Page(pageInfo);
    }


    @Override
    public Page selectCommodityListBySpecBatch(Map map) {
        Long sellerId = null;
        RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>() {{
            this.add((Long) map.get("sellerId"));
        }}, 1));
        if (!"100200".equals(RemoteUtil.getErrorCode()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, RemoteUtil.getMsg());
        List<Map> result = RemoteUtil.getList();
        if (result == null || result.isEmpty())
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家id不存在");
        Map user = result.get(0);
        if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家不存在");
        Integer type = (Integer) user.get("platformType");
        Integer userId = (Integer) user.get("userId");
        Integer parentId = (Integer) user.get("topUserId");
        if (type != null && type.intValue() == 1 && userId != null) {
            if (parentId.intValue() != 0) {
                sellerId = Long.valueOf(parentId);
            } else {
                sellerId = Long.valueOf(userId);
            }
        }
        //查询已经关注过的所有商品
        Long finalSellerId = sellerId;
        List<CommodityBase> listed = commodityBaseMapper.selectCommodityListBySpec(new HashMap() {{
            this.put("sellerId", String.valueOf(finalSellerId));
            this.put("sortKey", "t8.creat_time");
            this.put("sort", "DESC");
        }});
        Page.builder((String) map.get("page"), (String) map.get("row"));
        map.remove("sellerId");
        if (!listed.isEmpty())
            map.put("focusList", listed);
        List<CommodityBase> list = commodityBaseMapper.selectCommodityListBySpec(map);

        //设置商品商品名称
        if (list.size() > 0) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("list", list);
            Page.builder("1", String.valueOf(Integer.MAX_VALUE));
            List<CommoditySpec> listspc = commoditySpecMapper.selectCommoditySpecByCommodityId(param);
            for (CommodityBase cb : list) {
                cb.setCommoditySpecList(new ArrayList<>());
                for (CommoditySpec cs : listspc) {
                    //sku与spu匹配
                    if (cs.getCommodityId().intValue() == cb.getId().intValue()) {
                        cb.getCommoditySpecList().add(cs);
                    }
                }
                //设置商品的标题，sku，商品价，商品中英文名称，商品佣金，取第一个SKU的
                cb.setTitle(cb.getCommoditySpecList().get(0).getCommodityNameEn());
                cb.setCommodityNameCn(cb.getCommoditySpecList().get(0).getCommodityNameCn());
                cb.setCommodityNameEn(cb.getCommoditySpecList().get(0).getCommodityNameEn());
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return new Page(pageInfo);
    }
    
    @Override
	public void getAllPublishPack(Map<String,Object> param) {
    	try {
    		Long sellerId=(Long)param.get("sellerId");
    		String username=(String) param.get("username");
    		param.remove("username");
    		param.remove("sellerId");
    		int total=commodityBaseMapper.selectExportCount(param);
    		if (total>0) {
    			int pages=total%200==0 ? total/200 : total/200 + 1;
    			for (int i = 1; i <= pages; i++) {
    				String url="";
    		        String filename = username+"_"+DateUtil.DateToDir(new Date()) + "刊登包" + "_" + i;
    		        String jarroot = System.getProperty("user.home") + File.separator + Utils.uuid();
    		        String fileroot = jarroot + File.separator + filename;
    		        
    				Page.builder(String.valueOf(i), "200");
    				List<CommodityBase> baseList=commodityBaseMapper.selectCommodityListBySpec(param);
					for (CommodityBase commodityBase : baseList) {
						getPublishDate(fileroot,commodityBase.getId());
					}
					
					FileOutputStream fos = new FileOutputStream(fileroot + ".zip");
			        ZipOutputStream zipOut = new ZipOutputStream(fos);
			        ZipUtil.compressZip(zipOut, new File(fileroot), new File(fileroot).getName());
			        zipOut.closeEntry();
			        zipOut.close();
			        url=fileService.saveFile(ConstantAli.getEnv(env), ConstantAli.getFolder("publish"), filename+".zip", new File(fileroot + ".zip"), null, null, null);
			        
			        PublishPackRecord record=new PublishPackRecord();
			        record.setCommodityId(sellerId);
			        record.setFileUrl(url);
			        publishPackRecordMapper.insert(record);
			        
			        IOUtils.deleteAllFilesOfDir(new File(jarroot));
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("生成全部商品刊登包异常",e);
		}
	}
    

    @Override
    public String download(List<Long> list, HttpServletResponse response) throws Exception {
    	String url="";
    	String snow = String.valueOf(SnowFlakeUtil.nextId());
        String filename = DateUtil.DateToDir(new Date()) + "_" + snow.substring(snow.length() - 8, snow.length()) + com.rondaful.cloud.common.utils.Utils.translation("刊登包");
        String jarroot = System.getProperty("user.home") + File.separator + Utils.uuid();
        String fileroot = jarroot + File.separator + filename;
        log.error("jar==>" + fileroot);
    	if (list.size()==1) {
			//1.先查询记录表里是否有已生成的，有的话直接取
    		Long commodityId=list.get(0);
    		PublishPackRecord record=publishPackRecordMapper.getByCommodityId(commodityId);
    		if (record != null) {
    			url=record.getFileUrl();
			}else {
				getPublishDate(fileroot,commodityId);
				
				FileOutputStream fos = new FileOutputStream(fileroot + ".zip");
		        ZipOutputStream zipOut = new ZipOutputStream(fos);
		        ZipUtil.compressZip(zipOut, new File(fileroot), new File(fileroot).getName());
		        zipOut.closeEntry();
		        zipOut.close();
		        url=fileService.saveFile(ConstantAli.getEnv(env), ConstantAli.getFolder("publish"), filename+".zip", new File(fileroot + ".zip"), null, null, null);
		        IOUtils.deleteAllFilesOfDir(new File(jarroot));
		        
		        //新增记录
		        record=new PublishPackRecord();
		        record.setCommodityId(commodityId);
		        record.setFileUrl(url);
		        publishPackRecordMapper.insert(record);
			}
    		
    		//域名替换
    		if (StringUtils.isNotBlank(url)) {
    			url=url.replace(CommonConstant.DEF[0], CommonConstant.DO_MAIN[0])
    	        		.replace(CommonConstant.DEF[1], CommonConstant.DO_MAIN[1])
    	        		.replace(CommonConstant.DEF[2], CommonConstant.DO_MAIN[2]);
			}
		}else {
			for (Long id : list) {
	        	getPublishDate(fileroot,id);
	        }
			
			FileOutputStream fos = new FileOutputStream(fileroot + ".zip");
	        ZipOutputStream zipOut = new ZipOutputStream(fos);
	        ZipUtil.compressZip(zipOut, new File(fileroot), new File(fileroot).getName());
	        zipOut.closeEntry();
	        zipOut.close();
	        url=fileService.saveFile(ConstantAli.getEnv(env), ConstantAli.getFolder("publish"), filename+".zip", new File(fileroot + ".zip"), null, null, null);
	        IOUtils.deleteAllFilesOfDir(new File(jarroot));
	        
	        url=url.replace(CommonConstant.DEF[0], CommonConstant.DO_MAIN[0])
					.replace(CommonConstant.DEF[1], CommonConstant.DO_MAIN[1])
					.replace(CommonConstant.DEF[2], CommonConstant.DO_MAIN[2]);
		}
        return url;
    }
    
    private void getPublishDate(String fileroot,Long id) {
    	try {
            List<String> imglist = new ArrayList<>();
            CommodityBase comm = commodityBaseMapper.selectCommodityDetailsById(id);
            if (comm == null) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品id不存在");
            }
            List<CommodityDetails> cdlist = commodityDetailsMapper.page(new CommodityDetails() {{
                this.setCommodityId(id);
            }});
            List<CommoditySpec> cslist = commoditySpecMapper.page(new CommoditySpec() {{
                this.setCommodityId(id);
                this.setState(3);
            }});
            SystemSpu ss = systemSpuMapper.selectByPrimaryKey(comm.getSpuId());
            Brand br = brandMapper.selectByPrimaryKey(comm.getBrandId());
            if (cdlist != null && cdlist.size()>0) {
                for (CommoditySpec csp : cslist) {
                    csp.setSPU(ss == null ? "" : ss.getSpuValue());
                    csp.setBrandName(br == null ? "" : br.getBrandName());
                    csp.setProducer(comm.getProducer());

                    //搜索关键字
                    if (StringUtils.isNotBlank(cdlist.get(0).getSearchKeywords())) {
                        String obj = cdlist.get(0).getSearchKeywords();
                        String[] arr = obj.split(":::");
                        if (arr.length > 0) {
                            for (int i = 0; i < arr.length; i++) {
                                String key = arr[i].split("===")[0];
                                if ("EN".equals(key) && arr[i].split("===").length > 1)
                                    csp.setSearchKeywordsEn(arr[i].split("===")[1]);
                                else if ("CN".equals(key) && arr[i].split("===").length > 1)
                                    csp.setSearchKeywordsCn(arr[i].split("===")[1]);
                                else if ("FR".equals(key) && arr[i].split("===").length > 1)
                                    csp.setSearchKeywordsFr(arr[i].split("===")[1]);
                                else if ("DE".equals(key) && arr[i].split("===").length > 1)
                                    csp.setSearchKeywordsDe(arr[i].split("===")[1]);
                                else if ("IT".equals(key) && arr[i].split("===").length > 1)
                                    csp.setSearchKeywordsIt(arr[i].split("===")[1]);
                            }
                        }
                    }

                    //商品亮点1
                    if (StringUtils.isNotBlank(cdlist.get(0).getStrength1())) {
                        String obj = cdlist.get(0).getStrength1();
                        String[] arr = obj.split(":::");
                        if (arr.length > 0) {
                            for (int i = 0; i < arr.length; i++) {
                                String key = arr[i].split("===")[0];
                                if ("EN".equals(key) && arr[i].split("===").length > 1)
                                    csp.setStrengthEn(arr[i].split("===")[1]);
                                else if ("CN".equals(key) && arr[i].split("===").length > 1)
                                    csp.setStrengthCn(arr[i].split("===")[1]);
                                else if ("FR".equals(key) && arr[i].split("===").length > 1)
                                    csp.setStrengthFr(arr[i].split("===")[1]);
                                else if ("DE".equals(key) && arr[i].split("===").length > 1)
                                    csp.setStrengthDe(arr[i].split("===")[1]);
                                else if ("IT".equals(key) && arr[i].split("===").length > 1)
                                    csp.setStrengthIt(arr[i].split("===")[1]);
                            }
                        }
                    }

                    //包装清单
                    if (StringUtils.isNotBlank(cdlist.get(0).getPackingList())) {
                        String obj = cdlist.get(0).getPackingList();
                        String[] arr = obj.split(":::");
                        if (arr.length > 0) {
                            for (int i = 0; i < arr.length; i++) {
                                String key = arr[i].split("===")[0];
                                if ("EN".equals(key) && arr[i].split("===").length > 1)
                                    csp.setPackingListEn(arr[i].split("===")[1]);
                                else if ("CN".equals(key) && arr[i].split("===").length > 1)
                                    csp.setPackingListCn(arr[i].split("===")[1]);
                                else if ("FR".equals(key) && arr[i].split("===").length > 1)
                                    csp.setPackingListFr(arr[i].split("===")[1]);
                                else if ("DE".equals(key) && arr[i].split("===").length > 1)
                                    csp.setPackingListDe(arr[i].split("===")[1]);
                                else if ("IT".equals(key) && arr[i].split("===").length > 1)
                                    csp.setPackingListIt(arr[i].split("===")[1]);
                            }
                        }
                    }

                    //商品描述
                    if (StringUtils.isNotBlank(cdlist.get(0).getCommodityDesc())) {
                        String obj = cdlist.get(0).getCommodityDesc();
                        String[] arr = obj.split(":::");
                        if (arr.length > 0) {
                            for (int i = 0; i < arr.length; i++) {
                                String key = arr[i].split("===")[0];
                                if ("EN".equals(key) && arr[i].split("===").length > 1)
                                    csp.setCommodityDescEn(arr[i].split("===")[1]);
                                else if ("CN".equals(key) && arr[i].split("===").length > 1)
                                    csp.setCommodityDescCn(arr[i].split("===")[1]);
                                else if ("FR".equals(key) && arr[i].split("===").length > 1)
                                    csp.setCommodityDescFr(arr[i].split("===")[1]);
                                else if ("DE".equals(key) && arr[i].split("===").length > 1)
                                    csp.setCommodityDescDe(arr[i].split("===")[1]);
                                else if ("IT".equals(key) && arr[i].split("===").length > 1)
                                    csp.setCommodityDescIt(arr[i].split("===")[1]);
                            }
                        }
                    }

                    if (StringUtils.isNotBlank(cdlist.get(0).getProductFeaturesEn())) {
                        csp.setProductFeaturesEn(cdlist.get(0).getProductFeaturesEn().replaceAll("\\|", ";"));
                    }
                    if (StringUtils.isNotBlank(cdlist.get(0).getProductFeaturesCn())) {
                        csp.setProductFeaturesCn(cdlist.get(0).getProductFeaturesCn().replaceAll("\\|", ";"));
                    }

                    //加入SPU图片
                    if (StringUtils.isNotBlank(cdlist.get(0).getMasterPicture()))
                        imglist.addAll(Arrays.asList(cdlist.get(0).getMasterPicture().split("\\|")));
                    if (StringUtils.isNotBlank(cdlist.get(0).getAdditionalPicture()))
                        imglist.addAll(Arrays.asList(cdlist.get(0).getAdditionalPicture().split("\\|")));
                    //加入SKU图片
                    if (StringUtils.isNotBlank(csp.getMasterPicture()))
                        imglist.addAll(Arrays.asList(csp.getMasterPicture().split("\\|")));
                    if (StringUtils.isNotBlank(csp.getAdditionalPicture()))
                        imglist.addAll(Arrays.asList(csp.getAdditionalPicture().split("\\|")));
                }
                
                WebFileUtils.getImg(imglist, fileroot + File.separator + ss.getSpuValue() + File.separator + "picture");
                FileOutputStream out = new FileOutputStream(fileroot + File.separator + ss.getSpuValue() + File.separator + ss.getSpuValue() + ".xls");
                ExcelUtil.getExport(cslist, null, "Sheet1", CommoditySpec.class, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateAuditSku(List<String> ids, String audit, String auditDesc) {
    	//日志操作人
    	String optUser="";
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
        		optUser="【供应商】"+user.getUsername();
        	}else if (UserEnum.platformType.CMS.getPlatformType().equals(user.getPlatformType())) {//管理平台
        		optUser="【品连】"+user.getUsername();
        	}
        }
        
        SkuOperateLog skuLog=null;
        List<CommoditySpec> list = new ArrayList<>();
        for (String id : ids) {
            CommoditySpec com = commoditySpecMapper.selectByPrimaryKey(Long.valueOf(id));
            if (com == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格不存在");
            CommodityBase cb = commodityBaseMapper.selectByPrimaryKey(com.getCommodityId());
            if (cb == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku关联数据错误");
            if (com.getState() != 0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "只能操作待审核的商品");
            if ("2".equals(audit) && StringUtils.isBlank(auditDesc))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "审核描述不能为空");
            if (StringUtils.isNotBlank(auditDesc) && auditDesc.length()>200) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "审核描述最多200字符");
			}
            com.setState(Integer.valueOf(audit));
            com.setAuditDesc(auditDesc);
            com.setSupplierId(String.valueOf(cb.getSupplierId()));
            int result = commoditySpecMapper.updateByPrimaryKeySelective(com);
            if (result != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "审核失败，系统异常");
            list.add(com);
            
            /*commodityOperateLogMapper.insertSelective(new CommodityOperateLog() {{
                this.setOperateBy(getLoginUserInformationByToken.getUserInfo().getUser().getUsername());
                this.setCommodityId(com.getCommodityId());
                this.setCreatTime(DateUtil.getCurrentDate());
                this.setCommodityStat((String) Constant.map.get(com.getState()));
                this.setOperateInfo("SKU" + com.getSystemSku() + (String) Constant.map.get(com.getState()) + "  " + auditDesc);
            }});*/
            
            //插入操作日志
            skuLog=new SkuOperateLog();
            skuLog.setOperateBy(optUser);
            if ("1".equals(audit)) {
            	skuLog.setOperateInfo(SkuOperateInfoEnum.TO_UP.getMsg());
			}else if ("2".equals(audit)) {
				skuLog.setOperateInfo(SkuOperateInfoEnum.AUDIT_FAIL.getMsg());
			}
            skuLog.setSystemSku(com.getSystemSku());
            skuOperateLogService.addSkuLog(skuLog);
        }
        
        if ("1".equals(audit)) {
            //MQ,发给仓库
            mqSender.commoditySkuAdd(JSONArray.fromObject(list).toString());
        }
        // cms消息，发给供应商
        sendMessage(audit, list);
        messageService.unAuditSkuNumMsg();
    }


   

    /**
     * 根据spu值查找商品信息
     *
     * @param list
     * @return
     */
    @Override
    public List selectBySPUS(List<String> list) {
        List<CommodityBase> li = commodityBaseMapper.selectBySPUS(list);
        if (li != null && li.size()>0) {
			for (CommodityBase commodityBase : li) {
				commodityBase.setTitle(commodityBase.getCommodityNameEn());
			}
		}
        return li;
    }


    @Override
    public List selectBySKUS(QuerySkuBelongSellerVo vo) {
        List result = new ArrayList();
        List<CommoditySpec> csp = commoditySpecMapper.selectCommoditySpecBySku(vo.getSystemSkuList());
        //调用远程服务构造供应商信息、供应链信息、美元
        constructionSupplierForSpec(csp);
        //组装库存信息
        inventoryForDetail(csp);
        
        Map map = new HashMap();
        for (CommoditySpec commoditySpec : csp) {
            if (!map.containsKey(commoditySpec.getCommodityId().longValue())) {
                List<CommoditySpec> ls = new ArrayList<CommoditySpec>();
                ls.add(commoditySpec);
                map.put(commoditySpec.getCommodityId().longValue(), ls);
            } else {
                List<CommoditySpec> ol = (List<CommoditySpec>) map.get(commoditySpec.getCommodityId().longValue());
                ol.add(commoditySpec);
                map.put(commoditySpec.getCommodityId().longValue(), ol);
            }
        }
        
        Map<String, Object> param=null;
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Long key = (Long) entry.getKey();
            CommodityDetails cd = null;
            CommodityBase cb = null;
            List<CommodityDetails> li = commodityDetailsMapper.page(new CommodityDetails() {{
                this.setCommodityId(key);
            }});
            List<CommodityBase> cblist = commodityBaseMapper.page(new CommodityBase() {{
                this.setId(key);
            }});
            if (!li.isEmpty()) {
                cd = li.get(0);
            }
            if (!cblist.isEmpty()) {
                cb = cblist.get(0);
                
                //如果在指定表里没有商品，说明是全部卖家可售 1：可售，-1：不可售
                param=new HashMap<String, Object>();
                param.put("commodityId", cb.getId());
                int commodityNum=commodityBelongSellerMapper.selectCountByCommodityId(param);
                if (commodityNum == 0) {
                	cb.setCanSale(1);
				}else {
					//存在指定
					 if (vo.getSellerId() != null) {
	                	//根据commodityId和sellerId查询是否对卖家可售
	                    param.put("sellerId", vo.getSellerId());
	                	int belongSellerNum=commodityBelongSellerMapper.selectCountByCommodityId(param);
	                	if (belongSellerNum > 0) {
	                		cb.setCanSale(1);
						}else {
							cb.setCanSale(-1);
						}
	                	param.clear();
					}
				}
            	
                cb.setCommodityDetails(cd);
                cb.setCommoditySpecList((List<CommoditySpec>) map.get(key));
                result.add(cb);
            }
        }
        return result;
    }


    @Override
    @Transactional
	public void deleteCommodity(Long id, String type) {
    	//日志操作人
    	String optUser="";
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
        		optUser="【供应商】"+user.getUsername();
        	}else if (UserEnum.platformType.CMS.getPlatformType().equals(user.getPlatformType())) {//管理平台
        		optUser="【品连】"+user.getUsername();
        	}
        }
        SkuOperateLog skuLog=null;
        
		if ("sku".equals(type)) {
			CommoditySpec cs = commoditySpecMapper.selectByPrimaryKey(id);
			if (cs == null)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku id错误");
			if (cs.getState() == 1 || cs.getState() == 3)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "(待)上架的sku不能删除");

			String systemSku=cs.getSystemSku();
			
			// 通知卖家端下架商品
			mqSender.skuDown(systemSku);
			// cms通知
			RemoteUtil.invoke(remoteOrderService.queryMapsNoLimit(systemSku));
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
						messageService.downStateMsg(key.split("==")[0], key.split("==")[1], systemSku,
								valueSet.toString().substring(valueSet.toString().indexOf("[") + 1,
										valueSet.toString().indexOf("]")));
					}
				}
			}
			
			//插入操作日志
            skuLog=new SkuOperateLog();
            skuLog.setOperateBy(optUser);
            skuLog.setOperateInfo(SkuOperateInfoEnum.DELETE.getMsg());
            skuLog.setSystemSku(systemSku);
            skuOperateLogService.addSkuLog(skuLog);
            
            List<String> skuImgList = new ArrayList<>();
            skuImgList.addAll(Arrays.asList(cs.getMasterPicture().split("\\|")));
            if (StringUtils.isNotBlank(cs.getAdditionalPicture())) {
            	skuImgList.addAll(Arrays.asList(cs.getAdditionalPicture().split("\\|")));
			}

            //删除sku
			Long commodityId = cs.getCommodityId();
			commoditySpecMapper.deleteByPrimaryKey(id);
			
			// 删除商品图片
			fileService.deleteList(skuImgList);
			
			
			// 如果删除的是最后一个sku，则连base，detail，spu也删除
			int count = commoditySpecMapper.getSpecCount(commodityId, id, null);
			if (count == 0) {
				CommodityBase base = commodityBaseMapper.selectByPrimaryKey(commodityId);
				systemSpuMapper.deleteByPrimaryKey(base.getSpuId());
				
				List<String> detailImgList = new ArrayList<>();
				CommodityDetails detail=commodityDetailsMapper.selectByCommodityId(commodityId);
				if (detail != null) {
					detailImgList.addAll(Arrays.asList(detail.getMasterPicture().split("\\|")));
		            if (StringUtils.isNotBlank(detail.getAdditionalPicture())) {
		            	detailImgList.addAll(Arrays.asList(detail.getAdditionalPicture().split("\\|")));
					}
				}
				commodityDetailsMapper.deleteCommodityDetailsByCommodityId(commodityId);
				// 删除商品图片
				fileService.deleteList(detailImgList);
				
				commodityBaseMapper.deleteByPrimaryKey(commodityId);
				commodityLimitSaleMapper.deleteByCommodityId(commodityId);
				commodityBelongSellerMapper.deleteByCommodityId(commodityId);
				// 删除ES文档
				commodityJestService.deleteItem(ComomodityIndexConst.INDEX_NAME, ComomodityIndexConst.TYPE_NAME,commodityId + "");
			}
		} else if ("spu".equals(type)) {
			CommodityBase cb = commodityBaseMapper.selectByPrimaryKey(id);
			if (cb == null)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "spu id错误");
			SystemSpu ss = systemSpuMapper.selectByPrimaryKey(cb.getSpuId());
			if (ss == null)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "spu数据错误");
			List<CommoditySpec> list1 = commoditySpecMapper.page(new CommoditySpec() {
				{
					this.setCommodityId(cb.getId());
				}
			});

			List<String> skuImgList = new ArrayList<>();
			if (list1 != null && list1.size() > 0) {
				for (CommoditySpec spec : list1) {
					if (spec.getState() == 1 || spec.getState() == 3) {
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "该商品下有已上架/待上架的SKU，不能删除");
					}

					// 通知卖家端下架商品
					mqSender.skuDown(spec.getSystemSku());
					// cms通知
					RemoteUtil.invoke(remoteOrderService.queryMapsNoLimit(spec.getSystemSku()));
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
								messageService.downStateMsg(key.split("==")[0], key.split("==")[1], spec.getSystemSku(),
										valueSet.toString().substring(valueSet.toString().indexOf("[") + 1,
												valueSet.toString().indexOf("]")));
							}
						}
					}
					
					//插入操作日志
		            skuLog=new SkuOperateLog();
		            skuLog.setOperateBy(optUser);
		            skuLog.setOperateInfo(SkuOperateInfoEnum.DELETE.getMsg());
		            skuLog.setSystemSku(spec.getSystemSku());
		            skuOperateLogService.addSkuLog(skuLog);
		            
		            skuImgList.addAll(Arrays.asList(spec.getMasterPicture().split("\\|")));
		            if (StringUtils.isNotBlank(spec.getAdditionalPicture())) {
		            	skuImgList.addAll(Arrays.asList(spec.getAdditionalPicture().split("\\|")));
					}
		            fileService.deleteList(skuImgList);
				}
			}

			// 删除ES文档
			commodityJestService.deleteItem(ComomodityIndexConst.INDEX_NAME, ComomodityIndexConst.TYPE_NAME,cb.getId() + "");

			//相关表删除
			List<String> detailImgList = new ArrayList<>();
			CommodityDetails detail=commodityDetailsMapper.selectByCommodityId(cb.getId());
			if (detail != null) {
				detailImgList.addAll(Arrays.asList(detail.getMasterPicture().split("\\|")));
	            if (StringUtils.isNotBlank(detail.getAdditionalPicture())) {
	            	detailImgList.addAll(Arrays.asList(detail.getAdditionalPicture().split("\\|")));
				}
			}
			commodityDetailsMapper.deleteCommodityDetailsByCommodityId(cb.getId());
			// 删除商品图片
			fileService.deleteList(detailImgList);
			
			commodityBaseMapper.deleteByPrimaryKey(cb.getId());
			systemSpuMapper.deleteByPrimaryKey(ss.getId());
			commoditySpecMapper.deleteCommoditySpecWhereNoUpByCommodityId(cb.getId());
			commodityLimitSaleMapper.deleteByCommodityId(cb.getId());
			commodityBelongSellerMapper.deleteByCommodityId(cb.getId());

			// MQ通知
			mqSender.commodityDelete(ss.getSpuValue());
		}
	}


    @Override
    @Transactional
    public void focusCommodity(List<String> list, Long sellerId) {
        Long seller = null;
        RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>() {{
            this.add(sellerId);
        }}, 1));
        if (!"100200".equals(RemoteUtil.getErrorCode()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, RemoteUtil.getMsg());
        List<Map> result = RemoteUtil.getList();
        if (result == null || result.isEmpty())
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家id不存在");
        Map user = result.get(0);
        if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家不存在");
        Integer type = (Integer) user.get("platformType");
        Integer userId = (Integer) user.get("userId");
        Integer parentId = (Integer) user.get("topUserId");
        if (type != null && type.intValue() == 1 && userId != null) {
            if (parentId.intValue() != 0) {
                seller = Long.valueOf(parentId);
            } else {
                seller = Long.valueOf(userId);
            }
        }
        for (String id : list) {
            CommodityBase cb = commodityBaseMapper.selectByPrimaryKey(Long.valueOf(id));
            if (cb == null)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, com.rondaful.cloud.common.utils.Utils.i18n("商品id") + id + com.rondaful.cloud.common.utils.Utils.i18n("不存在"));
            Long finalSeller = seller;
            List cf = commodityFocusMapper.page(new CommodityFocus() {{
                this.setCommodityId(id);
                this.setSellerId(finalSeller);
            }});
            if (!cf.isEmpty())
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不可重复关注商品");
            Long finalSeller1 = seller;
            commodityFocusMapper.insertSelective(new CommodityFocus() {{
                this.setCreatTime(DateUtil.getCurrentDate());
                this.setCommodityId(id);
                this.setSellerId(finalSeller1);
            }});
        }
    }


    @Override
    @Transactional
    public void cancelFocusCommodity(List<String> list, Long sellerId) {
        Long seller = null;
        RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>() {{
            this.add(sellerId);
        }}, 1));
        if (!"100200".equals(RemoteUtil.getErrorCode()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, RemoteUtil.getMsg());
        List<Map> result = RemoteUtil.getList();
        if (result == null || result.isEmpty())
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家id不存在");
        Map user = result.get(0);
        if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家不存在");
        Integer type = (Integer) user.get("platformType");
        Integer userId = (Integer) user.get("userId");
        Integer parentId = (Integer) user.get("topUserId");
        if (type != null && type.intValue() == 1 && userId != null) {
            if (parentId.intValue() != 0) {
                seller = Long.valueOf(parentId);
            } else {
                seller = Long.valueOf(userId);
            }
        }
        for (String iid : list) {
            CommodityFocus cf = commodityFocusMapper.selectByPrimaryKey(Long.valueOf(iid));
            if (cf == null)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, com.rondaful.cloud.common.utils.Utils.i18n("关注商品id") + iid + com.rondaful.cloud.common.utils.Utils.i18n("不存在"));
            Long finalSeller = seller;
            List cflist = commodityFocusMapper.page(new CommodityFocus() {{
                this.setId(Long.valueOf(iid));
                this.setSellerId(finalSeller);
            }});
            if (cflist.isEmpty())
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, com.rondaful.cloud.common.utils.Utils.i18n("商品id") + iid + com.rondaful.cloud.common.utils.Utils.i18n("不存在关注列表中"));
            commodityFocusMapper.deleteByPrimaryKey(Long.valueOf(iid));
        }
    }


    /**
     * 获取sku商品列表
     *
     * @param map
     * @return
     */
    @Override
    public Page selectSkuList(Map map) {
        Page.builder((String) map.get("page"), (String) map.get("row"));
        List<CommoditySpec> list = commoditySpecMapper.selectSkuList(map);
        //库存
        inventoryForDetail(list);
        /*for (CommoditySpec commoditySpec : list) {
        	List<SkuWarehouseInfo> skuWarehouseInfoList=skuWarehouseInfoMapper.selectBySku(commoditySpec.getSystemSku());
        	if (skuWarehouseInfoList != null && skuWarehouseInfoList.size()>0) {
        		commoditySpec.setSkuWarehouseInfoList(skuWarehouseInfoList);
        		List<SkuInventoryVo> inventoryList=new ArrayList<SkuInventoryVo>();
        		SkuInventoryVo vo=null;
        		for (SkuWarehouseInfo info : commoditySpec.getSkuWarehouseInfoList()) {
        			vo=new SkuInventoryVo();
                	vo.setWarehouseId(info.getWarehouseId().intValue());
                	vo.setWarehousePrice(info.getWarehousePriceUs().toString());
                	inventoryList.add(vo);
        		}
        		commoditySpec.setInventoryList(inventoryList);
        	}
		}*/
        //供应商信息
        constructionSupplierForSpec(list);
        PageInfo pageInfo = new PageInfo(list);
        return new Page(pageInfo);
    }


    /**
     * @param spu
     * @param platform     平台名称
     * @param siteCode     站点编码
     * @param
     * @param categoryPath 分类路径
     * @return void
     * @Description: 新增或更新SPU分类映射
     * @author:范津
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateSpuCategory(String spu, String platform, String siteCode, Long platCategoryId, String categoryPath) {
        log.info("新增或更新SPU分类映射，入参====>" + spu + "," + platform + "," + siteCode + "," + platCategoryId + "," + categoryPath);
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("spu", spu);
        param.put("platform", platform);
        param.put("siteCode", siteCode);
    	SpuCategory spuCategory = spuCategoryMapper.queryBySpuPlatformSite(param);
        if (spuCategory == null) {
            //新增
            Long categoryLevel3 = null;
            Map<String, Object> map = new HashedMap<String, Object>();
            map.put("SPU", spu);
            List<CommodityBase> list = commodityBaseMapper.selectCommodityListBySpec(map);
            if (list != null && list.size() > 0) {
                if (list.get(0).getCategoryLevel3() != null) {
                    categoryLevel3 = list.get(0).getCategoryLevel3();
                } else {
                    categoryLevel3 = list.get(0).getCategoryLevel2();
                }
            }

            spuCategory = new SpuCategory();
            spuCategory.setSpu(spu);
            spuCategory.setPlatform(platform);
            spuCategory.setSiteCode(siteCode);
            spuCategory.setPlatCategoryId(platCategoryId);
            spuCategory.setCategoryLevel3(categoryLevel3);
            spuCategory.setCategoryPath(categoryPath);
            spuCategoryMapper.insert(spuCategory);
        } else {
            //更新
            spuCategory.setPlatCategoryId(platCategoryId);
            spuCategory.setCategoryPath(categoryPath);
            spuCategoryMapper.updateBySpuPlatformSite(spuCategory);
        }
    }


    /**
     * @param spu
     * @return List
     * @Description:根据spu查询spu分类映射
     * @author:范津
     */
    @Override
    public List<SpuCategory> querySpuCategoryList(String spu) {
        List<SpuCategory> result = null;
        if (spu != null) {
            result = spuCategoryMapper.queryList(spu);
        }
        return result;
    }

    /**
     * @param platform
     * @param categoryLevel3 品连商品分类ID
     * @return List
     * @Description:查询平台的站点分类映射
     * @author:范津
     */
    @Override
    public List<SiteCategory> querySiteCategoryList(String platform, Long categoryLevel3) {
        if (StringUtils.isBlank(platform)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台名称不能为空");
        if (categoryLevel3 == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连商品分类ID不能为空");
        List<SiteCategory> result = siteCategoryMapper.queryList(platform, categoryLevel3);
        if (result != null && result.size() > 0) {
            for (SiteCategory site : result) {
                site.setSiteName(com.rondaful.cloud.common.utils.Utils.translation(site.getSiteName()));
            }
        }
        return result;
    }

    /**
     * @param platform
     * @return void
     * @Description:清除站点的分类信息
     * @author:范津
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public void cleanUp(String platform, Long categoryLevel3) {
        if (StringUtils.isBlank(platform)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台名称不能为空");
        if (categoryLevel3 == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连商品分类ID不能为空");
        siteCategoryMapper.cleanUp(platform, categoryLevel3);
    }

    /**
     * @param
     * @return void
     * @Description:更新站点分类信息
     * @author:范津
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSiteCategory(List<SiteCategory> siteCategoryList) {
    	Map<String, Object> map = null;
        if (siteCategoryList != null && siteCategoryList.size() > 0) {
            for (SiteCategory siteCategory : siteCategoryList) {
            	if (StringUtils.isBlank(siteCategory.getPlatform()) || StringUtils.isBlank(siteCategory.getSiteCode())
            			|| siteCategory.getCategoryLevel3() == null) {
            		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
				}
            	map= new HashedMap<String, Object>();
                map.put("platform", siteCategory.getPlatform());
                map.put("siteCode", siteCategory.getSiteCode());
                map.put("categoryLevel3", siteCategory.getCategoryLevel3());
            	SiteCategory hasSite=siteCategoryMapper.querySiteCategory(map);
            	if (hasSite==null) {
            		siteCategoryMapper.insert(siteCategory);
				}else {
					siteCategory.setId(hasSite.getId());
	                siteCategory.setVersion(hasSite.getVersion());
	                siteCategoryMapper.updateByPrimaryKey(siteCategory);
				}
            }
        }
    }


    /**
     * @param spu
     * @param platform
     * @param siteCode
     * @return SiteCategory
     * @Description:查询SPU的分类映射
     * @author:范津
     */
    @Override
    public SiteCategory querySpuSiteCategory(String spu, String platform, String siteCode) {
        log.info("查询SPU的分类映射，入参=====>" + spu + "," + platform + "," + siteCode);
        Long categoryLevel3 = null;
        Map<String, Object> map = new HashedMap<String, Object>();
        map.put("SPU", spu);
        List<CommodityBase> list = commodityBaseMapper.selectCommodityListBySpec(map);
        if (list != null && list.size() > 0) {
            if (list.get(0).getCategoryLevel3() != null) {
                categoryLevel3 = list.get(0).getCategoryLevel3();
            }
        }
		map.clear();
        map.put("platform", platform);
        map.put("siteCode", siteCode);
        map.put("categoryLevel3", categoryLevel3);
        map.put("spu", spu);
        //1.先关联spu分类绑定映射,分类映射表取查询信息和分类模板一，分类模板二字段
        SiteCategory siteCategory = siteCategoryMapper.querySiteCategoryInfo(map);
        //2.如果1没有数据，则查询分类映射表返回数据
        if (siteCategory == null) {
        	 if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())) {
     			//速卖通
             	BindCategoryAliexpress categoryAliexpress=bindCategoryAliexpressMapper.getBindByCategoryId(categoryLevel3);
             	if (categoryAliexpress != null && StringUtils.isNotBlank(categoryAliexpress.getAliCategoryIds())) {
             		siteCategory = new SiteCategory();
             		String[] aliCategoryIdArr=categoryAliexpress.getAliCategoryIds().split(",");
             		String platCategoryId=aliCategoryIdArr[aliCategoryIdArr.length-1];
             		siteCategory.setCategoryLevel3(categoryLevel3);
             		siteCategory.setPlatCategoryId(Long.parseLong(platCategoryId));
             		siteCategory.setPlatform(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform());
     			}
     		}else {
     			siteCategory = siteCategoryMapper.querySiteCategory(map);
			}
        }
        if (siteCategory != null && siteCategory.getPlatCategoryId() != null ) {
        	return siteCategory;
		}else {
			return null;
		}
		
    }


    /**
     * @param commondityId
     * @return Long 返回复制后的商品ID
     * @Description:复制商品
     * @author:范津
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public synchronized Long copyCommodity(Long commondityId) {
        Long id = null;
        try {
        	//日志操作人
        	String optUser="";
            UserAll userAll=getLoginUserInformationByToken.getUserInfo();
            if (userAll!=null) {
            	UserCommon user = userAll.getUser();
            	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
            		optUser="【供应商】"+user.getUsername();
            	}else if (UserEnum.platformType.CMS.getPlatformType().equals(user.getPlatformType())) {//管理平台
            		optUser="【品连】"+user.getUsername();
            	}
            }
        	
            CommodityBase com = commodityBaseMapper.selectByPrimaryKey(commondityId);
            if (com == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "原商品不存在");
            Category category1 = categoryMapper.selectByPrimaryKey(com.getCategoryLevel1());
            Category category2 = categoryMapper.selectByPrimaryKey(com.getCategoryLevel2());

            List<CommodityDetails> cdlist = commodityDetailsMapper.page(new CommodityDetails() {{
                this.setCommodityId(commondityId);
            }});
            List<CommoditySpec> cslist = commoditySpecMapper.page(new CommoditySpec() {{
                this.setCommodityId(commondityId);
            }});

            CommodityDetails details = new CommodityDetails();
            if (cdlist != null && cdlist.size() > 0) {
                BeanUtils.copyProperties(details, cdlist.get(0));
            }

            //新的SPU
            String snow=String.valueOf(SnowFlakeUtil.nextId());
            String SPU = snow.substring(snow.length()-8, snow.length());
            int spuCount=systemSpuMapper.getSpuCount(SPU);
            while (spuCount>0){
	           	 snow=String.valueOf(SnowFlakeUtil.nextId());
	           	 SPU = snow.substring(snow.length()-8, snow.length());
	           	 spuCount=systemSpuMapper.getSpuCount(SPU);
			}
            
            SystemSpu ss = new SystemSpu();
            ss.setCategoryLevel1(category1.getId());
            ss.setCategoryLevel2(category2.getId());
            ss.setCreatTime(DateUtil.DateToString(new Date()));
            ss.setSpuValue(SPU);
            systemSpuMapper.insertSelective(ss);

            //新的communityBase
            CommodityBase commodityBase = new CommodityBase();
            BeanUtils.copyProperties(commodityBase, com);
            commodityBase.setCreatTime(DateUtil.getCurrentDate());
            commodityBase.setSpuId(ss.getId());
            commodityBase.setId(null);
            commodityBase.setSupplierSpu(null);
            commodityBaseMapper.insertSelective(commodityBase);

            id = commodityBase.getId();
            //新的商品detail
            details.setCommodityId(commodityBase.getId());
            details.setId(null);
            commodityDetailsMapper.insertSelective(details);

            //商品SKU
            if (cslist != null && cslist.size() > 0) {
            	SkuOperateLog skuLog=null;
                int i = 1;
                for (CommoditySpec spec : cslist) {
                    spec.setSystemSku(SPU + String.format(String.format("%%0%dd", 3), i));
                    spec.setSupplierSku(null);
                    spec.setCommodityId(commodityBase.getId());
                    spec.setState(-1);
                    spec.setFeePrice(spec.getFeePrice());
                    spec.setFeeRate(spec.getFeeRate());
                    spec.setId(null);
                    commoditySpecMapper.insertSelective(spec);
                    i++;
                    
                    //插入操作日志
        			skuLog=new SkuOperateLog();
        			skuLog.setOperateBy(optUser);
        			skuLog.setOperateInfo(SkuOperateInfoEnum.COPY_ADD.getMsg());
        			skuLog.setSystemSku(spec.getSystemSku());
        			skuOperateLogService.addSkuLog(skuLog);
                }
            }
        } catch (Exception e) {
        	log.error("复制商品异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return id;
    }


    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public void upperAndLowerFrames(List<String> ids, Boolean type) {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku的id不能为空");
        
        //日志操作人
    	String optUser="";
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
        		optUser="【供应商】"+user.getUsername();
        	}else if (UserEnum.platformType.CMS.getPlatformType().equals(user.getPlatformType())) {//管理平台
        		optUser="【品连】"+user.getUsername();
        	}
        }
        
        SkuOperateLog skuLog=null;
        //上架失败的sku
        List<String> faiList1=new ArrayList<String>();
        List<String> faiList2=new ArrayList<String>();
        for (String id : ids) {
            CommoditySpec com = commoditySpecMapper.selectByPrimaryKey(Long.valueOf(id));
            if (com == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格不存在");
            
            //只能上架审核通过的商品
            if (type) {
                if (com.getState() != 1)
                	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "只能操作待上架的商品");
               
                //入仓校验
                List<String> skulist = new ArrayList<String>();
                skulist.add(com.getSystemSku());
                Map map = (Map) remoteSupplierService.getBySku(skulist);
                if (map == null || map.get("data") == null || !ResponseCodeEnum.RETURN_CODE_100200.getCode().equals(map.get("errorCode")) 
                		|| ((List)map.get("data")).size()==0) {
                	faiList1.add(com.getSystemSku());
                	continue;
                }
                
                //上架最大数限制
                Long commodityId = com.getCommodityId();
                CommodityBase commodityBase = commodityBaseMapper.selectByPrimaryKey(commodityId);
                //erp商品外
                if (commodityBase.getSupplierId() != 100) {
                	//获取对应供应商最大上架数
                	RemoteUtil.invoke(remoteUserService.getSupplierList(new HashSet<Long>() {{
                        this.add(commodityBase.getSupplierId());
                    }}, 0));
                    List<Map> result = RemoteUtil.getList();
                    if (result == null || result.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不存在");
                    Map user = result.get(0);
                    if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商不存在");
                    Integer maxCommodity = (Integer) user.get("maxCommodity");
                    
                    //获取对应供应商已上架的sku数
                    int supplierSkuNum=commoditySpecMapper.getSkuNumBySupplierId(commodityBase.getSupplierId(), 3);
                    
                    //有限制，且达到限制数
                    if (maxCommodity != null && maxCommodity > 0 && supplierSkuNum >= maxCommodity) {
                    	faiList2.add(com.getSystemSku());
                    	continue;
					}
                }
                //不能上架erp下架的
                if (com.getDownStateType() != null && com.getDownStateType().intValue()==2) {
                	continue;
				}
                
                //设置已上架
                com.setState(3);
                
                mqSender.skuUp(com.getSystemSku());
                
            } else {
                com.setState(1);//下架改成待上架
                com.setDownStateType(1);//手工下架
                mqSender.commodityLowerframes(com.getSystemSku());
                mqSender.skuDown(com.getSystemSku());

                //cms通知
                RemoteUtil.invoke(remoteOrderService.queryMapsNoLimit(com.getSystemSku()));
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
                			messageService.downStateMsg(key.split("==")[0], key.split("==")[1], com.getSystemSku(), 
                					valueSet.toString().substring(valueSet.toString().indexOf("[") + 1, valueSet.toString().indexOf("]")));
                        }
					}
                }

                //下架的是最后一个上架状态的SKU
                int count = commoditySpecMapper.getSpecCount(com.getCommodityId(), Long.parseLong(id), 3);
                if (count == 0) {
                    //删除ES文档
                    commodityJestService.deleteItem(ComomodityIndexConst.INDEX_NAME, ComomodityIndexConst.TYPE_NAME, com.getCommodityId() + "");
                }
            }

            commoditySpecMapper.updateByPrimaryKeySelective(com);

            if (type) {
            	CommodityBase base = commodityBaseMapper.selectByPrimaryKey(com.getCommodityId());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", base.getId());
                map.put("isUp", true);
                initCommodityIndex(map);
            }
            
            //插入操作日志
            skuLog=new SkuOperateLog();
            skuLog.setOperateBy(optUser);
            if (type) {
            	skuLog.setOperateInfo(SkuOperateInfoEnum.DO_UP.getMsg());
			}else {
				skuLog.setOperateInfo(SkuOperateInfoEnum.DO_DOWN.getMsg());
			}
            skuLog.setSystemSku(com.getSystemSku());
            skuOperateLogService.addSkuLog(skuLog);
        }
        
        if (faiList1.size()>0) {
        	throw new GlobalException("-2", "上架失败，sku未入库");
		}else if (faiList2.size()>0) {
        	throw new GlobalException("-1", "上架失败，sku对应供应商已达最大上架数量限制");
		}
    }


    /**
     * @param map
     * @return void
     * @Description:初始化ES文档
     * @author:范津
     */
    @Override
    public void initCommodityIndex(Map<String, Object> map) {
    	CommoditySpec commoditySpec=null;
    	
        List<CommodityBase> list = commodityBaseMapper.selectListForEs(map);
        if (list != null && list.size() > 0) {
            int size = list.size();
            List<CommodityBase> tempList = new ArrayList<CommodityBase>();
            for (int i = 0; i < size; i++) {
                tempList.add(list.get(i));
                if ((i + 1) % 1000 == 0 || (i + 1) == size) {//每次最多1000个，分段查询
                    map.put("list", tempList);
                    Page.builder("1", String.valueOf(Integer.MAX_VALUE));
                    List<CommoditySpec> listspc = commoditySpecMapper.selectCommoditySpecByCommodityId(map);
                    // 构造库存
                    constructionSku(listspc);
                    
                    for (CommodityBase cb : tempList) {
                        //设置搜索字段
                        CommoditySearchVo searchVo = new CommoditySearchVo();
                        StringBuffer keyWordBuffer = new StringBuffer();
                        keyWordBuffer.append(cb.getSPU()).append(" ")
                                .append(cb.getVendibilityPlatform()).append(" ");

                        List<String> systemSkuList = new ArrayList<String>();
                        Set<String> commoditySpecList = new HashSet<String>();
                        Set<String> commodityNameCnList = new HashSet<String>();
                        Set<String> commodityNameEnList = new HashSet<String>();

                        cb.setProductMarketTime(null);//上市时间如果是空字符串，插入会报错，直接设置为null
                        cb.setCommoditySpecList(new ArrayList<>());
                        Set<String> set = new HashSet<String>();
                        
                        for (CommoditySpec cs : listspc) {
                            // sku与spu匹配
                            if (cs.getCommodityId().intValue() == cb.getId().intValue()) {
                                cb.getCommoditySpecList().add(cs);
                                
                                if (cb.getSupplierId() == 100) {
                                	//在这里顺序更新下erp商品的商品名称，去除所有括号和括号里的内容
                                	if (cs.getCommodityNameCn().contains("(") || cs.getCommodityNameCn().contains("（")) {
                                		commoditySpec=new CommoditySpec();
                                		commoditySpec.setId(cs.getId());
                                		commoditySpec.setVersion(cs.getVersion());
                                		commoditySpec.setCommodityNameCn(
                                				cs.getCommodityNameCn().replaceAll("（","(").replaceAll("）", ")")
                                					.replaceAll("\\(.*?）|（.*?\\)|\\(.*?\\)|（.*?）|\\[.*\\]", ""));
                                		commoditySpecMapper.updateByPrimaryKeySelective(commoditySpec);
        							}
								}

                                // 设置SPU的规格项
                                if (StringUtils.isNotBlank(cs.getCommoditySpec())) {
                                    String[] specArray = cs.getCommoditySpec().split("\\|");
                                    for (int j = 0; j < specArray.length; j++) {
                                        String[] arr = specArray[j].split(":");
                                        commoditySpecList.add(arr[1]);
                                    }
                                    if (set.size() > 0) {
                                        cb.setCommoditySpecKeys(set.toString().substring(set.toString().indexOf("[") + 1, set.toString().indexOf("]")));
                                    }
                                }
                                systemSkuList.add(cs.getSystemSku());
                                commodityNameCnList.add(cs.getCommodityNameCn());
                                commodityNameEnList.add(cs.getCommodityNameEn());
                            }
                        }

                        //spu属性设置
                        if (cb.getCommoditySpecList().size() > 0) {
                            // 设置商品的标题，sku，商品价，商品中英文名称，商品佣金，取第一个SKU的
                            cb.setTitle(cb.getCommoditySpecList().get(0).getCommodityNameEn());
                            cb.setSystemSku(cb.getCommoditySpecList().get(0).getSystemSku());
                            cb.setCommodityPrice(cb.getCommoditySpecList().get(0).getCommodityPrice());
                            cb.setCommodityPriceUs(cb.getCommoditySpecList().get(0).getCommodityPriceUs());
                            cb.setCommodityNameCn(cb.getCommoditySpecList().get(0).getCommodityNameCn());
                            cb.setCommodityNameEn(cb.getCommoditySpecList().get(0).getCommodityNameEn());

                            //构造总库存数
                            int totalInventory = 0;
                            //spu已售数
                            int saleNum=0;
                            //spu刊登数
                            int publishNum=0;
                            // 仓库ID 
                            StringBuilder warehouseIdBuilder=new StringBuilder();
                            for (CommoditySpec cs : cb.getCommoditySpecList()) {
                            	totalInventory += cs.getInventory();
                            	
                            	if (cs.getSaleNum() != null) {
									saleNum += cs.getSaleNum();
								}
                            	if (cs.getPublishNum() != null) {
                            		publishNum += cs.getPublishNum();
								}
                            	if (StringUtils.isNotBlank(cs.getWarehouseIds())) {
                            		warehouseIdBuilder.append(cs.getWarehouseIds()).append(" ");
								}
                            	
                            }
                            cb.setInventory(totalInventory);
                            cb.setSaleNum(saleNum);
                            cb.setPublishNum(publishNum);
                            if (warehouseIdBuilder.length()>0) {
								cb.setWarehouseId(warehouseIdBuilder.toString());
							}
                        }

                        keyWordBuffer.append(systemSkuList.toString().substring(systemSkuList.toString().indexOf("[") + 1, systemSkuList.toString().indexOf("]"))).append(" ")
                                .append(commoditySpecList.toString().substring(commoditySpecList.toString().indexOf("[") + 1, commoditySpecList.toString().indexOf("]"))).append(" ")
                                .append(commodityNameCnList.toString().substring(commodityNameCnList.toString().indexOf("[") + 1, commodityNameCnList.toString().indexOf("]"))).append(" ")
                                .append(commodityNameEnList.toString().substring(commodityNameEnList.toString().indexOf("[") + 1, commodityNameEnList.toString().indexOf("]"))).append(" ");

                        searchVo.setSearchKeyWords(keyWordBuffer.toString());
                        searchVo.setCommodityId(cb.getId() + "");
                        searchVo.setCommodityBase(cb);
                        
                        try {
                        	commodityJestService.singleIndexWithId(ComomodityIndexConst.INDEX_NAME, ComomodityIndexConst.TYPE_NAME, searchVo.getCommodityId(), searchVo);
						} catch (Exception e) {
							log.error("ES插入异常",e);
						}
                    }
                    tempList.clear();
                }
            }
        }

    }


    /**
     * @param sku
     * @return CommoditySpec
     * @Description:根据系统sku或者供应商sku查询
     * @author:范津
     */
    @Override
    public CommoditySpec getCommoditySpecBySku(String sku,Integer platform,String siteCode) {
        CommoditySpec result = null;
        List<String> list = new ArrayList<String>();
        list.add(sku);
        List<CommoditySpec> specList = commoditySpecMapper.selectCommoditySpecBySku(list);
        if (specList != null && specList.size() > 0) {
            result = specList.get(0);
        } else {
            specList = commoditySpecMapper.getSystemSkuByUserSku(list);
            if (specList != null && specList.size() > 0) {
                result = specList.get(0);
            }
        }
        if (result != null) {
        	String systemSpu=systemSpuMapper.getSpuBySku(sku);
        	SpuTortRecord param=new SpuTortRecord();
        	param.setSystemSpu(systemSpu);
        	param.setPlatform(platform);
        	param.setSiteCode(siteCode);
        	int num=skuTortRecordMapper.getTortNum(param);
        	if (num>0) {
        		result.setTortFlag(1);
			}else {
				result.setTortFlag(0);
			}
		}
        return result;
    }


    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public void updateSpecNum(List<CodeAndValueVo> data,int type) {
        for (CodeAndValueVo codeAndValueVo : data) {
        	if (StringUtils.isBlank(codeAndValueVo.getCode()) || StringUtils.isBlank(codeAndValueVo.getValue()) || !ValidatorUtil.isMath(codeAndValueVo.getValue())) {
                continue;
            }
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("systemSku",codeAndValueVo.getCode());
            List<CommoditySpec> specList = commoditySpecMapper.getSkuListByPage(map);
            if (specList != null && specList.size() > 0) {
                CommoditySpec comm = new CommoditySpec();
                comm.setId(specList.get(0).getId());
                comm.setVersion(specList.get(0).getVersion());
                if (type==1) {
                	comm.setSaleNum(specList.get(0).getSaleNum() + Integer.parseInt(codeAndValueVo.getValue()));
				}else if (type==2) {
					comm.setPublishNum(specList.get(0).getPublishNum() + Integer.parseInt(codeAndValueVo.getValue()));
				}
                commoditySpecMapper.updateByPrimaryKeySelective(comm);
            }
        }
    }


	@Override
	public Page getSkuListByPage(Map map) {
		Page.builder((String) map.get("page"), (String) map.get("row"));
        List<CommoditySpec> list = commoditySpecMapper.getSkuListByPage(map);
        PageInfo pageInfo = new PageInfo(list);
        return new Page(pageInfo);
	}
	
	
	
	 /**
     * MQ通知后台管理发送商品审核通知
     */
    public void sendMessage(String audit, List<CommoditySpec> commoditySpecs) {
        constructionSupplierForSpec(commoditySpecs);
        for (CommoditySpec cs : commoditySpecs) {
            Message msg = new Message();
            msg.setMessageCategory("COMMODITY_MESSAGE");
            msg.setMessageContent(cs.getSystemSku());
            msg.setMessagePlatform("0");
            msg.setMessageScceptUserName(cs.getSupplierName());
            msg.setReceiveSys("0");
            if ("1".equals(audit)) {
                msg.setMessageType("COMMODITY_AUDITING_SUCCESSFUL");
            } else {
                msg.setMessageType("COMMODITY_AUDITING_FAILED");
            }
            messageSender.sendMessage(JSONObject.fromObject(msg).toString());
        }
    }


    /**
     * 服务远程调用构造sku库存信息
     *
     * @param commoditySpecs
     */
    public void constructionSku(List<CommoditySpec> commoditySpecs) {
        try {
            List<String> list = new ArrayList<String>() {{
                for (CommoditySpec cs : commoditySpecs) {
                    this.add(cs.getSystemSku());
                }
            }};
            Map o = (Map) remoteSupplierService.getBySku(list);
            if (o != null && o.get("data") != null) {
                JSONArray ja = JSONArray.fromObject(o.get("data"));
                if (!ja.isEmpty() && ja.size() > 0) {
                    for (CommoditySpec cs : commoditySpecs) {
                        //同一个sku有会在多个仓库的情况，这里需要累加再set
                        int inventoryTotal = 0;
                        StringBuilder warehouseIdBuilder=new StringBuilder();
                        for (int i = 0; i < ja.size(); i++) {
                    		String pinlianSku=(JSONObject.fromObject(ja.get(i))).getString("pinlianSku");
                            String inventory = (JSONObject.fromObject(ja.get(i))).getString("localAvailableQty");
                            Integer warehouseId = (JSONObject.fromObject(ja.get(i))).getInt("warehouseId");
                            if (StringUtils.isNotBlank(pinlianSku) && StringUtils.isNotBlank(inventory) && pinlianSku.equals(cs.getSystemSku())) {
                            	//int num=Integer.parseInt(inventory)<0 ? 0 : Integer.parseInt(inventory);
                            	int num=cs.getInventoryPlay();
                                inventoryTotal += num;
                                
                                warehouseIdBuilder.append(warehouseId).append(" ");
                            }
                        }
                        cs.setInventory(inventoryTotal);
                        if (warehouseIdBuilder.length()>0) {
                        	cs.setWarehouseIds(warehouseIdBuilder.toString());
						}
                    }
                }
            }
        } catch (Exception e) {
        	log.error("获取库存信息异常",e);
        }
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


    /**
     * 服务远程调用构造供应商信息
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
    
    @Override
    public void inventoryForDetail(List<CommoditySpec> commoditySpecs) {
        try {
            List<String> list = new ArrayList<String>() {{
                for (CommoditySpec cs : commoditySpecs) {
                    this.add(cs.getSystemSku());
                }
            }};
            Map o = (Map) remoteSupplierService.getBySku(list);
            if (o != null && o.get("data") != null) {
                JSONArray ja = JSONArray.fromObject(o.get("data"));
                if (!ja.isEmpty() && ja.size() > 0) {
                    for (CommoditySpec cs : commoditySpecs) {
                    	int inventoryTotal = 0;
                    	StringBuilder warehouseIdBuilder=new StringBuilder();
                    	List<SkuInventoryVo> inventoryList=new ArrayList<SkuInventoryVo>();
                    	
                    	String[] warehousePriceArr=null;
                    	if (StringUtils.isNotBlank(cs.getWarehousePriceGroup())) {
                    		warehousePriceArr=cs.getWarehousePriceGroup().split("\\|");
						}
                    	
                        for (int i = 0; i < ja.size(); i++) {
                    		String pinlianSku=(JSONObject.fromObject(ja.get(i))).getString("pinlianSku");
                            String inventory = (JSONObject.fromObject(ja.get(i))).getString("localAvailableQty");
                            Integer warehouseId = (JSONObject.fromObject(ja.get(i))).getInt("warehouseId");
                            String warehouseName=(JSONObject.fromObject(ja.get(i))).getString("warehouseName");
                            
                            if (StringUtils.isNotBlank(pinlianSku) && StringUtils.isNotBlank(inventory) && pinlianSku.equals(cs.getSystemSku())) {
                            	//int num=Integer.parseInt(inventory)<0 ? 0 : Integer.parseInt(inventory);
                            	int num=cs.getInventoryPlay();
                                inventoryTotal += num;
                            	
                            	SkuInventoryVo vo=new SkuInventoryVo();
                            	vo.setInventory(num);
                            	vo.setWarehouseId(warehouseId);
                            	vo.setWarehouseName(com.rondaful.cloud.common.utils.Utils.translation(warehouseName));
                            	
                            	if (warehousePriceArr != null && warehousePriceArr.length>0) {
									for (int j = 0; j < warehousePriceArr.length; j++) {
										if (String.valueOf(warehouseId).equals(warehousePriceArr[j].split(":")[0]) 
												&& StringUtils.isNotBlank(warehousePriceArr[j].split(":")[1])) {
											vo.setWarehousePrice(BigDecimalUtil.formatMoney(Double.parseDouble(warehousePriceArr[j].split(":")[1])));
										}
									}
								}
                            	
                            	/*if (cs.getSkuWarehouseInfoList() != null && cs.getSkuWarehouseInfoList().size()>0) {
									for (SkuWarehouseInfo info : cs.getSkuWarehouseInfoList()) {
										if (warehouseId.equals(info.getWarehouseId().intValue()) && info.getWarehousePriceUs() != null) {
											vo.setWarehousePrice(info.getWarehousePriceUs().toString());
										}
									}
								}*/
                            	
                            	if (StringUtils.isBlank(vo.getWarehousePrice())) {
                            		vo.setWarehousePrice(String.valueOf(cs.getCommodityPriceUs()));
								}
                            	
                            	inventoryList.add(vo);
                            	
                                warehouseIdBuilder.append(warehouseId).append(" ");
                            }
                        }
                        cs.setInventory(inventoryTotal);
                        if (warehouseIdBuilder.length()>0) {
                        	cs.setWarehouseIds(warehouseIdBuilder.toString());
						}
                        if (inventoryList.size()>0) {
                        	cs.setInventoryList(inventoryList);
						}
                    }
                }
            }
        } catch (Exception e) {
            log.error("详情获取库存信息异常",e);
        }
    }


	@Override
	public List<CommoditySpec> getSkuListBySupplierSku(String supplierSku) {
        List<String> list = new ArrayList<String>();
        list.add(supplierSku);
        List<CommoditySpec> specList=commoditySpecMapper.getSystemSkuByUserSku(list);
        if (specList != null && specList.size()>0) {
			for (CommoditySpec commoditySpec : specList) {
				 List<String> sellerIds=commodityBelongSellerMapper.getSellerIdBySku(commoditySpec.getSystemSku());
				 commoditySpec.setSellerIds(sellerIds);
			}
		}
        return specList;
	}
	
}
