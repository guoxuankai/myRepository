package com.rondaful.cloud.supplier.vo;

import com.rondaful.cloud.supplier.dto.FreightTrialDTO;

public class FreightVo {

    private FreightTrialDTO list;

    private String search;

    public FreightTrialDTO getList() {
        return list;
    }

    public void setList(FreightTrialDTO list) {
        this.list = list;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
