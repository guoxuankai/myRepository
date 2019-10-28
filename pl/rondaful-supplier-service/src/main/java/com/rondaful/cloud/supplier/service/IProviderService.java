package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderNameDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderPageDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.ProviderQueryPageDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/19
 * @Description:
 */
public interface IProviderService {

    /**
     * 新增供货商
     * @param dto
     * @return
     */
    Integer add(ProviderDTO dto);

    /**
     * 修改供货商状态
     * @param id
     * @param status
     * @param remake
     * @return
     */
    Integer updateStatus(Integer id,Integer status,String remake,String updateBy);

    /**
     * 修改供货商
     * @param dto
     * @return
     */
    Integer update(ProviderDTO dto);

    /**
     * 根据id获取供货商
     * @param id
     * @return
     */
    ProviderDTO get(Integer id);

    /**
     * 分页查询
     * @param queryDTO
     * @return
     */
    PageDTO<ProviderPageDTO> getsPage(ProviderQueryPageDTO queryDTO);

    /**
     * 获取供货商名称列表
     * @param supplierId
     * @return
     */
    List<KeyValueDTO> getSelectName(Integer supplierId,String pinlianSku);

    /**
     * 根据供应商获取对应级别的供货商
     * @param supplierId
     * @param level_Three
     * @return
     */
    List<ProviderNameDTO> getsProviderName(Integer supplierId, String levelThree);

}
