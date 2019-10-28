package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceExportInfoVO;
import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.vo.sysOrderInvoice.SysOrderInvoiceVO;

/**
 * @author Blade
 * @date 2019-06-17 17:24:59
 **/
public interface ISysOrderInvoiceService {

    /**
     * 新增或者更新系统发票
     *
     * @param sysOrderInvoiceInsertOrUpdateDTO {@link SysOrderInvoiceInsertOrUpdateDTO}
     */
    void insertOrUpdateSysOrderInvoice(SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO);

    /**
     * 根据订单ID获取订单发票信息
     *
     * @param sysOrderId 系统订单ID
     * @return {@link SysOrderInvoiceVO}
     */
    SysOrderInvoiceVO getSysOrderInvoiceBySysOrderId(String sysOrderId);

    /**
     * 导出发票pdf
     *
     * @param name                             发票名称
     * @param sysOrderInvoiceInsertOrUpdateDTO {@link SysOrderInvoiceInsertOrUpdateDTO}
     * @param sysOrderId                       系统订单ID
     * @return pdf path
     * @throws Exception 异常
     */
    String exportPDF(String name, SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO, String sysOrderId) throws Exception;

    /**
     * 保存发票内容信息
     *
     * @param sysOrderInvoiceInsertOrUpdateDTO {@link SysOrderInvoiceInsertOrUpdateDTO}
     * @return {@link SysOrderInvoiceExportInfoVO}
     */
    void saveInvoiceInfo(SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO);
}
