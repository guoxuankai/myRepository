package com.rondaful.cloud.order.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.order.model.PageDTO;
import com.rondaful.cloud.order.model.aliexpress.request.QueryPageDTO;
import com.rondaful.cloud.order.model.aliexpress.response.OrderDTO;
import com.rondaful.cloud.order.model.aliexpress.response.OrderExportDTO;
import com.rondaful.cloud.order.model.aliexpress.response.OrderOtherDTO;
import com.rondaful.cloud.order.service.aliexpress.IAliexpressOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @Author: xqq
 * @Date: 2019/4/4
 * @Description:
 */
@Api(description = "速卖通订单相关业务")
@RestController
@RequestMapping("aliexpress/order/")
public class AliexpressOrderController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(EbayOrderController.class);
    @Autowired
    private IAliexpressOrderService aliexpressOrderService;



    @ApiOperation(value = "拉取订单")
    @GetMapping("initData")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "string", paramType = "query", required = true)
    })
    public void initData(String startTime, String endTime) {
        UserDTO userDTO = super.userToken.getUserDTO();
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DateUtils.FORMAT_2);
        if (StringUtils.isEmpty(startTime)) {
            LocalDateTime now = LocalDateTime.now();
            endTime = df.format(now);
            startTime = df.format(now.minusMonths(3));
        }
        LocalDate start = LocalDate.parse(startTime, df);
        LocalDate end = LocalDate.parse(endTime, df);
        Integer month = Period.between(start, end).getMonths();
        if (month > 3 || month < 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "只能同步起止时间跨度三个月内的订单");
        }
        String loginName = null;
        if (UserEnum.platformType.SELLER.getPlatformType().equals(userDTO.getPlatformType())) {
            loginName = userDTO.getLoginName();
        }
        this.aliexpressOrderService.initData(startTime, endTime, loginName);
    }

    @ApiOperation(value = "分页查询速卖通订单")
    @GetMapping("getPage")
    public PageDTO<OrderDTO> getPage(QueryPageDTO dto) {
        if (dto.getStartTime() == null) {
            dto.setEndTime(new Date());
            LocalDate localDate = LocalDate.now().minusMonths(3L);
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
            dto.setStartTime(Date.from(instant));
        } else {
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate start = dto.getStartTime().toInstant().atZone(zoneId).toLocalDate();
            LocalDate end = dto.getEndTime().toInstant().atZone(zoneId).toLocalDate();
            Integer month = Period.between(start, end).getMonths();
            if (month > 3 || month < 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "只能同步起止时间跨度三个月内的订单");
            }
        }
        if ("-1".equals(dto.getOrderStatus())) {
            dto.setOrderStatus(null);
        }
        if (dto.getCallBackStatus() == null || -1 == dto.getCallBackStatus()) {
            dto.setCallBackStatus(null);
        }
        if (dto.getPlProcessStatus() == null || -1 == dto.getPlProcessStatus()) {
            dto.setPlProcessStatus(null);
        }

        UserCommon user = super.userToken.getUserInfo().getUser();
        Map<Integer, List<Integer>> map = super.getBinds(user);
        if (map == null) {
            return new PageDTO<>(0L, 1L);
        }
        dto.setEmpIds(map.get(super.EMP_ID_LIST));
        dto.setUserNames(super.getsName(map.get(super.USER_ID_LIST)));
        return this.aliexpressOrderService.getPage(dto);
    }

    @ApiOperation(value = "根据订单ID查询订单中的款项信息与收货方相关详情")
    @GetMapping("getOrderData")
    public OrderOtherDTO getOrderData(String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单号为空");
        }
        return this.aliexpressOrderService.getOrderData(orderId);
    }

    @ApiOperation(value = "根据订单号转入系统订单")
    @PostMapping("toSysOrder")
    public Integer toSysOrder(String orderIds) {
        if (StringUtils.isEmpty(orderIds)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单号为空");
        }
        return this.aliexpressOrderService.toSysOrder(JSONObject.parseArray(orderIds, String.class));
    }

    @ApiOperation(value = "更新订单状态")
    @GetMapping("syncOrder")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "orderStatus", value = "订单状态", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "loginId", value = "卖家登录id", dataType = "string", paramType = "query", required = true)
    })
    public Integer syncOrder(String orderId, String orderStatus, String loginId) {
        return this.aliexpressOrderService.syncOrder(orderId, orderStatus, loginId);
    }


    @ApiOperation(value = "导出")
    @GetMapping("export")
    public void export(QueryPageDTO dto, HttpServletResponse response) {
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            dto.setEndTime(new Date());
            LocalDate localDate = LocalDate.now().minusMonths(3L);
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
            dto.setStartTime(Date.from(instant));
        } else {
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate start = dto.getStartTime().toInstant().atZone(zoneId).toLocalDate();
            LocalDate end = dto.getEndTime().toInstant().atZone(zoneId).toLocalDate();
            Integer month = Period.between(start, end).getMonths();
            if (month > 3 || month < 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "只能同步起止时间跨度三个月内的订单");
            }
        }
        if ("-1".equals(dto.getOrderStatus())) {
            dto.setOrderStatus(null);
        }
        if (dto.getCallBackStatus() == null || -1 == dto.getCallBackStatus()) {
            dto.setCallBackStatus(null);
        }
        if (dto.getPlProcessStatus() == null || -1 == dto.getPlProcessStatus()) {
            dto.setPlProcessStatus(null);
        }
        UserAll userAll = super.userToken.getUserInfoByToken(dto.getToken());
        if (userAll == null) {
            logger.error("未登录");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406.getCode(), ResponseCodeEnum.RETURN_CODE_100406.getMsg());
        }
        if (userAll.getUser().getPlatformType() == 1) {
            dto.setPlAccount(userAll.getUser().getLoginName());
        }
        Map<Integer, List<Integer>> map = super.getBinds(userAll.getUser());
        if (map == null) {
            return;
        }
        dto.setEmpIds(map.get(super.EMP_ID_LIST));
        dto.setUserNames(super.getsName(map.get(super.USER_ID_LIST)));

        Workbook workbook = null;
        Boolean isNext = false;
        dto.setCurrentPage(1);
        dto.setPageSize(50);
        do {
            PageDTO<OrderExportDTO> page = this.aliexpressOrderService.export(dto);
            if (dto.getCurrentPage() * dto.getPageSize() < page.getTotalCount()) {
                isNext = true;
                dto.setCurrentPage(dto.getCurrentPage() + 1);
            } else {
                isNext = false;
            }
            workbook = ExcelExportUtil.exportBigExcel(new ExportParams(null, "Sheet1", ExcelType.HSSF), OrderExportDTO.class, page.getList());
        } while (isNext);
        ExcelExportUtil.closeExportBigExcel();
        response.setHeader("Content-disposition", "attachment;filename=" + "order.xls");
        response.setContentType("application/x-download");
        response.setCharacterEncoding("UTF-8");
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            logger.error("导出异常:", e.getMessage(), e);
        }
    }
}
