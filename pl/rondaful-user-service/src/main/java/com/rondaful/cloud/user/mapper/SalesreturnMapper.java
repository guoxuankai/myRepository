package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.Salesreturn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface SalesreturnMapper extends BaseMapper<Salesreturn> {

    /**
     * 判断当前用户的退货信息是否存在
     * @param map
     * @return
     */
    Integer isSalesreturn(Map<String,Object> map);

    /**
     * 新增用户退货信息
     * @param salesreturn
     * @return
     */
    Integer insertSalesreturn(Salesreturn salesreturn);

    /**
     * 修改用户退货信息
     * @param salesreturn
     * @return
     */
    Integer updateSalesreturn(Salesreturn salesreturn);

    /**
     * 供应商个人中心===>获取退货信息
     * @param userId
     * @return
     */
    Salesreturn getSupplierSalesReturn(@Param("userId") Integer userId);

}