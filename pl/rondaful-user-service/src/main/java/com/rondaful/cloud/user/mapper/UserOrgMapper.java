package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.UserOrg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserOrgMapper extends BaseMapper<UserOrg> {

    /**
     * 批量插入绑定账号关系
     * @param list
     * @return
     */
    Integer insertBatch(List<UserOrg> list);

    /**
     * 根据用户id删除绑定的组织关系
     * @param userId
     * @return
     */
    Integer deleteByUserId(@Param("userId") Integer userId,@Param("userPlatform") Integer userPlatform);

    /**
     * 根据平台获取用户关联的组织列表
     * @param userId
     * @param userPlatform
     * @return
     */
    List<UserOrg> getAccount(@Param("userId") Integer userId,@Param("userPlatform") Integer userPlatform);

    /**
     * 根据绑定的子账号   获取主用户信息
     * @param userPlatform
     * @param bindType
     * @param bindCode
     * @return
     */
    List<Integer> getsByBindCode(@Param("userPlatform") Integer userPlatform,@Param("bindType") Integer bindType,@Param("bindCode") String bindCode);
}