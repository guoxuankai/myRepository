package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AmazonPublishReportDetail;

public interface AmazonPublishReportDetailMapper extends BaseMapper<AmazonPublishReportDetail> {

    /**
     * 查询version逆序排序第一个数据
     * @param detail 参数
     * @return 结果
     */
    AmazonPublishReportDetail selectLastOne(AmazonPublishReportDetail detail);


    /**
     * 通过卖家ID和站点ID删除比传入版本小的数据
     * @param detail 参数对象
     * @return 返回结果
     */
    int deleteByVersion(AmazonPublishReportDetail detail);


}