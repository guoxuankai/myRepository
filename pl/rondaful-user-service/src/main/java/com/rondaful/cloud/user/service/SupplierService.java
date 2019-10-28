package com.rondaful.cloud.user.service;

import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.entity.UserAndCompanyAndSalesReturnBean;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface SupplierService {

    /**
     *供应商登录
     * @param username
     * @param password
     * @return
     */
    public Map<String,Object> supplierUserLogin(String username, String password, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException;

    /**
     * 根据子账户的parentId找到他的父账户
     * @param parentId
     * @return
     */
    User getSupplierParentUserBySubUserParentId(Integer parentId);

    /**
     * 供应商个人中心
     * @param userId
     * @return
     */
    UserAndCompanyAndSalesReturnBean getSupplierPersonalCenter(Integer userId);

    /**
     * 忘记密码
     * @param phone
     * @return
     */
    Integer getSupplierUserByPhoneAndPlatformType(String phone,String email,Integer platformType,String newPassword);

    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    Integer supplierPasswordUpdate(Integer userId, String oldPassword, String newPassword);

    /**
     *判断该账户有无进行财务初始化
     * @param userId
     * @return
     */
    String isInitSupplier(Integer userId);

    /**
     * 将账户改为已经初始化财务账号
     * @param userId
     * @param insertResult
     * @return
     */
    Integer supplierInitResultOk(Integer userId, String insertResult);

    /**
     * 判断该供应商用户是否已经激活
     * @param userId
     * @return
     */
    Integer isSupplierUserDelfag(Integer userId);

}
