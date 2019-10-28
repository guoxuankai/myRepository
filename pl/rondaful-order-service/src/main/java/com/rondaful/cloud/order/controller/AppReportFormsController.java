package com.rondaful.cloud.order.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.order.entity.BuyerCountAndCountryCode;
import com.rondaful.cloud.order.entity.TheMonthOrderCount;
import com.rondaful.cloud.order.entity.TheMonthOrderSaleAndProfit;
import com.rondaful.cloud.order.service.ISysOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-02-13 8:55
 * 包名: com.rondaful.cloud.order.controller
 * 描述:
 */
@Api(description = "App报表控制层")
@RestController
@RequestMapping(value = "/AppReportFormsController")
public class AppReportFormsController extends BaseController {
    @Autowired
    private UserUtils userInfo;
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AppReportFormsController.class);
    @Autowired
    private ISysOrderService sysOrderService;

    @AspectContrLog(descrption = "查询今日和昨日订单",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "查询今日和昨日订单-wujiachuang")
    @GetMapping("/getOrderCountTodayAndYesterday")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopName", value = "亚马逊店铺名", dataType = "string", paramType = "query", required = false)
    })
    public Map<String,Object> getOrderCountTodayAndYesterday(String shopName){
        String loginName = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        if (StringUtils.isEmpty(loginName)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连用户名不能为空！");
        }
        return  sysOrderService.getOrderCountTodayAndYesterday(loginName, shopName);
    }

    @AspectContrLog(descrption = "查询卖家日订单数量",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "查询卖家日订单数量-wujiachuang")
    @GetMapping("/querySellerDayOrderCount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopName", value = "亚马逊店铺名", dataType = "string", paramType = "query", required = false)
    })
    public List<TheMonthOrderCount> querySellerDayOrderCount( String shopName){
        String loginName = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        if (StringUtils.isEmpty(loginName)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连用户名不能为空！");
        }
        return sysOrderService.querySellerDayOrderCount(loginName,shopName);
    }


    @AspectContrLog(descrption = "查询卖家的总销售额和总利润",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "查询卖家的总销售额和总利润-wujiachuang")
    @GetMapping("/querySellerTotalSalesAndTotalProfit")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopName", value = "亚马逊店铺名", dataType = "string", paramType = "query", required = false)
    })
    public Map<String,Object> querySellerTotalSalesAndTotalProfit(String shopName){
        String loginName = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        if (StringUtils.isEmpty(loginName)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连用户名不能为空！");
        }
        return sysOrderService.querySellerTotalSalesAndTotalProfit(loginName,shopName);
    }

    @AspectContrLog(descrption = "查询卖家日总销售额和日总利润",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "查询卖家日总销售额和日总利润-wujiachuang")
    @GetMapping("/querySellerDayTotalSalesAndTotalProfit")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopName", value = "亚马逊店铺名", dataType = "string", paramType = "query", required = false)
    })
    public List<TheMonthOrderSaleAndProfit> querySellerDayTotalSalesAndTotalProfit(String shopName){
        String loginName = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        if (StringUtils.isEmpty(loginName)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连用户名不能为空！");
        }
        return sysOrderService.querySellerDayTotalSalesAndTotalProfit(loginName,shopName);
    }

    @AspectContrLog(descrption = "查询买家所在国家分布情况",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "查询买家所在国家分布情况-wujiachuang")
    @GetMapping("/queryTotalBuyerCountAndCountry")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopName", value = "亚马逊店铺名", dataType = "string", paramType = "query", required = false)
    })
    public List<BuyerCountAndCountryCode> queryTotalBuyerCountAndCountry(String shopName){
        String loginName = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        if (StringUtils.isEmpty(loginName)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连用户名不能为空！");
        }
        List<BuyerCountAndCountryCode> buyerCountAndCountryCodes = sysOrderService.queryTotalBuyerCountAndCountry(loginName, shopName);
        return buyerCountAndCountryCodes;
    }

    @AspectContrLog(descrption = "查询买家人数和重复购买的人数",actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "查询买家人数和重复购买的人数-wujiachuang")
    @GetMapping("/queryBuyerCountAndBuyerCountBuyAgain")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopName", value = "亚马逊店铺名", dataType = "string", paramType = "query", required = false)
    })
    public Map<String,Object> queryBuyerCountAndBuyerCountBuyAgain(String shopName){
        String loginName = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        if (StringUtils.isEmpty(loginName)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连用户名不能为空！");
        }
        return sysOrderService.queryBuyerCountAndBuyerCountBuyAgain(loginName, shopName);
    }


}
