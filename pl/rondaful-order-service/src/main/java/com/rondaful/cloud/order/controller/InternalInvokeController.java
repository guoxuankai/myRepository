package com.rondaful.cloud.order.controller;

import com.rondaful.cloud.order.service.ISystemOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "品连系统订单微服务内部调用控制层")
@RestController
@RequestMapping(value = "/internal")
public class InternalInvokeController {
    @Autowired
    private ISystemOrderService systemOrderService;

    @PostMapping("/cmsMarkSYSCompleted")
    @ApiOperation("售后标记系统订单已完成")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysIDList", value = "请求标记的系统订单ID集合", paramType = "body", required = true)})
    public void cmsMarkSYSCompleted(@RequestBody List<String> sysIDList) {
        systemOrderService.cmsMarkSYSCompleted(sysIDList);
    }
}
