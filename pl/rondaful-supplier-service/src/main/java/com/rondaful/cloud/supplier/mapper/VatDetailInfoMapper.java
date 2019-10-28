package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.VatDetailInfo;

import java.util.List;

public interface VatDetailInfoMapper extends BaseMapper<VatDetailInfo> {

    /**
     * 根据进出口商审核状态查询进出口商列表
     *
     * @param cvStatus
     * @return
     */
    List<VatDetailInfo> selectBycvStatus(String cvStatus);

    /**
     * 批量添加进出口商明细
     *
     * @param list
     */
    void insertList(List<VatDetailInfo> list);

    /**
     * 查询进出口商信息表数量
     *
     * @return
     */
    int selectCount();

    /**
     * 清除表数据
     */
    void deleteFrom();
}