package com.rondaful.cloud.user.service.impl;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.entity.Companyinfo;
import com.rondaful.cloud.user.entity.SupplyChainCompanyListBean;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.CompanyinfoMapper;
import com.rondaful.cloud.user.mapper.SupplyChainCompanyMapper;
import com.rondaful.cloud.user.service.SupplyChainCompanyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("supplyChainCompanyService")
public class SupplyChainCompanyServiceImpl implements SupplyChainCompanyService {

    private Logger logger = LoggerFactory.getLogger(SupplyChainCompanyServiceImpl.class);

    @Autowired
    private SupplyChainCompanyMapper supplyChainCompanyMapper;

    @Autowired
    private CompanyinfoMapper companyinfoMapper;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    /**
     * 新增供应链公司
     * @param user
     * @return
     */
    @Override
    public Integer insertSupplyChainCompany(User user) {
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        user.setEnabled(userAll.getUser().getUsername());
        user.setCreateDate(new Date());
        user.setPlatformType(UserConstants.SUPPLYCHAINCOMPANY);
        Integer result = supplyChainCompanyMapper.insertSupplyChainCompany(user);
        return result;
    }

    /**
     * 获取供应链公司基础信息
     * @param status
     * @param supplyChainCompany
     * @param createDate
     * @return
     */
    @Override
    public Page<SupplyChainCompanyListBean> getSupplierChainCompanyUser(String status, String supplyChainCompany, Date createDate, Date createDateClose,
                                            Date updateDateStart, Date updateDateClose,String currPage, String row ) {
        Page.builder(currPage,row);
        Map<String,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("supplyChainCompany",supplyChainCompany);
        map.put("createDate",createDate);
        map.put("createDateClose",createDateClose);
        map.put("updateDateStart",updateDateStart);
        map.put("updateDateClose",updateDateClose);
        List<SupplyChainCompanyListBean> user = supplyChainCompanyMapper.getSupplierChainCompanyUser(map);
        for (SupplyChainCompanyListBean supply : user){
            //查询该供应链公司绑定的卖家和买家数量
            map.clear();
            map = getSupplyChainCompanyBindingNumber(supply.getId());
            if ( map.get("sellerNunble") != null ) supply.setBindingSeller((Integer) map.get("sellerNunble"));
            if ((Integer)map.get("supplierNumble") != null) supply.setBindingSupplier((Integer)map.get("supplierNumble"));
        }
        PageInfo pi = new PageInfo(user);
        return new Page(pi);
    }

    /**
     *供应链公司列表====>查询该供应链公司绑定的卖家和买家数量
     * @param companyinfoId
     * @return
     */
    @Override
    public Map<String, Object> getSupplyChainCompanyBindingNumber(Integer companyinfoId) {
        Map<String,Object> map = new HashMap<>();
        //查询与此供应链公司相关联的卖家数量
        Integer sellerCode = UserConstants.SELLERPLATFORM;
        Integer sellerNunble = supplyChainCompanyMapper.getSupplyChainCompanyBindingNumberSeller(companyinfoId,sellerCode);
        map.put("sellerNunble",sellerNunble);
        //查询与此供应链公司相关联的供应商数量
        Integer supplierCode = UserConstants.SUPPLIERPLATFORM;
        Integer supplierNumber = supplyChainCompanyMapper.getSupplyChainCompanyBindingNumberSupplier(companyinfoId,supplierCode);
        map.put("supplierNumble",supplierNumber);
        return map;
    }

    /**
     * 供应链公司信息修改
     * @param user
     * @param companyinfo
     * @return
     */
    @Override
    public Integer supplyChainUpdateInfo(User user, Companyinfo companyinfo) {
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        // 验证用户名是否存在,根据公司id查询信息
        User userResult = supplyChainCompanyMapper.isSupplyChainCompanyById(user.getUserid());
        if (userResult == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"查无此供应链公司，请重试");
        //修改供应链公司基础数据
        user.setUpdateDate(new Date());
        user.setRemarks(StringUtils.isNotBlank(userAll.getUser().getUsername()) ? userAll.getUser().getUsername() : "");
        if (companyinfo != null && StringUtils.isNotBlank(companyinfo.getCompanyName())) user.setCompanyNameUser(companyinfo.getCompanyName());
        Integer updateSupplyChainInfo = supplyChainCompanyMapper.updateSupplyChainInfo(user);
        if (updateSupplyChainInfo == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),"修改供应链公司基础数据失败");
        //修改供应链公司企业数据
        Integer updateSupplyChainCompany = null;
        if (companyinfo != null) updateSupplyChainCompany = companyinfoMapper.updateSupplyChainCompany(companyinfo);
        if (updateSupplyChainCompany == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),"修改供应链公司企业数据失败");
        return updateSupplyChainInfo+updateSupplyChainCompany;
    }

    /**
     * 供应链公司个人中心===>获取基本信息
     * @param supplyChainCompanyId
     * @return
     */
    @Override
    public User getSupplyChainCompanyUser(Integer supplyChainCompanyId) {
        User user = supplyChainCompanyMapper.getSupplyChainCompanyUser(supplyChainCompanyId);
        return user;
    }

    /**
     * 供应链公司的停用和启用
     * @param status
     * @param userIds
     * @return
     */
    @Override
    public Integer supplyChainCompanyStatusUpdate(Integer status, List<Integer> userIds) {
        Map<String,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("userIds",userIds);
        Integer result = supplyChainCompanyMapper.supplyChainCompanyStatusUpdate(map);
        return result;
    }

}
