package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.logistics.entity.Attribute;
import com.brandslink.cloud.common.service.BaseService;

public interface IAttributeService extends BaseService<Attribute> {
    Object updateAttribute(Attribute attribute) throws InterruptedException;
}
