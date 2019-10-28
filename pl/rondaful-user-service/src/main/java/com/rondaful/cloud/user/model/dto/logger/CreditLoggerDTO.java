package com.rondaful.cloud.user.model.dto.logger;


import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/7/25
 * @Description:
 */
@TypeAlias("credit_logger")
public class CreditLoggerDTO implements Serializable {
    private static final long serialVersionUID = 4436260907086923163L;

    @ApiModelProperty(value = "用户id", name = "userId", dataType = "Integer")
    private Integer userId;

    @ApiModelProperty(value = "创建人", name = "createBy", dataType = "String")
    private String createBy;

    @ApiModelProperty(value = "创建日期", name = "createDate", dataType = "String")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "操作事项", name = "operate", dataType = "String")
    private String operate;

    @ApiModelProperty(value = "描述", name = "desc", dataType = "String")
    private String desc;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
