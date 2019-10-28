package com.brandslink.cloud.user.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.AddOrUpdateCustomerAccountRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerAccountRequestDTO;
import com.brandslink.cloud.user.dto.response.CustomerUserInfoResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 用户管理业务接口
 * @date 2019/9/4 11:34
 */
public interface ICustomerAccountService {

    /**
     * @description: 获取账号信息列表
     * @param request
     * @return
     */
    Page<CustomerUserInfoResponseDTO> getAccountList(CustomerAccountRequestDTO request);

    /**
     * @description: 添加子账号
     * @param request
     * @return
     */
    void addAccount(AddOrUpdateCustomerAccountRequestDTO request);

    /**
     * @description: 更修改账号信息
     * @param request
     * @return
     */
    void updateAccount(AddOrUpdateCustomerAccountRequestDTO request);

    /**
     * @description: 根据账号获取仓库
     * @param account
     * @return
     */
    List<Map<String, String>> getWarehouseList(String account);

    /**
     * @description: 重置用户密码
     * @param userId
     * @return
     */
    void restorePassword(Integer userId);

    /**
     * @description: 修改密码
     * @param account
     * @param oldPassword
     * @param newPassword
     * @return
     */
    void updatePassword(String account, String oldPassword, String newPassword);

    /**
     * @description: 修改账号状态
     * @param userId
     * @param status
     * @return
     */
    void updateAccountStatus(Integer userId, Integer status);

    /**
     * @description: 账号绑定角色
     * @param userId
     * @param roleIds
     * @return
     */
    void bindAccountRole(Integer userId, List<Integer> roleIds);
}
