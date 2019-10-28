package com.rondaful.cloud.order.model.vo.invoiceTemplate;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 发票模板下拉列表模板
 *
 * @author Blade
 * @date 2019-06-20 17:52:54
 **/
public class InvoiceTemplateDropDownListVO implements Serializable {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
