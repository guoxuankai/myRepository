package com.brandslink.cloud.finance.pojo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yangzefei
 * @Classname BaseSortVo
 * @Description 分页排序条件
 * @Date 2019/6/22 10:33
 */
@Data
public class BaseSortVo extends  BaseVO {
    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序方式(升序-asc,降序-desc)")
    private String sortType="desc";

    /**
     * 判断是否为有效的排序关键字
     * 防止恶意sql注入
     * @param defaultField 默认排序字段
     * @param t 排序字段所在的实体类
     * @return
     */
    public boolean isValidSort(String defaultField,Class t){
        if(StringUtils.isEmpty(sortField)){
            sortType="desc";
            sortField=defaultField;
        }else{
            if(!"desc".equals(sortType.toLowerCase())&&!"asc".equals(sortType.toLowerCase())){
                return false;
            }
            try{
                //验证字段是否合法
                t.getDeclaredField(sortField);
            }catch(Exception e){
                return false;
            }
        }
        return true;
    }
}
