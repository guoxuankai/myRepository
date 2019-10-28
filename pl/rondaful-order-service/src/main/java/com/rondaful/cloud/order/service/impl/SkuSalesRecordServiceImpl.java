package com.rondaful.cloud.order.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.entity.SkuSalesRecord;
import com.rondaful.cloud.order.entity.supplier.PageDTO;
import com.rondaful.cloud.order.entity.supplier.SkuSalesRecordExportCmsDTO;
import com.rondaful.cloud.order.entity.supplier.SkuSalesRecordExportDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.enums.WarehouseFirmEnum;
import com.rondaful.cloud.order.mapper.SkuSalesRecordMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.model.dto.remoteUser.GetSupplyChainByUserIdDTO;
import com.rondaful.cloud.order.service.ISkuSalesRecordService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.utils.GetLoginInfo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class SkuSalesRecordServiceImpl implements ISkuSalesRecordService {

    private final static Logger log = LoggerFactory.getLogger(SkuSalesRecordServiceImpl.class);

    @Autowired
    private SkuSalesRecordMapper skuSalesRecordMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    GetLoginInfo getLoginInfo;

    @Autowired
    ISystemOrderCommonService systemOrderCommonService;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    /**
     * 批量插入sku销售记录
     *
     * @param skuSalesRecordList
     */
    @Override
    public Integer insertBatchSkuSalesRecord(List<SkuSalesRecord> skuSalesRecordList) {

        if (skuSalesRecordMapper.insertBatchSkuSalesRecord(skuSalesRecordList) > 0) {
            skuSalesRecordList.forEach(skuSalesRecord -> {
                        this.updateCache(skuSalesRecord);
                    }
            );

        }
        return 1;
    }

    @Override
    public Page<SkuSalesRecord> page(SkuSalesRecord skuSalesRecord) {
        setPrivParams(skuSalesRecord);
        List<SkuSalesRecord> skuSalesRecordList = skuSalesRecordMapper.page(skuSalesRecord);
        for (SkuSalesRecord skuRd : skuSalesRecordList) {
            skuRd.setDeliveryWarehouseName(Utils.translation(skuRd.getDeliveryWarehouseName()));
            this.getBusObject(skuRd);
        }

        PageInfo<SkuSalesRecord> pageInfo = new PageInfo(skuSalesRecordList);
        return new Page(pageInfo);
    }

    /**
     * 远程服务获取业务对象
     *
     * @param skuRd
     */
    private void getBusObject(SkuSalesRecord skuRd) {
        if (skuRd.getSupplierId() != null){
            skuRd.setSupplierName(systemOrderCommonService.getSupplyChinByUserId(skuRd.getSupplierId(), 0).getLoginName());
        }
        GetSupplyChainByUserIdDTO userDTO = null;
        if (skuRd.getSellerId() != null){
            userDTO = systemOrderCommonService.getSupplyChinByUserId(skuRd.getSellerId(), 1);
        }

        if (userDTO != null){
            skuRd.setSellerName(userDTO.getLoginName());
        }
//        Integer deliveryWarehouseId = skuRd.getDeliveryWarehouseId();
//        String firmCode = null;
//        if (deliveryWarehouseId != null && !String.valueOf(deliveryWarehouseId).equals("-1")) {
//            WarehouseDTO warehouseInfo = systemOrderCommonService.getWarehouseInfo(String.valueOf(deliveryWarehouseId));
//            if (warehouseInfo != null) {
//                firmCode = warehouseInfo.getFirmCode();
//            }
//            skuRd.setWserviceName(WarehouseFirmEnum.getByCode(firmCode));
//        }
        //设置物流商名称
        SysOrderPackage sysOrderPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(skuRd.getOrderTrackId());
        if (null != sysOrderPackage){
            skuRd.setWserviceName(sysOrderPackage.getShippingCarrierUsed());
        }
    }

    /**
     * 设置数据权限参数
     *
     * @param skuSalesRecord
     */
    private void setPrivParams(SkuSalesRecord skuSalesRecord) {
        if (StringUtils.isNotBlank(skuSalesRecord.getWserviceName())) {
            String firmCode = WarehouseFirmEnum.getByName(skuSalesRecord.getWserviceName());
            skuSalesRecord.setWarehouseIdList(systemOrderCommonService.getsWarehouseIdList(firmCode));
        }
        if (StringUtils.isNotBlank(skuSalesRecord.getSupplierName())) {
            skuSalesRecord.setSupplierId(systemOrderCommonService.getSupplyChinByUserIdOrUsername(null, skuSalesRecord.getSupplierName(), 0).getTopUserId());
        }
        if (StringUtils.isNotBlank(skuSalesRecord.getSellerName())) {
            skuSalesRecord.setSellerId(systemOrderCommonService.getSupplyChinByUserIdOrUsername(null, skuSalesRecord.getSellerName(), 1).getTopUserId());
        }
        skuSalesRecord.setTopFlag(getLoginInfo.getUserInfo().getTopFlag());
        if (getLoginInfo.getUserInfo().getPlatformType() == 0) {
            skuSalesRecord.setSupplierId(getLoginInfo.getUserInfo().getTopUserId());
            if (CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getwIds())) {
                skuSalesRecord.setwIds(getLoginInfo.getUserInfo().getwIds());
            }
        }
        if (getLoginInfo.getUserInfo().getPlatformType() == 2) {
            if (CollectionUtils.isNotEmpty(getLoginInfo.getUserInfo().getSuppliers())) {
                skuSalesRecord.setSuppliers(getLoginInfo.getUserInfo().getSuppliers());
            }

        }
    }


    /**
     * 导出sku销售记录
     *
     * @param skuSalesRecord
     * @param response
     */
    @Override
    public void exportSkuSalesRecordExcel(SkuSalesRecord skuSalesRecord, HttpServletResponse response) {
        log.info("导出sku销售记录:dto={}", JSONObject.toJSON(skuSalesRecord));
        int currPage = 1;
        int pageSize = 2000;
        Workbook workbook = getPageDTO(skuSalesRecord, currPage, pageSize);
        ExcelExportUtil.closeExportBigExcel();
        // 写出数据输出流到页面
        try {
            String fileName = "sku销售记录导出";
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            log.error("sku销售记录{}", e);
            e.printStackTrace();
        }


    }

    private Workbook getPageDTO(SkuSalesRecord skuSalesRecord, int currPage, int pageSize) {
        PageDTO<SkuSalesRecordExportDTO> result = new PageDTO<SkuSalesRecordExportDTO>();
        PageDTO<SkuSalesRecordExportCmsDTO> resultCms = new PageDTO<SkuSalesRecordExportCmsDTO>();
        List<SkuSalesRecordExportDTO> dataList = new ArrayList<>();
        List<SkuSalesRecordExportCmsDTO> dataCmsList = new ArrayList<>();
        setPrivParams(skuSalesRecord);
        List<SkuSalesRecord> skuRdList = skuSalesRecordMapper.page(skuSalesRecord);
        PageInfo<SkuSalesRecord> pageInfo = new PageInfo<>(skuRdList);
        result.setTotalCount((int) pageInfo.getTotal());
        result.setCurrentPage(currPage);
        log.info("当前页{}", currPage);
        if (CollectionUtils.isNotEmpty(pageInfo.getList())) {
            //供应商平台
            if (getLoginInfo.getUserInfo().getPlatformType() == 0) {
                for (SkuSalesRecord skuRd : pageInfo.getList()) {
                    SkuSalesRecordExportDTO exportDTO = new SkuSalesRecordExportDTO();
                    BeanUtils.copyProperties(skuRd, exportDTO);
                    if (skuRd.getFreeFreight() == 1) {
                        exportDTO.setTotalFreight(skuRd.getSupplierShipFee());
                    } else {
                        exportDTO.setTotalFreight(new BigDecimal(0));
                    }
                    exportDTO.setOddNumbers(skuRd.getSysOrderId() + "|" + skuRd.getOrderTrackId());
                    dataList.add(exportDTO);
                }
            }
            //管理后台
            if (getLoginInfo.getUserInfo().getPlatformType() == 2) {
                for (SkuSalesRecord skuRd : pageInfo.getList()) {
                    SkuSalesRecordExportCmsDTO exportCmsDTO = new SkuSalesRecordExportCmsDTO();
                    this.getBusObject(skuRd);
                    BeanUtils.copyProperties(skuRd, exportCmsDTO);
                    exportCmsDTO.setOddNumbers(skuRd.getSysOrderId() + "&" + skuRd.getOrderTrackId());
                    exportCmsDTO.setBusObject(skuRd.getSellerName() + "|" + skuRd.getSupplierName() + "|" + skuRd.getWserviceName());
                    String supplierShip = skuRd.getSupplierShipFee().toString();
                    String sellerShip = skuRd.getSellerShipFee().toString();
                    String logisticShip = skuRd.getLogisticCompanyShipFee().toString();
                    exportCmsDTO.setTotalFreight(sellerShip + "|" + supplierShip + "|" + logisticShip);
                    String supplierShipPer = skuRd.getSupplierSkuPerShipFee().toString();
                    String sellerShipPer = skuRd.getSellerSkuPerShipFee().toString();
                    String logisticShippPer = skuRd.getLogisticCompanySkuPerShipFee().toString();
                    exportCmsDTO.setFreightUnitPrice(sellerShipPer + "|" + supplierShipPer + "|" + logisticShippPer);
                    dataCmsList.add(exportCmsDTO);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(dataList)) {
            result.setList(dataList);
        }
        if (CollectionUtils.isNotEmpty(dataCmsList)) {
            resultCms.setList(dataCmsList);
        }

        Workbook workbook = null;
        if (CollectionUtils.isNotEmpty(result.getList())) {
            workbook = ExcelExportUtil.exportBigExcel(new ExportParams(null, "Sheet1", ExcelType.HSSF), SkuSalesRecordExportDTO.class, result.getList());
        }
        if (CollectionUtils.isNotEmpty(resultCms.getList())) {
            workbook = ExcelExportUtil.exportBigExcel(new ExportParams(null, "Sheet1", ExcelType.HSSF), SkuSalesRecordExportCmsDTO.class, resultCms.getList());
        }
        //int pageCount = (result.getTotalCount() + pageSize - 1) / pageSize; //以pageSize条分页的总页数
        if (currPage * pageSize < result.getTotalCount()) {
            getPageDTO(skuSalesRecord, ++currPage, pageSize);
        }
        return workbook;
    }

    /**
     * sku销售记录统计
     *
     * @param skuSalesRecord
     * @return
     */
    @Override
    public SkuSalesRecord statisSkuSales(SkuSalesRecord skuSalesRecord) {
        setPrivParams(skuSalesRecord);
        return skuSalesRecordMapper.statisSkuSales(skuSalesRecord);
    }


    /**
     * 更新缓存并永久存储据
     *
     * @param skuSalesRecord
     */
    private void updateCache(SkuSalesRecord skuSalesRecord) {
        String key = SKU_SALES_KEY + skuSalesRecord.getSysOrderId() + skuSalesRecord.getOrderTrackId() + skuSalesRecord.getSku();
        this.redisTemplate.delete(key);

        ValueOperations operations = this.redisTemplate.opsForValue();
        operations.set(key, skuSalesRecord);

    }
}
