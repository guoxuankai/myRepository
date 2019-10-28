package com.rondaful.cloud.seller.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.dto.CommodityDTO;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.vo.AmazonDisposePriceVO;
import com.rondaful.cloud.seller.vo.AmazonDisposePriceVO.Item;
import com.rondaful.cloud.seller.vo.AmazonDisposePriceVO.pricingRule;

/**
 * 计价模板相关的计算处理
 * @author 嘻嘻
 *
 */
@Component
public class ComputeTemplateUtil {

    private static Logger logger = LoggerFactory.getLogger(ComputeTemplateUtil.class);


    @Autowired
    public RemoteCommodityService remoteCommodityService;
    
    @Autowired
    public RemoteOrderRuleService remoteOrderRuleService;
    
    
    
    
    /**
     * 计算最终售价       最终价格=销售利润+物流运费+平台佣金+∑自定义费用项
     * @param amazonDisposePriceVO
     */
    public List<CommodityDTO> disposeComputePrice(AmazonDisposePriceVO amazonDisposePriceVO) {
    	List<CommodityDTO> commodity = getCommodity(amazonDisposePriceVO);
    	BigDecimal rate = getRate(amazonDisposePriceVO.getSourceCurrency(), amazonDisposePriceVO.getSiteCurrency());
    	for (CommodityDTO commodityDTO : commodity) {
    		BigDecimal disposePrice = disposePrice(commodityDTO, amazonDisposePriceVO.getPricingRuleJson());
    		commodityDTO.setComputeResultPriceUs(disposePrice);
    		if(disposePrice.compareTo(amazonDisposePriceVO.getPriceUSD())!=0) {
    			logger.error("计价模板价格计算不一致相关参数CommodityDTO{},AmazonDisposePriceVO{}",commodityDTO.toString(),JSON.toJSONString(amazonDisposePriceVO));
    			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "SKU:"+
    		amazonDisposePriceVO.getPlSkus()+"计价有误请重新输入或者修改相关计价数据!");
    		}
    		//汇率转换
    		disposePrice=multiply(disposePrice, rate);
    		commodityDTO.setComputeResultPrice(disposePrice);
		}
    	return commodity;
    }
    
    
    /**
     * 查询汇率
     * @param soStr
     * @param toStr
     * @return
     */
    public  BigDecimal getRate(String soStr,String toStr) {
    	String rateStr = remoteOrderRuleService.GetRate(soStr, toStr);
    	logger.info("计价模板查询汇率获取的数据：{}", rateStr);
    	rateStr = Utils.returnRemoteResultDataString(rateStr, "订单服务异常");
    	return new BigDecimal(rateStr);
    }
    
    
    /**
     * 计算计价模板价格  销售利润+物流运费+平台佣金+∑自定义费用项
     * @param commodityDTO
     */
    public BigDecimal disposePrice(CommodityDTO commodityDTO,String pricingRuleJson) {
    	pricingRule pricingRule = JSON.parseObject(pricingRuleJson, pricingRule.class);
    	BigDecimal commodityPriceUs = commodityDTO.getCommodityPriceUs();
    	//销售利润  //如:30% ---> 0.3
    	BigDecimal salePercentum = operPercentum(pricingRule.getSaleProfit(), commodityPriceUs);
    	salePercentum=multiply(commodityPriceUs, salePercentum); 
    	//物流运费 
    	BigDecimal logisticsPrice = pricingRule.getLogisticsPrice();
    	//平台佣金
    	BigDecimal brokeragePricePercentum = operPercentum(pricingRule.getBrokeragePriceRatio(), commodityPriceUs);
    	brokeragePricePercentum=multiply(commodityPriceUs, brokeragePricePercentum);
    	if(StringUtils.isNotBlank(pricingRule.getBrokeragePriceText())) {
    		brokeragePricePercentum=AmazonBachUpdate.operAdd(pricingRule.getBrokeragePriceText(), brokeragePricePercentum);
    	}
    	//自定义费用
    	BigDecimal itemsTotalPrice=new BigDecimal("0");
    	if(CollectionUtils.isNotEmpty(pricingRule.getItems())) {
    		for (Item item : pricingRule.getItems()) {
    			BigDecimal itemPercentum = operPercentum(item.getItemPriceRatio(), commodityPriceUs);
    			itemPercentum=multiply(commodityPriceUs, itemPercentum);
    			if(StringUtils.isNotBlank(item.getItemPriceText())) {
    				itemPercentum=AmazonBachUpdate.operAdd(item.getItemPriceText(), itemPercentum);
    			}
    			
    			itemsTotalPrice=AmazonBachUpdate.operAdd(itemsTotalPrice.toString(), itemPercentum);
			}
    	}
    	return salePercentum.add(logisticsPrice).add(brokeragePricePercentum).add(itemsTotalPrice);
    }
    
    /**
     * 四舍五入  乘法 向上取7.236--》7.24
     * @param args1
     * @param args2
     * @return
     */
    public static BigDecimal multiply(BigDecimal args1,BigDecimal args2) {
    	return args1.multiply(args2).setScale(2,BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算比率 如:30% ---> 0.3
     * @param val
     * @param price
     * @return
     */
    private static BigDecimal operPercentum(BigDecimal val,BigDecimal price) {
		BigDecimal divide = val.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
		BigDecimal multiply = price.multiply(divide).setScale(2,BigDecimal.ROUND_HALF_UP);
		return multiply;
    }
    
    /**
     * 获取相关的商品信息
     * @param amazonDisposePriceVO
     * @return
     */
    public List<CommodityDTO> getCommodity(AmazonDisposePriceVO amazonDisposePriceVO) {
    	JSONArray array = getListByPLSku(amazonDisposePriceVO.getPlSkus());
    	JSONObject object;
    	List<CommodityDTO> dtos=new LinkedList<>();
    	for (Object o : array) {
            if (o instanceof JSONObject) {
                object = (JSONObject) o;
                CommodityDTO commodityDTO=new CommodityDTO();
                commodityDTO.setCommodityPrice(object.getBigDecimal("commodityPrice"));
                commodityDTO.setCommodityPriceUs(object.getBigDecimal("commodityPriceUs"));
                commodityDTO.setPlSku(object.getString("systemSku"));
                dtos.add(commodityDTO);
            }
        }
    	return dtos;
    }
    
    
    /**
     * 根据品连sku获取商品信息多个sku用,分割
     * @param plSku
     * @return
     */
    public JSONArray getListByPLSku(String[] plSku)   {
    	String commodity = remoteCommodityService.getSystemListSkuBySystemSku(plSku);
    	logger.info("计价模板根据plSKU查询商品返回数据：{}", commodity);
    	String commodityStr = Utils.returnRemoteResultDataString(commodity, "商品服务异常");
    	return  JSONObject.parseArray(commodityStr);
    }
    
    public static void main(String[] args) {
    	//BigDecimal multiply = multiply(new BigDecimal("125"), new BigDecimal("0.958"));
    	//System.out.println(multiply);  //RoundingMode.CEILING
    	
    	BigDecimal bigDecimal=new BigDecimal("119.722");
    	System.out.println(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));;
    	BigDecimal bigDecimal2=new BigDecimal("119.7000000001");
    	System.out.println(bigDecimal2.setScale(2, BigDecimal.ROUND_HALF_UP));;
    	
    	
    
    }
}
