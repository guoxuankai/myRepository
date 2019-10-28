package com.brandslink.cloud.user.service;

import com.brandslink.cloud.user.dto.response.OrganizationalChartInfoResponseDTO;

import java.util.List;

/**
 * 组织架构
 *
 * @ClassName IOrganizationalChartService
 * @Author tianye
 * @Date 2019/6/5 19:53
 * @Version 1.0
 */
public interface IOrganizationalChartService {

    /**
     * 获取组织架构树列表
     *
     * @return
     */
    List<OrganizationalChartInfoResponseDTO> getTree();

    /**
     * 添加下级
     *
     * @param parentId
     * @param name
     * @param position
     */
    void addSubNode(Integer parentId, String name, String position);

    /**
     * 编辑
     *
     * @param id
     * @param name
     * @param position
     */
    void updateNodeDetail(Integer id, String name, String position);

    /**
     * 新增公司
     *
     * @param name
     */
    void addTopNode(String name);

    /**
     * 删除
     *
     * @param id
     */
    void deleteNode(Integer id);
}
