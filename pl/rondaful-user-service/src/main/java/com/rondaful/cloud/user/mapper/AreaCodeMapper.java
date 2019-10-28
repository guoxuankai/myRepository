package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.AreaCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AreaCodeMapper extends BaseMapper<AreaCode> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer insertBranch(List<AreaCode> list);

    /**
     * 根据等级及父级id获取列表
     * @param level
     * @param parentId
     * @return
     */
    List<AreaCode> getsByLevel(@Param("level") Integer level,@Param("parentId") Integer parentId);

    /**
     * 获取所有组织
     * @return
     */
    List<AreaCode> getsAll();

    int updateByCode(AreaCode areaCode);

    /**
     * 根据code查询
     * @param code
     * @return
     */
    AreaCode getNameByCode(@Param("code") String code,@Param("level") Integer level);


    /**
     * 根据code 查询国家
     * @param countryName
     * @param countryCode
     * @return
     */
    AreaCode getArea(@Param("countryName")String countryName, @Param("countryCode")String countryCode);


}