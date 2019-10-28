package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.user.entity.OrganizationalChartInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrganizationalChartInfoMapper extends BaseMapper<OrganizationalChartInfo> {

    /**
     * 查询所有组织架构信息
     *
     * @return
     */
    List<OrganizationalChartInfo> selectAll();

    /**
     * 根据父级id查询最大排序号
     *
     * @param parentId
     * @return
     */
    Byte selectMaxSeqByParentId(Integer parentId);

    /**
     * 根据公司id查询用户数量
     *
     * @param id
     * @return
     */
    Integer selectUserCountByCompanyId(Integer id);

    /**
     * 根据父级id该父级下面该名称的数量
     *
     * @param name
     * @param parentId
     * @return
     */
    Integer selectNameByParentId(@Param("name") String name, @Param("parentId") Integer parentId);

    /**
     * 根据部门id查询用户数量
     *
     * @param id
     * @return
     */
    Integer selectUserCountByDepartmentId(Integer id);
}