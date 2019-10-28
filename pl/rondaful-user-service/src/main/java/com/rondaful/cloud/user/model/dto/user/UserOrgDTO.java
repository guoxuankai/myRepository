package com.rondaful.cloud.user.model.dto.user;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/26
 * @Description:
 */
public class UserOrgDTO implements Serializable {
    private static final long serialVersionUID = -7856587495767918653L;

    /**
     * 绑定类型：
     * 0 供应商平台账号
     * 1 卖家平台账号
     * 2
     * 3
     * 4  店铺账号
     * 5  仓库code
     * 6
     * 7
     *
     *
     *
     */
    private Integer bindType;

    /**
     * 绑定标志类型
     */
    private List<String> bindCode;

    public UserOrgDTO(){}

    public UserOrgDTO(Integer bindType, List<String> bindCode) {
        this.bindType = bindType;
        this.bindCode = bindCode;
    }

    public Integer getBindType() {
        return bindType;
    }

    public void setBindType(Integer bindType) {
        this.bindType = bindType;
    }

    public List<String> getBindCode() {
        return bindCode;
    }

    public void setBindCode(List<String> bindCode) {
        this.bindCode = bindCode;
    }
}
