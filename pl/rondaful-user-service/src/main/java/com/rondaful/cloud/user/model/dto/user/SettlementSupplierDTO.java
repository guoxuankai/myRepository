package com.rondaful.cloud.user.model.dto.user;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/8/23
 * @Description:
 */
public class SettlementSupplierDTO implements Serializable {
    private static final long serialVersionUID = 7040557780320180461L;

    private String type;

    private String stageList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStageList() {
        return stageList;
    }

    public void setStageList(String stageList) {
        this.stageList = stageList;
    }
}
