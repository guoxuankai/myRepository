package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishReport;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AmazonPublishReportService extends BaseService<AmazonPublishReport> {

    /**
     * 不分页直接根据条件查询列表
     * @param report 查询条件
     * @return 报告数据列表
     */
    List<AmazonPublishReport> findAll(AmazonPublishReport report);

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
    List<AmazonPublishReport> selectNotFinishMessage(ReportTypeEnum reportType);

}
