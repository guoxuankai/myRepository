package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.entity.NewSellerUser;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.user.*;
import com.rondaful.cloud.user.model.response.provider.ProviderUserDTO;

import java.util.Date;
import java.util.List;


/**
 * @Author: xqq
 * @Date: 2019/4/28
 * @Description:
 */
public interface ISellerUserService {

    public static String QUERY_SELLER_USER_MANAGE_NAME="user:seller.user.seller.name.v4";
    public static String QUERY_SELLER_USER_MANAGE_EMAIL="user:seller.user.seller.email.v0";
    public static String QUERY_SELLER_USER_MANAGE_PHONE="user:seller.user.seller.phone.v0";
    public static String QUERY_SELLER_USER_MANAGE_ID="user:seller.user.seller.id.v4";

    /**
     * 新增总后台账号
     * @param dto
     * @return
     */
    Integer add(SellerUserDTO dto);

    /**
     * 一级账号的编辑
     * @param dto1
     * @param dto2
     * @return
     */
    Integer update(SellerUserDTO dto1,SellerCompanyDTO dto2);

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
     * 根据名字查询用户
     * @param name
     * @return
     */
    SellerUserDetailDTO getByName(String name);

    /**
     * 根据id获取用户详情
     * @param userId
     * @return
     */
    SellerUserDetailDTO getById(Integer userId);

    /**
     * 获取子账号详情
     * @param userId
     * @return
     */
    SellerUserChildDetail getChildById(Integer userId,String languageType);

    /**
     * 后台分页查询接口
     * @param dto
     * @return
     */
    PageDTO<BackSellerPageDTO> getBackPage(QueryBackSellerDTO dto);

    /**
     * 根据授信状态查询
     * @param status
     * @param currentPage
     * @param pageSize
     * @return
     */
    PageDTO<BackSellerPageDTO> getsCreditPage(String status,Integer currentPage,Integer pageSize);

    /**
     * 获取所有一级账号
     * @return
     */
    BindAccountDTO getsParent(List<Integer> userIds,Integer type);

    /**
     * 根据平台查询对应卖家下的子账号
     * @param dto
     * @return
     */
    PageDTO<PithyUserDTO> getChildPage(QueryChildDTO dto);

    /**
     * 根据id批量获取用户
     * @param userIds
     * @return
     */
    List<NewSellerUser> getsName(List<String> userIds);

    /**
     * 获取子账号名（临时调用）
     * @param userId
     * @return
     */
    List<NewSellerUser> getsChildName(Integer userId);

    /**
     *
     * @param binds
     * @param userId
     * @param updateBy
     * @return
     */
    Integer bindEmp(List<UserOrgDTO> binds,Integer userId,String updateBy);

    /**
     * 删除账号
     * @param userId
     * @return
     */
    Integer delete(Integer userId);

    /**
     * 修改头像
     * @param userId
     * @param imgPath
     * @return
     */
    Integer updateHeadImg(Integer userId,String imgPath);

    /**
     * 修改手机号
     * @param phone
     * @param userId
     * @return
     */
    Integer updatePhone(String phone,Integer userId,String email);

    /**
     * 修改状态
     * @param dto
     * @return
     */
    Integer updateStatus(SellerUserDTO dto,String mainCategory);
    /**
     * 根据手机号查询
     * @param phone
     * @return
     */
    SellerUserDetailDTO getByPhone(String phone);

    /**
     * 根据邮件查询用户
     * @param email
     * @return
     */
    SellerUserDetailDTO getByEmail(String email);

    /**
     * 个人中心资料修改
     * @param dto
     * @return
     */
    Integer updateCenter(SellerUserDTO dto);

    /**
     * 根据供应链公司id获取绑定总数
     * @param supplyChainId
     * @return
     */
    Integer getTotalAccount(Integer supplyChainId);

    /**
     * 子账号绑定店铺
     * @param userId
     * @param empowerId
     * @return
     */
    Integer bindStore(Integer userId,Integer empowerId);

    /**
     * 获取数据库当前最大的id
     * @return
     */
    Integer getMaxId();

    /**
     * 根据状态获取一级账号总数
     * @param status
     * @return
     */
    Integer getSizeByStatus(Integer status);

    /**
     * 导出
     * @return
     */
    List<Object> export(Integer currentPage, Integer userId,Date startDate,Date endDate);

    /**
     * 分页获取所有卖家账号
     * @param currentPage
     * @param pageSize
     * @return
     */
    PageDTO<ProviderUserDTO> getPageUser(Integer currentPage,Integer pageSize);

    /**
     * 根据店铺id获取绑定 子账号信息
     * @param storeId
     * @return
     */
    List<ProviderUserDTO> getsUserByStore(Integer storeId);
}
