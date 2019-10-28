package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.procurement.Provider;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ProviderMapper extends BaseMapper<Provider> {

    /**
     * 分页查询数据
     * @param id
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    List<Provider> getsPage(@Param("supplierId") Integer supplierId,@Param("id") Integer id, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("status") Integer status);

    /**
     * 根据id获取列表
     * @param supplierId
     * @return
     */
    List<Provider> getsName(@Param("supplierId") Integer supplierId,@Param("levelThree") String levelThree);
}