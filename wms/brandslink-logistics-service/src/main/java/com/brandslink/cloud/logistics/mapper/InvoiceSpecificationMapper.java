package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.model.InvoiceSpecificationModel;

public interface InvoiceSpecificationMapper extends BaseMapper<InvoiceSpecificationModel> {

    void insertUpdateSelective(InvoiceSpecificationModel specificationModel);
}