package com.rondaful.cloud.user.model.dto.logger;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/9/6
 * @Description:
 */
public class ShowPropertyDTO implements Serializable {
    private static final long serialVersionUID = 6233044761607785846L;

    @ApiModelProperty(value = "id", required = false)
    private Long id;

    @ApiModelProperty(value = "platformType", required = false)
    private Integer platformType;

    @ApiModelProperty(value = "userId", required = false)
    private Integer userId;

    @ApiModelProperty(value = "path", required = false)
    private String path;

    @ApiModelProperty(value = "show", required = false)
    private List<String> show;

    @ApiModelProperty(value = "hide", required = false)
    private List<String> hide;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getShow() {
        return show;
    }

    public void setShow(List<String> show) {
        this.show = show;
    }

    public List<String> getHide() {
        return hide;
    }

    public void setHide(List<String> hide) {
        this.hide = hide;
    }
}
