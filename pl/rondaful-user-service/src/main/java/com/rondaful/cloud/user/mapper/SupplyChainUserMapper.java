package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.SupplyChainUser;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SupplyChainUserMapper extends BaseMapper<SupplyChainUser> {

    /**
     * 分页查询供应链公司信息
     * @param dataType
     * @param startTime
     * @param endTime
     * @param companyName
     * @return
     */
    List<SupplyChainUser> getsPage(@Param("dataType") Integer dataType, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("companyName") String companyName);

    /**
     * 根据状态获取所有供应链
     * @param status
     * @return
     */
    List<SupplyChainUser> getAll(@Param("status") Integer status);

}