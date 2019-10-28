package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.model.InvoiceSpecificationModel;

public interface IInvoiceSpecificationService extends BaseService<InvoiceSpecificationModel> {

    Long editInvoiceSpecification(InvoiceSpecificationModel specificationModel);
}
