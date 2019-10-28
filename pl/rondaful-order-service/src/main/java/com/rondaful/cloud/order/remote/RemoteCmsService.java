package com.rondaful.cloud.order.remote;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.order.entity.CallBackVO;
import com.rondaful.cloud.order.entity.MessageNoticeModel;
import com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe.GoodCangBackOrderVo;
import com.rondaful.cloud.order.enums.ResponseCodeEnum;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "rondaful-cms-service", fallback = RemoteCmsService.RemoteCmsServiceImpl.class)
public interface RemoteCmsService {

    @PostMapping("/afterSales/judgeAfterSalesFinishByOrderId")
    String judgeAfterSalesFinishByOrderId(@RequestBody List<String> orderId);

    @GetMapping("/afterSales/getOrderAfterSaleByOrderTrackIdAndSku/{orderTrackId}/{commoditySku}")
    @AspectContrLog(descrption = "根据包裹号和sku查询售后订单详情", actionType = SysLogActionType.QUERY)
    String getOrderAfterSaleByOrderTrackIdAndSku( @PathVariable(value = "orderTrackId") String orderTrackId, @PathVariable(value = "commoditySku") String commoditySku
    , @PathVariable(value = "orderId")String orderId);

    @GetMapping("/afterSales/findOrderAfterSaleByOrderId/{orderId}")
    @ApiOperation(value = "根据订单ID查询售后订单详情")
    String findOrderAfterSaleByOrderId(@PathVariable(value = "orderId")String orderId);

    @PostMapping("/replenishment/updateOrderCallBack")
    @ApiOperation(value = "订单系统回调修改状态")
    String updateOrderCallBack(@RequestBody CallBackVO vo) throws  Exception;

    @PostMapping(value = "/messageNotice/dispose")
    @ApiOperation(value = "业务完成接口调用")
    void messageNoticeDispose(@RequestBody MessageNoticeModel message);

    @GetMapping("/afterSales/findUserRefundMoney/{type}")
    @ApiOperation(value = "查询用户售后退款金额")
    String findUserRefundMoney(String shop,@ApiParam(value = "Y当月、N上月、X全部", name = "type", required = true) @PathVariable(value = "type") String
            type);

    @PostMapping("/replenishment/getGCErrorOrder")
    @ApiOperation(value = "获取谷仓异常订单推送的订单")
    String getGCErrorOrder(@RequestBody GoodCangBackOrderVo goodCangBackOrderVo);

    @Service
    class RemoteCmsServiceImpl implements RemoteCmsService {

        @Override
        public String judgeAfterSalesFinishByOrderId(List<String> orderId) {
            return null;
        }

        @Override
        public String getOrderAfterSaleByOrderTrackIdAndSku(String orderTrackId, String commoditySku, String orderId) {
            return null;
        }

        @Override
        public String findOrderAfterSaleByOrderId(String orderId) {
            return null;
        }

        @Override
        public String updateOrderCallBack(CallBackVO vo) throws Exception {
            return null;
        }

        @Override
        public void messageNoticeDispose(MessageNoticeModel message) {

        }

        @Override
        public String findUserRefundMoney( String shop,String type) {
            return null;
        }

        @Override
        public String getGCErrorOrder(GoodCangBackOrderVo goodCangBackOrderVo) {
            return null;
        }

        public String fallback() {
            return String.valueOf(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "售后服务异常。。。")));
        }
    }
}
