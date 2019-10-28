package com.rondaful.cloud.order.model.dto.syncorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 订单包裹拆分
 *
 * @author lijt
 * @date 2019-07-23 10:00:00
 */
@ApiModel(value = "SplitPackageDTO")
public class SplitPackageDTO implements Serializable {

    @ApiModelProperty(value = "系统订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "接受包裹对象")
    private List<SysOrderPackageDTO> sysOrderPackageDTOList;

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId;
    }

    public List<SysOrderPackageDTO> getSysOrderPackageDTOList() {
        return sysOrderPackageDTOList;
    }

    public void setSysOrderPackageDTOList(List<SysOrderPackageDTO> sysOrderPackageDTOList) {
        this.sysOrderPackageDTOList = sysOrderPackageDTOList;
    }
}