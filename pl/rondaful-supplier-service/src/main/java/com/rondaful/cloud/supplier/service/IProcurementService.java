package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.procurement.*;

import java.util.List;


/**
 * @Author: xqq
 * @Date: 2019/6/20
 * @Description:
 */
public interface IProcurementService {

    /**
     * 修改子项的采购信息
     * @param dto
     * @return
     */
    Integer update(ProcurementDTO dto);


    /**
     * 分页查询采购单
     * @param dto
     * @return
     */
    PageDTO<ProcurementPageDTO> getsPage(QueryProcurementPageDTO dto);

    /**
     * 根据采购单id获取采购明细
     * @param procurementId
     * @return
     */
    ProcurementDetailDTO getsDetailList(Long procurementId, String languageType);

    /**
     *
     * @param procurementId
     * @param status
     * @param updateBy
     * @return
     */
    Integer updateStatus(Long procurementId,Integer status,String updateBy,String remake);


    /**
     * 添加采购单
     * @param dto
     * @return
     */
    Integer add(ProcurementDTO dto);

    /**
     * 批量添加采购单
     * @param list
     * @return
     */
    Integer addBatch(List<ProcurementDTO> list);



    /**
     * 新增采购建议
     * @param dto
     * @return
     */
    Integer addSuggest(SuggestDTO dto);

    /**
     * 修改采购建议状态
     * @param updateBy
     * @param status
     * @param suggestId
     * @return
     */
    Integer updateSuggestStatus(String updateBy,Integer status,Long suggestId);

    /**
     * 采购建议分页查询
     * @param dto
     * @return
     */
    PageDTO<SuggestPageDTO> getsSuggestPage(QuerySuggestPageDTO dto);

    /**
     * 入库采购商品
     * @param id
     * @param num
     * @return
     */
    Integer storage(Long id,Integer num);



}
