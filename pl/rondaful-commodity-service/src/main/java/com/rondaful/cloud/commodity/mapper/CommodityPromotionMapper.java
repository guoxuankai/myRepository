package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.CommodityPromotion;

import java.util.List;
import java.util.Map;

public interface CommodityPromotionMapper {

	//添加商品到产品推广表
	Integer addProductPromotion(List<CommodityPromotion> list);
	
	//条件搜索
	List<String> searchPromotion(Map params);

	//查看按钮
	CommodityPromotion getPromotion(Integer promotionId);


	/**功能描述
	 * @date
	 * @param promotionId
	 * @return promotionId
	 * description: 批量删除商品推广数据
	 */
	Integer deleteByPromotionId(List<Integer> promotionId);

    //获取所有的数据
	List<CommodityPromotion> getAll();
}
