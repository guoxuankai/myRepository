package com.rondaful.cloud.seller.mapper;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AmazonTemplateRule;

public interface AmazonTemplateRuleMapper extends BaseMapper<AmazonTemplateRule> {

	void copyTemplateRule(Long id);

	void updateByDefaultTemplateStatus(Map<String, Object> map);

	List<AmazonTemplateRule> getByEmpowerIdAndDefaultTemplate(AmazonTemplateRule templateRule);

	List<AmazonTemplateRule> getByList(AmazonTemplateRule templateRuleNew);
	
	/**
	 * templateName是非模糊查询
	 * @param map
	 * @return
	 */
	List<AmazonTemplateRule> getByMap(Map<String, Object> map);
	
	List<AmazonTemplateRule> getByThirdPartyNameAndDefaultTemplate(Map<String, Object> map);

	List<AmazonTemplateRule> getByThirdPartyNameTemplateAndDefaultTemplate(AmazonTemplateRule t);

	List<AmazonTemplateRule> selectDefaulte(AmazonTemplateRule t);
	
}