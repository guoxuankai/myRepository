package com.brandslink.cloud.finance.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.finance.pojo.entity.SysAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface SysAccountMapper extends BaseMapper<SysAccount> {

    Boolean updateByRecharge(@Param("sysId") Integer sysId,@Param("money") BigDecimal money);
}