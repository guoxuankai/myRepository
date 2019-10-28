package com.rondaful.cloud.commodity.service;

import com.rondaful.cloud.commodity.entity.CommodityPromotion;
import com.rondaful.cloud.common.entity.Page;

import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: IProductPromotionService
 * @Description: 产品推广service接口
 * @author lz
 * @date 2018年12月10日 上午9:28:26
 */
public interface ICommodityPromotionService {
	// 添加商品到产品推广表
	Integer addProductPromotion(List<CommodityPromotion> list);




	// 条件搜索
	Page<String> searchPromotion(Map params);

	//查看按钮
	CommodityPromotion getPromotion(Integer promotionId);


	 /* *功能描述
	  * @date
	  * @param [spu]
	  * @return [spu]
	  * description:商品服务删除数据
	  */
	Integer deleteByPromotionId(List<Integer> promotionId);



	//获取所有的数据
	List<CommodityPromotion> getAll();
}
