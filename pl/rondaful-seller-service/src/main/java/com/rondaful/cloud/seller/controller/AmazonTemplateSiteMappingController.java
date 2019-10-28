package com.rondaful.cloud.seller.controller;


import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.constant.UserConstants;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.MD5;
import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import com.rondaful.cloud.seller.entity.AmazonTemplateSiteMapping;

import com.rondaful.cloud.seller.entity.amazon.AmazonAttr;
import com.rondaful.cloud.seller.entity.amazon.AmazonAttributeOperation;
import com.rondaful.cloud.seller.enums.AmazonTemplateAttributeEnum;
import com.rondaful.cloud.seller.service.AmazonTemplateAttributeService;
import com.rondaful.cloud.seller.service.AmazonTemplateSiteMappingService;
import com.rondaful.cloud.seller.utils.ClassReflectionUtil;
import com.rondaful.cloud.seller.utils.DecapitalizeChar;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping("/amazonTemplate")
@Api(description = "亚马逊后台模板和属性相关接口")
public class AmazonTemplateSiteMappingController {

    private Logger logger = LoggerFactory.getLogger(AmazonTemplateSiteMappingController.class);


    @Autowired
    private AmazonTemplateSiteMappingService amazonTemplateSiteMappingService;

    @Autowired
    private AmazonTemplateAttributeService amazonTemplateAttributeService;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;


    private void checkUser() {
        UserDTO userDTO = getLoginUserInformationByToken.getUserDTO();
        if (userDTO == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "该token已经失效，未登录或登录超时请重新登录，谢谢");
        }
        if (!userDTO.getPlatformType().equals(UserConstants.MANAGEPLATFORMTYPE)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "非后台管理账户不能进行该操作！");
        }
    }

    @AspectContrLog(descrption = "分页查询后台模板", actionType = SysLogActionType.QUERY)
    @GetMapping("/findTemplateAll")
    @ApiOperation("分页查询后台模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "当前页码", name = "page", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "query", value = "每页显示行数", name = "row", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "query", value = "站点", name = "site", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "父级模板", name = "templateParent", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "自己模板", name = "templateChild", dataType = "String"),
            @ApiImplicitParam(paramType = "query", value = "包含字段", name = "attribute", dataType = "String")

    })
    public Page<AmazonTemplateSiteMapping> findTemplateAll(@ApiIgnore AmazonTemplateSiteMapping mapping, String page, String row) {
        this.checkUser();
        try {
            if (StringUtils.isNotBlank(mapping.getSite()) && mapping.getSite().equalsIgnoreCase("GB"))
                mapping.setSite("UK");
            Page.builder(page, row);
            return amazonTemplateSiteMappingService.findAllByPage(mapping);
        } catch (Exception e) {
            logger.error("分页查询亚马逊后台模板异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "分页查询亚马逊后台模板异常");
        }

    }

    @AspectContrLog(descrption = "更改模板的启停状态(只能更新状态)", actionType = SysLogActionType.UDPATE)
    @PutMapping("/changeIsDisabled")
    @ApiOperation("更改模板的启停状态(只能更新状态)")
    public void changeIsDisabled(@RequestBody AmazonTemplateSiteMapping mapping) {
        this.checkUser();
        if (mapping.getId() == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "ID不能为空");
        if (mapping.getIsDisabled() == null || (!mapping.getIsDisabled().equals(AmazonTemplateAttributeEnum.IsDisabledE.OK.getCode()) && !mapping.getIsDisabled().equals(AmazonTemplateAttributeEnum.IsDisabledE.NOT.getCode())))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "模板状态错误");
        try {
            AmazonTemplateSiteMapping param = new AmazonTemplateSiteMapping();
            param.setId(mapping.getId());
            param.setIsDisabled(mapping.getIsDisabled());
            amazonTemplateSiteMappingService.updateByPrimaryKeySelective(param);
        } catch (Exception e) {
            logger.error("更改模板的启停状态异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更改模板的启停状态异常");
        }
    }

    @AspectContrLog(descrption = "查询属性列表", actionType = SysLogActionType.QUERY)
    @GetMapping("/findAttributeAll")
    @ApiOperation("查询属性列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", value = "站点", name = "site", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "query", value = "父级模板", name = "templateParent", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "query", value = "自己模板", name = "templateChild", dataType = "String", required = true)
            //@ApiImplicitParam(paramType = "query", value = "0:可选，1：必选", name = "required", dataType = "Integer", required = true)
    })
    public List<AmazonTemplateAttribute> findAttributeAll(@ApiIgnore AmazonTemplateAttribute attribute) {
        this.checkUser();
        try {
            return amazonTemplateAttributeService.findAttributeList(attribute);
        } catch (Exception e) {
            logger.error("查询属性列表异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询属性列表异常");
        }

    }

    @Autowired
    private AmazonPublishController amazonPublishController;

    @AspectContrLog(descrption = "操作亚马逊属性", actionType = SysLogActionType.UDPATE)
    @PutMapping("/operationAttributes")
    @ApiOperation("操作亚马逊属性")
    public Map<String, List<String>> operationAttributes(@RequestBody AmazonAttributeOperation operation) {
        this.checkUser();
        String site = operation.getSite();
        String templateParent = operation.getTemplateParent();
        String templateChild = operation.getTemplateChild();
        List<AmazonTemplateAttribute> updateAttributes = operation.getUpdateAttributes();
        List<AmazonTemplateAttribute> addAttribute = operation.getAddAttribute();

        String marketplaceId = MarketplaceIdList.createMarketplace().get(site).getMarketplaceId();

        ArrayList<String> updateNotSameMsg = new ArrayList<>();
        ArrayList<String> addNotRightMsg = new ArrayList<>();

        try {
            for (AmazonTemplateAttribute attribute : updateAttributes) {
                if (!attribute.getMarketplaceId().equalsIgnoreCase(marketplaceId) ||
                        !attribute.getTemplateParent().equalsIgnoreCase(templateParent) || !attribute.getTemplateChild().equalsIgnoreCase(templateChild)) {
                    updateNotSameMsg.add(attribute.getAttributeName());
                }
                if (StringUtils.isBlank(attribute.getSign())) {
                    attribute.setSign(MD5.md5Password(site + templateParent + templateChild));
                }
            }

            templateParent = DecapitalizeChar.sameParent(templateParent);
            templateChild = DecapitalizeChar.sameChild(templateParent, templateChild);

            templateParent = DecapitalizeChar.decapitalizeUpperCase(templateParent);
            templateChild = DecapitalizeChar.decapitalizeUpperCase(templateChild);

            Map<String, Object> templateAttr = amazonPublishController.getTemplateAttr(templateParent, templateChild, marketplaceId);
            Object productParentAttr = templateAttr.get("productParentAttr");
            Object productChildAttr = templateAttr.get("productChildAttr");


            for (AmazonTemplateAttribute attribute : addAttribute) {
                if (!this.checkAttributeIsXMLData(productParentAttr, productChildAttr, attribute)) {
                    addNotRightMsg.add(attribute.getAttributeName());
                }
                attribute.setSite(site);
                attribute.setMarketplaceId(marketplaceId);
                attribute.setTemplateParent(templateParent);
                attribute.setTemplateChild(templateChild);
                attribute.setSign(MD5.md5Password(site + templateParent + templateChild));
            }
            if (updateNotSameMsg.size() > 0 || addNotRightMsg.size() > 0) {
                HashMap<String, List<String>> resulte = new HashMap<>();
                resulte.put("updateNotSameMsg", updateNotSameMsg);
                resulte.put("addNotRightMsg", addNotRightMsg);
                return resulte;
            }

            amazonTemplateAttributeService.addOrUpdateAttribute(updateAttributes, addAttribute);
            return null;
        } catch (Exception e) {
            logger.error("操作亚马逊属性异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "操作亚马逊属性异常");
        }
    }


    public static void main(String[] ss) {
        String s = MD5.md5Password("DE" + "home" + "cutlery");

        String md = "77a55eedfff6ee26d7dcf91d2d80c7e9";

        System.out.println(md.equals(s));
        System.out.println(md.equalsIgnoreCase(s));

    }


    private boolean checkAttributeIsXMLData(Object productParentAttr, Object productChildAttr, AmazonTemplateAttribute attribute) { //AmazonAttr
        if (productChildAttr != null) {
            String c = JSONObject.toJSONString(productChildAttr);
            List<AmazonAttr> amazonAttrs = JSONObject.parseArray(c, AmazonAttr.class);
            boolean exit = isExit(amazonAttrs, attribute.getAttributeName());
            if (exit)
                return true;
        }

        if (productParentAttr != null) {
            String p = JSONObject.toJSONString(productParentAttr);
            List<AmazonAttr> amazonAttrs = JSONObject.parseArray(p, AmazonAttr.class);
            boolean exit = isExit(amazonAttrs, attribute.getAttributeName());
            if (exit)
                return true;
        }
        return false;
    }


    private boolean isExit(List<AmazonAttr> amazonAttrs, String attriName) {
        if (amazonAttrs == null || amazonAttrs.size() == 0) {
            return false;
        }
        for (AmazonAttr attr : amazonAttrs) {
            if (attr.getAttrName().equalsIgnoreCase(attriName)) {
                return true;
            } else {
                if (isExit(attr.getNextNodes(), attriName)) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean checkAttributeIsXmlData(Class<?> classz, AmazonTemplateAttribute attribute, AmazonAttr parentAmazonAttr) {
        Field[] fields = classz.getDeclaredFields();
        for (Field field : fields) {
            if (attribute.getAttributeName().contains(".")) {
                String[] selectSplit = attribute.getAttributeName().split("\\.");
                if (field.getName().equalsIgnoreCase(selectSplit[1]) &&
                        parentAmazonAttr != null &&
                        parentAmazonAttr.getAttrName().equalsIgnoreCase(selectSplit[0])) {
                    return true;
                }
            }
            if (field.getName().equalsIgnoreCase(attribute.getAttributeName())) {
                return true;
            }
            if (!ClassReflectionUtil.isBaseType(field.getType())) {
                AmazonAttr attr = new AmazonAttr();
                attr.setAttrName(field.getName());
                attr.setAttrType(field.getType().getName());
                if (checkAttributeIsXmlData(field.getType(), attribute, attr)) {
                    return true;
                }
            }
        }
        return false;
    }


}
