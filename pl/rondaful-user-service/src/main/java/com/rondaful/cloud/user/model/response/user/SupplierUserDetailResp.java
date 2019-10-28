package com.rondaful.cloud.user.model.response.user;

import com.rondaful.cloud.user.model.dto.user.SupplierUserDetailDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/8/22
 * @Description:
 */
public class SupplierUserDetailResp extends SupplierUserDetailDTO {

    private List<String> proportion;

    public List<String> getProportion() {
        return proportion;
    }

    public void setProportion(List<String> proportion) {
        this.proportion = proportion;
    }
}
