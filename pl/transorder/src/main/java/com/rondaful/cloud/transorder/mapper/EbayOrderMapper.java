package com.rondaful.cloud.transorder.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.transorder.entity.ebay.EbayOrder;

import java.util.List;

public interface EbayOrderMapper extends BaseMapper<EbayOrder> {

    List<EbayOrder> getsByOrderIds(List<String> orderIds);

}