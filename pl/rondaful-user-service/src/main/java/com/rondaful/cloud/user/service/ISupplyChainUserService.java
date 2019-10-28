package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.KeyValueDTO;
import com.rondaful.cloud.user.model.dto.user.QuerySuppluChinaDTO;
import com.rondaful.cloud.user.model.dto.user.SupplyChainUserDTO;
import com.rondaful.cloud.user.model.dto.user.SupplyChainUserPageDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/22
 * @Description:
 */
public interface ISupplyChainUserService {
    public static String SUPPLY_CHINA_USER_ID="user:supply.china.user.id.v1.";
    public static String SUPPLY_CHINA_USER_BIDN_TYPE="user:supply.china.user.bindtype.v2.";

    /**
     * 添加供应链公司
     * @param dto
     * @return
     */
    Integer add(SupplyChainUserDTO dto);

    /**
     * 根据id获取供应链公司
     * @param id
     * @return
     */
    SupplyChainUserDTO get(Integer id);

    /**
     * 修改供应链公司
     * @param dto
     * @return
     */
    Integer update(SupplyChainUserDTO dto);

    /**
     * 分页查询供应链公司
     * @param dto
     * @return
     */
    PageDTO<SupplyChainUserPageDTO> getsPage(QuerySuppluChinaDTO dto);

    /**
     * 根据平台类型获取绑定供应链公司列表
     * @param type
     * @return
     */
    List<KeyValueDTO> getsSelect(Integer type);

}
