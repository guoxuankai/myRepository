package com.rondaful.cloud.commodity.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.GoodCangCategory;
import com.rondaful.cloud.commodity.entity.GoodCangCategoryBind;
import com.rondaful.cloud.commodity.entity.SkuPushLog;
import com.rondaful.cloud.commodity.entity.SkuPushRecord;
import com.rondaful.cloud.common.mapper.BaseMapper;

public interface GoodCangMapper extends BaseMapper<GoodCangCategory>{
	
	void deleteCategory();
	
	/**
	 * @Description:插入分类
	 * @param list
	 * @return void
	 * @author:范津
	 */
	void insertCategory(GoodCangCategory category);

	
	/**
	 * @Description:批量插入分类
	 * @param list
	 * @return void
	 * @author:范津
	 */
	void insertBatchCategory(List<GoodCangCategory> list);

	/**
	 * @Description:查询分类list
	 * @param category
	 * @return
	 * @author:范津
	 */
	List<GoodCangCategory> pageCategory(GoodCangCategory category);
	
	/**
	 * @Description:根据品连三级分类查询绑定关系
	 * @param pinlianCategoty3Id
	 * @return
	 * @author:范津
	 */
	GoodCangCategoryBind getCategoryBindByCategoryId(@Param("pinlianCategoty3Id") Integer pinlianCategoty3Id);
	
	/**
	 * @Description:插入分类绑定
	 * @param bind
	 * @return void
	 * @author:范津
	 */
	void insertCategoryBind(GoodCangCategoryBind bind);
	
	/**
	 * @Description:更新分类绑定
	 * @param bind
	 * @return void
	 * @author:范津
	 */
	void updateCategoryBind(GoodCangCategoryBind bind);
	
	/**
	 * @Description:插入推送记录
	 * @param record
	 * @return void
	 * @author:范津
	 */
	void insertSkuPushRecord(SkuPushRecord record);
	
	/**
	 * @Description:批量插入推送记录
	 * @param list
	 * @return void
	 * @author:范津
	 */
	void insertSkuPushRecordBatch(List<SkuPushRecord> list);
	
	/**
	 * @Description:更新推送记录
	 * @param record
	 * @return void
	 * @author:范津
	 */
	void updateSkuPushRecord(SkuPushRecord record);
	
	/**
	 * @Description:获取推送记录列表
	 * @param record
	 * @return
	 * @author:范津
	 */
	List<SkuPushRecord> querySkuPushRecord(Map<String, Object> param);
	
	/**
	 * @Description:插入推送操作日志
	 * @param log
	 * @return void
	 * @author:范津
	 */
	void insertSkuPushLog(SkuPushLog log);
	
	/**
	 * @Description:获取操作日志
	 * @param recordId
	 * @return
	 * @author:范津
	 */
	List<SkuPushLog> querySkuPushLog(Map<String, Object> param);
	
}

