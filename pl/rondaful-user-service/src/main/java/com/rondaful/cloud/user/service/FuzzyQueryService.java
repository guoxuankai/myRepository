package com.rondaful.cloud.user.service;

import java.util.List;
import java.util.Map;

/**
 * 模糊查询接口
 */
public interface FuzzyQueryService {
    /**
     * 操作账号管理-操作员模糊查询
     * @param username
     * @return
     */
     List<String> getOperationUsernamr(String username);

    /**
     * 角色管理-角色代码模糊搜索
     * @param roleCode
     * @return
     */
    List<String> getroleFuzzyQuery(String roleCode);

    /**
     * 供应商管理-供应商公司模糊搜索
     * @param spplierCompany
     * @return
     */
    List<String> getSpplierCompanyFuzzyQuery(String spplierCompany);
    

}
