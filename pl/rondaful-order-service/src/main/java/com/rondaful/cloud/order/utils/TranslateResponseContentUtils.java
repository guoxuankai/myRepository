package com.rondaful.cloud.order.utils;

import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.entity.SysOrderLog;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-03-22 16:54
 * 包名: com.rondaful.cloud.order.utils
 * 描述: 翻译响应内容工具类
 * */
public class TranslateResponseContentUtils {
    /**
     * 翻译操作日志
     * @param sysOrderLog
     * @return 翻译后的内容
     */
    public static String getTranslateContent(SysOrderLog sysOrderLog) {
        return sysOrderLog.getContent().
                replaceAll("新建订单", com.rondaful.cloud.common.utils.Utils.translation("新建订单")).
                replaceAll("编辑发货仓库变更为", com.rondaful.cloud.common.utils.Utils.translation("编辑发货仓库变更为")).
                replaceAll("；邮寄方式变更为", com.rondaful.cloud.common.utils.Utils.translation("；邮寄方式变更为")).
                replaceAll("订单规则自动匹配发货仓库", com.rondaful.cloud.common.utils.Utils.translation("订单规则自动匹配发货仓库")).
                replaceAll("，自动匹配物流方式", com.rondaful.cloud.common.utils.Utils.translation("，自动匹配物流方式")).
                replaceAll("所选仓库对应商品缺货", com.rondaful.cloud.common.utils.Utils.translation("所选仓库对应商品缺货")).
                replaceAll("发货推送成功", com.rondaful.cloud.common.utils.Utils.translation("发货推送成功")).
                replaceAll("发货冻结金额成功", com.rondaful.cloud.common.utils.Utils.translation("发货冻结金额成功")).
                replaceAll("发货冻结金额失败", com.rondaful.cloud.common.utils.Utils.translation("发货冻结金额失败")).
                replaceAll("发货取消冻结金额成功", com.rondaful.cloud.common.utils.Utils.translation("发货取消冻结金额成功")).
                replaceAll("发货取消冻结金额失败", com.rondaful.cloud.common.utils.Utils.translation("发货取消冻结金额失败")).
                replaceAll("发货推送失败，物流方式不支持", com.rondaful.cloud.common.utils.Utils.translation("发货推送失败，物流方式不支持")).
                replaceAll("发货，库存不足", com.rondaful.cloud.common.utils.Utils.translation("发货，库存不足")).
                replaceAll("发货推送失败，未填写仓库或物流方式不支持", com.rondaful.cloud.common.utils.Utils.translation("发货推送失败，未填写仓库或物流方式不支持")).
                replaceAll("合并成功，合并主订单号", com.rondaful.cloud.common.utils.Utils.translation("合并成功，合并主订单号")).
                replaceAll("配货中", com.rondaful.cloud.common.utils.Utils.translation("配货中")).
                replaceAll("已发货，跟踪号", com.rondaful.cloud.common.utils.Utils.translation("已发货，跟踪号")).
                replaceAll("申请退货退款，售后单号", com.rondaful.cloud.common.utils.Utils.translation("申请退货退款，售后单号")).
                replaceAll("申请仅退款，售后单号", com.rondaful.cloud.common.utils.Utils.translation("申请仅退款，售后单号")).
                replaceAll("申请补发货，售后单号", com.rondaful.cloud.common.utils.Utils.translation("申请补发货，售后单号")).
                replaceAll("已退款，售后单号", com.rondaful.cloud.common.utils.Utils.translation("已退款，售后单号")).
                replaceAll("补发货完成，售后单号", com.rondaful.cloud.common.utils.Utils.translation("补发货完成，售后单号")).
                replaceAll("退货退款完成，售后单号", com.rondaful.cloud.common.utils.Utils.translation("退货退款完成，售后单号")).
                replaceAll("已收货", com.rondaful.cloud.common.utils.Utils.translation("已收货")).
                replaceAll("作废", com.rondaful.cloud.common.utils.Utils.translation("作废")).
                replaceAll("已拦截", com.rondaful.cloud.common.utils.Utils.translation("已拦截")).
                replaceAll("拆分成功，拆分子订单号", com.rondaful.cloud.common.utils.Utils.translation("拆分成功，拆分子订单号")).
                replaceAll("和", com.rondaful.cloud.common.utils.Utils.translation("和")).
                replaceAll("已退款，售后单号", com.rondaful.cloud.common.utils.Utils.translation("已退款，售后单号")).
                replaceAll("已退款，售后单号", com.rondaful.cloud.common.utils.Utils.translation("已退款，售后单号")).
                replaceAll("订单", com.rondaful.cloud.common.utils.Utils.translation("订单")).
                replaceAll("111111", com.rondaful.cloud.common.utils.Utils.translation("111111")).
                replaceAll("IB物流", com.rondaful.cloud.common.utils.Utils.translation("IB物流")).
                replaceAll("UBI物流 (利朗达)", com.rondaful.cloud.common.utils.Utils.translation("UBI物流 (利朗达)")).
                replaceAll("vova平台线上物流", com.rondaful.cloud.common.utils.Utils.translation("vova平台线上物流")).
                replaceAll("Wish邮（利朗达）", com.rondaful.cloud.common.utils.Utils.translation("Wish邮（利朗达）")).
                replaceAll("万邑通邮选物流", com.rondaful.cloud.common.utils.Utils.translation("万邑通邮选物流")).
                replaceAll("世航国际", com.rondaful.cloud.common.utils.Utils.translation("世航国际")).
                replaceAll("云途", com.rondaful.cloud.common.utils.Utils.translation("云途")).
                replaceAll("亚太物流", com.rondaful.cloud.common.utils.Utils.translation("亚太物流")).
                replaceAll("代运物流", com.rondaful.cloud.common.utils.Utils.translation("代运物流")).
                replaceAll("创优达国际快递", com.rondaful.cloud.common.utils.Utils.translation("创优达国际快递")).
                replaceAll("国内快递", com.rondaful.cloud.common.utils.Utils.translation("国内快递")).
                replaceAll("国洋运通小包", com.rondaful.cloud.common.utils.Utils.translation("国洋运通小包")).
                replaceAll("易世通达（中邮小包）（深圳）", com.rondaful.cloud.common.utils.Utils.translation("易世通达（中邮小包）（深圳）")).
                replaceAll("易通关物流(利朗达)", com.rondaful.cloud.common.utils.Utils.translation("易通关物流(利朗达)")).
                replaceAll("星邮物流(利朗达)", com.rondaful.cloud.common.utils.Utils.translation("星邮物流(利朗达)")).
                replaceAll("朗智物流", com.rondaful.cloud.common.utils.Utils.translation("朗智物流")).
                replaceAll("淼信物流", com.rondaful.cloud.common.utils.Utils.translation("淼信物流")).
                replaceAll("燕文物流", com.rondaful.cloud.common.utils.Utils.translation("燕文物流")).
                replaceAll("趣物流", com.rondaful.cloud.common.utils.Utils.translation("趣物流")).
                replaceAll("速卖通线上发货", com.rondaful.cloud.common.utils.Utils.translation("速卖通线上发货")).
                replaceAll("顺丰国际", com.rondaful.cloud.common.utils.Utils.translation("顺丰国际")).
                replaceAll("顺丰速运俄罗斯流向(中山)", com.rondaful.cloud.common.utils.Utils.translation("顺丰速运俄罗斯流向(中山)")).
                replaceAll("顺友物流", com.rondaful.cloud.common.utils.Utils.translation("顺友物流")).
                replaceAll("Daraz平台线上物流", com.rondaful.cloud.common.utils.Utils.translation("Daraz平台线上物流")).
                replaceAll("DHL eCommerce 电子商务(上海-敏感货)", com.rondaful.cloud.common.utils.Utils.translation("DHL eCommerce 电子商务(上海-敏感货)")).
                replaceAll("DHL eCommerce 电子商务(上海-普货)", com.rondaful.cloud.common.utils.Utils.translation("DHL eCommerce 电子商务(上海-普货)")).
                replaceAll("DHL快递（中山）", com.rondaful.cloud.common.utils.Utils.translation("DHL快递（中山）")).
                replaceAll("eBay亚太物流（中山）", com.rondaful.cloud.common.utils.Utils.translation("eBay亚太物流（中山）")).
                replaceAll("eBay亚太物流（金华）", com.rondaful.cloud.common.utils.Utils.translation("eBay亚太物流（金华）")).
                replaceAll("eDIS物流（中山）", com.rondaful.cloud.common.utils.Utils.translation("eDIS物流（中山）")).
                replaceAll("eDIS物流（金华）", com.rondaful.cloud.common.utils.Utils.translation("eDIS物流（金华）")).
                replaceAll("Gati物流（印度专线）", com.rondaful.cloud.common.utils.Utils.translation("Gati物流（印度专线）")).
                replaceAll("joom平台专用物流", com.rondaful.cloud.common.utils.Utils.translation("joom平台专用物流")).
                replaceAll("Jumia平台线上专用物流", com.rondaful.cloud.common.utils.Utils.translation("Jumia平台线上专用物流")).
                replaceAll("Lazada官方LGS专用物流", com.rondaful.cloud.common.utils.Utils.translation("Lazada官方LGS专用物流")).
                replaceAll("mymall平台专用物流", com.rondaful.cloud.common.utils.Utils.translation("mymall平台专用物流")).
                replaceAll("shopee平台专用物流", com.rondaful.cloud.common.utils.Utils.translation("shopee平台专用物流")).
                replaceAll("UBI", com.rondaful.cloud.common.utils.Utils.translation("UBI")).
                replaceAll("vova平台线上物流", com.rondaful.cloud.common.utils.Utils.translation("vova平台线上物流")).
                replaceAll("Wish邮(金华)", com.rondaful.cloud.common.utils.Utils.translation("Wish邮(金华)")).
                replaceAll("Wish邮（利朗达）", com.rondaful.cloud.common.utils.Utils.translation("Wish邮（利朗达）")).
                replaceAll("Yandex平台专用物流", com.rondaful.cloud.common.utils.Utils.translation("Yandex平台专用物流")).
                replaceAll("zoodmall专用物流", com.rondaful.cloud.common.utils.Utils.translation("zoodmall专用物流")).
                replaceAll("万邑通物流（中山）", com.rondaful.cloud.common.utils.Utils.translation("万邑通物流（中山）")).
                replaceAll("万邑通物流（金华）", com.rondaful.cloud.common.utils.Utils.translation("万邑通物流（金华）")).
                replaceAll("世航国际", com.rondaful.cloud.common.utils.Utils.translation("世航国际")).
                replaceAll("中山邮政", com.rondaful.cloud.common.utils.Utils.translation("中山邮政")).
                replaceAll("中邮shopee物流", com.rondaful.cloud.common.utils.Utils.translation("中邮shopee物流")).
                replaceAll("中邮小包带电", com.rondaful.cloud.common.utils.Utils.translation("中邮小包带电")).
                replaceAll("中邮小包（金华）", com.rondaful.cloud.common.utils.Utils.translation("中邮小包（金华）")).
                replaceAll("义达物流（日本专线渠道）", com.rondaful.cloud.common.utils.Utils.translation("义达物流（日本专线渠道）")).
                replaceAll("乐天国际", com.rondaful.cloud.common.utils.Utils.translation("乐天国际")).
                replaceAll("云途", com.rondaful.cloud.common.utils.Utils.translation("云途")).
                replaceAll("俄航达", com.rondaful.cloud.common.utils.Utils.translation("俄航达")).
                replaceAll("创优达国际快递", com.rondaful.cloud.common.utils.Utils.translation("创优达国际快递")).
                replaceAll("国内快递", com.rondaful.cloud.common.utils.Utils.translation("国内快递")).
                replaceAll("国洋运通（台湾小包渠道）", com.rondaful.cloud.common.utils.Utils.translation("国洋运通（台湾小包渠道）")).
                replaceAll("快系统（纯电）", com.rondaful.cloud.common.utils.Utils.translation("快系统（纯电）")).
                replaceAll("急速物流", com.rondaful.cloud.common.utils.Utils.translation("急速物流")).
                replaceAll("惠优物流", com.rondaful.cloud.common.utils.Utils.translation("惠优物流")).
                replaceAll("朗智物流", com.rondaful.cloud.common.utils.Utils.translation("朗智物流")).
                replaceAll("淼信物流（中山仓）", com.rondaful.cloud.common.utils.Utils.translation("淼信物流（中山仓）")).
                replaceAll("淼信物流（金华仓）", com.rondaful.cloud.common.utils.Utils.translation("淼信物流（金华仓）")).
                replaceAll("燕文物流（中山）", com.rondaful.cloud.common.utils.Utils.translation("燕文物流（中山）")).
                replaceAll("燕文物流（金华）", com.rondaful.cloud.common.utils.Utils.translation("燕文物流（金华）")).
                replaceAll("线下E邮宝（东莞）", com.rondaful.cloud.common.utils.Utils.translation("线下E邮宝（东莞）")).
                replaceAll("线下E邮宝（金华）", com.rondaful.cloud.common.utils.Utils.translation("线下E邮宝（金华）")).
                replaceAll("腾嘉物流", com.rondaful.cloud.common.utils.Utils.translation("腾嘉物流")).
                replaceAll("趣物流（老挝小包渠道）", com.rondaful.cloud.common.utils.Utils.translation("趣物流（老挝小包渠道）")).
                replaceAll("递四方货代", com.rondaful.cloud.common.utils.Utils.translation("递四方货代")).
                replaceAll("速卖通线上发货", com.rondaful.cloud.common.utils.Utils.translation("速卖通线上发货")).
                replaceAll("长沙E邮宝（新账号）", com.rondaful.cloud.common.utils.Utils.translation("长沙E邮宝（新账号）")).
                replaceAll("韵达快递", com.rondaful.cloud.common.utils.Utils.translation("韵达快递")).
                replaceAll("顺丰俄罗斯专线(金华) ", com.rondaful.cloud.common.utils.Utils.translation("顺丰俄罗斯专线(金华) ")).
                replaceAll("顺丰国际(中山)", com.rondaful.cloud.common.utils.Utils.translation("顺丰国际(中山)")).
                replaceAll("顺友物流 ", com.rondaful.cloud.common.utils.Utils.translation("顺友物流 ")).
                replaceAll("飞特物流", com.rondaful.cloud.common.utils.Utils.translation("飞特物流")).
                replaceAll("7777", com.rondaful.cloud.common.utils.Utils.translation("7777")).
                replaceAll("7878787", com.rondaful.cloud.common.utils.Utils.translation("7878787")).
                replaceAll("中山仓", com.rondaful.cloud.common.utils.Utils.translation("中山仓")).
                replaceAll("测试仓库", com.rondaful.cloud.common.utils.Utils.translation("测试仓库")).
                replaceAll("金华仓", com.rondaful.cloud.common.utils.Utils.translation("金华仓")).
                replaceAll("美国虚拟仓", Utils.translation("美国虚拟仓"));
    }
    /**
     * description: 翻译手动同步订单响应的数据
      * @Param: string  需要翻译的内容
     * @return    翻译后的内容
     * create by wujiachuang
     */
    public static String getTranslateContent(String string) {
        return string.
                replaceAll("天", com.rondaful.cloud.common.utils.Utils.translation("天")).
                replaceAll("小时", com.rondaful.cloud.common.utils.Utils.translation("小时")).
                replaceAll("分", com.rondaful.cloud.common.utils.Utils.translation("分")).
                replaceAll("秒", com.rondaful.cloud.common.utils.Utils.translation("秒")).
                replaceAll("距离下次可同步时间还有：", com.rondaful.cloud.common.utils.Utils.translation("距离下次可同步时间还有：")).
                replaceAll("，请稍后重试！", com.rondaful.cloud.common.utils.Utils.translation("，请稍后重试！"));
    }
}
