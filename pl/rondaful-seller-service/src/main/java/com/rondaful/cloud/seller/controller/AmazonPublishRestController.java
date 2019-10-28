package com.rondaful.cloud.seller.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.enums.PlatformEnum;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.IAliexpressPublishListingService;
import com.rondaful.cloud.seller.service.IEbayPublishListingService;
import com.rondaful.cloud.seller.vo.PublishListingParamsVO;
import com.rondaful.cloud.seller.vo.ResultPublishListingVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


/**
 * 亚马逊对外提供接口
 * @author 
 *
 */
@RestController
@RequestMapping("/amazon/rest")
@Api(description = "亚马逊对外提供接口-dingshulin")
public class AmazonPublishRestController {

	private final Logger logger = LoggerFactory.getLogger(AmazonPublishRestController.class);
	
	@Autowired
	AmazonPublishListingService amazonPublishListingService;
	@Autowired
	private IAliexpressPublishListingService aliexpressPublishListingService;
	@Autowired
	private IEbayPublishListingService ebayPublishListingService;
	
	@ApiOperation(value="根据platformSku和站点查询",notes="根据platformSku和站点查询")
    @PostMapping("/getByplatformSkuAndSite")
    public List<ResultPublishListingVO> getByplatformSkuAndSite(@RequestBody PublishListingParamsVO params){
		logger.info("getByplatformSkuAndSite订单传入相关参数{}",JSON.toJSONString(params));
		 
    	List<ResultPublishListingVO> resultListingVOs=new ArrayList<>(); 
    	
    	if(CollectionUtils.isEmpty(params.getPlatformSku()) || params.getEmpowerId() == null) {
    		logger.error("PlatformSku or EmpowerId is null {}",JSON.toJSONString(params));
    		return resultListingVOs;
    	}
    	if(PlatformEnum.AMAZON.getCode().equals(params.getType())) {
    		List<AmazonPublishListing>  publishListing= amazonPublishListingService.getByplatformSkuAndSite(params.getPlatformSku(),params.getEmpowerId());
    		if(CollectionUtils.isEmpty(publishListing)) {
    			return resultListingVOs;
    		}
    		publishListing.forEach(listing -> {
    			ResultPublishListingVO listingVO=new ResultPublishListingVO();
    			listingVO.setLogisticsCode(listing.getLogisticsCode());
    			listingVO.setLogisticsType(listing.getLogisticsType());
    			listingVO.setWarehouseId(listing.getWarehouseId());
    			resultListingVOs.add(listingVO);
    		});
    		return resultListingVOs;
    	}
    	if(PlatformEnum.ALIEXPRESS.getCode().equals(params.getType())) {
			return aliexpressPublishListingService.getAliexpressResultPublishListingVO(params.getPlatformSku(),params.getEmpowerId());
    	}
    	if(PlatformEnum.EBAY.getCode().equals(params.getType())) {
			return ebayPublishListingService.getEbayResultPublishListingVO(params.getEmpowerId(),params.getPlatformSku());
    	}
    	
    	 return resultListingVOs;
    }
	
}
