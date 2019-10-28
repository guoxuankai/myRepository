package com.rondaful.cloud.commodity.service.impl;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.entity.CommodityPromotion;
import com.rondaful.cloud.commodity.mapper.CommodityPromotionMapper;
import com.rondaful.cloud.commodity.service.ICommodityPromotionService;
import com.rondaful.cloud.common.entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 
* @ClassName: ProductPromotionImpl 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author lz
* @date 2018年12月10日 上午9:29:31
 */
@Service
public class CommodityPromotionImpl implements ICommodityPromotionService {

	//注入mapper
	@Autowired
	private CommodityPromotionMapper promotionMapper;

	@Override
	public Integer addProductPromotion(List<CommodityPromotion> list) {

		return promotionMapper.addProductPromotion(list);
	}

	@Override
	public Integer deleteByPromotionId(List<Integer> promotionId) {
		return promotionMapper.deleteByPromotionId(promotionId);
	}

	@Transactional
	@Override
	public Page<String> searchPromotion(Map params) {
		List<String> CommodityPromotions = promotionMapper.searchPromotion(params);
		PageInfo<CommodityPromotion> pageInfo = new PageInfo(CommodityPromotions);
		return new Page(pageInfo);
	}

	/**
	 * <p>Title: getSpuBysaleType</p>
	 * <p>Description: 根据销售类型查询spu</p>
	 *
	 * @param
	 * @return
	 */

	@Override
	public CommodityPromotion getPromotion(Integer promotionId) {
		// TODO Auto-generated method stub
		return promotionMapper.getPromotion(promotionId);
	}

	@Override
	public List<CommodityPromotion> getAll() {
		return promotionMapper.getAll();
	}
}


