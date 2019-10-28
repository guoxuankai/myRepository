package com.brandslink.cloud.user.service;

import com.brandslink.cloud.user.dto.request.AddTopMenuInfoRequestDTO;
import com.brandslink.cloud.user.dto.response.MenuInfoResponseDTO;

import java.util.List;

/**
 * 菜单
 *
 * @ClassName IMenuInfoService
 * @Author tianye
 * @Date 2019/6/10 10:05
 * @Version 1.0
 */
public interface IMenuInfoService {

    /**
     * 获取菜单树结构列表
     *
     * @param id
     * @param flag
     * @return
     */
    List<MenuInfoResponseDTO> getTree(Integer id, Integer flag);

    /**
     * 新增菜单
     *
     * @param requestDTO
     */
    void addTopNode(AddTopMenuInfoRequestDTO requestDTO);

    /**
     * 新增子菜单
     *
     * @param parentId
     * @param requestDTO
     */
    void addSubNode(Integer parentId, AddTopMenuInfoRequestDTO requestDTO);

    /**
     * 编辑菜单
     *
     * @param id
     * @param requestDTO
     */
    void updateNodeDetail(Integer id, AddTopMenuInfoRequestDTO requestDTO);

    /**
     * 删除菜单
     *
     * @param id
     */
    void deleteNode(Integer id, Integer flag);

    /**
     * 获取菜单树列表，过滤功能菜单
     *
     * @param id
     * @param flag
     * @param filter
     * @return
     */
    List<MenuInfoResponseDTO> getTreeOfMenus(Integer id, Integer flag, Integer filter);

    /**
     * 获取账号对应的菜单树列表，过滤功能菜单以及中间节点菜单
     *
     * @param id
     * @return
     */
    List<MenuInfoResponseDTO> getTreeOfMenusByUserId(Integer id, Integer flag);
}
