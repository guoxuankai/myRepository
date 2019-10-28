package com.rondaful.cloud.transorder.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.transorder.entity.aliexpress.AliexpressOrderChild;
import com.rondaful.cloud.transorder.entity.ebay.EbayOrderDetail;

import java.util.List;

public interface EbayOrderDetailMapper extends BaseMapper<EbayOrderDetail> {

    List<EbayOrderDetail> getByParentId(String parentId);

}