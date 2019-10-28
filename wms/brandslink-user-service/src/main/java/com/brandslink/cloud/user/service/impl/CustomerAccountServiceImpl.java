package com.brandslink.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.CustomerDetails;
import com.brandslink.cloud.common.entity.CustomerUserDetailInfo;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.PhoneUtils;
import com.brandslink.cloud.user.dto.request.AddOrUpdateCustomerAccountRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerAccountRequestDTO;
import com.brandslink.cloud.user.dto.response.CustomerUserInfoResponseDTO;
import com.brandslink.cloud.user.entity.CustomerRoleInfo;
import com.brandslink.cloud.user.entity.CustomerUserInfo;
import com.brandslink.cloud.user.mapper.CustomerRoleInfoMapper;
import com.brandslink.cloud.user.mapper.CustomerUserInfoMapper;
import com.brandslink.cloud.user.service.ICustomerAccountService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 用户管理业务接口实现
 * @date 2019/9/4 11:40
 */
@Service
public class CustomerAccountServiceImpl implements ICustomerAccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Resource
    private CustomerUserInfoMapper customerUserInfoMapper;
    @Resource
    private CustomerRoleInfoMapper customerRoleInfoMapper;
    @Resource
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Override
    public List<Map<String, String>> getWarehouseList(String account) {
        return customerUserInfoMapper.selectWarehouseByAccount(account);
    }

    @Override
    public void restorePassword(Integer userId) {
        customerUserInfoMapper.restorePassword(userId, B_CRYPT_PASSWORD_ENCODER.encode(UserConstant.INITIAL_PASSWORD));
    }

    @Override
    public void updatePassword(String account, String oldPassword, String newPassword) {
        CustomerDetails customerDetails = getUserDetailInfoUtil.getCustomerDetails();
        if (StringUtils.equals(oldPassword, newPassword)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100014);
        }
        CustomerUserInfo customerUserInfo = customerUserInfoMapper.selectByAccount(account);
        if (null == customerUserInfo) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "账号不存在，请检查");
        } else if (!B_CRYPT_PASSWORD_ENCODER.matches(oldPassword, customerUserInfo.getPassword())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002);
        }
        customerUserInfo.setId(customerUserInfo.getId());
        customerUserInfo.setPassword(B_CRYPT_PASSWORD_ENCODER.encode(newPassword));
        customerUserInfo.setLastUpdateBy(customerDetails.getCustomerUserDetailInfo().getName());
        customerUserInfo.setLastUpdateTime(new Date());
        customerUserInfoMapper.updateByPrimaryKeySelective(customerUserInfo);
    }

    @Override
    public void updateAccountStatus(Integer userId, Integer status) {
        Integer result = customerUserInfoMapper.updateAccountStatus(userId, status);
        if (result != 1) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100402.getCode(), "修改账号状态失败，请重试");
        }
    }

    @Override
    public void bindAccountRole(Integer userId, List<Integer> roleIds) {
        LOGGER.info("账号：{}，绑定角色：{}", userId, roleIds.toString());
        if (CollectionUtils.isNotEmpty(roleIds)) {
            Set<Integer> roleSets = new HashSet<>(roleIds);
            customerRoleInfoMapper.deleteCustomerRoleByAccountId(userId);
            customerRoleInfoMapper.insertCustomerAccountRole(userId, roleSets);
        }
    }

    @Override
    public Page<CustomerUserInfoResponseDTO> getAccountList(CustomerAccountRequestDTO request) {
        LOGGER.info("查询客户端用户列表信息request：{}", JSON.toJSONString(request));
        CustomerUserDetailInfo customerUserDetailInfo = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo();
        request.setCustomerId(customerUserDetailInfo.getCustomerId());
        Page.builder(request.getPage(), request.getRow());
        List<CustomerUserInfoResponseDTO> customerUserInfos = customerUserInfoMapper.selectAccountDetail(request);
        if (CollectionUtils.isEmpty(customerUserInfos)) {
            PageInfo<CustomerUserInfoResponseDTO> pageInfo = new PageInfo<>(new ArrayList<>(0));
            return new Page<>(pageInfo);
        }
        List<Integer> idList = customerUserInfos.stream().map(CustomerUserInfoResponseDTO::getId).collect(Collectors.toList());
        // 这里的 customerId 字段暂时充当userId使用
        List<CustomerRoleInfo> roleInfos = customerRoleInfoMapper.selectByUserIdList(idList);
        customerUserInfos.forEach(c -> {
            if (c.getType().equals(0)) {
                c.setRoleList(new ArrayList<CustomerUserInfoResponseDTO.RoleDetail>() {{
                    add(new CustomerUserInfoResponseDTO.RoleDetail() {{
                        setRoleId(0);
                        setRoleName("系统管理员");
                    }});
                }});
            } else {
                List<CustomerRoleInfo> collect = roleInfos.stream().filter(r -> r.getCustomerId().equals(c.getId())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)) {
                    c.setRoleList(new ArrayList<>(0));
                } else {
                    List<CustomerUserInfoResponseDTO.RoleDetail> roleList = new ArrayList<>();
                    collect.forEach(cc -> {
                        CustomerUserInfoResponseDTO.RoleDetail detail = new CustomerUserInfoResponseDTO.RoleDetail() {{
                            setRoleId(cc.getId());
                            setRoleName(cc.getRoleName());
                        }};
                        roleList.add(detail);
                    });
                    c.setRoleList(roleList);
                }
            }
        });
        return new Page<>(new PageInfo<>(customerUserInfos));
    }

    @Override
    public void addAccount(AddOrUpdateCustomerAccountRequestDTO request) {
        LOGGER.info("添加账号request detail：{}", JSON.toJSONString(request));
        CustomerDetails customerDetails = getUserDetailInfoUtil.getCustomerDetails();
        CustomerUserInfo customerUserInfo = customerUserInfoMapper.selectByAccount(request.getAccount());
        if (null != customerUserInfo) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100003);
        }
        if (StringUtils.isNotBlank(request.getEmail())) {
            PhoneUtils.judgeEmail(request.getEmail());
        }
        String contactPhone = request.getContactPhone();
        if (StringUtils.isNotBlank(contactPhone)) {
            PhoneUtils.judgeContactWay(contactPhone);
            CustomerUserInfo customerInfo = customerUserInfoMapper.selectByContactWay(contactPhone);
            if (null != customerInfo) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100003.getCode(), "该手机号:" + contactPhone + ",已注册，请更换手机号");
            }
        }
        CustomerUserInfo customerUserReqInfo = new CustomerUserInfo() {{
            setEnabled(0);
            setType(1);
            setCustomerId(customerDetails.getCustomerInfoEntity().getId());
            setCreateBy(customerDetails.getCustomerUserDetailInfo().getName());
            setCreateTime(new Date());
        }};
        BeanUtils.copyProperties(request, customerUserReqInfo);
        customerUserReqInfo.setPassword(B_CRYPT_PASSWORD_ENCODER.encode(request.getPassword()));
        customerUserInfoMapper.insertSelective(customerUserReqInfo);
        if (CollectionUtils.isNotEmpty(request.getRoleIds())) {
            Set<Integer> roleSets = new HashSet<>(request.getRoleIds());
            customerRoleInfoMapper.deleteCustomerRoleByAccountId(customerUserReqInfo.getId());
            customerRoleInfoMapper.insertCustomerAccountRole(customerUserReqInfo.getId(), roleSets);
        }
    }

    @Override
    public void updateAccount(AddOrUpdateCustomerAccountRequestDTO request) {
        LOGGER.info("修改账号request detail：{}", JSON.toJSONString(request));
        if (StringUtils.isNotBlank(request.getContactPhone())) {
            PhoneUtils.judgeContactWay(request.getContactPhone());
        }
        if (StringUtils.isNotBlank(request.getEmail())) {
            PhoneUtils.judgeEmail(request.getEmail());
        }
        CustomerDetails customerDetails = getUserDetailInfoUtil.getCustomerDetails();
        CustomerUserInfo customerUserInfo = new CustomerUserInfo() {{
            setLastUpdateBy(customerDetails.getCustomerUserDetailInfo().getName());
            setLastUpdateTime(new Date());
        }};
        BeanUtils.copyProperties(request, customerUserInfo);
        customerUserInfoMapper.updateByPrimaryKeySelective(customerUserInfo);
        if (CollectionUtils.isNotEmpty(request.getRoleIds())) {
            Set<Integer> roleSets = new HashSet<>(request.getRoleIds());
            customerRoleInfoMapper.deleteCustomerRoleByAccountId(customerUserInfo.getId());
            customerRoleInfoMapper.insertCustomerAccountRole(customerUserInfo.getId(), roleSets);
        }
    }

}
