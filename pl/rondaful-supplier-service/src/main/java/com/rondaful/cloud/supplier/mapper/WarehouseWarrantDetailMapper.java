package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.WarehouseWarrantDetail;

import java.util.List;
import java.util.Map;

public interface WarehouseWarrantDetailMapper extends BaseMapper<WarehouseWarrantDetail> {

    /**
     * 动态批量添加入库单商品明细表，以map形式传参
     * k:"listColumn" 需要添加商品明细表的哪些字段  （可以直接取"listData"的第一个对象，需要添加字段的值不能为空）
     * k:"listData" 具体添加的数据集合 list类型
     * 例：
     * List<WarehouseWarrantDetail> detailList = new ArrayList<>();
     * WarehouseWarrantDetail detail = new WarehouseWarrantDetail();
     * detail.setId(200L);
     * detail.setBoxNo(1);
     * detail.setParentSequenceNumber("1245679465");
     * detail.setProductSku("145678931");
     * detail.setOverseasSellableQty(10);
     * detail.setOverseasUnsellableQty(20);
     * detail.setOverseasShelvesCount(30);
     * WarehouseWarrantDetail detail2 = new WarehouseWarrantDetail();
     * detail2.setId(201L);
     * detail2.setBoxNo(1);
     * detail2.setParentSequenceNumber("1245679465");
     * detail2.setProductSku("145678931");
     * detail2.setOverseasSellableQty(10);
     * detail2.setOverseasUnsellableQty(20);
     * detail2.setOverseasShelvesCount(30);
     * detailList.add(detail);
     * detailList.add(detail2);
     * Map<String,Object> map = new HashMap<>();
     * map.put("listColumn",detailList.get(0));
     * map.put("listData",detailList);
     * warrantDetailMapper.insertList(map);
     *
     * @param map
     */
    void insertList(Map<String, Object> map);

    /**
     * 根据入库单表序号查询入库单商品明细表
     *
     * @param parentSequenceNumber
     * @return
     */
    List<WarehouseWarrantDetail> selectByParentSequenceNumber(String parentSequenceNumber);

    /**
     * 根据入库单表序号批量删除入库单商品明细表数据
     *
     * @param sequenceNumber
     */
    void deleteByParentSequenceNumber(String sequenceNumber);
}