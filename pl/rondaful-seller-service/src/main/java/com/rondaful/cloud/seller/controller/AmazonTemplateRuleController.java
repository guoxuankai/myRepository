package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.security.UserSession;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.entity.AmazonTemplateRule;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.entity.amazon.AmazonSubRequestProduct;
import com.rondaful.cloud.seller.enums.AmazonTemplateEnums.DefaultTemplate;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.service.AmazonCategoryService;
import com.rondaful.cloud.seller.service.AmazonTemplateRuleService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.service.IEmpowerService;
import com.rondaful.cloud.seller.vo.AmazonTemplateRuleVO;
import com.rondaful.cloud.seller.vo.EmpowerVo;

import io.swagger.annotations.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

/**
 * 亚马逊刊登模板
 *
 * @author dingshulin
 */
@RestController
@RequestMapping("/amazon/template/rule")
@Api(description = "亚马逊刊登模板-dingshulin")
public class AmazonTemplateRuleController extends BaseController {
 
    private final Logger logger = LoggerFactory.getLogger(AmazonTemplateRuleController.class);

    @Autowired
    private AmazonCategoryService amazonCategoryService;
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Autowired
    private AuthorizationSellerService authorizationSellerService;

    @Autowired
    private AmazonTemplateRuleService amazonTemplateRuleService;

    @Autowired
    private IEmpowerService empowerService;
    

    @AspectContrLog(descrption = "亚马逊刊登模板列表", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "亚马逊刊登模板分页查询", notes = "亚马逊刊登模板分页查询")
    @GetMapping("/getByPage")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "当前页码", name = "page", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "row", dataType = "String", required = true),
    })
    public Page<AmazonTemplateRule> getByPage(AmazonTemplateRuleVO amazonTemplateRuleVO) {
    	AmazonTemplateRule amazonTemplateRule = new AmazonTemplateRule();
        Page.builder(amazonTemplateRuleVO.getPage(), amazonTemplateRuleVO.getRow());
        amazonTemplateRule.setTimeType(amazonTemplateRuleVO.getTimeType());
        amazonTemplateRule.setStartCreateTime(amazonTemplateRuleVO.getStartCreateTime());
        amazonTemplateRule.setEndCreateTime(amazonTemplateRuleVO.getEndCreateTime());
        amazonTemplateRule.setEmpowerId(amazonTemplateRuleVO.getEmpowerId());
        amazonTemplateRule.setDefaultTemplate(amazonTemplateRuleVO.getDefaultTemplate());
        amazonTemplateRule.setTemplateName(amazonTemplateRuleVO.getTemplateName());
        if(amazonTemplateRuleVO.getDefaultTemplate()== null && StringUtils.isBlank(amazonTemplateRuleVO.getStartCreateTime()) 
        &&StringUtils.isBlank(amazonTemplateRuleVO.getEndCreateTime())&& StringUtils.isBlank(amazonTemplateRuleVO.getTemplateName())
        &&amazonTemplateRuleVO.getEmpowerId() == null) {
        	amazonTemplateRule.setIsPage(1);
        }
        if (UserSession.getUserBaseUserInfo() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "获取当前用户失败");
        }
        amazonTemplateRule.setCreateUserId((long) UserSession.getUserBaseUserInfo().getUserid());
        if(getLoginUserInformationByToken.getUserInfo() == null) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "该token已经失效，未登录或登录超时请重新登录，谢谢");
        } 
        
        UserDTO userdTO=getLoginUserInformationByToken.getUserDTO();
        if (userdTO == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "获取当前用户失败");
        }
        //权限处理
        if(userdTO.getManage()) {
        	//主账号
			amazonTemplateRule.setTopUserId(userdTO.getTopUserId());
        }else {
        	List<String> bindCode =new ArrayList<>();
        	//子账号
        	List<UserAccountDTO> binds = getLoginUserInformationByToken.getUserInfo().getUser().getBinds();
        	if(CollectionUtils.isEmpty(binds)) {
        		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, "权限异常");
        	}
        	bindCode=binds.get(0).getBindCode();
        	amazonTemplateRule.setEmpowerIds(strToLong(bindCode));
        }
         
        Page<AmazonTemplateRule> page = amazonTemplateRuleService.page(amazonTemplateRule);
        try {
            Object pageInfo = page.getPageInfo();
            if(pageInfo instanceof PageInfo){
                /*List<AmazonTemplateRule> list = ((PageInfo) pageInfo).getList();
                list.forEach(l -> {
                    l.setTemplateName(Utils.translation(l.getTemplateName()));
                    l.setEmpowerAccount(Utils.translation(l.getEmpowerAccount()));
                });*/
            }
        }catch (Exception e){
        	e.printStackTrace();
        }

        return page;
    }

     
    @AspectContrLog(descrption = "复制亚马逊刊登模板", actionType = SysLogActionType.ADD)
    @PostMapping("/copy/{id}")
    @ApiOperation("复制亚马逊刊登模板")
    public void copy(@ApiParam(value = "被复制的id", name = "id", required = true) @PathVariable Long id) {
        if (id == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "被复制的id不能为空");
        AmazonTemplateRule templateRule = amazonTemplateRuleService.selectByPrimaryKey(id);
        if (templateRule == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "被复制的id不存在");
        try {
        	UserDTO userdTO=getLoginUserInformationByToken.getUserDTO();
            if(userdTO == null) {
            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "获取当前用户失败");
            }
            amazonTemplateRuleService.copyTemplateRule(id,userdTO.getTopUserId());
        } catch (Exception e) {
            logger.error("复制亚马逊刊登异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "复制亚马逊刊登异常");
        }
    }


    @AspectContrLog(descrption = "保存模板", actionType = SysLogActionType.ADD)
    @PostMapping("/save")
    @ApiOperation(value = "保存刊登模板", notes = "保存刊登模板")
    public void add(@Valid AmazonTemplateRule amazonTemplateRule, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (UserSession.getUserBaseUserInfo() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "获取当前用户失败");
        }
        
        amazonTemplateRule.setCreateUserName(UserSession.getUserBaseUserInfo().getUsername());
        amazonTemplateRule.setCreateUserId((long) UserSession.getUserBaseUserInfo().getUserid());
        logger.info("保存或者更新模板相关参数："+amazonTemplateRule.toString());
        String templateName = amazonTemplateRule.getTemplateName();
        Long id = amazonTemplateRule.getId();
        String thirdPartyName = amazonTemplateRule.getThirdPartyName();
       
        boolean b=amazonTemplateRuleService.checkTemplateName(templateName,id,thirdPartyName,UserSession.getUserBaseUserInfo().getUserid());
        if(b) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "刊登模板名称不能重复");
        }
        UserDTO userdTO=getLoginUserInformationByToken.getUserDTO();
        if(userdTO == null) {
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "获取当前用户失败");
        }
        amazonTemplateRule.setTopUserId(userdTO.getTopUserId());
		amazonTemplateRuleService.saveOrUpdate(amazonTemplateRule);
    }

    @AspectContrLog(descrption = "删除模板", actionType = SysLogActionType.DELETE)
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除亚马逊刊登模板", notes = "删除亚马逊刊登模板")
    public void delete(@ApiParam(value = "被删除的id", name = "id", required = true) @PathVariable Long id) {
        AmazonTemplateRule templateRule = amazonTemplateRuleService.selectByPrimaryKey(id);
        if (templateRule == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "没有查询到对应的模板数据");
        }
        amazonTemplateRuleService.deleteByPrimaryKey(id);
    }


    @GetMapping("/editView/{id}")
    @ApiOperation(value = "刊登模板编辑数据回显", notes = "刊登模板编辑数据回显")
    public AmazonTemplateRule getById(@ApiParam(value = "刊登模板编辑数据回显的ID", name = "id", required = true) @PathVariable Long id) {
        AmazonTemplateRule rule = amazonTemplateRuleService.getEditViewById(id);
        if (rule == null) {                                                      
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "没有查询到对应的模板数据");
        }
        
        return rule;
    }


    @AspectContrLog(descrption = "设置默认模板", actionType = SysLogActionType.UDPATE)
    @PostMapping("/editDefault/{id}")
    @ApiOperation(value = "设置默认模板", notes = "设置默认模板")
    public void editDefault(@ApiParam(value = "需要设置为默认模板的ID", name = "id", required = true) @PathVariable Long id) {
        AmazonTemplateRule templateRule = amazonTemplateRuleService.selectByPrimaryKey(id);
        if (templateRule == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "没有查询到对应的模板数据");
        }
        
        amazonTemplateRuleService.editDefaultTemplate(id);
    }

    @AspectContrLog(descrption = "通过模板生成刊登数据", actionType = SysLogActionType.QUERY)
    @PostMapping("/createPublish")
    @ApiOperation(value = "通过模板生成刊登数据", notes = "通过模板生成刊登数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateId", value = "模板id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SPU", value = "商品SPU", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "empowerId", value = "授权id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "templateParent", value = "父级模板", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "templateChild", value = "自己模板", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "publishType", value = "刊登类型[1:单属性格式 2:多属性格式]", dataType = "Integer", paramType = "query")
    })
    public AmazonRequestProduct createPublishByTemplate(Long templateId, String SPU, String empowerId,String templateParent,String templateChild,Integer publishType) throws Exception {
        AmazonTemplateRule amazonTemplateRule = amazonTemplateRuleService.selectByPrimaryKey(templateId);
        if(amazonTemplateRule == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"请切换有效的模板");
        try {
            AmazonRequestProduct<Object> product = new AmazonRequestProduct<>();
            Empower empower = authorizationSellerService.selectByPrimaryKey(empowerId);
            if(empower == null )
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),"店铺不存在");
            if(empower.getStatus() != 1)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),"店铺状态异常");
            product.setShopName(empower.getAccount());
            product.setShopId(empowerId);
            product.setMerchantIdentifier(empower.getThirdPartyName());
            product.setCountryCode(empower.getWebName());
            product.setSpu(SPU);
            AmazonRequestProduct publishByTemplate = amazonTemplateRuleService.createPublishByTemplate(product, templateId, SPU,templateParent,templateChild,publishType);
            publishByTemplate.setComputeTemplateJson(amazonTemplateRule.getComputeTemplate()); 
            return publishByTemplate;
        } catch (Exception e) {
            logger.error("通过模板生成刊登数据异常 templateId：" + templateId + " SPU：" + SPU + " empowerId：" + empowerId, e);
            throw e;
        }
    }


    @AspectContrLog(descrption = "通过模板生成刊登子数据", actionType = SysLogActionType.QUERY)
    @PostMapping("/createSubPublish")
    @ApiOperation(value = "通过模板生成刊登子数据", notes = "通过模板生成刊登子数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateId", value = "模板id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "SKU", value = "商品SKU", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "empowerId", value = "授权id", dataType = "string", paramType = "query")
    })
    public AmazonSubRequestProduct createSubPublishByTemplate(Long templateId, String SKU, String empowerId) throws Exception {
        AmazonTemplateRule amazonTemplateRule = amazonTemplateRuleService.selectByPrimaryKey(templateId);

        if(amazonTemplateRule == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"请切换有效的模板");
        try {
            Empower empower = authorizationSellerService.selectByPrimaryKey(empowerId);
            if(empower == null )
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),"店铺不存在");
            if(empower.getStatus() != 1)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),"店铺状态异常");
            AmazonSubRequestProduct subPublishByTemplate = amazonTemplateRuleService.createSubPublishByTemplate(empower, templateId, SKU);
            return subPublishByTemplate;
        } catch (Exception e) {
            logger.error("通过模板生成刊登子数据异常 templateId：" + templateId + " SKU：" + SKU + " empowerId：" + empowerId, e);
            throw e;
        }
    }

    @PostMapping("/getAmazonTemplateRules")
    @ApiOperation(value = "根据第三方账号ID获取刊登模板数据(包含通用模板)", notes = "根据第三方账号ID获取刊登模板数据(包含通用模板)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "thirdPartyName", value = "第三方授权id", dataType = "Long", paramType = "query")
    })
    public List<AmazonTemplateRule> getAmazonTemplateRules(String thirdPartyName,Integer empowerId){
    	if(StringUtils.isBlank(thirdPartyName)) {
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"授权账号ID为空");
    	}
    	UserDTO userdTO=getLoginUserInformationByToken.getUserDTO();
        if (userdTO == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "获取当前用户失败");
        }
        Integer topUserId =null;
        Integer createUserId =null;
        
        if(userdTO.getManage()) {
        	//主账号
        	 topUserId = userdTO.getTopUserId();
        	 empowerId=null;
        }
    	List<AmazonTemplateRule> rules=amazonTemplateRuleService.getAmazonTemplateRulesByThirdPartyName(thirdPartyName,topUserId,createUserId,empowerId);
        //rules.forEach(r -> r.setTemplateName(Utils.translation(r.getTemplateName())));
    	return rules;
    }

    
    @PostMapping("/getByEmpowerIdAndDefaultTemplate")
    @ApiOperation(value = "判断默认模板在同一账号下是否重复", notes = "判断默认模板在同一账号下是否重复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "thirdPartyName", value = "授权id", dataType = "String", paramType = "query")
    })
    public List<AmazonTemplateRule> getByEmpowerIdAndDefaultTemplate(String thirdPartyName) {
    	if(StringUtils.isBlank(thirdPartyName)) {
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"授权账号ID为空");
    	}
    	List<AmazonTemplateRule> rules=amazonTemplateRuleService.getByEmpowerIdAndDefaultTemplate(thirdPartyName,DefaultTemplate.DEFAULT.getType());
    	return rules;
    }
    
    @PostMapping("/getByThirdPartyNameTemplateAndNotDefault")
    @ApiOperation(value = "获取此授权账号下的模板", notes = "获取此授权账号下的模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "thirdPartyName", value = "第三方授权账号id", dataType = "String", paramType = "query")
    }) 
    public List<AmazonTemplateRule> getByThirdPartyNameTemplateAndNotDefault(String thirdPartyName){
    	 
    	AmazonTemplateRule t=new AmazonTemplateRule();
    	t.setThirdPartyName(thirdPartyName);
    	t.setDefaultTemplate(DefaultTemplate.NOT_DEFAULT.getType());
    	List<AmazonTemplateRule>list=amazonTemplateRuleService.getByThirdPartyNameTemplateAndDefaultTemplate(t);
    	return list;
    }
    
    
    public static void main(String[] args) {
    	
    }
    
    @PostMapping("/getByPrimaryKey")
    @ApiOperation(value = "根据ID查询模板", notes = "根据ID查询模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query")
    })
    public AmazonTemplateRule selectByPrimaryKey(Long id){
    	AmazonTemplateRule templateRule = amazonTemplateRuleService.selectByPrimaryKey(id);
    	if(templateRule == null) {
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"id为空");
    	}
    	
    	return amazonTemplateRuleService.getByPrimaryKey(id);
    }
    
   private List<Long> strToLong(List<String> bindCode) {
	   List<Long> empowerIds=new ArrayList<>();
	   for (String str : bindCode) {
		   empowerIds.add(Long.valueOf(str));
		}
	   return empowerIds;
   }
   
   private List<Long> getEmpowerId(List<EmpowerVo> accounts) {
	   List<Long> ids=new ArrayList<>(); 
	   for (EmpowerVo empowerVo : accounts) {
		   ids.add(Long.parseLong(String.valueOf(empowerVo.getEmpowerId())));
	   }
	   return ids;
   }
   
    
}

