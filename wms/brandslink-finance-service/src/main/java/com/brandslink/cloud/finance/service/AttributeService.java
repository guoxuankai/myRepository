package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.finance.pojo.entity.Attribute;
import com.brandslink.cloud.common.service.BaseService;

public interface AttributeService extends BaseService<Attribute> {
    Object updateAttribute(Attribute attribute) throws InterruptedException;
}
