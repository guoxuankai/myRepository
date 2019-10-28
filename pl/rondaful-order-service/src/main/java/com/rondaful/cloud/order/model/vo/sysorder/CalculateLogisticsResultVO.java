package com.rondaful.cloud.order.model.vo.sysorder;

import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Blade
 * @date 2019-07-26 19:34:27
 **/
public class CalculateLogisticsResultVO implements Serializable {

    private static final long serialVersionUID = -5026644364499199788L;
    /**
     * 卖家 物流费
     */
    private List<CalculateLogisticsSupplierVO> sellerList;

    /**
     * 供应商 物流费
     */
    private List<CalculateLogisticsSupplierVO> supplierList;

    /**
     * 物流商 物流费
     */
    private List<CalculateLogisticsSupplierVO> logisticsList;

    /**
     * 计算 卖家 供应商 物流商 费用的基础数据
     */
    private LogisticsCostVo logisticsCostData;

    public LogisticsCostVo getLogisticsCostData() {
        return logisticsCostData;
    }

    public void setLogisticsCostData(LogisticsCostVo logisticsCostData) {
        this.logisticsCostData = logisticsCostData;
    }

    public List<CalculateLogisticsSupplierVO> getSellerList() {
        return sellerList;
    }

    public void setSellerList(List<CalculateLogisticsSupplierVO> sellerList) {
        this.sellerList = sellerList;
    }

    public List<CalculateLogisticsSupplierVO> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<CalculateLogisticsSupplierVO> supplierList) {
        this.supplierList = supplierList;
    }

    public List<CalculateLogisticsSupplierVO> getLogisticsList() {
        return logisticsList;
    }

    public void setLogisticsList(List<CalculateLogisticsSupplierVO> logisticsList) {
        this.logisticsList = logisticsList;
    }
}
