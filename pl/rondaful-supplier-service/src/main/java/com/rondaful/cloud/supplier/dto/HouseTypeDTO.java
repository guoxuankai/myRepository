package com.rondaful.cloud.supplier.dto;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/5/8
 * @Description:
 */
public class HouseTypeDTO {

    private String type;

    private List<HouseNameDTO> list;

    public HouseTypeDTO(){}

    public HouseTypeDTO(String type, List<HouseNameDTO> list) {
        this.type = type;
        this.list = list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<HouseNameDTO> getList() {
        return list;
    }

    public void setList(List<HouseNameDTO> list) {
        this.list = list;
    }
}
