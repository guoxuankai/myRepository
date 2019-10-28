package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.AddressDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/8/7
 * @Description:
 */
public interface IStorageAddressService {

    /**
     * 添加联系地址
     * @param dto
     * @return
     */
    Integer add(AddressDTO dto);

    /**
     * 修改联系地址
     * @param dto
     * @return
     */
    Integer update(AddressDTO dto);

    /**
     * 删除联系地址
     * @param id
     * @return
     */
    Integer del(Integer id);

    /**
     * 获取所有地址
     * @param supplierId
     * @return
     */
    PageDTO<AddressDTO> getsBySupplierId(Integer supplierId, String phone, Integer currentPage, Integer pageSize);


}
