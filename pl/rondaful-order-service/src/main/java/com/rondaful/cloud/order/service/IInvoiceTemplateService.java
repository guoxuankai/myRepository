package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateInsertDTO;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateListSearchDTO;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateUpdateDTO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateDropDownListVO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateInfoVO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateListVO;

import java.util.List;

/**
 * @author Blade
 * @date 2019-06-17 17:26:32
 **/
public interface IInvoiceTemplateService {

    /**
     * 新增发票模板
     *
     * @param invoiceTemplate {@link InvoiceTemplateInsertDTO}
     */
    void insert(InvoiceTemplateInsertDTO invoiceTemplate);

    /**
     * 发票模板删除
     *
     * @param id long
     */
    void delete(long id);

    /**
     * 更新发票模板
     *
     * @param invoiceTemplateUpdateDTO {@link InvoiceTemplateUpdateDTO}
     */
    void update(InvoiceTemplateUpdateDTO invoiceTemplateUpdateDTO);

    /**
     * 发票模板分页查询
     *
     * @param searchDTO {@link InvoiceTemplateListSearchDTO}
     * @return {@link Page<InvoiceTemplateListVO>}
     */
    Page<InvoiceTemplateListVO> page(InvoiceTemplateListSearchDTO searchDTO);

    /**
     * 根据ID查询发票模板
     *
     * @param id id
     * @return {@link InvoiceTemplateInfoVO}
     */
    InvoiceTemplateInfoVO findById(Long id);

    /**
     * 获取发票模板下拉列表，包括该账户和主账号的模板
     *
     * @param sellerId 卖家id
     * @return {@link List<InvoiceTemplateDropDownListVO>}
     */
    List<InvoiceTemplateDropDownListVO> selectDropDownList(Integer sellerId);
}
