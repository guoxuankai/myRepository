package com.rondaful.cloud.seller.remote;


import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.seller.entity.OrderRule;
import com.rondaful.cloud.seller.entity.OrderRuleSort;
import com.rondaful.cloud.seller.entity.OrderRuleWithBLOBs;
import com.rondaful.cloud.seller.entity.SellerSkuMap;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 远程调用订单服务（订单规则相关）
 */
@FeignClient(name = "rondaful-order-service", fallback = RemoteOrderRuleService.RemoteOrderRuleServiceImpl.class)
public interface RemoteOrderRuleService {

	
	/**
	 * 检验亚马逊授权token是否有效
	 * @param sellerId
	 * @param marketplaceId
	 * @param mwsAuthToken
	 * @return
	 */
	@GetMapping("/amazonOrder/checkAmazonTokenIsValid")
    String checkAmazonTokenIsValid(@RequestParam("sellerId")String sellerId,
    		                       @RequestParam("marketplaceId")String marketplaceId,
    		                       @RequestParam("mwsAuthToken")String mwsAuthToken);
	
	
    /**
     * 添加订单规则
     * @param rule 订单规则参数封装对象
     * @param type 规则类型 [mail:订单邮寄方式 warehouse:订单发货仓库]
     * @return 返回数据
     */
    @PostMapping("/orderRule/addRule/{type}")
    String addRule(@RequestBody OrderRuleWithBLOBs rule,
                   @PathVariable(value = "type") String type);

    /**
     * 更新订单规则
     * @param rule 订单规则参数封装对象
     * @param type 规则类型 [mail:订单邮寄方式 warehouse:订单发货仓库]
     * @return 返回数据
     */
    @PutMapping("/orderRule/updateRule/{type}")
    String updateRule(@RequestBody OrderRuleWithBLOBs rule,
                      @PathVariable(value = "type") String type);

    /**
     * 交换两个规则的优先级
     * @param type 规则类型 [mail:订单邮寄方式 warehouse:订单发货仓库]
     * @param swop 参数封装对象
     */
    @PutMapping("/orderRule/swopPriority/{type}")
    String swopPriority(@PathVariable(value = "type") String type,
                      @RequestBody OrderRuleSort swop);

    /**
     * 将规则优先级置顶或者置底
     * @param type 规则类型 [mail:订单邮寄方式 warehouse:订单发货仓库]
     * @param sort 参数封装对象
     */
    @PutMapping("/orderRule/topOrTailPriority/{type}")
    String topOrTailPriority(@PathVariable(value = "type") String type,
                           @RequestBody OrderRuleSort sort);

    /**
     * 删除规则对象
     * @param type 规则类型 [mail:订单邮寄方式 warehouse:订单发货仓库]
     * @param id 规则id
     */
    @DeleteMapping("/orderRule/delete/{type}/{id}")
    String delete(@PathVariable(value = "type") String type,
                @PathVariable(value = "id") Long id);

    /**
     * 查询规则列表
     * @param type 规则类型 [mail:订单邮寄方式 warehouse:订单发货仓库]
     * @return 返回数据
     */
    @RequestMapping(value = "/orderRule/queryRuleList/{type}", method = RequestMethod.POST)
    String queryRuleList(@PathVariable(value = "type") String type, @RequestBody OrderRule rule);

    /**
     * 根据id查询规则详情
     * @param type 规则类型 [mail:订单邮寄方式 warehouse:订单发货仓库]
     * @param id 规则id
     * @return 返回数据
     */
    @GetMapping("/orderRule/queryRuleById/{type}/{id}")
    String queryRuleById(@PathVariable(value = "type") String type,
                         @PathVariable(value = "id") Long id);

    /**
     * 批量添加sku映射
     * @param maps sku映射列表
     * @return 返回数据
     */
    @PostMapping("/skuMap/addSkuMaps")
    String addSkuMaps(@RequestBody List<SellerSkuMap> maps);

    /**
     * 内部定时任务批量添加sku映射
     * @param maps sku映射列表
     * @param key 内部验证key
     * @return 返回数据
     */
    @PostMapping("/skuMap/addSkuMapsWhitTask")
    String addSkuMapsWhitTask(@RequestBody List<SellerSkuMap> maps,@RequestParam("key")String key);

    /**
     * 更新sku映射
     * @param map sku 对象
     */
    @PutMapping("/skuMap/updateSkuMap")
    String updateSkuMap(@RequestBody SellerSkuMap map);

    /**
     * 查询sku映射列表
     * @param page 页码
     * @param row 页容量
     * @param startCreateTime 开始创建时间
     * @param endCreateTime 结束创建时间
     * @param status 状态 [1:启用 2:停用 ]
     * @param plSku 品连sku
     * @param platformSku 平台sku
     * @param sellerPlAccount   用户在品连的账号
     * @param sellerSelfAccount 授权时的自定义账号
     * @return 返回数据
     */
    @RequestMapping(value = "/skuMap/queryMaps",method = RequestMethod.GET)
    String queryMaps(@RequestParam("page") String page, @RequestParam("row") String row, @RequestParam("startCreateTime") String startCreateTime, @RequestParam("endCreateTime") String endCreateTime,
                     @RequestParam("status")Integer status ,@RequestParam("plSku") String plSku,@RequestParam("platformSku")String platformSku, @RequestParam("sellerPlAccount") String sellerPlAccount,
                     @RequestParam("sellerSelfAccount") String sellerSelfAccount );

    /**
     * 删除sku映射
     * @param id sku映射id
     * @return 返回数据
     */
    @DeleteMapping("/skuMap/deleteMap/{id}")
    String deleteMap(@PathVariable(value = "id") Long id);

    /**
     *
     * @param platform 平台 aliexpress，eBay，Amazon，with
     * @param authorizationId 授权id
     * @param platformSku 平台sku
     * @param sellerId 卖家id
     * @return
     */
    @GetMapping("/skuMap/getSellerSkuMapByPlatformSku")
    String getSellerSkuMapByPlatformSku(@RequestParam("platform") String platform,
                                        @RequestParam("authorizationId")String authorizationId, @RequestParam("platformSku")String platformSku,
                                        @RequestParam("sellerId")String sellerId);


    /**
     * 获取对应的汇率
     * @param soStr
     * @param toStr
     * @return
     */
    @GetMapping("/rate/GetRate")
    String GetRate(@RequestParam("soStr") String soStr,@RequestParam("toStr") String toStr);
    
    /**
     * 断路降级
     */
    @Service
    class RemoteOrderRuleServiceImpl implements RemoteOrderRuleService {

        public String fallback() {
            return JSON.toJSONString(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "订单服务异常")));
        }

        @Override
        public String addRule(OrderRuleWithBLOBs rule, String type) {
            return fallback();
        }

        @Override
        public String updateRule(OrderRuleWithBLOBs rule, String type) {
            return null;
        }

        @Override
        public String swopPriority(String type, OrderRuleSort swop) {
return null;
        }

        @Override
        public String topOrTailPriority(String type, OrderRuleSort sort) {
return null;
        }

        @Override
        public String delete(String type, Long id) {
return null;
        }

        @Override
        public String queryRuleList(String type, OrderRule rule) {
            return fallback();
        }

        @Override
        public String queryRuleById(String type, Long id) {
            return null;
        }

        @Override
        public String addSkuMaps(List<SellerSkuMap> maps) {
            return null;
        }

        @Override
        public String addSkuMapsWhitTask(List<SellerSkuMap> maps, String key) {
            return null;
        }

        @Override
        public String updateSkuMap(SellerSkuMap map) {
return null;
        }

        @Override
        public String queryMaps( String page,  String row, String startCreateTime, String endCreateTime,
                               Integer status , String plSku,String platformSku,String sellerPlAccount,
                                 String sellerSelfAccount) {
            return null;
        }

        @Override
        public String deleteMap(Long id) {
            return null;
        }

		@Override
		public String checkAmazonTokenIsValid(String sellerId, String marketplaceId, String mwsAuthToken) {
			return null;
		}
        @Override
        public  String getSellerSkuMapByPlatformSku(String platform, String authorizationId, String platformSku, String sellerId){
            return null;
        }

		@Override
		public String GetRate(String soStr, String toStr) {
			return null;
		}
    }

}
