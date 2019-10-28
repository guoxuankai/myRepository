package com.rondaful.cloud.commodity.service;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.GoodCangCategory;
import com.rondaful.cloud.commodity.entity.GoodCangCategoryBind;
import com.rondaful.cloud.commodity.entity.SkuPushLog;
import com.rondaful.cloud.commodity.entity.SkuPushRecord;
import com.rondaful.cloud.common.entity.Page;

public interface GoodCangService {

	/**
	 * @Description:获取谷仓分类列表
	 * @return
	 * @author:范津
	 */
	List<GoodCangCategory> findList();
	
	/**
	 * @Description:谷仓分类和品连分类映射绑定
	 * @param bind
	 * @return void
	 * @author:范津
	 */
	void addOrUpdateCategoryBind(GoodCangCategoryBind bind);
	
	/**
	 * @Description:推送商品到谷仓
	 * @param accountId 账号ID
	 * @param type  接口类型:1新增，2编辑
	 * @param skuList 品连sku数组
	 * @return void
	 * @author:范津
	 */
	Map<String, Object> pushSkusToGoodCang(Integer accountId,int type,List<CommoditySpec> commoditySpecs,String optUser);
	
	
	/**
	 * @Description:获取推送记录列表
	 * @param record
	 * @return
	 * @author:范津
	 */
	Page<SkuPushRecord> getSkuPushRecordPage(Map<String, Object> param);
	
	/**
	 * @Description:获取操作日志
	 * @param recordId
	 * @return
	 * @author:范津
	 */
	Page<SkuPushLog> querySkuPushLog(Map<String, Object> param);
	
	/**
	 * @Description:商品列表-全部推送
	 * @param accountId
	 * @return
	 * @author:范津
	 */
	Map<String, Object> pushAll(Integer accountId,Integer status);
	
	/**
	 * @Description:仓库商品列表-批量推送
	 * @param ids
	 * @return
	 * @author:范津
	 */
	void pushBatch(List<Long> ids);
	
	/**
	 * @Description:推送选中的商品到谷仓
	 * @param accountId 账号ID
	 * @param type  接口类型:1新增，2编辑
	 * @param skuList 品连sku数组
	 * @return void
	 * @author:范津
	 */
	Map<String, Object> pushSelectedSkusToGoodCang(Integer accountId,int type,List<String> skuList);
	
	/**
	 * @Description:根据账号获取appKey和Token
	 * @param accountId
	 * @return
	 * @return Map<String,String>
	 * @author:范津
	 */
	Map<String, String> getAppkeyAndTokenByAccountId(Integer accountId);
	
	
	void addAllProductByPage(Integer accountId,int total,Long supplierId,String optUser,Integer status);
	
}
