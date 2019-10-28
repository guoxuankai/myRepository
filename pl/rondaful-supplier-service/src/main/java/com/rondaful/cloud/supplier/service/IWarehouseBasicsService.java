package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.basics.*;
import com.rondaful.cloud.supplier.model.request.basic.WarehouseSelectDTO;
import com.rondaful.cloud.supplier.model.request.basic.WarehouseServiceDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/11
 * @Description: 仓库基本属性信息
 */
public interface IWarehouseBasicsService {
    public static String SUPPLIER_WAREHOUSE_ID="supplier:warehouseId.v10.";

    /**
     * 新建仓库服务商
     * @param dto
     * @return
     */
    Integer add(WarehouseFirmDTO dto);

    /**
     * 拉取未同步后的仓库服务商
     * @return
     */
    Integer update();

    /**
     * 根据服务商id删除仓库
     * @param firmId
     * @return
     */
    Integer del(Integer firmId);

    /**
     * 根据供应商id获取列表
     * @param supplierIds
     * @return
     */
    List<WarehouseLogisTreeDTO> getTree(List<Integer> supplierIds,String languageType);

    /**
     * 改变仓库状态
     * @param warehouseIds
     * @param status
     * @return
     */
    Integer updateStatus(List<Integer> warehouseIds,Integer status);

    /**
     * 获取有效的仓库
     * @return
     */
    List<WarehouseInitDTO> getsInit();

    /**
     * 根据仓库id获取仓库信息
     * @param warehouseId
     * @return
     */
    WarehouseDTO getByWarehouseId(Integer warehouseId);

    /**
     * h获取仓库服务商名称
     * @return
     */
    List<WarehouseServiceDTO> getsServiceName(Integer type);

    /**
     *
     * @return
     */
    List<WarehouseSelectDTO> getSelect(List<Integer>  warehouseIds, List<Integer> supplierId,String languageType,Integer status);

    /**
     * 根据仓库服务商编码获取仓库列表
     * @param serviceCode
     * @return
     */
    List<WarehouseSelectDTO> getSelectByServiceCode(String serviceCode,Integer userId);

    /**
     * 根据老版仓库code换取新版id
     * @param warehouseCode
     * @return
     */
    @Deprecated
    Integer codeToId(String warehouseCode);

    /**
     * 根据新版id换取老版仓库code
     * @param warehouseId
     * @return
     */
    @Deprecated
    String idToCode(Integer warehouseId);

    /**
     * 根据账号列表id获取仓库
     * @param firmId
     * @return
     */
    WarehouseInitDTO getByFirmId(Integer firmId);

    /**
     * 根据类型获取仓库类型的授权信息及列表 1  共有仓  2  私有仓
     * @param type
     * @return
     */
    List<InitWarehouseDTO> getAuth(Integer type);

    /**
     * 根据根据 供应商账号获取仓库服务上账号列表
     * @param supplierIds
     * @return
     */
    List<WarehouseSelectDTO> getsFirm(List<Integer> supplierIds);

    /**
     * 获取服务商下所有的仓库id列表
     * @param serviceCode
     * @return
     */
    List<Integer> getsByType(String serviceCode);

    /**
     * 获取谷仓公司名及账户
     * @param appKey
     * @param appToken
     * @return
     */
    AccountDTO getAccount(String appKey,String appToken);

    /**
     * 根据账号名查询
     * @param name
     * @return
     */
    Integer getFirmByName(String name);

    /**
     * 根据appToken及warehouseCode获取仓库信息
     * @param appToken
     * @param warehouseCode
     * @return
     */
    WarehouseDTO getByAppTokenAndCode(String appToken,String warehouseCode);

    /**
     * 根据appKey及warehouseCode获取仓库信息
     * @param appKey
     * @param warehouseCode
     * @return
     */
    WarehouseDTO getByAppKeyAndCode(String appKey,String warehouseCode);

    /**
     * 根据谷仓code获取仓库
     * @param warehouseCode
     * @return
     */
    WarehouseDTO getByAppTokenAndCode(String warehouseCode);

    /**
     * 根据供应链公司获取绑定仓库服务商总数
     * @param supplyId
     * @return
     */
    Integer getBindService(Integer supplyId);

    /**
     * 获取供应商主账号关联所有的仓库id
     * @param supplierId
     * @return
     */
    List<Integer> getsIdBySupplierId(Integer supplierId);

    /**
     * 获取仓库列表（含国家）
     * @param supplierIds
     * @param warehouseIds
     * @return
     */
    List<WarehouseCountryDTO> getsWarehouseList(List<Integer> supplierIds,List<Integer> warehouseIds,String languageType);

    /**
     * 分页查询仓库列表
     * @param dto
     * @return
     */
    PageDTO<WarehousePageDTO> getsPage(WarehouseQueryDTO dto);

    /**
     * 根据仓库id获取仓库地址
     * @param warehouseId
     * @return
     */
    AddressDTO getAddress(Integer warehouseId,String languageType);

    /**
     * 修改地址
     * @param dto
     * @return
     */
    Integer updateAddress(AddressDTO dto);

    /**
     * 获取自定义账号
     * @return
     */
    List<String> getsAccount(List<Integer> supplierIds);

    /**
     * 获取仓库名
     * @param supplierIds
     * @return
     */
    List<KeyValueDTO> getsWarehouseName(List<Integer> supplierIds,String languageType);

    /**
     * 根据供应商id获取列表
     * 页面搜索条件框 特殊情况下使用
     * @param supplierId
     * @return
     */
    List<WarehouseSelectDTO> getSelectList(Integer supplierId,String languageType);


    /**
     * 根据erp仓库code及供应商账号获取仓库id
    * @param warehouseCode
     * @param supplierId
     * @return
     */
    Integer getCodeAndId(String warehouseCode,Integer supplierId);




}
