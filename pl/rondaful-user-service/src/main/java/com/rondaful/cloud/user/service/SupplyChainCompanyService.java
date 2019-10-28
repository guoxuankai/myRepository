package com.rondaful.cloud.user.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.user.entity.Companyinfo;
import com.rondaful.cloud.user.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 供应链公司管理接口
 */
public interface SupplyChainCompanyService {

    /**
     * 新增供应链公司
     * @param user
     * @return
     */
    Integer insertSupplyChainCompany(User user);

    /**
     *  获取供应链公司基础信息
     * @param status
     * @param supplyChainCompany
     * @param createDate
     * @return
     */
    Page getSupplierChainCompanyUser(String status, String supplyChainCompany, Date createDate, Date createDateClose,
                                     Date updateDateStart, Date updateDateClose,String currPage, String row );

    /**
     * 供应链公司列表====>查询该供应链公司绑定的卖家和买家数量
     * @param companyinfoId
     * @return
     */
    Map<String,Object> getSupplyChainCompanyBindingNumber(Integer companyinfoId);

    /**
     * 供应链公司信息修改
     * @param user
     * @param companyinfo
     * @return
     */
    Integer supplyChainUpdateInfo(User user, Companyinfo companyinfo);

    /**
     * 供应链公司个人中心===>获取基本信息
     * @param supplyChainCompanyId
     * @return
     */
    User getSupplyChainCompanyUser(Integer supplyChainCompanyId);

    /**
     * 供应链公司的停用和启用
     * @param status
     * @param userIds
     * @return
     */
    Integer supplyChainCompanyStatusUpdate(Integer status, List<Integer> userIds);



}
