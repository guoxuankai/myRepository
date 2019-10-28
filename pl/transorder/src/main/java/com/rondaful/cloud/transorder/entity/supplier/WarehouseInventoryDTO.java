package com.rondaful.cloud.transorder.entity.supplier;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class WarehouseInventoryDTO implements Serializable {

    private String msg;

    private Map<String, List<WarehouseInventory>> data;

    private String success;

    private String errorCode;

    private static final long serialVersionUID = 1L;
}
