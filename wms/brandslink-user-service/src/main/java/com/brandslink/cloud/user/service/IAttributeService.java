package com.brandslink.cloud.user.service;

import com.brandslink.cloud.user.entity.Attribute;
import com.brandslink.cloud.common.service.BaseService;

public interface IAttributeService extends BaseService<Attribute> {
    Object updateAttribute(Attribute attribute) throws InterruptedException;
}
