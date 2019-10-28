package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.order.entity.SkuSalesRecord;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ISkuSalesRecordService {

    public static String SKU_SALES_KEY="order:skusales.v0.";
    /**
     * 批量插入sku销售记录
     */
   public Integer insertBatchSkuSalesRecord(List<SkuSalesRecord> skuSalesRecordList);

    public Page<SkuSalesRecord> page(SkuSalesRecord skuSalesRd);


    public void exportSkuSalesRecordExcel(SkuSalesRecord param, HttpServletResponse response);

    public SkuSalesRecord statisSkuSales(SkuSalesRecord skuSalesRecord);
}
