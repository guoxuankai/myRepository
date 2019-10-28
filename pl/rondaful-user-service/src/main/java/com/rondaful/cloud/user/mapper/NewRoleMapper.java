package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.NewRole;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface NewRoleMapper extends BaseMapper<NewRole> {

    /**
     *
     * @param roleName
     * @param startTime
     * @param endTime
     * @return
     */
    List<NewRole> getPage(@Param("roleName") String roleName,@Param("startTime") Date startTime,@Param("endTime") Date endTime,
                          @Param("platform") Integer platform,@Param("attribution") Integer attribution);

    /**
     * 添加角色
     * @param role
     * @return
     */
    Integer insertId(NewRole role);

    /**
     * 根据角色id获取角色名
     * @param list
     * @return
     */
    List<NewRole> getsName(@Param("list") List<Integer> list);

    /**
     * 查询顶级组织下所有角色名
     * @param platformType
     * @param attributionId
     * @return
     */
    List<NewRole> selectByAttr(@Param("platformType") Integer platformType,@Param("attributionId") Integer attributionId);
}