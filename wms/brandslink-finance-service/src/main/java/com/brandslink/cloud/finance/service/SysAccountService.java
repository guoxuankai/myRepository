package com.brandslink.cloud.finance.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.finance.pojo.entity.SysAccount;

/**
 * @author yangzefei
 * @Classname SysAccountService
 * @Description 客户资金服务
 * @Date 2019/8/26 10:07
 */
public interface SysAccountService extends BaseService<SysAccount> {
    SysAccount get(Integer sysId);
}
