package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.seller.entity.AmazonListingExportRecord;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;

import java.io.IOException;
import java.text.ParseException;

public interface AmazonListingExportRecordService   extends BaseService<AmazonListingExportRecord> {
    void
    export(AmazonPublishListing model,String exportType) throws IOException, ParseException;
}
