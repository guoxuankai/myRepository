package com.rondaful.cloud.seller.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingDTO;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.mapper.*;
import com.rondaful.cloud.seller.service.IAliexpressCategoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *速卖通 api参考地址
 *
 *开发者中心
 *
 * @author chenhan
 *
 */

@Service
public class AliexpressCategoryServiceImpl implements IAliexpressCategoryService {

    private final Logger logger = LoggerFactory.getLogger(AliexpressCategoryServiceImpl.class);

	@Autowired
	private AliexpressCategoryMapper aliexpressCategoryMapper;
	@Autowired
	private AliexpressCategoryAttributeMapper aliexpressCategoryAttributeMapper;
	@Autowired
	private AliexpressCategoryAttributeKeyMapper aliexpressCategoryAttributeKeyMapper;
	@Autowired
	private AliexpressCategoryAttributeSelectMapper aliexpressCategoryAttributeSelectMapper;
	@Autowired
	private AliexpressFreightTemplateMapper aliexpressFreightTemplateMapper;
	@Autowired
	private AliexpressGroupMapper aliexpressGroupMapper;
	@Autowired
	private AliexpressPromiseTemplateMapper aliexpressPromiseTemplateMapper;
	@Autowired
	private AliexpressAttributeSelectRelationMapper aliexpressAttributeSelectRelationMapper;

	@Override
	public String insertAliexpressCategory(String jsonStr,Long categoryParentId) {

		if(StringUtils.isBlank(jsonStr)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据为空");
		}
		JSONObject objJson = JSONObject.parseObject(jsonStr);
		JSONObject errorString = objJson.getJSONObject("error_response");
		if(errorString!=null){
			return errorString.toJSONString();
		}else{
			List<AliexpressCategory> listAliexpressCategory = this.jsonAliexpressCategory(objJson,categoryParentId);
			if(listAliexpressCategory!=null) {
				//分类id的集合
				List<Long> categoryIds = Lists.newArrayList();
				listAliexpressCategory.forEach(category -> {
					categoryIds.add(category.getCategoryId());
				});
				//查询到已有的数据
				List<AliexpressCategory> queryAliexpressCategory = null;
				if(categoryIds!=null) {
					if (categoryIds.size() > 1000) {
						//截取数据 每次查询1000个id
						int count = categoryIds.size()/1000;
						//循环开始
						int i =0;
						//初始化数据
						queryAliexpressCategory = Lists.newArrayList();
						while (i<count){
							//从多少开始
							int start = 0+(i*1000);
							//到多少结束
							int end = 1000+(i*1000);
							//截取id
							List<Long> subcategoryIds = categoryIds.subList(start,end);
							List<AliexpressCategory> listquery = aliexpressCategoryMapper.getCategoryByCategoryIdsList(categoryIds);
							//判断是否有数据
							if(listquery!=null && listquery.size()>0) {
								queryAliexpressCategory.addAll(listquery);
							}
							i++;
						}
						//从多少开始
						int start = i*1000;
						//到多少结束
						int end = categoryIds.size();
						//判断list的结束值是否还有数据需要查询
						if(end>start){
							List<Long> subcategoryIds = categoryIds.subList(start,end);
							List<AliexpressCategory> listquery = aliexpressCategoryMapper.getCategoryByCategoryIdsList(categoryIds);
							if(listquery.size()>0) {
								queryAliexpressCategory.addAll(listquery);
							}
						}
					} else if(categoryIds.size()>0){
						queryAliexpressCategory = aliexpressCategoryMapper.getCategoryByCategoryIdsList(categoryIds);
					}
				}
				Date date = new Date();
				for (AliexpressCategory aliexpressCategory : listAliexpressCategory) {
					//是否是修改
					boolean updateBool = false;
					if(queryAliexpressCategory!=null && queryAliexpressCategory.size()>0){
						for (AliexpressCategory queryModel : queryAliexpressCategory){
							//是否有数据
							if(queryModel.getCategoryId()!=null && queryModel.getCategoryId().equals(aliexpressCategory.getCategoryId())){
								aliexpressCategory.setId(queryModel.getId());
								queryAliexpressCategory.remove(queryModel);
								updateBool = true;
								break;
							}
						}
					}
					if(updateBool){
						//aliexpressCategory.setUpdateTime(date);
						//aliexpressCategoryMapper.updateByPrimaryKeySelective(aliexpressCategory);
					}else {
						aliexpressCategory.setCreationTime(date);
						aliexpressCategory.setUpdateTime(date);
						aliexpressCategoryMapper.insertSelective(aliexpressCategory);
					}

				}
			}
		}
		return "true";
	}

    @Override
    public int updateByPrimaryKeySelective(AliexpressCategory aliexpressCategory) {
		return aliexpressCategoryMapper.updateByPrimaryKeySelective(aliexpressCategory);
    }

    @Override
	public List<AliexpressCategoryAttribute> insertAliexpressCategoryAttribute(String jsonStr, Long categoryId,Long empowerId) {
		if(StringUtils.isBlank(jsonStr)){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据为空");
		}
		JSONObject objJson = JSONObject.parseObject(jsonStr);
		JSONObject errorString = objJson.getJSONObject("error_response");
		if(errorString!=null){
			return null;
		}else{
			List<AliexpressCategoryAttribute> listAliexpressCategoryAttribute = this.jsonAliexpressCategoryAttribute(objJson,categoryId);
			if(listAliexpressCategoryAttribute!=null) {
				//分类id的集合
				List<Long> categoryAttributeIds = Lists.newArrayList();
				listAliexpressCategoryAttribute.forEach(categoryAttribute -> {
					categoryAttributeIds.add(categoryAttribute.getAttributeId());
				});
				//查询到已有的数据
				List<AliexpressCategoryAttribute> queryAliexpressCategoryAttribute = null;
				if(categoryAttributeIds!=null) {
					if (categoryAttributeIds.size() > 1000) {
						//截取数据 每次查询1000个id
						int count = categoryAttributeIds.size()/1000;
						//循环开始
						int i =0;
						//初始化数据
						queryAliexpressCategoryAttribute = Lists.newArrayList();
						while (i<count){
							//从多少开始
							int start = 0+(i*1000);
							//到多少结束
							int end = 1000+(i*1000);
							//截取id
							List<Long> subcategoryAttributeIds = categoryAttributeIds.subList(start,end);
							List<AliexpressCategoryAttribute> listquery = aliexpressCategoryAttributeMapper.getAttributeByCategoryAttributeIdsList(subcategoryAttributeIds);
							//判断是否有数据
							if(listquery!=null && listquery.size()>0) {
								queryAliexpressCategoryAttribute.addAll(listquery);
							}
							i++;
						}
						//从多少开始
						int start = i*1000;
						//到多少结束
						int end = categoryAttributeIds.size();
						//判断list的结束值是否还有数据需要查询
						if(end>start){
							List<Long> subcategoryAttributeIds = categoryAttributeIds.subList(start,end);
							List<AliexpressCategoryAttribute> listquery = aliexpressCategoryAttributeMapper.getAttributeByCategoryAttributeIdsList(subcategoryAttributeIds);
							if(listquery.size()>0) {
								queryAliexpressCategoryAttribute.addAll(listquery);
							}
						}
					} else if(categoryAttributeIds.size()>0){
						queryAliexpressCategoryAttribute = aliexpressCategoryAttributeMapper.getAttributeByCategoryAttributeIdsList(categoryAttributeIds);
					}
				}
				Date date = new Date();
				//删除当前用户分类下的属性重新增加
				aliexpressCategoryAttributeKeyMapper.deleteByCategoryAttributeKey(categoryId,empowerId);
                List<AliexpressCategoryAttributeKey> listAttributeKey = Lists.newArrayList();
				for (AliexpressCategoryAttribute categoryAttribute : listAliexpressCategoryAttribute) {
					//是否是修改
					boolean updateBool = false;
					if(queryAliexpressCategoryAttribute!=null && queryAliexpressCategoryAttribute.size()>0){
						for (AliexpressCategoryAttribute queryModel : queryAliexpressCategoryAttribute){
							//是否有数据
							if(queryModel.getAttributeId()!=null && queryModel.getAttributeId().equals(categoryAttribute.getAttributeId())){
								categoryAttribute.setId(queryModel.getId());
								queryAliexpressCategoryAttribute.remove(queryModel);
								updateBool = true;
								break;
							}
						}
					}
					if(updateBool){
						//aliexpressCategoryAttributeMapper.updateByPrimaryKeySelective(categoryAttribute);
					}else {
						aliexpressCategoryAttributeMapper.insertSelective(categoryAttribute);
					}
					//保存关联属性的下拉选择值
					if(categoryAttribute.getAttributeSelectList()!=null && categoryAttribute.getAttributeSelectList().size()>0) {
						//删除属性的下拉选择值
						aliexpressAttributeSelectRelationMapper.deleteByAliexpressAttributeSelectRelation(categoryId,categoryAttribute.getAttributeId(),empowerId);
						List<AliexpressAttributeSelectRelation> listSelectRelation = Lists.newArrayList();
                        List<Long> selectIds = Lists.newArrayList();
                        for (AliexpressCategoryAttributeSelect attributeSelect : categoryAttribute.getAttributeSelectList()) {
                            selectIds.add(attributeSelect.getSelectId());
                        }
                        List<AliexpressCategoryAttributeSelect> listQuyerSelect = aliexpressCategoryAttributeSelectMapper.getCategoryAttributeSelectByselectIds(selectIds);
                        Map<Long,Boolean> mapSelect = Maps.newHashMap();
                        for (AliexpressCategoryAttributeSelect querySelect : listQuyerSelect) {
                            mapSelect.put(querySelect.getSelectId(),true);
                        }

						for (AliexpressCategoryAttributeSelect attributeSelect : categoryAttribute.getAttributeSelectList()) {
							//判断值是否存在
							//AliexpressCategoryAttributeSelect aliexpressCategoryAttributeSelect = aliexpressCategoryAttributeSelectMapper.getCategoryAttributeSelectBySelectId(attributeSelect.getSelectId());
							if(mapSelect.get(attributeSelect.getSelectId())==null){
								attributeSelect.setEmpowerId(0L);
								aliexpressCategoryAttributeSelectMapper.insertSelective(attributeSelect);
							}
							//保存select关联关系
							AliexpressAttributeSelectRelation aliexpressAttributeSelectRelation = new AliexpressAttributeSelectRelation();
							aliexpressAttributeSelectRelation.setCategoryId(categoryId);
							aliexpressAttributeSelectRelation.setAttributeId(categoryAttribute.getAttributeId());
							aliexpressAttributeSelectRelation.setCreateTime(date);
							aliexpressAttributeSelectRelation.setEmpowerId(empowerId);
							aliexpressAttributeSelectRelation.setSelectId(attributeSelect.getSelectId());
							aliexpressAttributeSelectRelation.setStatus(0);
                            aliexpressAttributeSelectRelation.setCreateId(0L);
							listSelectRelation.add(aliexpressAttributeSelectRelation);
						}
						aliexpressAttributeSelectRelationMapper.insertBatch(listSelectRelation);
					}
					//保存CategoryAttribute关联关系
					AliexpressCategoryAttributeKey aliexpressCategoryAttributeKey = new AliexpressCategoryAttributeKey();
					aliexpressCategoryAttributeKey.setAttributeId(categoryAttribute.getAttributeId());
					aliexpressCategoryAttributeKey.setCategoryId(categoryId);
					aliexpressCategoryAttributeKey.setCreateTime(date);
					aliexpressCategoryAttributeKey.setEmpowerId(empowerId);
					aliexpressCategoryAttributeKey.setStatus(0);
                    aliexpressCategoryAttributeKey.setCreateId(0L);
					aliexpressCategoryAttributeKey.setAttributeShowTypeValue(categoryAttribute.getAttributeShowTypeValue());
					aliexpressCategoryAttributeKey.setCustomizedName(categoryAttribute.getCustomizedName());
					aliexpressCategoryAttributeKey.setCustomizedPic(categoryAttribute.getCustomizedPic());
					aliexpressCategoryAttributeKey.setInputType(categoryAttribute.getInputType());
					aliexpressCategoryAttributeKey.setKeyAttribute(categoryAttribute.getKeyAttribute());
					aliexpressCategoryAttributeKey.setRequired(categoryAttribute.getRequired());
					aliexpressCategoryAttributeKey.setSku(categoryAttribute.getSku());
					aliexpressCategoryAttributeKey.setSkuStyleValue(categoryAttribute.getSkuStyleValue());
					aliexpressCategoryAttributeKey.setSpec(categoryAttribute.getSpec());
					aliexpressCategoryAttributeKey.setVisible(categoryAttribute.getVisible());
                    listAttributeKey.add(aliexpressCategoryAttributeKey);
					//aliexpressCategoryAttributeKeyMapper.insertSelective(aliexpressCategoryAttributeKey);
				}
                aliexpressCategoryAttributeKeyMapper.insertBatch(listAttributeKey);

			}
			return listAliexpressCategoryAttribute;
		}
	}

    @Override
    public List<AliexpressCategoryAttributeSelect> insertAliexpressCategoryAttributeBrand(String jsonStr, Long categoryId, Long empowerId, Long createId) {

        if(StringUtils.isBlank(jsonStr)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据为空");
        }
        JSONObject objJson = JSONObject.parseObject(jsonStr);
        JSONObject errorString = objJson.getJSONObject("error_response");
        if(errorString!=null){
            return null;
        }else{
            List<AliexpressCategoryAttribute> listAliexpressCategoryAttribute = this.jsonAliexpressCategoryAttributeChildattributesresul(objJson,categoryId);
            if(listAliexpressCategoryAttribute!=null) {
                Date date = new Date();
                //删除分类下的属性重新增加
                for (AliexpressCategoryAttribute categoryAttribute : listAliexpressCategoryAttribute) {
                    //当等于品牌的时候
                    if(2L==categoryAttribute.getAttributeId()) {
                        if (categoryAttribute.getAttributeSelectList() != null && categoryAttribute.getAttributeSelectList().size() > 0) {
                            //根据刊登账号修改属性值
                            //aliexpressCategoryAttributeSelectMapper.updateAttributeSelectByEmpowerId(empowerId);
//                            for (AliexpressCategoryAttributeSelect attributeSelect : categoryAttribute.getAttributeSelectList()) {
//                                attributeSelect.setEmpowerId(empowerId);
//                                attributeSelect.setCreateTime(date);
//                                attributeSelect.setCreateId(createId);
//								attributeSelect.setAttributeId(null);
//                                aliexpressCategoryAttributeSelectMapper.insertSelective(attributeSelect);
//                            }
							aliexpressAttributeSelectRelationMapper.deleteByAliexpressAttributeSelectRelation(categoryId,categoryAttribute.getAttributeId(),empowerId);
							for (AliexpressCategoryAttributeSelect attributeSelect : categoryAttribute.getAttributeSelectList()) {
								//判断值是否存在
								AliexpressCategoryAttributeSelect aliexpressCategoryAttributeSelect = aliexpressCategoryAttributeSelectMapper.getCategoryAttributeSelectBySelectId(attributeSelect.getSelectId());
								if(aliexpressCategoryAttributeSelect==null || aliexpressCategoryAttributeSelect.getSelectId()==null){
									attributeSelect.setEmpowerId(0L);
									aliexpressCategoryAttributeSelectMapper.insertSelective(attributeSelect);
								}
								//保存select关联关系
								AliexpressAttributeSelectRelation aliexpressAttributeSelectRelation = new AliexpressAttributeSelectRelation();
								aliexpressAttributeSelectRelation.setCategoryId(categoryId);
								aliexpressAttributeSelectRelation.setAttributeId(categoryAttribute.getAttributeId());
								aliexpressAttributeSelectRelation.setCreateTime(date);
								aliexpressAttributeSelectRelation.setEmpowerId(empowerId);
								aliexpressAttributeSelectRelation.setSelectId(attributeSelect.getSelectId());
								aliexpressAttributeSelectRelation.setStatus(0);
								aliexpressAttributeSelectRelationMapper.insertSelective(aliexpressAttributeSelectRelation);
							}

							return categoryAttribute.getAttributeSelectList();
                        }

                    }
                }
            }
        }
        return null;

    }

    /**
	 * 服务模板
	 * @param jsonStr
	 * @param empowerId
	 * @param plAccount
	 * @return
	 */
	@Override
	public List<AliexpressPromiseTemplate> insertAliexpressPromiseTemplate(String jsonStr, Long empowerId,String plAccount) {
		JSONObject objJson = JSONObject.parseObject(jsonStr);
		JSONObject errorString = objJson.getJSONObject("error_response");
		if(errorString!=null){
			return null;
		}else {
			Date date= new Date();
			List<AliexpressPromiseTemplate> listAliexpressPromiseTemplate = Lists.newArrayList();
			JSONObject jsonObject1 = objJson.getJSONObject("aliexpress_postproduct_redefining_querypromisetemplatebyid_response");
			JSONObject jsonObject2 = jsonObject1.getJSONObject("result");
			//Object strSuccess = jsonObject1.get("result_success");//判断数据是否成功
			if (jsonObject2 != null ) {
				JSONObject jsonObject4 = jsonObject2.getJSONObject("template_list");
				if(jsonObject4!=null){
					JSONArray arrayJson = jsonObject4.getJSONArray("templatelist");
					if(arrayJson!=null && arrayJson.size()>0){
						for(int i=0;i<arrayJson.size();i++){
							// 遍历 jsonarray 数组，把每一个对象转成 json 对象
							JSONObject job = arrayJson.getJSONObject(i);
							Long template_id = job.get("id")==null ?0L:Long.valueOf(job.get("id").toString());
							String template_name = job.get("name")==null ?"":job.get("name").toString();

							AliexpressPromiseTemplate aliexpressPromiseTemplate = new AliexpressPromiseTemplate();
							aliexpressPromiseTemplate.setPromiseTemplateId(template_id);
							aliexpressPromiseTemplate.setPromiseTemplateName(template_name);
							aliexpressPromiseTemplate.setDefaultIs(false);
							aliexpressPromiseTemplate.setCreateTime(date);
							aliexpressPromiseTemplate.setEmpowerId(empowerId);
							aliexpressPromiseTemplate.setPlAccount(plAccount);
							listAliexpressPromiseTemplate.add(aliexpressPromiseTemplate);
							//是否有相同的模板
							List<AliexpressPromiseTemplate> listTemplate = aliexpressPromiseTemplateMapper.getAliexpressPromiseTemplateByPlAccountList(empowerId,null,template_id);
							if(listTemplate!=null && listTemplate.size()>0){
								continue;
							}
							aliexpressPromiseTemplateMapper.insertSelective(aliexpressPromiseTemplate);
						}
					}
				}
			}
			return listAliexpressPromiseTemplate;
		}
	}

	@Override
	public List<AliexpressGroup> insertAliexpressGroup(String jsonStr, Long empowerId,String plAccount) {

		JSONObject objJson = JSONObject.parseObject(jsonStr);
		JSONObject errorString = objJson.getJSONObject("error_response");
		if(errorString!=null){
			return null;
		}else {
			List<AliexpressGroup> listAliexpressGroup = Lists.newArrayList();
			Date date= new Date();

			JSONObject jsonObject1 = objJson.getJSONObject("aliexpress_product_productgroups_get_response");
			JSONObject jsonObject2 = jsonObject1.getJSONObject("result");

			if (jsonObject2 != null ) {
				JSONObject jsonObject4 = jsonObject2.getJSONObject("target_list");
				if(jsonObject4!=null){
					JSONArray arrayJson = jsonObject4.getJSONArray("aeop_ae_product_tree_group");
					if(arrayJson!=null && arrayJson.size()>0){
						aliexpressGroupMapper.deleteAliexpressGroupByEmpowerId(empowerId);
						for(int i=0;i<arrayJson.size();i++){
							// 遍历 jsonarray 数组，把每一个对象转成 json 对象
							JSONObject job = arrayJson.getJSONObject(i);
							AliexpressGroup aliexpressGroup = new AliexpressGroup();
							String groupName = job.get("group_name")==null?"":job.get("group_name").toString();
							Long groupId = job.get("group_id")==null?0L:Long.valueOf(job.get("group_id").toString());
							aliexpressGroup.setGroupId(groupId);
							aliexpressGroup.setGroupName(groupName);
							aliexpressGroup.setParentGroupId(0L);
							aliexpressGroup.setPlAccount(plAccount);
							aliexpressGroup.setEmpowerId(empowerId);
							listAliexpressGroup.add(aliexpressGroup);
							aliexpressGroupMapper.insertSelective(aliexpressGroup);

							JSONObject jsonchildgroup = job.getJSONObject("child_group_list");
							if(jsonchildgroup!=null) {
								JSONArray arrayJsonchildgroup = jsonchildgroup.getJSONArray("aeop_ae_product_child_group");
								if(arrayJsonchildgroup!=null) {
									for (int j = 0; j < arrayJsonchildgroup.size(); j++) {
										JSONObject jobchildgroup = arrayJsonchildgroup.getJSONObject(j);
										aliexpressGroup = new AliexpressGroup();
										String childgroupName = jobchildgroup.get("group_name")==null?"":jobchildgroup.get("group_name").toString();
										Long childgroupId = jobchildgroup.get("group_id")==null?0L:Long.valueOf(jobchildgroup.get("group_id").toString());
										aliexpressGroup.setGroupId(childgroupId);
										aliexpressGroup.setGroupName(childgroupName);
										aliexpressGroup.setParentGroupId(groupId);
										aliexpressGroup.setPlAccount(plAccount);
										aliexpressGroup.setEmpowerId(empowerId);
										listAliexpressGroup.add(aliexpressGroup);
										aliexpressGroupMapper.insertSelective(aliexpressGroup);
									}
								}
							}
						}
					}
				}
			}
			return listAliexpressGroup;
		}
	}

	@Override
	public List<AliexpressFreightTemplate> insertAliexpressFreightTemplate(String jsonStr, Long empowerId,String plAccount) {

		JSONObject objJson = JSONObject.parseObject(jsonStr);
		JSONObject errorString = objJson.getJSONObject("error_response");
		if(errorString!=null){
			return null;
		}else {
			Date date= new Date();
			List<AliexpressFreightTemplate> listAliexpressFreightTemplate = Lists.newArrayList();
			JSONObject jsonObject1 = objJson.getJSONObject("aliexpress_freight_redefining_listfreighttemplate_response");
			//JSONObject jsonObject2 = jsonObject1.getJSONObject("result_success");
			Object strSuccess = jsonObject1.get("result_success");//判断数据是否成功
			if (strSuccess != null && "true".equals(strSuccess.toString())) {
				JSONObject jsonObject4 = jsonObject1.getJSONObject("aeop_freight_template_d_t_o_list");
				if(jsonObject4!=null){
					JSONArray arrayJson = jsonObject4.getJSONArray("aeopfreighttemplatedtolist");
					if(arrayJson!=null && arrayJson.size()>0){
						for(int i=0;i<arrayJson.size();i++){

							JSONObject job = arrayJson.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
							Long template_id = job.get("template_id")==null ?0L:Long.valueOf(job.get("template_id").toString());
							String template_name = job.get("template_name")==null ?"":job.get("template_name").toString();
							Boolean is_default = job.get("is_default")==null ?false:Boolean.valueOf(job.get("is_default").toString());
							AliexpressFreightTemplate aliexpressFreightTemplate = new AliexpressFreightTemplate();
							aliexpressFreightTemplate.setTemplateId(template_id);
							aliexpressFreightTemplate.setTemplateName(template_name);
							aliexpressFreightTemplate.setDefaults(is_default);
							aliexpressFreightTemplate.setCreateTime(date);
							aliexpressFreightTemplate.setEmpowerId(empowerId);
							aliexpressFreightTemplate.setPlAccount(plAccount);
							listAliexpressFreightTemplate.add(aliexpressFreightTemplate);
							//是否有相同的模板
							List<AliexpressFreightTemplate> listTemplate = aliexpressFreightTemplateMapper.getAliexpressFreightTemplateByPlAccountList(empowerId,null,template_id);
							if(listTemplate!=null && listTemplate.size()>0){
								continue;
							}
							aliexpressFreightTemplateMapper.insertSelective(aliexpressFreightTemplate);
						}
					}
				}
			}
			return listAliexpressFreightTemplate;
		}
	}

	@Override
	public List<AliexpressCategoryAttributeSelect> getCategoryAttributeSelectByList(Long categoryId,Long attributeId, Long empowerId) {
		return aliexpressCategoryAttributeSelectMapper.getCategoryAttributeSelectByList(categoryId,attributeId,empowerId);
	}

    @Override
    public Page<AliexpressCategoryAttributeSelect> findSelectPage(Integer currentPage, Integer pageSize, Long attributeId, String selectName) throws Exception {
		PageHelper.startPage(currentPage, pageSize);
		AliexpressCategoryAttributeSelect select = new AliexpressCategoryAttributeSelect();
		select.setAttributeId(attributeId);
		select.setSelectName(selectName);
		List<AliexpressCategoryAttributeSelect> list = aliexpressCategoryAttributeSelectMapper.page(select);
		PageInfo<AliexpressCategoryAttributeSelect> pageInfo = new PageInfo<>(list);
		Page<AliexpressCategoryAttributeSelect> page = new Page<>(pageInfo);
		return  page;
    }

    @Override
    public AliexpressCategory getCategoryByCategoryId(Long categoryId) {
        return aliexpressCategoryMapper.getCategoryByCategoryId(categoryId);
    }

    private List<AliexpressCategory> jsonAliexpressCategory(JSONObject jsonObject,Long categoryParentId){
		List<AliexpressCategory> listAliexpressCategory = null;

		JSONObject jsonObject1 = jsonObject.getJSONObject("aliexpress_category_redefining_getchildrenpostcategorybyid_response");
		if(jsonObject1==null){
			return  listAliexpressCategory;
		}
		JSONObject jsonObject2 =jsonObject1.getJSONObject("result");
		if(jsonObject2==null){
			return  listAliexpressCategory;
		}
		Object strSuccess =jsonObject2.get("success");//判断数据是否成功
		if(strSuccess!=null && "true".equals(strSuccess.toString())) {
			JSONObject jsonObject4 = jsonObject2.getJSONObject("aeop_post_category_list");
			//数据为空结束保存
			if(jsonObject4==null){
				return listAliexpressCategory;
			}
			JSONArray arrayJson = jsonObject4.getJSONArray("aeop_post_category_dto");
			if (arrayJson!=null && arrayJson.size() > 0) {
				listAliexpressCategory = Lists.newArrayList();
				for (int i = 0; i < arrayJson.size(); i++) {
					// 遍历 jsonarray 数组，把每一个对象转成 json 对象
					JSONObject job = arrayJson.getJSONObject(i);
					//获取一个对象
					AliexpressCategory category = new AliexpressCategory();
					String names = job.get("names")==null?"":job.get("names").toString();
					Integer level = job.get("level")==null?null:Integer.valueOf(job.get("level").toString());
					Long id = job.get("id")==null?null:Long.valueOf(job.get("id").toString());
					Boolean isleaf = job.get("isleaf")==null?false:Boolean.valueOf(job.get("isleaf").toString());
					JSONObject objJsonNames = JSONObject.parseObject(names);
					//中文名称
					String zh = objJsonNames.get("zh")==null?"":objJsonNames.get("zh").toString();
					//英文名称
					String en = objJsonNames.get("en")==null?"":objJsonNames.get("en").toString();
					category.setCategoryName(zh);
					category.setCategoryNameEn(en);
					category.setCategoryNameAll(names);
					category.setCategoryId(id);
					category.setCategoryLevel(level);
					category.setCategoryParentId(categoryParentId);
					category.setIsleaf(isleaf);
					listAliexpressCategory.add(category);
				}
			}
		}
		return listAliexpressCategory;
	}
	private List<AliexpressCategoryAttribute> jsonAliexpressCategoryAttributeChildattributesresul(JSONObject jsonObject,Long categoryId){
		List<AliexpressCategoryAttribute> listAliexpressCategoryAttribute = null;

		JSONObject jsonObject1 = jsonObject.getJSONObject("aliexpress_category_redefining_getchildattributesresultbypostcateidandpath_response");
		JSONObject jsonObject2 =jsonObject1==null ? null : jsonObject1.getJSONObject("result");
		Object strSuccess =jsonObject2==null ? null : jsonObject2.get("success");//判断数据是否成功
		if(strSuccess!=null && "true".equals(strSuccess.toString())) {
			JSONObject jsonObject4 = jsonObject2.getJSONObject("attributes");
			//数据为空结束保存
			if(jsonObject4==null){
				return listAliexpressCategoryAttribute;
			}
			JSONArray arrayJson = jsonObject4.getJSONArray("aeop_attribute_dto");
			if (arrayJson!=null && arrayJson.size() > 0) {
				listAliexpressCategoryAttribute = Lists.newArrayList();
				for (int i = 0; i < arrayJson.size(); i++) {
					// 遍历 jsonarray 数组，把每一个对象转成 json 对象
					JSONObject job = arrayJson.getJSONObject(i);
					//获取一个对象
					AliexpressCategoryAttribute categoryAttribute = new AliexpressCategoryAttribute();
					String names = job.get("names")==null?"":job.get("names").toString();
					JSONObject objJsonNames = JSONObject.parseObject(names);
					//中文名称
					String zh = objJsonNames.get("zh")==null?"":objJsonNames.get("zh").toString();
					//英文名称
					String en = objJsonNames.get("en")==null?"":objJsonNames.get("en").toString();

					String attribute_show_type_value = job.get("attribute_show_type_value") == null ? "" : job.get("attribute_show_type_value").toString();
					Boolean customized_name = job.get("customized_name") == null ? false : Boolean.valueOf(job.get("customized_name").toString());
					Boolean customized_pic = job.get("customized_pic") == null ? false : Boolean.valueOf(job.get("customized_pic").toString());
					String input_type = job.get("input_type") == null ? "" : job.get("input_type").toString();
					Long id = job.get("id") == null ? null : Long.valueOf(job.get("id").toString());

					Boolean key_attribute = job.get("key_attribute") == null ? false : Boolean.valueOf(job.get("key_attribute").toString());
					Boolean required = job.get("required") == null ? false : Boolean.valueOf(job.get("required").toString());
					Boolean sku = job.get("sku") == null ? false : Boolean.valueOf(job.get("sku").toString());
					Integer spec = job.get("spec") == null ? null : Integer.valueOf(job.get("spec").toString());
					Boolean visible = job.get("visible") == null ? false : Boolean.valueOf(job.get("visible").toString());
					String sku_style_value = job.get("sku_style_value") == null ? "" : job.get("sku_style_value").toString();
					//单位
					String units = job.get("units") == null ? null : job.get("units").toString();
					if(units!=null){
						JSONObject aeopUnitJson = JSONObject.parseObject(units);
						JSONArray aeopUnit = aeopUnitJson.getJSONArray("aeop_unit");
						if(aeopUnit!=null && aeopUnit.size()>0) {
							categoryAttribute.setUnits(aeopUnit.toJSONString());
						}
					}
					//下拉属性值
					String values = job.get("values") == null ? "" : job.get("values").toString();
					if(values!=null){
						JSONObject valuesJson = JSONObject.parseObject(values);
						if(valuesJson!=null){
							JSONArray attrValueSelect = valuesJson.getJSONArray("aeop_attr_value_dto");
							if(attrValueSelect!=null) {
								List<AliexpressCategoryAttributeSelect> listAliexpressCategoryAttributeSelect = Lists.newArrayList();
								for (int j = 0; j < attrValueSelect.size(); j++) {
									// 遍历 jsonarray 数组，把每一个对象转成 json 对象
									JSONObject attrValueSelectJson = attrValueSelect.getJSONObject(j);
									String namesSelect = attrValueSelectJson.get("names")==null?"":attrValueSelectJson.get("names").toString();
									JSONObject objJsonNamesSelect = JSONObject.parseObject(namesSelect);
									//中文名称
									String zhSelect = objJsonNamesSelect.get("zh")==null?"":objJsonNamesSelect.get("zh").toString();
									//英文名称
									String enSelect = objJsonNamesSelect.get("en")==null?"":objJsonNamesSelect.get("en").toString();


									Long idSelect = attrValueSelectJson.get("id")==null?null:Long.valueOf(attrValueSelectJson.get("id").toString());
									String value_tags = attrValueSelectJson.get("value_tags")==null?"":attrValueSelectJson.get("value_tags").toString();
									AliexpressCategoryAttributeSelect attributeSelect = new AliexpressCategoryAttributeSelect();
									attributeSelect.setSelectId(idSelect);
									attributeSelect.setCategoryId(categoryId);
									attributeSelect.setSelectName(zhSelect);
									attributeSelect.setSelectNameEn(enSelect);
									attributeSelect.setSelectNameAll(namesSelect);
									attributeSelect.setValueTags(value_tags);
									listAliexpressCategoryAttributeSelect.add(attributeSelect);
								}
								categoryAttribute.setAttributeSelectList(listAliexpressCategoryAttributeSelect);
							}
						}
					}

					categoryAttribute.setAttributeId(id);
					categoryAttribute.setAttributeName(zh);
					categoryAttribute.setAttributeNameEn(en);
					categoryAttribute.setAttributeNameAll(names);
					categoryAttribute.setAttributeShowTypeValue(attribute_show_type_value);
					categoryAttribute.setCustomizedName(customized_name);
					categoryAttribute.setCustomizedPic(customized_pic);
					categoryAttribute.setInputType(input_type);
					categoryAttribute.setKeyAttribute(key_attribute);
					categoryAttribute.setRequired(required);
					categoryAttribute.setSku(sku);
					categoryAttribute.setSkuStyleValue(sku_style_value);
					categoryAttribute.setSpec(spec);
					categoryAttribute.setVisible(visible);

					listAliexpressCategoryAttribute.add(categoryAttribute);
				}
			}
		}
		return listAliexpressCategoryAttribute;
	}

	private List<AliexpressCategoryAttribute> jsonAliexpressCategoryAttribute(JSONObject jsonObject,Long categoryId){
		List<AliexpressCategoryAttribute> listAliexpressCategoryAttribute = null;

		JSONObject jsonObject1 = jsonObject.getJSONObject("aliexpress_category_redefining_getallchildattributesresult_response");
		JSONObject jsonObject2 =jsonObject1==null ? null : jsonObject1.getJSONObject("result");
		Object strSuccess =jsonObject2==null ? null : jsonObject2.get("success");//判断数据是否成功
		if(strSuccess!=null && "true".equals(strSuccess.toString())) {
			JSONObject jsonObject4 = jsonObject2.getJSONObject("attributes");
			//数据为空结束保存
			if(jsonObject4==null){
				return listAliexpressCategoryAttribute;
			}
			JSONArray arrayJson = jsonObject4.getJSONArray("aeop_attribute_dto");
			if (arrayJson!=null && arrayJson.size() > 0) {
				listAliexpressCategoryAttribute = Lists.newArrayList();
				for (int i = 0; i < arrayJson.size(); i++) {
					// 遍历 jsonarray 数组，把每一个对象转成 json 对象
					JSONObject job = arrayJson.getJSONObject(i);
					//获取一个对象
					AliexpressCategoryAttribute categoryAttribute = new AliexpressCategoryAttribute();
					String names = job.get("names")==null?"":job.get("names").toString();
					JSONObject objJsonNames = JSONObject.parseObject(names);
					//中文名称
					String zh = objJsonNames.get("zh")==null?"":objJsonNames.get("zh").toString();
					//英文名称
					String en = objJsonNames.get("en")==null?"":objJsonNames.get("en").toString();

					String attribute_show_type_value = job.get("attribute_show_type_value") == null ? "" : job.get("attribute_show_type_value").toString();
					Boolean customized_name = job.get("customized_name") == null ? false : Boolean.valueOf(job.get("customized_name").toString());
					Boolean customized_pic = job.get("customized_pic") == null ? false : Boolean.valueOf(job.get("customized_pic").toString());
					String input_type = job.get("input_type") == null ? "" : job.get("input_type").toString();
					Long id = job.get("id") == null ? null : Long.valueOf(job.get("id").toString());

					Boolean key_attribute = job.get("key_attribute") == null ? false : Boolean.valueOf(job.get("key_attribute").toString());
					Boolean required = job.get("required") == null ? false : Boolean.valueOf(job.get("required").toString());
					Boolean sku = job.get("sku") == null ? false : Boolean.valueOf(job.get("sku").toString());
					Integer spec = job.get("spec") == null ? null : Integer.valueOf(job.get("spec").toString());
					Boolean visible = job.get("visible") == null ? false : Boolean.valueOf(job.get("visible").toString());
					String sku_style_value = job.get("sku_style_value") == null ? "" : job.get("sku_style_value").toString();
					//单位
					String units = job.get("units") == null ? null : job.get("units").toString();
					if(units!=null){
						JSONObject aeopUnitJson = JSONObject.parseObject(units);
						JSONArray aeopUnit = aeopUnitJson.getJSONArray("aeop_unit");
						if(aeopUnit!=null && aeopUnit.size()>0) {
							categoryAttribute.setUnits(aeopUnit.toJSONString());
						}
					}
					//下拉属性值
					String values = job.get("values") == null ? "" : job.get("values").toString();
					if(values!=null && 2L!=id){
						JSONObject valuesJson = JSONObject.parseObject(values);
						if(valuesJson!=null){
							JSONArray attrValueSelect = valuesJson.getJSONArray("aeop_attr_value_dto");
							if(attrValueSelect!=null) {
								List<AliexpressCategoryAttributeSelect> listAliexpressCategoryAttributeSelect = Lists.newArrayList();
								for (int j = 0; j < attrValueSelect.size(); j++) {
									// 遍历 jsonarray 数组，把每一个对象转成 json 对象
									JSONObject attrValueSelectJson = attrValueSelect.getJSONObject(j);
									String namesSelect = attrValueSelectJson.get("names")==null?"":attrValueSelectJson.get("names").toString();
									JSONObject objJsonNamesSelect = JSONObject.parseObject(namesSelect);
									//中文名称
									String zhSelect = objJsonNamesSelect.get("zh")==null?"":objJsonNamesSelect.get("zh").toString();
									//英文名称
									String enSelect = objJsonNamesSelect.get("en")==null?"":objJsonNamesSelect.get("en").toString();


									Long idSelect = attrValueSelectJson.get("id")==null?null:Long.valueOf(attrValueSelectJson.get("id").toString());
									String value_tags = attrValueSelectJson.get("value_tags")==null?"":attrValueSelectJson.get("value_tags").toString();
									AliexpressCategoryAttributeSelect attributeSelect = new AliexpressCategoryAttributeSelect();
									attributeSelect.setSelectId(idSelect);
									//attributeSelect.setAttributeId(id);
                                    //attributeSelect.setCategoryId(categoryId);
									attributeSelect.setSelectName(zhSelect);
									attributeSelect.setSelectNameEn(enSelect);
									attributeSelect.setSelectNameAll(namesSelect);
									attributeSelect.setValueTags(value_tags);
									listAliexpressCategoryAttributeSelect.add(attributeSelect);
								}
								categoryAttribute.setAttributeSelectList(listAliexpressCategoryAttributeSelect);
							}
						}
					}

					categoryAttribute.setAttributeId(id);
					categoryAttribute.setAttributeName(zh);
					categoryAttribute.setAttributeNameEn(en);
					categoryAttribute.setAttributeNameAll(names);
					categoryAttribute.setAttributeShowTypeValue(attribute_show_type_value);
					categoryAttribute.setCustomizedName(customized_name);
					categoryAttribute.setCustomizedPic(customized_pic);
					categoryAttribute.setInputType(input_type);
					categoryAttribute.setKeyAttribute(key_attribute);
					categoryAttribute.setRequired(required);
					categoryAttribute.setSku(sku);
					categoryAttribute.setSkuStyleValue(sku_style_value);
					categoryAttribute.setSpec(spec);
					categoryAttribute.setVisible(visible);

					listAliexpressCategoryAttribute.add(categoryAttribute);
				}
			}
		}
		return listAliexpressCategoryAttribute;
	}
}
