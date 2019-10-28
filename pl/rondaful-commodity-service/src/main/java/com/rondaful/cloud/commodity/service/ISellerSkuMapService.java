package com.rondaful.cloud.commodity.service;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.commodity.entity.SellerSkuMap;
import com.rondaful.cloud.commodity.vo.QuerySkuMapForOrderVo;
import com.rondaful.cloud.commodity.vo.SkuMapAddVo;
import com.rondaful.cloud.commodity.vo.SkuMapUpdateStatusVo;
import com.rondaful.cloud.common.entity.Page;


public interface ISellerSkuMapService {
	
	/**
	 * @Description:增加导入记录
	 * @param files
	 * @return void
	 * @author:范津
	 */
	Map<String, Object> addByExcel(MultipartFile[] files);
	
    
    /**
     * @Description:查询列表
     * @param skuMap
     * @return
     * @author:范津
     */
    Page<SellerSkuMap> page(SellerSkuMap skuMap);
    
    /**
     * @Description:新增
     * @param skuMap
     * @param isPublish 是否卖家端刊登调用
     * @return void
     * @author:范津
     */
    void addSkuMap(SellerSkuMap skuMap);
    
    /**
     * @Description:编辑
     * @param skuMap
     * @return void
     * @author:范津
     */
    void updateSkuMap(SellerSkuMap skuMap);
    
    /**
     * @Description:删除
     * @param id
     * @return void
     * @author:范津
     */
    void deleteByPrimaryKey(Long id);
    
    /**
     * @Description:获取sku的名称和属性
     * @param systemSku
     * @return
     * @author:范津
     */
    JSONObject getSkuNameAndSpec(String systemSku);
    
    /**
     * @Description:根据平台sku获取sku映射信息
     * @param platformSku
     * @return
     * @author:范津
     */
    JSONObject getSkuMapByPlatformSku(String platform,String platformSku,String authorizationId,boolean flag);
    
    /**
     * @Description:sku映射导入结果明细导出
     * @param importId
     * @param response
     * @return void
     * @author:范津
     */
    public void exportImportLogExcel(Long importId,HttpServletResponse response);
    
    /**
     * @Description:订单查询平台sku映射
     * @param voList
     * @return
     * @author:范津
     */
    JSONArray getSkuMapForOrder(List<QuerySkuMapForOrderVo> voList);
    
    /**
     * @Description:新增
     * @param skuMap
     * @return void
     * @author:范津
     */
    void addSkuMapForSeller(List<SkuMapAddVo> voList);
    
    /**
     * @Description:更新状态
     * @param vo
     * @return void
     * @author:范津
     */
    void updateMapStatus(SkuMapUpdateStatusVo vo);
}
