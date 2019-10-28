package com.rondaful.cloud.transorder.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.transorder.entity.ebay.EbayOrderDetail;
import com.rondaful.cloud.transorder.entity.ebay.EbayOrderStatus;

public interface EbayOrderStatusMapper extends BaseMapper<EbayOrderStatus> {

    EbayOrderStatus getByParentId(String parentId);

    void updateByOrderId(EbayOrderStatus ebayOrderStatus);
}