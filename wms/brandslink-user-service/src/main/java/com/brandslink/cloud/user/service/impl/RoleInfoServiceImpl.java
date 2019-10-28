package com.brandslink.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.user.dto.request.GetRoleListRequestDTO;
import com.brandslink.cloud.user.dto.response.RoleInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.RoleListResponseDTO;
import com.brandslink.cloud.user.entity.RoleInfo;
import com.brandslink.cloud.user.entity.RoleWarehouseResult;
import com.brandslink.cloud.user.entity.UserAndRoleEntity;
import com.brandslink.cloud.user.mapper.CustomerRoleInfoMapper;
import com.brandslink.cloud.user.mapper.MenuInfoMapper;
import com.brandslink.cloud.user.mapper.RoleInfoMapper;
import com.brandslink.cloud.user.remote.CenterRemoteService;
import com.brandslink.cloud.user.service.IRoleInfoService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色
 *
 * @ClassName RoleInfoServiceImpl
 * @Author tianye
 * @Date 2019/6/11 15:58
 * @Version 1.0
 */
@Service
public class RoleInfoServiceImpl implements IRoleInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleInfoServiceImpl.class);

    @Resource
    private RoleInfoMapper mapper;

    @Resource
    private MenuInfoMapper menuInfoMapper;

    @Resource
    private CustomerRoleInfoMapper customerRoleInfoMapper;

    @Resource
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Resource
    private CenterRemoteService centerRemoteService;

    @Override
    public Page<RoleInfoResponseDTO> getRoleList(GetRoleListRequestDTO request) {
        List<RoleInfoResponseDTO> resultList = new ArrayList<>();
        LOGGER.info("获取角色列表Request:{}", JSON.toJSONString(request));
        Long page = Long.valueOf(request.getPage());
        Long row = Long.valueOf(request.getRow());
        long skip = (page - 1 < 0 ? 0 : page - 1) * row;
        // 获取所有角色列表
        List<RoleInfo> list = mapper.getPage(request);
        if (CollectionUtils.isEmpty(list)) {
            PageInfo<RoleInfoResponseDTO> pageInfo = new PageInfo<>(resultList);
            return new Page<>(pageInfo);
        }
        // 根据id去重，分页
        List<RoleInfo> distinctList = list.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(RoleInfo::getId))), ArrayList::new));
        // 获取总数
        int total = distinctList.size();
        // 去重分页之后的角色列表id
        List<Integer> idList = distinctList.stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .skip(skip)
                .limit(Long.parseLong(request.getRow()))
                .map(RoleInfo::getId)
                .distinct()
                .collect(Collectors.toList());
        Map<Integer, List<RoleInfo>> listMap = list.stream().filter(l -> idList.contains(l.getId())).collect(Collectors.groupingBy(RoleInfo::getId));
        // 映射结果集
        listMap.values().forEach(v -> {
            RoleInfoResponseDTO result = new RoleInfoResponseDTO();
            BeanUtils.copyProperties(v.get(0), result);
            List<RoleInfoResponseDTO.WarehouseDetail> warehouseDetailList = new ArrayList<>();
            v.forEach(vv -> {
                RoleInfoResponseDTO.WarehouseDetail warehouseDetail = new RoleInfoResponseDTO.WarehouseDetail();
                warehouseDetail.setWarehouseCode(vv.getWarehouseCode());
                warehouseDetail.setWarehouseName(vv.getWarehouseName());
                warehouseDetailList.add(warehouseDetail);
            });
            result.setWarehouseList(warehouseDetailList);
            resultList.add(result);
        });
        PageInfo<RoleInfoResponseDTO> pageInfo = new PageInfo<>(resultList.stream().sorted((a, b) -> Integer.compare(b.getId(), a.getId())).collect(Collectors.toList()));
        pageInfo.setTotal(total);
        return new Page<>(pageInfo);
    }

    @Override
    public void addRole(String roleName, String warehouseName, String warehouseCode) {
        LOGGER.info("新增角色request  roleName:{},warehouseName:{},warehouseCode:{}", roleName, warehouseName, warehouseCode);
        judgeRoleName(roleName);
        Integer count = mapper.selectByRoleName(roleName);
        if (null != count && count > 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100004);
        }
        // 查询当前用户信息
        UserDetailInfo detailInfo = getUserDetailInfoUtil.getUserDetailInfo();
        RoleInfo roleInfo = new RoleInfo(detailInfo.getName());
        roleInfo.setRoleName(roleName);
        Integer maxRoleCode = mapper.selectMaxRoleCode();
        if (null == maxRoleCode || 0 == maxRoleCode) {
            maxRoleCode = 1;
        } else {
            maxRoleCode++;
        }
        roleInfo.setRoleCode(maxRoleCode);
        mapper.insertSelective(roleInfo);
        String[] codes = warehouseCode.split(",");
        String[] names = warehouseName.split(",");
        Integer roleInfoId = roleInfo.getId();
        commonInsertWarehouseDetailByRoleId(false, roleInfoId, names, codes);
        List<Integer> homeMenuIds = menuInfoMapper.selectHomeMenuIds(0);
        mapper.insertMenusByRoleId(roleInfoId, homeMenuIds, 0);
    }

    @Override
    public void updateRole(String id, String roleName, String warehouseName, String warehouseCode) {
        LOGGER.info("编辑角色request  id:{},roleName:{},warehouseName:{},warehouseCode:{}", id, roleName, warehouseName, warehouseCode);
        judgeRoleName(roleName);
        RoleInfo roleInfo = mapper.selectByPrimaryKey(Long.valueOf(id));
        String oldRoleName = roleInfo.getRoleName();
        if (!StringUtils.equals(roleName, oldRoleName)) {
            roleInfo.setRoleName(roleName);
            // 查询当前用户信息
            UserDetailInfo detailInfo = getUserDetailInfoUtil.getUserDetailInfo();
            roleInfo.setLastUpdateBy(detailInfo.getName());
            roleInfo.setLastUpdateTime(new Date());
            mapper.updateByPrimaryKeySelective(roleInfo);
            // 更新所有关联账户角色名称
            mapper.updateUserRoleNameByRoleName(oldRoleName, roleName);
        }
        List<RoleInfo> roleInfos = mapper.selectAndWarehouseByPrimaryKey(Integer.valueOf(id));
        String[] codes = warehouseCode.split(",");
        String[] names = warehouseName.split(",");
        int length = codes.length;
        commonInsertWarehouseDetailByRoleId(true, Integer.valueOf(id), names, codes);
        // 更新账号所属仓库，新增的仓库不更新，删除的仓库删除对应账号的所属仓库
        List<String> oldNames = roleInfos.stream().map(r -> StringUtils.join(r.getWarehouseName(), "-", r.getRoleName())).collect(Collectors.toList());
        // 删除的仓库
        final Iterator<String> iterator = oldNames.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            for (int i = 0; i < length; i++) {
                if (StringUtils.equals(next.split("-")[0], names[i])) {
                    iterator.remove();
                    break;
                }
            }
        }
        if (CollectionUtils.isNotEmpty(oldNames)) {
            // 更新所属账号的角色
            // 获取删除的仓库对应的账户id
            List<Integer> userIds = mapper.selectUserIdsByRoleIdAndWarehouseName(oldNames);
            if (CollectionUtils.isNotEmpty(userIds)) {
                mapper.deleteWarehouseDetailByRoleIdAndWarehouseName(oldNames);
                List<UserAndRoleEntity> list = mapper.selectUserIdAndRoleNamesByUserIds(userIds);
                mapper.deleteRoleByUserIds(userIds);
                if (CollectionUtils.isNotEmpty(list)) {
                    mapper.insertUserAndRoleByUserAndRoleEntity(list);
                }
            }
        }
    }

    /**
     * 公共添加修改角色对应仓库信息
     *
     * @param flag  修改or添加   true：修改  false：添加
     * @param id
     * @param names
     * @param codes
     */
    private void commonInsertWarehouseDetailByRoleId(boolean flag, Integer id, String[] names, String[] codes) {
        List<RoleWarehouseResult> warehouseDetail = new ArrayList<>();
        for (int i = 0; i < codes.length; i++) {
            RoleWarehouseResult result = new RoleWarehouseResult();
            result.setRoleId(id);
            result.setWarehouseCode(codes[i]);
            result.setWarehouseName(names[i]);
            warehouseDetail.add(result);
        }
        if (flag) {
            // 先删除当前角色所属仓库，然后添加
            mapper.deleteWarehouseDetailByRoleId(id);
        }
        mapper.insertWarehouseDetailByRoleId(warehouseDetail);
    }

    @Override
    public void deleteRole(Integer id) {
        // 如果该角色有绑定的用户，则不能删除
        Integer count = mapper.selectUserCountByRoleId(id);
        if (null != count && count > 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100007);
        }
        // 删除角色，同时要删除该角色的权限和所属仓库
        mapper.deleteMenusByRoleId(id, null);
        mapper.deleteByPrimaryKey(id.longValue());
        mapper.deleteWarehouseDetailByRoleId(id);
    }

    @Override
    public void addPermission(Integer id, List<Integer> menuIds, Integer flag) {
        if (CollectionUtils.isNotEmpty(menuIds)) {
            // 查询原本的权限（过滤功能菜单以及顶级菜单）
//            mapper.selectMenusByRoleId()
            // 先删除该角色的权限，然后再添加最新的权限
            mapper.deleteMenusByRoleId(id, flag);
            // 不做过滤，保留整个权限结构
            mapper.insertMenusByRoleId(id, menuIds, flag);
        }
    }

    @Override
    public Map<String, Object> getPermission(Integer id, Integer flag) {
        List<RoleInfo> roleInfo = mapper.selectAndWarehouseByPrimaryKey(id);
        Map<String, Object> map = new HashMap<>(3);
        map.put("roleName", roleInfo.get(0).getRoleName());
        map.put("warehouseName", roleInfo.stream().map(RoleInfo::getWarehouseName).collect(Collectors.joining("、")));
        map.put("menus", mapper.selectMenusByRoleId(id, flag));
        return map;
    }

    @Override
    public List<RoleListResponseDTO> getRoleListByWarehouseCode() {
        List<RoleListResponseDTO> resultList = new ArrayList<>();
        // 查询所有仓库信息
        String jsonString = JSON.toJSONString(centerRemoteService.getWarehouseDetail());
        Map map = JSON.parseObject(jsonString, Map.class);
        String toJSONString = JSON.toJSONString(map.get("data"));
        List responseDTO = JSON.parseObject(toJSONString, List.class);
        // 映射仓库信息
        if (CollectionUtils.isEmpty(responseDTO)) {
            return resultList;
        }
        for (Object dto : responseDTO) {
            Map object = JSON.parseObject(JSON.toJSONString(dto), Map.class);
            RoleListResponseDTO response = new RoleListResponseDTO();
            response.setWarehouseCode(object.get("dataCode").toString());
            response.setWarehouseName(object.get("dataName").toString());
            resultList.add(response);
        }
        // 根据仓库code查对应的角色列表
        List<RoleWarehouseResult> warehouseResult = mapper.selectRoleListByWarehouseCode(resultList.stream().map(RoleListResponseDTO::getWarehouseCode).collect(Collectors.toList()));
        // 映射结果集
        resultList.forEach(result -> {
            List<RoleListResponseDTO.RoleListResponse> list = new ArrayList<>();
            warehouseResult.forEach(warehouse -> {
                if (StringUtils.equals(result.getWarehouseCode(), warehouse.getWarehouseCode())) {
                    RoleListResponseDTO.RoleListResponse response = new RoleListResponseDTO.RoleListResponse();
                    response.setId(warehouse.getRoleId());
                    response.setRoleName(warehouse.getWarehouseName());
                    list.add(response);
                }
            });
            result.setRoleList(list);
        });
        return resultList;
    }

    @Override
    public String getMenusByRequestUrl(String requestUrl, String platformType) {
        LOGGER.info("平台：{}，通过请求url：{}，查询所需要的角色列表", platformType, requestUrl);
        String result = null;
        //平台类型 0：wms 1：oms 2：ocms
        switch (platformType) {
            case "0":
                result = JSON.toJSONString(mapper.getRoleListByMenuUrl(requestUrl));
                break;
            case "1":
                result = JSON.toJSONString(customerRoleInfoMapper.getRoleListByMenuUrl(requestUrl));
                break;
            case "2":
                result = "ocms";
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(null));
    }

    @Override
    public String getMenusByRoleList(String authority, String platformType) {
        LOGGER.info("平台：{}，通过用户角色id:{},查询所能访问的所有url", platformType, authority);
        List<Integer> authorityList = Arrays.stream(authority.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        //平台类型 0：wms 1：oms 2：ocms
        List<String> urlList = new ArrayList<>();
        switch (platformType) {
            case "0":
                urlList = mapper.selectMenuUrlByRoleList(authorityList);
                break;
            case "1":
                urlList = customerRoleInfoMapper.selectMenuUrlByRoleList(authorityList);
                break;
            case "2":
                urlList = new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(urlList)) {
            return StringUtils.EMPTY;
        }
        return String.join(",", urlList);
    }

    /**
     * 校验角色名称合法性
     *
     * @param roleName
     */
    private void judgeRoleName(String roleName) {
        if (StringUtils.contains(roleName, "-")) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100012);
        }
    }
}
