package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.user.dto.request.CustomerAccountRequestDTO;
import com.brandslink.cloud.user.dto.response.CustomerUserInfoResponseDTO;
import com.brandslink.cloud.user.entity.CustomerUserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomerUserInfoMapper extends BaseMapper<CustomerUserInfo> {

    /**
     * 根据账号查询账户信息（包含角色信息）
     *
     * @param username
     * @return
     */
    List<CustomerUserInfo> selectByAccountResult(String username);

    /**
     * 根据账号查询账户信息
     *
     * @param account
     * @return
     */
    CustomerUserInfo selectByAccount(String account);

    /**
     * 根据手机号查询账户信息（包含角色信息）
     *
     * @param mobile
     * @return
     */
    List<CustomerUserInfo> selectByMobileResult(String mobile);

    /**
     * 根据手机号查询账户信息
     *
     * @param mobile
     * @return
     */
    CustomerUserInfo selectByContactWay(String mobile);

    /**
     * @param request
     * @return
     * @description: 账号信息查询（支持分页,用户名模糊搜索且不包含密码）
     */
    List<CustomerUserInfoResponseDTO> selectAccountDetail(CustomerAccountRequestDTO request);

    /**
     * @param account
     * @return
     * @description: 根据用户名获取获取仓库
     */
    List<Map<String, String>> selectWarehouseByAccount(String account);

    /**
     * 通过账户id查询所属客户id
     *
     * @param id
     * @return
     */
    Integer selectCustomerIdByPrimaryKey(Integer id);

    /**
     * @param userId
     * @description: 更新用户密码
     */
    void restorePassword(@Param("userId") Integer userId, @Param("password") String password);

    /**
     * @param userId
     * @param status
     * @return
     * @description: 更新账号状态
     */
    Integer updateAccountStatus(@Param("userId") Integer userId, @Param("status") Integer status);

    /**
     * 根据客户id查询主账号id
     *
     * @param customerId
     * @return
     */
    Integer selectPrimaryAccountIdByCustomerId(@Param("customerId") Integer customerId);
}