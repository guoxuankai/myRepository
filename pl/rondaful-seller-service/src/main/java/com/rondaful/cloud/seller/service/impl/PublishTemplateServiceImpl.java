package com.rondaful.cloud.seller.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.dto.PlatformSkuDTO;
import com.rondaful.cloud.seller.entity.EbayPublishListingVariant;
import com.rondaful.cloud.seller.entity.PublishTemplate;
import com.rondaful.cloud.seller.mapper.EbayPublishListingNewMapper;
import com.rondaful.cloud.seller.mapper.EbayPublishListingVariantMapper;
import com.rondaful.cloud.seller.mapper.PublishTemplateMapper;
import com.rondaful.cloud.seller.service.PublishTemplateService;
import com.rondaful.cloud.seller.utils.GeneratePlateformSku;
import com.rondaful.cloud.seller.utils.ValidatorUtil;
import com.rondaful.cloud.seller.vo.PublishTemplateSearchVO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class PublishTemplateServiceImpl implements PublishTemplateService {

    private final Logger logger = LoggerFactory.getLogger(PublishTemplateServiceImpl.class);

	@Autowired
	private PublishTemplateMapper publishTemplateMapper;
	@Autowired
	private EbayPublishListingVariantMapper listingVariantMapper;
	@Override
	public Page<PublishTemplate> findPage(PublishTemplateSearchVO vo) throws Exception {
		PageHelper.startPage(vo.getPage(), vo.getRow());
		List<PublishTemplate> list = publishTemplateMapper.findPage(vo);

		PageInfo<PublishTemplate> pageInfo = new PageInfo<>(list);
		Page<PublishTemplate> page = new Page<>(pageInfo);
		return  page;
	}

    @Override
    public List<PublishTemplate> getPublishTemplateALLList(Integer platform,String site, Integer templateType, String plAccount,Boolean defaultIs,String empowerId) {
        return publishTemplateMapper.getPublishTemplateALLList(platform,site, templateType, plAccount,defaultIs,empowerId);
    }

    @Override
    public PublishTemplate savePublishTemplate(PublishTemplate publishTemplate) {
		this.check(publishTemplate);

		Date date = new Date();
		if(publishTemplate.getId()==null){
			publishTemplate.setCreateTime(date);
			publishTemplate.setUpdateTime(date);
			publishTemplateMapper.insertSelective(publishTemplate);
		}else{
			PublishTemplate query = publishTemplateMapper.selectByPrimaryKey(publishTemplate.getId());
			if(query!=null && query.getSystemIs()!=null && query.getSystemIs()){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统模板不能操作");
			}
			publishTemplate.setUpdateTime(date);
			publishTemplateMapper.updateByPrimaryKeySelective(publishTemplate);
		}
		//默认为true 当前分类
		if(publishTemplate.getDefaultIs()){
			publishTemplateMapper.updatePublishTemplateDefault(publishTemplate.getId(),publishTemplate.getPlAccount(),publishTemplate.getSite(),publishTemplate.getTemplateType());
		}
        return publishTemplate;
    }

	@Override
	public int deletePublishTemplate(Long id) {
		PublishTemplate publishTemplate = new PublishTemplate();
		publishTemplate.setId(id);
		publishTemplate.setStatus(1);
		publishTemplateMapper.updateByPrimaryKeySelective(publishTemplate);
		return 1;
	}

	@Override
	public PublishTemplate getPublishTemplateById(Long id) {
		return publishTemplateMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<EbayPublishListingVariant> getVariantByPlatformSku(String platformSku, Long listingId) {

		return listingVariantMapper.getVariantByPlatformSku(platformSku,listingId);
	}

	@Override
	public Map<String, String> findPlatformSku(Integer platform, String site, String skus, String sellerName, Long listingId, Integer sellerNameNum, String userName) {

//		List<PublishTemplate> findAll = this.getPublishTemplateALLList(platform,site,10,userName,null);
//		if(findAll==null || findAll.size()==0){
//			return null;
//		}
//		PublishTemplate publishTemplate = null;
//		if(findAll.size()==1){
//			publishTemplate = findAll.get(0);
//			publishTemplate = this.getPublishTemplateById(publishTemplate.getId());
//			if(publishTemplate.getDefaultIs()==null || !publishTemplate.getDefaultIs()) {
//				return null;
//			}
//		}else
//		{
//			//站点默认优先
//			for (PublishTemplate pt:findAll){
//				if(site.equals(pt.getSite()) && pt.getDefaultIs()!=null && pt.getDefaultIs()){
//					publishTemplate = pt;
//					break;
//				}
//			}
//			//通用默认
//			if(publishTemplate==null) {
//				for (PublishTemplate pt : findAll) {
//					if (pt.getDefaultIs() != null && pt.getDefaultIs()) {
//						publishTemplate = pt;
//						break;
//					}
//				}
//			}
//
//			if(publishTemplate==null){
//				return null;
//			}else{
//				publishTemplate = this.getPublishTemplateById(publishTemplate.getId());
//			}
//
//		}
		Map<String,String> map = Maps.newHashMap();
//		PlatformSkuDTO platformSkuDTO =  JSONObject.parseObject(publishTemplate.getContentExt(), PlatformSkuDTO.class);
//		if(platformSkuDTO!=null){
			//授权店铺名称
//			if("name".equals(platformSkuDTO.getRuleOne())){
//				map.put("tuleType","name");
//				if(org.apache.commons.lang3.StringUtils.isBlank(sellerName)){
//					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "授权店铺名称为空");
//				}
//				if(sellerNameNum!=null){
//					for(int i=1;i<=sellerNameNum;i++) {
//						String platformSku = GeneratePlateformSku.getEbayPlateformSku(sellerName, platformSkuDTO.getRuleTwo(), Integer.valueOf(platformSkuDTO.getRuleThree()));
//
//						List<EbayPublishListingVariant> list = this.getVariantByPlatformSku(platformSku, listingId);
//						while (list != null && list.size() > 0) {
//							platformSku = GeneratePlateformSku.getEbayPlateformSku(sellerName, platformSkuDTO.getRuleTwo(), Integer.valueOf(platformSkuDTO.getRuleThree()));
//							list = this.getVariantByPlatformSku(platformSku, listingId);
//						}
//						map.put(sellerName+i, platformSku);
//					}
//				}
//			}else{
				if(org.apache.commons.lang3.StringUtils.isBlank(skus)){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku为空");
				}
				map.put("tuleType","sku");
				String[] skustr = skus.split(",");
				for(String sku:skustr){
					String platformSku = GeneratePlateformSku.getEbayPlateformSku(sku,8);

					List<EbayPublishListingVariant> list = this.getVariantByPlatformSku(platformSku,listingId);
					while (list!=null && list.size()>0){
						platformSku = GeneratePlateformSku.getEbayPlateformSku(sku,8);
						list = this.getVariantByPlatformSku(platformSku,listingId);
					}
					map.put(sku,platformSku);
				}
//			}
//		}
		return map;
	}


	private void check(PublishTemplate publishTemplate){
		if(publishTemplate.getSystemIs()==null){
			publishTemplate.setSystemIs(false);
		}
		if(publishTemplate.getPlatform()==null){
			publishTemplate.setPlatform(2);
		}
		if(publishTemplate.getTemplateType()==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "模板类型不能为空");
		}
		if(StringUtils.isBlank(publishTemplate.getTemplateName())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "模板名称不能为空");
		}else{
			if(!ValidatorUtil.isAlphanumericChinese(publishTemplate.getTemplateName())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "模板名称过长");
			}
		}
		if(publishTemplate.getDefaultIs()==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "是否默认不能为空");
		}
		int countNum = publishTemplateMapper.checktemplateName(publishTemplate.getId(),publishTemplate.getPlAccount(),publishTemplate.getTemplateName());
		if(countNum>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "模板名称重复");
		}
	}
}
