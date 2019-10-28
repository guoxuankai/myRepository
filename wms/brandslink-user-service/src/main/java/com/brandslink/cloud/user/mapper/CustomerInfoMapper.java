package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.common.entity.request.CustomerShipperDetailRequestDTO;
import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.user.dto.response.CustomerShipperDetailResponseDTO;
import com.brandslink.cloud.user.dto.response.RoleInfoResponseDTO;
import com.brandslink.cloud.user.entity.CustomerInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerInfoMapper extends BaseMapper<CustomerInfo> {

    /**
     * 获取所有客户信息（编码、名称）列表
     *
     * @return
     */
    List<CustomerInfo> selectAll();

    /**
     * 根据客户编码查询客户中文名称
     *
     * @param customerCodeList
     * @return
     */
    List<CustomerShipperDetailResponseDTO> selectCustomerChineseNamesByCustomerCodes(List<String> customerCodeList);

    /**
     * 根据客户编码以及货主编码查询货主名称
     *
     * @param list
     * @return
     */
    List<CustomerShipperDetailResponseDTO> selectShipperNamesByCustomerCodeAndShipperCodes(List<CustomerShipperDetailRequestDTO> list);

    /**
     * 添加客户货主关联表
     *
     * @param customerId
     * @param id
     */
    void insertCustomerShipper(@Param("customerId") Integer customerId, @Param("id") Integer id);

    /**
     * 根据客户code查询客户信息
     *
     * @param customerCode
     * @return
     */
    CustomerInfo selectByCustomerCode(@Param("customerCode") String customerCode);

    /**
     * 根据客户名称查询客户信息
     *
     * @param chineseName
     * @param id
     * @return
     */
    CustomerInfo selectByCustomerName(@Param("chineseName") String chineseName, @Param("id") Integer id);

    /**
     * 根据客户id查询客户编码
     *
     * @param customerId
     * @return
     */
    String selectCustomerCodeByCustomerId(@Param("customerId") Integer customerId);

    /**
     * 根据客户id查询客户信息（包含所属仓库）
     *
     * @param customerId
     * @return
     */
    List<CustomerInfo> selectCustomerDetailAndWarehouseInfoByPrimaryKey(@Param("customerId") Integer customerId);

    /**
     * 更新客户以及主账号手机号
     *
     * @param mobile
     */
    void updatePhone(@Param("customerId") Integer customerId, @Param("mobile") String mobile);

    /**
     * 根据客户id删除对应所属仓库
     *
     * @param id
     */
    void deleteWarehouseByCustomerId(Integer id);

    /**
     * 根据客户id添加所属仓库信息
     *
     * @param id
     * @param warehouseList
     */
    void insertWarehouseByCustomerId(@Param("id") Integer id, @Param("list") List<RoleInfoResponseDTO.WarehouseDetail> warehouseList);
}