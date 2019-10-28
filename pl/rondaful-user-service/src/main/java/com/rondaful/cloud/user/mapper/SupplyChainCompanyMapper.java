package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.user.entity.SupplyChainCompanyListBean;
import com.rondaful.cloud.user.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SupplyChainCompanyMapper {

    /**
     * 新增供应链公司
     * @param user
     * @return
     */
    Integer insertSupplyChainCompany(User user);

    /**
     * 获取供应链公司基础信息
     * @param map
     * @return
     */
    List<SupplyChainCompanyListBean> getSupplierChainCompanyUser(Map<String,Object> map);

    /**
     * 供应链公司列表====>查询与此供应链公司相关联的卖家数量
     * @param companyinfoId
     * @param sellerCode
     * @return
     */
    Integer getSupplyChainCompanyBindingNumberSeller(@Param("companyinfoId") Integer companyinfoId, @Param("sellerCode") Integer sellerCode);

    /**
     * 供应链公司列表====>查询与此供应链公司相关联的供应商数量
     * @param companyinfoId
     * @param supplierCode
     * @return
     */
    Integer getSupplyChainCompanyBindingNumberSupplier(@Param("companyinfoId") Integer companyinfoId,@Param("supplierCode") Integer supplierCode );

    /**
     * 供应链公司信息修改===>验证用户名是否存在,根据公司id查询信息
     * @param supplyChainCompanyId
     * @return
     */
    User isSupplyChainCompanyById(@Param("supplyChainCompanyId") Integer supplyChainCompanyId);

    /**
     * 供应链公司信息修改===>修改供应链公司基础数据
     * @param user
     * @return
     */
    Integer updateSupplyChainInfo(User user);

    /**
     * 供应链公司个人中心===>获取基本信息
     * @param supplyChainCompanyId
     * @return
     */
    User getSupplyChainCompanyUser(@Param("supplyChainCompanyId") Integer supplyChainCompanyId);

    /**
     * 供应链公司的停用和启用
     * @param map
     * @return
     */
    Integer supplyChainCompanyStatusUpdate(Map<String,Object> map);

}
