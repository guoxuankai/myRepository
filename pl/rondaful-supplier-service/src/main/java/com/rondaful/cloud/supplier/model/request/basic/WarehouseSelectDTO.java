package com.rondaful.cloud.supplier.model.request.basic;


import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/21
 * @Description:
 */
public class WarehouseSelectDTO implements Serializable {
    private static final long serialVersionUID = 6012367341594871060L;


    private Integer id;

    private String name;

    private List<WarehouseSelectDTO> childs;

    public WarehouseSelectDTO(){}

    public WarehouseSelectDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WarehouseSelectDTO> getChilds() {
        return childs;
    }

    public void setChilds(List<WarehouseSelectDTO> childs) {
        this.childs = childs;
    }
}
