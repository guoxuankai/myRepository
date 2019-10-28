package com.rondaful.cloud.seller.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.seller.entity.AmazonPublishReport;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import com.rondaful.cloud.seller.mapper.AmazonPublishReportMapper;
import com.rondaful.cloud.seller.service.AmazonPublishReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmazonPublishReportServiceImpl extends BaseServiceImpl<AmazonPublishReport> implements AmazonPublishReportService {

    @Autowired
    private AmazonPublishReportMapper amazonPublishReportMapper;

    @Override
    public List<AmazonPublishReport> findAll(AmazonPublishReport report) {
        return amazonPublishReportMapper.page(report);
    }

    @Override
    public AmazonPublishReport selectFirstOne(AmazonPublishReport report) {
        return amazonPublishReportMapper.selectFirstOne(report);
    }

    @Override
    public List<AmazonPublishReport> selectNotFinishMessage(ReportTypeEnum reportType) {
        String reportTyp = reportType.getReportTyp();
        return amazonPublishReportMapper.selectNotFinishMessage(reportTyp);
    }
}
