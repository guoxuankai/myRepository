package com.rondaful.cloud.seller.controller;

import com.google.common.collect.Maps;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.enums.AliexpressProductUnitEnum;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.service.IAliexpressBaseService;
import com.rondaful.cloud.seller.service.IAliexpressCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 基础数据controller
 * @author chenhan
 *
 */
@Api(description = "Aliexpress基础接口")
@RestController
@RequestMapping("/aliexpress")
public class AliexpressBaseController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(AliexpressBaseController.class);
	@Autowired
	private IAliexpressBaseService aliexpressBaseService;
	@Autowired
	private IAliexpressCategoryService aliexpressCategoryService;
	@Autowired
	private AuthorizationSellerService authorizationSellerService;
	@Autowired
	private GetLoginUserInformationByToken getUserInfo;
	@Autowired
	private AliexpressConfig aliexpressConfig;
	@AspectContrLog(descrption = "获取速卖通级联分类",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通产品分类级联取值AliexpressCategory", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryParentId", value = "分类id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "siteName", value = "站点(预留)", dataType = "string", paramType = "query")
	})
	@PostMapping("/getCategoryList")
	public List<AliexpressCategory> getAliexpressCategoryByList(Long categoryParentId,Long empowerId,String siteName){
		if(categoryParentId == null)
		{
			//为空默认值0 第一级分类
			categoryParentId = 0L ;
		}
		List<AliexpressCategory> listAliexpressCategory = aliexpressBaseService.getAliexpressCategoryByList(categoryParentId);
		String headeri18n = request.getHeader("i18n");
		if (StringUtils.isNotBlank(headeri18n)){
			for (AliexpressCategory aliexpressCategory:listAliexpressCategory){
				//if("en_us".equals(i18n)) {
				aliexpressCategory.setCategoryName(aliexpressCategory.getCategoryNameEn());
				//}
			}
		}
		return listAliexpressCategory;
	}


	@ApiOperation(value = "速卖通产品分类详情", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "int", paramType = "query")
	})
	@PostMapping("/getAliexpressCategoryById")
	public AliexpressCategory getAliexpressCategoryById(Long categoryId){
		if(categoryId == null)
		{
			return null;
		}
		return aliexpressBaseService.getCategoryByCategoryId(categoryId);
	}


	@AspectContrLog(descrption = "获取速卖通分类属性",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通产品分类属性取值AliexpressCategoryAttribute", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "int", paramType = "query")
	})
	@PostMapping("/getCategoryAttributeList")
	public List<AliexpressCategoryAttribute> getAliexpressCategoryAttributeByCategoryIdList(Long categoryId,Long empowerId){
		if(categoryId == null)
		{
			logger.error("categoryId参数错误，参数:{}为空",categoryId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"商品分类不能为空");
		}
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		List<AliexpressCategoryAttribute> listAliexpressCategoryAttribute= aliexpressBaseService.getAliexpressCategoryAttributeByCategoryIdList(categoryId,empowerId);

		String headeri18n = request.getHeader("i18n");
		if (StringUtils.isNotBlank(headeri18n)){
			for (AliexpressCategoryAttribute aliexpressCategoryAttribute:listAliexpressCategoryAttribute){
				//if("en_us".equals(i18n)) {
				aliexpressCategoryAttribute.setAttributeName(aliexpressCategoryAttribute.getAttributeNameEn());
				//}
				for(AliexpressCategoryAttributeSelect select:aliexpressCategoryAttribute.getAttributeSelectList()){
					select.setSelectName(select.getSelectNameEn());
				}
			}
		}
		return listAliexpressCategoryAttribute;
	}

	@AspectContrLog(descrption = "获取速卖通区域调价",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通区域调价取值AliexpressCountry", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query")
	})
	@PostMapping("/getAliexpressCountryByList")
	public List<AliexpressCountry> getAliexpressCountryByList(String plAccount,Integer empowerId){
		if(plAccount == null)
		{
			logger.error("plAccount参数错误，参数:{}为空",plAccount);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"账号为空");
		}
		return aliexpressBaseService.getAliexpressCountryByList();
	}

	@AspectContrLog(descrption = "获取速卖通运费模板",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通运费模板取值AliexpressFreightTemplate", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query")
	})
	@PostMapping("/getFreightTemplateByPlAccountList")
	public List<AliexpressFreightTemplate> getFreightTemplateByPlAccountList(String plAccount,Long empowerId){
		if(plAccount == null)
		{
			logger.error("plAccount参数错误，参数:{}为空",plAccount);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"账号为空");
		}
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		return aliexpressBaseService.getAliexpressFreightTemplateByPlAccountList(empowerId,null,null);
	}

	@AspectContrLog(descrption = "获取速卖通服务模板",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通服务模板取值AliexpressPromiseTemplate", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query")
	})
	@PostMapping("/getPromiseTemplateByPlAccountList")
	public List<AliexpressPromiseTemplate> getAliexpressPromiseTemplateByPlAccountList(String plAccount, Long empowerId){
		if(plAccount == null)
		{
			logger.error("plAccount参数错误，参数:{}为空",plAccount);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"账号为空");
		}
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		return aliexpressBaseService.getAliexpressPromiseTemplateByPlAccountList(empowerId,null,null);
	}


	@AspectContrLog(descrption = "获取速卖通商品分组",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通商品分组取值AliexpressPromiseTemplate", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "groupId", value = "分组id", dataType = "int", paramType = "query")
	})
	@PostMapping("/getGroupByPlAccountList")
	public List<AliexpressGroup> getGroupByPlAccountList(String plAccount,Long groupId, Long empowerId){
		if(plAccount == null)
		{
			logger.error("plAccount参数错误，参数:{}为空",plAccount);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"账号为空");
		}
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		return aliexpressBaseService.getAliexpressGroupByPlAccountList(plAccount,groupId,empowerId);
	}



	@AspectContrLog(descrption = "速卖通获取品牌",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通分类属性品牌AliexpressCategoryAttributeSelect", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "attributeId", value = "属性id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "int", paramType = "query")
	})
	@PostMapping("/getCategoryAttributeSelectByList")
	public List<AliexpressCategoryAttributeSelect> getCategoryAttributeSelectByList(Long categoryId,String plAccount,Long attributeId, Long empowerId){
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		if(categoryId == null)
		{
			logger.error("categoryId参数错误，参数:{}为空",categoryId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"商品分类不能为空");
		}
		return aliexpressCategoryService.getCategoryAttributeSelectByList(categoryId,attributeId,empowerId);
	}

    @AspectContrLog(descrption = "速卖通属性选择值分页",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "速卖通属性选择值分页查询", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "当前页码", name = "currentPage", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "pageSize", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "属性id", name = "attributeId", dataType = "int"),
            @ApiImplicitParam(paramType = "query", value = "名称模糊查询", name = "selectName", dataType = "String")
    })
    @PostMapping("/findSelectPage")
    public Page<AliexpressCategoryAttributeSelect> findSelectPage(
            @RequestParam(value="currentPage",defaultValue = "1")Integer currentPage,
             @RequestParam(value="pageSize",defaultValue = "10")Integer pageSize,Long attributeId,String selectName) {
        try {
            UserCommon user = getUserInfo.getUserInfo().getUser();

            Page<AliexpressCategoryAttributeSelect> findAll = aliexpressCategoryService.findSelectPage(currentPage,pageSize,attributeId,selectName);
            return findAll;
        } catch (Exception e) {
            logger.error("速卖通列表查询",e);
            throw new GlobalException(com.rondaful.cloud.common.enums.ResponseCodeEnum.RETURN_CODE_100500,"速卖通列表查询");
        }
    }

	@AspectContrLog(descrption = "速卖通单位",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通单位查询", notes = "")
	@ApiImplicitParam(paramType = "query", value = "授权账号", name = "plAccount", dataType = "String")
	@PostMapping("/findProductUnitList")
	public List findProductUnitList(String plAccount) {
		try {
			return AliexpressProductUnitEnum.toList();
		} catch (Exception e) {
			logger.error("速卖通列表查询",e);
			throw new GlobalException(com.rondaful.cloud.common.enums.ResponseCodeEnum.RETURN_CODE_100500,"速卖通列表查询");
		}
	}


	@AspectContrLog(descrption = "速卖通同步运费模板",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通同步运费模板 AliexpressFreightTemplate", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query")
	})
	@PostMapping("/getAliexpressFreightTemplateSync")
	public List<AliexpressFreightTemplate> getAliexpressFreightTemplateSync(String plAccount, Integer empowerId){
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setEmpowerId(empowerId);
		empower.setPlatform(3);//速卖通平台
		empower = authorizationSellerService.selectOneByAcount(empower);
		if( empower == null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
		}
		String url = aliexpressConfig.getAliexpressUrl()+"/api/aliexpress/listfreighttemplate";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("sessionKey",empower.getToken());

		String body= HttpUtil.post(url,paramsMap);
		return aliexpressCategoryService.insertAliexpressFreightTemplate(body,empower.getEmpowerId().longValue(),plAccount);
	}

	@AspectContrLog(descrption = "速卖通同步服务模板",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通同步服务模板 AliexpressFreightTemplate", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query")
	})
	@PostMapping("/getAliexpressPromiseTemplateSync")
	public List<AliexpressPromiseTemplate> getAliexpressPromiseTemplateSync(String plAccount, Integer empowerId){
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setEmpowerId(empowerId);
		empower.setPlatform(3);//速卖通平台
		empower = authorizationSellerService.selectOneByAcount(empower);
		if( empower == null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
		}
		String url = aliexpressConfig.getAliexpressUrl()+"/api/aliexpress/querypromisetemplatebyid";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("sessionKey",empower.getToken());

		String body= HttpUtil.post(url,paramsMap);
		return aliexpressCategoryService.insertAliexpressPromiseTemplate(body,empower.getEmpowerId().longValue(),plAccount);
	}

	@AspectContrLog(descrption = "速卖通同步分组",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通同步分组 AliexpressFreightTemplate", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query")
	})
	@PostMapping("/getUserGroupsSync")
	public List<AliexpressGroup> getUserGroupsSync(String plAccount, Integer empowerId){
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setEmpowerId(empowerId);
		empower.setPlatform(3);//速卖通平台
		empower = authorizationSellerService.selectOneByAcount(empower);
		if( empower == null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
		}
		String url = aliexpressConfig.getAliexpressUrl()+"/api/aliexpress/getUserGroups";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("sessionKey",empower.getToken());

		String body= HttpUtil.post(url,paramsMap);
		return aliexpressCategoryService.insertAliexpressGroup(body,empower.getEmpowerId().longValue(),plAccount);
	}


	@AspectContrLog(descrption = "速卖通同步属性",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通同步属性 AliexpressFreightTemplate", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query")
	})
	@PostMapping("/getCategoryAttributeSync")
	public List<AliexpressCategoryAttribute> getCategoryAttributeSync(String plAccount, Integer empowerId,Long categoryId){
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		if(categoryId == null)
		{
			logger.error("categoryId参数错误，参数:{}为空",categoryId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"分类id不能为空");
		}
		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setEmpowerId(empowerId);
		empower.setPlatform(3);//速卖通平台
		empower = authorizationSellerService.selectOneByAcount(empower);
		if( empower == null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
		}
		String url = aliexpressConfig.getAliexpressUrl()+"/api/aliexpress/getallchildattributesresult";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("sessionKey",empower.getToken());
		paramsMap.put("categoryId",categoryId.toString());
		String body= HttpUtil.post(url,paramsMap);
		return aliexpressCategoryService.insertAliexpressCategoryAttribute(body,categoryId,null);
	}
	@AspectContrLog(descrption = "速卖通同步品牌",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通同步刊登账号分类品牌AliexpressCategoryAttributeSelect", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "empowerId", value = "授权账号id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "plAccount", value = "账号", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "分组id", dataType = "int", paramType = "query")
	})
	@PostMapping("/getCategoryAttributeBrand")
	public List<AliexpressCategoryAttributeSelect> getCategoryAttributeBrand(String plAccount,Long categoryId, Integer empowerId){
		if(empowerId == null)
		{
			logger.error("empowerId参数错误，参数:{}为空",empowerId);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登账号为空");
		}
		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setEmpowerId(empowerId);
		empower.setPlatform(3);//速卖通平台
		empower = authorizationSellerService.selectOneByAcount(empower);
		if( empower == null)
		{
			throw new GlobalException(com.rondaful.cloud.seller.enums.ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
		}
		String url = aliexpressConfig.getAliexpressUrl()+"/api/aliexpress/getchildattributesresultbypostcateidandpath";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("categoryId",categoryId.toString());
		paramsMap.put("sessionKey",empower.getToken());

		String body= HttpUtil.post(url,paramsMap);

		return aliexpressCategoryService.insertAliexpressCategoryAttributeBrand(body,
				Long.valueOf(categoryId),empower.getEmpowerId().longValue(),getUserInfo.getUserInfo().getUser().getUserid().longValue());
	}
	@AspectContrLog(descrption = "速卖通分类名称",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通分类名称", notes = "")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "int", paramType = "query")
	})
	@PostMapping("/getCategoryName")
	public String getCategoryName(Long categoryId){

		boolean boolEn = false;
		String headeri18n = request.getHeader("i18n");
		if (StringUtils.isNotBlank(headeri18n)){
			boolEn = true;
		}
		return aliexpressBaseService.getCategoryName(categoryId,boolEn);
	}
}


