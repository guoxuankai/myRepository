package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 根据平台及归属获取等级列表
     * @param platform
     * @param attribution
     * @return
     */
    List<Department> getByPlatform(@Param("platform") Byte platform, @Param("attribution") Integer attribution);

    /**
     * 根据父级id查询列表
     * @param parentId
     * @return
     */
    List<Department> getByParentId(@Param("parentId") Integer parentId);


}