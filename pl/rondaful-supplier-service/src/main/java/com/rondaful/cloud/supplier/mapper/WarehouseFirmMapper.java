package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.basics.WarehouseFirm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarehouseFirmMapper extends BaseMapper<WarehouseFirm> {

    /**
     * 根据供应商id获取列表
     * @param supplierIds
     * @return
     */
    List<WarehouseFirm> getBySupplierId(@Param("supplierIds") List<Integer> supplierIds,@Param("status") Integer status);

    /**
     * 根据状态获取列表
     * @param status
     * @return
     */
    List<WarehouseFirm> getsByStatus(@Param("status") Integer status);

    /**
     * 根据服务商编码获取仓库code
     * @param firmCode
     * @return
     */
    List<WarehouseFirm> getsByFirmCode(@Param("firmCode") String firmCode,@Param("userId") Integer userId);

    /**
     * 根据账号
     * @param name
     * @return
     */
    WarehouseFirm getByName(String name);

    /**
     * 根据apptoken查询信息
     * @param appToken
     * @return
     */
    WarehouseFirm getsByAppToken(String appToken);

    /**
     * appKey
     * @param appKey
     * @return
     */
    WarehouseFirm getsByAppKey(String appKey);

    /**
     * 分页查询仓库列表
     * @param firmCode
     * @param supplierIds
     * @param name
     * @return
     */
    List<Integer> getPage(@Param("firmCode") String firmCode,@Param("supplierIds") List<Integer> supplierIds,@Param("id") Integer id);

}