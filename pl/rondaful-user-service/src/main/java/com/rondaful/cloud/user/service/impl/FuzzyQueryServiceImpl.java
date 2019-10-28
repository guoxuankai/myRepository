package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.user.mapper.FuzzyQueryMapper;
import com.rondaful.cloud.user.service.FuzzyQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *模糊查询接口
 */
@Service("fuzzyQueryService")
public class FuzzyQueryServiceImpl  implements FuzzyQueryService {

    @Autowired
    private FuzzyQueryMapper fuzzyQueryMapper;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    /**
     * 操作账号管理-操作员模糊查询
     * @param username
     * @return
     */
    @Override
    public List<String> getOperationUsernamr(String username) {
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("userid",userAll.getUser().getUserid());
        map.put("username",username);
        map.put("platformType",userAll.getUser().getPlatformType());
        List<String> usernames = null;
        if (map != null) usernames = fuzzyQueryMapper.getOperationUsernamr(map);
        return usernames;
    }

    /**
     * 角色管理-角色代码模糊搜索
     * @param roleCode
     * @return
     */
    @Override
    public List<String> getroleFuzzyQuery(String roleCode) {
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();//当前用户信息
        Map<String,Object> map = new HashMap<String,Object>();
        List<String> roleCodes = null;
        List<Integer> roleIds = null;
        if (userAll.getUser().getUserid() != null) roleIds = fuzzyQueryMapper.getroleFuzzyQueryRoleId(userAll.getUser().getUserid());//根据当前用户id查询用户所拥有的角色id
        map.put("userid",userAll.getUser().getUserid());
        map.put("roleCode",roleCode);
        map.put("roleIds",roleIds);
        if (roleIds != null) roleCodes = fuzzyQueryMapper.getroleFuzzyQuery(map);//迷糊查询当前输入的角色代码
        return roleCodes;//返回角色代码
    }

    /**
     * 供应商管理-供应商公司模糊搜索
     * @param spplierCompany
     * @return
     */
    @Override
    public List<String> getSpplierCompanyFuzzyQuery(String spplierCompany) {
        List<String> companyName = null;
        if (StringUtils.isNotBlank(spplierCompany)) companyName = fuzzyQueryMapper.getSpplierCompanyFuzzyQuery(spplierCompany);
        return companyName;
    }


}
