package com.rondaful.cloud.seller.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.seller.entity.AmazonPublishReportTime;
import com.rondaful.cloud.seller.mapper.AmazonPublishReportTimeMapper;
import com.rondaful.cloud.seller.service.AmazonPublishReportTimeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmazonPublishReportTimeServiceImpl extends BaseServiceImpl<AmazonPublishReportTime> implements AmazonPublishReportTimeService {

    @Autowired
    private AmazonPublishReportTimeMapper amazonPublishReportTimeMapper;

    private static Logger logger = LoggerFactory.getLogger(AmazonPublishReportTimeServiceImpl.class);

    @Override
    public void addOrUpdateReportTime(AmazonPublishReportTime time) {
        if(StringUtils.isBlank(time.getMarketplaceId()) || StringUtils.isBlank(time.getMerchantId()))
            return;
        AmazonPublishReportTime param = new AmazonPublishReportTime();
        param.setMarketplaceId(time.getMarketplaceId());
        param.setMerchantId(time.getMerchantId());
        List<AmazonPublishReportTime> page = amazonPublishReportTimeMapper.page(param);
        if(page == null || page.size() == 0){
            amazonPublishReportTimeMapper.insertSelective(time);
        }else {
            time.setId(page.get(0).getId());
            amazonPublishReportTimeMapper.updateByPrimaryKeySelective(time);
        }
    }

    @Override
    public List<AmazonPublishReportTime> selectNoPage(AmazonPublishReportTime time) {
        return amazonPublishReportTimeMapper.page(time);
    }
}
