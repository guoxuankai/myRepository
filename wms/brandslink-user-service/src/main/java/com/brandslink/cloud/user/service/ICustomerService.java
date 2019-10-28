package com.brandslink.cloud.user.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.entity.request.CustomerShipperDetailRequestDTO;
import com.brandslink.cloud.user.dto.request.AddOrUpdateShipperRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerForAuditInfoRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerForBasicInfoRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerSignInRequestDTO;
import com.brandslink.cloud.user.dto.response.CodeAndNameResponseDTO;
import com.brandslink.cloud.user.dto.response.CustomerInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.CustomerShipperDetailResponseDTO;
import com.brandslink.cloud.user.dto.response.DeveloperCentreInfoResponseDTO;
import com.brandslink.cloud.user.entity.CustomerInfo;
import com.brandslink.cloud.user.entity.ShipperInfo;

import java.util.List;
import java.util.Map;

/**
 * 客户
 *
 * @ClassName ICustomerService
 * @Author tianye
 * @Date 2019/7/16 11:26
 * @Version 1.0
 */
public interface ICustomerService {


    /**
     * 添加客户
     *
     * @param request
     */
    void addCustomer(CustomerForBasicInfoRequestDTO request);

    /**
     * 编辑客户信息-> 基本信息
     *
     * @param request
     */
    void updateCustomerForBasicInfo(CustomerForBasicInfoRequestDTO request);

    /**
     * 获取客户信息列表
     *
     * @param customerCode
     * @param customerName
     * @param status
     * @return
     */
    Page<CustomerInfo> getCustomerList(String customerCode, String customerName, String status, String auditStatus);

    /**
     * 根据客户id获取客户信息以及户主信息
     *
     * @param id
     * @return
     */
    CustomerInfoResponseDTO getCustomer(Integer id);

    /**
     * 获取客户名称以及货主名称  -> 内部调用
     *
     * @param list
     * @return
     */
    List<CustomerShipperDetailResponseDTO> getCustomerShipperDetail(List<CustomerShipperDetailRequestDTO> list);

    /**
     * 获取客户编码和名称
     *
     * @return
     */
    List<CodeAndNameResponseDTO> getCustomerCodeAndName();

    /**
     * 获取货主编码和名称
     *
     * @return
     */
    List<CodeAndNameResponseDTO> getShipperCodeAndName(String customerCode);

    /**
     * 添加货主
     *
     * @param request
     */
    void addShipper(AddOrUpdateShipperRequestDTO request);

    /**
     * 编辑货主
     *
     * @param request
     */
    void updateShipper(AddOrUpdateShipperRequestDTO request);

    /**
     * 获取货主名称
     *
     * @param shipperCodeList
     * @return
     */
    List<CodeAndNameResponseDTO> getShipperDetail(List<String> shipperCodeList);

    /**
     * 根据客户id查询货主列表
     *
     * @param customerId
     */
    List<ShipperInfo> getShipperByCustomerId(Integer customerId);

    /**
     * 根据客户编码获取客户信息
     *
     * @param customerCode
     * @return
     */
    CustomerInfoResponseDTO getCustomerByCustomerCode(String customerCode);

    /**
     * 客户注册
     *
     * @param request
     * @return
     */
    String insertSignIn(CustomerSignInRequestDTO request);

    /**
     * 获取验证码
     *
     * @param mobile
     * @param type
     */
    void getAuthCode(String mobile, Integer type);

    /**
     * 编辑客户信息-> 审核信息
     *
     * @param request
     */
    void updateCustomerForAuditInfo(CustomerForAuditInfoRequestDTO request);

    /**
     * 提交审核信息
     */
    void commitAuditInfo();

    /**
     * 获取当前登录用户货主编码和名称 -> oms系统
     *
     * @return
     */
    List<CodeAndNameResponseDTO> getShipperCodeAndNameForOMS();

    /**
     * 获取开发者中心信息
     *
     * @return
     */
    DeveloperCentreInfoResponseDTO getDeveloperCentreInfo();

    /**
     * 更改手机号
     *
     * @param mobile
     * @param authCode
     */
    void updatePhone(String mobile, String authCode);

    /**
     * 获取首页信息
     *
     * @return
     */
    Map<String, String> getHomePageInfo();

    /**
     * 审核操作
     *
     * @param id
     * @param auditStatus
     * @param auditFailedCause
     */
    void updateAudit(Integer id, Integer auditStatus, String auditFailedCause);

}
