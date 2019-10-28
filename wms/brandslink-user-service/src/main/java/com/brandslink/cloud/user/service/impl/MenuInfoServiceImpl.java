package com.brandslink.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.user.dto.request.AddTopMenuInfoRequestDTO;
import com.brandslink.cloud.user.dto.response.MenuInfoResponseDTO;
import com.brandslink.cloud.user.entity.MenuInfo;
import com.brandslink.cloud.user.mapper.CustomerRoleInfoMapper;
import com.brandslink.cloud.user.mapper.MenuInfoMapper;
import com.brandslink.cloud.user.service.IMenuInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 菜单
 *
 * @ClassName MenuInfoServiceImpl
 * @Author tianye
 * @Date 2019/6/10 10:29
 * @Version 1.0
 */
@Service
public class MenuInfoServiceImpl implements IMenuInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuInfoServiceImpl.class);

    @Resource
    private MenuInfoMapper mapper;

    @Autowired
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Resource
    private CustomerRoleInfoMapper customerRoleInfoMapper;

    @Override
    public List<MenuInfoResponseDTO> getTree(Integer id, Integer flag) {
        List<MenuInfo> infoList;
        if (null != id) {
            // 根据用户id查询拥有权限的菜单列表
            if (flag == 3) {
                // ocms端 暂时只有一个admin，获取ocms端所有菜单
                infoList = mapper.selectAll(flag);
            } else if (flag == 2) {
                infoList = mapper.selectMenusAllByUserIdForOms(id, flag);
            } else {
                infoList = mapper.selectMenusAllByUserIdForWms(id, flag);
            }
        } else {
            // 获取所有菜单列表信息
            infoList = mapper.selectAll(flag);
        }
        return getMenusTree(infoList);
    }

    @Override
    public void addTopNode(AddTopMenuInfoRequestDTO requestDTO) {
        verifyNameAndPosition(null, 0, requestDTO.getName(), requestDTO.getBelong());
        LOGGER.info("新增菜单request：{}", JSON.toJSONString(requestDTO));
        // 获取当前账户
        UserDetailInfo detailInfo = getUserDetailInfo();
        MenuInfo menuInfo = new MenuInfo(requestDTO, detailInfo.getName());
        menuInfo.setParentId(0);
        menuInfo.setLevel(0);
        if (null == menuInfo.getSeq()) {
            setSeq(menuInfo, 0);
        }
        mapper.insertSelective(menuInfo);
        addPrimaryAccountRole(requestDTO.getBelong(), menuInfo.getId());
    }

    /**
     * 根据系统获取当前登录用户
     *
     * @return
     */
    private UserDetailInfo getUserDetailInfo() {
        String platformType = getUserDetailInfoUtil.getPlatformType();
        if (StringUtils.equals(platformType, "0")) {
            return getUserDetailInfoUtil.getUserDetailInfo();
        } else {
            return new UserDetailInfo() {{
                setName("admin-ocms");
            }};
        }
    }

    /**
     * 验证菜单名称是否重复
     *
     * @param parentId
     * @param name
     */
    private void verifyNameAndPosition(Integer id, Integer parentId, String name, Integer belong) {
        if (StringUtils.isNotBlank(name)) {
            // 判断部门名称是否重复
            Integer count = mapper.selectNameByParentId(id, name, parentId, belong);
            if (null != count && count > 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100008);
            }
        }
    }

    /**
     * 查询子级节点列表中最大的顺序号，如果为空则设置为0，否则+1
     *
     * @param menuInfo
     * @param parentId
     */
    private void setSeq(MenuInfo menuInfo, int parentId) {
        Byte maxSeq = mapper.selectMaxSeqByParentId(parentId);
        if (null == maxSeq) {
            maxSeq = 0;
        } else {
            maxSeq++;
        }
        menuInfo.setSeq(maxSeq.intValue());
    }

    @Override
    public void addSubNode(Integer parentId, AddTopMenuInfoRequestDTO requestDTO) {
        verifyNameAndPosition(null, parentId, requestDTO.getName(), requestDTO.getBelong());
        LOGGER.info("新增子菜单，request父级id：{}，detail：{}", parentId, JSON.toJSONString(requestDTO));
        MenuInfo parentMenuInfo = mapper.selectByPrimaryKey(parentId.longValue());
        // 获取当前账户
        UserDetailInfo detailInfo = getUserDetailInfo();
        MenuInfo menuInfo = new MenuInfo(requestDTO, detailInfo.getName());
        menuInfo.setParentId(parentId);
        // 设置节点等级，父级节点等级+1
        Integer level = parentMenuInfo.getLevel();
        level++;
        menuInfo.setLevel(level);
        if (null == menuInfo.getSeq()) {
            setSeq(menuInfo, parentId);
        }
        mapper.insertSelective(menuInfo);
        addPrimaryAccountRole(requestDTO.getBelong(), menuInfo.getId());
    }

    /**
     * 主账号角色添加菜单权限
     *
     * @param belong
     * @param menuId
     */
    private void addPrimaryAccountRole(Integer belong, Integer menuId) {
        if (belong == 2) {
            Integer initializeRoleId = customerRoleInfoMapper.selectInitializeRoleId(-1);
            if (null != initializeRoleId) {
                customerRoleInfoMapper.insertMenusByRoleId(initializeRoleId, new ArrayList<Integer>() {{
                    add(menuId);
                }}, 0);
            }
        }
    }

    @Override
    public void updateNodeDetail(Integer id, AddTopMenuInfoRequestDTO requestDTO) {
        verifyNameAndPosition(id, null, requestDTO.getName(), requestDTO.getBelong());
        LOGGER.info("编辑菜单，request id：{}，detail：{}", id, JSON.toJSONString(requestDTO));
        MenuInfo menuInfo = mapper.selectByPrimaryKey(id.longValue());
        Integer seq = menuInfo.getSeq();
        BeanUtils.copyProperties(requestDTO, menuInfo);
        if (null == menuInfo.getSeq()) {
            menuInfo.setSeq(seq);
        }
        // 获取当前账户
        UserDetailInfo detailInfo = getUserDetailInfo();
        menuInfo.setLastUpdateBy(detailInfo.getName());
        menuInfo.setLastUpdateTime(new Date());
        mapper.updateByPrimaryKeySelective(menuInfo);
    }

    @Override
    public void deleteNode(Integer id, Integer flag) {
        // 如删除父级菜单提示：删除子级菜单后再删除父级菜单；如果是子级 就直接删除
        Byte maxSeq = mapper.selectMaxSeqByParentId(id);
        if (null != maxSeq) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100005);
        }
        // 删除菜单后，角色对应的权限也会删除
        mapper.deleteByPrimaryKey(id.longValue());
        if (flag == 2) {
            mapper.deleteRoleMenuByMenuIdForOms(id);
        } else {
            mapper.deleteRoleMenuByMenuIdForWms(id);
        }
    }

    @Override
    public List<MenuInfoResponseDTO> getTreeOfMenus(Integer id, Integer flag, Integer filter) {
        List<MenuInfo> infoList = mapper.selectAllByUserIdOfMenus(id, flag, filter);
        if (CollectionUtils.isEmpty(infoList)) {
            return new ArrayList<>();
        }
        return getMenusTree(infoList);
    }

    @Override
    public List<MenuInfoResponseDTO> getTreeOfMenusByUserId(Integer id, Integer flag) {
        List<MenuInfoResponseDTO> result = getTreeOfMenus(id, flag, 1);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        // 过滤中间菜单
        result.forEach(r -> r.setChildren(filterMiddleMenus(r)));
        return result;
    }

    /**
     * 获取当前顶级菜单下所有最底级菜单（过滤中间菜单）
     *
     * @param menu
     * @return
     */
    private List<MenuInfoResponseDTO> filterMiddleMenus(MenuInfoResponseDTO menu) {
        List<MenuInfoResponseDTO> children = menu.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return new ArrayList<>();
        }
        List<MenuInfoResponseDTO> result = new ArrayList<>();
        recursiveFilterMenus(children, result);
        return result;
    }

    /**
     * 递归过滤树结构菜单列表的中间菜单
     *
     * @param list
     * @return
     */
    private void recursiveFilterMenus(List<MenuInfoResponseDTO> list, List<MenuInfoResponseDTO> result) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (MenuInfoResponseDTO dto : list) {
            List<MenuInfoResponseDTO> children = dto.getChildren();
            if (CollectionUtils.isEmpty(children)) {
                result.add(dto);
                continue;
            }
            recursiveFilterMenus(children, result);
        }
    }

    /**
     * 构建菜单树结构
     *
     * @param infoList
     * @return
     */
    private List<MenuInfoResponseDTO> getMenusTree(List<MenuInfo> infoList) {
        List<MenuInfoResponseDTO> dtoList = new ArrayList<>();
        List<MenuInfoResponseDTO> result = new ArrayList<>();
        infoList.forEach(i -> {
            MenuInfoResponseDTO dto = new MenuInfoResponseDTO();
            BeanUtils.copyProperties(i, dto);
            dtoList.add(dto);
        });
        // 获取所有顶级列表
        Iterator<MenuInfoResponseDTO> iterator = dtoList.iterator();
        while (iterator.hasNext()) {
            MenuInfoResponseDTO next = iterator.next();
            if (next.getParentId() == 0) {
                result.add(next);
                iterator.remove();
            }
        }
        // 排序顶级列表
        result.sort(Comparator.comparing(MenuInfoResponseDTO::getSeq));
        // 递归构造下级树列表
        if (CollectionUtils.isNotEmpty(dtoList) && CollectionUtils.isNotEmpty(result)) {
            result.forEach(r -> sortedList(r, dtoList));
        }
        return result;
    }

    /**
     * 构造树列表
     *
     * @param dto
     * @param dtoList
     */
    private void sortedList(MenuInfoResponseDTO dto, List<MenuInfoResponseDTO> dtoList) {
        List<MenuInfoResponseDTO> list = new ArrayList<>();
        // 获取父节点
        Iterator<MenuInfoResponseDTO> iterator = dtoList.iterator();
        while (iterator.hasNext()) {
            MenuInfoResponseDTO next = iterator.next();
            if (next.getParentId().equals(dto.getId())) {
                list.add(next);
                iterator.remove();
            }
        }
        // 排序当前节点，储存当前节点的子节点列表
        if (CollectionUtils.isNotEmpty(list)) {
            list.sort(Comparator.comparing(MenuInfoResponseDTO::getSeq));
            dto.setChildren(list);
        }
        // 递归查询所有子节点列表
        if (CollectionUtils.isNotEmpty(dtoList) && CollectionUtils.isNotEmpty(list)) {
            list.forEach(l -> sortedList(l, dtoList));
        }
    }
}
