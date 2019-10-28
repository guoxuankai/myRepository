package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.ManageUser;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ManageUserMapper extends BaseMapper<ManageUser> {

    /**
     * 根据用户名查询用户信息
     * @param loginName
     * @return
     */
    ManageUser selectByUserName(String loginName);

    /**
     * 分页查询用户
     * @param loginName
     * @param userName
     * @param departmentId
     * @param Jobs
     * @param startTime
     * @param endTime
     * @return
     */
    List<ManageUser> getPage(@Param("loginName") String loginName,@Param("userName") String userName,@Param("departmentId") Integer departmentId,
                             @Param("jobName") String jobName,@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("userIds") List<Integer> userIds);

    /**
     * 获取所有用户名
     * @return
     */
    List<String> getAllName();

    /**
     * 根据手机号查询
     * @param phone
     * @return
     */
    ManageUser getByPhone(@Param("phone") String phone);

    /**
     * 查询组织是否绑定
     * @param departmentId
     * @return
     */
    Integer getsByDep(@Param("departmentId") Integer departmentId);

    /**
     * 获取子账号名称
     * @return
     */
    List<ManageUser> getsChildName();

}