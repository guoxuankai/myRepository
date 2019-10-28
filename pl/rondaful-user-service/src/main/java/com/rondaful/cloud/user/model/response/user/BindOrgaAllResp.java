package com.rondaful.cloud.user.model.response.user;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/5/4
 * @Description:
 */
public class BindOrgaAllResp implements Serializable {
    private static final long serialVersionUID = -1400328571236513695L;

    private Integer type;

    private List<BindOrgResp> list;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<BindOrgResp> getList() {
        return list;
    }

    public void setList(List<BindOrgResp> list) {
        this.list = list;
    }
}
