package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/29
 * @Description:
 */
public class BindAccountDTO implements Serializable {
    private static final long serialVersionUID = -4423375352248623555L;

    private Integer type;
    private List<BindAccountDetailDTO> list;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<BindAccountDetailDTO> getList() {
        return list;
    }

    public void setList(List<BindAccountDetailDTO> list) {
        this.list = list;
    }
}
