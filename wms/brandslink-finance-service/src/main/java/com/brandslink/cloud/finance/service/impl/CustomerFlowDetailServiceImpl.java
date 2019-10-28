package com.brandslink.cloud.finance.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.finance.constants.CustomerFlowConstant;
import com.brandslink.cloud.finance.mapper.CustomerFlowDetailMapper;
import com.brandslink.cloud.finance.pojo.dto.CustomerFlowDetailDto;
import com.brandslink.cloud.finance.pojo.entity.CustomerFlowDetail;
import com.brandslink.cloud.finance.pojo.feature.details.*;
import com.brandslink.cloud.finance.service.CustomerFlowDetailService;
import com.brandslink.cloud.finance.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangzefei
 * @Classname CustomerFlowDetailServiceImpl
 * @Description 客户资金流水详情
 * @Date 2019/8/26 10:08
 */
@Service
public class CustomerFlowDetailServiceImpl extends BaseServiceImpl<CustomerFlowDetail> implements CustomerFlowDetailService {
    private static String[] titleNames={"","存储费明细","入库费明细(免检)","销退费明细","出库费明细","入库费明细(抽检)","入库费明细(全检)"};
    private static Map<Integer,Class> clzMap=new HashMap<>();
    static{
        clzMap.put(CustomerFlowConstant.DETAIL_TYPE_STORAGE, StorageDetailFeature.class);
        clzMap.put(CustomerFlowConstant.DETAIL_TYPE_EXEMPT, InStockExemptFeature.class);
        clzMap.put(CustomerFlowConstant.DETAIL_TYPE_RETURN, ReturnDetailFeature.class);
        clzMap.put(CustomerFlowConstant.DETAIL_TYPE_OUT_STOCK, OutStockPackFeature.class);
        clzMap.put(CustomerFlowConstant.DETAIL_TYPE_SPOT, InStockSpotFeature.class);
        clzMap.put(CustomerFlowConstant.DETAIL_TYPE_ALL, InStockAllFeature.class);
    }

    @Resource
    private CustomerFlowDetailMapper customerFlowDetailMapper;

    /**
     * 流水详情导出
     * @param customerFlowId
     * @param response
     */
    @Override
    public void export(Integer customerFlowId, HttpServletResponse response){
        List<CustomerFlowDetailDto> list= customerFlowDetailMapper.getByCustomerFlowId(customerFlowId);
        for(CustomerFlowDetailDto p:list){
            if(CustomerFlowConstant.DETAIL_TYPE_OUT_STOCK.equals(p.getDetailType())){
                OutStockPackFeature feature=JSON.parseObject(p.getFeatureJson(),OutStockPackFeature.class);
                if(feature.getPackCost()==null){
                    p.setFeature(JSON.parseObject(p.getFeatureJson(),OutStockOperateFeature.class));
                }else{
                    p.setFeature(feature);
                }
            }else{
                p.setFeature(JSON.parseObject(p.getFeatureJson(),clzMap.get(p.getDetailType())));
            }

        }
        int index=list.get(0).getDetailType();
        String titleName=titleNames[index];
        ExcelUtil.exportExcel(list,titleName,titleName,CustomerFlowDetailDto.class,titleName+".xls",response);
    }
}
