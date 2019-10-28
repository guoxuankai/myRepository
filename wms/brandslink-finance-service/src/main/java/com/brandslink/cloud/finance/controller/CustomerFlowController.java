package com.brandslink.cloud.finance.controller;

import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.finance.pojo.dto.CustomerFlowDto;
import com.brandslink.cloud.finance.pojo.dto.CustomerSelfFlowDto;
import com.brandslink.cloud.finance.pojo.feature.*;
import com.brandslink.cloud.finance.pojo.vo.*;
import com.brandslink.cloud.finance.service.CenterDbService;
import com.brandslink.cloud.finance.service.CustomerFlowService;
import com.brandslink.cloud.finance.utils.ExcelUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangzefei
 * @Classname CustomerFlowController
 * @Description 客户交易流水
 * @Date 2019/8/27 14:15
 */
@RestController
@RequestMapping("/customerFlow")
public class CustomerFlowController extends BaseController {

    @Resource
    private CustomerFlowService customerFlowService;

    @Resource
    private CenterDbService centerDbService;

    @ApiOperation(value = "获取客户交易流水列表", notes = "客户交易流水列表")
    @GetMapping("/getListBySelf")
    public Page<CustomerSelfFlowDto> getListBySelf(CustomerFlowVo param){
        Page.builder(param.getPageNum(),param.getPageSize());
        List<CustomerSelfFlowDto> list=customerFlowService.getListBySelf(param);
        return new Page(new PageInfo(list));
    }

    @ApiOperation(value = "交易流水导出",notes = "交易流水导出")
    @GetMapping("/export")
    public void export(CustomerFlowVo param){
        param.setPageSize(50000);
        List<CustomerFlowDto> list=customerFlowService.getList(param);
        String fileName="交易流水";
        ExcelUtil.exportExcel(list,fileName,fileName,CustomerFlowDto.class,fileName+".xls",response);
    }

    @ApiOperation(value = "客户流水记录导出",notes = "客户流水记录导出")
    @GetMapping("/exportSelf")
    public void exportSelf(CustomerFlowVo param){
        param.setPageSize(50000);
        List<CustomerSelfFlowDto> list=customerFlowService.getListBySelf(param);
        String fileName="交易流水";
        ExcelUtil.exportExcel(list,fileName,fileName,CustomerFlowDto.class,fileName+".xls",response);
    }


    @ApiResponses(value = {
            @ApiResponse(code=1,message ="费用类型为存储费(costType=1),feature值" ,response = StorageCostFeature.class),
            @ApiResponse(code=2,message ="费用类型为入库费(costType=2),feature值" ,response = InStockCostFeature.class),
            @ApiResponse(code=3,message ="费用类型为销退费(costType=3),feature值" ,response = ReturnCostFeature.class),
            @ApiResponse(code=4,message ="费用类型为出库费(costType=4),feature值" ,response = OutStockCostFeature.class),
            @ApiResponse(code=5,message ="费用类型为拦截费(costType=5),feature值" ,response = InterceptCostFeature.class),
            @ApiResponse(code=6,message ="费用类型为物流费(costType=6),feature值" ,response = LogisticsCostFeature.class),
            @ApiResponse(code=7,message ="费用类型为充值费(costType=7),feature值" ,response = RechargeFeature.class)
    })
    @ApiOperation(value = "获取单个流水详情",notes = "获取单个流水详情")
    @GetMapping("/getById")
    public CustomerFlowDto getById(@ApiParam(value = "流水ID",required = true)@RequestParam("id") Integer id){
        return customerFlowService.getById(id);
    }

    @ApiOperation(value = "计算入库费",notes = "计算入库费")
    @PostMapping("/calcInStockCost")
    public void calcInStockCost(@RequestBody StockCostVo param){
        centerDbService.setStockCostVo(param);
        customerFlowService.calcInStockCost(param);
    }

    @ApiOperation(value = "计算销退费",notes = "计算销退费")
    @PostMapping("/calcReturnCost")
    public void calcReturnCost(@RequestBody StockCostVo param){
        centerDbService.setStockCostVo(param);
        customerFlowService.calcReturnCost(param);
    }

    @ApiOperation(value = "计算出库费",notes = "计算出库费")
    @PostMapping("/calcOutStockCost")
    public void calcOutStockCost(@RequestBody OutStockCostVo param){
        centerDbService.setStockCostVo(param);
        customerFlowService.calcOutStockCost(param);
    }

    @ApiOperation(value = "计算拦截费",notes = "计算拦截费")
    @PostMapping("/calcInterceptCost")
    public void calcInterceptCost(@RequestBody InterceptCostVo param){
        String warehouseName=centerDbService.getWarehouseName(param.getWarehouseCode());
        param.setWarehouseName(warehouseName);
        customerFlowService.calcInterceptCost(param);
    }

    @ApiOperation(value = "计算物流费",notes = "计算物流费")
    @PostMapping("/calcLogisticsCost")
    public void calcLogisticsCost(@RequestBody LogisticsCostVo param){
        String warehouseName=centerDbService.getWarehouseName(param.getWarehouseCode());
        param.setWarehouseName(warehouseName);
        customerFlowService.calcLogisticsCost(param);
    }
}
