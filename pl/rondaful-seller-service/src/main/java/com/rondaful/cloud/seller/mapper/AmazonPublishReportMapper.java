package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AmazonPublishReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AmazonPublishReportMapper extends BaseMapper<AmazonPublishReport> {


    /**
     * 根据条件选出更新时间最前面的数据
     * @param report 报告参数
     * @return 报告结果
     */
    AmazonPublishReport selectFirstOne( AmazonPublishReport report);

    /**
     * 查询某种未处理完成的报告列表
     * @param reportType 报告类型
     * @return 报告列表
     */
    List<AmazonPublishReport> selectNotFinishMessage(@Param("reportType") String reportType);




}