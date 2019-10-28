package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.seller.entity.EbayRecordAttributeSelect;

import java.util.List;

public interface EbayRecordAttributeSelectService {

    void saveEbayRecordAttributeSelect(Long id);

    List<EbayRecordAttributeSelect> getEbayRecordAttributeSelectByPublish(Long categoryId, String site, String plSpu);
}
