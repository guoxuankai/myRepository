package com.rondaful.cloud.supplier.controller;

import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.logistics.*;
import com.rondaful.cloud.supplier.model.request.logistics.QueryPageReq;
import com.rondaful.cloud.supplier.model.response.logistics.OrderSelectReq;
import com.rondaful.cloud.supplier.service.ILogisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/10/17
 * @Description:
 */
@Api(description = "物流服务相关")
@RestController
@RequestMapping("logistics/")
public class NewLogisticsController extends BaseController {

    @Autowired
    private ILogisticsService logisticsService;

    @ApiOperation(value = "查询物流方式")
    @PostMapping("getsPage")
    public PageDTO<LogisticsPageDTO> getsPage(QueryPageReq req){
        QueryPageDTO dto=new QueryPageDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setLanguageType(super.request.getHeader("i18n"));
        return this.logisticsService.getsPage(dto);
    }

    @ApiOperation(value = "修改物流方式状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "物流方式id",paramType = "form",dataType = "Integer",required = true),
            @ApiImplicitParam(name = "status",value = "1 有效  4 无效",paramType = "form",dataType = "Integer",required = true)
    })
    @PostMapping("updateStatus")
    public Integer updateStatus(Integer id,Integer status){
        return this.logisticsService.updateStatus(id,status);
    }

    @ApiOperation(value = "获取物流方式详情")
    @ApiImplicitParam(name = "id",value = "物流方式id",dataTypeClass = Integer.class,paramType = "query",required = true)
    @GetMapping("get")
    public LogisticsDetailDTO get(Integer id){
        return this.logisticsService.get(id,super.request.getHeader("i18n"));
    }


    @ApiOperation(value = "修改平台物流映射")
    @PostMapping("updateMap")
    public Integer updateMap(@RequestBody List<LogisticsMapDTO> list){
        return this.logisticsService.updateMap(list);
    }

    @ApiOperation(value = "运费试算下拉")
    @PostMapping("getSelect")
    public OrderSelectReq getSelect(@RequestBody QuerySelectDTO dto){
        OrderSelectReq result=new OrderSelectReq();
        List<String> codes=new ArrayList<>();
        List<LogisticsSelectDTO> valid=this.logisticsService.getSelect(dto);
        result.setValid(valid);
        valid.forEach(selectDTO-> {
            codes.add(selectDTO.getSmCode());
        });
        List<LogisticsSelectDTO> invalid=this.logisticsService.getByWarehouseId(dto.getWarehouseId(),codes);
        result.setInvalid(invalid);
        return result;
    }



}
