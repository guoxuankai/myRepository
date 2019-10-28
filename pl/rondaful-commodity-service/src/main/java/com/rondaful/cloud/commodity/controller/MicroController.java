package com.rondaful.cloud.commodity.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.SellerSkuMap;
import com.rondaful.cloud.commodity.entity.SiteCategory;
import com.rondaful.cloud.commodity.entity.SkuWarehouseInfo;
import com.rondaful.cloud.commodity.entity.SpuTortRecord;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.mapper.CommodityBelongSellerMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.mapper.SkuTortRecordMapper;
import com.rondaful.cloud.commodity.mapper.SkuWarehouseInfoMapper;
import com.rondaful.cloud.commodity.mapper.SystemSpuMapper;
import com.rondaful.cloud.commodity.service.ICommodityService;
import com.rondaful.cloud.commodity.service.ISellerSkuMapService;
import com.rondaful.cloud.commodity.vo.CodeAndValueVo;
import com.rondaful.cloud.commodity.vo.QuerySkuBelongSellerVo;
import com.rondaful.cloud.commodity.vo.QuerySkuMapForOrderVo;
import com.rondaful.cloud.commodity.vo.QueryTortNumVo;
import com.rondaful.cloud.commodity.vo.SkuInventoryVo;
import com.rondaful.cloud.commodity.vo.SkuMapAddVo;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;

import io.swagger.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 微服务内部API
 */
@Api(description="微服务内部API")
@RestController
public class MicroController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(MicroController.class);

    @Autowired
    private CommoditySpecMapper commoditySpecMapper;

    @Autowired
    private ICommodityService commodityService;
    
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
    
    @Autowired
    private SkuTortRecordMapper skuTortRecordMapper;
    
    @Autowired
    private SystemSpuMapper systemSpuMapper;
    
    @Autowired
    private CommodityBelongSellerMapper commodityBelongSellerMapper;
    
    @Autowired
    private ISellerSkuMapService sellerSkuMapService;
    
    //@Autowired
    //private  SkuWarehouseInfoMapper skuWarehouseInfoMapper;


    @PostMapping("micro/commodity/getSystemSkuByUserSku")
    @ApiOperation(value = "根据供应商sku获取sku信息列表", notes = "", response = CommoditySpec.class)
    public List<CommoditySpec> getSKU(@ApiParam(name = "skus", value = "用户sku值数组，多个以逗号隔开传递", required = true) @RequestBody List<String> ids) {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        List<CommoditySpec> list = commoditySpecMapper.getSystemSkuByUserSku(ids);
        commodityService.constructionSupplierForSpec(list);
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
        commodityService.inventoryForDetail(list);
        return list;
    }


    @PostMapping("micro/commodity/getSystemListSkuBySystemSku")
    @ApiOperation(value = "根据品连sku获取sku信息列表", notes = "", response = CommoditySpec.class)
    public List<CommoditySpec> getSystemSKUList(@ApiParam(name = "skus", value = "系统sku值数组，多个以逗号隔开传递", required = true) @RequestBody List<String> ids) {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        List<CommoditySpec> list = commoditySpecMapper.getSystemSkuBySystemSku(ids);
        commodityService.constructionSupplierForSpec(list);
        commodityService.inventoryForDetail(list);
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
        return list;
    }


    @PostMapping("micro/commodity/getCommodityListBySPU")
    @ApiOperation(value = "根据spu值获取商品base信息列表", notes = "")
    public List getCommodityForSPU(@ApiParam(name = "spus", value = "spu值数组，多个以逗号隔开传递", required = true) @RequestBody List<String> ids) {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        return commodityService.selectBySPUS(ids);
    }


    @PostMapping("micro/commodity/getCommodityListBySystemSKU")
    @ApiOperation(value = "根据品连sku获取商品base信息列表", notes = "")
    public List getCommodityListBySystemSKU(@ApiParam(name = "skus", value = "SKU值数组，多个以逗号隔开传递", required = true) @RequestBody List<String> ids) {
        if (ids.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        QuerySkuBelongSellerVo vo=new QuerySkuBelongSellerVo();
        vo.setSystemSkuList(ids);
        return commodityService.selectBySKUS(vo);
    }
    
    @PostMapping("micro/commodity/getCommodityBySkuList")
    @ApiOperation(value = "根据卖家ID、品连sku数组获取可售商品信息", notes = "")
    public List getCommodityBySkuList( @RequestBody QuerySkuBelongSellerVo vo) {
        if (vo.getSystemSkuList().isEmpty() || vo.getSellerId()==null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        return commodityService.selectBySKUS(vo);
    }


    @GetMapping("micro/commodity/getSkuList")
    @ApiOperation(value = "获取sku商品列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "category_level_1", value = "一级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_2", value = "二级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_level_3", value = "三级分类", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "systemSku", value = "系统sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "supplierSku", value = "供应商sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "系统spu", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "vendibilityPlatform", value = "可售平台", dataType = "string", paramType = "query") })
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page getSkuList(String page, String row, Long category_level_1, Long category_level_2, Long category_level_3,
                     String commodityName, String systemSku, String supplierSku, String SPU,String vendibilityPlatform) {
        
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("page", page);
        param.put("row", row);
        param.put("categoryLevel1", category_level_1);
        param.put("categoryLevel2", category_level_2);
        param.put("categoryLevel3", category_level_3);
        param.put("systemSku", systemSku);
        param.put("supplierSku", supplierSku);
        param.put("SPU", SPU);
        param.put("vendibilityPlatform", vendibilityPlatform);
        //判断是中文还是英文商品名称搜索
        if (isEnNameSearch()) {
        	param.put("commodityNameEn", commodityName);
		}else {
			param.put("commodityNameCn", commodityName);
		}
        
        Long belongSellerId = 0L;
        //判断是否登录
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
        		if (user.getUserid() != null && user.getTopUserId() != null) {
        			List<String> limitIds=null;
        			if (user.getTopUserId() == 0) {//主账号
        				belongSellerId=Long.parseLong(String.valueOf(user.getUserid()));
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getUserid())));
        			}else {
        				belongSellerId=Long.parseLong(String.valueOf(user.getTopUserId()));
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getTopUserId())));
    				}
                    if (limitIds != null && limitIds.size()>0) {
                    	param.put("limitIds", limitIds);
            		}
                    
                    param.put("belongSellerId", belongSellerId);
				}
        	}
		}else {//如果没登录，指定卖家的不可搜索
			List<String> belongSellerCommodityIds=commodityBelongSellerMapper.selectAllCommodityId(null);
			if (belongSellerCommodityIds != null && belongSellerCommodityIds.size()>0) {
				param.put("limitIds", belongSellerCommodityIds);
			}
		}
        
    	Page p = commodityService.selectSkuList(param);
        return p;
    }

    @PostMapping("micro/commodity/saveOrUpdateSpuCategory")
    @ApiOperation(value = "新增或更新SPU分类映射", notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "spu", value = "商品spu码", dataType = "string", paramType = "query", required = true),
        @ApiImplicitParam(name = "platform", value = "平台名称", dataType = "string", paramType = "query", required = true),
        @ApiImplicitParam(name = "siteCode", value = "站点编码", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "platCategoryId", value = "平台商品分类ID", dataType = "Long", paramType = "query"),
        @ApiImplicitParam(name = "categoryPath", value = "分类路径", dataType = "string", paramType = "query")})
    public void saveOrUpdateSpuCategory(String spu,String platform,String siteCode,Long platCategoryId,String categoryPath){
    	if (StringUtils.isBlank(spu)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "spu不能为空");
        if (StringUtils.isBlank(platform)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台名称不能为空");
        if (platCategoryId==null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台商品分类ID不能为空");
        commodityService.saveOrUpdateSpuCategory(spu, platform, siteCode, platCategoryId,categoryPath);
    }
    
    
    @GetMapping("micro/commodity/querySpuSiteCategory")
    @ApiOperation(value = "查询SPU的分类映射", notes = "")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "spu", value = "商品spu码", dataType = "string", paramType = "query", required = true),
        @ApiImplicitParam(name = "platform", value = "平台名称", dataType = "string", paramType = "query", required = true),
        @ApiImplicitParam(name = "siteCode", value = "站点编码", dataType = "string", paramType = "query") })
    @RequestRequire(require = "spu, platform", parameter = String.class)
    public SiteCategory querySpuSiteCategory(String spu,String platform,String siteCode){
    	if (StringUtils.isBlank(spu)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "spuId不能为空");
        if (StringUtils.isBlank(platform)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台名称不能为空");
        return commodityService.querySpuSiteCategory(spu, platform, siteCode);
    }
    
    
    @ApiImplicitParams({
        @ApiImplicitParam(name = "platform", value = "查侵权时传，平台，1：eBay，2：Amazon，3：wish，4：AliExpress", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "siteCode", value = "查侵权时传，站点编码", dataType = "string", paramType = "query") })
    @GetMapping("micro/commodity/getCommoditySpecBySku")
    @ApiOperation(value = "根据系统sku码或者供应商sku码查询sku信息", notes = "")
    public CommoditySpec getCommoditySpecBySku(@RequestParam("sku")String sku,Integer platform,String siteCode) {
    	return commodityService.getCommoditySpecBySku(sku,platform,siteCode);
    }
    
    
    @PostMapping("micro/commodity/updateSkuSaleNum")
    @ApiOperation(value = "更新sku已售数量", notes = "")
    public void updateSkuSaleNum(@RequestBody List<CodeAndValueVo> data) {
        if (data.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        updateSkuNum(data,1);
    }
    
    @PostMapping("micro/commodity/updateSkuPublishNum")
    @ApiOperation(value = "更新sku刊登数量", notes = "")
    public void updateSkuPublishNum(@RequestBody List<CodeAndValueVo> data) {
        if (data.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        updateSkuNum(data,2);
    }
    
    /**
     * @Description:
     * @param data
     * @param type 1:已售数量，2：刊登数量
     * @return void
     * @author:范津
     */
    private void updateSkuNum(List<CodeAndValueVo> data,int type) {
    	commodityService.updateSpecNum(data,type);
    }
    
    @PostMapping("micro/commodity/getSupplierSkuBySystemSKU")
    @ApiOperation(value = "根据品连SKU值获取供应商SKU", notes = "")
    public Map<String, String> getSupplierSkuBySystemSKU(@ApiParam(name = "skus", value = "品连SKU值数组，多个以逗号隔开传递", required = true) @RequestBody List<String> skus) {
        if (skus.isEmpty()) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        Map<String, String> map=new HashMap<String,String>();
        for (String sku : skus) {
			String supplieSku=commoditySpecMapper.getSupplierSkuBySystemSku(sku);
			map.put(sku, supplieSku);
		}
        return map;
    }
    
    
    @PostMapping("micro/commodity/getTortNum")
    @ApiOperation(value = "获取侵权记录数量", notes = "")
    public int getTortNum(@RequestBody QueryTortNumVo vo) {
    	if (StringUtils.isBlank(vo.getSpu()) && StringUtils.isBlank(vo.getSystemSku())) {
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
		}
    	if (StringUtils.isNotBlank(vo.getSystemSku())) {
    		String systemSpu=systemSpuMapper.getSpuBySku(vo.getSystemSku());
    		if (StringUtils.isBlank(systemSpu)) {
    			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"sku对应的spu不存在");
			}
    		vo.setSpu(systemSpu);
		}
    	
    	SpuTortRecord param=new SpuTortRecord();
    	param.setPlatform(vo.getPlatform());
    	param.setSiteCode(vo.getSiteCode());
    	param.setSystemSpu(vo.getSpu());
        return skuTortRecordMapper.getTortNum(param);
    }
    
    @GetMapping("micro/commodity/getSkuListByPage")
    @ApiOperation(value = "分页获取sku列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "systemSku", value = "品连sku", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态：-1：待提交，0：审核中，1：待上架，2：已拒绝，3：已上架", dataType = "Long", paramType = "query") })
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page getSkuListForSupplier(String page, String row,String systemSku,Integer state) {
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("page", page);
        param.put("row", row);
        param.put("systemSku", systemSku);
        param.put("state", state);
    	Page p = commodityService.getSkuListByPage(param);
        return p;
    }
    
    
    @GetMapping("micro/commodity/getSkuSellerList")
    @ApiOperation(value = "获取sku指定可售的卖家ID", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true)
            })
    public List<String> getSkuSellerList(String page, String row,String sku) {
        return commodityBelongSellerMapper.getSellerIdBySku(sku);
    }
    
    @PostMapping("micro/commodity/addSkuMap")
    @ApiOperation(value = "批量新增平台sku映射", notes = "")
    public void addSkuMap(@RequestBody List<SkuMapAddVo> voList) {
    	log.error("卖家端调用新增平台sku映射入参===》{}",JSON.toJSON(voList).toString());
    	sellerSkuMapService.addSkuMapForSeller(voList);
    }
    
    @GetMapping("micro/commodity/getSkuMapByPlatformSku")
    @ApiOperation(value = "根据平台sku获取sku映射信息", notes = "")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "platform", value = "平台：amazon, eBay, wish, aliexpress,other", dataType = "string", paramType = "query",required=true),
        @ApiImplicitParam(name = "platformSku", value = "平台sku", dataType = "string", paramType = "query",required=true),
        @ApiImplicitParam(name = "authorizationId", value = "店铺id", dataType = "string", paramType = "query",required=true) })
    public JSONObject getSkuMapByPlatformSku(@RequestParam("platform") String platform,
    		@RequestParam("platformSku") String platformSku,@RequestParam("authorizationId") String authorizationId) {
    	log.error("根据平台sku获取sku映射信息入参===》{}",platform+","+platformSku+","+authorizationId);
        return sellerSkuMapService.getSkuMapByPlatformSku(platform,platformSku,authorizationId,true);
    }
    
    @GetMapping("micro/commodity/getSkuMapByPlatformSkuForOrder")
    @ApiOperation(value = "查询所有状态的平台sku映射", notes = "")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "platform", value = "平台：amazon, eBay, wish, aliexpress,other", dataType = "string", paramType = "query",required=true),
        @ApiImplicitParam(name = "platformSku", value = "平台sku", dataType = "string", paramType = "query",required=true),
        @ApiImplicitParam(name = "authorizationId", value = "店铺id", dataType = "string", paramType = "query",required=true) })
    public JSONObject getSkuMapByPlatformSkuForOrder(@RequestParam("platform") String platform,
    		@RequestParam("platformSku") String platformSku,@RequestParam("authorizationId") String authorizationId) {
    	log.error("查询所有状态的平台sku映射，入参===》{}",platform+","+platformSku+","+authorizationId);
        return sellerSkuMapService.getSkuMapByPlatformSku(platform,platformSku,authorizationId,false);
    }
    
    
    @PostMapping("micro/commodity/getSkuMapForOrder")
    @ApiOperation(value = "订单获取平台sku映射", notes = "")
    public JSONArray getSkuMapForOrder(@RequestBody List<QuerySkuMapForOrderVo> voList) {
    	log.error("订单获取平台sku映射入参===》{}",JSON.toJSON(voList).toString());
    	return sellerSkuMapService.getSkuMapForOrder(voList);
    }
    
    
    /**
     * @Description:仓库服务定制，一个供应商sku对应多个品连sku的业务
     * @param supplierSku
     * @return
     * @author:范津
     */
    @GetMapping("micro/commodity/getSkuListBySupplierSku")
    @ApiOperation(value = "根据供应商sku查询sku数组，一对多", notes = "")
    public List<CommoditySpec> getSkuListBySupplierSku(@RequestParam("supplierSku")String supplierSku) {
    	return commodityService.getSkuListBySupplierSku(supplierSku);
    }
    
}
