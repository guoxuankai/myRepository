package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.supplier.dto.GranarySmCodeResponse;
import com.rondaful.cloud.supplier.dto.GranaryTransferWarehouseResponse;
import com.rondaful.cloud.supplier.entity.VatDetailInfo;
import com.rondaful.cloud.supplier.entity.WarehouseWarrantRequest;
import com.rondaful.cloud.supplier.entity.WarehouseWarrantResponse;
import com.rondaful.cloud.supplier.vo.ModifyWarehouseWarrantVo;
import com.rondaful.cloud.supplier.vo.UserInfoVO;
import com.rondaful.cloud.supplier.vo.WarehouseWarrantDetailResponseVo;
import com.rondaful.cloud.supplier.vo.WarehouseWarrantVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 入库单信息表Service
 *
 * @ClassName IWarehouseWarrantService
 * @Author tianye
 * @Date 2019/4/25 18:55
 * @Version 1.0
 */
public interface IWarehouseWarrantService {

    /**
     * 模糊查询入库单信息
     *
     * @return
     */
    Page<WarehouseWarrantResponse> getWarehouseWarrantListBySelective(WarehouseWarrantRequest request);

    /**
     * 根据primaryKey查询入库单明细
     *
     * @param primaryKey
     * @return
     */
    WarehouseWarrantDetailResponseVo getWarehouseWarrantDetailByPrimaryKey(Long primaryKey);

    /**
     * 根据commitFlag判断提交入库单or保存草稿
     *
     * @param vo
     * @param userInfo
     */
    void insertWarrant(WarehouseWarrantVo vo, UserInfoVO userInfo);

    /**
     * 根据primaryKey提交入库单
     *
     * @param primaryKey
     */
    void commitWarrant(Long primaryKey);

    /**
     * 获取中转仓库列表信息
     *
     * @param warehouseCode
     * @return
     */
    List<GranaryTransferWarehouseResponse.TransferWarehouseDeatil> getTransferWarehouse(String warehouseCode);

    /**
     * 获取中转服务方式
     *
     * @param warehouseCode
     * @return
     */
    GranarySmCodeResponse.SmCodeData getSmCode(String warehouseCode);

    /**
     * 编辑入库单
     *
     * @param vo
     * @param userInfo
     */
    void updateWarrant(ModifyWarehouseWarrantVo vo, UserInfoVO userInfo);

    /**
     * 删除入库单
     *
     * @param primaryKey
     */
    void deleteWarehouseWarrantDetail(Long primaryKey);

    /**
     * 编辑备注
     *
     * @param primaryKey
     * @param comment
     */
    void editorsComment(Long primaryKey, String comment);

    /**
     * 取消已经提交谷仓的入库单
     *
     * @param primaryKey
     */
    void deleteByDiscardWarehouseWarrant(Long primaryKey);

    /**
     * 根据目的仓代码获取进出口商代码
     *
     * @return
     */
    Map<String, List<VatDetailInfo>> getVatList(String warehouseCode);

    /**
     * 打印箱唛
     *
     * @return
     */
    void printGcReceivingBox(Long primaryKey, HttpServletResponse response, String receivingCode, String printSize, String printType, String[] arr);

    /**
     * 打印SKU标签
     *
     * @param primaryKey
     * @param response
     * @param printSize
     * @param printCode
     * @param arr
     */
    void printSku(Long primaryKey, HttpServletResponse response, String printSize, String printCode, String[] arr);

    /**
     * 定时更新入库单状态
     */
    void syncWarrantStatus();
}
