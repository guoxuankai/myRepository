package com.rondaful.cloud.seller.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.entity.AmazonPublishTemplateOrder;
import com.rondaful.cloud.seller.entity.AmazonTemplateRule;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.entity.amazon.AmazonSubRequestProduct;
import com.rondaful.cloud.seller.enums.AmazonTemplateEnums;
import com.rondaful.cloud.seller.enums.AmazonTemplateEnums.DefaultTemplate;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.mapper.AmazonPublishTemplateOrderMapper;
import com.rondaful.cloud.seller.mapper.AmazonTemplateRuleMapper;
import com.rondaful.cloud.seller.mapper.EmpowerMapper;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.service.AmazonTemplateRuleService;
import com.rondaful.cloud.seller.utils.AmazonTemplateUtils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



@Service
public class AmazonTemplateRuleServiceImpl extends BaseServiceImpl<AmazonTemplateRule> implements AmazonTemplateRuleService {


    private final String ruleOrderKey = "amazonTemplateKey:";

    @Autowired
    private AmazonPublishTemplateOrderMapper amazonPublishTemplateOrderMapper;

    @Autowired
    private AmazonTemplateUtils amazonTemplateUtils;

    @Autowired
    private RemoteCommodityService remoteCommodityService;


	private final Logger logger = LoggerFactory.getLogger(AmazonTemplateRuleServiceImpl.class);

	
	@Autowired
	private AmazonTemplateRuleMapper amazonTemplateRuleMapper;
	@Autowired
	private EmpowerMapper empowerMapper ;
	
	@Override
	public void copyTemplateRule(Long id,Integer topUserId) {
		AmazonTemplateRule  templateRule= amazonTemplateRuleMapper.selectByPrimaryKey(id);
		if(templateRule != null) {
			AmazonTemplateRule  templateRuleNew=new AmazonTemplateRule();
			try {
				BeanUtils.copyProperties(templateRuleNew, templateRule);
				templateRuleNew.setId(null);
				templateRuleNew.setDefaultTemplate(DefaultTemplate.NOT_DEFAULT.getType());
				templateRuleNew.setCreateTime(new Date());
				templateRuleNew.setUpdateTime(null);
				
				int j = new Random().nextInt(999);
				String name="";
				
				int indexOf = templateRuleNew.getTemplateName().indexOf("副");
				int count=com.rondaful.cloud.seller.utils.Utils.countStrKey(templateRule.getTemplateName(), "副");
				if(indexOf == -1) {
					//第一次复制
					name=templateRuleNew.getTemplateName()+"-副本"+j;	
				}else {
					name=templateRuleNew.getTemplateName().substring(0,indexOf)+"-副本"+j;
				}
				if(count >0) {
					//大于0表示复制的副本//英国模板1-副本1
					name=templateRuleNew.getTemplateName()+"-副本"+j;	
				}
				
				templateRuleNew.setTemplateName(name);
				templateRuleNew.setTopUserId(topUserId);
				templateRuleNew.setComputeTemplate(templateRule.getComputeTemplate());
				amazonTemplateRuleMapper.insertSelective(templateRuleNew);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("刊登模板复制出错:"+e.getMessage());
			}
			
		}
		
	}


	@Override
	public AmazonTemplateRule getEditViewById(Long id) {
		AmazonTemplateRule rule = amazonTemplateRuleMapper.selectByPrimaryKey(id);
		if(rule == null ) {
			return null;
		}
		Empower empower = empowerMapper.selectByPrimaryKey(String.valueOf(rule.getEmpowerId()));
		if(empower != null) {
			rule.setEmpowerAccount(empower.getAccount());
		}else {
			rule.setEmpowerAccount("");
		}
		return rule;
	}


	@Override
	@Transactional 
	public void editDefaultTemplate(Long id) {
		AmazonTemplateRule templateRule = amazonTemplateRuleMapper.selectByPrimaryKey(id);
		AmazonTemplateRule rule=new AmazonTemplateRule();
		rule.setId(id);
		rule.setDefaultTemplate(AmazonTemplateEnums.DefaultTemplate.DEFAULT.getType());
		Map<String, Object> map=new HashMap<>();
		map.put("notDefault", AmazonTemplateEnums.DefaultTemplate.NOT_DEFAULT.getType());
		map.put("default", AmazonTemplateEnums.DefaultTemplate.DEFAULT.getType());
		String thirdPartyName = templateRule.getThirdPartyName();
		map.put("thirdPartyName", thirdPartyName);
		amazonTemplateRuleMapper.updateByDefaultTemplateStatus(map);
		amazonTemplateRuleMapper.updateByPrimaryKeySelective(rule);
		
	}

	@Override
	@Transactional
	public void saveOrUpdate(AmazonTemplateRule amazonTemplateRule) {
		//获取第三方id
		Empower empower = empowerMapper.selectByPrimaryKey(String.valueOf(amazonTemplateRule.getEmpowerId()));
		logger.info("-----------------授权表信息："+empower.toString());
		if(empower != null) {
			amazonTemplateRule.setThirdPartyName(empower.getThirdPartyName());
		}
		
		if(amazonTemplateRule.getId() == null) {
			save(amazonTemplateRule);
		}else {
			update(amazonTemplateRule);
		}
		if(amazonTemplateRule.getDefaultTemplate()==DefaultTemplate.DEFAULT.getType()) {
			editDefaultTemplate(amazonTemplateRule.getId());
		}  
	}

	private void HasDefaultSave(AmazonTemplateRule amazonTemplateRule,Long id) {
		if(amazonTemplateRule.getDefaultTemplate()==DefaultTemplate.DEFAULT.getType()) {
			AmazonTemplateRule update=new AmazonTemplateRule();
			update.setId(id);
			update.setDefaultTemplate(DefaultTemplate.NOT_DEFAULT.getType());
			amazonTemplateRuleMapper.updateByPrimaryKeySelective(update);
		 }
	}

	@Override
	public JSONObject getCommodity(String SPUId){
		String result = remoteCommodityService.managerCommodity("1", "2", null, null, null, null, null, null, null, true,
				null, SPUId,null,null);
		String dataString = Utils.returnRemoteResultDataString(result, "商品服务异常");
		JSONObject object = JSONObject.parseObject(dataString);
		JSONObject pageInfo = object.getJSONObject("pageInfo");
		JSONArray list = pageInfo.getJSONArray("list");
		if(list == null || list.size() == 0)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600.getCode(),"商品为空");
		return (JSONObject) list.get(0);
	}

	
	@Override
	public AmazonRequestProduct createPublishByTemplate(AmazonRequestProduct requestProduct, Long templateId,String SPUId,String templateParent,String templateChild,Integer publishType) throws Exception{
		JSONObject o = this.getCommodity(SPUId);
		AmazonTemplateRule amazonTemplateRule = amazonTemplateRuleMapper.selectByPrimaryKey(templateId);
        String countryCode = MarketplaceIdList.createMarketplaceForKeyId().get(requestProduct.getCountryCode()).getCountryCode();
        amazonTemplateUtils.setFirstCategory(requestProduct,SPUId,countryCode,templateParent,templateChild);
        amazonTemplateUtils.setSecondCategory(requestProduct,SPUId,countryCode,amazonTemplateRule.getCategorySecondRule());
        //amazonTemplateUtils.setPublishType(requestProduct,amazonTemplateRule.getPublishType());
        amazonTemplateUtils.setFulfillmentLatency(requestProduct,amazonTemplateRule.getFulfillmentLatency());
        amazonTemplateUtils.setVariationTheme(requestProduct,o,amazonTemplateRule,publishType);
        return requestProduct;
	}

	@Override
	public AmazonSubRequestProduct createSubPublishByTemplate(Empower empower, Long templateId , String SKU) throws Exception{
		String commoditySpec = remoteCommodityService.getCommoditySpecBySku(SKU);
		String dataString = Utils.returnRemoteResultDataString(commoditySpec, "商品服务异常");
		JSONObject object = JSONObject.parseObject(dataString);
		AmazonTemplateRule amazonTemplateRule = amazonTemplateRuleMapper.selectByPrimaryKey(templateId);
		AmazonSubRequestProduct amazonSubRequestProduct = new AmazonSubRequestProduct();
		amazonSubRequestProduct.setPlSku(SKU);
		amazonTemplateUtils.setSubPublish(object,amazonSubRequestProduct,empower,amazonTemplateRule);
		return amazonSubRequestProduct;
	}


    @Override
    public Long findRuleOrder(Long ruleId) {
	    String key = ruleOrderKey + ruleId;
        AmazonPublishTemplateOrder byKey = amazonPublishTemplateOrderMapper.findByKey(key);
        if(byKey == null){
           amazonPublishTemplateOrderMapper.insert(new AmazonPublishTemplateOrder(){{
               setKey(key);
           }});
           return 0L;
        }else {
            int i = amazonPublishTemplateOrderMapper.addValue1(key);
            logger.info("------findRuleOrder返回值{}key{}",i,key);
            return byKey.getValue();
        }
    }

    private AmazonTemplateRule save(AmazonTemplateRule amazonTemplateRule) {
		amazonTemplateRule.setCreateTime(new Date());
		amazonTemplateRuleMapper.insertSelective(amazonTemplateRule);
		return amazonTemplateRule;
	}


	private AmazonTemplateRule update(AmazonTemplateRule amazonTemplateRule) {
		amazonTemplateRule.setUpdateTime(new Date());
		amazonTemplateRule.setUpdateUserId(amazonTemplateRule.getCreateUserId());
		amazonTemplateRule.setUpdateUserName(amazonTemplateRule.getCreateUserName());
		amazonTemplateRule.setCreateUserId(null);
		amazonTemplateRule.setCreateUserName(null);
		amazonTemplateRuleMapper.updateByPrimaryKeySelective(amazonTemplateRule);
		return amazonTemplateRule;
	}


	@Override
	public List<AmazonTemplateRule> getAmazonTemplateRulesByThirdPartyName(String thirdPartyName,Integer topUserId,Integer createUserId,Integer empowerId) {
		 Map<String, Object> map=new HashMap<>();
		 map.put("thirdPartyName", thirdPartyName);
		 map.put("defaultTemplate", DefaultTemplate.GLOBAL_DEFAULT.getType());
		 map.put("topUserId", topUserId);
		 map.put("createUserId", createUserId);
		 map.put("empowerId", empowerId);
		 List<AmazonTemplateRule> rules =amazonTemplateRuleMapper.getByThirdPartyNameAndDefaultTemplate(map);
		 return rules;
	}
	
	public List<AmazonTemplateRule> getByEmpowerIdAndDefaultTemplate(String thirdPartyName,Integer code){
		AmazonTemplateRule t=new AmazonTemplateRule();
		t.setDefaultTemplate(code);
		t.setThirdPartyName(thirdPartyName);
		List<AmazonTemplateRule> list =amazonTemplateRuleMapper.selectDefaulte(t);
		return list;
	}

	@Override
	public AmazonTemplateRule getByPrimaryKey(Long id) {
		AmazonTemplateRule templateRule = amazonTemplateRuleMapper.selectByPrimaryKey(id);
		Empower empower = empowerMapper.selectByPrimaryKey(String.valueOf(templateRule.getEmpowerId()));
		String webName = empower.getWebName();
		
    	templateRule.setWebName(MarketplaceIdList.createMarketplaceForKeyId().get(webName).getCountryName());
    	templateRule.setEmpowerAccount(empower.getAccount());
		String beforeTemplateName="";
    	List<AmazonTemplateRule> list = getByEmpowerIdAndDefaultTemplate(templateRule.getThirdPartyName(), DefaultTemplate.DEFAULT.getType());
    	if(!CollectionUtils.isEmpty(list)) {
    		beforeTemplateName=list.get(0).getTemplateName();
    	}
    	templateRule.setBeforeTemplateName(beforeTemplateName);
		return templateRule;
	}


	@Override
	public List<AmazonTemplateRule> getByList(AmazonTemplateRule t) {
		return amazonTemplateRuleMapper.getByList(t);
		
	}


	@Override
	public boolean checkTemplateName(String templateName, Long id, String thirdPartyName, Integer userid) {
		Map<String, Object> map =new HashMap<>();
		
		map.put("templateName", templateName);
		map.put("createUserId", userid);
		
		List<AmazonTemplateRule> list =amazonTemplateRuleMapper.getByMap(map);
		if(CollectionUtils.isEmpty(list)) {
			return false;
		}
		if(id == null) {
			//添加
			if(list.size()==0) {
				return false;
			}
		}else {
			if(!list.get(0).getId().equals(id)) {
				if(list.get(0).getTemplateName().equals(templateName)) {
					return true;
				}
			}else {
				if(list.size()>=1) {
					return false;
				}
			}
			
		}
		return true;
	}


	@Override
	public List<AmazonTemplateRule> getByThirdPartyNameTemplateAndDefaultTemplate(AmazonTemplateRule t) {
		return amazonTemplateRuleMapper.getByThirdPartyNameTemplateAndDefaultTemplate(t);
	}
	
}


