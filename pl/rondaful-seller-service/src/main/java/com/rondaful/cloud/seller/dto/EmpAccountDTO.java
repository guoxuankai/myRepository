package com.rondaful.cloud.seller.dto;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/5/6
 * @Description:
 */
public class EmpAccountDTO {

    private Integer type;

    private List<BindEmpDTO> binds;

    public EmpAccountDTO(){}

    public EmpAccountDTO(Integer type, List<BindEmpDTO> binds) {
        this.type = type;
        this.binds = binds;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<BindEmpDTO> getBinds() {
        return binds;
    }

    public void setBinds(List<BindEmpDTO> binds) {
        this.binds = binds;
    }
}
