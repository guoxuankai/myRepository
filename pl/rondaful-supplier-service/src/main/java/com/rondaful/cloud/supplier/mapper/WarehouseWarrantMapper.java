package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.WarehouseWarrant;
import com.rondaful.cloud.supplier.entity.WarehouseWarrantRequest;
import com.rondaful.cloud.supplier.entity.WarehouseWarrantResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 入库单 Mapper
 *
 * @author tianye
 * @date 2019-04-26 13:15:49
 */
public interface WarehouseWarrantMapper extends BaseMapper<WarehouseWarrant> {

    /**
     * 根据传入字段模糊查询入库单列表
     *
     * @param request
     * @return
     */
    List<WarehouseWarrantResponse> selectWarehouseWarrantListBySelective(/*@Param("request")*/ WarehouseWarrantRequest request);

    /**
     * 根据入库单号查询入库单明细
     *
     * @param receivingCode
     * @return
     */
    WarehouseWarrant selectWarehouseWarrantDetailByReceivingCode(String receivingCode);

    /**
     * 根据入库单序号更新入库单信息
     *
     * @param updateRequest
     */
    void updateBySequenceNumber(WarehouseWarrant updateRequest);

    /**
     * 根据入库单号批量更新入库单状态
     *
     * @param map
     */
    void updateReceivingStatusByReceivingCodeList(@Param("map") Map<String, Integer> map);

    /**
     * 根据主键查询部分字段（除可编辑之外所有字段）
     *
     * @param primaryKey
     * @return
     */
    WarehouseWarrant selectValidColumnByPrimaryKey(Long primaryKey);

    /**
     * 根据主键查询入库单序号
     *
     * @param primaryKey
     * @return
     */
    String selectValidColumnByPrimaryKeyOnSequenceNumber(Long primaryKey);

    /**
     * 根据主键编辑入库单备注
     *
     * @param primaryKey
     * @param comment
     */
    void updateCommentByPrimaryKey(@Param("primaryKey") Long primaryKey, @Param("comment") String comment);

    /**
     * 根据入库单号更新入库单状态
     *
     * @param code
     * @param status
     */
    void updateWarrantStatusByReceivingCode(@Param("code") String code, @Param("status") Byte status);

    /**
     * 获取 审核中 和 待收货的所有入库单号
     *
     * @return
     */
    List<String> selectReceivingCodeByReceivingStatus();
}