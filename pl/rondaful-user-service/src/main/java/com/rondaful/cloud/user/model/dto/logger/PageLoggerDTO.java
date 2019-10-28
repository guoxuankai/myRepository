package com.rondaful.cloud.user.model.dto.logger;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/6/12
 * @Description:
 */
public class PageLoggerDTO implements Serializable {
    private static final long serialVersionUID = -1868804840296033257L;

    @ApiModelProperty(value = "loginName", required = false)
    private String loginName;

    @ApiModelProperty(value = "用户ip地址", required = false)
    private String clientIp;

    @ApiModelProperty(value = "操作路径", required = false)
    private String optionUrl;

    @ApiModelProperty(value = "操作时间", required = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "操作的类型：增，删，改，查", required = false)
    private String optionActiontype;

    @ApiModelProperty(value = "操作内容", required = false)
    private String optionDescrption;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getOptionUrl() {
        return optionUrl;
    }

    public void setOptionUrl(String optionUrl) {
        this.optionUrl = optionUrl;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getOptionActiontype() {
        return optionActiontype;
    }

    public void setOptionActiontype(String optionActiontype) {
        this.optionActiontype = optionActiontype;
    }

    public String getOptionDescrption() {
        return optionDescrption;
    }

    public void setOptionDescrption(String optionDescrption) {
        this.optionDescrption = optionDescrption;
    }
}
