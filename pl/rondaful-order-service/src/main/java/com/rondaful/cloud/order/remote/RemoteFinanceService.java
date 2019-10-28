package com.rondaful.cloud.order.remote;


import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.order.entity.finance.OrderRequestVo;
import com.rondaful.cloud.order.enums.ResponseCodeEnum;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 远程调用服务
 * */
@FeignClient(name = "rondaful-finance-service", fallback = RemoteFinanceService.RemoteFinanceServiceImpl.class)
public interface RemoteFinanceService {
    /**
     * 自动支付校验
     * @param userId
     * @return
     */
    @PostMapping("/settings/autopayVerify")
    String autopayVerify(@RequestParam("userId") Integer userId);

    /**
     * 订单重新激活(重新发起冻结)
     * @param orderNo
     * @return
     */
    @PostMapping("/order/reactivation")
    String reactivation(@RequestParam("orderNo")String orderNo);

    /**
     * 财务模块订单创建：冻结订单金额
     * @param orderRequestVo
     * @return
     */
    @PostMapping("/order/generate")
    String generate(@RequestBody OrderRequestVo orderRequestVo);

    /**
     * 财务模块订单取消：取消冻结
     * @param orderNo
     * @return
     */
    @PostMapping("/order/cancel")
    String cancel(@RequestParam("orderNo") String orderNo);

    /**
     * 订单确认
     * @param orderNo
     * @param actualLogisticFare
     * @return
     */
    @PostMapping("/order/confirm")
    String confirm(@RequestParam("orderNo") String orderNo, @RequestParam("actualLogisticFare") BigDecimal actualLogisticFare);

    /**
     * 查询待补款
     * @param sellerId
     * @return
     */
    @PostMapping("/logistics/query")
    String logisticsQuery(@RequestParam("sellerId") Integer sellerId);

    /**
     * 查询待补款(内部调用免登陆)
     * @param sellerId
     * @return
     */
    @PostMapping("/logistics/query/intern")
    String logisticsQueryIntern(@RequestParam("sellerId") Integer sellerId);

    /**
     * 查询卖家账户余额
     * @param sellerId
     * @return
     */
    @PostMapping("/account/seller")
    String getSellerInfo(@RequestParam("sellerId") Integer sellerId);

    /**
     * 查询卖家账户余额(内部调用免登陆)
     * @param sellerId
     * @return
     */
    @PostMapping("/account/seller/intern")
    String getSellerInfoById(@RequestParam("sellerId") Integer sellerId);

    /**
     * 断路降级
     * */
    @Service
    class RemoteFinanceServiceImpl implements RemoteFinanceService {

        @Override
        public String autopayVerify(Integer userId) {
            return null;
        }

        @Override
        public String reactivation(String orderNo) {
            return null;
        }

        @Override
        public String generate(OrderRequestVo orderRequestVo) {
            return fallback();
        }

        @Override
        public String cancel(String orderNo) {
            return null;
        }

        @Override
        public String confirm(String orderNo, BigDecimal actualLogisticFare) {
            return null;
        }

        @Override
        public String logisticsQuery(Integer sellerId) {
            return null;
        }

        @Override
        public String logisticsQueryIntern(Integer sellerId) {
            return null;
        }

        @Override
        public String getSellerInfo(Integer sellerId) {
            return null;
        }

        @Override
        public String getSellerInfoById(Integer sellerId) {
            return null;
        }

        public String fallback() {
            return String.valueOf(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "财务服务异常")));
        }
    }
}





