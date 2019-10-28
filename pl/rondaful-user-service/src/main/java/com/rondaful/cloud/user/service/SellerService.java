package com.rondaful.cloud.user.service;

import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.user.entity.SellerInfo;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.entity.UserAndCompanyAndSalesReturnBean;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 卖家接口
 */
public interface SellerService{

    /**
     * 卖家登录
     * @param username
     * @param password
     * @param platformType
     * @return
     */
    public Map<String,Object> sellerUserLogin(String username, String password, Integer platformType, HttpServletResponse response) throws IllegalAccessException, InvocationTargetException;

    /**
     * 卖家登录 ===> 根据子账户的parentId找到他的父账户
     * @param parentId
     * @return
     */
    public User getSellerParentUserBySubUserParentId(Integer parentId);

    /**
     * 卖家个人中心
     * @param userId
     * @return
     */
    public UserAndCompanyAndSalesReturnBean getSellerPersonalCenter(Integer userId);

    /**
     * 忘记密码
     * @param phone
     * @return
     */
    Integer getSellerUserByPhoneAndPlatformType(String phone,String email,Integer platformType,String newPassword);

    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    Integer sellerPasswordUpdate(Integer userId, String oldPassword, String newPassword);

    /**
     *判断当前卖家信息是否已经添加
     * @param platformTypes
     * @param userId
     * @return
     */
    Integer isSellerInfo(Integer platformTypes,Integer userId);

    /**
     * 添加买家信息
     * @param sellerInfo
     * @return
     */
    Integer insertSellerInfo(SellerInfo sellerInfo);

    /**
     * 判断该用户是否已经激活
     * @param userId
     * @return
     */
    Integer isSellerUserDelfag(Integer userId);

    /**
     * 判断当前手机是否已经绑定
     * @param phone
     * @return
     */
    Integer isPhoneSellerUser(String phone,Integer userId);
    
    /**
     * 卖家手机号码绑定
     * @param userId
     * @param phone
     * @return
     */
    Integer sellerPhoneBinding(Integer userId,String phone);

    /**
     * 获取是否需要绑定手机信息
     * @param userId
     * @return
     */
    Boolean getIsPhoneBinding(Integer userId);

}
