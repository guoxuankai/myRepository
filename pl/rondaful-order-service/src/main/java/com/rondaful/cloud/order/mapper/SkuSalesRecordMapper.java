package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.SkuSalesRecord;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface SkuSalesRecordMapper extends BaseMapper<SkuSalesRecord> {
    public Integer insertBatchSkuSalesRecord(List<SkuSalesRecord> skuSalesRecordList);
    public SkuSalesRecord statisSkuSales(SkuSalesRecord skuSalesRecord);
}