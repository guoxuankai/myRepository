package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.basics.WarehouseList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarehouseListMapper extends BaseMapper<WarehouseList> {

    /**
     * 批量插入仓库列表
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<WarehouseList> list);

    /**
     * 根据仓库标识删除仓库列表
     * @param firmId
     * @return
     */
    Integer deleteByFirmId(@Param("firmId") Integer firmId);

    /**
     * 根据服务商获取列表
     * @param firmId
     * @return
     */
    List<WarehouseList> getsByFirmId(@Param("firmId") Integer firmId,@Param("status") Integer status);

    /**
     * 根据服务商账号修改状态
     * @param firmId
     * @param status
     * @return
     */
    Integer updateStatusByFirmId(@Param("firmId") Integer firmId,@Param("status") Integer status);

    /**
     * 根据状态获取列表
     * @param firmId
     * @param status
     * @return
     */
    List<WarehouseList> getsByStatus(@Param("firmId") Integer firmId,@Param("status") Integer status);

    /**
     * 临时兼容老版需求
     * @param warehouseCode
     * @return
     */
    WarehouseList getByCode(@Param("warehouseCode") String warehouseCode,@Param("firmId") Integer firmId);

    /**
     * 根据id列表获取仓库列表
     * @param ids
     * @return
     */
    List<WarehouseList> getsByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据账号id及仓库编码获取仓库
     * @param firmId
     * @param warehouseCode
     * @return
     */
    WarehouseList selectByFIdCode(@Param("firmId") Integer firmId,@Param("warehouseCode") String warehouseCode);

    /**
     * 根据账号id获取仓库id
     * @param firmIds
     * @return
     */
    List<Integer> getsByFirmIds(@Param("firmIds") List<Integer> firmIds);

    /**
     * 分页查询仓库列表
     * @param firmIds
     * @param status
     * @return
     */
    List<WarehouseList> getPage(@Param("firmIds") List<Integer> firmIds,@Param("status") Integer status,@Param("ids") List<Integer> ids);

    /**
     * 修改仓库状态
     * @param warehouseIds
     * @param status
     * @param updateBy
     * @return
     */
    Integer updateStatus(@Param("warehouseIds") List<Integer> warehouseIds,@Param("status") Integer status);

}