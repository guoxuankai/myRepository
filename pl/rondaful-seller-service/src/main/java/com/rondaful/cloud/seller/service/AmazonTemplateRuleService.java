package com.rondaful.cloud.seller.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.seller.entity.AmazonTemplateRule;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.entity.amazon.AmazonSubRequestProduct;
import com.rondaful.cloud.seller.enums.AmazonTemplateEnums.DefaultTemplate;

public interface AmazonTemplateRuleService extends BaseService<AmazonTemplateRule> {

	/**
	 * 复制刊登模板
	 * @param id 被复制的id
	 */
	void copyTemplateRule (Long id,Integer topUserId);

	/**
	 * 编辑回显
	 * @param id
	 * @return
	 */
	AmazonTemplateRule getEditViewById(Long id);

	/**
	 * 设置默认模板
	 * @param id模板id
	 * 当前卖家账号
	 */
	void editDefaultTemplate(Long id);

	void saveOrUpdate(AmazonTemplateRule amazonTemplateRule);

	/**
	 * 通过模板创建刊登数据
	 * @param requestProduct 刊登数据对象
	 * @param templateId 模板id
	 * @param SPUId spuid
	 * @param templateParent 父级模板
	 * @param templateChild 自己模板
	 * @return 刊登数据对象
	 */
	AmazonRequestProduct createPublishByTemplate(AmazonRequestProduct requestProduct,Long templateId ,String SPUId,String templateParent,String templateChild,Integer publishType) throws Exception;


	/**
	 * 通过spu返回商品数据
	 * @param SPUId spu
	 * @return 商品数据
	 */
	JSONObject getCommodity(String SPUId);


	/**
	 * 通过模板创建刊登子数据
	 * @param empower 授权数据
	 * @param templateId 模板id
	 * @param SKU SKU
	 * @return 刊登数据对象
	 */
	AmazonSubRequestProduct createSubPublishByTemplate(Empower empower, Long templateId , String SKU) throws Exception;

	/**
	 * 获取规则模板的sku自增序列值
	 * @param ruleId 规则模板id
	 * @return 序列值
	 */
	Long findRuleOrder(Long ruleId);

	/**
	 * 根据授权账号Id获取刊登模板包含通用模板
	 * @param empowerId
	 * @return
	 */
	List<AmazonTemplateRule> getAmazonTemplateRulesByThirdPartyName(String thirdPartyName,Integer topUserId,Integer createUserId,Integer empowerId);

	List<AmazonTemplateRule> getByEmpowerIdAndDefaultTemplate(String thirdPartyName, Integer code);

	AmazonTemplateRule getByPrimaryKey(Long id);

	
	List<AmazonTemplateRule> getByList(AmazonTemplateRule t);

	/**
	 * 判断账号下模板名字不能重复
	 * @param templateName
	 * @param id
	 * @param thirdPartyName
	 * @return true 存在 
	 */
	boolean checkTemplateName(String templateName, Long id, String thirdPartyName,Integer userid);

	List<AmazonTemplateRule> getByThirdPartyNameTemplateAndDefaultTemplate(AmazonTemplateRule t);




}
