package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.storage.StorageAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StorageAddressMapper extends BaseMapper<StorageAddress> {

    /**
     * 根据供应商id获取仓库列表
     * @param supplierId
     * @return
     */
    List<StorageAddress> getsBySupplierId(@Param("supplierId") Integer supplierId,@Param("phone") String phone);
}