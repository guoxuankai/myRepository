package com.rondaful.cloud.seller.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.seller.entity.AmazonPublishReportDetail;
import com.rondaful.cloud.seller.mapper.AmazonPublishReportDetailMapper;
import com.rondaful.cloud.seller.service.AmazonPublishReportDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmazonPublishReportDetailServiceImpl extends BaseServiceImpl<AmazonPublishReportDetail> implements AmazonPublishReportDetailService {

    @Autowired
    private AmazonPublishReportDetailMapper amazonPublishReportDetailMapper;


    @Override
    public AmazonPublishReportDetail selectLastOne(AmazonPublishReportDetail detail) {
        return amazonPublishReportDetailMapper.selectLastOne(detail);
    }

    @Override
    public int deleteByVersion(AmazonPublishReportDetail detail) {
        return amazonPublishReportDetailMapper.deleteByVersion(detail);
    }
}
