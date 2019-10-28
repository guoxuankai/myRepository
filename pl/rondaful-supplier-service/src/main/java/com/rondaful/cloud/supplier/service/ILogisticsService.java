package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.*;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/10/16
 * @Description: 物流方式
 */
public interface ILogisticsService {

    /**
     * 初始话物流方式
     * @param firmId
     * @return
     */
    Integer init(Integer firmId);

    /**
     * 根据仓库id分页获取物流方式
     * @param dto
     * @return
     */
    PageDTO<LogisticsPageDTO> getsPage(QueryPageDTO dto);

    /**
     * 修改状态
     * @param id
     * @param status
     * @return
     */
    Integer updateStatus(Integer id,Integer status);

    /**
     * 根据id获取物流详情信息
     * @param id
     * @return
     */
    LogisticsDetailDTO get(Integer id,String languageType);

    /**
     * 修改平台物流映射
     * @param list
     * @return
     */
    Integer updateMap(List<LogisticsMapDTO> list);

    /**
     * 查询物流费
     * @param dto
     * @return
     */
    List<LogisticsSelectDTO> getSelect(QuerySelectDTO dto);

    /**
     * 订单运费试算
     * @param dto
     * @return
     */
    LogisticsCostVo orderLogistics(LogisticsCostVo dto);

    /**
     * 刊登时运费试算
     * @param dto
     * @return
     */
    LogisticsPublishDTO publishLogistics(QuerySelectDTO dto);

    /**
     * 获取仓库下所有的物流方式
     * @param warehouseId
     * @return
     */
    List<LogisticsSelectDTO> getByWarehouseId(Integer warehouseId,List<String> codes);

    /**
     * 获取平台物流信息
     * @param code
     * @param warehouseId
     * @param platform
     */
    LogisticsMapDTO getPlatLogisticsByCode(String code,Integer warehouseId,Integer platform);

}
