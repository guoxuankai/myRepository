package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.seller.entity.AmazonPublishReportTime;

import java.util.List;

public interface AmazonPublishReportTimeService extends BaseService<AmazonPublishReportTime> {

    /**
     * 添加或者更新报告最后时间
     * @param time 报告时间数据
     */
    public void addOrUpdateReportTime(AmazonPublishReportTime time);

    /**
     * 查询报告列表，不分页
     * @param time 参数
     * @return 报告列表
     */
    public List<AmazonPublishReportTime> selectNoPage(AmazonPublishReportTime time);


}
