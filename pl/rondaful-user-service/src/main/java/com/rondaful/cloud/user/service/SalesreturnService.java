package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.entity.Salesreturn;
import org.apache.ibatis.annotations.Mapper;

public interface SalesreturnService {

    /**
     * 判断当前用户的的退货信息是否注册
     * @param userId
     * @return
     */
    Integer isSalesreturn(Integer userId, Integer platformType);

    /**
     * 新增用户退货信息
     * @return
     */
    Integer insertSalesreturn(Salesreturn salesreturn, Integer platformType);

    /**
     * 修改用户退货信息
     * @param salesreturn
     * @return
     */
    Integer updateSalesreturn(Salesreturn salesreturn,Integer platformType);



}
