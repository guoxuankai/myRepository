package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.entity.Companyinfo;

public interface CompanyinfoService {

    /**
     * 判断当前用户的企业信息是否存在
     * @param userId
     * @return
     */
    Companyinfo isCompanyinfo(Integer userId, Integer platformType);

    /**
     * 新增供应商企业信息
     * @param companyinfo
     * @return
     */
    Integer insertCompanyInfo(Companyinfo companyinfo);

    /**
     * 修改供应商企业信息
     * @return
     */
    Integer updateCompanyInfo(Companyinfo companyinfo);

    /**
     * 供应链公司个人中心===>获取企业信息
     * @param userId
     * @return
     */
    Companyinfo getSupplyChainCompanyUserCompanyInfo(Integer userId);

}
