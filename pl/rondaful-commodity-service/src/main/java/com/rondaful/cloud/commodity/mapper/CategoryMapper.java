package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.Category;
import com.rondaful.cloud.commodity.vo.ApiCategoryResponseVo;
import com.rondaful.cloud.common.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CategoryMapper extends BaseMapper<Category> {
    int deleteCategoryByIds (List list);
    
    List<Category> selectCategory1ByCategoryName(@Param("name")String name,@Param("status")Integer status);
    
    List<Category> selectCategory1ByCategoryName3(String name);
    List<Category> findFindInSet(Long id);
    List<Category> selectCategoryListByParentId(List list);
    List<Category> findCategoryList(Category category);
    
    List<Category> selectCategoryByName(@Param("categoryName")String categoryName,@Param("categoryLevel")Integer categoryLevel);
    
    void updateStatusByParentId(Map<String, Object> param);
    
    List<Category> selectHasProductCategory(Map<String, Object> map);
    
    List<ApiCategoryResponseVo> getForApi(Map<String, Object> param);
}