package com.rondaful.cloud.seller.controller;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.seller.entity.EbayCountry;
import com.rondaful.cloud.seller.entity.EbayProductCategory;
import com.rondaful.cloud.seller.entity.EbaySite;
import com.rondaful.cloud.seller.entity.EbaySiteDetail;
import com.rondaful.cloud.seller.entity.ebay.store.Store;
import com.rondaful.cloud.seller.service.IEbayBaseService;
import com.rondaful.cloud.seller.service.IEbayPublishListingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 基础数据controller
 * @author songjie
 *
 */
@Api(description = "ebay基础接口")
@RestController
@RequestMapping("/ebay/data")
public class EbayBaseController extends BaseController {

	@Autowired
	private IEbayBaseService ebayBaseService;
	

	@Autowired
	private IEbayPublishListingService publishListingService;
	
	@AspectContrLog(descrption = "获取站点数据",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "刊登站点", notes = "")
	@PostMapping("/site")
	public List<EbaySite> site(EbaySite site) throws Exception {
		site.setStatus(0);
		return ebayBaseService.findSiteByValue(site);
	}
	
	@AspectContrLog(descrption = "获取站点属性值",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "站点属性值", notes = "")
	@GetMapping("/site/defaultValue/{site}")
	public EbaySiteDetail defaultValue(@PathVariable String site)throws Exception {
		EbaySiteDetail obj = new EbaySiteDetail();
		obj.setSite(site);
		List<EbaySiteDetail> findSiteDetail = ebayBaseService.findSiteDetail(obj);
		if (CollectionUtils.isNotEmpty(findSiteDetail)){
			return findSiteDetail.get(0);
		}
		return null;
	}
	
	@AspectContrLog(descrption = "获取国家",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "按洲区分国家 (0=所有, 1=亚洲，2=非洲，3=中美洲和加勒比海，4=欧洲，5=中东，6=北美洲，7=大洋洲，8=东南亚 ,9=南美洲)", notes = "")
	@GetMapping("/country/{type}")
	public List<EbayCountry> country(@PathVariable int type) throws Exception {
		EbayCountry country = new EbayCountry();
		country.setStatus(0);
		if (type !=0){
			country.setType(type);
		}
		return ebayBaseService.findCountryByValue(country);
	}
    
	@AspectContrLog(descrption = "获取级联分类",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "产品分类级联取值", notes = "")
	@PostMapping("/category")
	public List<EbayProductCategory> category(EbayProductCategory category) throws Exception {
		return ebayBaseService.findCategoryByValue(category);
	}

	@AspectContrLog(descrption = "获取分类特征与属性",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "产品分类的详细属性", notes = "")
	@PostMapping("/attributeList")
	public Map<String, Object> attribute(EbayProductCategory category) throws Exception {
		return ebayBaseService.getCategoryAttributeAndFeatures(category);
	}
	
	@AspectContrLog(descrption = "产品分类值加载",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "产品分类值加载", notes = "")
	@GetMapping("/load/{categoryId}/{site}")
	public String load(@PathVariable String categoryId,@PathVariable String site)throws Exception {
		return ebayBaseService.loadCateory(categoryId, site);
	}
	
	@AspectContrLog(descrption = "产品分类模糊",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "分类模糊查询", notes = "")
	@PostMapping("/query/fuzzyQuery")
	public List<Map<String,String>> query(String site,String empowerId , String title)throws Exception {
		if(StringUtils.isEmpty(empowerId)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登账号不能为空");
		}
		if(StringUtils.isEmpty(site)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "站点不能为空");
		}
		if(StringUtils.isEmpty(title)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "标题不能为空");
		}
		return ebayBaseService.fuzzyQuery(empowerId,site, title);
	}
	
	@ApiOperation(value = "获取数据", notes = "")
	@GetMapping("/listing/getItem/{itemId}")
	public String getItem(@PathVariable String itemId) throws Exception {
		String url = "https://www.ebay.com/itm/-/";
		//return ebayBaseService.getItem(itemId);
		return url+itemId;
	}
	
	@AspectContrLog(descrption = "同步店铺",actionType = SysLogActionType.ADD)
	@ApiOperation(value = "同步店铺", notes = "")
	@GetMapping("/getStore/{userId}")
	public Store getStore(@PathVariable String userId){
		return ebayBaseService.getStore(userId);
	}
	
	@AspectContrLog(descrption = "获取在线数量",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "获取在线数量", notes = "")
	@PostMapping("/getOnlineCount/{seller}")
	public int getOnlineCount(@PathVariable Long seller) throws Exception{
		return publishListingService.getOnlineCount(seller);
	}
	
}


