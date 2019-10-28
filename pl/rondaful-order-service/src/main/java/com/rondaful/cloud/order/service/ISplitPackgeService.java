package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.model.dto.syncorder.SplitPackageDTO;

import java.util.List;

public interface ISplitPackgeService {
    /**
     * 保存拆分后的子包裹集合
     * @param splitPackageDTO
     */
    void saveSplittedSysPackage(SplitPackageDTO splitPackageDTO) throws Exception;

    /**
     * 撤销已拆分的包裹
     * @param sysOrderId
     * @return
     */
    void cancelSplittedSysPackage(String sysOrderId);

    /**
     * 保存合并后的包裹
     * @param sysOrderIds
     * @return
     */
    void saveMergedSysPackage(List<String> sysOrderIds) throws Exception;

    /**
     * 取消合并包裹
     * @param sysOrderIds
     * @return
     */
    void cancelMergedSysPackage(List<String> sysOrderIds);
}
