package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.NewMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewMenuMapper extends BaseMapper<NewMenu> {

    /**
     * 根据平台类型获取菜单列表
     * @param platformType
     * @return
     */
    List<NewMenu> getsAll(@Param("platformType") Integer platformType);

    /**
     * 根据id批量查询
     * @param menuIds
     * @return
     */
    List<NewMenu> getsByIds(@Param("menuIds") List<Integer> menuIds);
}