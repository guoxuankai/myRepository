package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.InvoiceTemplate;
import com.rondaful.cloud.order.model.dto.invoiceTemplate.InvoiceTemplateListSearchDTO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateDropDownListVO;
import com.rondaful.cloud.order.model.vo.invoiceTemplate.InvoiceTemplateListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InvoiceTemplateMapper extends BaseMapper<InvoiceTemplate> {

    List<InvoiceTemplateListVO> page(@Param("searchDTO") InvoiceTemplateListSearchDTO searchDTO,
                                     @Param("userIdList") List<Integer> userIdList);

    List<InvoiceTemplateDropDownListVO> selectDropDownList(@Param("userIdList") List<Integer> userIdList);
}