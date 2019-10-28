package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.security.UserSession;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.seller.dto.PlatformSkuDTO;
import com.rondaful.cloud.seller.entity.EbayPublishListingVariant;
import com.rondaful.cloud.seller.entity.PublishTemplate;
import com.rondaful.cloud.seller.service.PublishTemplateService;
import com.rondaful.cloud.seller.utils.GeneratePlateformSku;
import com.rondaful.cloud.seller.vo.PublishTemplateSearchVO;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Api(description = "刊登模板")
@RestController
@RequestMapping("publish/template")
public class PublishTemplateController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(PublishTemplateController.class);
	@Autowired
	private PublishTemplateService publishTemplateService;

	@Autowired
	private GetLoginUserInformationByToken getUserInfo;



	@AspectContrLog(descrption = "获取模板列表",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "获取模板列表 PublishTemplateSearchVO", notes = "")
	@PostMapping("/getPublishTemplatePage")
	public Page<PublishTemplate> getPublishTemplatePage(PublishTemplateSearchVO vo){
		try {
			UserCommon user = getUserInfo.getUserInfo().getUser();
			//设置默认分页页数
			if(vo.getPage()==null){
				vo.setPage(1);
			}
			if(vo.getRow()==null){
				vo.setRow(10);
			}
			vo.setPlAccount(user.getUsername());
			Page<PublishTemplate> findAll = publishTemplateService.findPage(vo);
			return findAll;
		} catch (Exception e) {
			logger.error("获取模板列表",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"获取模板列表");
		}
	}

	@AspectContrLog(descrption="模板保存",actionType= SysLogActionType.ADD)
	@PostMapping("/save")
	@ApiOperation(value="模板保存",notes="模板保存")
	public String savePublishTemplate(@RequestBody PublishTemplate publishTemplate)
	{
		UserCommon user = getUserInfo.getUserInfo().getUser();
		if(user== null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}
		publishTemplate.setCreateId(user.getUserid().longValue());
		publishTemplate.setPlAccount(user.getUsername());
		publishTemplateService.savePublishTemplate(publishTemplate);
		return "true";
	}
	@AspectContrLog(descrption="删除模板",actionType= SysLogActionType.DELETE)
	@DeleteMapping("/delete/{id}")
	@ApiOperation("删除模板")
	public String delete(@ApiParam(value = "被删除的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "被删除的id不能为空");
		}
		PublishTemplate publishTemplate = publishTemplateService.getPublishTemplateById(id);
		if (publishTemplate == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不存在");
		}
		UserCommon user = getUserInfo.getUserInfo().getUser();
		if (!publishTemplate.getPlAccount().equalsIgnoreCase(user.getUsername())) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能删除其他用户的数据");
		}
		try {
			publishTemplateService.deletePublishTemplate(id);
		} catch (Exception e) {
			logger.error("删除异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除异常");
		}
		return "true";
	}

	@AspectContrLog(descrption="模板详情",actionType= SysLogActionType.QUERY)
	@PostMapping("/view-details/{id}")
	@ApiOperation("模板详情数据")
	public PublishTemplate viewById(@PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}
		PublishTemplate publishTemplate = publishTemplateService.getPublishTemplateById(id);
		if (publishTemplate == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
		}
		String username = UserSession.getUserBaseUserInfo().getUsername();
		if(!publishTemplate.getPlAccount().equalsIgnoreCase(username))
		{
			if(publishTemplate.getSystemIs()!=null && !publishTemplate.getSystemIs()){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "无权限查看该数据");
			}
		}
		return publishTemplate;
	}


	@AspectContrLog(descrption = "获取模板下拉数据",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "获取模板下拉数据", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "platform", value = "平台 1:amazon 2:eBay 3:wish 4:aliexpress", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "site", value = "站点", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "templateType",  value = "模板类型 1:物流设置2:买家限制3:商品所在地4:议价5:退货政策6:收款说明7:橱窗展示8:屏蔽目的地",  dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "defaultIs", value = "是否默认 true false 为空查询所有", dataType = "boolean", paramType = "query"),
			@ApiImplicitParam(name = "empowerId", value = "刊登账号", dataType = "String", paramType = "query")
	})
	@PostMapping("/getPublishTemplateALLList")
	public List<PublishTemplate> getPublishTemplateALLList(Integer platform,String site,Integer templateType,Boolean defaultIs,String empowerId){
		try {
			UserCommon user = getUserInfo.getUserInfo().getUser();

			List<PublishTemplate> findAll = publishTemplateService.getPublishTemplateALLList(platform,site,templateType,user.getUsername(),defaultIs,empowerId);
			return findAll;
		} catch (Exception e) {
			logger.error("获取模板下拉数据",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"获取模板下拉数据");
		}
	}

	@AspectContrLog(descrption="eBay平台sku生成",actionType= SysLogActionType.UDPATE)
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "int" ,name = "platform", value = "平台 1:amazon 2:eBay 3:wish 4:aliexpress",  required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "skus", value = "skus多个用,号格开", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "string", name = "site", value = "站点", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "sellerName", value = "授权店铺名称", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "sellerNameNum", value = "店铺规则生成的数量", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Long", name = "listingId", value = "屏蔽重复数据的id", required = false)
	})
	@PostMapping("/findEBayPlatformSku")
	@ApiOperation("eBay平台sku生成")
	public Map<String,String> findPlatformSku(Integer platform,String site,String skus,String sellerName,Long listingId,Integer sellerNameNum) {

		if(platform==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台为空");
		}
//		if(StringUtils.isBlank(site)){
//			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "站点为空");
//		}
		UserCommon user = getUserInfo.getUserInfo().getUser();
		return publishTemplateService.findPlatformSku(platform,site,skus,sellerName,listingId,sellerNameNum,user.getUsername());
	}



}


