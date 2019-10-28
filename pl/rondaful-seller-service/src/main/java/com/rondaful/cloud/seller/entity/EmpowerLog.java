package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class EmpowerLog implements Serializable {
	@ApiModelProperty(value = "id")
    private Integer id;
	@ApiModelProperty(value = "操作人")
    private String handler;
	@ApiModelProperty(value = "操作")
    private String operation;
	@ApiModelProperty(value = "创建时间")
    private Date createtime;
	@ApiModelProperty(value = "授权表id")
    private Integer empowerid;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler == null ? null : handler.trim();
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation == null ? null : operation.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getEmpowerid() {
        return empowerid;
    }

    public void setEmpowerid(Integer empowerid) {
        this.empowerid = empowerid;
    }
}