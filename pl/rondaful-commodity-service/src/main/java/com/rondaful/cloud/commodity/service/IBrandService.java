package com.rondaful.cloud.commodity.service;

import com.rondaful.cloud.commodity.entity.Brand;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.service.BaseService;

import java.util.List;

public interface IBrandService extends BaseService<Brand> {
    Page selectBranchList(String page, String row,Brand brand);
    void auditNoticMeassage(Integer stat, Brand brand);
}
