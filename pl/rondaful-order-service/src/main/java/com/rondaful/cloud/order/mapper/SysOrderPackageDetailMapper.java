package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SysOrderPackageDetailMapper extends BaseMapper<SysOrderPackageDetail> {
    /**
     * 根据订单包裹号查询包裹详情wujiachuang
     * @param orderTrackId
     * @return
     */
    List<SysOrderPackageDetail> queryOrderPackageDetails(@Param("orderTrackId") String orderTrackId);

    /**
     * 根据订单包裹号查询包裹详情lijiantao
     * @param orderTrackId
     * @return
     */
    List<SysOrderPackageDetail> queryOrderPackageDetailsContainAllSku(@Param("orderTrackId") String orderTrackId);

    /**
     * 批量插入
     * @param sysOrderPackageDetailDTOList {@link List<SysOrderPackageDetailDTO>}
     */
    void insertBatchSelective(@Param("list") List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList);

    /**
     * 根据包裹号批量删除_ljt
     *
     * @param collect
     * @return
     */
    int deleteBatchBySysOrderTrackId(List<String> collect);

    /**
     * 更新包裹详情的包裹订单号wujiachuang
     * @param orderTrackId
     * @param plTrackNumber
     */
    void updateOrderTrackId(@Param("orderTrackId") String orderTrackId, @Param("plTrackNumber") String plTrackNumber);

    /**
     * 根据订单包裹号集合查询包裹详情ljt
     * @param collect
     * @return
     */
    List<SysOrderPackageDetail> queryBatchOrderPackageDetails(List<String> collect);

    /**
     * 根据包裹号、sku修改售后状态wujiachuang
     * @param orderTrackId
     * @param sku
     * @param status
     */
    void updateOrderPackageItemStatus(@Param("orderTrackId") String orderTrackId, @Param("sku") String sku, @Param("status") byte status);

    /**
     * 更改包裹详情信息wujiachaung
     * @param item
     */
    void editPackageDetailInfo(SysOrderPackageDetail item);

}