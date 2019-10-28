package com.brandslink.cloud.user.dto.request;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 添加公告
 *
 * @ClassName AddOrUpdateAnnouncementListRequestDTO
 * @Author tianye
 * @Date 2019/8/19 16:33
 * @Version 1.0
 */
public class AddOrUpdateAnnouncementListRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @ApiModelProperty(value = "公告内容", required = true)
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
