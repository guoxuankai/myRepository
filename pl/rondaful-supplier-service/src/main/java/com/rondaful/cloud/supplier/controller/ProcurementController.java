package com.rondaful.cloud.supplier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.supplier.model.dto.procurement.*;
import com.rondaful.cloud.supplier.model.request.procurement.AddSuggestReq;
import com.rondaful.cloud.supplier.model.request.procurement.ProcurementReq;
import com.rondaful.cloud.supplier.model.request.procurement.QueryProcurementPageReq;
import com.rondaful.cloud.supplier.model.request.procurement.QuerySuggestPageReq;
import com.rondaful.cloud.supplier.service.IProcurementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/20
 * @Description:
 */
@Api(description = "采购相关业务")
@RestController
@RequestMapping("/procurement/")
public class ProcurementController extends BaseController{
    @Autowired
    private IProcurementService procurementService;


    @ApiOperation(value = "修改采购单明细")
    @PostMapping("update")
    public Integer update(ProcurementReq req){
        UserDTO userDTO=super.userToken.getUserDTO();
        JSONArray items=JSONArray.parseArray(req.getItems());
        for (int i = 0; i < items.size(); i++) {
            ProcurementDTO dto=new ProcurementDTO();
            BeanUtils.copyProperties(req,dto);
            dto.setCreateBy(userDTO.getLoginName());
            JSONObject jsonObject=items.getJSONObject(i);
            List<ProcurementListDTO> childItem=new ArrayList<>(1);
            ProcurementListDTO dto1=JSONObject.parseObject(jsonObject.toJSONString(),ProcurementListDTO.class);
            childItem.add(dto1);
            dto.setItems(childItem);
            this.procurementService.update(dto);
        }
        return 1;
    }

    @ApiOperation(value = "分页查询采购单")
    @PostMapping("getsPage")
    public PageDTO<ProcurementPageDTO> getsPage(QueryProcurementPageReq req){
        QueryProcurementPageDTO dto =new QueryProcurementPageDTO();
        BeanUtils.copyProperties(req,dto);
        return this.procurementService.getsPage(dto);
    }

    @ApiOperation(value = "根据采购单号获取明细")
    @ApiImplicitParam(name = "id", value = "采购单号", required = true, dataType = "int", paramType = "query")
    @GetMapping("getsDetailList")
    public ProcurementDetailDTO getsDetailList(Long id){
        return this.procurementService.getsDetailList(id,super.request.getHeader("i18n"));
    }

    @ApiOperation(value = "修改采购单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "状态:7-草稿,2-审核中,3-审核失败,6-待入库,5-入库,8-部分入库,4-作废", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "采购单id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "remake", value = "备注", dataType = "String", paramType = "query")
    })
    @PostMapping("updateStatus")
    public Integer updateStatus(Long id,Integer status,String remake){
        return this.procurementService.updateStatus(id,status,super.userToken.getUserDTO().getLoginName(),remake);
    }

    @ApiOperation(value = "新增采购单")
    @PostMapping("add")
    public Integer add(ProcurementReq req){
        /**
         * 日常关注SX设计行动
         */
        UserDTO userDTO=super.userToken.getUserDTO();
        JSONArray items=JSONArray.parseArray(req.getItems());
        for (int i = 0; i < items.size(); i++) {
            ProcurementDTO dto=new ProcurementDTO();
            BeanUtils.copyProperties(req,dto);
            dto.setCreateBy(userDTO.getLoginName());
            dto.setTopUserId(userDTO.getTopUserId());
            JSONObject jsonObject=items.getJSONObject(i);
            List<ProcurementListDTO> childItem=new ArrayList<>(1);
            ProcurementListDTO dto1=JSONObject.parseObject(jsonObject.toJSONString(),ProcurementListDTO.class);
            childItem.add(dto1);
            dto.setItems(childItem);
            this.procurementService.add(dto);
        }
        return 1;
    }


    @ApiOperation(value = "添加采购建议")
    @PostMapping("addSuggest")
    public Integer addSuggest(AddSuggestReq req){
        SuggestDTO dto=new SuggestDTO();
        BeanUtils.copyProperties(req,dto);
        return this.procurementService.addSuggest(dto);
    }

    @ApiOperation(value = "修改状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "状态:1-处理,2-未处理", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "suggestId", value = "id", required = true, dataType = "Long", paramType = "query")
    })
    @PostMapping("updateSuggestStatus")
    public Integer updateSuggestStatus(Integer status,Long suggestId){
        return this.procurementService.updateSuggestStatus(super.userToken.getUserDTO().getLoginName(),status,suggestId);
    }

    @ApiOperation(value = "分页查询采购建议")
    @PostMapping("getsSuggestPage")
    public PageDTO<SuggestPageDTO> getsSuggestPage(QuerySuggestPageReq req){
        QuerySuggestPageDTO dto =new QuerySuggestPageDTO();
        BeanUtils.copyProperties(req,dto);
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime=LocalDateTime.now().minusMonths(3);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isEmpty(req.getStartTime())){
            Instant instant = localDateTime.atZone(zone).toInstant();
            dto.setStartTime(Date.from(instant));
        }else{
            LocalDateTime localStart=LocalDateTime.parse(req.getStartTime(),df);
            if (localStart.isBefore(localDateTime)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "只能查询三个月之内的数据");
            }
            Instant instant = localStart.atZone(zone).toInstant();
            dto.setStartTime(Date.from(instant));
        }
        if (StringUtils.isEmpty(req.getEndTime())){
            dto.setEndTime(new Date());
        }else {
            LocalDateTime localEnd=LocalDateTime.parse(req.getEndTime(),df);
            if (LocalDateTime.now().isBefore(localEnd)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "结束时间超过当前时间");
            }
            Instant instant = localEnd.atZone(zone).toInstant();
            dto.setEndTime(Date.from(instant));
        }
        dto.setLanguageType(super.request.getHeader("i18n"));
        return this.procurementService.getsSuggestPage(dto);
    }

    @ApiOperation(value = "批量添加入库单")
    @ApiImplicitParam(name = "params", value = "单个入库的数组对象tostring", required = true, dataType = "String", paramType = "query")
    @PostMapping("addBatch")
    public Integer addBatch(String params){
        List<ProcurementDTO> list=JSONArray.parseArray(params,ProcurementDTO.class);
        return this.procurementService.addBatch(list);
    }


    @ApiOperation(value = "采购单入库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "采购单的单项id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "num", value = "入库数量", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("storage")
    public Integer storage(Long id,Integer num){
        return this.procurementService.storage(id,num);
    }


}
