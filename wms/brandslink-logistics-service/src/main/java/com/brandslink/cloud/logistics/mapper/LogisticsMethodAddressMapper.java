package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.model.LogisticsMethodAddressModel;

public interface LogisticsMethodAddressMapper extends BaseMapper<LogisticsMethodAddressModel> {

    void insertUpdateSelective(LogisticsMethodAddressModel addressModel);

    /**
     * 邮寄方式查询寄件人揽件人退货人时使用
     * @param addressModel
     * @return
     */
    LogisticsMethodAddressModel selectByID(LogisticsMethodAddressModel addressModel);
}