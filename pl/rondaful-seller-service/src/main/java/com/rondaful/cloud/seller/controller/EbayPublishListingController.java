package com.rondaful.cloud.seller.controller;


import com.google.common.collect.Lists;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.seller.dto.EbayHistoryDTO;
import com.rondaful.cloud.seller.dto.EbayPublishListingAPPDTO;
import com.rondaful.cloud.seller.dto.EbayPublishListingDTO;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.service.*;
import com.rondaful.cloud.seller.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ebay刊登数据相关controller
 * 
 * @author songjie
 *
 */
@Api(description = "ebay刊登相关接口")
@RestController
@RequestMapping("/ebay/publish")
public class EbayPublishListingController extends BaseController {

	@Autowired
	private IEbayPublishListingService publishListingService;
	@Autowired
	private GetLoginUserInformationByToken getUserInfo;
	@Autowired
	private IEbayPublishListingOperationLogService operationLogService;
	@Autowired
	private IEbayListingService ebayListingService;
	@Autowired
	private AuthorizationSellerService authorizationSellerService;
    @Autowired
    private EbayRecordAttributeSelectService ebayRecordAttributeSelectService;
	
	@AspectContrLog(descrption = "列表查询",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "列表数据查询", notes = "")
	@PostMapping("/page")
	public Page<EbayPublishListingDTO> find(PublishListingSearchVO vo) throws Exception{
		//用户数据权限
		UserDTO userDTO = getUserInfo.getUserDTO();
		//设置默认分页页数
		if(vo.getPage()==null){
			vo.setPage("1");
		}
		if(vo.getRow()==null){
			vo.setRow("10");
		}
		if(userDTO.getManage()){
			vo.setSeller(userDTO.getUserId().toString());
		}else{
			vo.setSeller(userDTO.getTopUserId().toString());
			vo.setEmpowerIds(this.getEmpowerIds(userDTO.getBinds()));
			if(vo.getEmpowerIds()==null || vo.getEmpowerIds().size()==0){
				return null;
			}
		}
		return publishListingService.find(vo);
	}
	
	@AspectContrLog(descrption = "产品刊登",actionType = SysLogActionType.ADD)
	@ApiOperation(value = "产品刊登", notes = "")
	@PostMapping("/listing")
	public Long listing(@RequestBody PublishListingVO vo)throws Exception {
		return publishListingService.insertPublishListing(vo);
	}
	
	@AspectContrLog(descrption = "刊登产品查看",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "刊登查看", notes = "")
	@GetMapping("/listing/view/{listingId}")
	public PublishListingVO listingViw(@PathVariable Integer listingId) throws Exception {
		return publishListingService.findListingById(listingId);
	}
	
	@AspectContrLog(descrption = "刊登编辑",actionType = SysLogActionType.UDPATE)
	@ApiOperation(value = "刊登编辑", notes = "")
	@PostMapping("/listing/edit")
	public void listingEdit(@RequestBody PublishListingVO vo) throws Exception {
		publishListingService.updateListing(vo);
	}
	
	@AspectContrLog(descrption = "备注编辑",actionType = SysLogActionType.UDPATE)
	@ApiOperation(value = "备注编辑", notes = "")
	@PostMapping("/listing/edit/remarks")
	public void listingEditRemarks(@RequestParam Integer id,@RequestParam String remarks) throws Exception {
		publishListingService.updateListingRemarks(id,remarks);
	}
	
	@AspectContrLog(descrption = "产品下架('草稿'和'平台删除'是删除)",actionType = SysLogActionType.ADD)
	@ApiOperation(value = "产品下架(删除)", notes = "")
	@GetMapping("/listing/del/{listingId}")
	public void listingDel(@PathVariable Integer listingId)throws Exception {
		publishListingService.delListing(listingId);
	}
	
	@AspectContrLog(descrption = "产品复制",actionType = SysLogActionType.ADD)
	@ApiOperation(value = "产品复制", notes = "")
	@GetMapping("/listing/copy/{listingId}")
	public void listingCopy(@PathVariable Integer listingId) throws Exception {
		publishListingService.insertListingCopy(listingId);
	}
	
	@AspectContrLog(descrption = "重刊登(草稿产品上线,下线后的产品重上线)",actionType = SysLogActionType.ADD)
	@ApiOperation(value = "发布上线(草稿产品上线,下线后的产品重上线)", notes = "")
	@GetMapping("/listing/again/{listingId}")
	public void listingAgain(@PathVariable Integer listingId) throws Exception {
		publishListingService.relistItem(listingId);
	}
	
	@AspectContrLog(descrption = "刊登日志查看",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "刊登相关日志查看", notes = "")
	@GetMapping("/listing/logView/{listingId}")
	public List<EbayPublishListingError> logView(@PathVariable Integer listingId) throws Exception {
		return publishListingService.listingErrorView(listingId);
	}
	
	@AspectContrLog(descrption = "费用检测",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "费用检测", notes = "")
	@PostMapping("/listing/verify")
	public Double verfiy(@RequestBody PublishListingVO vo) throws Exception {
		return publishListingService.verify(vo);
	}

	@AspectContrLog(descrption = "重刊登费用检测",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "重刊登费用检测", notes = "")
	@PostMapping("/listing/verify/{listingId}")
	public Double verfiy(@PathVariable Integer listingId) throws Exception {
		return publishListingService.verifyRelist(listingId);
	}
	
	@AspectContrLog(descrption = "用户操作日志",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "操作日志", notes = "")
	@GetMapping("/listing/operation/log/{listingId}")
	public List<EbayPublishListingOperationLog> operationLog(@PathVariable Integer listingId) throws Exception {
		List<EbayPublishListingOperationLog> logList = operationLogService.findOperationLogList(listingId);
		if(logList!=null && logList.size()>0){

			List<Integer> userIds = new ArrayList<>();
			logList.forEach(listing ->{
				if(listing.getOperationUserId()!=null) {
					userIds.add(listing.getOperationUserId().intValue());
				}
			});
			Map<Long,String> map = publishListingService.getUsers(userIds);

			for(EbayPublishListingOperationLog log:logList){
				if(log.getOperationUserId()!=null){
					log.setOperationUser(map.get(log.getOperationUserId()));
				}
			}
		}
		return logList;
	}  
	
	@AspectContrLog(descrption = "app获取列表数据",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "app获取列表数据(只返回上线，下线的数据)", notes = "")
	@PostMapping("/getListingList")
	public Page<EbayPublishListingAPPDTO> getListingByList(PublishListingAppSearchVO vo) throws Exception{
		UserDTO userDTO = getUserInfo.getUserDTO();
		if(userDTO.getManage()){
			vo.setSeller(userDTO.getUserId().toString());
		}else{
			vo.setSeller(userDTO.getTopUserId().toString());
			vo.setEmpowerIds(this.getEmpowerIds(userDTO.getBinds()));
			if(vo.getEmpowerIds()==null || vo.getEmpowerIds().size()==0){
				return null;
			}
		}
		return publishListingService.findToAppByPage(vo);
	}


	@AspectContrLog(descrption = "获取平台发货期",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "获取平台发货期", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "itemIds", value = "刊登商品id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "empowerId", value = "刊登账号id", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "site", value = "站点", required = false)
	})
	@PostMapping("/getDispatchTimeMax")
	public Map<String,Object> getDispatchTimeMax(EbayMaxTimeVO vo){
		return publishListingService.getDispatchTimeMax(vo);
	}


	@AspectContrLog(descrption = "刊登spu历史数据",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "刊登spu历史数据", notes = "")
	@PostMapping("/getEbayHistoryPage")
	public Page<EbayHistoryDTO> getEbayHistoryPage(PublishListingSearchVO vo) throws Exception{
		//用户数据权限
		UserDTO userDTO = getUserInfo.getUserDTO();
		//设置默认分页页数
		if(vo.getPage()==null){
			vo.setPage("1");
		}
		if(vo.getRow()==null){
			vo.setRow("10");
		}
		if(userDTO.getManage()){
			vo.setSeller(userDTO.getUserId().toString());
		}else{
			vo.setSeller(userDTO.getTopUserId().toString());
			vo.setEmpowerIds(this.getEmpowerIds(userDTO.getBinds()));
			if(vo.getEmpowerIds()==null || vo.getEmpowerIds().size()==0){
				return null;
			}
		}
		return publishListingService.getEbayHistoryPage(vo);
	}

	@AspectContrLog(descrption="同步Ebay刊登商品",actionType= SysLogActionType.UDPATE)
	@PostMapping("/syncEbayListingIds")
	@ApiOperation("同步Ebay刊登商品")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "ids", value = "刊登表id list 数据", required = true)
	})
	public Integer syncEbayListingIds(EabyPublishListingVO vo) {
		int countnum =0;
		Integer sellerId = null;
		UserDTO userDTO = getUserInfo.getUserDTO();
		if (userDTO.getManage()) {
			sellerId = userDTO.getUserId();
		} else {
			sellerId = userDTO.getTopUserId();
		}
		for (Integer id:vo.getIds()) {
			if (id == null) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
			}
			EbayPublishListingNew publishListing = publishListingService.getListingById(id);
			if (publishListing == null || StringUtil.isEmpty(publishListing.getItemid())) {
				continue;
			}
			countnum++;
			if (!publishListing.getSellerId().equals(sellerId.longValue())) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "无权限查看该数据");
			}
			try {
				ebayListingService.saveEbayListing(publishListing.getEmpowerId().longValue(), userDTO.getUserId().longValue(), userDTO.getLoginName(), sellerId.toString(), null, publishListing.getItemid());
			} catch (Exception e) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "同步速卖通刊登商品异常");
			}
		}
		return countnum;
	}


	@AspectContrLog(descrption="同步Ebay Listing",actionType= SysLogActionType.QUERY)
	@PostMapping("/syncEbayListing")
	@ApiOperation(value="同步Ebay Listing",notes="")
	public Integer syncEbayListing(){
		UserDTO userDTO = getUserInfo.getUserDTO();
		Long sellerId;
		if(userDTO.getManage()){
			sellerId = userDTO.getUserId().longValue();
		}else{
			sellerId = userDTO.getTopUserId().longValue();
		}
		String key ="ebay:ebay-syncListing"+ sellerId;
		if (redisUtils.exists(key)){
			return 0;
		}else {
			redisUtils.set(key,1,600L);//十分钟不能重复请求
		}
		Integer productCount = 0;
		Empower queryEmpower = new Empower();
		queryEmpower.setPinlianId(sellerId.intValue());
		queryEmpower.setPlatform(1);
		queryEmpower.setStatus(1);
		List<Empower> listEmpower = authorizationSellerService.selectObjectByAccountDataLimit(queryEmpower);
		if(listEmpower!=null && listEmpower.size()>0){
			for(Empower empower:listEmpower){
				Integer count = ebayListingService.getEbayListingList(
						empower.getEmpowerId().longValue(),userDTO.getUserId().longValue(),userDTO.getLoginName(),sellerId.toString());
				productCount = productCount+count;
			}
		}else {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登账号为空");
		}
		return productCount;
	}


	@AspectContrLog(descrption = "ebay刊登数量统计",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "ebay刊登数量统计", notes = "")
	@PostMapping("/getEbaySkuNumber")
	public List<Map<String,Object>> getEbaySkuNumber(){
		return publishListingService.getEbaySkuNumber();
	}


	@AspectContrLog(descrption = "商品库存状态查询",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "商品库存状态查询", notes = "")
	@PostMapping("/getCommodityStatusVOBySku")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "warehouseId", value = "库存id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "sku", value = "sku多个用,隔开", required = true),
			@ApiImplicitParam(name = "platform", value = "查侵权时传，平台，1：eBay，2：Amazon，3：wish，4：AliExpress", dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "site", value = "查侵权时传，站点编码", dataType = "string", paramType = "query")
	})
	public List<CommodityStatusVO> getCommodityStatusVOBySku(String warehouseId, String sku,Integer platform,String site){
		String[] skuArray= sku.split(",");
		List<CommodityStatusVO> listVo = Lists.newArrayList();
		for (String plsku:skuArray){
			CommodityStatusVO vo = publishListingService.getCommodityStatusVOBySku(warehouseId, plsku,platform,site);
			listVo.add(vo);
		}
		return listVo;
	}

    @ApiOperation(value = "刊登商品属性历史刊登记录匹配字段值", notes = "")
    @PostMapping("/getEbayRecordAttributeSelectByPublish")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "categoryId", value = "分类id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "site", value = "分类id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "plSpu", value = "品连spu", required = true)
    })
    public List<EbayRecordAttributeSelect> getEbayRecordAttributeSelectByPublish(Long categoryId, String site, String plSpu){
        if (categoryId==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品分类不能为空");
        }
	    if (StringUtils.isBlank(site)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登站点不能为空");
        }
        if (StringUtils.isBlank(plSpu)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连spu不能为空");
        }
        return ebayRecordAttributeSelectService.getEbayRecordAttributeSelectByPublish(categoryId,site,plSpu);
    }


	@ApiOperation(value = "获取平台sku图片", notes = "")
	@PostMapping("/getListingVariantByItemIdPlatformSku")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "itemId", value = "刊登商品itemId", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "platformSku", value = "平台sku", required = true)
	})
	public List<String> getListingVariantByItemIdPlatformSku(String itemId, String platformSku){

		if (StringUtils.isBlank(itemId)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登商品itemId不能为空");
		}
		if (StringUtils.isBlank(platformSku)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台sku不能为空");
		}
		List<String> imgList = new ArrayList<>();
		List<EbayPublishListingVariant> listVariant = publishListingService.getListingVariantByItemIdPlatformSku(itemId,platformSku);
		if(listVariant!=null && listVariant.size()>0){
			listVariant.forEach(variant->{
				if(StringUtils.isNotEmpty(variant.getPicture())){
					imgList.add(variant.getPicture());
				}
			});
		}
		return imgList;
	}


}
