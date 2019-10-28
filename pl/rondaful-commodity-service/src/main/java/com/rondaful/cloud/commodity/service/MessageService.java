package com.rondaful.cloud.commodity.service;


/**
* @Description:模板消息
* @author:范津 
* @date:2019年5月24日 上午9:39:44
 */
public interface MessageService {
	
	/**
	 * @Description:商品价格变动
	 * @param userId 用户ID 多个用#拼接
	 * @param account 用户账号 多个用#拼接
	 * @param systemSku 品连sku
	 * @param platform 平台
	 * @return void
	 * @author:范津
	 */
	void priceChangeMsg(String userId,String account,String systemSku, String platform);
	
	/**
	 * @Description:商品下架
	 * @param userId 用户ID 多个用#拼接
	 * @param account 用户账号 多个用#拼接
	 * @param systemSku 品连sku
	 * @param platform 平台
	 * @return void
	 * @author:范津
	 */
	void downStateMsg(String userId,String account,String systemSku, String platform);
	
	/**
	 * @Description:商品侵权
	 * @param userId 用户ID 多个用#拼接
	 * @param account 用户账号 多个用#拼接
	 * @param systemSku 品连sku
	 * @param platform 平台
	 * @return void
	 * @author:范津
	 */
	void tortMsg(String userId,String account,String systemSku, String platform);
	
	/**
	 * @Description:待审核sku数量
	 * @param num
	 * @return void
	 * @author:范津
	 */
	void unAuditSkuNumMsg();
	
	/**
	 * @Description:待审核品牌数量
	 * @param num
	 * @return void
	 * @author:范津
	 */
	void unAuditBrandNumMsg();
}
