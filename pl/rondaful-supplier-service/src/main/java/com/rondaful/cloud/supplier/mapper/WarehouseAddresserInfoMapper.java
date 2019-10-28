package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.WarehouseAddresserInfo;

public interface WarehouseAddresserInfoMapper extends BaseMapper<WarehouseAddresserInfo> {

    /**
     * 根据入库单表序号查询揽收信息表
     *
     * @param parentSequenceNumber
     * @return
     */
    WarehouseAddresserInfo selectByParentSequenceNumber(String parentSequenceNumber);

    /**
     * 根据入库单表序号批量删除揽收信息表数据
     *
     * @param sequenceNumber
     */
    void deleteByParentSequenceNumber(String sequenceNumber);
}