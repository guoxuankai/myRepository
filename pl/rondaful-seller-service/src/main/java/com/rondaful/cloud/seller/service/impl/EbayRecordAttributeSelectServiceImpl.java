package com.rondaful.cloud.seller.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.seller.entity.EbayPublishListingAttribute;
import com.rondaful.cloud.seller.entity.EbayPublishListingNew;
import com.rondaful.cloud.seller.entity.EbayRecordAttributeSelect;
import com.rondaful.cloud.seller.mapper.EbayPublishListingAttributeMapper;
import com.rondaful.cloud.seller.mapper.EbayPublishListingNewMapper;
import com.rondaful.cloud.seller.mapper.EbayRecordAttributeSelectMapper;
import com.rondaful.cloud.seller.service.EbayRecordAttributeSelectService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class EbayRecordAttributeSelectServiceImpl implements EbayRecordAttributeSelectService {

    private final Logger logger = LoggerFactory.getLogger(EbayRecordAttributeSelectServiceImpl.class);


	@Autowired
	private EbayRecordAttributeSelectMapper ebayRecordAttributeSelectMapper;
	@Autowired
	private EbayPublishListingNewMapper ebayPublishListingNewMapper;
	@Autowired
	private EbayPublishListingAttributeMapper ebayPublishListingAttributeMapper;


	@Override
	public void saveEbayRecordAttributeSelect(Long id){
		EbayPublishListingNew listing = ebayPublishListingNewMapper.selectByPrimaryKey(id);
		if(listing!=null){
			EbayPublishListingAttribute ebayPublishListingAttribute = new EbayPublishListingAttribute();
			ebayPublishListingAttribute.setListingId(Integer.valueOf(id+""));
			List<EbayPublishListingAttribute> listingAttributes = ebayPublishListingAttributeMapper.page(ebayPublishListingAttribute);
			if(listingAttributes!=null && listingAttributes.size()>0){
				for (EbayPublishListingAttribute attribute:listingAttributes){
					if(StringUtils.isEmpty(attribute.getAttributeValue())){
						continue;
					}
					EbayRecordAttributeSelect model = new EbayRecordAttributeSelect();
					model.setPlSpu(listing.getPlSpu());
					model.setCategoryId(Long.valueOf(listing.getProductCategory1()));
					model.setSite(listing.getSite());
					model.setAttributeVal(attribute.getAttributeKey());
					model.setAttributeSelectVal(attribute.getAttributeValue());
					model.setStatus(0);
					this.save(model);
				}
			}
		}
	}
	public void save(EbayRecordAttributeSelect model){
		List<EbayRecordAttributeSelect> asList = ebayRecordAttributeSelectMapper.page(model);
		Date date = new Date();
		try {
			if(asList!=null && asList.size()>0){
				EbayRecordAttributeSelect queryras=asList.get(0);
				EbayRecordAttributeSelect updateras= new EbayRecordAttributeSelect();
				updateras.setId(queryras.getId());
				updateras.setNumberAdd(queryras.getNumberAdd()+1);
				updateras.setUpdateTime(date);
				ebayRecordAttributeSelectMapper.updateByPrimaryKeySelective(updateras);
			}else {
				model.setCreateTime(date);
				model.setUpdateTime(date);
				model.setNumberAdd(1L);
				ebayRecordAttributeSelectMapper.insertSelective(model);
			}
		}catch (Exception e){
			//e.printStackTrace();
			logger.info("保存商品属性记录异常:"+e.getMessage());
		}

	}
	public List<EbayRecordAttributeSelect> getEbayRecordAttributeSelectByPublish(Long categoryId,String site,String plSpu){
		EbayRecordAttributeSelect model = new EbayRecordAttributeSelect();
		model.setCategoryId(categoryId);
		model.setSite(site);
		model.setPlSpu(plSpu);
		List<EbayRecordAttributeSelect> asList = ebayRecordAttributeSelectMapper.page(model);
		if(asList!=null) {
			List<EbayRecordAttributeSelect> retList = Lists.newArrayList();
			Map<String,EbayRecordAttributeSelect> map = Maps.newHashMap();
			for (EbayRecordAttributeSelect select:asList) {
				EbayRecordAttributeSelect ebayRecordAttributeSelect = map.get(select.getAttributeVal());
				if(ebayRecordAttributeSelect!=null){
					//比较数量
					if(ebayRecordAttributeSelect.getNumberAdd()<select.getNumberAdd()){
						map.put(select.getAttributeVal(),select);
					}else if(ebayRecordAttributeSelect.getNumberAdd()==select.getNumberAdd()){//数量相当
						//比较时间
						if(select.getUpdateTime().getTime()>ebayRecordAttributeSelect.getUpdateTime().getTime()){
							map.put(select.getAttributeVal(),select);
						}
					}
				}else {
					map.put(select.getAttributeVal(),select);
				}
			}
			for(Map.Entry<String,EbayRecordAttributeSelect> entry : map.entrySet()){
				retList.add(entry.getValue());
			}
			return retList;
		}

		return null;
	}

}
