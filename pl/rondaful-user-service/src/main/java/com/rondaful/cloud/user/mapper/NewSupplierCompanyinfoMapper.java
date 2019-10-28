package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.NewSupplierCompanyinfo;
import org.apache.ibatis.annotations.Param;

public interface NewSupplierCompanyinfoMapper extends BaseMapper<NewSupplierCompanyinfo> {

    /**
     * 根据用户id获取公司信息
     * @param userId
     * @return
     */
    NewSupplierCompanyinfo selectByUserId(@Param("userId") Integer userId);
}