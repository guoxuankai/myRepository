package com.rondaful.cloud.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 模糊查询接口
 */
@Mapper
public interface FuzzyQueryMapper {

    /**
     * 操作账号管理-操作员模糊查询
     * @param map
     */
    List<String> getOperationUsernamr(Map<String,Object> map);

    /**
     *角色管理-角色代码模糊搜索 - 查找用户对应角色id
     * @param userid
     * @return
     */
    List<Integer> getroleFuzzyQueryRoleId(@Param("userid") Integer userid);

    /**
     * 角色管理-角色代码模糊搜索 - 查找用户对应角色code
     * @param map
     * @return
     */
    List<String> getroleFuzzyQuery(Map<String,Object> map);

    /**
     *供应商管理-供应商公司模糊搜索
     * @param spplierCompany
     * @return
     */
    List<String> getSpplierCompanyFuzzyQuery(@Param("spplierCompany") String spplierCompany);

}
