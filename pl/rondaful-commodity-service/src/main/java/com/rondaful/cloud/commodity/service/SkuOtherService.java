package com.rondaful.cloud.commodity.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.rondaful.cloud.commodity.entity.BindCategoryAliexpress;
import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.SkuImport;
import com.rondaful.cloud.common.entity.Page;

public interface SkuOtherService {

	/**
	 * @Description:excel导入新增
	 * @param baseList
	 * @param optUser
	 * @param supplierId
	 * @param importId
	 * @param isAdmin
	 * @return
	 * @author:范津
	 */
	String addSkuByExcel(List<CommodityBase> baseList,String optUser,Long supplierId,Long importId,boolean isAdmin);
	
	/**
	 * @Description:excel导入编辑
	 * @param baseList
	 * @param optUser
	 * @param supplierId
	 * @param importId
	 * @param isAdmin
	 * @return
	 * @author:范津
	 */
	String updateSkuByExcel(List<CommodityBase> baseList,String optUser,Long supplierId,Long importId);
	
	/**
     * @Description:导出sku
     * @param param 查询参数map
     * @param response
     * @return void
     * @author:范津
     */
    public InputStream exportSkuExcel(Map<String, Object> param,boolean isEn);
    
    /**
     * @Description:新增导入sku任务
     * @param files
     * @return void
     * @author:范津
     */
    void addImportTask(MultipartFile[] files,Long supplierId,Integer imType);
    
    /**
     * @Description:删除导入任务
     * @param id
     * @return void
     * @author:范津
     */
    void delTask(Long id);
    
    /**
     * @Description:查询导入任务列表
     * @param param
     * @return
     * @author:范津
     */
    Page<SkuImport> querySkuImportList(Map<String, Object> param);
    
    
    /**
     * @Description:sku导入结果明细导出
     * @param importId
     * @param response
     * @return void
     * @author:范津
     */
    public void exportImportLogExcel(Long importId,HttpServletResponse response);
    
    
    /**
     * @Description:速卖通分类映射绑定
     * @param bind
     * @return void
     * @author:范津
     */
    void addOrUpdateCategoryBindAli(BindCategoryAliexpress bind);
    
    /**
     * @Description:导出sku放到下载中心
     * @param param
     * @param isEn
     * @return void
     * @author:范津
     */
    void asynExportSku(Map<String, Object> param, boolean isEn,Integer userId,Integer topUserId,Integer platformType,String optUser);
}
