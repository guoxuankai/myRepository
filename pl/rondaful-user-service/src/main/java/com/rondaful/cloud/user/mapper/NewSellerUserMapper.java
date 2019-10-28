package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.NewSellerUser;
import com.rondaful.cloud.user.entity.QuerySellerPageDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface NewSellerUserMapper extends BaseMapper<NewSellerUser> {

    /**
     * 根据登录名获取账户信息
     * @param loginName
     * @return
     */
    NewSellerUser getByName(String loginName);

    /**
     * 总后台的分页查询接口
     * @return
     */
    List<NewSellerUser> getBackPage(QuerySellerPageDO queryDO);

    /**
     * 获取卖家所有一级账号的用户信息
     * @return
     */
    List<NewSellerUser> getTop(@Param("userIds") List<Integer> userIds,@Param("status") Integer status);

    /**
     * 根据状态获取一级账号总数
     * @param status
     * @return
     */
    Integer getSizeByStatus(@Param("status") Integer status);

    /**
     * 根据顶级账号获取子集账号列表
     * @param loginName
     * @param userName
     * @param departmentId
     * @param Jobs
     * @param startTime
     * @param endTime
     * @param userIds
     * @return
     */
    List<NewSellerUser> getChildPage(@Param("loginName") String loginName,@Param("userName") String userName,@Param("departmentId") Integer departmentId,
                            @Param("jobName") String jobName,@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("userIds") List<Integer> userIds,@Param("topUserId") Integer topUserId);


    /**
     * 批量获取用户
     * @param userId
     * @return
     */
    List<NewSellerUser> getsName(@Param("userId") List<String> userId);

    /**
     * 获取子账号名（临时调用）
     * @param userId
     * @return
     */
    List<NewSellerUser> getsChildName(@Param("userId") Integer userId);

    /**
     * 根据手机号查询
     * @param phone
     * @return
     */
    NewSellerUser getByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询
     * @param email
     * @return
     */
    NewSellerUser getByEmail(@Param("email") String email);

    /**
     * 查询组织是否绑定
     * @param departmentId
     * @return
     */
    Integer getsByDep(@Param("departmentId") Integer departmentId);

    /**
     * 根据供应链公司获取绑定总数
     * @param supplyChainId
     * @return
     */
    Integer getTotalAccount(@Param("supplyChainId") String supplyChainId);

    /**
     * 获取最大的id
     * @return
     */
    Integer getMaxId();

}