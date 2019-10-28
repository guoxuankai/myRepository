package com.rondaful.cloud.user.mapper;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.user.entity.SysLog;

public interface SysLogMapper {
  //  int deleteByPrimaryKey(Integer id);

   // int insert(SysLog record);

    int insertSelective(SysLog record);

   // SysLog selectByPrimaryKey(Integer id);

 //   int updateByPrimaryKeySelective(SysLog record);

  //  int updateByPrimaryKey(SysLog record);
    
    /**
     * 根据用户名查询对应的用户信息
     * @param username
     * @return
     */
//    List<SysLog> getUserSysLog(@Param("username")String username);
    
    /**
     * 查询多个用户的操作日志
     * @param map
     * @return
     */
    List<SysLog> getSysLogByListUsername(Map<String,Object> map);
   
    
}