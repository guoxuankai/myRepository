package com.rondaful.cloud.user.entity;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/9/6
 * @Description:
 */
@Document(collection = "html_show_property")
public class ShowProperty implements Serializable {
    private static final long serialVersionUID = -5120497575498804173L;

    @Field("id")
    @ApiModelProperty(value = "id", required = false)
    private Long id;

    @Field("platform_type")
    @ApiModelProperty(value = "platformType", required = false)
    private Integer platformType;

    @Field("user_id")
    @ApiModelProperty(value = "userId", required = false)
    private Integer userId;

    @Field("path")
    @ApiModelProperty(value = "path", required = false)
    private String path;

    @Field("show")
    @ApiModelProperty(value = "show", required = false)
    private List<String> show;

    @Field("hide")
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
