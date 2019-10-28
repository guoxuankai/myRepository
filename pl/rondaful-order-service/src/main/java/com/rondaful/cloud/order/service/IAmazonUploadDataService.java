package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.order.entity.Amazon.AmazonUploadData;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.text.ParseException;

public interface IAmazonUploadDataService extends BaseService<AmazonUploadData> {
    void queryAmazonUploadDataAndDeal();

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    void getResultAndUpdateStatus(String feedType, String marketplaceId, String amazonSellerId, String mwsToken, String maxTime, String minTime)
            throws ParseException, InterruptedException, FileNotFoundException;
}
