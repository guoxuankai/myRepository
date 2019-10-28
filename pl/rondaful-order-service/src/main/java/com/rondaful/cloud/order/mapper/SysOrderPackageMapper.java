package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.WarehouseShipExceptionVo;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysOrderPackageMapper extends BaseMapper<SysOrderPackage> {

    /**
     * 根据
     * @param offset 当前位置
     * @param size 返回行数
     * @return
     */
    List<SysOrderPackage> queryOrderPackageByLimit(@Param("offset") Integer offset,@Param("size") Integer size);


    /**
     * 根据订单ID查询要展示的包裹  wujiachuang
     *
     * @param sysOrderId
     * @return
     */
    List<SysOrderPackage> queryOrderPackageByOrderId(@Param("sysOrderId") String sysOrderId);

    /**
     * 根据订单ID查询包裹  wujiachuang
     *
     * @param sysOrderId
     * @return
     */
    List<SysOrderPackage> queryOrderPackage(@Param("sysOrderId") String sysOrderId);

    /**
     * 根据订单ID查询不要展示的包裹  wujiachuang
     *
     * @param sysOrderId
     * @return
     */
    List<SysOrderPackage> queryOrderNoShowPackageByOrderId(@Param("sysOrderId") String sysOrderId);

    /**
     * 根据包裹号查询包裹wujiachuang
     *
     * @param orderTrackId
     * @return
     */
    SysOrderPackage queryOrderPackageByOrderTrackId(@Param("orderTrackId") String orderTrackId);

    /**
     * 更改订单包裹物流信息 wujiachuang
     *
     * @param orderPackage
     */
    void editPackageInfo(SysOrderPackage orderPackage);

    /**
     * 根据订单ID更改订单包裹状态wujiachuang
     *
     * @param sysOrderId
     * @param packageStatus
     */
    void updatePackageStatus(@Param("sysOrderId") String sysOrderId, @Param("packageStatus") String packageStatus);

    /**
     * 根据包裹ID更改订单包裹状态wujiachuang
     *
     * @param orderTrackId
     * @param packageStatus
     */
    void updatePackageStatusByOrderTrackId(@Param("orderTrackId") String orderTrackId, @Param("packageStatus") String packageStatus);

    /**
     * 功能描述 根据系统订单查询包裹信息
     *
     * @param sysOrderId
     * @return [SysOrderPackage]
     * @date 2019-07-22
     * @author lz
     */
    List<SysOrderPackage> selectPackageByOderId(String sysOrderId);

    /**
     * 批量插入
     *
     * @param sysOrderPackageDTOList {@link List<SysOrderPackageDTO>}
     */
    void insertBatchSelective(@Param("list") List<SysOrderPackageDTO> sysOrderPackageDTOList);

    /**
     * 根据订单ID查找所有包裹的发货异常信息wujiachuang
     *
     * @param orderId
     * @return
     */
    List<WarehouseShipExceptionVo> queryWarehouseShipExceptionByOrderId(String orderId);
    /**
     * 根据包裹ID查找所有包裹的发货异常信息wujiachuang
     *
     * @param orderTrackId
     * @return
     */
    List<WarehouseShipExceptionVo> queryWarehouseShipExceptionByOrderTrackId(String orderTrackId);

    /**
     * 根据订单ID 根据订单ID更改包裹状态并清空包裹发货异常信息、跟踪单号、物流商单号wujiachuang
     *
     * @param sysOrderId
     */
    void updatePackageInfoByIntercept(String sysOrderId);

    /**
     * 根据包裹ID更改包裹状态并清空包裹发货异常信息、跟踪单号、物流商单号wujiachuang
     *
     * @param orderTrackId
     */
    void updatePackageInfoByOrderTrackId(String orderTrackId);

    /**
     * 撤销拆包，根据订单ID删除拆分后的包裹、包裹详情
     *
     * @param sysOrderId
     */
    void deletePackageBySplitSysOrderId(@Param("sysOrderId") String sysOrderId);

    /**
     * 更新包裹号wujiachuang
     *
     * @param orderTrackId
     */
    void updateOrderTrackId(@Param("orderTrackId") String orderTrackId, @Param("trackId") String trackId);

    /**
     * 根据包裹号更改操作的包裹号wujiachuang
     *
     * @param orderTrackId
     * @param plTrackNumber
     */
    void updateOperateTrackId(@Param("orderTrackId") String orderTrackId, @Param("plTrackNumber") String plTrackNumber);

    /**
     * 撤销合包，根据包裹号删除拆分后的包裹、包裹详情
     *
     * @param orderTrackId
     */
    void deletePackageByOrderTrackId(@Param("orderTrackId") String orderTrackId);

    /**
     * 根据订单ID集合查询所有的包裹_ljt
     *
     * @param collect
     * @return
     */
    List<SysOrderPackage> queryBatchOrderPackageByOrderId(List<String> collect);

    /**
     * 根据包裹号添加包裹异常信息wujiachuang
     *
     * @param orderTrackId
     */
    void updateException(String orderTrackId);

    void updateByOrderTrackIdSelective(SysOrderPackage sysOrderPackage);

    /**
     * 根据包裹号查询包裹wujiachuang
     *
     * @param orderTrackId
     * @return
     */
    SysOrderPackage queryOrderPackageByPk(@Param("orderTrackId") String orderTrackId);

    /**
     * 根据仓库ID列表查询 待发货 的包裹
     *
     * @param warehouseIds 仓库ID列表
     * @return {@link List<SysOrderPackage>}
     */
    List<SysOrderPackage> getPackageByWarehouseId(@Param("warehouseIds") List<Integer> warehouseIds);

    /**
     *  根据包裹号添加包裹异常信息wujiachuang
     * @param orderTrackId
     */
    void updatePackageErrorInfo(String orderTrackId);

}