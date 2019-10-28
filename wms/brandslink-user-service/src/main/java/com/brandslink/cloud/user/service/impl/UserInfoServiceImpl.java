package com.brandslink.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.common.utils.PhoneUtils;
import com.brandslink.cloud.user.dto.request.AddOrUpdateUserRequestDTO;
import com.brandslink.cloud.user.dto.request.GetUserListRequestDTO;
import com.brandslink.cloud.user.dto.response.CodeAndNameResponseDTO;
import com.brandslink.cloud.user.dto.response.RoleInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.UserInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.UserWarehouseDetailResponseDTO;
import com.brandslink.cloud.user.entity.MenuInfo;
import com.brandslink.cloud.user.entity.RoleWarehouseResult;
import com.brandslink.cloud.user.entity.UserInfo;
import com.brandslink.cloud.user.mapper.UserInfoMapper;
import com.brandslink.cloud.user.service.IUserInfoService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.brandslink.cloud.common.constant.UserConstant.INITIAL_PASSWORD;

/**
 * 用户
 *
 * @ClassName UserInfoServiceImpl
 * @Author tianye
 * @Date 2019/6/12 10:35
 * @Version 1.0
 */
@Service
public class UserInfoServiceImpl implements IUserInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Resource
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Resource
    private UserInfoMapper mapper;

    @Override
    public Page<UserInfoResponseDTO> getUserList(GetUserListRequestDTO request) {
        List<UserInfoResponseDTO> resultList = new ArrayList<>();
        LOGGER.info("根据模糊条件查询用户列表信息request：{}", JSON.toJSONString(request));
        Long page = Long.valueOf(request.getPage());
        Long row = Long.valueOf(request.getRow());
        long skip = (page - 1 < 0 ? 0 : page - 1) * row;
        // 获取所有用户信息列表
        List<UserInfo> userInfoList = mapper.getUserList(request);
        if (CollectionUtils.isEmpty(userInfoList)) {
            PageInfo<UserInfoResponseDTO> pageInfo = new PageInfo<>(resultList);
            return new Page<>(pageInfo);
        }
        // 根据id去重，分页
        List<UserInfo> distinctList = userInfoList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(UserInfo::getId))), ArrayList::new));
        // 获取总数
        int total = distinctList.size();
        // 去重分页之后的用户列表id
        List<Integer> idList = distinctList.stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .skip(skip)
                .limit(Long.parseLong(request.getRow()))
                .map(UserInfo::getId)
                .distinct()
                .collect(Collectors.toList());
        Map<Integer, List<UserInfo>> listMap = userInfoList.stream().filter(l -> idList.contains(l.getId())).collect(Collectors.groupingBy(UserInfo::getId));
        // 映射结果集
        listMap.values().forEach(v -> {
            UserInfoResponseDTO response = new UserInfoResponseDTO();
            BeanUtils.copyProperties(v.get(0), response);
            // 获取仓库信息
            List<RoleInfoResponseDTO.WarehouseDetail> warehouseDetailList = new ArrayList<>();
            List<UserInfo> warehouses = v.stream().filter(vv -> StringUtils.isNotBlank(vv.getWarehouseCode())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(warehouses)) {
                Map<String, List<UserInfo>> warehouseMap = v.stream().collect(Collectors.groupingBy(UserInfo::getWarehouseName));
                warehouseMap.values().forEach(w -> {
                    RoleInfoResponseDTO.WarehouseDetail warehouseDetail = new RoleInfoResponseDTO.WarehouseDetail();
                    warehouseDetail.setWarehouseName(w.get(0).getWarehouseName());
                    warehouseDetail.setWarehouseCode(w.get(0).getWarehouseCode());
                    warehouseDetailList.add(warehouseDetail);
                });
            }
            response.setWarehouseList(warehouseDetailList);
            response.setWarehouseNameList(warehouseDetailList.stream().map(w -> w.getWarehouseName().split("-")[0]).distinct().collect(Collectors.joining(",")));
            // 获取角色信息
            List<UserInfoResponseDTO.RoleDetail> roleDetailList = new ArrayList<>();
            List<UserInfo> roles = v.stream().filter(vv -> StringUtils.isNotBlank(vv.getRoleCode())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(roles)) {
                Map<String, List<UserInfo>> roleMap = v.stream().collect(Collectors.groupingBy(UserInfo::getRoleCode));
                roleMap.values().forEach(r -> {
                    UserInfoResponseDTO.RoleDetail roleDetail = new UserInfoResponseDTO.RoleDetail();
                    roleDetail.setRoleCode(r.get(0).getRoleCode());
                    roleDetail.setRoleName(r.get(0).getRoleName());
                    roleDetailList.add(roleDetail);
                });
            }
            response.setRoleList(roleDetailList);
            resultList.add(response);
        });
        PageInfo<UserInfoResponseDTO> pageInfo = new PageInfo<>(resultList.stream().sorted((a, b) -> Integer.compare(b.getId(), a.getId())).collect(Collectors.toList()));
        pageInfo.setTotal(total);
        return new Page<>(pageInfo);
    }

    @Override
    public void addUser(AddOrUpdateUserRequestDTO request) {
        judgeAccount(request.getAccount());
        judgeName(request.getName());
        PhoneUtils.judgeContactWay(request.getContactWay());
        LOGGER.info("添加用户request：{}", JSON.toJSONString(request));
        UserInfo insertUserInfo = new UserInfo();
        BeanUtils.copyProperties(request, insertUserInfo);
        Date date = new Date();
        // 获取当前用户
        UserDetailInfo detailInfo = getUserDetailInfoUtil.getUserDetailInfo();
        insertUserInfo.setCreateBy(detailInfo.getName());
        insertUserInfo.setCreateTime(date);
        insertUserInfo.setLastUpdateBy(detailInfo.getName());
        insertUserInfo.setLastUpdateTime(date);
        // 所有账户初始密码都是6个1
        insertUserInfo.setPassword(B_CRYPT_PASSWORD_ENCODER.encode(INITIAL_PASSWORD));
        mapper.insertSelective(insertUserInfo);
        Integer userInfoId = insertUserInfo.getId();
        commonUpdateDepartmentDetailByUserId(false, request, userInfoId);
        // 添加所属仓库信息
        commonInsertWarehouseDetailByRoleId(false, userInfoId, request);
        // 添加用户角色
        commonUpdatedRoles(false, userInfoId, commonDistinctList(request.getRoleIds()));
    }

    @Override
    public void updateUser(AddOrUpdateUserRequestDTO request) {
        LOGGER.info("修改用户request：{}", JSON.toJSONString(request));
        PhoneUtils.judgeContactWay(request.getContactWay());
        Integer id = Integer.valueOf(request.getId());
        UserInfo userInfo = getUserInfoByUserId(id);
        BeanUtils.copyProperties(request, userInfo);
        commonUpdateLastUpdateInfo(userInfo);
        commonUpdateDepartmentDetailByUserId(true, request, id);
        commonUpdatedRoles(true, id, commonDistinctList(request.getRoleIds()));
        // 修改所属仓库信息
        commonInsertWarehouseDetailByRoleId(true, id, request);
    }

    /**
     * 公共更新账户部门信息
     *
     * @param flag
     * @param request
     * @param userId
     */
    private void commonUpdateDepartmentDetailByUserId(boolean flag, AddOrUpdateUserRequestDTO request, Integer userId) {
        List<AddOrUpdateUserRequestDTO.DepartmentDetail> detailList = request.getDepartmentDetailList();
        if (flag) {
            mapper.deleteDepartmentDetailByUserId(userId);
        }
        mapper.insertDepartmentDetailByUserId(userId, detailList);
    }

    /**
     * 去重
     *
     * @param list
     * @return
     */
    private List<Integer> commonDistinctList(List<Integer> list) {
        return list.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    /**
     * 公共添加修改用户对应仓库信息
     *
     * @param flag    修改or添加   true：修改  false：添加
     * @param id
     * @param request
     */
    private void commonInsertWarehouseDetailByRoleId(boolean flag, Integer id, AddOrUpdateUserRequestDTO request) {
        List<RoleInfoResponseDTO.WarehouseDetail> warehouseList = request.getWarehouseList();
        if (CollectionUtils.isNotEmpty(warehouseList)) {
            List<RoleWarehouseResult> warehouseDetail = new ArrayList<>();
            warehouseList.forEach(w -> {
                RoleWarehouseResult result = new RoleWarehouseResult();
                result.setRoleId(id);
                result.setWarehouseName(w.getWarehouseName());
                result.setWarehouseCode(w.getWarehouseCode());
                warehouseDetail.add(result);
            });
            // 先删除所属仓库信息，然后添加
            if (flag) {
                mapper.deleteWarehouseDetailByUserId(id);
            }
            mapper.insertWarehouseDetailByUserId(warehouseDetail);
        }
    }

    @Override
    public void reset(Integer id) {
        // admin账号无法重置密码
        String name = mapper.selectAccountById(id);
        if (StringUtils.equals(name, UserConstant.ADMIN)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100013);
        }
        UserInfo userInfo = getUserInfoByUserId(id);
        userInfo.setPassword(B_CRYPT_PASSWORD_ENCODER.encode(INITIAL_PASSWORD));
        commonUpdateLastUpdateInfo(userInfo);
    }

    @Override
    public void updateBindingRoles(Integer id, List<RoleInfoResponseDTO.WarehouseDetail> warehouseList, List<Integer> roleIds) {
        UserInfo userInfo = getUserInfoByUserId(id);
        commonUpdateLastUpdateInfo(userInfo);
        commonUpdatedRoles(true, id, commonDistinctList(roleIds));
        commonInsertWarehouseDetailByRoleId(true, id, new AddOrUpdateUserRequestDTO() {{
            setWarehouseList(warehouseList);
        }});
    }

    @Override
    public void updatePassword(Integer id, String oldPassword, String changePassword) {
        if (StringUtils.equals(oldPassword, changePassword)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100014);
        }
        UserInfo userInfo = mapper.selectByPrimaryKey(id.longValue());
        if (!B_CRYPT_PASSWORD_ENCODER.matches(oldPassword, userInfo.getPassword())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002);
        }
        userInfo.setPassword(B_CRYPT_PASSWORD_ENCODER.encode(changePassword));
        commonUpdateLastUpdateInfo(userInfo);
    }

    /**
     * 判断账号是否存在
     *
     * @param account
     */
    private void judgeAccount(String account) {
        UserInfo count = mapper.selectByAccount(account);
        LOGGER.info("判断账号：{}是否存在,count is ：{}.", account, JSON.toJSONString(count));
        if (null != count) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100003);
        }
    }

    /**
     * 判断姓名是否存在
     *
     * @param name
     */
    private void judgeName(String name) {
        UserInfo count = mapper.selectByName(name);
        LOGGER.info("判断账号：{}是否存在,count is ：{}.", name, JSON.toJSONString(count));
        if (null != count) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100011);
        }
    }

    @Override
    public void enabled(List<Integer> ids) {
        LOGGER.info("启用账号：{}", ids.toString());
        mapper.enabledByIds(ids);
    }

    @Override
    public void disabled(List<Integer> ids) {
        LOGGER.info("禁用账号：{}", ids.toString());
        mapper.disabledByIds(ids);
    }

    @Override
    public List<UserWarehouseDetailResponseDTO> getWarehouseDetail(String account) {
        UserInfo userInfo = mapper.selectByAccount(account);
        if (null == userInfo) {
            LOGGER.info("根据账号:{},查询所属仓库,账号不存在!", account);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100003, "账号不存在，请重新输入账号!");
        }
        return CommonGetWarehouseDetailByUserId(userInfo.getId());
    }

    @Override
    public List<Map<String, String>> getAccountNameListByWarehouseId(String warehouseCode) {
        List<UserInfo> userInfoList = mapper.selectIdAndNamesByWarehouseId(warehouseCode);
        List<Map<String, String>> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userInfoList)) {
            ArrayList<UserInfo> userInfos = userInfoList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(UserInfo::getId))),
                    ArrayList::new));
            userInfos.forEach(u -> {
                Map<String, String> map = new HashMap<>(3);
                map.put("id", String.valueOf(u.getId()));
                map.put("account", u.getAccount());
                map.put("name", u.getName());
                result.add(map);
            });
        }
        return result;
    }

    @Override
    public List<CodeAndNameResponseDTO> getWarehouseDetailByUserId() {
        List<UserWarehouseDetailResponseDTO> list = CommonGetWarehouseDetailByUserId(getUserDetailInfoUtil.getUserDetailInfo().getId());
        List<CodeAndNameResponseDTO> result = new ArrayList<>();
        list.forEach(l -> {
            CodeAndNameResponseDTO dto = new CodeAndNameResponseDTO();
            dto.setDataName(l.getWarehouseName());
            dto.setDataCode(l.getWarehouseCode());
            result.add(dto);
        });
        return result;
    }

    @Override
    public void bindingShortcutMenus(List<Integer> shortcutMenus, Integer flag) {
        Integer userId;
        if (flag == 0) {
            userId = getUserDetailInfoUtil.getUserDetailInfo().getId();
        } else {
            userId = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getId();
        }
        mapper.deleteShortcutMenusByUserI(userId, flag);
        if (CollectionUtils.isNotEmpty(shortcutMenus)) {
            // 先删除之前快捷菜单，再添加新菜单
            mapper.insertShortcutMenusByUserId(userId, shortcutMenus, flag);
        }
    }

    @Override
    public List<MenuInfo> getShortcutMenus(Integer flag) {
        List<MenuInfo> result;
        Integer userId;
        if (flag == 0) {
            userId = getUserDetailInfoUtil.getUserDetailInfo().getId();
        } else {
            userId = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getId();
        }
        result = mapper.getShortcutMenusByUserId(userId, flag);
        if (CollectionUtils.isEmpty(result)) {
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * 根据用户id查询所属仓库信息
     *
     * @param userId
     * @return
     */
    private List<UserWarehouseDetailResponseDTO> CommonGetWarehouseDetailByUserId(Integer userId) {
        List<UserWarehouseDetailResponseDTO> result = mapper.selectWarehouseInfoByUserId(userId);
        LOGGER.info("根据用户id:{},查询所属仓库detail:{}", userId, JSON.toJSONString(result));
        if (CollectionUtils.isEmpty(result)) {
            result = new ArrayList<>();
        }
        result.forEach(r -> r.setWarehouseName(r.getWarehouseName().split("-")[0]));
        return result;
    }

    /**
     * 根据用户id获取用户信息
     *
     * @param userId
     * @return
     */
    private UserInfo getUserInfoByUserId(Integer userId) {
        return new UserInfo() {{
            setId(userId);
        }};
    }

    /**
     * 更新用户最后一次修改人信息并更新数据库
     *
     * @param userInfo
     */
    private void commonUpdateLastUpdateInfo(UserInfo userInfo) {
        Date date = new Date();
        // 获取当前用户
        UserDetailInfo detailInfo = getUserDetailInfoUtil.getUserDetailInfo();
        userInfo.setLastUpdateBy(detailInfo.getName());
        userInfo.setLastUpdateTime(date);
        mapper.updateByPrimaryKeySelective(userInfo);
    }

    /**
     * 根据用户id更新用户角色
     *
     * @param id
     * @param roleIds
     */
    private void commonUpdatedRoles(boolean flag, Integer id, List<Integer> roleIds) {
        LOGGER.info("更新用户：{} 角色：{}", id, roleIds.toString());
        if (flag) {
            mapper.deleteRolesByUserId(id);
        }
        if (CollectionUtils.isNotEmpty(roleIds)) {
            mapper.insertRolesByUserId(id, roleIds);
        }
    }
}
