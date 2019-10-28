package com.rondaful.cloud.seller.controller;

import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import com.rondaful.cloud.seller.service.AmazonTemplateAttributeService;
import com.rondaful.cloud.seller.utils.ChangeJavaEnumsToStringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Api("亚马逊刊登模板属性相关接口")
@RestController
@RequestMapping("/amazonTemplate")
public class AmazonTemplateAttributeController {

    private static final Logger logger = LoggerFactory.getLogger(AmazonTemplateAttributeController.class);

    @Autowired
    private ChangeJavaEnumsToStringUtils changeJavaEnumsToStringUtils;

    @Autowired
    private AmazonTemplateAttributeService amazonTemplateAttributeService;


    @PostMapping("/changeEnum")
    @ApiOperation("将指定模板指定属性的枚举改为String")
    public void changeEnum() {
        List<String> templates = amazonTemplateAttributeService.selectAllChildTemplate();
        for (String template : templates) {
            List<AmazonTemplateAttribute> amazonTemplateAttributes = amazonTemplateAttributeService.selectAllAttributeByTemplate(template);
            List<String> attributes = amazonTemplateAttributes.stream()
                    .filter(a -> StringUtils.isNotBlank(a.getOptions()))
                    .map(AmazonTemplateAttribute::getAttributeName)
                    .collect(Collectors.toList());
            changeJavaEnumsToStringUtils.writeToString(template, attributes);
        }
    }


    @PostMapping("/addAttributes")
    @ApiOperation("给模板站点添加属性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sites", value = "站点 BR|CA|MX|US|DE|ES|FR|UK|IN|IT|TR|AU|JP|CN 以上格式", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "date", value = "数据，要添加的数据", dataType = "String", paramType = "query", required = true)
    })
    public void addAttributes(String sites, String date) {
        if (StringUtils.isBlank(sites) || StringUtils.isBlank(date)) {
            return;
        } else {
            ArrayList<MarketplaceId> siteLists = getSites(sites);

            String[] attrs = date.split(",");
            String[] attrModels;
            ArrayList<AmazonTemplateAttribute> amazonTemplateAttributes = new ArrayList<>();
            AmazonTemplateAttribute tem = null;
            for (String attr : attrs) {
                attrModels = attr.split("\t");
                for (MarketplaceId id : siteLists) {
                    tem = new AmazonTemplateAttribute();
                    tem.setSite(id.getCountryCode());
                    tem.setMarketplaceId(id.getMarketplaceId());
                    tem.setTemplateParent(attrModels[0].trim());
                    tem.setTemplateChild(attrModels[1].trim());
                    tem.setAttributeName(attrModels[2].trim());
                    tem.setRequired(1);
                    tem.setCreateTime(new Date());
                    tem.setUpdateTime(new Date());
                    amazonTemplateAttributes.add(tem);
                }
            }

            for (AmazonTemplateAttribute a : amazonTemplateAttributes) {
                amazonTemplateAttributeService.insert(a);
            }
            amazonTemplateAttributeService.setSign();
        }

    }


    private ArrayList<MarketplaceId> getSites( String sites){
        String[] siteList = sites.split("\\|");
        Map<String, MarketplaceId> marketplace = MarketplaceIdList.createMarketplace();
        ArrayList<MarketplaceId> siteLists = new ArrayList<>();
        for (String site : siteList) {
            MarketplaceId marketplaceId = marketplace.get(site.trim());
            if (marketplaceId != null)
                siteLists.add(marketplaceId);
        }
        return siteLists;
    }


    @PostMapping("/updateOptions")
    @ApiOperation("给指定属性添加可选值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sites", value = "站点 BR|CA|MX|US|DE|ES|FR|UK|IN|IT|TR|AU|JP|CN 以上格式", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "templateParent", value = "父级模板", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "templateChild", value = "自己模板", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "attribute", value = "字段名称", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "date", value = "数据，要添加的数据", dataType = "String", paramType = "query", required = true)
    })
    public void updateOptions(String sites,String templateParent,String templateChild,String attribute, String date) {
        if(StringUtils.isNotBlank(sites) && StringUtils.isNotBlank(templateParent) && StringUtils.isNotBlank(templateChild) && StringUtils.isNotBlank(attribute) && StringUtils.isNotBlank(date)){
            ArrayList<MarketplaceId> siteLists = getSites(sites);

            String[] split = date.split(",");
            StringBuilder sb = new StringBuilder();
            for (String str:split){
                sb.append(str.trim()).append("|");
            }
            String s = sb.toString();
            String options = s.substring(0,s.lastIndexOf("|"));
            AmazonTemplateAttribute amazonTemplateAttribute = null;
            AmazonTemplateAttribute resulte = new AmazonTemplateAttribute();
            resulte.setOptions(options);
            for (MarketplaceId id:siteLists){
                amazonTemplateAttribute = new AmazonTemplateAttribute();
                amazonTemplateAttribute.setSite(id.getCountryCode());
                //amazonTemplateAttribute.setMarketplaceId(id.getMarketplaceId());
                amazonTemplateAttribute.setTemplateParent(templateParent);
                amazonTemplateAttribute.setTemplateChild(templateChild);
                amazonTemplateAttribute.setAttributeName(attribute);
                List<AmazonTemplateAttribute> allNoPage = amazonTemplateAttributeService.findAllNoPage(amazonTemplateAttribute);
                if(!CollectionUtils.isEmpty(allNoPage)){
                    for(AmazonTemplateAttribute templateAttribute : allNoPage){
                        resulte.setId(templateAttribute.getId());
                        amazonTemplateAttributeService.updateByPrimaryKeySelective(resulte);
                    }
                }
            }
        }
    }





}
