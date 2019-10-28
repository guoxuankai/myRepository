package com.rondaful.cloud.order.model.dto.syncorder;

import java.io.Serializable;
import java.util.List;

/**
 * 系统订单插入DTO
 *
 * @author Blade
 * @date 2019-07-22 10:33:07
 **/
public class SysOrderTransferInsertOrUpdateDTO implements Serializable {
    private static final long serialVersionUID = -7656183008454908795L;

    private List<SysOrderDTO> sysOrderDTOList;

    private List<SysOrderDetailDTO> sysOrderDetailDTOList;

    private List<SysOrderReceiveAddressDTO> sysOrderReceiveAddressDTOList;

    private List<SysOrderPackageDTO> sysOrderPackageDTOList;

    private List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList;

    private List<UpdateSourceOrderDTO> updateSourceOrderDTOList;

    private List<UpdateSourceOrderDetailDTO> updateSourceOrderDetailDTOList;

    public List<SysOrderDTO> getSysOrderDTOList() {
        return sysOrderDTOList;
    }

    public void setSysOrderDTOList(List<SysOrderDTO> sysOrderDTOList) {
        this.sysOrderDTOList = sysOrderDTOList;
    }

    public List<SysOrderDetailDTO> getSysOrderDetailDTOList() {
        return sysOrderDetailDTOList;
    }

    public void setSysOrderDetailDTOList(List<SysOrderDetailDTO> sysOrderDetailDTOList) {
        this.sysOrderDetailDTOList = sysOrderDetailDTOList;
    }

    public List<SysOrderReceiveAddressDTO> getSysOrderReceiveAddressDTOList() {
        return sysOrderReceiveAddressDTOList;
    }

    public void setSysOrderReceiveAddressDTOList(List<SysOrderReceiveAddressDTO> sysOrderReceiveAddressDTOList) {
        this.sysOrderReceiveAddressDTOList = sysOrderReceiveAddressDTOList;
    }

    public List<SysOrderPackageDTO> getSysOrderPackageDTOList() {
        return sysOrderPackageDTOList;
    }

    public void setSysOrderPackageDTOList(List<SysOrderPackageDTO> sysOrderPackageDTOList) {
        this.sysOrderPackageDTOList = sysOrderPackageDTOList;
    }

    public List<SysOrderPackageDetailDTO> getSysOrderPackageDetailDTOList() {
        return sysOrderPackageDetailDTOList;
    }

    public void setSysOrderPackageDetailDTOList(List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList) {
        this.sysOrderPackageDetailDTOList = sysOrderPackageDetailDTOList;
    }

    public List<UpdateSourceOrderDTO> getUpdateSourceOrderDTOList() {
        return updateSourceOrderDTOList;
    }

    public void setUpdateSourceOrderDTOList(List<UpdateSourceOrderDTO> updateSourceOrderDTOList) {
        this.updateSourceOrderDTOList = updateSourceOrderDTOList;
    }

    public List<UpdateSourceOrderDetailDTO> getUpdateSourceOrderDetailDTOList() {
        return updateSourceOrderDetailDTOList;
    }

    public void setUpdateSourceOrderDetailDTOList(List<UpdateSourceOrderDetailDTO> updateSourceOrderDetailDTOList) {
        this.updateSourceOrderDetailDTOList = updateSourceOrderDetailDTOList;
    }
}
