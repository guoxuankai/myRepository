package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.Companyinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CompanyinfoMapper extends BaseMapper<Companyinfo> {

    /**
     * 判断当前用户的企业信息是否存在
     * @param map
     */
    Companyinfo isCompanyinfo(Map<String,Object> map);

    /**
     * 新增用户企业信息
     * @param companyinfo
     * @return
     */
    Integer insertCompanyInfo(Companyinfo companyinfo);

    /**
     * 修改用户企业信息
     * @param companyinfo
     * @return
     */
    Integer updateCompanyInfo(Companyinfo companyinfo);

    /**
     * 修改供应链公司企业数据
     * @param companyinfo
     * @return
     */
    Integer updateSupplyChainCompany(Companyinfo companyinfo);

    /**
     * 供应链公司个人中心===>获取企业信息
     * @param userId
     * @return
     */
    Companyinfo getSupplyChainCompanyUserCompanyInfo(@Param("userId") Integer userId);

    /**
     * 供应商个人中心===>获取企业信息
     * @param userId
     * @return
     */
    Companyinfo getSupplierCompanyUserCompanyInfo(@Param("userId")Integer userId);

    /**
     * 卖家个人中心===>获取企业信息
     * @param userId
     * @return
     */
    Companyinfo getSellerCompanyUserCompanyInfo(@Param("userId") Integer userId);

    
    
    Companyinfo selectByUserId(Integer userId);
    
    
    Integer updateCompanyInfoByUserId(Companyinfo companyinfo);

    /**
     * 获取所有供应链公司名称
     * @return
     */
    List<Companyinfo> getsName();


    
}