package com.rondaful.cloud.supplier.model.response.logistics;

import com.rondaful.cloud.supplier.model.dto.logistics.LogisticsSelectDTO;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/10/22
 * @Description:
 */
public class OrderSelectReq implements Serializable {
    private static final long serialVersionUID = 1548712156569682007L;

    @ApiModelProperty(value = "无效")
    private List<LogisticsSelectDTO> invalid;

    @ApiModelProperty(value = "有效")
    private List<LogisticsSelectDTO> valid;

    public List<LogisticsSelectDTO> getInvalid() {
        return invalid;
    }

    public void setInvalid(List<LogisticsSelectDTO> invalid) {
        this.invalid = invalid;
    }

    public List<LogisticsSelectDTO> getValid() {
        return valid;
    }

    public void setValid(List<LogisticsSelectDTO> valid) {
        this.valid = valid;
    }
}
