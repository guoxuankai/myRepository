package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.model.dto.dep.DepartmentDTO;
import com.rondaful.cloud.user.model.dto.dep.DepartmentTreeDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/24
 * @Description:
 */
public interface IDepartmentService {

    public static String DEPARTMENT_KEY="user.depart.v0";

    /**
     * 新增
     * @param dto
     * @return
     */
    Integer add(DepartmentDTO dto);

    /**
     * 根据组织id删除组织
     * @param departmentId
     * @return
     */
    Integer delete(Integer departmentId);

    /**
     * 修改
     * @param departmentId
     * @param positionNames
     * @param updateBy
     * @return
     */
    Integer update(Integer departmentId, List<String> positionNames, String updateBy, String departmentName);

    /**
     * 根据平台和归属人获取组织树
     * @param platform
     * @param attribution
     * @return
     */
    List<DepartmentTreeDTO> getTree(Byte platform, Integer attribution);

    /**
     * 根据id获取详情
     * @param id
     * @return
     */
    DepartmentDTO get(Integer id);
}
