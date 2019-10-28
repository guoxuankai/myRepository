package com.rondaful.cloud.supplier.model.dto.basics;

import com.rondaful.cloud.supplier.model.request.basic.WarehouseSelectDTO;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/10/23
 * @Description:
 */
public class WarehouseLogisTreeDTO implements Serializable {
    private static final long serialVersionUID = 6746145206561293760L;

    private String type;

    List<WarehouseSelectDTO> list;

    public WarehouseLogisTreeDTO(String type, List<WarehouseSelectDTO> list) {
        this.type = type;
        this.list = list;
    }

    public WarehouseLogisTreeDTO() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WarehouseSelectDTO> getList() {
        return list;
    }

    public void setList(List<WarehouseSelectDTO> list) {
        this.list = list;
    }
}
