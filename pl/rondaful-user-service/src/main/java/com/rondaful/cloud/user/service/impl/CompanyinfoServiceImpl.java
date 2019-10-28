package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.user.entity.Companyinfo;
import com.rondaful.cloud.user.mapper.CompanyinfoMapper;
import com.rondaful.cloud.user.service.CompanyinfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 卖家企业信息接口
 */
@Service("companyinfoService")
public class CompanyinfoServiceImpl implements CompanyinfoService {

    private Logger logger = LoggerFactory.getLogger(CompanyinfoServiceImpl.class);

    @Autowired
    private CompanyinfoMapper companyinfoMapper;

    /**
     * 判断当前用户的企业信息是否存在
     * @param userId
     * @return
     */
    @Override
    public Companyinfo isCompanyinfo(Integer userId,Integer platformType) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userId",userId);
        map.put("platformTypes",platformType);
        Companyinfo result = null;
        if (userId != null) result = companyinfoMapper.isCompanyinfo(map);
        return result;
    }

    /**
     * 新增用户企业信息
     * @param companyinfo
     * @return
     */
    @Override
    public Integer insertCompanyInfo(Companyinfo companyinfo) {
        Integer result = companyinfoMapper.insertCompanyInfo(companyinfo);
        return result;
    }

    /**
     * 修改用户企业信息
     * @param companyinfo
     * @return
     */
    @Override
    public Integer updateCompanyInfo(Companyinfo companyinfo) {
        Map<String,Object> map = new HashMap<String, Object>();
        Integer result = companyinfoMapper.updateCompanyInfo(companyinfo);
        return result;
    }

    /**
     * 供应链公司个人中心===>获取企业信息
     * @param userId
     * @return
     */
    @Override
    public Companyinfo getSupplyChainCompanyUserCompanyInfo(Integer userId) {
        Companyinfo companyinfo = companyinfoMapper.getSupplyChainCompanyUserCompanyInfo(userId);
        if (companyinfo == null) logger.error("供应链公司个人中心===>获取企业信息--->getSupplyChainCompanyUserCompanyInfo");
        return companyinfo;
    }


}
