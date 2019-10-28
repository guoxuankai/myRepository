package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.entity.ManageUser;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.user.*;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/26
 * @Description:
 */
public interface IManageUserService {

    public static String QUERY_USER_MANAGE_NAME="user:user.manage.name.v2";
    public static String QUERY_USER_MANAGE_ID="user:user.manage.id.v2";

    /**
     * 新增总后台账号
     * @param dto
     * @return
     */
    Integer add(ManageUserDTO dto);

    /**
     * 编辑用户
     * @param dto
     * @return
     */
    Integer update(ManageUserDTO dto);

    /**
     * 根据名字查询用户
     * @param name
     * @return
     */
    ManageUserDTO getByName(String name);

    /**
     * 分页查询账号
     * @param dto
     * @return
     */
    PageDTO<PithyUserDTO> getPage(QuerManagePageDTO dto);

    /**
     * 根据id获取详细信息
     * @param userId
     * @return
     */
    ManageUserDetailDTO getById(Integer userId,String languageType);

    /**
     * 根据id删除用户
     * @param userId
     * @return
     */
    Integer delete(Integer userId);

    /**
     * 绑定用户
     * @param binds
     * @param userId
     * @return
     */
    Integer bindAccount(List<UserOrgDTO> binds,Integer userId);

    /**
     * 获取所有管理后台的用户名
     * @return
     */
    List<String> getAllName();

    /**
     * 修改密码
     * @param userId
     * @param passWord
     * @return
     */
    Integer updatePassWord(Integer userId,String passWord);

    /**
     * 后台添加一级用户是将其绑定到名下
     * @param cmsId
     * @param bingType
     * @param userId
     * @return
     */
    Integer addBind(Integer cmsId,Integer bingType,Integer userId);

    /**
     * 获取子账号名称
     * @param userId
     * @return
     */
    List<ManageUser> getsChildName(Integer userId);

}
