package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.storage.*;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description: 入库单相关记录
 */
public interface IStorageRecordService {

    public static String STORAGE_RECORD_SM_CODE="supplier:storage.record.cm.code.v1.";
    public static String STORAGE_TRANSFER_WAREHOUSE_CODE="supplier:storage.record.getTransferWarehouse.v1.";
    public static String STORAGE_TRANSFER_WAREHOUSE_CODE_MAP="supplier:storage.record.getTransferWarehouse.map.v1.";
    public static String STORAGE_TRANSFER_VAT_LIST="supplier:storage.record.watlist.v2.";

    /**
     * 编辑入库单
     * @param dto
     * @return
     */
    Integer update(StorageRecordDTO dto);

    /**
     * 新建入库单
     * @param dto
     * @return
     */
    String add(StorageRecordDTO dto);

    /**
     * 分页获取入库单
     * @param dto
     * @return
     */
    PageDTO<StoragePageDTO> getsPage(StorageQueryPageDTO dto);

    /**
     * 根据id获取入库单详细信息
     * @param id
     * @return
     */
    StorageRecordDTO getById(Long id);

    /**
     * 获取中转服务方式
     * @param type
     * @param warehouseId
     * @return
     */
    List<SmCodeDTO> getsSmCode(Integer type,Integer warehouseId);

    /**
     * 获取中转仓库
     * @param warehouseId
     * @return
     */
    List<SmCodeDTO> getTransferWarehouse(Integer warehouseId);

    /**
     * 根据仓库id获取增值税号
     * @param warehouseId
     * @return
     */
    @Deprecated
    List<VatListDTO> getsVat(Integer warehouseId);

    /**
     * 获取进出口商编码
     * @param warehouseId
     * @param type
     * @return
     */
    List<KeyValueDTO> getCompany(Integer warehouseId, Integer type);

    /**
     *
     * @param id
     * @param desc
     * @return
     */
    Integer updateDesc(Long id,String desc,String updateBy);

    /**
     * 删除入库单id
     * @param id
     * @return
     */
    Integer del(Long id);

    /**
     * 审核入库单
     * @param id
     * @return
     */
    Integer audit(Long id,String updateBy,String receivingShippingType,String trackingNumber);


    /**
     * 打印箱唛
     * @param id
     * @param printSize
     * @param printType
     * @param boxArr
     * @return
     */
    BoxDTO printBox(Long id,Integer printSize,Integer printType,List<String> boxArr);

    /**
     * 打印sku
     * @param id
     * @param printSize
     * @param printCode
     * @param skuArr
     * @return
     */
    BoxDTO printSku(Long id,Integer printSize,Integer printCode,List<String> skuArr);

    /**
     * 同步审核中和待收货的状态
     */
    void syncStatus();

}
