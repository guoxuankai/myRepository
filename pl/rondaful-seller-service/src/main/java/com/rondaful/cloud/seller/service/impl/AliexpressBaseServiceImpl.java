package com.rondaful.cloud.seller.service.impl;


import com.google.common.collect.Lists;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.mapper.*;
import com.rondaful.cloud.seller.service.IAliexpressBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 *速卖通 api参考地址
 *
 *开发者中心
 *
 * @author chenhan
 *
 */

@Service
public class AliexpressBaseServiceImpl implements IAliexpressBaseService {

    private final Logger logger = LoggerFactory.getLogger(AliexpressBaseServiceImpl.class);


	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private AliexpressCategoryMapper aliexpressCategoryMapper;
	@Autowired
	private AliexpressCategoryAttributeMapper aliexpressCategoryAttributeMapper;
	@Autowired
	private AliexpressCountryMapper aliexpressCountryMapper;
	@Autowired
	private AliexpressFreightTemplateMapper aliexpressFreightTemplateMapper;
	@Autowired
	private AliexpressPromiseTemplateMapper aliexpressPromiseTemplateMapper;
	@Autowired
	private AliexpressGroupMapper aliexpressGroupMapper;
	@Autowired
	private AliexpressCategoryAttributeSelectMapper aliexpressCategoryAttributeSelectMapper;


	@Override
	public List<AliexpressCategory> getAliexpressCategoryByList(Long categoryParentId) {
		//为空默认值0 第一级分类
		if(categoryParentId == null){
			categoryParentId = 0L;
		}
		String key ="aliexpress:aliexpress-Category"+ categoryParentId;
		if (redisUtils.exists(key)){
			return (List<AliexpressCategory>) redisUtils.get(key);
		}
		List<AliexpressCategory> listAliexpressCategory = aliexpressCategoryMapper.getAliexpressCategoryByList(categoryParentId);
		redisUtils.set(key,listAliexpressCategory,21600L);//6个小时缓存时间
		return listAliexpressCategory;
	}

	@Override
	public AliexpressCategory getCategoryByCategoryId(Long categoryId) {
		return aliexpressCategoryMapper.getCategoryByCategoryId(categoryId);
	}

	@Override
	public List<AliexpressCategoryAttribute> getAliexpressCategoryAttributeByCategoryIdList(Long categoryId,Long empowerId) {
		String key = "aliexpress:aliexpress-attribute"+categoryId;
		List<AliexpressCategoryAttribute> listAttribute = Lists.newArrayList();
		if (redisUtils.exists(key)){
			listAttribute = (List<AliexpressCategoryAttribute>)redisUtils.get(key);
		}else {
			listAttribute =aliexpressCategoryAttributeMapper.getAliexpressCategoryAttributeByCategoryIdList(categoryId,empowerId);
			redisUtils.set(key,listAttribute,21600L);//6个小时缓存时间
		}

		for(AliexpressCategoryAttribute attribute:listAttribute){
			if(2L==attribute.getAttributeId()){
				List<AliexpressCategoryAttributeSelect> listSelect = aliexpressCategoryAttributeSelectMapper.getCategoryAttributeSelectByList(categoryId,attribute.getAttributeId(),empowerId);
				if(listSelect!=null && listSelect.size()>0){
					attribute.setAttributeSelectList(listSelect);
				}else {
					//默认无品牌
					AliexpressCategoryAttributeSelect attributeSelect = aliexpressCategoryAttributeSelectMapper.getCategoryAttributeSelectBySelectId(201512802L);
					List<AliexpressCategoryAttributeSelect> attributeSelectList = Lists.newArrayList();
					attributeSelectList.add(attributeSelect);
					attribute.setAttributeSelectList(attributeSelectList);
				}
				break;
			}
		}

		return listAttribute;
	}

	@Override
	public List<AliexpressCountry> getAliexpressCountryByList() {
		String key = "aliexpress:aliexpressCountry";
		if (redisUtils.exists(key)){
			return (List<AliexpressCountry>) redisUtils.get(key);
		}
		List<AliexpressCountry> listAliexpressCountry = aliexpressCountryMapper.page(new AliexpressCountry());
		redisUtils.set(key,listAliexpressCountry,7200L);
		return listAliexpressCountry;
	}

	@Override
	public List<AliexpressFreightTemplate> getAliexpressFreightTemplateByPlAccountList(Long empowerId,String plAccount,Long templateId) {
		List<AliexpressFreightTemplate> listFreightTemplate = aliexpressFreightTemplateMapper.getAliexpressFreightTemplateByPlAccountList(empowerId,plAccount,templateId);
		if(listFreightTemplate==null || listFreightTemplate.size()==0){
			listFreightTemplate = new ArrayList<>();
			AliexpressFreightTemplate template = new AliexpressFreightTemplate();
			template.setTemplateId(1000L);
			template.setTemplateName("Shipping Cost Template for New Sellers");
			listFreightTemplate.add(template);
		}
		return listFreightTemplate;
	}

	@Override
	public List<AliexpressPromiseTemplate> getAliexpressPromiseTemplateByPlAccountList(Long empowerId,String plAccount,Long promiseTemplateId) {
		List<AliexpressPromiseTemplate> listPromiseTemplate = aliexpressPromiseTemplateMapper.getAliexpressPromiseTemplateByPlAccountList(empowerId,null,promiseTemplateId);
		if(listPromiseTemplate==null || listPromiseTemplate.size()==0){
			listPromiseTemplate = new ArrayList<>();
			AliexpressPromiseTemplate template = new AliexpressPromiseTemplate();
			template.setPromiseTemplateId(0L);
			template.setPromiseTemplateName("Service Template for New Sellers");
			listPromiseTemplate.add(template);
		}
		return listPromiseTemplate;
	}

	@Override
	public List<AliexpressGroup> getAliexpressGroupByPlAccountList(String plAccount, Long groupId,Long empowerId) {
		return aliexpressGroupMapper.getAliexpressGroupByPlAccountList(null,groupId,empowerId);
	}
	@Override
	public String getCategoryName(Long categoryId,boolean boolEn){
		String key ="aliexpress:aliexpressCategoryName"+ categoryId+boolEn;
		if (redisUtils.exists(key)){
			return  redisUtils.get(key).toString();
		}
		StringBuffer str = new StringBuffer("");
		AliexpressCategory aliexpressCategory = aliexpressCategoryMapper.getCategoryByCategoryId(categoryId);

		if(aliexpressCategory!=null) {
			if(boolEn){
				str.append(aliexpressCategory.getCategoryNameEn());
			}else{
				str.append(aliexpressCategory.getCategoryName());
			}
			while (aliexpressCategory.getCategoryParentId() > 0) {
				aliexpressCategory = aliexpressCategoryMapper.getCategoryByCategoryId(aliexpressCategory.getCategoryParentId());
				if(boolEn){
					str.insert(0, aliexpressCategory.getCategoryNameEn() + "#,");
				}else{
					str.insert(0, aliexpressCategory.getCategoryName() + "#,");
				}
			}
		}
		redisUtils.set(key,str.toString());
		return str.toString();
	}
}
