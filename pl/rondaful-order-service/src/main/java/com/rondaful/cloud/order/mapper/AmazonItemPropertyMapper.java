package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.Amazon.AmazonItemProperty;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AmazonItemPropertyMapper extends BaseMapper<AmazonItemProperty> {
    /*批量插入*/
    int insertBulk(List<AmazonItemProperty> list);
}