

package com.rondaful.cloud.seller.controller;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.seller.common.task.ProcessXmlDraftTask;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.amazon.*;
import com.rondaful.cloud.seller.enums.*;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.remote.RemoteLogisticsService;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.remote.RemoteUserService;
import com.rondaful.cloud.seller.service.*;
import com.rondaful.cloud.seller.utils.*;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.security.UserSession;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.common.mws.AmazonConvert;
import com.rondaful.cloud.seller.common.task.AmazonTemplateGenerate;
import com.rondaful.cloud.seller.common.task.ProcessXmlTask;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.constants.AmazonProductDataConstants;
import com.rondaful.cloud.seller.constants.AmazonPublishUpdateStatus;
import com.rondaful.cloud.seller.dto.CommodityDTO;
import com.rondaful.cloud.seller.generated.ProductImage;
import com.rondaful.cloud.seller.vo.AmazonDisposePriceVO;
import com.rondaful.cloud.seller.vo.BatchUpdateVO;
import com.rondaful.cloud.seller.vo.PublishListingParamsVO;
import com.rondaful.cloud.seller.vo.PublishListingVO;
import com.rondaful.cloud.seller.vo.ResultPublishListingVO;
import com.rondaful.cloud.seller.vo.UserVO;

import springfox.documentation.annotations.ApiIgnore;

import com.rondaful.cloud.seller.entity.amazon.AmazonImageRequest.Images;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;

/**
   *   亚马逊刊登 Cntroller
 * @author ouxiangfeng
 *
 */
@RestController
@RequestMapping("/amazon")
@Api(description = "Amazon刊登接口-OuXiangFeng")
public class AmazonPublishController  extends BaseController {

	private final Logger logger = LoggerFactory.getLogger(AmazonPublishController.class);
	@Autowired
	AmazonCategoryService amazonCategoryService;

	@Autowired
	AmazonXsdTemplateService amazonXsdTemplateService;

	//@Autowired
	//SubmitFeed submitFeed;

	@Autowired
	AmazonConvert amazonConvert;

	@Autowired
	AmazonPublishListingService amazonPublishListingService;

	@Autowired 
	AmazonPublishSubListingService amazonPublishSubListingService;

	@Autowired
	AmazonPublishSubListingService usbListingService;

	@Autowired
	private GetLoginUserInformationByToken getLoginUserInformationByToken;

	@Autowired
	AuthorizationSellerService authorizationSellerService;

    @Autowired
    private UpcmanageService upcManageService;

    @Autowired
    private PublishLogService publishLogService;

    @Autowired
	private RemoteCommodityService remoteCommodityService;
    
    @Autowired
    private AmazonTemplateGenerate amazonTemplateGenerate;

    @Autowired
	private SetAmazonSubSkuUtils setAmazonSubSkuUtils;
    
    @Autowired
    private AmazonTemplateAttributeService amazonTemplateAttributeService;



    @Autowired
	private AmazonSubListingUtil amazonSubListingUtil;



	@Autowired
	private AmazonTemplateRuleService amazonTemplateRuleService;

	@Autowired
	private AmazonBatchCopyUtils amazonBatchCopyUtils;

    
    private static final String msg="亚马逊同步数据";
    
	@Autowired
	private IEmpowerService empowerService;
     

/*	private List<String > eurMarketplaces = Arrays.asList(
			"A1PA6795UKMFR9",  //德国
			"A1RKKUPIHCS9HS", // 西班牙
			"A13V1IB3VIYZZH",//法国
			"A1F83G8C2ARO7P", //英国
			"APJ6JRA9NG5V4" // 意大利
	);*/

	@Autowired
	private RemoteUserService remoteUserService;

    /*//一级模板是这些的，不需要处理二级模板
    private List<String> enumClassTemplate = Arrays.asList(
    		"Shoes",
    		"Clothing",
    		"Sports",
    		"RawMaterials",
    		"PowerTransmission",
    		"SportsMemorabilia");
    
    //一级模板是这些的，不需要处理二级模板，,注意传入的二级模板的大小写
    private List<String> enumClassChildTemplate = Arrays.asList(
    		"professionalHealthCare",
    		"Luggage",
    		"electronicGiftCard"
    		,"furniture");
    
    //一二级模板组合是这些的，二级模板不需要处理
    private List<String> enumClassCompositeTemplate = Arrays.asList(
    		"ToysBaby.BabyProducts",
    		"WineAndAlcohol.beer",
    		"WineAndAlcohol.spirits");*/
    
	/**
	 * 	根据站点查询分类 </br>
	 * 	原siteName为国家编码的，于2019-01-22日将参数值改为站点ID。所以现在参入的是站点ID
	 * @param id
	 * @param siteName
	 * @return
	 */
	@AspectContrLog(descrption="根据站点查询分类",actionType= SysLogActionType.QUERY)
	@ApiOperation(value="根据站点查询分类",notes="获取当前站点的产品分类")
	@PostMapping("/publish-category")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "分类，0为所有顶级分类的父id", dataType = "Long", paramType = "query"),
        @ApiImplicitParam(name = "siteName",  value = "站点id",  dataType = "string", paramType = "query",required = true)
    })
	public List<AmazonCategory> getCategory(Long id, String siteName) {
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplaceForKeyId().get(siteName);
		if(marketplaceId == null)
		{
			logger.error("根据站点查询分类参数错误，参数站点id:{}为空",siteName);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"当前站点不存在");
		}

		if("GB".equalsIgnoreCase(marketplaceId.getCountryCode()))
		{
			marketplaceId.setCountryCode("UK");
		}
		
		List<AmazonCategory> result = amazonCategoryService.queryCategoryList(id,marketplaceId.getCountryCode());
		if(CollectionUtils.isEmpty(result))
		{
			logger.error("根据分类，当前category_id:{},在数据库中找不到");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "找不到分类数据");
		}
		return result;
	}

	/**
	 * 	根据站点及关键词(name)查询分类 </br>
	 * 	原siteName为国家编码的，于2019-01-22日将参数值改为站点ID。所以现在参入的是站点ID
	 * @param keyWord
	 * @param siteName
	 * @return
	 */
	@AspectContrLog(descrption="根据站点及关键词(name)查询分类",actionType= SysLogActionType.QUERY)
	@ApiOperation(value="根据站点及关键词(name)查询分类",notes="获取当前站点及关键字(name)的产品分类")
	@PostMapping("/publish-category-siteNameAndKeyWord")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "keyWord", value = "搜索关键词(name)", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "siteName",  value = "站点id",  dataType = "string", paramType = "query")
	})
	public List<AmazonCategory> getCategoryBySiteNameAndKeyWord(String keyWord, String siteName) {
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplaceForKeyId().get(siteName);
		if(keyWord == null)
		{
			logger.error("根据站点及关键词(name)查询分类参数错误，参数keyWord:{}为空", keyWord);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"当前关键词不允许为空");
		}

		if(marketplaceId == null)
		{
			logger.error("根据站点及关键词(name)查询分类参数错误，参数站点id:{}为空", siteName);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前站点不存在");
		}

		if("GB".equalsIgnoreCase(marketplaceId.getCountryCode()))
		{
			marketplaceId.setCountryCode("UK");
		}

		List<AmazonCategory> result = amazonCategoryService.queryCategoryListSiteNameAndKeyWord(keyWord, marketplaceId.getCountryCode());
		if(CollectionUtils.isEmpty(result))
		{
			logger.error("根据站点及关键词(name)查询分类，在数据库中找不到，当前站定id:{},keyWord:{}", siteName, keyWord);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "找不到分类数据");
		}
		return result;
	}

	@AspectContrLog(descrption="根据分类id查询分类",actionType= SysLogActionType.QUERY)
	@PostMapping("/publish-category-categoryid")
	@ApiOperation(value="根据分类id查询分类",notes="获取当前分类id的分类")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "Long", paramType = "query",required = true),
		@ApiImplicitParam(name = "siteName",  value = "站点id",  dataType = "string", paramType = "query",required = true)
    })
	public List<AmazonCategory> getCategorybyCategoryId(Long  categoryId, String siteName) {
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplaceForKeyId().get(siteName);
		if(marketplaceId == null)
		{
			logger.error("根据分类id查询分类参数错误，参数站点id:{}为空",siteName);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"当前站点不存在");
		}

		if("GB".equalsIgnoreCase(marketplaceId.getCountryCode()))
		{
			marketplaceId.setCountryCode("UK");
		}

		return amazonCategoryService.queryCategoryListByCategoryId(new Long[] {categoryId},marketplaceId.getCountryCode());
	}

	@AspectContrLog(descrption="获取站点信息",actionType= SysLogActionType.QUERY)
	@PostMapping("/publish-site")
	@ApiOperation(value="获取站点信息",notes="获取所有站点信息")
	public List<MarketplaceId> getPublishSite()
	{
		Map<String,MarketplaceId> map =MarketplaceIdList.createMarketplace();
		// 为了前端的方便使用，转成list
		List<MarketplaceId> result = map.entrySet().stream().map(et -> et.getValue()).collect(Collectors.toList());
		return  result;
	}

	@AspectContrLog(descrption="模板分类信息",actionType= SysLogActionType.QUERY)
	@PostMapping("/publish-template-category")
	@ApiOperation(value="模板分类信息",notes="查询模板分类信息,获取的仅是首级分类")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataType = "string", name = "marketplaceId", value = "站点Id", required = true)
	})
	public List<AmazonNode> getFirstXsdTemplateCategory( String marketplaceId)
	{
		try {
			return amazonXsdTemplateService.getFirstXsdTemplateCategory(marketplaceId);
		} catch (Exception e) {
			logger.error("获取模板分类出错", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}


	@AspectContrLog(descrption="查询指定的模板下的分类信息",actionType= SysLogActionType.QUERY)
	@PostMapping("/publish-template-category-next")
	@ApiOperation(value="查询指定的模板下的分类信息",notes="查询指定的模板下的分类信息")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataType = "string", name = "classPath", value = "模板指向的位置,子classPath", required = true),
		@ApiImplicitParam(paramType = "query", dataType = "string", name = "marketplaceId", value = "站点Id", required = true)
	})
	public List<AmazonNode> getNextXsdTemplateCategory( String classPath, String marketplaceId )
	{
		try {
			return amazonXsdTemplateService.getNextXsdTemplateCategory(classPath,marketplaceId);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.error("获取模板分类出错", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
		}
	}

	@AspectContrLog(descrption="查询指定的模板相关属性",actionType= SysLogActionType.QUERY)
	@PostMapping("/publish-template-allattr")
	@ApiOperation(value="查询指定的模板相关属性",notes="查询指定的模板相关属性")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataType = "String", name = "templateParentName", value = "一级模板名称", required = true),
		@ApiImplicitParam(paramType = "query", dataType = "String", name = "templateChildName", value = "二级模板名称，如果是玫举，则传入fieldName"),
		@ApiImplicitParam(paramType = "query", dataType = "String", name = "marketplaceId", value = "站点ID")
	})
	public  Map<String,Object> getTemplateAttr(
			@RequestParam(value="templateParentName", required=true) String  templateParentName,
			@RequestParam(value="templateChildName" , required=false) String  templateChildName,
			@RequestParam(value="marketplaceId", required=true) String marketplaceId )
	{
		//templateName = DecapitalizeChar.decapitalizeUpperCase(templateName);

		if(StringUtils.isBlank(templateParentName))
		{
			logger.error("请求参数错误，参数为空");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"请求参数错误");
		}
		List<AmazonAttr> productParentAttr = new ArrayList<>();
		List<AmazonAttr> productChildAttr = new ArrayList<>();
		ModelMap  result = new ModelMap();
		
		// com.rondaful.cloud.seller.generated.Shoes
		templateParentName =  templateParentName.substring(templateParentName.lastIndexOf(".") + 1 , templateParentName.length());
		
		String  childName = templateChildName;
		//这里需要特殊处理玫举类型的二级模板
		if(!AmazonTemplateUtils.enumClassTemplate.contains(templateParentName))
		{
			//com.rondaful.cloud.seller.generated.WineAndAlcohol.beer
			childName = templateChildName.substring(templateChildName.lastIndexOf(".") + 1, templateChildName.length()); // beer
			if(templateChildName.indexOf("$") != -1) // com.rondaful.cloud.seller.generated.WineAndAlcohol$ProductType$Wine
			{
				childName = templateChildName.substring(templateChildName.lastIndexOf("$") + 1, templateChildName.length()); // Wine
			}
		}
		
		//String _tempChildName = StringUtils.isBlank(templateChildName) ? null : templateChildName;
		
		/*AmazonTemplateAttribute amazonTemplateAttribute = new AmazonTemplateAttribute();
		amazonTemplateAttribute.setMarketplaceId(marketplaceId);*/
		List<AmazonTemplateAttribute> attrList = amazonTemplateAttributeService.selectByTemplateAndMarketplaceId(
				templateParentName,childName, marketplaceId);
		
		/*if(CollectionUtils.isEmpty(attrList))
		{
			logger.error("刊登模板未导入，无法进一步操作。");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登模板未导入，无法进一步操作。");
		}*/
		if(CollectionUtils.isNotEmpty(attrList) && StringUtils.isBlank(attrList.get(0).getMarketplaceId()))
		{
			logger.error("刊登模板数据未经处理，无法进一步操作。");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登模板数据未经处理，无法进一步操作。");
		}
			
		//如果数据库不配置必填项，则与xsd为标准
		if(CollectionUtils.isEmpty(attrList))
		{
			logger.debug("配置中找不到templateParentName:{},templateChildName:{}, marketplaceId:{}的数据，将会读取XSD属性",templateParentName,templateChildName, marketplaceId);
			attrList = new ArrayList<AmazonTemplateAttribute>();
		}
		try {
			// 一级模板属性加载
			Class classzParent = Class.forName(ClassReflectionUtil.BASE_CLASS_PATH + templateParentName);
			ClassReflectionUtil.beanToJson(productParentAttr, classzParent ,null,new ArrayList<AmazonAttr>(),attrList);
			List<String> themeList =  ClassReflectionUtil.calculateTheme(classzParent);
			// 二级模板属性加载
			//有些模板没有二级模板，也有可能同一个prodctType中有对象，与String组合在一个productType中（如：WineAndAlcohol.productType），。
			// 注意：大小写
			if(StringUtils.isNotBlank(templateChildName) 
					&& !AmazonTemplateUtils.enumClassTemplate.contains(templateParentName) // 一级模板不在集合里的
					&& !AmazonTemplateUtils.enumClassChildTemplate.contains(childName) // 二级模板不在集合里的
					&& !AmazonTemplateUtils.enumClassCompositeTemplate.contains(templateParentName+"."+childName)) //  一.二 级模板不在集合里的
			{
				Class classzChild = null ;
				if(templateChildName.indexOf("$") != -1) //带$为内部类
				{
					classzChild = Class.forName(templateChildName);
				}else
				{
					classzChild = Class.forName(ClassReflectionUtil.BASE_CLASS_PATH + childName);
				}
				ClassReflectionUtil.beanToJson(productChildAttr, classzChild ,null,new ArrayList<AmazonAttr>(),attrList);
				List<String> childThemeList =  ClassReflectionUtil.calculateTheme(classzChild);
				if(CollectionUtils.isNotEmpty(childThemeList)) // 如果二级模板有变体，则不使用父的变体
				{
					themeList = childThemeList;
				}
			}
			
			
			// 主题解释 -------------------begin ------------------------
			//List<String> themeList = new ArrayList<>();
			for(AmazonTemplateAttribute attr : attrList) //如果配置了，就读取数据的
			{
				 if("variationTheme".equalsIgnoreCase(attr.getAttributeName()) && 
						 StringUtils.isNotBlank(attr.getOptions()))
				 {
				 	String arrays [] = attr.getOptions().split(ClassReflectionUtil.ATTR_ENUM_VALUES_PREX);
					//List<String> listValues = new ArrayList<>(arrays.length);
				 	themeList.clear();
					Collections.addAll(themeList, arrays);
					 // themeList.addAll(Arrays.asList(attr.getOptions().split(ClassReflectionUtil.ATTR_ENUM_VALUES_PREX)));
					 break;
				 }
			 }
			
			
			
			// 二级模板是否有主题。
			boolean isExtendsVariationTheme = ClassReflectionUtil.isExtendsVariationTheme(productChildAttr);
			
			// 计算那些主题是有效的
			/*List<String> _tempThemeList = new ArrayList<>();
			if(isExtendsVariationTheme)
			{
				_tempThemeList = ClassReflectionUtil.calculateThemeNew(Class.forName(ClassReflectionUtil.BASE_CLASS_PATH + templateChildName), themeList);
			}else
			{
				_tempThemeList = ClassReflectionUtil.calculateThemeNew(Class.forName(ClassReflectionUtil.BASE_CLASS_PATH + templateParentName), themeList);
			}*/
			// 主题解释 ------------------- end  ------------------------
			
			
			// 高级范文，不管是一级还是二级模，如果VariationData与ClassificationData存在相同的属性，则去掉ClassificationData下相同的属性
			ClassReflectionUtil.clearSurplusForAttr(productParentAttr, productChildAttr);
			// 去掉一、二级模板存在相同的属性
			ClassReflectionUtil.clearParentAndChildAttr(productParentAttr, productChildAttr);
			
			result.put("productBaseAttr", AmazonProductDataConstants.productBaseAttrSetter(attrList));
			result.put("productParentAttr",productParentAttr); //一级模板属性
			result.put("productChildAttr",productChildAttr);   // 二级模板属性
			result.put("variationTheme",themeList);   // 主题
			result.put("productChildExtendsTheme",isExtendsVariationTheme); //二级模模板是否存在variationTheme
			return result;
		} catch (Exception e) {
			logger.error("获取模板信息时出错", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}

	public static void main(String[] args) {
		//String templateParentName = "com.sere.home.Acciereisefeows";
		 //System.out.println(templateParentName.substring(templateParentName.lastIndexOf(".")+1, templateParentName.length()));
		/*List<AmazonAttr> productAttr = new ArrayList<>();
		 Map<String,Object> result = new HashMap<>();
		try {
			Class classz = Class.forName("com.rondaful.cloud.seller.generated.Wireless");
			ClassReflectionUtil.beanToJson(productAttr, classz ,null,new ArrayList<AmazonAttr>());
			//List<String> themeList =  ClassReflectionUtil.calculateTheme(classz);
			System.out.println(">>>"+JSON.toJSONString(productAttr));
			 result.put("amazonAttr",productAttr);
			 //result.put("variationTheme",themeList);
		} catch (Exception e) {
			// logger.error("获取模板信息时出错", e);
		}*/
	}

	@AspectContrLog(descrption="编辑",actionType= SysLogActionType.ADD)
	@PostMapping("/publish-submitfeed-edit")
	@ApiOperation(value="保存编辑",notes="")
	public String submitfeedEdit(@RequestBody @Valid  AmazonRequestProduct requestProduct,BindingResult bindingResult)
	{
		/*if(bindingResult.hasErrors())
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,
			bindingResult.getAllErrors().get(0).getDefaultMessage());
		}*/
		AmazonPublishListing listing = amazonPublishListingService.selectByPrimaryKey(requestProduct.getId());
		if(listing == null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"刊登数据不存在");
		}
		if(listing.getPublishStatus() == null || !(listing.getPublishStatus() == 1 || listing.getPublishStatus() == 4  )){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"该状态不能编辑");
		}


		if(UserSession.getUserBaseUserInfo()== null)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}
		requestProduct.setSubPublishStatus(null);

		// 这里转换一下，和张聪开的冲突，这里做了兼容
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplaceForKeyId().get(
				requestProduct.getCountryCode());
		requestProduct.setCountryCode(marketplaceId.getCountryCode());


		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setWebName(MarketplaceIdList.createMarketplace().get(requestProduct.getCountryCode()).getMarketplaceId());
		empower.setThirdPartyName(requestProduct.getMerchantIdentifier());
		empower.setPlatform(2);
		empower = authorizationSellerService.selectAmazonAccount(empower);
		if( empower == null)
		{
			logger.error("授权信息错误：找不到授权信息");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
		}

		if(StringUtils.isBlank(requestProduct.getBatchNo()))
		{
			logger.error("参数错误，批次号丢失");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"参数错误,批次号丢失");
		}
		String userLogin = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
		AmazonPublishListing amazonPublishListing=new AmazonPublishListing();
		try {
			 amazonPublishListing =amazonPublishListingService.saveOrUpdate(requestProduct,  userLogin,  null, empower,null); //草稿
		} catch (Exception e) {
			logger.error("listting相关参数数据有误",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"listting相关数据有误");
		}
		
		//刊登操作日志
		publishLogService.insert(JSON.toJSONString(requestProduct), PublishLogEnum.valueOf("EDIT"),
				UserSession.getUserBaseUserInfo().getUserid(), UserSession.getUserBaseUserInfo().getUsername(),amazonPublishListing.getId());

		try {
			ExecutorService executor = Executors.newFixedThreadPool(1);
			ProcessXmlDraftTask processXmlDraftTask = new ProcessXmlDraftTask(requestProduct,amazonSubListingUtil);
			Future<String> futureResult = executor.submit(processXmlDraftTask);
			String errorMsg = futureResult.get();
			executor.shutdown();

			//AmaznoExecutors.getInstance().addTask(new ProcessXmlTask(requestProduct));
			if(errorMsg != null && errorMsg.length() > 0)
			{
				logger.warn("生成sub数据时异常，中止线操作，数据被手动删除，被删除的ID为：{}",amazonPublishListing.getId());
				// amazonPublishListingService.deleteByPrimaryKey(amazonPublishListing.getId());
				amazonPublishSubListingService.deleteForBaseId(amazonPublishListing.getId());
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,errorMsg);
			}
		}catch (Exception e){
			logger.error("生成sub数据时异常",e);
		}
		return requestProduct.getBatchNo();
	}


	@AspectContrLog(descrption="保存草稿不做校验",actionType= SysLogActionType.ADD)
	@PostMapping("/publish-submitfeed-draft-not-check")
	@ApiOperation(value="保存草稿不做校验",notes="保存草稿,仅限于保存草稿，不参与预生成报文")
	public String submitfeedDraftNotCheck(@RequestBody AmazonRequestProduct requestProduct)
	{
		if(UserSession.getUserBaseUserInfo()== null)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}

		//logger.info("临时调试打的日志 ：{}",JSONObject.toJSONString(requestProduct));

		// 这里转换一下，和张聪开的冲突，这里做了兼容
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplaceForKeyId().get(
				requestProduct.getCountryCode());
		requestProduct.setCountryCode(marketplaceId.getCountryCode());

		requestProduct.setSubPublishStatus(null);
		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setWebName(MarketplaceIdList.createMarketplace().get(requestProduct.getCountryCode()).getMarketplaceId());
		empower.setThirdPartyName(requestProduct.getMerchantIdentifier());
		empower.setPlatform(2);
		empower = authorizationSellerService.selectAmazonAccount(empower);
		if( empower == null)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
		}

		String userLogin = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
		AmazonPublishListing tempObj = amazonPublishListingService.saveOrUpdate(requestProduct,  userLogin,  AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT, empower,null); //草稿
		
		//刊登操作日志
		publishLogService.insert(JSON.toJSONString(requestProduct),
				 PublishLogEnum.valueOf("INSERT"),
				UserSession.getUserBaseUserInfo().getUserid(), UserSession.getUserBaseUserInfo().getUsername(),tempObj.getId());
		
		setUpc(requestProduct.getStandardProductID());


		try {
			requestProduct.setId(tempObj.getId());
			ExecutorService executor = Executors.newFixedThreadPool(1);
			ProcessXmlDraftTask processXmlDraftTask = new ProcessXmlDraftTask(requestProduct,amazonSubListingUtil);
			Future<String> futureResult = executor.submit(processXmlDraftTask);
			String errorMsg = futureResult.get();
			executor.shutdown();

			//AmaznoExecutors.getInstance().addTask(new ProcessXmlTask(requestProduct));
			if(errorMsg != null && errorMsg.length() > 0)
			{
				logger.warn("生成sub数据时异常，中止线操作，数据被手动删除，被删除的ID为：{}",tempObj.getId());
				// amazonPublishListingService.deleteByPrimaryKey(amazonPublishListing.getId());
				amazonPublishSubListingService.deleteForBaseId(tempObj.getId());
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,errorMsg);
			}
		}catch (Exception e){
			logger.error("生成sub数据时异常",e);
		}
		return tempObj.getBatchNo();
	}
	
	@AspectContrLog(descrption="保存草稿",actionType= SysLogActionType.ADD)
	@PostMapping("/publish-submitfeed-draft")
	@ApiOperation(value="保存草稿",notes="保存草稿,仅限于保存草稿，不参与预生成报文")
	public String submitfeedDraft(@RequestBody @Valid  AmazonRequestProduct requestProduct,BindingResult bindingResult)
	{
		if(bindingResult.hasErrors())
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,
			bindingResult.getAllErrors().get(0).getDefaultMessage());
		}

		if(UserSession.getUserBaseUserInfo()== null)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}
		requestProduct.setSubPublishStatus(null);
		// 这里转换一下，和张聪开的冲突，这里做了兼容
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplaceForKeyId().get(
				requestProduct.getCountryCode());
		requestProduct.setCountryCode(marketplaceId.getCountryCode());


		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setWebName(MarketplaceIdList.createMarketplace().get(requestProduct.getCountryCode()).getMarketplaceId());
		empower.setThirdPartyName(requestProduct.getMerchantIdentifier());
		empower.setPlatform(2);
		empower = authorizationSellerService.selectAmazonAccount(empower);
		if( empower == null)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
		}

		String userLogin = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
		AmazonPublishListing tempObj = amazonPublishListingService.saveOrUpdate(requestProduct,  userLogin,  AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT, empower,null); //草稿
		
		//刊登操作日志
		publishLogService.insert(JSON.toJSONString(requestProduct),
				 PublishLogEnum.valueOf("INSERT"),
				UserSession.getUserBaseUserInfo().getUserid(), UserSession.getUserBaseUserInfo().getUsername(),tempObj.getId());
		
		setUpc(requestProduct.getStandardProductID());
		return tempObj.getBatchNo();
	}

	@AspectContrLog(descrption="保存并刊登",actionType= SysLogActionType.UDPATE)
	@PostMapping("/publish-submitfeed-save")
	@ApiOperation(value="保存并刊登",notes="保存并刊登")
	public void submitfeedSave(@RequestBody @Valid AmazonRequestProduct requestProduct, BindingResult bindingResult)
	{
		UserDTO userDTO = getLoginUserInformationByToken.getUserDTO();

		List varRequestProductList = requestProduct.getVarRequestProductList();
		String plSku = null;
		if(varRequestProductList != null && varRequestProductList.size() > 0){
			Object o = varRequestProductList.get(0);
			if(o instanceof AmazonRequestProduct ){
				AmazonRequestProduct  req = (AmazonRequestProduct)o;
				plSku = req.getPlSku();
			}

		}
		logger.info("卖家用户 ：{} 提交保存并刊登，spu为： {}，sku为 ：{}，亚马逊ID：{}， 站点：{}  ",userDTO.getLoginName(),
				requestProduct.getSpu(),plSku,requestProduct.getManufacturer(),requestProduct.getCountryCode());
		/*if(bindingResult.hasErrors())
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,
			bindingResult.getAllErrors().get(0).getDefaultMessage());
		}*/
		requestProduct.setSubPublishStatus(null);
		String batchNoStr = requestProduct.getBatchNo();
		try
		{
			// 这里转换一下，和张聪开的冲突，这里做了兼容
			String marketplaceId = requestProduct.getCountryCode();
			MarketplaceId marketplaceIdObj = MarketplaceIdList.createMarketplaceForKeyId().get(
					requestProduct.getCountryCode()) ;
			requestProduct.setCountryCode(marketplaceIdObj.getCountryCode());

			Empower empower = new Empower();
			empower.setStatus(1);
			empower.setPinlianAccount(userDTO.getTopUserLoginName());
			empower.setWebName(marketplaceId);
			empower.setThirdPartyName(requestProduct.getMerchantIdentifier());
			empower.setPlatform(2);
			empower = authorizationSellerService.selectAmazonAccount(empower);
			if( empower == null)
			{
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
			}

			// new AmazonTemplateValidate().validateAttrRely(requestProduct.getTemplatesName(), requestProduct.getTemplatesName2(), requestProduct.getCategoryPropertyJson());
			
			setUpc(requestProduct.getStandardProductID());
			List<AmazonRequestProduct> plist = requestProduct.getVarRequestProductList();
			requestProduct.setIsMultiattribute(Boolean.FALSE);
			if(CollectionUtils.isNotEmpty(plist))
			{
				requestProduct.setIsMultiattribute(Boolean.TRUE);
			}

			AmazonPublishListing amazonPublishListing = amazonPublishListingService.saveOrUpdate(
					requestProduct,getLoginUserInformationByToken.getUserDTO().getTopUserLoginName(),
					AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT, empower,new Date()); //等待刊登

			//刊登操作日志
			publishLogService.insert(amazonPublishListing.getPublishMessage(),
					StringUtils.isBlank(batchNoStr) ? PublishLogEnum.valueOf("INSERT"):PublishLogEnum.valueOf("EDIT"),
					UserSession.getUserBaseUserInfo().getUserid(), UserSession.getUserBaseUserInfo().getUsername(),amazonPublishListing.getId());

			//requestProduct.setBatchNo(UUID.randomUUID().toString());
			StringBuilder errorFuture = new StringBuilder();
			ExecutorService executor = Executors.newFixedThreadPool(1);
            ProcessXmlTask processXmlTask = new ProcessXmlTask(requestProduct,amazonSubListingUtil);
            Future<String> futureResult = executor.submit(processXmlTask);
			String errorMsg = futureResult.get();
			if(StringUtils.isNotEmpty(errorMsg))
			{
				errorFuture.append(errorMsg);
			}
			executor.shutdown();

			//AmaznoExecutors.getInstance().addTask(new ProcessXmlTask(requestProduct));
			if(errorMsg.length() > 0) // 如果有错误，以异常方式返回错误信息
			{
				// 回滚数据，这里不用事务，在线程中增加难度，这里用手动回滚
				logger.warn("解释xml时出现错误，中止线操作，数据被手动删除，被删除的ID为：{}",amazonPublishListing.getId());
				// amazonPublishListingService.deleteByPrimaryKey(amazonPublishListing.getId());
				amazonPublishSubListingService.deleteForBaseId(amazonPublishListing.getId());
				amazonPublishListingService.deleteByPrimaryKey(amazonPublishListing.getId());
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,errorFuture.toString());
			}
			try{
            	logger.info("保存并刊登回写分类映射给商品模块开始：" + requestProduct.getProductCategory1Path());
                List<AmazonCategory> amazonCategories = amazonCategoryService.selectCategoryListById(requestProduct.getProductCategoryForInteger());
                remoteCommodityService.saveOrUpdateSpuCategory(requestProduct.getSpu(),"Amazon",
                        requestProduct.getCountryCode(),amazonCategories.get(0).getCategoryId(),requestProduct.getProductCategory1Path());
            }catch (Exception e){
                logger.error("保存并刊登回写分类映射给商品模块失败",e);
            }
		}catch(Exception e){
			logger.error("保存并刊登异常",e);
			if(e.getMessage().contains("\\")){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"包含非法字符");
			}
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
	}


	public void setUpc(String upc){

//			if (StringUtils.isNotBlank(upc)){
//				Integer selectUsedplatform = upcManageService.selectUsedplatform(upc);
//				logger.error("queryUsedplatform:{}",selectUsedplatform);
//				if(selectUsedplatform == null )	{
//					selectUsedplatform =2;
//				}else{
//					if (selectUsedplatform < 2){
//						selectUsedplatform =3;
//					}else{
//						selectUsedplatform =2;
//					}
//				}
//				
//				upcManageService.updateUPCStatus(upc, 1, selectUsedplatform);
//			}

	}

	@AspectContrLog(descrption="单独的刊登",actionType= SysLogActionType.UDPATE)
	@PostMapping("/publish-submitfeed")
	@ApiOperation(value="单独的刊登    草稿后调用",notes="发布刊登，对已存在的数据进行发布，如果数据有更改，需要调用保存并刊登接口")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataType = "String", name = "batchNo", value = "当前批次号", required = true)
	})
	public void submitfeed(String batchNo)
	{
		try
		{
			// 这个结果一般情况下都是只有一条数据，list是为以后有可能发生
			List<AmazonPublishListing> amazonPublishListingList = amazonPublishListingService.selectBybatchNo(batchNo);
			if(CollectionUtils.isEmpty(amazonPublishListingList))
			{
				logger.error("当前批次号：{} 找不到可操作数据",batchNo);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"批次号不存在。");
			}

			// 清除历史数据,子表的数据需要重新生成，这里先删除，下面再生成子表数据
			AmazonPublishListing amazonPublishListing = amazonPublishListingList.get(0);
			
			//校验是否有必填项
			if(amazonPublishListing.getHasRequired()==AmazonPublishEnums.HasRequired.YES.getCode()) {
				logger.error("存在必填项未填写");
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,"存在必填项未填写");
			}
			
			// 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线,6:等待刊登
			if(amazonPublishListing.getPublishStatus() == 2
					|| amazonPublishListing.getPublishStatus() == 3
					|| amazonPublishListing.getPublishStatus() == 5
					|| amazonPublishListing.getPublishStatus() == 6)
			{
				logger.error("刊登中，在线，多次刊登， 已下线，等待刊登的数据不可多次刊登");
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"刊登中，在线， 已下线，等待刊登 的数据不可多次刊登");
			}


			usbListingService.deleteForBaseId(amazonPublishListing.getId());

			AmazonRequestProduct requestProduct = JSON.parseObject(amazonPublishListing.getPublishMessage(),AmazonRequestProduct.class);
			// new AmazonTemplateValidate().validateAttrRely(requestProduct.getTemplatesName(), requestProduct.getTemplatesName2(), requestProduct.getCategoryPropertyJson());
			
			//由于复制时，批次号是补复制的数据库批次号，需要设置为最新的批次号
			requestProduct.setBatchNo(batchNo);
			requestProduct.setSubPublishStatus(null);

			List<AmazonRequestProduct> plist = requestProduct.getVarRequestProductList();
			requestProduct.setIsMultiattribute(Boolean.FALSE);
			if(CollectionUtils.isNotEmpty(plist))
			{
				requestProduct.setIsMultiattribute(Boolean.TRUE);
			}
			
			// 这里转换一下，和张聪开的冲突，这里做了兼容
			MarketplaceId marketplaceIdObj = MarketplaceIdList.createMarketplace().get(
					requestProduct.getCountryCode());
			requestProduct.setCountryCode(marketplaceIdObj.getCountryCode());
			Empower empower = new Empower();
			empower.setStatus(1);
			// empower.setAccount(UserSession.getUserBaseUserInfo().getUsername());
			empower.setWebName(marketplaceIdObj.getMarketplaceId());
			empower.setThirdPartyName(requestProduct.getMerchantIdentifier());
			empower.setPlatform(2);
			empower = authorizationSellerService.selectAmazonAccount(empower);
			if( empower == null)
			{
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
			}
			amazonPublishListingService.saveOrUpdate(requestProduct, getLoginUserInformationByToken.getUserDTO().getTopUserLoginName(),
					AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_AWAIT, empower,new Date()); //等待刊登

			publishLogService.insert(JSON.toJSONString(requestProduct), PublishLogEnum.valueOf("PUBLISH"),
					UserSession.getUserBaseUserInfo().getUserid(), UserSession.getUserBaseUserInfo().getUsername(),amazonPublishListing.getId());

			StringBuilder errorFuture = new StringBuilder();
			for(AmazonPublishListing ting : amazonPublishListingList) //
			{

				ExecutorService executor = Executors.newFixedThreadPool(5);
				Future<String> futureResult = executor.submit(new ProcessXmlTask(requestProduct,amazonSubListingUtil));
				if(StringUtils.isNotEmpty(futureResult.get()))
				{
					errorFuture.append(futureResult.get());
				}
				executor.shutdown();

				//AmaznoExecutors.getInstance().addTask(new ProcessXmlTask(requestProduct));
				if(errorFuture.length() > 0) { // 如果有错误，以异常方式返回错误信息
					logger.error("生成xml失败：{}",errorFuture.toString());
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,errorFuture.toString());
				}
			}
			logger.debug("生成xml完成，准备更新更新SPU分类映射");
			logger.debug("requestProduct.getProductCategory()={}",requestProduct.getProductCategory());
            try{
                List<AmazonCategory> amazonCategories = amazonCategoryService.selectCategoryListById(requestProduct.getProductCategoryForInteger());
                remoteCommodityService.saveOrUpdateSpuCategory(requestProduct.getSpu(),"Amazon",
                        requestProduct.getCountryCode(),amazonCategories.get(0).getCategoryId(),requestProduct.getProductCategory1Path());
            }catch (Exception e){
                logger.error("保存并刊登回写分类映射给商品模块失败",e);
            }
			logger.debug("更新更新SPU分类映射完成");

		}catch(Exception e)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
	}

	@AspectContrLog(descrption="查询用户刊登列表",actionType= SysLogActionType.QUERY)
	@GetMapping("/findAll")
	@ApiOperation("查询用户刊登列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", value = "当前页码", name = "page", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "row", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", value = "刊登状态 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线", name = "publishStatus", dataType = "Integer"),
			@ApiImplicitParam(paramType = "query", value = "刊登状态 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线", name = "publishStatusList", dataType = "List"),
			@ApiImplicitParam(paramType = "query", value = "刊登类型 1：单属性 2：多属性", name = "publishType", dataType = "Integer"),
			@ApiImplicitParam(paramType = "query", value = "开始时间[yyyy-MM-dd]", name = "startCreateTime", dataType = "String"),
			@ApiImplicitParam(paramType = "query", value = "结束时间[yyyy-MM-dd]", name = "endCreateTime", dataType = "String"),
			@ApiImplicitParam(paramType = "query", value = "刊登账号", name = "publishAccount", dataType = "String"),
			@ApiImplicitParam(paramType = "query", value = "刊登站点", name = "publishSite", dataType = "String"),
			@ApiImplicitParam(paramType = "query", value = "asin", name = "asin", dataType = "String"),
			@ApiImplicitParam(paramType = "query", value = "在亚马逊平台上的sku", name = "platformSku", dataType = "String"),
			@ApiImplicitParam(paramType = "query", value = "品连sku", name = "plSku", dataType = "String"),
			@ApiImplicitParam(paramType = "query", value = "产品标题", name = "title", dataType = "String"),
			@ApiImplicitParam(paramType = "query", value = "0没有绑定品连sku,1绑定品连sku", name = "listingType", dataType = "Integer"),
			@ApiImplicitParam(paramType = "query", value = "0正常 1下架 2缺货 3少货 4未知 5侵权", name = "supplyStatus", dataType = "Integer"),
			@ApiImplicitParam(paramType = "query", value = "销售人员id", name = "saleUserId", dataType = "Integer"),
			@ApiImplicitParam(paramType = "query", value = "销售人员名字", name = "saleName", dataType = "String"),
			@ApiImplicitParam(paramType = "query", value = "在线修改状态 0初始值 1在线未修改 2修改成功 3修改失败 4修改中", name = "updateStatus", dataType = "Integer"),
			@ApiImplicitParam(paramType = "query", value = "时间范围查询类型[0:创建时间 1:发布时间　2：上线时间 3：更新时间 ] 查询参数", name = "timeType", dataType = "Integer")
	})
	public Page<AmazonPublishListing> findAll(@ApiIgnore AmazonPublishListing model, String page, String row) {
		try {
			UserAll userInfo = getLoginUserInformationByToken.getUserInfo();
			
			if(userInfo == null) {
	        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "该token已经失效，未登录或登录超时请重新登录，谢谢");
	        }
			//处理数据权限
			UserDTO userdTO=getLoginUserInformationByToken.getUserDTO();
			if (userdTO == null) {
	            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "获取当前用户失败");
	        }
			List<String> bindCode =new ArrayList<>();
			model.setPlAccount(userdTO.getTopUserLoginName());
			if(userdTO.getManage()) {
		       	//主账号

		     }else {
		   	    //子账号
		    	List<UserAccountDTO> binds = userdTO.getBinds();
	        	if(CollectionUtils.isEmpty(binds)) {
	        		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "权限异常");
	        	}
		   	    bindCode = binds.get(0).getBindCode();
				List<Empower> accounts = authorizationSellerService.getEmpowerByIds(strToInt(bindCode));
				if(accounts == null) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "权限异常");
				}
				bindCode.clear();
				for (Empower empower : accounts) {
					if(!bindCode.contains(empower.getPinlianAccount())) {
						bindCode.add(empower.getAccount());
					}
				}
				model.setPublishAccounts(bindCode);
		    	 
		    }
			Page.builder(page, row);
			if(model.getPublishStatus() != null && model.getPublishStatus() == 3) {
				model.setTemp(msg);
			}
			
			 Page<AmazonPublishListing> amazonPublishListingPage = amazonPublishListingService.page(model);
			 //设置销售人员名字
			 setSaleName(amazonPublishListingPage,model.getSaleName());
			return amazonPublishListingPage;
		} catch (Exception e) {
			logger.error("查询亚马逊刊登信息异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询亚马逊刊登信息异常");
		}
	}

	private void setSaleName(Page<AmazonPublishListing> amazonPublishListingPage,String saleName) {
		 PageInfo<AmazonPublishListing> pageInfo = amazonPublishListingPage.getPageInfo();
		 List<AmazonPublishListing> list = pageInfo.getList();
		 if(StringUtils.isNotBlank(saleName)) {
			 list.forEach(amazon->{
				 amazon.setSaleName(saleName);
			 });
		 }else {
			 List<Integer> saleUserIds=new LinkedList<Integer>();
			 for (AmazonPublishListing amazonPublishListing : list) {
				 if(amazonPublishListing.getSaleUserId() != null) {
					 saleUserIds.add(amazonPublishListing.getSaleUserId());
				 } 
			}
			 if(saleUserIds.size()>=1) {
				 Integer[] userIdArray =  saleUserIds.toArray(new Integer[saleUserIds.size()]);
					String strJson = remoteUserService.getSupplierList(userIdArray, 1);
					logger.info("刊登列表批量请求用户返回数据：{}", strJson);
					String userJsonArr = Utils.returnRemoteResultDataString(strJson, "用户服务异常");
					List<JSONObject> array = JSONObject.parseArray(userJsonArr, JSONObject.class);
					for (JSONObject jsonObject : array) {
						for(AmazonPublishListing amazon: list) {
							if(amazon.getSaleUserId() != null) {
								if(jsonObject.getIntValue("userId")==amazon.getSaleUserId().intValue()) {
									amazon.setSaleName(jsonObject.getString("userName"));
								}
							}
						}
					}
			 }
		 }
	}

	@AspectContrLog(descrption="删除亚马逊刊登",actionType= SysLogActionType.DELETE)
	@DeleteMapping("/delete/{id}")
	@ApiOperation("删除亚马逊刊登")
	public void delete(@ApiParam(value = "被删除的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "被删除的id不能为空");
		AmazonPublishListing amazonPublishListing = amazonPublishListingService.selectByPrimaryKey(id);
		if (amazonPublishListing == null)
			return;
		if (!amazonPublishListing.getPlAccount().equalsIgnoreCase(getLoginUserInformationByToken.getUserDTO().getTopUserLoginName()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能删除其他用户的刊登");
		if(amazonPublishListing.getPublishStatus() == AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_PUBLISHING || amazonPublishListing.getPublishStatus() ==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE
				||amazonPublishListing.getPublishStatus() == AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE||amazonPublishListing.getPublishStatus() == AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_AWAIT	
				||amazonPublishListing.getPublishStatus() == AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601, "数据状态改变不能删除");
			}
		try {
			if(amazonPublishListing.getPublishStatus()==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT) {
				//草稿
				deleteListingAndSubListing(id);
				return ;
			}
			AmazonRequestProduct product = JSON.parseObject(amazonPublishListing.getPublishMessage(), AmazonRequestProduct.class);
			List<AmazonRequestProduct> varProductList = product.getVarRequestProductList();
			if(CollectionUtils.isEmpty(varProductList)) {
				//单属性直接删除
				deleteListingAndSubListing(id);
				return ;
			}
			
			AmazonPublishSubListing query=new AmazonPublishSubListing();
			query.setListingId(id);
			query.setParentType(AmazonConstants.PARENT_TYPE_NO);
			query.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_FAILED);
			List<AmazonPublishSubListing> failList = amazonPublishSubListingService.selectPage(query);
			//子表删除状态是失败的数据
			Set<String> setSku=new HashSet<>();
			List<AmazonRequestProduct> removeList=new LinkedList<>();
			List<Long> ids=new LinkedList<>();
			if(CollectionUtils.isNotEmpty(failList)) {
				for (AmazonPublishSubListing amazonPublishSubListing : failList) {
					if(amazonPublishSubListing.getMsgType().equals(AmazonPostMethod.POST_RELATIONSHIP_DATA)) {
						//关系不属于子也不属于主
						continue;
					}
					setSku.add(amazonPublishSubListing.getSku());
					ids.add(amazonPublishSubListing.getId());
				}
				//子体全部失败
				if(varProductList.size()==setSku.size()) {
					deleteListingAndSubListing(id);
					return ;
				}
				if(CollectionUtils.isEmpty(setSku)) {
					return ;
				}
				//删除失败的sublisting
				amazonPublishSubListingService.deleteByListingIdAndSkus(id, setSku);
					//修改publicshMessage
					if(CollectionUtils.isNotEmpty(varProductList)) {
						for (AmazonRequestProduct requestProductList : varProductList) {
							for(String sku : setSku) {
								if(requestProductList.getSku().equals(sku)) {
									removeList.add(requestProductList);
								}
							}
						}
					}
					varProductList.removeAll(removeList);
					product.setVarRequestProductList(varProductList);
					ObjectMapper mapper = new ObjectMapper();
					mapper.setSerializationInclusion(Include.NON_NULL);
					String publishMessageJson="";
					try {
						publishMessageJson=mapper.writeValueAsString(product);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					amazonPublishListing.setPublishMessage(publishMessageJson);
					amazonPublishListingService.updateByPrimaryKeySelective(amazonPublishListing);
				}
			
			//刊登操作日志
			publishLogService.insert(amazonPublishListing.getPublishMessage(), PublishLogEnum.valueOf("DELETE"),
					UserSession.getUserBaseUserInfo().getUserid(), UserSession.getUserBaseUserInfo().getUsername(),amazonPublishListing.getId());
		} catch (Exception e) {
			logger.error("删除亚马逊刊登异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除亚马逊刊登异常");
		}
	}

	/**
	 * 删除主表和字表数据
	 * @param id
	 */
	private void deleteListingAndSubListing(Long id) {
		amazonPublishListingService.deleteByPrimaryKey(id);
		amazonPublishSubListingService.deleteForBaseId(id);
	}
	
	@AspectContrLog(descrption="复制亚马逊刊登",actionType= SysLogActionType.ADD)
	@PostMapping("/copy/{id}")
	@ApiOperation("复制亚马逊刊登")
	public void copy(@ApiParam(value = "被复制的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "被复制的id不能为空");
		AmazonPublishListing amazonPublishListing = amazonPublishListingService.selectByPrimaryKey(id);
		if (amazonPublishListing == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "被复制的id不存在");
		/*if (!amazonPublishListing.getPlAccount().equalsIgnoreCase(getLoginUserInformationByToken.getUserDTO().getTopUserLoginName()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能复制其他用户的刊登");*/
		try {
//			amazonPublishListingService.copyPublish(id);
			
	    	//------------ 处理参数json串 ----------------
	    	AmazonRequestProduct<?> product = JSON.parseObject(amazonPublishListing.getPublishMessage(), AmazonRequestProduct.class);
	    	//bug:2534
	    	// 1:sku、partNumber重新生成
	    	String sku = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	    	product.setSku(sku);
	    	product.setMfrPartNumber(sku);
	    	// 2：清空upc
	    	product.setStandardProductID("");
	    	product.setSubPublishStatus(null);

	    	if(CollectionUtils.isNotEmpty(product.getVarRequestProductList()))
	    	{
	    		List<AmazonRequestProduct> varProductList = product.getVarRequestProductList();
	    		for(AmazonRequestProduct<?> varProduct : varProductList)
	    		{
	    			String vsku = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

	    	    	// 1:sku、partNumber重新生成
	    	    	varProduct.setSku(vsku);
	    	    	varProduct.setMfrPartNumber(vsku);
	    	    	varProduct.setStandardProductID("");// 2：清空upc
	    	    	varProduct.setSubPublishStatus(null);
	    		}
	    	}
	    	product.setId(null);
	    	product.setBatchNo(UUID.randomUUID().toString().replaceAll("-", ""));
	    	//------------ 处理参数json串  end ----------------

			Empower empower = new Empower();
			empower.setStatus(1);
			empower.setWebName(MarketplaceIdList.createMarketplace().get(product.getCountryCode()).getMarketplaceId());
			empower.setThirdPartyName(product.getMerchantIdentifier());
			empower.setPlatform(2);
			empower = authorizationSellerService.selectAmazonAccount(empower);
			if( empower == null)
			{
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"找不到授权信息");
			}

			//处理message里面的asin
			product= AmazonBachUpdate.cleanAsin(product, amazonPublishListing.getPublishType());
			
			amazonPublishListing.setBatchNo(product.getBatchNo());
			amazonPublishListing.setAsin(null);
			amazonPublishListing.setId(null);
			amazonPublishListing.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT);
			amazonPublishListing.setUpdateStatus(AmazonPublishUpdateStatus.INIT);
			amazonPublishListing.setPlatformSku(product.getSku());
			amazonPublishListing.setCreateTime(new Date());
			amazonPublishListing.setUpdateTime(new Date());
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			String publishMessageJson="";
			try {
				publishMessageJson=mapper.writeValueAsString(product);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			amazonPublishListing.setPublishMessage(publishMessageJson);
			amazonPublishListing.setRemark(amazonPublishListing.getRemark() + "copy From:"+id);
			amazonPublishListing.setId(null);
			amazonPublishListing.setLogisticsCode(amazonPublishListing.getLogisticsCode());
			amazonPublishListingService.insertSelective(amazonPublishListing);

			/*AmazonPublishListing tempObj = amazonPublishListingService.saveOrUpdate(product,  userLogin,  AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT, empower); //草稿
			usbListingService.deleteForBaseId(amazonPublishListing.getId());
			*/
			//刊登操作日志
			publishLogService.insert(amazonPublishListing.getPublishMessage(), PublishLogEnum.valueOf("COPY"),
					UserSession.getUserBaseUserInfo().getUserid(), UserSession.getUserBaseUserInfo().getUsername(),amazonPublishListing.getId());
			try {
				product.setId(amazonPublishListing.getId());
				ExecutorService executor = Executors.newFixedThreadPool(1);
				ProcessXmlDraftTask processXmlDraftTask = new ProcessXmlDraftTask(product,amazonSubListingUtil);
				Future<String> futureResult = executor.submit(processXmlDraftTask);
				String errorMsg = futureResult.get();
				executor.shutdown();

				//AmaznoExecutors.getInstance().addTask(new ProcessXmlTask(requestProduct));
				if(errorMsg != null && errorMsg.length() > 0)
				{
					logger.warn("生成sub数据时异常，中止线操作，数据被手动删除，被删除的ID为：{}",product.getId());
					// amazonPublishListingService.deleteByPrimaryKey(amazonPublishListing.getId());
					amazonPublishSubListingService.deleteForBaseId(product.getId());
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,errorMsg);
				}
			}catch (Exception e){
				logger.error("生成sub数据时异常",e);
			}
			
			
		} catch (Exception e) {
			logger.error("复制亚马逊刊登异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "复制亚马逊刊登异常");
		}
	}

	@AspectContrLog(descrption="获取亚马逊刊登草稿数据",actionType= SysLogActionType.QUERY)
	@PostMapping("/view/{id}")
	@ApiOperation("获取亚马逊刊登草稿数据")
	public RespPublishEditer  viewById(@ApiParam(value = "当前数据的id", name = "id", required = true)
										@PathVariable Long id) {
		if (id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");

		AmazonPublishListing amazonPublishListing = amazonPublishListingService.selectByPrimaryKey(id);
		if (amazonPublishListing == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");

		String username = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
		if(!amazonPublishListing.getPlAccount().equalsIgnoreCase(username))
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "无权限查看该数据");
		}
		AmazonPublishSubListing subListing=new AmazonPublishSubListing();
		subListing.setListingId(id);
		RespPublishEditer result = new RespPublishEditer(); // JSON.parseObject(amazonPublishListing.getExt(), RespPublishEditer.class);
		result.setId(amazonPublishListing.getId());
		result.setExt(amazonPublishListing.getExt());
		result.setStatus(/*AmazonPublishListStatus.getStatus(*/amazonPublishListing.getPublishStatus());
		result.setBatchNo(amazonPublishListing.getBatchNo());
		result.setPublishMessage(amazonPublishListing.getPublishMessage());
		result.setLogisticsType(amazonPublishListing.getLogisticsType());
		result.setWarehouseId(amazonPublishListing.getWarehouseId());
		return result;
	}


	@AspectContrLog(descrption="获取亚马逊刊登详情",actionType= SysLogActionType.QUERY)
	@PostMapping("/view-details/{id}")
	@ApiOperation("获取亚马逊刊登详情数据")
	public RespPublishEditer  viewDetailsById(@ApiParam(value = "当前数据的id", name = "id", required = true)
										@PathVariable Long id) {
		if (id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");

		AmazonPublishListing amazonPublishListing = amazonPublishListingService.selectByPrimaryKey(id);
		if (amazonPublishListing == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "id不存在");

		String username = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
		if(!amazonPublishListing.getPlAccount().equalsIgnoreCase(username))
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "无权限查看该数据");
		}
		RespPublishEditer result = new RespPublishEditer(); // JSON.parseObject(amazonPublishListing.getExt(), RespPublishEditer.class);
		result.setId(amazonPublishListing.getId());
		// result.setExt(amazonPublishListing.getExt());
		result.setStatus(/*AmazonPublishListStatus.getStatus(*/amazonPublishListing.getPublishStatus());
		result.setBatchNo(amazonPublishListing.getBatchNo());
		String publishMessage = amazonPublishListing.getPublishMessage();
		if(StringUtils.isNotBlank(publishMessage)){
			try {
				JSONObject object = JSONObject.parseObject(publishMessage);
				if(StringUtils.isBlank(object.getString("plSku"))){
					object.put("plSku","亚马逊同步数据");
					publishMessage = JSONObject.toJSONString(object);
				}
			}catch (Exception e){
				logger.error("ID为："+id+" 的刊登数据的PublishMessage有误",e);
			}
		}
		result.setPublishMessage(publishMessage);
		//result.setExt(amazonPublishListing.getExt());
		return result;
	}

	@AspectContrLog(descrption="修改备注",actionType= SysLogActionType.UDPATE)
	@PostMapping("/udpate-remark")
	@ApiOperation("修改备注")
	public void  udpateRemarkById(@ApiParam(value = "当前数据的id", name = "id", required = true) @RequestParam Long id,
			@ApiParam(value = "备注", name = "remark", required = true) @RequestParam String  remark) {
		if (id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");

		AmazonPublishListing updateObj = new AmazonPublishListing();
		try {
			updateObj.setId(id);
			updateObj.setRemark(remark);
			int rows = amazonPublishListingService.updateByPrimaryKeySelective(updateObj);
			if(rows <= 0)
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更新记录数0条");
		} catch (Exception e) {
			logger.error("修改备注失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更新记录失败");
		}
	}

	@AspectContrLog(descrption="查看刊登日志",actionType= SysLogActionType.QUERY)
	@PostMapping("/view-log/{id}")
	@ApiOperation(value="查看刊登日志" ,notes="msgType-> 商品上报方法类型：   _POST_PRODUCT_DATA_，库存上报方法类型：   _POST_INVENTORY_AVAILABILITY_DATA_，图片上报方法类型：  _POST_PRODUCT_IMAGE_DATA_ ，"
			+ "价格上报方法类型 ：  _POST_PRODUCT_PRICING_DATA_，关系上报方法类型：_POST_PRODUCT_RELATIONSHIP_DATA_"
			+ "CompleteStatus=1：已完成，2：失败，3：需要继续上报（刊登中）")
	public List<AmazonPublishSubListing> viewLogById(
			@ApiParam(value = "当前数据的id", name = "id", required = true) @PathVariable Long id) {
		if (id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id不能为空");

		AmazonPublishSubListing subListQuery = new AmazonPublishSubListing();
		subListQuery.setListingId(id);
		List<AmazonPublishSubListing> result = usbListingService.selectViewReportResultByListingId(id); // usbListingService.selectPage(subListQuery);
		List<AmazonPublishSubListing> list = new ArrayList<>();
		AmazonPublishSubListing as = null;
		boolean flag=false;
		for(AmazonPublishSubListing sub : result) {
			if(StringUtils.isBlank(sub.getResultMessage()) || "编辑未刊登的数据,或者亚马逊同步数据".equals(sub.getResultMessage())) {
				flag=true;
			}
			as = new AmazonPublishSubListing();
			as.setMsgType(sub.getMsgType());
			as.setResultMessage(sub.getResultMessage());
			as.setUpdateTime(sub.getUpdateTime());
			as.setCompleteStatus(sub.getCompleteStatus());
			list.add(as);
		}
		if(flag) {
			list.clear();
			return list;
		}
		
		return list;
	}

	/**
	 * 根据刊登id查询操作日志
	 */
	@ApiOperation(value="根据刊登id查询操作日志",notes="根据刊登id查询操作日志")
	@GetMapping("/getByPublishId")
	public List<PublishLog> getByPublishId(Long publishId){
		List<PublishLog> logs = publishLogService.getByPublishId(publishId);
		return logs;
	}
	
	
	@AspectContrLog(descrption="批量编辑",actionType= SysLogActionType.UDPATE)
	@PostMapping("/batchUpdate")
	@ApiOperation(value="批量编辑",notes="列表页面批量编辑")
	public void batchUpdate(@Valid BatchUpdateVO batchUpdateVO,BindingResult bindingResult) {
		if(UserSession.getUserBaseUserInfo()== null)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}
		if(bindingResult.hasErrors())
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,
			bindingResult.getAllErrors().get(0).getDefaultMessage());
		}
		
		amazonPublishListingService.batchUpdate(batchUpdateVO,UserSession.getUserBaseUserInfo().getUsername(), UserSession.getUserBaseUserInfo().getUserid());
		
	}
	
	
	@AspectContrLog(descrption="在线编辑",actionType= SysLogActionType.UDPATE)
	@PostMapping("/updateOnline")
	@ApiOperation(value="在线编辑",notes="在线编辑")
	public void updateOnline(@RequestBody AmazonRequestProduct requestProduct) {
		requestProduct.setSubPublishStatus(null);
		if(UserSession.getUserBaseUserInfo()== null)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406,"获取当前用户失败");
		}
		amazonPublishListingService.updateOnline(requestProduct,UserSession.getUserBaseUserInfo().getUsername(), UserSession.getUserBaseUserInfo().getUserid());
	}
	
	
	/**
	 * 校验店铺权限
	 * @param empowerId
	 */
	private void checkAuthByEmpId(Integer empowerId) {
		Empower empower = empowerService.getEmpowerById(empowerId);
		if(empower == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"店铺不存在");
		}
		if(empower.getStatus() != 1) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100601,"店铺授权异常");
		}
	}
	
	@AspectContrLog(descrption="更新图片",actionType= SysLogActionType.UDPATE)
	//@PostMapping("/updateImg/{listingId}")
	@PostMapping("/updateImg")
	@ApiOperation(value="更新图片",notes="更新图片")
	public void updateImg(/*@PathVariable Long listingId,*/ @RequestBody AmazonImageRequest images) {
		if(images.getImages() == null || StringUtils.isBlank(images.getListingId()))
		{
			// CollectionUtils
			 throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
		}
		
		Long listingId =Long.valueOf(images.getListingId());
		// 检查数据是否有效
		AmazonPublishListing amazonPublishListing = amazonPublishListingService.selectByPrimaryKey(listingId);
		if(amazonPublishListing == null)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600);
		}
		// 不是在线状态的都不可以更新
		if(amazonPublishListing.getPublishStatus() != AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL
				&& amazonPublishListing.getPublishStatus() != AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"当前状态不可更新");
		}
		try
		{
			List<AmazonPublishSubListing> subListing = new ArrayList<AmazonPublishSubListing>();
			
			AmazonRequestProduct<?> imageProduct = new AmazonRequestProduct<>();
			imageProduct.setCountryCode(amazonPublishListing.getPublishSite());
			imageProduct.setMerchantIdentifier(amazonPublishListing.getMerchantIdentifier());
			//为空单属性
			if(CollectionUtils.isEmpty(images.getImages().getSubImage())) {
			// 主
			if(CollectionUtils.isNotEmpty(images.getImages().getMainImage()))
			{
				imageProduct.setImages(images.getImages().getMainImage());
				imageProduct.setSku(imageProduct.getImages().get(0).getSKU());
				
				AmazonPublishSubListing sublist = new AmazonPublishSubListing();
				sublist.setSku(imageProduct.getSku());
				List<AmazonPublishSubListing> queryList =  amazonPublishSubListingService.selectPage(sublist);
				if(CollectionUtils.isEmpty(queryList))
				{
					logger.error("更新图片时，找不到sku：{}",imageProduct.getSku());
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600);
				}
				imageProduct.setPlSku(queryList.get(0).getPlSku());
				List<ProductImage> invokeValues = amazonTemplateGenerate.generagteProductImage(imageProduct, Boolean.FALSE);
				for(ProductImage image : invokeValues)
				{
					subListing.add(getNewSubListing(imageProduct,listingId,AmazonPostMethod.POST_IMAGE_DATA,Boolean.TRUE,image,new String[] {"^SKU"}));
				}
			}
			}
			//变体
			if(CollectionUtils.isNotEmpty(images.getImages().getSubImage()))
			{
				for(List<ProductImage> list : images.getImages().getSubImage())
				{
					if(CollectionUtils.isEmpty(list))
					{
						continue ;
					}
					imageProduct.setImages(list);
					imageProduct.setSku(list.get(0).getSKU());
					List<ProductImage> invokeValues = amazonTemplateGenerate.generagteProductImage(imageProduct, Boolean.FALSE);
					
					AmazonPublishSubListing sublist = new AmazonPublishSubListing();
					sublist.setSku(imageProduct.getSku());
					List<AmazonPublishSubListing> queryList =  amazonPublishSubListingService.selectPage(sublist);
					if(CollectionUtils.isEmpty(queryList))
					{
						logger.error("更新图片时，找不到sku：{}",imageProduct.getSku());
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600);
					}
					imageProduct.setPlSku(queryList.get(0).getPlSku());
					
					for(ProductImage image : invokeValues)
					{
						subListing.add(getNewSubListing(imageProduct,listingId,AmazonPostMethod.POST_IMAGE_DATA,Boolean.TRUE,image,new String[] {"^SKU"}));
					}
				}
			}
			//批量删除改listing下type为img的数据
			amazonPublishSubListingService.deleteByListingIdAndMsgType(images.getListingId(),AmazonPostMethod.POST_IMAGE_DATA);
			amazonSubListingUtil.setPlSkuStatusAndCount(subListing, images.getWarehouseId());
			logger.debug("更新图片批量添加参数{}",JSON.toJSONString(subListing));
			if(CollectionUtils.isEmpty(subListing)) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"没有需要更新的图片");
			}
			amazonPublishSubListingService.insertBatch(subListing);
		
			
			AmazonRequestProduct publishListingObj = JSON.parseObject(amazonPublishListing.getPublishMessage(), AmazonRequestProduct.class);
			AmazonRequestProduct setImgs = AmazonBachUpdate.setImgs(publishListingObj, amazonPublishListing.getPublishType(), images);
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			String publishMessageJson="";
			try {
				publishMessageJson=mapper.writeValueAsString(setImgs);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		//更新主表数据状态为7，7状态也是在线状态，只不过7是代表更新在线状态的数据需要重新刊登
		AmazonPublishListing tempObj = new AmazonPublishListing();
		tempObj.setPublishStatus(images.getType()==0?AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH:AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_AWAIT);
		tempObj.setId(listingId);
		tempObj.setPublishMessage(publishMessageJson);//同步message字段
		if(tempObj.getPublishStatus()==AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH) {
			tempObj.setUpdateStatus(AmazonPublishUpdateStatus.UPDATE_GOING);
		}
		amazonPublishListingService.updateByPrimaryKeySelective(tempObj);
		
		}catch(Exception e)
		{
			logger.error("",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}
	
	
	private AmazonPublishSubListing getNewSubListing(AmazonRequestProduct<?> requestProduct,
			Long parentId,
			String msgType,
			boolean parentType,
			Object t,String [] cDataElements)
	{
		ClassXmlUtil xmlutil = new ClassXmlUtil();
		//RedisUtils redisUtils = (RedisUtils) ApplicationContextProvider.getBean("redisUtils");
		AmazonPublishSubListing subListing  = new AmazonPublishSubListing();
		subListing.setListingId(parentId);
		subListing.setMsgType(msgType);
		subListing.setResultMessage(AmazonConstants.RESPORT_RESULT_AWAIT);
		subListing.setProcessStatus(AmazonConstants.RESPORT_RESULT_AWAIT);
		subListing.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_AWAIT);
		subListing.setMarketplaceId(MarketplaceIdList.createMarketplace().get(requestProduct.getCountryCode()).getMarketplaceId());
		subListing.setMessageId(redisUtils.incrt(AmazonConstants.REDIS_XML_MESSAGE_ID));
		subListing.setParentType(parentType?0:1);
		subListing.setMerchantId(requestProduct.getMerchantIdentifier());
		
		if(cDataElements == null || cDataElements.length <= 0)
		{
			subListing.setXmls(xmlutil.toXML(t));
		}else
		{
			subListing.setXmls(xmlutil.toXML(t,cDataElements));
		}
		
		logger.debug(subListing.getXmls());
		subListing.setSku(requestProduct.getSku());
		subListing.setPlSku(requestProduct.getPlSku());
		return subListing;
	}
	
	private List<Integer> strToInt(List<String> bindCode) {
		   List<Integer> empowerIds=new ArrayList<>();
		   for (String str : bindCode) {
			   empowerIds.add(Integer.parseInt(str));
			}
		   return empowerIds;
	}




	@AspectContrLog(descrption="给没有sku的添加上sku",actionType= SysLogActionType.UDPATE)
	@PostMapping("/updateSubSku")
	@ApiOperation(value="给没有sku的添加上sku",notes="给没有sku的添加上sku")
	public void updateSubSku() {
		setAmazonSubSkuUtils.setSke();
	}

	@ApiOperation(value="提供给商品站点信息",notes="提供给商品站点信息")
	@GetMapping("/getMarketplaces")
	public Map<String,MarketplaceId> getMarketplace() {
		Map<String,MarketplaceId> marketplaceres = new HashMap<String,MarketplaceId>();
		
		MarketplaceId CA = new MarketplaceId();
		CA.setCountryCode("CA");
		CA.setCountryName("加拿大");
		CA.setUri("https://mws.amazonservices.ca");
		CA.setMarketplaceId("A2EUQ1WTGCTBG2");
		CA.setCurrency("CAD");
		marketplaceres.put(CA.getCountryCode(), CA);
		
		MarketplaceId MX = new MarketplaceId();
		MX.setCountryCode("MX");
		MX.setCountryName("墨西哥");
		MX.setUri("https://mws.amazonservices.com.mx");
		MX.setMarketplaceId("A1AM78C64UM0Y8");
		MX.setCurrency("MXN");
		marketplaceres.put(MX.getCountryCode(), MX);
		
		MarketplaceId US = new MarketplaceId();
		US.setCountryCode("US");
		US.setCountryName("美国");
		US.setUri("https://mws.amazonservices.com");
		US.setMarketplaceId("ATVPDKIKX0DER");
		US.setCurrency("USD");
		marketplaceres.put(US.getCountryCode(), US);
		
		MarketplaceId DE = new MarketplaceId();
		DE.setCountryCode("DE");
		DE.setCountryName("德国");
		DE.setUri("https://mws-eu.amazonservices.com");
		DE.setMarketplaceId("A1PA6795UKMFR9");
		DE.setCurrency("EUR");
		marketplaceres.put(DE.getCountryCode(), DE);
		
		MarketplaceId ES = new MarketplaceId();
		ES.setCountryCode("ES");
		ES.setCountryName("西班牙");
		ES.setUri("https://mws-eu.amazonservices.com");
		ES.setMarketplaceId("A1RKKUPIHCS9HS");
		ES.setCurrency("EUR");
		marketplaceres.put(ES.getCountryCode(), ES);
		
		MarketplaceId FR = new MarketplaceId();
		FR.setCountryCode("FR");
		FR.setCountryName("法国");
		FR.setUri("https://mws-eu.amazonservices.com");
		FR.setMarketplaceId("A13V1IB3VIYZZH");
		FR.setCurrency("EUR"); 
		marketplaceres.put(FR.getCountryCode(), FR);
		
		MarketplaceId GB = new MarketplaceId();
		GB.setCountryCode("GB");
		GB.setCountryName("英国");
		GB.setUri("https://mws-eu.amazonservices.com");
		GB.setMarketplaceId("A1F83G8C2ARO7P");
		GB.setCurrency("GBP");
		marketplaceres.put(GB.getCountryCode(), GB);
		 
		MarketplaceId IT = new MarketplaceId();
		IT.setCountryCode("IT");
		IT.setCountryName("意大利"); 
		IT.setUri("https://mws-eu.amazonservices.com");
		IT.setMarketplaceId("APJ6JRA9NG5V4");
		IT.setCurrency("EUR");
		marketplaceres.put(IT.getCountryCode(), IT);
		 
		MarketplaceId AU = new MarketplaceId();
		AU.setCountryCode("AU");
		AU.setCountryName("澳大利亚");
		AU.setUri("https://mws.amazonservices.com.au");
		AU.setMarketplaceId("A39IBJ37TRP1C6");
		AU.setCurrency("AUD");
		marketplaceres.put(AU.getCountryCode(), AU);
		
		MarketplaceId JP = new MarketplaceId();
		JP.setCountryCode("JP");
		JP.setCountryName("日本");
		JP.setUri("https://mws.amazonservices.jp");
		JP.setMarketplaceId("A1VC38T7YXB528");
		JP.setCurrency("JPY");
		marketplaceres.put(JP.getCountryCode(), JP);
		
		return marketplaceres;
	}

	@ApiOperation(value="计算最终售价",notes="计算最终售价")
	@GetMapping("/disposePrice")
	public List<CommodityDTO> disposePrice(AmazonDisposePriceVO amazonDisposePriceVO) {
		List<CommodityDTO> disposePrice = amazonPublishListingService.disposePrice(amazonDisposePriceVO);
		return disposePrice;
	}
	
	@ApiOperation(value="根据当前用户id获取到相关的主子账号信息哦",notes="根据当前用户id获取到相关的主子账号信息哦")
	@GetMapping("/getUsersInfo")
    public List<UserVO>  getUsersInfo() {
		UserDTO userdTO=getLoginUserInformationByToken.getUserDTO();
		if (userdTO == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "获取当前用户失败");
        }
		List<UserVO> users=new ArrayList<UserVO>();
		UserVO user=new UserVO();
		user.setUserName(userdTO.getUserName());
		user.setUserId(userdTO.getUserId());
		users.add(user);
		if(userdTO.getManage()) {
			users.addAll(getUsers(userdTO.getUserId()));
		}else {
			List<UserVO> userVos = getUsers(userdTO.getTopUserId());
            userVos=userVos.stream().filter(u -> u.getUserId().intValue() != userdTO.getUserId()).collect(Collectors.toList());
            UserVO topUser=new UserVO();
            topUser.setUserName(userdTO.getTopUserLoginName());
            topUser.setUserId(userdTO.getTopUserId());
    		users.add(topUser);
            users.addAll(userVos);
		}
		return users;
	}


	private List<UserVO> getUsers(Integer userId){
		String userStr = remoteUserService.getChildAccount(userId, null, "1");
		logger.info("刊登时请求用户返回数据：{}", userStr);
		String jsonStr = Utils.returnRemoteResultDataString(userStr, "用户服务异常");
		return JSONObject.parseArray(jsonStr, UserVO.class);
	}

	@AspectContrLog(descrption="批量复制",actionType= SysLogActionType.UDPATE)
	@PostMapping("/batchCopy/{type}/{listingId}")
	@ApiOperation(value="批量复制",notes="批量复制")
	public void batchCopy( @ApiParam(value = "复制类型[more:多账号  shire:欧洲共享 ] ", name = "type",required = true) @PathVariable("type") String type,
						   @ApiParam(value = "要复制的listingId", name = "listingId",required = true) @PathVariable("listingId") Long listingId,
						   @ApiParam(value = "目标授权ID列表 ", name = "empowerIds",required = true) @RequestBody List<Integer> empowerIds) {
		UserDTO userDTO = getLoginUserInformationByToken.getUserDTO();
		Integer topUserId = userDTO.getTopUserId();
		String topUserLoginName = userDTO.getTopUserLoginName();
		if( !type.equalsIgnoreCase("more") && !type.equalsIgnoreCase("shire")){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"复制类型有误");
		}
		AmazonPublishListing listing = amazonPublishListingService.selectByPrimaryKey(listingId);
		if(listing == null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"目标数据不存在");
		}
		//eurMarketplaces
		List<Empower> empowerByIds = authorizationSellerService.getEmpowerByIds(empowerIds);
		for(Empower e : empowerByIds){
			if(!e.getPinlianAccount().equalsIgnoreCase(topUserLoginName)){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"目标店铺非法");
			}
		}
		AmazonTemplateRule defaultTemplate = amazonTemplateRuleService.getByList(new AmazonTemplateRule() {{
			setDefaultTemplate(AmazonTemplateEnums.DefaultTemplate.GLOBAL_DEFAULT.getType());
		}}).get(0);

		JSONObject commodity = null;
		AmazonRequestProduct<?> sourcePro = null;
		String SPU= null;
		if(StringUtils.isNotBlank(listing.getPublishMessage())){
			sourcePro = JSONObject.parseObject(listing.getPublishMessage(),AmazonRequestProduct.class);
			commodity = amazonTemplateRuleService.getCommodity(sourcePro.getSpu());
			SPU = sourcePro.getSpu();
		}

		JSONObject freightTrial = null;
		if(listing.getWarehouseId()!= null && listing.getLogisticsType() != null){
			freightTrial = new JSONObject();
			freightTrial.put("platformType","2");  //代表亚马逊
			freightTrial.put("searchType",listing.getLogisticsType());
			freightTrial.put("warehouseId",listing.getWarehouseId());
			ArrayList<JSONObject> skuList = new ArrayList<>();
			if(StringUtils.isNotBlank(listing.getPublishMessage())){
				if(sourcePro.getVarRequestProductList() != null && sourcePro.getVarRequestProductList().size() > 0){
					List<AmazonRequestProduct> varRequestProductList = sourcePro.getVarRequestProductList();
					for (AmazonRequestProduct sub: varRequestProductList){
						if(StringUtils.isNotBlank(sub.getPlSku())){
							JSONObject subo = new JSONObject();
							subo.put("sku",sub.getPlSku());
							subo.put("num",1);
							skuList.add(subo);
						}
					}
				}else {
					JSONObject object = new JSONObject();
					object.put("sku",listing.getPlSku());
					object.put("num",1);
					skuList.add(object);
				}
			}
			freightTrial.put("skuList",skuList);
		}

		if(type.equalsIgnoreCase("more")){
			amazonBatchCopyUtils.copyMore(listing,empowerByIds,defaultTemplate,freightTrial, commodity,SPU);
		}else if(type.equalsIgnoreCase("shire")){
			amazonBatchCopyUtils.copyShire(listing,empowerByIds,defaultTemplate,freightTrial, commodity);
		}
	}


	@AspectContrLog(descrption="获取引入数据",actionType= SysLogActionType.QUERY)
	@GetMapping("/getAmazonReference")
	@ApiOperation(value="获取引入数据",notes="获取引入数据")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", value = "当前页码", name = "page", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "row", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", value = "站点ID", name = "marketplaceId", dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query", value = "亚马逊卖家ID", name = "merchantId", dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query", value = "品连sku", name = "plSku", dataType = "String",required = true)
	})
	public Page<AmazonReference> getAmazonReferenceByPage(@ApiIgnore AmazonPublishSubListing model, String page, String row){
		try {
			Page.builder(page, row);
			Page<AmazonReference> amazonReferenceByPage = amazonPublishListingService.getAmazonReferenceByPage(model);
			return amazonReferenceByPage;
		}catch (Exception e){
			logger.error("获取引入数据异常",e);
			return null;
		}
	}




	@AspectContrLog(descrption="获取全部模板信息",actionType= SysLogActionType.QUERY)
	@PostMapping("/getAllCat")
	@ApiOperation(value="获取全部模板信息",notes="获取全部模板信息")
	public Object getAllCat(){
		try {
			ClassReflectionUtil cru = new ClassReflectionUtil();
			List<AmazonNode> categoryOne = cru.getFirstXsdTemplateCategory();

			ClassReflectionUtil cru1 = new ClassReflectionUtil();
			//List<AmazonNode> NodeList = cru.getNextXsdTemplateCategory(classPath);
			HashMap<String, List<String>> resulte = new HashMap<>();
			ArrayList<String> errorList = new ArrayList<>();
			for(AmazonNode node : categoryOne){

				try {
					String classPath = this.getClassPath(node);
					List<AmazonNode> nextXsdTemplateCategory = cru1.getNextXsdTemplateCategory(classPath);
					ArrayList<String> strings = new ArrayList<>();
					for(AmazonNode nextNode : nextXsdTemplateCategory){  //com.rondaful.cloud.seller.generated.AutoAccessory$ProductType
						strings.add(nextNode.getFieldName());
					}
					resulte.put(node.getFieldName(),strings);
				}catch (Exception e){
					errorList.add(node.getFieldName());
				}
			}
			resulte.put("errorParent",errorList);

			return resulte;

		}catch (Exception e){
			logger.error("",e);
			return null;
		}
	}

	private String getClassPath(AmazonNode node){
		String path = node.getClassPath() + "$ProductType";
		if(node.getFieldName().equalsIgnoreCase("clothing")){
			path = "com.rondaful.cloud.seller.generated.ProductClothingClothingType";
		}
		if(node.getFieldName().equalsIgnoreCase("sports")){
			path = "com.rondaful.cloud.seller.generated.Sports100Enum";
		}
		if(node.getFieldName().equalsIgnoreCase("toysBaby")){
			path = "com.rondaful.cloud.seller.generated.ToysBabyProductType";
		}
		if(node.getFieldName().equalsIgnoreCase("shoes")){
			path = "com.rondaful.cloud.seller.generated.ShoesClothingType";
		}
		if(node.getFieldName().equalsIgnoreCase("luggage")){
			path = "com.rondaful.cloud.seller.generated.Luggage100Enum";
		}
		return path;
	}

	
	@GetMapping("/deleteByPlatformSku")
	@ApiOperation("删除亚马逊疑似删除数据")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", value = "listingId", name = "listingId", dataType = "Long",required = true),
		@ApiImplicitParam(paramType = "query", value = "平台sku", name = "platformSku", dataType = "String",required = true)
	})
	public void deleteByPlatformSku(Long listingId,String platformSku) {
		if (listingId == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "被删除的id不能为空");
		if(StringUtils.isBlank(platformSku)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "平台sku不能为空");
		}
		AmazonPublishListing amazonPublishListing = amazonPublishListingService.selectByPrimaryKey(listingId);
		if (amazonPublishListing == null)
			return;
		if (!amazonPublishListing.getPlAccount().equalsIgnoreCase(getLoginUserInformationByToken.getUserDTO().getTopUserLoginName()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不能删除其他用户的刊登");
		String[] skus = platformSku.split(",");
		Set<String> setSku = Arrays.asList(skus).stream().collect(Collectors.toSet());
		//解析message
		AmazonRequestProduct amazonProduct = JSON.parseObject(amazonPublishListing.getPublishMessage(), AmazonRequestProduct.class);
		List<AmazonRequestProduct> varRequestProductList = amazonProduct.getVarRequestProductList();
		//如果疑似删除的sku和变体数量一样就直接删除
		if(setSku.size()==varRequestProductList.size()) {
			deleteListingAndSubListing(listingId);
			return ;
		}
		amazonPublishSubListingService.deleteByListingIdAndSkus(listingId, setSku);
		List<AmazonRequestProduct> deletePro=new LinkedList<AmazonRequestProduct>();
		for (AmazonRequestProduct amazonRequestProduct : varRequestProductList) {
			for (String sku : setSku) {
				if(amazonRequestProduct.getSku().equals(sku)) {
					//删除被删除的sku
					deletePro.add(amazonRequestProduct);
				}
			}
			
		}
		varRequestProductList.removeAll(deletePro);
		amazonProduct.setVarRequestProductList(varRequestProductList);
		//更新message
		AmazonPublishListing amazonPublishListingUpdate=new AmazonPublishListing();
		amazonPublishListingUpdate.setPublishMessage(JSON.toJSONString(amazonProduct));
		amazonPublishListingUpdate.setId(listingId);
		amazonPublishListingService.updateByPrimaryKeySelective(amazonPublishListingUpdate);
	}
	
	




}

