package com.rondaful.cloud.commodity.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.discovery.EurekaClient;
import com.rondaful.cloud.commodity.constant.CommonConstant;
import com.rondaful.cloud.commodity.dto.FreightTrialDto;
import com.rondaful.cloud.commodity.dto.LogisticFreight;
import com.rondaful.cloud.commodity.dto.WmsCategory;
import com.rondaful.cloud.commodity.entity.*;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.enums.WarehouseFirmEnum;
import com.rondaful.cloud.commodity.mapper.CategoryMapper;
import com.rondaful.cloud.commodity.mapper.CommodityBaseMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.remote.RemoteOrderService;
import com.rondaful.cloud.commodity.remote.RemoteSupplierService;
import com.rondaful.cloud.commodity.service.*;
import com.rondaful.cloud.commodity.thread.InitEsThread;
import com.rondaful.cloud.commodity.utils.BigDecimalUtil;
import com.rondaful.cloud.commodity.utils.ExcelUtil;
import com.rondaful.cloud.commodity.utils.ValidatorUtil;
import com.rondaful.cloud.commodity.vo.ErpUpdateCommodityVo;
import com.rondaful.cloud.commodity.vo.FreightTrial;
import com.rondaful.cloud.commodity.vo.GranaryFreightTrial;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.service.FileService;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.RemoteUtil;
import com.rondaful.cloud.common.utils.Utils;

import io.swagger.annotations.*;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 通用控制层
 * */
@Api(description="通用API服务控制层")
@RestController
public class CommonController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(CommonController.class);

    @Value("${swagger.enable}")
    public boolean isDev;

    @Value("${erp.freight_trial}")
    public String freight_trial;

    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private CommoditySpecMapper commoditySpecMapper;

    @Autowired
    private ICommonService commonService;

    @Autowired
    private IAttributeService attributeService;

    @Autowired
    private IBrandService brandService;

    @Autowired
    private ICategoryService categoryService;
    
    @Autowired
    private ICommodityService commodityService;
	
	@Autowired
	private CommonJestIndexService commonJestIndexService;
	
	@Autowired
    private RemoteOrderService remoteOrderService;
	
	@Autowired
    private GranaryUtils granaryUtils;
	
	@Autowired
	private RemoteSupplierService remoteSupplierService;
	
	@Value("${wsdl.url}")
    private String goodCangUrl;
	
	@Value("${rondaful.system.env}")
	public String env;

	@Resource
	private FileService fileService;
	
	@Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	@Autowired
	private WmsPushService wmsPushService;
	
	@Autowired
	private CommodityBaseMapper commodityBaseMapper;


    @ApiOperation(value = "服务下线", notes = "")
    @RequestMapping(value = "/offline", method = RequestMethod.GET)
    public void offLine() {
        if (isDev) {
            eurekaClient.shutdown();
        }
    }

    /**
     * @Description:
     * @param rate
     * @param oriCur 当前币种
     * @param distCur 目标币种
     * @return void
     * @author:范津
     */
    private BigDecimal getRate(String oriCur,String distCur) {
    	BigDecimal rate=null;
    	Object obj=remoteOrderService.getRate(oriCur, distCur);
    	com.alibaba.fastjson.JSONObject json=(com.alibaba.fastjson.JSONObject) JSON.toJSON(obj);
        if (json != null) {
        	String data = (String) json.get("data");
            if (data != null ) {
        		rate = new BigDecimal(data);
            }else {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单服务异常，获取"+distCur+"利率为空");
			}
		}
        return rate;
	}
    
    @PostMapping("/common/trial")
    @ApiOperation(value = "运费试算", notes = "", response = Trial.Details.class)
    @RequestRequire(require = "warehouse_code, country_code", parameter = Trial.class)
    public List<Trial.Details> trial(@RequestBody Trial trial) {
    	List<Trial.Details> result = null;
    	//利润率
        if (StringUtils.isBlank(trial.getProfit())) {
            trial.setProfit("0");
        } else {
        	if (Double.parseDouble(trial.getProfit()) >= 100) {
        		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品利润不能大于等于100");
			}
            trial.setProfit(BigDecimalUtil.divide(Double.valueOf(trial.getProfit()), 100).toString());
        }
        //平台佣金率
        if (StringUtils.isBlank(trial.getPlatformFeeRate())) {
            trial.setPlatformFeeRate("0");
        } else {
            trial.setPlatformFeeRate(BigDecimalUtil.divide(Double.valueOf(trial.getPlatformFeeRate()), 100).toString());
        }
        //运费折扣率
        if (StringUtils.isBlank(trial.getFreightRebate())) {
            trial.setFreightRebate("0");
        } else {
            trial.setFreightRebate(BigDecimalUtil.divide(Double.valueOf(trial.getFreightRebate()), 100).toString());
        }
        if (StringUtils.isBlank(trial.getExRateCur())) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "汇率币种不能为空");
		}
        //商品总价
        String sum_money = "0.00";
        
        StringBuilder sb=new StringBuilder();//报错返回
        if (StringUtils.isNotBlank(trial.getProfit()) && !ValidatorUtil.isMathFloat(trial.getProfit()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品利润只能小数点后两位");
        if (StringUtils.isNotBlank(trial.getFreightRebate()) && !ValidatorUtil.isMathFloat(trial.getFreightRebate()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "运费折扣只能小数点后两位");
        
        
        //按件算
        List<Trial.Skus> list = trial.getSkus();
        if (list == null || list.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku信息不能为空");
        Map<String,Object> check = new HashMap<String,Object>();
        //获取系统sku对应的供应商sku
        for (Trial.Skus sku : list) {
            if (check.containsKey(sku.getSku())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku重复");
            check.put(sku.getSku(), sku.getSku());
            List<CommoditySpec> ll = commoditySpecMapper.page(new CommoditySpec(){{
                this.setSystemSku(sku.getSku());
            }});
            if (ll==null || ll.size()==0) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku:" + sku.getSku() + Utils.i18n("错误"));
            }
            CommoditySpec spec=ll.get(0);
            //sum_money=(sku单价*sku个数)+0
            if (StringUtils.isNotBlank(spec.getWarehousePriceGroup())) {
            	//如果有仓库价格，则取对应的仓库价格
            	String[] warehousePriceArr=spec.getWarehousePriceGroup().split("\\|");
            	if (warehousePriceArr != null && warehousePriceArr.length>0) {
					for (int j = 0; j < warehousePriceArr.length; j++) {
						if (trial.getWarehouse_code().equals(warehousePriceArr[j].split(":")[0])) {
							sum_money = BigDecimalUtil.formatMoney(
									BigDecimalUtil.add(
											BigDecimalUtil.multiply(
													Double.parseDouble(warehousePriceArr[j].split(":")[1]), 
													Double.valueOf(sku.getNum())
											), 
											Double.valueOf(sum_money)
									)
							);
						}
					}
				}
			}else {
				 sum_money = BigDecimalUtil.formatMoney(BigDecimalUtil.add(BigDecimalUtil.multiply(spec.getCommodityPriceUs().doubleValue(), Double.valueOf(sku.getNum())), Double.valueOf(sum_money)));
			}
            sb.append(spec.getSystemSku()).append(",");
        }
        
        //组装参数
        FreightTrialDto freight=new FreightTrialDto();
        freight.setCountryCode(trial.getCountry_code());
        if (trial.getShipping_code_arr() != null && trial.getShipping_code_arr().size()>0) {
        	freight.setLogisticsCode(trial.getShipping_code_arr().get(0));
		}
        freight.setPlatformType(trial.getChannel_id());
        freight.setPostCode(trial.getPostCode());
        freight.setSkuList(list);
        freight.setWarehouseId(Integer.parseInt(trial.getWarehouse_code()));
        
    	log.info("调用供应商服务运费试算接口===>{}",JSON.toJSON(freight).toString());
    	Object retnrnObj=remoteSupplierService.getFreight(freight);
    	log.info("供应商服务运费试算返回结果===>{}",JSON.toJSON(retnrnObj).toString());
    	
    	if (retnrnObj != null) {
    		JSONObject jsondata=(JSONObject) JSONObject.toJSON(retnrnObj);
    		if (jsondata != null && jsondata.get("data") != null) {
    			List<LogisticFreight> resultList=com.alibaba.fastjson.JSONArray.parseArray(JSONObject.toJSONString(jsondata.get("data")), LogisticFreight.class);
    			
    			result=new ArrayList<Trial.Details>();
    			List<Object[]>  dataList = new ArrayList<Object[]>();
                Object[] objs = null;
    			for (LogisticFreight logistic : resultList) {
    				if (logistic.getTotalCost()==null) {
    					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "供应商服务返回总运费：null");
					}
    				
    				objs = new Object[8];
                	//总运费(USD)
                	String amount="";
                    //总售价
                    String total_sales_price = null;
                    //折后运费
                    String after_discount_amount = null;
                    
                    //总运费*1.02
                	amount = BigDecimalUtil.formatMoney(BigDecimalUtil.multiply(logistic.getTotalCost().doubleValue(), Double.parseDouble("1.02")));
                    
                    if (BigDecimalUtil.compare(Double.valueOf(trial.getFreightRebate()), 0.00) < 0 || BigDecimalUtil.compare(Double.valueOf(trial.getFreightRebate()), 0.00) == 0) {
                    	//折后运费=总运费
                    	after_discount_amount = amount;
                    	
                    } else {
                    	//折后运费=总运费*运费折扣率
                        after_discount_amount = BigDecimalUtil.formatMoney(BigDecimalUtil.multiply(Double.valueOf(amount), Double.valueOf(trial.getFreightRebate())));
                    }
                	
                	//总售价=(商品成本+折扣运费）/（1-平台佣金率-利润率）
                    total_sales_price = BigDecimalUtil.formatMoney(
            				BigDecimalUtil.divide(BigDecimalUtil.add(Double.valueOf(sum_money), Double.valueOf(after_discount_amount)),
            					BigDecimalUtil.subtract(BigDecimalUtil.subtract(1L, Double.valueOf(trial.getPlatformFeeRate())),Double.valueOf(trial.getProfit()))
            			));
                    
                    //利润=总售价*产品利润率
                    String profit = BigDecimalUtil.formatMoney(
                    		BigDecimalUtil.multiply(
                    				Double.valueOf(total_sales_price),
                    				Double.valueOf(trial.getProfit())
                    		));
                    
                    //平台佣金=总售价*平台佣金率
                    String platformFee=BigDecimalUtil.formatMoney(
                    		BigDecimalUtil.multiply(
                    				Double.valueOf(total_sales_price),
                    				Double.valueOf(trial.getPlatformFeeRate())
                    		));
                    
                    
                    Trial.Details dt=new Trial.Details();
                    dt.setAmount(amount);
                    dt.setSumMoney(sum_money);
                    dt.setProfit(profit);//利润
                    dt.setTotal_sales_price(total_sales_price); //总售价
                    dt.setAfter_discount_amount(after_discount_amount);//折后运费
                    dt.setPlatformFee(platformFee);//平台佣金
                    dt.setEarliest_days(logistic.getMinDeliveryTime()==null?"0":String.valueOf(logistic.getMinDeliveryTime()));
                    dt.setShipping_name(logistic.getLogisticsName());
                    
                    // 如果选择的币种不是USD,那么还要再转一次，拿当前的USD转目标币种
                    BigDecimal rate2=null;
                    if (!"USD".equals(trial.getExRateCur())) {
                    	rate2=getRate("USD", trial.getExRateCur());
                    }
                    
                    if (rate2 != null) {
                    	//商品成本
                        BigDecimal sum_moneyBig=new BigDecimal(sum_money).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                    	//总运费
                        BigDecimal amountBig=new BigDecimal(dt.getAmount()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                        //折后运费
                        BigDecimal after_discount_amountBig=new BigDecimal(dt.getAfter_discount_amount()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                        //平台佣金
                        BigDecimal platformFeeBig=new BigDecimal(dt.getPlatformFee()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                        //利润
                        BigDecimal profitBig=new BigDecimal(dt.getProfit()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                        //总售价
                        BigDecimal total_sales_priceBig=new BigDecimal(dt.getTotal_sales_price()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                        
                        dt.setSumMoney(sum_moneyBig.toString());
                        dt.setAmount(amountBig.toString());
                        dt.setAfter_discount_amount(after_discount_amountBig.toString());
                        dt.setPlatformFee(platformFeeBig.toString());
                        dt.setProfit(profitBig.toString());
                        dt.setTotal_sales_price(total_sales_priceBig.toString());
					}
                    
                    //国际化
                    if (StringUtils.isNotBlank(dt.getShipping_name())) {
                    	dt.setShipping_name(Utils.translation(dt.getShipping_name()));
					}
                    
                    objs[0] = dt.getShipping_name();
        			objs[1] = dt.getEarliest_days();
        			objs[2] = dt.getSumMoney();
        			objs[3] = dt.getAmount();
        			objs[4] = dt.getAfter_discount_amount();
        			objs[5] = dt.getPlatformFee();
        			objs[6] = dt.getProfit();
        			objs[7] = dt.getTotal_sales_price();
        			dataList.add(objs);
        			
        			result.add(dt);
				}
    			
    			if (trial.getIsExport()) {//导出
                	TrialExport(trial.getExRateCur(),dataList);
                    return null;
                }
			}else {
				log.info("供应商服务返回data为null");
			}
		}
    	
        return result;
    }

    //@PostMapping("/common/trial2")
    //@ApiOperation(value = "运费试算", notes = "", response = Trial.Details.class)
    @RequestRequire(require = "warehouse_code, country_code, search_type", parameter = Trial.class)
    @AspectContrLog(descrption = "运费试算",actionType = SysLogActionType.QUERY)
    public List<Trial.Details> trial2(@RequestBody Trial trial) {
    	if (trial.getCallPlatform()==null) {
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "调用平台不能为空");
		}
    	//利润率
        if (StringUtils.isBlank(trial.getProfit())) {
            trial.setProfit("0");
        } else {
            trial.setProfit(BigDecimalUtil.divide(Double.valueOf(trial.getProfit()), 100).toString());
        }
        //平台佣金率
        if (StringUtils.isBlank(trial.getPlatformFeeRate())) {
            trial.setPlatformFeeRate("0");
        } else {
            trial.setPlatformFeeRate(BigDecimalUtil.divide(Double.valueOf(trial.getPlatformFeeRate()), 100).toString());
        }
        
        //运费折扣率
        if (StringUtils.isBlank(trial.getFreightRebate())) {
            trial.setFreightRebate("0");
        } else {
            trial.setFreightRebate(BigDecimalUtil.divide(Double.valueOf(trial.getFreightRebate()), 100).toString());
        }
        if (StringUtils.isBlank(trial.getExRateCur())) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "汇率币种不能为空");
		}
        
        //商品总价
        String sum_money = "0.00";
        
        //根据仓库ID获取仓库code
		String appKey="";
		String appToken="";
		String warehouseCode="";
        RemoteUtil.invoke(remoteSupplierService.getWarehouseById(Integer.parseInt(trial.getWarehouse_code())));
		Map remoteMap = RemoteUtil.getMap();
		if (remoteMap != null) {
			appKey=(String) remoteMap.get("appKey");
			appToken=(String) remoteMap.get("appToken");
			warehouseCode=(String) remoteMap.get("warehouseCode");
		}
		
		if (StringUtils.isBlank(warehouseCode)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, Utils.translation("根据仓库ID："+trial.getWarehouse_code()+"获取不到仓库code"));
		}
    	
    	if (trial.getCallPlatform()==0) {//ERP
    		StringBuilder sb=new StringBuilder();//报错返回
            if (StringUtils.isNotBlank(trial.getProfit()) && !ValidatorUtil.isMathFloat(trial.getProfit()))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品利润只能小数点后两位");
            if (StringUtils.isNotBlank(trial.getFreightRebate()) && !ValidatorUtil.isMathFloat(trial.getFreightRebate()))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "运费折扣只能小数点后两位");
            
            Map map = new HashMap();
            map.put("channel_id", trial.getChannel_id());
            map.put("warehouse_code", warehouseCode);
            map.put("country_code", trial.getCountry_code());
            map.put("search_type", trial.getSearch_type());
            map.put("shipping_code_arr", JSONArray.fromObject(trial.getShipping_code_arr()).toString());
            
            if ("1".equals(trial.getSearch_type())) {//根据重量算
                if (StringUtils.isBlank(trial.getLength()) || StringUtils.isBlank(trial.getHeight()) || StringUtils.isBlank(trial.getWidth()) || StringUtils.isBlank(trial.getWeight())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "长、宽、高、重量不能为空");
                }
                map.put("length", trial.getLength());
                map.put("width", trial.getWidth());
                map.put("height", trial.getHeight());
                map.put("weight", trial.getWeight());
            } else if ("2".equals(trial.getSearch_type())) {//按件算
                List<Trial.Skus> list = trial.getSkus();
                if (list == null || list.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku信息不能为空");
                Map check = new HashMap();
                //获取系统sku对应的供应商sku
                for (Trial.Skus sku : list) {
                    if (check.containsKey(sku.getSku())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku重复");
                    check.put(sku.getSku(), sku.getSku());
                    List<CommoditySpec> ll = commoditySpecMapper.page(new CommoditySpec(){{
                        this.setSystemSku(sku.getSku());
                    }});
                    if (ll==null || ll.size()==0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku:" + sku.getSku() + Utils.i18n("错误"));
                    sku.setSku(ll.get(0).getSupplierSku());
                    //sum_money=(sku单价*sku个数)+0
                    sum_money = BigDecimalUtil.formatMoney(BigDecimalUtil.add(BigDecimalUtil.multiply(ll.get(0).getCommodityPriceUs().doubleValue(), Double.valueOf(sku.getNum())), Double.valueOf(sum_money)));
                    sb.append(ll.get(0).getSystemSku()).append(",");
                }
                map.put("skus", JSONArray.fromObject(list).toString());
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "search_type只能为1或2");
            }
            try {
            	log.info("调用ERP运费试算接口："+freight_trial+"/post?url=trial&version=1.0.0&app=distribution&mark=shipping&sign=a02d257a40f3c3a6ed22462cd3e02f78"+map);
                JSONObject jb = JSON.parseObject(HttpUtil.postSendByFormData(freight_trial, "trial", map));
                log.info("ERP运费试算返回结果："+jb.toJSONString());
                if (StringUtils.isNotBlank(jb.getString("status")) && jb.getString("status").equals("1")) {
                    com.alibaba.fastjson.JSONArray ja = jb.getJSONArray("data");
                    List<Trial.Details> result = ja.toJavaList(Trial.Details.class);
                    List<Object[]>  dataList = new ArrayList<Object[]>();
                    Object[] objs = null;
                    for (Trial.Details dt : result) {
                    	objs = new Object[8];
                    	//总运费(CNY)
                    	String amount="";
                        //总售价
                        String total_sales_price = null;
                        //折后运费
                        String after_discount_amount = null;
                        
                        // CNY先全部转为USD
                    	BigDecimal rate=getRate("CNY", "USD");
                    	
                    	//总运费
                    	amount = BigDecimalUtil.formatMoney(BigDecimalUtil.multiply(Double.valueOf(dt.getCny_amount()), rate.doubleValue()));
                    	
                    	if (BigDecimalUtil.compare(Double.valueOf(trial.getFreightRebate()), 0.00) < 0 || BigDecimalUtil.compare(Double.valueOf(trial.getFreightRebate()), 0.00) == 0) {
                        	//折后运费=总运费
                        	after_discount_amount = amount;
                        	
                        } else {
                        	//折后运费=总运费*运费折扣率
                            after_discount_amount = BigDecimalUtil.formatMoney(BigDecimalUtil.multiply(Double.valueOf(amount), Double.valueOf(trial.getFreightRebate())));
                        }
                    	
                    	//总售价=(商品成本+折扣运费）/（1-平台佣金率-利润率）
                        total_sales_price = BigDecimalUtil.formatMoney(
                				BigDecimalUtil.divide(BigDecimalUtil.add(Double.valueOf(sum_money), Double.valueOf(after_discount_amount)),
                					BigDecimalUtil.subtract(BigDecimalUtil.subtract(1L, Double.valueOf(trial.getPlatformFeeRate())),Double.valueOf(trial.getProfit()))
                			));
                        
                        //利润=总售价*产品利润率
                        String profit = BigDecimalUtil.formatMoney(
                        		BigDecimalUtil.multiply(
                        				Double.valueOf(total_sales_price),
                        				Double.valueOf(trial.getProfit())
                        		));
                        
                        //平台佣金=总售价*平台佣金率
                        String platformFee=BigDecimalUtil.formatMoney(
                        		BigDecimalUtil.multiply(
                        				Double.valueOf(total_sales_price),
                        				Double.valueOf(trial.getPlatformFeeRate())
                        		));
                        
                    	dt.setAmount(amount);
                        dt.setSumMoney(sum_money);
                        dt.setProfit(profit);//利润
                        dt.setTotal_sales_price(total_sales_price); //总售价
                        dt.setAfter_discount_amount(after_discount_amount);//折后运费
                        dt.setPlatformFee(platformFee);//平台佣金
                        
                        //如果选择的币种不是USD,那么还要再转一次，拿当前的USD转目标币种
                        BigDecimal rate2=null;
                        if (!"USD".equals(trial.getExRateCur())) {
                        	rate2=getRate("USD", trial.getExRateCur());
                        }
                        
                        if (rate2 != null) {
                        	//商品成本
                            BigDecimal sum_moneyBig=new BigDecimal(sum_money).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                        	//总运费
                            BigDecimal amountBig=new BigDecimal(dt.getAmount()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            //折后运费
                            BigDecimal after_discount_amountBig=new BigDecimal(dt.getAfter_discount_amount()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            //平台佣金
                            BigDecimal platformFeeBig=new BigDecimal(dt.getPlatformFee()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            //利润
                            BigDecimal profitBig=new BigDecimal(dt.getProfit()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            //总售价
                            BigDecimal total_sales_priceBig=new BigDecimal(dt.getTotal_sales_price()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            
                            dt.setSumMoney(sum_moneyBig.toString());
                            dt.setAmount(amountBig.toString());
                            dt.setAfter_discount_amount(after_discount_amountBig.toString());
                            dt.setPlatformFee(platformFeeBig.toString());
                            dt.setProfit(profitBig.toString());
                            dt.setTotal_sales_price(total_sales_priceBig.toString());
    					}
                        
                        //国际化
                        if (StringUtils.isNotBlank(dt.getShipping_name())) {
                        	dt.setShipping_name(Utils.translation(dt.getShipping_name()));
    					}
                        
                        objs[0] = dt.getShipping_name();
            			objs[1] = dt.getEarliest_days();
            			objs[2] = dt.getSumMoney();
            			objs[3] = dt.getAmount();
            			objs[4] = dt.getAfter_discount_amount();
            			objs[5] = dt.getPlatformFee();
            			objs[6] = dt.getProfit();
            			objs[7] = dt.getTotal_sales_price();
            			dataList.add(objs);
                    }
                    
                    if (trial.getIsExport()) {//导出
                    	TrialExport(trial.getExRateCur(),dataList);
                        return null;
                    }
                    return result;
                } else {
                	if (sb.length()>0) {
                		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, Utils.translation("此仓库无"+sb.toString().substring(0,sb.toString().length()-1)+"的SKU"));
    				}else {
    					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, jb.getString("message"));
    				}
                }
            } catch (Exception e) {
            	log.error("调用erp运费试算异常",e);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, StringUtils.isBlank(e.getMessage())?"系统异常":e.getMessage());
            }
		}else if (trial.getCallPlatform()==1) {//谷仓
			List<Trial.Details> result =new ArrayList<Trial.Details>();
			List<Trial.Skus> list = trial.getSkus();
            if (list == null || list.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku信息不能为空");
            Map check = new HashMap();
            for (Trial.Skus sku : list) {
                if (check.containsKey(sku.getSku())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku重复");
                check.put(sku.getSku(), sku.getSku());
                List<CommoditySpec> ll = commoditySpecMapper.page(new CommoditySpec(){{
                    this.setSystemSku(sku.getSku());
                }});
                if (ll==null || ll.size()==0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku:" + sku.getSku() + Utils.i18n("错误"));
                
                //sum_money=(sku单价*sku个数)+0
                sum_money = BigDecimalUtil.formatMoney(BigDecimalUtil.add(BigDecimalUtil.multiply(ll.get(0).getCommodityPriceUs().doubleValue(), Double.valueOf(sku.getNum())), Double.valueOf(sum_money)));
			}
			
            if (StringUtils.isBlank(appKey) || StringUtils.isBlank(appToken)) {
    			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, Utils.translation("根据仓库ID："+trial.getWarehouse_code()+"获取不到仓库appKey和appToken"));
    		}
            
			FreightTrial freightTrial=new FreightTrial();
			freightTrial.setWarehouseCode(warehouseCode);
			freightTrial.setCountryCode(trial.getCountry_code());
			freightTrial.setPostCode(trial.getPostCode());
			List<String> skuList=new ArrayList<String>();
			for (Trial.Skus sku : trial.getSkus()) {
				skuList.add(sku.getSku()+":"+sku.getNum());
			}
			freightTrial.setGcSKUList(skuList);
			
			List<GranaryFreightTrial> granaryResult=getGranaryFreightTrial(freightTrial,appKey,appToken);
			
			 List<Object[]> dataList = new ArrayList<Object[]>();
             Object[] objs = null;
			if (granaryResult != null && granaryResult.size()>0) {
				for (GranaryFreightTrial granaryTrial : granaryResult) {
					objs = new Object[8];
					Trial.Details tDetails=new Trial.Details();
					//物流方式
					if (isEnNameSearch()) {
						tDetails.setShipping_name(granaryTrial.getSm_name());
					}else {
						tDetails.setShipping_name(granaryTrial.getSm_name_cn());
					}
					//预计运输时效,最快天数
					tDetails.setEarliest_days(granaryTrial.getSm_delivery_time_min()==null?"0":String.valueOf(granaryTrial.getSm_delivery_time_min()));
					//最忙天数（不设置0）
					tDetails.setLatest_days(granaryTrial.getSm_delivery_time_max()==null?"0":String.valueOf(granaryTrial.getSm_delivery_time_max()));
					//总运费
					tDetails.setAmount(granaryTrial.getTotal()==null?"0":String.valueOf(granaryTrial.getTotal()));

					//总运费
					String amount="";
					//总售价
                    String total_sales_price = null;
                    //折后运费
                    String after_discount_amount = null;
                    
                    if (StringUtils.isNotBlank(granaryTrial.getCurrency())) {
                    	String oriCur="";
                    	if ("rmb".equalsIgnoreCase(granaryTrial.getCurrency())) {
                    		oriCur="CNY";
						}else {
							oriCur=granaryTrial.getCurrency();
						}
                    	
                    	BigDecimal rate=null;
                    	//如果谷仓返回的币种不是美元，获取美元利率，将所有价格转换为美元再计算，因为商品价格是取的美元
                    	if (!"USD".equals(oriCur)) {
                    		rate=getRate(oriCur,"USD");
						}
                    	
                    	if (rate == null) {
                    		rate=new BigDecimal(1);
						}
                    	
                    	//总运费
                    	amount = BigDecimalUtil.formatMoney(BigDecimalUtil.multiply(Double.valueOf(tDetails.getAmount()), rate.doubleValue()));
                        
                        if (BigDecimalUtil.compare(Double.valueOf(trial.getFreightRebate()), 0.00) < 0 || BigDecimalUtil.compare(Double.valueOf(trial.getFreightRebate()), 0.00) == 0) {
                        	//折后运费=总运费
                        	after_discount_amount = amount;
                        	
                        } else {
                        	//折后运费=总运费*运费折扣率
                            after_discount_amount = BigDecimalUtil.formatMoney(BigDecimalUtil.multiply(Double.valueOf(amount), Double.valueOf(trial.getFreightRebate())));
                        }
                        
                        //总售价=(商品成本+折扣运费）/（1-平台佣金率-利润率）
                        total_sales_price = BigDecimalUtil.formatMoney(
                				BigDecimalUtil.divide(BigDecimalUtil.add(Double.valueOf(sum_money), Double.valueOf(after_discount_amount)),
                					BigDecimalUtil.subtract(BigDecimalUtil.subtract(1L, Double.valueOf(trial.getPlatformFeeRate())),Double.valueOf(trial.getProfit()))
                			));
                        
                        //利润=总售价*产品利润率
                        String profit = BigDecimalUtil.formatMoney(
                        		BigDecimalUtil.multiply(
                        				Double.valueOf(total_sales_price),
                        				Double.valueOf(trial.getProfit())
                        		));
                        
                        //平台佣金=总售价*平台佣金率
                        String platformFee=BigDecimalUtil.formatMoney(
                        		BigDecimalUtil.multiply(
                        				Double.valueOf(total_sales_price),
                        				Double.valueOf(trial.getPlatformFeeRate())
                        		));
                        
                        tDetails.setAmount(amount);
                        tDetails.setSumMoney(sum_money);
                        tDetails.setProfit(profit);//利润
                        tDetails.setTotal_sales_price(total_sales_price);//总售价
                        tDetails.setAfter_discount_amount(after_discount_amount);//折后运费
                        tDetails.setPlatformFee(platformFee);//平台佣金
                        
                        
                        //如果选择的币种不是USD,那么还要再转一次，拿当前的USD转目标币种
                        BigDecimal rate2=null;
                        if (!"USD".equals(trial.getExRateCur())) {
                        	rate2=getRate("USD", trial.getExRateCur());
                        }
                        if (rate2 != null) {
                        	//商品成本
                            BigDecimal sum_moneyBig=new BigDecimal(sum_money).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                        	//总运费
                            BigDecimal amountBig=new BigDecimal(tDetails.getAmount()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            //折后运费
                            BigDecimal after_discount_amountBig=new BigDecimal(tDetails.getAfter_discount_amount()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            //平台佣金
                            BigDecimal platformFeeBig=new BigDecimal(tDetails.getPlatformFee()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            //利润
                            BigDecimal profitBig=new BigDecimal(tDetails.getProfit()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            //总售价
                            BigDecimal total_sales_priceBig=new BigDecimal(tDetails.getTotal_sales_price()).multiply(rate2).setScale(2, BigDecimal.ROUND_DOWN);
                            
                            tDetails.setSumMoney(sum_moneyBig.toString());
                            tDetails.setAmount(amountBig.toString());
                            tDetails.setAfter_discount_amount(after_discount_amountBig.toString());
                            tDetails.setPlatformFee(platformFeeBig.toString());
                            tDetails.setProfit(profitBig.toString());
                            tDetails.setTotal_sales_price(total_sales_priceBig.toString());
    					}
                        
                        objs[0] = tDetails.getShipping_name();
            			objs[1] = tDetails.getEarliest_days();
            			objs[2] = tDetails.getSumMoney();
            			objs[3] = tDetails.getAmount();
            			objs[4] = tDetails.getAfter_discount_amount();
            			objs[5] = tDetails.getPlatformFee();
            			objs[6] = tDetails.getProfit();
            			objs[7] = tDetails.getTotal_sales_price();
            			dataList.add(objs);
					}else {
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "谷仓返回币种为空");
					}
                    
					result.add(tDetails);
				}
			}
			
			if (trial.getIsExport()) {
				TrialExport(trial.getExRateCur(),dataList);
                return null;
            }
			
			return result;
		}else {
			return null;
		}
    }
    
    private void TrialExport(String currency,List<Object[]> dataList) {
    	String title = "商品利润报表";
    	String[] rowsName = null;
    	ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String header = request.getHeader("i18n");
		if (StringUtils.isNotBlank(header) && "en".equals(header.split("_")[0].toLowerCase())) {
			rowsName = new String[]{
        			"Shipping Method",
        			"Estimated Shipping Failure(Working Days)",
        			"Product Price("+currency+")",
        			"Total Shipping Cost("+currency+")",
        			"After Discount Amount("+currency+")",
        			"Commission("+currency+")",
        			"Profit("+currency+")",
        			"Total Sales Price("+currency+")"
        			};
		}else {
			rowsName = new String[]{
        			"物流方式",
        			"预计运输时效(工作日)",
        			"商品成本("+currency+")",
        			"总运费("+currency+")",
        			"折后运费("+currency+")",
        			"平台佣金("+currency+")",
        			"利润("+currency+")",
        			"总售价("+currency+")"
        			};
		}
		try {
			ExcelUtil ex=new ExcelUtil(title,rowsName,"库存动态导出",dataList,response);
			ex.export();
		} catch (Exception e) {
			log.error("运费试算导出异常",e);
		}
    }
    
    
    /**
     * 获取谷仓运费试算
     *
     * @param freightTrial
     * @return
     */
    protected List<GranaryFreightTrial> getGranaryFreightTrial(FreightTrial freightTrial,String appKey,String appToken) {
        List<GranaryFreightTrial> granaryFreightTrialList = new ArrayList<>();
        try{
        	if (StringUtils.isNotBlank(appKey) && StringUtils.isNotBlank(appToken)) {
        		if (StringUtils.isEmpty(freightTrial.getPostCode()))
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "邮政编码不能为空");
                Map<String, Object> map = new HashMap<>();
                map.put("warehouse_code", freightTrial.getWarehouseCode());//仓库code
                map.put("country_code", freightTrial.getCountryCode());//国家编码
                map.put("postcode", freightTrial.getPostCode());//邮政编码
                map.put("sku", freightTrial.getGcSKUList());//sku集合
                log.info("谷仓运费试算数据===>{}", JSONObject.toJSONString(map));
                
                String dataStr = granaryUtils.getInstance(appToken,appKey,goodCangUrl,JSONObject.toJSONString(map), CommonConstant.GC_GET_CALCULATEDELIVERYFEE).getCallService();
                JSONObject json = JSONObject.parseObject(dataStr);
                if ("Failure".equals(json.getString("ask")))
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, json.getString("message"));
                
                granaryFreightTrialList = JSONObject.parseArray(json.getString("data"), GranaryFreightTrial.class);
                //将全局的货币代码设置进每个对象里
                if (CollectionUtils.isEmpty(granaryFreightTrialList)) return null;
                granaryFreightTrialList.stream().forEach(x -> x.setCurrency(json.getString("currency")));
			}
        } catch (Exception e) {
        	log.error("调用谷仓计算预估物流费出错,订单为 ===>"+freightTrial.getSysOrderId(), e);
        }
        return granaryFreightTrialList;
    }


    @PostMapping("/common/commodity/add")
    @ApiOperation(value = "添加商品", notes = "")
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public Map addCommodity(@RequestBody CommodityBase commodityBase) {
        log.error("ERP推送商品数据开始==>{}", JSON.toJSON(commodityBase).toString());
        if (commodityBase == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "参数不能为空");
        List<CommoditySpec> commoditySpec = commodityBase.getCommoditySpecList();
        CommodityDetails commodityDetails = commodityBase.getCommodityDetails();
        if (commodityDetails == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "详情数据参数不能为空");
        if (CollectionUtils.isEmpty(commoditySpec)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规格不能为空");
        
        if (commodityBase.getCategoryLevel1() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "一级分类不能为空");
        if (commodityBase.getCategoryLevel2() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "二级分类不能为空");
        if (commodityBase.getCategoryLevel3() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "三级分类不能为空");
        
        if (commodityBase.getSupplierId() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商id不能为空");
        if (commodityBase.getBrandName() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品牌名不能为空");
        if (commodityBase.getDefaultRepository() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "默认仓库不能为空");
        if (commodityBase.getIsPrivateModel() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "是否私模产品不能为空");
        if (commodityBase.getProductLogisticsAttributes() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品物流属性不能为空");
        if (commodityBase.getVendibilityPlatform() == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "可售平台不能为空");
        
        if (StringUtils.isBlank(commodityDetails.getMasterPicture())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品主图不能为空");

        return commonService.addCommodity(commodityBase, commodityDetails, commoditySpec);
    }

    @GetMapping("/common/attribute/list")
    @ApiOperation(value = "查询属性列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page<Attribute> listAttribute(String page, String row, Attribute attribute) {
        Page.builder(page, row);
        Page<Attribute> p = attributeService.page(attribute);
        return p;
    }


    @GetMapping("/common/brand/list")
    @ApiOperation(value = "查询品牌列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page<Brand> listBrand(String page, String row, Brand brand) {
        Page.builder(page, row);
        Page<Brand> p = brandService.page(brand);
        return p;
    }


    @GetMapping("/common/category/list")
    @ApiOperation(value = "查询分类列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page listCategory(String page, String row) throws NoSuchFieldException, IllegalAccessException {
        Page.builder(page, row);
        Page p = categoryService.findList(null,1);
        return p;
    }


    @GetMapping("/common/attribute/listSelective")
    @ApiOperation(value = "根据条件查询分类列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "每页显示行数", dataType = "string", paramType = "query", required = true)})
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page<Category> listSelective(String page, String row, Category category) {
        Page.builder(page, row);
        Page<Category> p = categoryService.page(category);
        return p;
    }


    @GetMapping("/common/commodity/statistics")
    @ApiOperation(value = "商品统计", notes = "up_count，上架数量；down_count，下架数量；audit_pass，审核通过数量")
    public Map statistics() {
    	//2019-03-25  18:30:00 王更(PM)讲的，下架的数量取待上架(1)的,审核通过的数量是取待上架和已上架的
    	
    	Map<String,Long> resultMap = new HashMap<String,Long>();
        
    	Map<String, Object> param=new HashMap<String, Object>();
    	
    	Integer supplierId=null;
    	//判断是否登录
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(user.getPlatformType())) {//供应商平台
        		if (user.getTopUserId() == 0) {//主账号
        			supplierId=user.getUserid();
    			}else {
    				supplierId=user.getTopUserId();
				}
        	}
		}
    	
    	
        param.put("supplierId", supplierId);
        param.put("state",3);//已上架
        Long list1 = commoditySpecMapper.statistics(param);
        param.clear();
        
        param.put("supplierId", supplierId);
        param.put("state",1);//待上架
        Long list2 = commoditySpecMapper.statistics(param);
        param.clear();
        
        param.put("supplierId", supplierId);
        param.put("isUp",1);//审核通过
        Long list3 = commoditySpecMapper.statistics(param);
        
        
        resultMap.put("up_count", list1);
        resultMap.put("down_count", list2);
        resultMap.put("audit_pass", list3);
        return resultMap;
    }
    
    
    @PostMapping("/common/updateCommodityState")
    @ApiOperation(value = "ERP商品上下架", notes = "")
    @CacheEvict(value = "commodityListCache", allEntries = true)
    public void updateCommodityState(@RequestBody ErpUpdateCommodityVo vo){
    	log.info("ERP更新商品上下架状态，参数==>{}", JSON.toJSON(vo).toString());
    	if (vo==null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "参数不能为空");
    	if (!"100".equals(vo.getSupplierId())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商ID非法");	
    	if(!"1".equals(vo.getOptType()) && !"-1".equals(vo.getOptType())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "操作类型错误");
    	if(CollectionUtils.isEmpty(vo.getSupplierSkuList())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商sku不能为空");
    	commonService.UpOrDownStateCommodity(vo);
    }
    
    @GetMapping("/common/getSkuNum")
    @ApiOperation(value = "erp查询sku是否存在", notes = "")
    public int getSkuNum(@RequestParam("supplierSku") String supplierSku){
    	return commoditySpecMapper.getErpSkuNum(supplierSku);
    }
    
    
    @GetMapping("/common/initSearchEs")
    @ApiOperation(value = "卖家首页搜索ES初始化", notes = "")
    public void initSearchEs(){
    	try {
    		new InitEsThread(commodityService,commonJestIndexService).start();
		} catch (Exception e) {
			log.error("卖家首页搜索ES初始化异常",e);
		}
    }
    
    @GetMapping("/common/pushAllCategoryToWms")
    @ApiOperation(value = "推送全部分类到wms", notes = "")
    public void initCategoryToWms(){
    	try {
    		Category category=new Category();
    		category.setStatus(1);
    		List<Category> list=categoryMapper.findCategoryList(category);
    		
    		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
    		RemoteUtil.invoke(remoteSupplierService.getAuth(1));
    		List<Map> resultList = RemoteUtil.getList();
    		if (resultList != null && !resultList.isEmpty()) {
    			for (int i = 0; i < resultList.size(); i++) {
    				Map<String, Object> map = new HashMap<String, Object>();
    				map.put("id", ((Map) resultList.get(i)).get("id"));
    				map.put("code", ((Map) resultList.get(i)).get("code"));
    				map.put("appKey", ((Map) resultList.get(i)).get("appKey"));
    				map.put("appToken", ((Map) resultList.get(i)).get("appToken"));
    				accountList.add(map);
    			}
    		}
    		if (accountList.size() > 0) {
    			for (Map map : accountList) {
    				if (map.get("code") != null && WarehouseFirmEnum.WMS.getCode().equals(map.get("code"))) {
    					if (list != null && list.size() > 0) {
    		    			int size = list.size();
    		    			List<Category> tempList=new ArrayList<Category>();
    		    	        for (int i = 0; i < size; i++) {
    		    	        	tempList.add(list.get(i));
    		    	        	if ((i + 1) % 50 == 0 || (i + 1) == size) {
    		    	        		List<WmsCategory> wmsCaList=new ArrayList<WmsCategory>();
    		    	        		for (Category ca : tempList) {
    		    	        			WmsCategory wmsCategory=new WmsCategory();
    		    						wmsCategory.setCategoryCode(String.valueOf(ca.getId()));
    		    						wmsCategory.setCategoryLevel(ca.getCategoryLevel());
    		    						wmsCategory.setCategoryName(ca.getCategoryName());
    		    						wmsCategory.setCategoryNameEn(ca.getCategoryNameEn());
    		    						wmsCategory.setDataSources("1");
    		    						wmsCategory.setParentCode(String.valueOf(ca.getCategoryParentId()));
    		    						wmsCaList.add(wmsCategory);
    								}
    		    	        		wmsPushService.addCategory((String)map.get("appKey"),(String)map.get("appToken"),wmsCaList);
    		    	        		tempList.clear();
    		    	        	}
    		    	        }
    		    		}
    				}
    			}
    		}
		} catch (Exception e) {
			log.error("推送全部分类到wms异常",e);
		}
    }
    
    @GetMapping("/common/upAll")
    @ApiOperation(value = "上架所有下架的商品", notes = "")
    public void upAll(){
    	try {
    		CommoditySpec param=new CommoditySpec();
    		param.setState(1);
    		List<CommoditySpec> list=commoditySpecMapper.page(param);
    		if (list != null && list.size()>0) {
				for (CommoditySpec comm : list) {
					//不能上架erp下架的
	                if (comm.getDownStateType() != null && comm.getDownStateType().intValue()==2) {
	                	continue;
					}
	                //设置已上架
	                comm.setState(3);
	                commoditySpecMapper.updateByPrimaryKeySelective(comm);
	                
	                CommodityBase base = commodityBaseMapper.selectByPrimaryKey(comm.getCommodityId());
	                Map<String, Object> map = new HashMap<String, Object>();
	                map.put("id", base.getId());
	                map.put("isUp", true);
	                commodityService.initCommodityIndex(map);
				}
			}
		} catch (Exception e) {
			log.error("上架所有下架的商品异常",e);
		}
    }
    
    
    @GetMapping("/common/updateWarehousePriceRmb")
    @ApiOperation(value = "获取有仓库价格的sku并设置仓库价格rmb", notes = "")
    public void updateWarehousePriceRmb(){
    	try {
    		List<CommoditySpec> list=commoditySpecMapper.selectAllWarehousePrice();
    		if (list != null && list.size()>0) {
    			BigDecimal rate = new BigDecimal("0.149253");
				for (CommoditySpec comm : list) {
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
	                commoditySpecMapper.updateByPrimaryKeySelective(comm);
				}
			}
		} catch (Exception e) {
			log.error("获取有仓库价格的sku并设置仓库价格rmb",e);
		}
    }
}
