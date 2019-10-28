package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.orderRule.OrderRule;
import com.rondaful.cloud.order.entity.orderRule.OrderRuleSort;
import com.rondaful.cloud.order.entity.orderRule.OrderRuleWithBLOBs;
import com.rondaful.cloud.order.model.SellerRuleModel;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;

import java.util.List;

public interface IOrderRuleService {

    /**
     * 新增分配邮寄方式规则
     *
     * @param rule 订单邮寄方式规则
     * @return 规则ie
     */
    String insertMail(OrderRuleWithBLOBs rule);

    /**
     * 新增分配发货仓库规则
     *
     * @param rule 订单发货仓库规则
     * @return 规则id
     */
    String insertWarehouse(OrderRuleWithBLOBs rule);

    /**
     * 更新分配邮寄方式规则
     *
     * @param rule 订单邮寄方式规则
     */
    void updateMail(OrderRuleWithBLOBs rule);

    /**
     * 更新分配发货仓库规则
     *
     * @param rule 订单发货仓库规则
     */
    void updateWarehouse(OrderRuleWithBLOBs rule);


    /**
     * 更新分配邮寄方式规则转态
     *
     * @param rule 订单邮寄方式规则
     */
    void updateMailStatus(OrderRuleWithBLOBs rule);

    /**
     * 更新分配发货仓库规则状态
     *
     * @param rule 订单发货仓库规则
     */
    void updateWarehouseStatus(OrderRuleWithBLOBs rule);


    /**
     * 交换两个规则的优先级
     *
     * @param swop 交换数据分装对象
     * @param type 规则类型
     */
    void swopPriority(OrderRuleSort swop, String type);

    /**
     * 置顶或者置底规则优先级
     *
     * @param type 订单规则类型[mail:订单邮寄方式 warehouse:订单发货仓库 ]
     * @param sort 排序对象
     */
    void topOrTailPriority(String type, OrderRuleSort sort);


    /**
     * 上移或下移规则优先级
     *
     * @param type 订单规则类型[mail:订单邮寄方式 warehouse:订单发货仓库 ]
     * @param sort 排序对象
     */
    void upOrDownPriority(String type, OrderRuleSort sort);

    /**
     * 查询订单邮寄方式规则列表
     *
     * @param rule 订单邮寄规则
     * @return 订单邮寄规则列表
     */
    List<OrderRuleWithBLOBs> mailList(OrderRule rule);

    /**
     * 查询订单发货仓库规则列表
     *
     * @param rule 订单发货仓库规则
     * @return 订单发货仓库规则列表 [mail:订单邮寄方式 warehouse:订单发货仓库 ]
     */
    List<OrderRuleWithBLOBs> warehouseList(OrderRule rule);

    /**
     * 删除订单规则
     *
     * @param id   订单规则id
     * @param type 订单规则类型[mail:订单邮寄方式 warehouse:订单发货仓库 ]
     */
    void deleteRule(Long id, String type);

    /**
     * 根据id（主键）查询订单规则
     *
     * @param id   id
     * @param type 规则类型
     * @return 规则
     */
    OrderRuleWithBLOBs selectByPrimaryKey(Long id, String type);

    /**
     * 根据 发货仓库id 和 邮寄方式id 废弃对应的订单规则
     * 当两个参数都传入后废弃 对应邮寄方式作为结果，对应仓库作为条件的规则
     * 当只传入一个参数时废弃相关规则
     *
     * @param deliveryWarehouseId 相关的发货仓库id
     * @param mailTypeId          相关的邮寄方式id
     */
    void discardOrderRule(String deliveryWarehouseId, String mailTypeId);

    /**
     * 匹配订单的发货规则。
     * @param sellerAccount 品连卖家账号
     * @param platform 订单所属平台 [Amazon, eBay, wish, aliexpress]
     * @param account 订单所属账号id（卖家在平台的账号id）
     * @param receiveGoodsCountry 收货国家，使用国家国际简写[USA  DE  UK]
     * @param receiveGoodsZipCode 收货邮编
     * @param skus 订单在品连的sku 列表
     * @param order 订单
     * @param price 订单的总价格 RMB
     * @param weight 订单的总重量 g
     * @param volume 订单的总体积 m³
     * @return 返回map结构， 键值  "分配邮寄方式"："mail"  "分配发货仓库"："warehouse"
     */
 /*    Map<String,String> mappingOrderRule(String sellerAccount, String platform, String account, String receiveGoodsCountry,
                                         String receiveGoodsZipCode, List<String> skus, SysOrder order, BigDecimal price, BigDecimal weight, BigDecimal volume);*/

//    /**
//     * 匹配订单的发货规则
//     *
//     * @param platform 订单所属平台 [Amazon, eBay, wish, aliexpress]
//     * @param order    订单
//     */
//    void mappingOrderRule(String platform, SysOrder order);


    /**
     * 匹配订单发货规则
     *
     * @param platform 订单所属平台 [Amazon, eBay, wish, aliexpress]
     * @param order    订单
     */
    void mappingOrderRuleNew(String platform, SysOrder order);

    /**
     * 包裹匹配仓库物流规则
     *
     * @param platform           平台
     * @param sysOrderPackageDTO {@link SysOrderPackageDTO}
     * @param site               亚马逊平台才有，其余的传null
     * @param empowerId          店铺账号ID
     */
    void mappingPackageOrderRule(String platform, SysOrderPackageDTO sysOrderPackageDTO, String site,
                                 Integer empowerId);

    /**
     * 针对新版本需求加以区分是public还是seller
     * @param ruleType 规则类型 公共类型：public 卖家类型seller
     * @param rule 参数承载体
     * @param type 区分是查询仓库还是物流
     * @return
     */
    List<OrderRuleWithBLOBs> queryRuleList(String ruleType, String type,OrderRule rule);
}
