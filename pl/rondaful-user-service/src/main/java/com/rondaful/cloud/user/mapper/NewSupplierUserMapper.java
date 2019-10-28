package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.NewSupplierUser;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface NewSupplierUserMapper extends BaseMapper<NewSupplierUser> {


    /**
     * 后台一级账号查询
     * @param status
     * @param companyNameUser
     * @param supplyChainCompany
     * @param dateType
     * @param startTime
     * @param endTime
     * @return
     */
    List<NewSupplierUser> getPageBack(@Param("status") Integer status, @Param("userIds") List<Integer> userIds, @Param("companyNameUser") String companyNameUser,
                                      @Param("supplyChainCompany") String supplyChainCompany, @Param("dateType") Integer dateType,@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    /**
     * 获取子账号列表
     * @param loginName
     * @param userName
     * @param departmentId
     * @param Jobs
     * @param startTime
     * @param endTime
     * @param userIds
     * @param topUserId
     * @return
     */
    List<NewSupplierUser> getChildPage(@Param("loginName") String loginName,@Param("userName") String userName,@Param("departmentId") Integer departmentId,
                                       @Param("jobName") String jobName,@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("userIds") List<Integer> userIds,@Param("topUserId") Integer topUserId);

    /**
     * 根据登录名查询用户
     * @param loginName
     * @return
     */
    NewSupplierUser getByName(@Param("loginName") String loginName);

    /**
     * 查询顶级账号
     * @param userIds
     * @return
     */
    List<NewSupplierUser> getTopUser(@Param("userIds") List<Integer> userIds);

    /**
     * 根据主账号获取所有子集账号名(仅临时调用)
     * @param userId
     * @return
     */
    List<NewSupplierUser> getChildName(@Param("userId") Integer userId);

    /**
     * 根据手机号获取信息
     * @param phone
     * @return
     */
    NewSupplierUser getByPhone(String phone);

    /**
     * 根据邮箱获取信息
     * @param email
     * @return
     */
    NewSupplierUser getByEmail(String email);

    /**
     * 查询组织是否绑定
     * @param departmentId
     * @return
     */
    Integer getsByDep(@Param("departmentId") Integer departmentId);

    /**
     * 根据状态获取主供应商列表
     * @return
     */
    List<NewSupplierUser> getsByStatus(Integer status);

    /**
     * 根据供应链公司获取绑定总数
     * @param supplyChainId
     * @return
     */
    Integer getTotalAccount(@Param("supplyChainId") String supplyChainId);
}