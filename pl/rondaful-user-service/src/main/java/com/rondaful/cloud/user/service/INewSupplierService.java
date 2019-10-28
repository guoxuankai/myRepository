package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.entity.NewSupplierUser;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.user.*;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/5/2
 * @Description:
 */
public interface INewSupplierService {

    public static String SUPPLIER_NAME_CACHE="user:supplier.name.cache.v8";
    public static String SUPPLIER_ID_CACHE="user:supplier.id.cache.v8";

    /**
     * 新增供应商
     * @param dto
     * @return
     */
    Integer addUser(SupplierUserDTO dto);

    /**
     * 一级管供应链编辑
     * @param dto1
     * @param dto2
     * @return
     */
    Integer update(SupplierUserDTO dto1, SupplierCompanyDTO dto2);

    /**
     * 根据id获取详情
     * @param userId
     * @return
     */
    SupplierUserDetailDTO getById(Integer userId);

    /**
     * 分页获取后台一级账号
     * @param dto
     * @return
     */
    PageDTO<BackSupplierDTO> getPageBack(QueryBackSupplierDTO dto);

    /**
     * 修改密码  公司名之类的
     * @param userId
     * @param passWord
     * @param supplyChainCompany
     * @param updateBy
     * @return
     */
    Integer updateDetail(Integer userId,String passWord,String supplyChainCompany,String updateBy);

    /**
     * 查询子账号列表
     * @param dto
     * @return
     */
    PageDTO<PithyUserDTO> getChildPage(QueryChildDTO dto);

    /**
     * 根据用户id删除账号
     * @param userId
     * @return
     */
    Integer delete(Integer userId);

    /**
     * 根据名字查询用户
     * @param name
     * @return
     */
    SupplierUserDetailDTO getByName(String name);


    /**
     * 获取所有一级账号
     * @return
     */
    BindAccountDTO getTopUser(List<Integer> userIds);

    /**
     * 根据主账号获取所有子集账号名(仅临时调用)
     * @param userId
     * @return
     */
    List<NewSupplierUser> getChildName(Integer userId);

    /**
     * 根据id获取子账号详情
     * @param userId
     * @return
     */
    SupplierUserChiletDetail getChildById(Integer userId,String languageType);

    /**
     * 店铺绑定
     * @param userId
     * @param binds
     * @return
     */
    Integer bindHouse(Integer userId,List<UserOrgDTO> binds);

    /**
     * 修改用户图片
     * @param imgPath
     * @param userId
     * @param updateBy
     * @return
     */
    Integer updateHeadImg(String imgPath,Integer userId,String updateBy);

    /**
     * 修改手机号
     * @param userId
     * @param phone
     * @param updateBy
     * @return
     */
    Integer updatePhone(Integer userId,String phone,String updateBy,String email);

    /**
     * 获取有效的供应商公司名
     * @return
     */
    List<BindAccountDetailDTO> getsActiveSupp(List<Integer> userIds);

    /**
     * 根据手机查询用户
     * @param phone
     * @return
     */
    NewSupplierUser getByPhone(String phone);

    /**
     * 根据邮箱查询用户
     * @param email
     * @return
     */
    NewSupplierUser getByEmail(String email);

    /**
     * 根据供应链公司id获取绑定总数
     * @param supplyChainId
     * @return
     */
    Integer getTotalAccount(Integer supplyChainId);


}
