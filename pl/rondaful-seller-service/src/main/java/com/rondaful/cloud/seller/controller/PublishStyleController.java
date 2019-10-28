package com.rondaful.cloud.seller.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.security.UserSession;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.seller.entity.PublishStyle;
import com.rondaful.cloud.seller.entity.PublishStyleType;
import com.rondaful.cloud.seller.entity.PublishTemplate;
import com.rondaful.cloud.seller.service.IAliexpressBaseService;
import com.rondaful.cloud.seller.service.PublishStyleService;
import com.rondaful.cloud.seller.vo.PublishStyleSearchVO;
import com.rondaful.cloud.seller.vo.PublishStyleTypeSearchVO;
import com.rondaful.cloud.seller.vo.PublishTemplateSearchVO;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@Api(description = "刊登风格")
@RestController
@RequestMapping("publish/style")
public class PublishStyleController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(PublishStyleController.class);
	@Autowired
	private PublishStyleService publishStyleService;

	@Autowired
	private GetLoginUserInformationByToken getUserInfo;

	@AspectContrLog(descrption = "风格类型列表",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "风格类型列表 PublishStyleTypeSearchVO", notes = "")
	@PostMapping("/getPublishStyleTypePage")
	public Page<PublishStyleType> getPublishStyleTypePage(PublishStyleTypeSearchVO vo){
		try {
			UserCommon user = getUserInfo.getUserInfo().getUser();
			//设置默认分页页数
			if(vo.getPage()==null){
				vo.setPage(1);
			}
			if(vo.getRow()==null){
				vo.setRow(10);
			}
			vo.setSystemIs(false);
			vo.setCreateId(user.getUserid().longValue());
			Page<PublishStyleType> findAll = publishStyleService.findPublishStyleTypePage(vo);
			return findAll;
		} catch (Exception e) {
			logger.error("风格类型列表查询",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"风格类型列表查询错误");
		}
	}

	@AspectContrLog(descrption = "风格列表查询",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "风格列表查询 PublishStyleSearchVO", notes = "")
	@PostMapping("/getPublishStylePage")
	public Page<PublishStyle> getPublishStylePage(PublishStyleSearchVO vo){
		try {
			UserCommon user = getUserInfo.getUserInfo().getUser();
			//设置默认分页页数
			if(vo.getPage()==null){
				vo.setPage(1);
			}
			if(vo.getRow()==null){
				vo.setRow(10);
			}
			//查询是否是系统模板
			if(vo.getSystemIs()!=null){
				if(vo.getSystemIs()){
					vo.setCreateId(0L);
				}else{
					vo.setCreateId(user.getUserid().longValue());
				}
			}else{
				vo.setCreateId(user.getUserid().longValue());
			}
			Page<PublishStyle> findAll = publishStyleService.findPublishStylePage(vo);
			return findAll;
		} catch (Exception e) {
			logger.error("风格列表查询",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"风格列表查询");
		}
	}


	@AspectContrLog(descrption="风格类型保存",actionType= SysLogActionType.ADD)
	@PostMapping("/savePublishStyleType")
	@ApiOperation(value="风格类型保存",notes="风格类型保存")
	public String savePublishStyleType(@RequestBody PublishStyleType publishStyleType)
	{
		UserCommon user = getUserInfo.getUserInfo().getUser();
		if(user== null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}
		publishStyleType.setCreateId(user.getUserid().longValue());
		publishStyleType.setPlAccount(user.getUsername());
		publishStyleService.savePublishStyleType(publishStyleType);
		return "true";
	}

	@AspectContrLog(descrption="风格保存",actionType= SysLogActionType.ADD)
	@PostMapping("/savePublishStyle")
	@ApiOperation(value="风格保存",notes="风格保存")
	public String savePublishStyle(@RequestBody PublishStyle publishStyle)
	{
		UserCommon user = getUserInfo.getUserInfo().getUser();
		if(user== null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}
		publishStyle.setCreateId(user.getUserid().longValue());
		publishStyle.setPlAccount(user.getUsername());
		publishStyleService.savePublishStyle(publishStyle);
		return "true";
	}

	@AspectContrLog(descrption="删除风格类型",actionType= SysLogActionType.DELETE)
	@DeleteMapping("/deletePublishStyleType/{id}")
	@ApiOperation("删除风格类型")
	public String deletePublishStyleType(@ApiParam(value = "被删除的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "被删除的id不能为空");
		}
		PublishStyleType publishStyleType = publishStyleService.getPublishStyleTypeById(id);
		if (publishStyleType == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不存在");
		}
		UserCommon user = getUserInfo.getUserInfo().getUser();
		if (!publishStyleType.getCreateId().equals(Long.valueOf(user.getUserid()))) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能删除其他用户的数据");
		}
		Integer countStyle = publishStyleService.checkPublishStyle(id);
		if (countStyle>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请先删除风格");
		}
		try {
			publishStyleService.deletePublishStyleType(id);
		} catch (Exception e) {
			logger.error("删除异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除异常");
		}
		return "true";
	}

	@AspectContrLog(descrption="删除风格",actionType= SysLogActionType.DELETE)
	@DeleteMapping("/deletePublishStyle/{id}")
	@ApiOperation("删除风格")
	public String deletePublishStyle(@ApiParam(value = "被删除的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "被删除的id不能为空");
		}
		PublishStyle publishStyle = publishStyleService.getPublishStyleById(id);
		if (publishStyle == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不存在");
		}
		UserCommon user = getUserInfo.getUserInfo().getUser();
		if (!publishStyle.getCreateId().equals(Long.valueOf(user.getUserid()))) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能删除其他用户的数据");
		}
		try {
			publishStyleService.deletePublishStyle(id);
		} catch (Exception e) {
			logger.error("删除异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除异常");
		}
		return "true";
	}



	@AspectContrLog(descrption="风格类型详情",actionType= SysLogActionType.QUERY)
	@PostMapping("/view-PublishStyleType/{id}")
	@ApiOperation("风格类型详情数据")
	public PublishStyleType viewPublishStyleTypeById(@PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}
		PublishStyleType publishStyleType = publishStyleService.getPublishStyleTypeById(id);
		if (publishStyleType == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
		}
//		String username = UserSession.getUserBaseUserInfo().getUsername();
//		if(!publishStyleType.getPlAccount().equalsIgnoreCase(username))
//		{
//			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "无权限查看该数据");
//		}
		return publishStyleType;
	}

	@AspectContrLog(descrption="风格详情",actionType= SysLogActionType.QUERY)
	@PostMapping("/view-PublishStyle/{id}")
	@ApiOperation("风格详情数据")
	public PublishStyle viewPublishStyleById(@PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");
		}
		PublishStyle publishStyle = publishStyleService.getPublishStyleById(id);
		if (publishStyle == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");
		}
//		String username = UserSession.getUserBaseUserInfo().getUsername();
//		if(!publishStyle.getPlAccount().equalsIgnoreCase(username))
//		{
//			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "无权限查看该数据");
//		}
		return publishStyle;
	}


	@AspectContrLog(descrption = "获取刊登风格下拉数据",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "获取刊登风格下拉数据", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "platform", value = "平台 1:amazon 2:eBay 3:wish 4:aliexpress", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "systemIs", value = "是否是系统风格类型ture,false不传查询所有", dataType = "boolean", paramType = "query")
	})
	@PostMapping("/getPublishStyleTypeALLList")
	public List<PublishStyleType> getPublishStyleTypeALLList(Integer platform,Boolean systemIs){
		try {
			UserCommon user = getUserInfo.getUserInfo().getUser();

			List<PublishStyleType> findAll = publishStyleService.getPublishStyleTypeAll(platform,user.getUserid(),systemIs);
			return findAll;
		} catch (Exception e) {
			logger.error("获取刊登风格下拉查询",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"获取刊登风格下拉查询");
		}
	}

	@AspectContrLog(descrption = "刊登风格预览效果",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "刊登风格预览效果", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "styleId", value = "风格id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "商品标题", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "descriptionANDpicture", value = "描述图片", dataType = "String", paramType = "query")

	})
	@PostMapping("/getStylePreview")
	public String getStylePreview(Long styleId,String title,String descriptionANDpicture){
		if(styleId==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "风格不能为空");
		}
		if(StringUtils.isBlank(title)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品标题不能为空");
		}
		if(StringUtils.isBlank(descriptionANDpicture)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品描述不能为空");
		}
		try {
			UserCommon user = getUserInfo.getUserInfo().getUser();
			PublishStyle publishStyle = publishStyleService.getPublishStyleById(styleId);
			if(publishStyle!=null){
				String content = publishStyle.getContent();
				if(content!=null){
					content = content.replace("[TITLE]",title);
					content = content.replace("[DESCRIPTION]",descriptionANDpicture);
				}else{
					content = descriptionANDpicture;
				}
				return content;
			}
			return "";
		} catch (Exception e) {
			logger.error("刊登风格预览效果异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"刊登风格预览效果异常");
		}
	}



	@AspectContrLog(descrption = "品连分类获取分格",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "品连分类获取分格", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "platform", value = "平台 1:amazon 2:eBay 3:wish 4:aliexpress", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plCategory", value = "品连分类", dataType = "String", paramType = "query")
	})
	@PostMapping("/getStyleCategory")
	public PublishStyle getStyleCategory(Integer platform,String plCategory){
		try {
			UserCommon user = getUserInfo.getUserInfo().getUser();
			PublishStyle findAll = publishStyleService.getStyleTypeCategory(platform,Long.valueOf(user.getUserid()),plCategory);
			return findAll;
		} catch (Exception e) {
			logger.error("品连分类获取分格异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"品连分类获取分格异常");
		}
	}


}


