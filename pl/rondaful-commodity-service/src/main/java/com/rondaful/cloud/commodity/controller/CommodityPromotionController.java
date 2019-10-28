package com.rondaful.cloud.commodity.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.CommodityPromotion;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.enums.ResponseCodeEnum;
import com.rondaful.cloud.commodity.mapper.CommodityBaseMapper;
import com.rondaful.cloud.commodity.mapper.CommodityBelongSellerMapper;
import com.rondaful.cloud.commodity.mapper.CommodityPromotionMapper;
import com.rondaful.cloud.commodity.mapper.CommoditySpecMapper;
import com.rondaful.cloud.commodity.remote.RemoteSupplierService;
import com.rondaful.cloud.commodity.service.ICommodityPromotionService;
import com.rondaful.cloud.commodity.service.ICommodityService;
import com.rondaful.cloud.commodity.vo.CategoryCommodityVO;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author lz
 * @ClassName: CommodityPromotionController
 * @Description: 卖家产品推广控制层
 * @date 2018年12月8日 下午2:25:29
 */
@Api(description = "卖家产品推广控制层")
@RequestMapping("/promotion")
@RestController

public class CommodityPromotionController extends BaseController{
    private final Logger logger = LoggerFactory.getLogger(CommodityPromotionController.class);

    @Autowired
    private ICommodityPromotionService commodityPromotionService;

    @Autowired
    private CommodityPromotionMapper commodityPromotionMapper;

    @Autowired
    private ICommodityService commodityService;

    @Autowired
    private CommodityBaseMapper commodityBaseMapper;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Autowired
    private CommoditySpecMapper commoditySpecMapper;

    @Autowired
    private RemoteSupplierService remoteSupplierService;
    
    @Autowired
    private CommodityBelongSellerMapper commodityBelongSellerMapper;
    
    
    

    @ApiOperation(value = "添加商品到产品推广")
    @PostMapping("/addPromotion")
    @AspectContrLog(descrption = "添加商品", actionType = SysLogActionType.ADD)
    public void addPromotion(@RequestParam("saleType") Integer saleType,@RequestParam("commodityIds") ArrayList<Integer> commodityIds){
    	List<String> failList=new ArrayList<String>();
        //循环添加商品id和销售类型
        for (int i = 0; i < commodityIds.size(); i++) {
        	List<CommodityPromotion> list = new ArrayList<CommodityPromotion>();
        	CommodityPromotion spp = new CommodityPromotion();
        	long commodityId=Long.parseLong(commodityIds.get(i)+"");
            spp.setSaleType(saleType);
            spp.setCommodityId(commodityId);
            String loginUserName = getLoginUserInformationByToken.getUserInfo().getUser().getUsername();
            logger.info("获取到的userName为" + loginUserName);
            spp.setCreateUserName(loginUserName);
            list.add(spp);
            try {
            	commodityPromotionService.addProductPromotion(list);
			} catch (DuplicateKeyException e) {
				CommodityBase commodityBase = commodityBaseMapper.selectCommodityDetailsById(commodityId);
				failList.add(commodityBase.getSPU());
				continue;
			}catch (Exception e1) {
				logger.error("添加商品到产品推广异常",e1);
				continue;
			}
        }
        if (failList.size()>0) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "重复添加商品SPU:" + failList);
		}
    }



    @ApiOperation("多条件搜索")
    @PostMapping("/searchPromotion")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "显示行数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "commodityName", value = "商品名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "商品SPU", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "saleType", value = "销售类型（1精品，2热卖，3新品）", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "category_level_1", value = "一级分类", dataType = "string", paramType = "query") })
    public Page searchPromotion(@RequestParam("page") String page, @RequestParam("row") String row, Integer saleType,
                                String SPU, String commodityName, Long category_level_1) {

    	
    	Map<String,Object> params = new HashMap<>();
    	
    	List<String> limitIds=null;
    	//判断是否登录
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
        		if (user.getUserid() != null && user.getTopUserId() != null) {
        			if (user.getTopUserId() == 0) {//主账号
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getUserid())));
        			}else {
        				limitIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getTopUserId())));
    				}
				}
        	}
		}else {//如果没登录，指定卖家的不可搜索
			limitIds=commodityBelongSellerMapper.selectAllCommodityId(null);
		}
    	
        params.put("saleType", saleType);
        List<String> ids = commodityPromotionMapper.searchPromotion(params);
        if (ids.size() <= 0) {
           return null;
        }
        
        //取出ids和limitIds的不同值，即过滤对指定非当前卖家的
        if (limitIds != null) {
        	List<String> queryIds=new ArrayList<String>();
			for (String limitId : limitIds) {
				for (String id : ids) {
					if (!id.equals(limitId)) {
						queryIds.add(id);
					}
				}
			}
			if (queryIds.size()>0) {
				params.put("ids", queryIds);
			}
		}else {
			params.put("ids", ids);
		}
        
        
        params.put("page", page);
        params.put("row", row);
        //查询上架商品
        params.put("isUp",true);
        if (StringUtils.isNotBlank(commodityName)) {
        	if (isEnNameSearch()) {
        		params.put("commodityNameEn", commodityName);
			}else {
				params.put("commodityNameCn", commodityName);
			}
        }
        params.put("promotion", 1);
        params.put("SPU", SPU);
        params.put("sortKey", "t10.create_date");
        params.put("sort", "DESC");
        params.put("category_level_1", category_level_1);
        Page p = commodityService.selectCommodityListBySpec(params);
        return p;
    }

    
	@ApiOperation("根据推广id查看详情信息,查看按钮")
	@GetMapping("/getPromotion")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "promotionId", value = "商品推广id", dataType = "int", paramType = "query", required = true) })
	public Page getPromotion(Integer promotionId) {
		CommodityPromotion commodityPromotion = commodityPromotionService.getPromotion(promotionId);
		List<Long> ids = new ArrayList<Long>();
		Long commodityId = commodityPromotion.getCommodityId();
		Integer saleType = commodityPromotion.getSaleType();
		ids.add(commodityId);
		Page p = commodityService.selectCommodityListBySpec(new HashMap() {
			{
				this.put("page", "1");
				this.put("row", "1");
				this.put("ids", ids);
				this.put("promotion", 1);
				this.put("saleType", saleType);
				this.put("sortKey", "t10.create_date");
				this.put("sort", "DESC");
			}
		});
		return p;
	}
	
	
    @ApiImplicitParams({
            @ApiImplicitParam(name = "promotionIds", value = "商品推广id", dataType = "Integer", paramType = "query", required = true) })
    @ApiOperation("根据推广id删除数据")
    @PostMapping("/deleteByCommodity")
    @AspectContrLog(descrption = "根据推广id删除产品推广信息", actionType = SysLogActionType.DELETE)
    public Integer deleteBySpu(@RequestParam("promotionIds") List<Integer> promotionIds) {
        Integer delete = commodityPromotionService.deleteByPromotionId(promotionIds);
        if (delete <= 0) {
            logger.error("删除推广信息失败");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除推广信息失败");
        }
        return delete;
    }

    
	@ApiOperation("卖家首页分类楼层")
	@GetMapping("/getCommodityByCategoryId")
	public Page getCommodityByCategoryId(@RequestParam("page") String page, @RequestParam("row") String row,
			@RequestParam("categoryIds") List<Integer> categoryIds) {
		//返回结果
		List<CategoryCommodityVO> commoditys = new ArrayList<CategoryCommodityVO>();
		
		Long belongSellerId = 0L;
		List<String> limitCommodityIds=null;
		//判断是否登录
        UserAll userAll=getLoginUserInformationByToken.getUserInfo();
        if (userAll!=null) {
        	UserCommon user = userAll.getUser();
        	if (UserEnum.platformType.SELLER.getPlatformType().equals(user.getPlatformType())) {//卖家平台
        		if (user.getUserid() != null && user.getTopUserId() != null) {
        			if (user.getTopUserId() == 0) {//主账号
        				belongSellerId=Long.parseLong(String.valueOf(user.getUserid()));
        				limitCommodityIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getUserid())));
        			}else {
        				belongSellerId=Long.parseLong(String.valueOf(user.getTopUserId()));
        				limitCommodityIds=commodityBelongSellerMapper.selectCommodityIdBySellerId(Long.parseLong(String.valueOf(user.getTopUserId())));
    				}
				}
        	}
		}else {//如果没登录，指定卖家的不可搜索
			limitCommodityIds=commodityBelongSellerMapper.selectAllCommodityId(null);
		}
        
        List<CommodityBase> baseAll=new ArrayList<CommodityBase>();
        Map map = new HashMap();
        for (Integer categoryid : categoryIds) {
        	Page.builder(page,row);
        	map.put("isUp", true);
			map.put("category_level_1", categoryid);
			map.put("belongSellerId", belongSellerId);
			if (limitCommodityIds != null && limitCommodityIds.size()>0) {
	        	map.put("limitIds", limitCommodityIds);
			}
			List<CommodityBase> commodityBases = commodityBaseMapper.selectCommodityListBySpec(map);
			baseAll.addAll(commodityBases);
			map.clear();
		}
        
        List<CommoditySpec> listspcAll=new ArrayList<CommoditySpec>();
        if (baseAll.size()>0) {
        	Map param = new HashMap();
        	param.put("list", baseAll);
			listspcAll = commoditySpecMapper.selectCommoditySpecByCommodityId(param);
			//构造库存
			constructionSku(listspcAll);
		}
        for (CommodityBase cb : baseAll) {
			int totalNum = 0;
			cb.setCommoditySpecList(new ArrayList<>());
			for (CommoditySpec cs : listspcAll) {
				// sku与spu匹配
				if (cs.getCommodityId().intValue() == cb.getId().intValue()) {
					cb.getCommoditySpecList().add(cs);
					
					totalNum += cs.getInventory();
					//totalNum += cs.getInventoryPlay();
				}
			}
			cb.setInventory(totalNum);
			//设置中文名称
			cb.setCommodityNameCn(cb.getCommoditySpecList().get(0).getCommodityNameCn());
			//设置英文名称
			cb.setCommodityNameEn(cb.getCommoditySpecList().get(0).getCommodityNameEn());
            //设置商品价格
            cb.setCommodityPrice(cb.getCommoditySpecList().get(0).getCommodityPrice());
			//设置美元
            cb.setCommodityPriceUs(cb.getCommoditySpecList().get(0).getCommodityPriceUs());
		}
        
        CategoryCommodityVO categoryCommodityVO = null;
        List<CommodityBase> baseCategory = null;
        for (Integer categoryid : categoryIds) {
        	categoryCommodityVO = new CategoryCommodityVO();
			categoryCommodityVO.setCategoryId(categoryid);
			baseCategory=new ArrayList<CommodityBase>();
			for (CommodityBase base : baseAll) {
				if (categoryid.equals(base.getCategoryLevel1().intValue())) {
					baseCategory.add(base);
				}
			}
			// baseCategory排序
			Collections.sort(baseCategory, new Comparator<CommodityBase>() {
				@Override
				public int compare(CommodityBase base1, CommodityBase base2) {
					return base2.getInventory()-base1.getInventory();
				}
			});
			categoryCommodityVO.setCommodityBases(baseCategory);
			commoditys.add(categoryCommodityVO);
        }
        
		PageInfo pageInfo = new PageInfo(commoditys);
		return new Page(pageInfo);
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
                    	int inventoryTotal=0;
                        for (int i = 0; i < ja.size(); i++) {
                        	String pinlianSku=(JSONObject.fromObject(ja.get(i))).getString("pinlianSku");
                            String inventory = (JSONObject.fromObject(ja.get(i))).getString("localAvailableQty");
                            if (StringUtils.isNotBlank(pinlianSku) && StringUtils.isNotBlank(inventory) && pinlianSku.equals(cs.getSystemSku())) {
                            	//int num=Integer.parseInt(inventory)<0 ? 0 : Integer.parseInt(inventory);
                            	int num=cs.getInventoryPlay();
                                inventoryTotal += num;
                            }
                        }
                        cs.setInventory(inventoryTotal);
                    }
                }
            }
        } catch (Exception e) {
        	logger.error("获取库存信息异常",e);
        }
    }
	
}
