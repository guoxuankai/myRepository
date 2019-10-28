package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.ShippingAddress;
import com.rondaful.cloud.order.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderReceiveAddressDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysOrderReceiveAddressMapper extends BaseMapper<SysOrderReceiveAddress> {
    /**
     * 根据订单ID查询收货地址wujiachuang
     *
     * @param sysOrderId
     * @return
     */
    SysOrderReceiveAddress queryAddressByOrderId(@Param("sysOrderId") String sysOrderId);

    /**
     * 根据订单ID更改收货地址wujiachuang
     *
     * @param address
     */
    void updateAddressByOrderId(SysOrderReceiveAddress address);

    /**
     * 批量插入
     *
     * @param sysOrderReceiveAddressDTOList {@link List<SysOrderReceiveAddressDTO>}
     */
    void insertBatchSelective(@Param("list") List<SysOrderReceiveAddressDTO> sysOrderReceiveAddressDTOList);

    /**
     * 根据订单ID查询收货地址_ljt
     *
     * @param sysOrderId
     * @return
     */
    List<ShippingAddress> queryBatchAddressByOrderId(List<String> sysOrderId);
}