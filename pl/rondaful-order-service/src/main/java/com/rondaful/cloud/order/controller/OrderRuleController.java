package com.rondaful.cloud.order.controller;


import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.orderRule.OrderRule;
import com.rondaful.cloud.order.entity.orderRule.OrderRuleSort;
import com.rondaful.cloud.order.entity.orderRule.OrderRuleWithBLOBs;
import com.rondaful.cloud.order.service.IOrderRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Api(description = "订单规则相关接口")
@RestController
@RequestMapping("/orderRule")
public class OrderRuleController {


    private static final Logger logger = LoggerFactory.getLogger(OrderRuleController.class);


    private final IOrderRuleService orderRuleService;

    private final GetLoginUserInformationByToken userInfo;


    @Autowired
    public OrderRuleController(IOrderRuleService IOrderRuleService, GetLoginUserInformationByToken userInfo) {
        this.orderRuleService = IOrderRuleService;
        this.userInfo = userInfo;
    }

    @AspectContrLog(descrption = "order添加订单规则", actionType = SysLogActionType.ADD)
    @PostMapping("/addRule/{type}")
    @ApiOperation("添加订单规则")
    public String addRule(
            @RequestBody OrderRuleWithBLOBs rule,
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type")
            @PathVariable String type) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
        if (StringUtils.isBlank(rule.getRuleName())
                || StringUtils.isBlank(rule.getPlatformMark()) //新增字段：平台标志
                || (type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType())
                && StringUtils.isBlank(rule.getMailTypeCode()))
                || (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && null == rule.getDeliveryWarehouseId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        }
        if ("S".equals(rule.getPlatformMark())) {
            if (StringUtils.isBlank(rule.getSellerId()) || StringUtils.isBlank(rule.getSellerAccount())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
            }
        }
        this.checkRong(rule);
        try {
            // todo 此处调用仓储接口判断 仓库 和 仓库下的发货方式是否可用(如果可用，其下残酷或者发货条件是否可用 )
            this.toNull(rule);
            if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())) {
                return orderRuleService.insertWarehouse(rule);
            } else
                return orderRuleService.insertMail(rule);
        } catch (Exception e) {
            if (e instanceof org.apache.xmlbeans.impl.piccolo.util.DuplicateKeyException
                    || e instanceof org.springframework.dao.DuplicateKeyException) {
                logger.error("规则名称重复", e);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "规则名称重复");
            }
            logger.error("添加订单规则异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    private void toNull(OrderRuleWithBLOBs rule) {
        rule.setDeliveryWarehouseCodeList(null);
        rule.setSellerAccountList(null);
        rule.setSellerAccountList(null);
        rule.setReceiveGoodsZipCodeList(null);
        rule.setPlSkuList(null);
    }

    @AspectContrLog(descrption = "order更新订单规则", actionType = SysLogActionType.UDPATE)
    @PutMapping("/updateRule/{type}")
    @ApiOperation("更新订单规则")
    public String updateRule(
            @RequestBody OrderRuleWithBLOBs rule,
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type")
            @PathVariable String type) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        if (rule.getId() == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        this.checkRong(rule);
        try {
            // todo 此处调用仓储接口判断 仓库 和 仓库下的发货方式是否可用 (如果可用，其下残酷或者发货条件是否可用 )
            this.toNull(rule);
            if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())) {
                orderRuleService.updateWarehouse(rule);
            } else
                orderRuleService.updateMail(rule);
            return rule.getId().toString();
        } catch (Exception e) {
            if (e instanceof org.apache.xmlbeans.impl.piccolo.util.DuplicateKeyException
                    || e instanceof org.springframework.dao.DuplicateKeyException) {
                logger.error("规则名称重复", e);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "规则名称重复");
            }
            logger.error("更新订单规则异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }


    @AspectContrLog(descrption = "更改订单状态", actionType = SysLogActionType.UDPATE)
    @PutMapping("/updateRuleStatus/{type}")
    @ApiOperation("更改订单状态")
    public String updateRuleStatus(
            @RequestBody OrderRule rule,
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type")
            @PathVariable String type) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "规则类型有误");
        if (rule.getId() == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        try {
            OrderRuleWithBLOBs orderRuleWithBLOBs = new OrderRuleWithBLOBs();
            orderRuleWithBLOBs.setId(rule.getId());
            orderRuleWithBLOBs.setStatus(rule.getStatus());
            if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())) {
                orderRuleService.updateWarehouseStatus(orderRuleWithBLOBs);
            } else {
                orderRuleService.updateMailStatus(orderRuleWithBLOBs);
            }
            return rule.getId().toString();
        } catch (Exception e) {
            logger.error("更新订单规则状态异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }


    private void checkRong(OrderRuleWithBLOBs rule) {
        this.checkString(rule.getPriceMin(), rule.getPriceMax(), "订单总售价");
        this.checkString(rule.getVolumeMin(), rule.getVolumeMax(), "订单总体积");
        this.checkString(rule.getWeightMin(), rule.getWeightMax(), "订单总重量");
        if (StringUtils.isNotBlank(rule.getEffectiveStartTime())) {
            rule.setEffectiveStartTime(this.addString(rule.getEffectiveStartTime(), 1));
        }
        if (StringUtils.isNotBlank(rule.getEffectiveEndTime())) {
            rule.setEffectiveEndTime(this.addString(rule.getEffectiveEndTime(), 2));
        }
        if (StringUtils.isNotBlank(rule.getEffectiveStartTime()) && StringUtils.isNotBlank(rule.getEffectiveEndTime())) {
            if (DateUtils.strToDate(rule.getEffectiveStartTime(), DateUtils.FORMAT_2).after(DateUtils.strToDate(rule.getEffectiveEndTime(), DateUtils.FORMAT_2))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "有效开始时间必须小于结束时间");
            }
            if (StringUtils.isNotBlank(rule.getEffectiveEndTime())) {
                if (new Date().after(DateUtils.strToDate(rule.getEffectiveEndTime(), DateUtils.FORMAT_2))) {
                    rule.setStatus(2);
                }
            }
        }
    }

    /**
     * 为一个只有年月日的日期字符串添加 时分秒
     *
     * @param str     添加前的字符串
     * @param integer 1 开始时间 2 结束时间
     * @return 添加后的字符串
     */
    private String addString(String str, Integer integer) {
        if (str.contains(":"))
            return str;
        if (integer == 1) {
            return str.trim() + " 00:00:00";
        } else {
            return str.trim() + " 23:59:59";
        }
    }

    /**
     * 检查字符串格式(1#00.00)，并且大小
     *
     * @param min          小的字符串
     * @param max          大的字符串
     * @param errorMessage 错误提示头
     */
    private void checkString(String min, String max, String errorMessage) {
        if (StringUtils.isNotBlank(min)) {
            if (checkStr(min))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), errorMessage + "下限格式错误");
        }
        if (StringUtils.isNotBlank(max)) {
            if (checkStr(max))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), errorMessage + "上限格式错误");
        }

        if (StringUtils.isNotBlank(max) && StringUtils.isNotBlank(min)) {
            Integer minCount = Integer.valueOf(min.split("#")[0]);
            Integer minNum = Integer.valueOf(min.split("#")[1]);
            Integer maxCount = Integer.valueOf(max.split("#")[0]);
            Integer maxNum = Integer.valueOf(max.split("#")[1]);
            if (minNum < 0 || maxNum < 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据格式有误，不能为负数");
            } else if (minNum > maxNum) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据格式有误，下限值不能大于上限值");
            } else if (minCount == 1 && maxCount == 1) {
                if (minNum == maxNum) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据格式有误，不能同时大于且小于" + minNum);
                }
            } else if (minCount == 1 && maxCount == 0) {
                if (minNum == maxNum) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据格式有误，不能同时大于且小于等于" + minNum);
                }
            } else if (minCount == 0 && maxCount == 1) {
                if (minNum == maxNum) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据格式有误，不能同时大于等于且小于" + minNum);
                }
            }
        }
//
//
//        if (StringUtils.isNotBlank(max) && StringUtils.isNotBlank(min)) {
//            String[] splitMin = min.split("#");
//            String[] splitMax = max.split("#");
//            if (new BigDecimal(splitMax[1]).compareTo(new BigDecimal(splitMin[1])) < 0)
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), errorMessage + "下限值不能大于上限值");
//        }
    }

    /**
     * 检查字符串格式是否错误，
     *
     * @param str 字符串
     * @return 错误返回 true 正确 放回false
     */
    private boolean checkStr(String str) {
        String[] split = str.split("#");
        if (split[1].contains("-")) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据格式有误");
        }
        if (split.length != 2)
            return true;
        try {
            int integer = Integer.parseInt(split[0]);
            if (integer != 0 && integer != 1)
                return true;
            new BigDecimal(split[1]);
        } catch (Exception e) {
            return true;
        }
        return false;
    }


    @AspectContrLog(descrption = "order交换两个订单规则优先级", actionType = SysLogActionType.UDPATE)
    @PutMapping("/swopPriority/{type}")
    @ApiOperation("交换两个规则的优先级")
    public void swopPriority(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type")
            @PathVariable String type,
            @RequestBody OrderRuleSort swop) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        if (swop.getId1() == null || swop.getId2() == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        try {
            OrderRuleWithBLOBs rule1 = orderRuleService.selectByPrimaryKey(swop.getId1(), type);
            OrderRuleWithBLOBs rule2 = orderRuleService.selectByPrimaryKey(swop.getId2(), type);
            if (rule1.getSellerId().equalsIgnoreCase(rule2.getSellerId()) ||
                    (rule1.getPlatformMark().equalsIgnoreCase(OrderRuleEnum.platformMark.CMS.getWay()) && rule2.getPlatformMark().equalsIgnoreCase(OrderRuleEnum.platformMark.CMS.getWay()))) {
                swop.setPriority1(rule2.getPriority());
                swop.setPriority2(rule1.getPriority());
                orderRuleService.swopPriority(swop, type);
            }
        } catch (Exception e) {
            logger.error("交换两个规则的优先级异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "order将订单规则置顶置尾", actionType = SysLogActionType.UDPATE)
    @PutMapping("/topOrTailPriority/{type}")
    @ApiOperation("将一个订单规则的优先级置顶或者置尾")
    public void topOrTailPriority(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type")
            @PathVariable String type,
            @RequestBody OrderRuleSort sort) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        if (sort.getId() == null || StringUtils.isBlank(sort.getWay()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        if (!sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TOP.getWay())
                && !sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TAIL.getWay()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        try {
            orderRuleService.topOrTailPriority(type, sort);
        } catch (Exception e) {
            logger.error("将一个订单规则的优先级置顶或者置尾异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "order将订单规则上移下移", actionType = SysLogActionType.UDPATE)
    @PutMapping("/upOrDownPriority/{type}")
    @ApiOperation("将一个订单规则的优先级上移或下移")
    public void upOrDownPriority(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type")
            @PathVariable String type,
            @RequestBody OrderRuleSort sort) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        if (sort.getId() == null || StringUtils.isBlank(sort.getWay()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        if (!sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TOP.getWay())
                && !sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TAIL.getWay()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        try {
            orderRuleService.upOrDownPriority(type, sort);
        } catch (Exception e) {
            logger.error("将一个订单规则的优先级上移或下移异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }


    @AspectContrLog(descrption = "order删除订单规则", actionType = SysLogActionType.DELETE)
    @DeleteMapping("/delete/{type}/{id}")
    @ApiOperation("删除订单规则")
    public void delete(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type")
            @PathVariable String type,
            @ApiParam(value = "订单规则id", name = "id") @PathVariable Long id) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        try {
            orderRuleService.deleteRule(id, type);
        } catch (Exception e) {
            logger.error("删除订单规则异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    /**
     * 针对将原有订单物流规则拆分为公共规则与卖家规则所做的调整
     *
     * @param type
     * @param ruleType
     * @param rule
     * @return
     */
    @AspectContrLog(descrption = "order查询订单规则列表", actionType = SysLogActionType.QUERY)
    @PostMapping("/queryRuleList/{ruleType}/{type}")
    @ApiOperation("查询规则列表")
    public List<OrderRuleWithBLOBs> queryRuleList(@ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type") @PathVariable String type, @ApiParam(value = "规则范围[public:公共规则 seller：卖家规则]", name = "ruleType") @PathVariable(value = "ruleType") String ruleType, @RequestBody OrderRule rule) {
        //0.对type值进行判空处理
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType()) && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
        //对搜索规则为卖家账号的
        if ("S".equals(rule.getPlatformMark()) && StringUtils.isBlank(rule.getSellerAccount())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        }
        try {
            return orderRuleService.queryRuleList(ruleType, type, rule);

        } catch (Exception e) {
            logger.error("查询规则列表异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "order查询订单规则列表", actionType = SysLogActionType.QUERY)
    @PostMapping("/queryRuleList/{type}")
    @ApiOperation("查询规则列表")
    public List<OrderRuleWithBLOBs> queryRuleList(@ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type") @PathVariable String type, @RequestBody OrderRule rule) {
        //0.对type值进行判空处理
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType()) &&
                !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);

        //对搜索规则为卖家账号的
        if ("S".equals(rule.getPlatformMark()) && StringUtils.isBlank(rule.getSellerAccount()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);

        try {
            if (type.equals(OrderRuleEnum.RuleType.WAREHOUSE.getType())) {
                return orderRuleService.warehouseList(rule);
            } else {
                return orderRuleService.mailList(rule);
            }

        } catch (Exception e) {
            logger.error("查询规则列表异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "order查询订单规则详情", actionType = SysLogActionType.QUERY)
    @GetMapping("/queryRuleById/{type}/{id}")
    @ApiOperation("根据id查询订单规则详情")
    public OrderRuleWithBLOBs queryRuleById(
            @ApiParam(value = "规则类型[mail:订单邮寄方式 warehouse:订单发货仓库]", name = "type")
            @PathVariable String type,
            @ApiParam(value = "订单规则id", name = "id") @PathVariable Long id) {
        if (!type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())
                && !type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        try {
            OrderRuleWithBLOBs orderRuleWithBLOBs = orderRuleService.selectByPrimaryKey(id, type);
            this.toNull(orderRuleWithBLOBs);
            return orderRuleWithBLOBs;
        } catch (Exception e) {
            logger.error("根据id查询订单规则详异常", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "订单规则匹配", actionType = SysLogActionType.QUERY)
    @PostMapping("/mapping/{platform}")
    @ApiOperation(value = "订单规则匹配")
    public void mappingOrderRule(
            @ApiParam(value = "[平台类型:amazon/eBay/wish/aliexpress] ", name = "platform")
            @PathVariable String platform, @RequestBody SysOrder order) {
        logger.info("订单规则匹配[mappingOrderRule]=======>执行开始");
        if (StringUtils.isBlank(order.getSysOrderId()) || StringUtils.isBlank(order.getSellerPlAccount())) {
            logger.info("SysOrderId或SellerPlAccount======>为空");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
        }
        orderRuleService.mappingOrderRuleNew(platform, order);
    }

}
