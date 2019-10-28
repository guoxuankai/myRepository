package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.Amazon.AmazonDelivery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AmazonDeliveryMapper extends BaseMapper<AmazonDelivery> {
    /*
    * 查询需要上传的数据集合
    * */
    List<AmazonDelivery> queryUploadData();

    void updateUploadStatus(Integer id);
}