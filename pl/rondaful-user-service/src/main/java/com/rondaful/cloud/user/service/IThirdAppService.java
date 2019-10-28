package com.rondaful.cloud.user.service;

import com.rondaful.cloud.common.entity.third.AppDTO;
import com.rondaful.cloud.user.model.PageDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/7/2
 * @Description:
 */
public interface IThirdAppService {

    /**
     * 新增应用
     * @param dto
     * @return
     */
    Integer add(AppDTO dto);

    /**
     * 修改应用
     * @param dto
     * @return
     */
    Integer update(AppDTO dto);

    /**
     * 修改状态
     * @param appKey
     * @param status
     * @return
     */
    Integer updateStatus(String appKey,Integer status);

    /**
     * 根据appkey获取授权信息
     * @param appKey
     * @return
     */
    AppDTO getByAppKey(String appKey);

    /**
     * 分页查询应用授权列表
     * @param currentPage
     * @param pageSize
     * @param status
     * @return
     */
    PageDTO<AppDTO> getsPage(Integer currentPage,Integer pageSize,Integer status);

    /**
     * 获取所有有效的第三方应用
     * @return
     */
    List<AppDTO> getsAll();

    /**
     * 重置密码
     * @return
     */
    Integer resetAppToken(Integer id);

    /**
     * 根据绑定类型获取列表
     * @param bindType
     * @param bindCode
     * @return
     */
    List<AppDTO> getsByBindCode(Integer bindType,String bindCode);


}
