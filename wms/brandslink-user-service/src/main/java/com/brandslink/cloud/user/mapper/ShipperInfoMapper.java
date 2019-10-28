package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.user.entity.ShipperInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShipperInfoMapper extends BaseMapper<ShipperInfo> {

    /**
     * 批量插入货主信息
     *
     * @param shipperInfoList
     */
    void insertList(List<ShipperInfo> shipperInfoList);

    /**
     * 根据客户id获取所属户主信息
     *
     * @param id
     * @return
     */
    List<ShipperInfo> selectByCustomerId(Integer id);

    /**
     * 获取所有货主信息（编码、名称）列表
     *
     * @return
     */
    List<ShipperInfo> selectAll(@Param("customerCode") String customerCode);

    /**
     * 根据货主编码查询货主信息
     *
     * @param shipperCodeList
     * @return
     */
    List<ShipperInfo> selectByShipperCodeList(@Param("list") List<String> shipperCodeList);

    /**
     * 根据货主编码以及客户id查询货主信息
     *
     * @param shipperCode
     * @return
     */
    ShipperInfo selectByShipperCode(@Param("shipperCode") String shipperCode, @Param("customerId") Integer customerId);

    /**
     * 根据货主名称以及客户id查询货主信息
     *
     * @param shipperName
     * @param customerId
     * @return
     */
    ShipperInfo selectByShipperName(@Param("shipperName") String shipperName, @Param("customerId") Integer customerId, @Param("shipperId") Integer shipperId);

    /**
     * 根据客户id查询货主列表
     *
     * @param customerId
     * @return
     */
    List<ShipperInfo> selectShipperListByCustomerId(@Param("customerId") Integer customerId);
}