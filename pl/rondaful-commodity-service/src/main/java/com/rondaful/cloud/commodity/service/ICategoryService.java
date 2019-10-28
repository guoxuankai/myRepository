package com.rondaful.cloud.commodity.service;

import com.rondaful.cloud.commodity.entity.Category;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.service.BaseService;

import java.util.List;

public interface ICategoryService extends BaseService<Category> {
    int deleteCategoryByIds(List list);
    void deleteCategorys(List<String> ids);
    void addCategorys(Category category);
    int updateCategorys(Category category);
    void bindCategorySet(Category category) throws NoSuchFieldException, IllegalAccessException;
    
    Page findList(String key,Integer status);
}
