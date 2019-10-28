package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.basics.WarehouseListAddress;

public interface WarehouseListAddressMapper extends BaseMapper<WarehouseListAddress> {

    /**
     * 插入仓库地址信息  存在即修改
     * @param address 中间件不支持语法
     * @return
     */
    @Deprecated
    Integer replace(WarehouseListAddress address);
}