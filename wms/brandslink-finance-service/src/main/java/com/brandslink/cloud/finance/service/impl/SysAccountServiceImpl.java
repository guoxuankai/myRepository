package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.mapper.SysAccountMapper;
import com.brandslink.cloud.finance.pojo.entity.SysAccount;
import com.brandslink.cloud.finance.service.SysAccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yangzefei
 * @Classname SysAccountServiceImpl
 * @Description 客户资金流水服务
 * @Date 2019/8/26 10:08
 */
@Service
public class SysAccountServiceImpl extends BaseServiceImpl<SysAccount> implements SysAccountService {

    @Resource
    private SysAccountMapper sysAccountMapper;
    public SysAccount get(Integer sysId){
        return sysAccountMapper.selectByPrimaryKey((long)sysId);
    }
}
