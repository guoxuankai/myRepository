package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.SysOrderInvoice;
import com.rondaful.cloud.order.model.vo.sysOrderInvoice.SysOrderInvoiceVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysOrderInvoiceMapper extends BaseMapper<SysOrderInvoice> {

    SysOrderInvoiceVO selectBySysOrderId(String sysOrderId);

    void insertOrUpdateSelective(SysOrderInvoice sysOrderInvoice);
}