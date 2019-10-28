package com.rondaful.cloud.seller.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 前缀码
 * @author guoxuankai
 * @date 2019/6/10
 */
@ApiModel(value = "前缀码")
public class PrefixCode implements Serializable {

    @ApiModelProperty(value = "唯一id")
    private Integer id;

    @ApiModelProperty(value = "前缀码")
    private String prefixcode;

    @ApiModelProperty(value = "已经生成次数，10000封顶")
    private Integer count;

    @ApiModelProperty(value = "状态  1可生成upc码 0作废")
    private Integer status;

    @ApiModelProperty(value = "所属账户")
    private String account;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


    private static final long serialVersionUID = 1L;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getPrefixcode() {
        return prefixcode;
    }


    public void setPrefixcode(String prefixcode) {
        this.prefixcode = prefixcode == null ? null : prefixcode.trim();
    }


    public Integer getCount() {
        return count;
    }


    public void setCount(Integer count) {
        this.count = count;
    }


    public Integer getStatus() {
        return status;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getAccount() {
        return account;
    }


    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }


    public Date getCreateTime() {
        return createTime;
    }


    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}