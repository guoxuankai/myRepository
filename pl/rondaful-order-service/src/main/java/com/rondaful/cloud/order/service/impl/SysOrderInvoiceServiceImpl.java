package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.entity.CountryCode;
import com.rondaful.cloud.order.entity.SysOrderInvoice;
import com.rondaful.cloud.order.entity.aliexpress.AliExpressOrderInfoDTO;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrder;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.mapper.SysOrderInvoiceMapper;
import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceExportInfoVO;
import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceExportSkuDetailVO;
import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.vo.sysOrderInvoice.SysOrderInvoiceVO;
import com.rondaful.cloud.order.service.IAmazonOrderService;
import com.rondaful.cloud.order.service.ICountryCodeService;
import com.rondaful.cloud.order.service.ISyncEbayOrderService;
import com.rondaful.cloud.order.service.ISysOrderInvoiceService;
import com.rondaful.cloud.order.service.ISystemOrderService;
import com.rondaful.cloud.order.service.aliexpress.IAliexpressOrderService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.FreeMarkerUtil;
import com.rondaful.cloud.order.utils.WKHtmlToPdfUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 系统订单发票业务功能
 *
 * @author Blade
 * @date 2019-06-17 17:25:17
 **/
@Service
public class SysOrderInvoiceServiceImpl implements ISysOrderInvoiceService {

    private static Logger LOGGER = LoggerFactory.getLogger(SysOrderInvoiceServiceImpl.class);

    @Autowired
    private SysOrderInvoiceMapper sysOrderInvoiceMapper;

    @Autowired
    private IAmazonOrderService amazonOrderService;

    @Autowired
    private ISyncEbayOrderService syncEbayOrderService;

    @Autowired
    private IAliexpressOrderService aliexpressOrderService;

    @Autowired
    private ICountryCodeService countryCodeService;

    @Autowired
    private ISystemOrderService systemOrderService;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Value("${invoice.html.path}")
    private String invoiceHtmlPath;

    @Value("${invoice.pdf.path}")
    private String invoicePdfPath;

    @Override
    public void insertOrUpdateSysOrderInvoice(SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO) {
        SysOrderInvoice sysOrderInvoice = new SysOrderInvoice();
        BeanUtils.copyProperties(sysOrderInvoiceInsertOrUpdateDTO, sysOrderInvoice);
        sysOrderInvoiceMapper.insertOrUpdateSelective(sysOrderInvoice);
    }

    public SysOrderInvoiceVO getSysOrderInvoiceBySysOrderId(String sysOrderId) {
        return sysOrderInvoiceMapper.selectBySysOrderId(sysOrderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public String exportPDF(String name, SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO, String sysOrderId) throws Exception {
        String ftlType = "";
        try {
            if (Objects.equals("DE", sysOrderInvoiceInsertOrUpdateDTO.getExportLanguage())) {
                ftlType = "InvoiceDE.ftl";
            } else {
                ftlType = "InvoiceEN.ftl";
            }
            SysOrderInvoiceExportInfoVO sysOrderInvoiceExportInfoVO = assembleInvoiceInfo(sysOrderId, sysOrderInvoiceInsertOrUpdateDTO);

            Date now = new Date();
            sysOrderInvoiceExportInfoVO.setDate(DateUtils.formatDate(now, "yyyy/MM/dd"));
            sysOrderInvoiceExportInfoVO.setCreateDate(DateUtils.formatDate(now, DateUtils.FORMAT_1));
            LOGGER.error("sysOrderInvoiceExportInfoVO={}", FastJsonUtils.toJsonString(sysOrderInvoiceExportInfoVO));
            this.htmlToPdf(ftlType, sysOrderInvoiceExportInfoVO);
        }catch (GlobalException e){
            throw e;
        }catch (Exception e) {
            LOGGER.error("订单导出发票PDF异常", e);
            ftlType = "ErrorMsg.ftl";
            Map<String, String> map = new HashMap<>();
            map.put("msg", e.getMessage() + "");
            this.htmlToPdf(ftlType, map);
        }
        sysOrderInvoiceInsertOrUpdateDTO.setSysOrderId(sysOrderId);
        this.insertOrUpdateSysOrderInvoice(sysOrderInvoiceInsertOrUpdateDTO);
        return invoicePdfPath;
    }

    /**
     * html 转换成 PDF
     *
     * @param ftlFileName freemarker文件名
     * @param object      填充的内容
     */
    private void htmlToPdf(String ftlFileName, Object object) {
        String content = FreeMarkerUtil.getContent(ftlFileName, object);
        // 写成HTML
        WKHtmlToPdfUtil.strToHtmlFile(content, invoiceHtmlPath);
        // 转成PDF
        WKHtmlToPdfUtil.htmlToPdf(invoiceHtmlPath, invoicePdfPath);
    }

    /**
     * 组装发票内容信息
     *
     * @param sysOrderId                       系统订单ID
     * @param sysOrderInvoiceInsertOrUpdateDTO {@link SysOrderInvoiceInsertOrUpdateDTO}
     * @return {@link SysOrderInvoiceExportInfoVO}
     */
    private SysOrderInvoiceExportInfoVO assembleInvoiceInfo(String sysOrderId, SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO) {
        SysOrderNew sysOrderNew = systemOrderService.getSysOrderNew(sysOrderId);

        if (null == sysOrderNew) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单信息不存在，无法导出PDF发票");
        }

        if (StringUtils.isBlank(sysOrderNew.getSourceOrderId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "手动创建的订单不能导出PDF发票");
        }

        LOGGER.error("sysOrderNew={}", FastJsonUtils.toJsonString(sysOrderNew));
        LOGGER.error("sysOrderInvoiceInsertOrUpdateDTO={}", FastJsonUtils.toJsonString(sysOrderInvoiceInsertOrUpdateDTO));

        SysOrderInvoiceExportInfoVO sysOrderInvoiceExportInfoVO = new SysOrderInvoiceExportInfoVO();
        BeanUtils.copyProperties(sysOrderInvoiceInsertOrUpdateDTO, sysOrderInvoiceExportInfoVO);
        BeanUtils.copyProperties(sysOrderNew, sysOrderInvoiceExportInfoVO);
        SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderNew.getSysOrderReceiveAddress();
//        sysOrderReceiveAddress.setShipToCountryName(sysOrderReceiveAddress.getShipToCity());
//        sysOrderReceiveAddress.setShipToCity(sysOrderReceiveAddress.getShipToCountryName());
        BeanUtils.copyProperties(sysOrderReceiveAddress, sysOrderInvoiceExportInfoVO);

        CountryCode countryCode = countryCodeService.findCountryByISO(sysOrderInvoiceExportInfoVO.getCountryCode());
        sysOrderInvoiceExportInfoVO.setCountryEnName(countryCode.getNicename());

        List<SysOrderInvoiceExportSkuDetailVO> sysOrderInvoiceExportSkuDetailList = new ArrayList<>();
        // 来源订单ID
        String sourceOrderId = sysOrderNew.getSourceOrderId();

        // 根据订单来源，组装订单信息
        Byte orderSource = sysOrderNew.getOrderSource();
        if (Objects.equals(orderSource, OrderSourceEnum.CONVER_FROM_AMAZON.getValue())) {
            sysOrderInvoiceExportInfoVO = assembleInvoiceInfoFromAmazonOrder(sysOrderInvoiceExportInfoVO,
                    sysOrderInvoiceExportSkuDetailList, sysOrderInvoiceInsertOrUpdateDTO, sourceOrderId);
        } else if (Objects.equals(orderSource, OrderSourceEnum.CONVER_FROM_EBAY.getValue())) {
            sysOrderInvoiceExportInfoVO = assembleInvoiceInfoFromEbayOrder(sysOrderInvoiceExportInfoVO,
                    sysOrderInvoiceExportSkuDetailList, sysOrderInvoiceInsertOrUpdateDTO, sourceOrderId);
        } else if (Objects.equals(orderSource, OrderSourceEnum.CONVER_FROM_ALIEXPRESS.getValue())) {
            sysOrderInvoiceExportInfoVO = assembleInvoiceInfoFromAliExpressOrder(sysOrderInvoiceExportInfoVO,
                    sysOrderInvoiceExportSkuDetailList, sysOrderInvoiceInsertOrUpdateDTO, sourceOrderId);
        }

        LOGGER.info("sysOrderInvoiceExportInfoVO={}", FastJsonUtils.toJsonString(sysOrderInvoiceExportInfoVO));
        return sysOrderInvoiceExportInfoVO;
    }

    /**
     * 获取税费
     *
     * @param vatTaxRate 税率
     * @param calFees    计算金额数
     * @return {@link BigDecimal} tax fee
     */
    private BigDecimal getTaxFee(BigDecimal vatTaxRate, BigDecimal... calFees) {
        BigDecimal calFee = new BigDecimal(0);
        for (BigDecimal fee : calFees) {
            calFee = calFee.add(fee);
        }
        BigDecimal rate = vatTaxRate.divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
        return calFee.multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
    }

    /**
     * 价格转换成USD价格
     *
     * @param sourcePrice  价格
     * @param exchangeRate 汇率
     * @return {@link BigDecimal} USD price
     */
    private BigDecimal priceToUSDPrice(BigDecimal sourcePrice, BigDecimal exchangeRate) {
        return sourcePrice.multiply(exchangeRate);
    }

    /**
     * 组装亚马逊订单信息
     *
     * @param sysOrderInvoiceExportInfoVO        {@link SysOrderInvoiceExportInfoVO}
     * @param sysOrderInvoiceExportSkuDetailList {@link SysOrderInvoiceExportSkuDetailVO}
     * @param sysOrderInvoiceInsertOrUpdateDTO   {@link SysOrderInvoiceInsertOrUpdateDTO}
     * @param sourceOrderId                      平台订单ID
     * @return {@link SysOrderInvoiceExportInfoVO}
     */
    private SysOrderInvoiceExportInfoVO assembleInvoiceInfoFromAmazonOrder(SysOrderInvoiceExportInfoVO sysOrderInvoiceExportInfoVO,
                                                                           List<SysOrderInvoiceExportSkuDetailVO> sysOrderInvoiceExportSkuDetailList,
                                                                           SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO,
                                                                           String sourceOrderId) {
        // 从亚马逊订单获取对应的单价和数量
        AmazonOrder amazonOrder = amazonOrderService.findAmazonOrderByOrderId(sourceOrderId);

        if (null == amazonOrder) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该亚马逊订单原始订单信息不存在，无法导出PDF发票");
        }
        if (amazonOrder.getAmazonOrderDetails()==null||amazonOrder.getAmazonOrderDetails().isEmpty()){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该亚马逊订单原始订单下未包含任何的商品列表信息，不构成开具发票的条件，不予受理发票的开局，亲完善你的订单商品详情");
        }

        List<AmazonOrderDetail> amazonOrderDetails = amazonOrder.getAmazonOrderDetails();

        // 支付币种兑USD的汇率
        BigDecimal exchangeRate = new BigDecimal(amazonOrder.getPlExchangeRate());

        // 商品总价
        BigDecimal subTotal = new BigDecimal(0);
        // 运费
        BigDecimal shippingFee = new BigDecimal(0);

        for (AmazonOrderDetail amazonOrderDetail : amazonOrderDetails) {
            SysOrderInvoiceExportSkuDetailVO sysOrderInvoiceExportSkuDetailVO = new SysOrderInvoiceExportSkuDetailVO();
            // 商品单价
            BigDecimal itemPrice = priceToUSDPrice(amazonOrderDetail.getItemPrice(), exchangeRate).setScale(2, BigDecimal.ROUND_DOWN);
            Integer quantity = amazonOrderDetail.getQuantity();

            sysOrderInvoiceExportSkuDetailVO.setItemPrice(itemPrice);
            sysOrderInvoiceExportSkuDetailVO.setSkuQuantity(quantity);
            sysOrderInvoiceExportSkuDetailVO.setItemNameEn(amazonOrderDetail.getItemTitle());
            sysOrderInvoiceExportSkuDetailVO.setSourceOrderId(sourceOrderId);
            sysOrderInvoiceExportSkuDetailVO.setSku(amazonOrderDetail.getPlatformSku());

            // 运费
            BigDecimal shippingPrice = amazonOrderDetail.getShippingPrice();
            shippingFee = shippingFee.add(priceToUSDPrice(shippingPrice, exchangeRate).setScale(2, BigDecimal.ROUND_DOWN));

            // 单个商品总价
            BigDecimal total = itemPrice.multiply(new BigDecimal(quantity));

            subTotal = subTotal.add(total);

            sysOrderInvoiceExportSkuDetailVO.setTotal(total);
            sysOrderInvoiceExportSkuDetailList.add(sysOrderInvoiceExportSkuDetailVO);
        }

        sysOrderInvoiceExportInfoVO = calculateInvoiceFee(sysOrderInvoiceExportInfoVO, subTotal, shippingFee,
                sysOrderInvoiceInsertOrUpdateDTO.getVatTaxRate(), sysOrderInvoiceInsertOrUpdateDTO.getVatTaxType());

        sysOrderInvoiceExportInfoVO.setSysOrderInvoiceExportSkuDetailList(sysOrderInvoiceExportSkuDetailList);

        return sysOrderInvoiceExportInfoVO;
    }

    /**
     * 组装ebay订单信息
     *
     * @param sysOrderInvoiceExportInfoVO        {@link SysOrderInvoiceExportInfoVO}
     * @param sysOrderInvoiceExportSkuDetailList {@link SysOrderInvoiceExportSkuDetailVO}
     * @param sysOrderInvoiceInsertOrUpdateDTO   {@link SysOrderInvoiceInsertOrUpdateDTO}
     * @param sourceOrderId                      平台订单ID
     * @return {@link SysOrderInvoiceExportInfoVO}
     */
    private SysOrderInvoiceExportInfoVO assembleInvoiceInfoFromEbayOrder(SysOrderInvoiceExportInfoVO sysOrderInvoiceExportInfoVO,
                                                                         List<SysOrderInvoiceExportSkuDetailVO> sysOrderInvoiceExportSkuDetailList,
                                                                         SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO,
                                                                         String sourceOrderId) {

        EbayOrder ebayOrder = syncEbayOrderService.queryEbayOrderDetail(sourceOrderId);

        if (null == ebayOrder) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该ebay订单原始订单信息不存在，无法导出PDF发票");
        }

        if (ebayOrder.getEbayOrderDetails()==null||ebayOrder.getEbayOrderDetails().isEmpty()){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该eBay订单原始订单下未包含任何的商品列表信息，不构成开具发票的条件，不予受理发票的开局，亲完善你的订单商品详情");
        }

        // 所有商品总价
        BigDecimal subTotal = new BigDecimal(0);

        // 获取当时的汇率
        String ebayAmountPaid = ebayOrder.getAmountPaid();
        String[] ebayAmountPaidData = StringUtils.split(ebayAmountPaid, "#");
        BigDecimal exchangeRate = new BigDecimal(ebayAmountPaidData[2]);

        // 获取物流费用
        String shippingServiceCost = ebayOrder.getShippingServiceCost();
        String[] shippingServiceCostData = StringUtils.split(shippingServiceCost, "#");
        BigDecimal shippingFee = priceToUSDPrice(new BigDecimal(shippingServiceCostData[0]), exchangeRate).setScale(2, BigDecimal.ROUND_DOWN);

        List<EbayOrderDetail> ebayOrderDetailList = ebayOrder.getEbayOrderDetails();

        for (EbayOrderDetail ebayOrderDetail : ebayOrderDetailList) {
            SysOrderInvoiceExportSkuDetailVO sysOrderInvoiceExportSkuDetailVO = new SysOrderInvoiceExportSkuDetailVO();
            // ebay 商品单价  格式为 : 单价 # 支付货币单位 # 转USD的汇率
            String ebayOrderItemAmount = ebayOrderDetail.getTransactionPrice();
            String[] ebayOrderItemAmountData = StringUtils.split(ebayOrderItemAmount, "#");
            BigDecimal orderItemAmount = new BigDecimal(ebayOrderItemAmountData[0]);
            BigDecimal itemPrice = priceToUSDPrice(orderItemAmount, exchangeRate).setScale(2, BigDecimal.ROUND_DOWN);
            Integer quantity = ebayOrderDetail.getQuantityPurchased();

            sysOrderInvoiceExportSkuDetailVO.setItemPrice(itemPrice);
            sysOrderInvoiceExportSkuDetailVO.setSkuQuantity(quantity);
            sysOrderInvoiceExportSkuDetailVO.setItemNameEn(ebayOrderDetail.getItemTitle());
            sysOrderInvoiceExportSkuDetailVO.setSourceOrderId(sourceOrderId);
            sysOrderInvoiceExportSkuDetailVO.setSku(ebayOrderDetail.getVariationSku());

            // 单个商品总价
            BigDecimal total = itemPrice.multiply(new BigDecimal(quantity));
            subTotal = subTotal.add(total);

            sysOrderInvoiceExportSkuDetailVO.setTotal(total);
            sysOrderInvoiceExportSkuDetailList.add(sysOrderInvoiceExportSkuDetailVO);
        }

        sysOrderInvoiceExportInfoVO = calculateInvoiceFee(sysOrderInvoiceExportInfoVO, subTotal, shippingFee,
                sysOrderInvoiceInsertOrUpdateDTO.getVatTaxRate(), sysOrderInvoiceInsertOrUpdateDTO.getVatTaxType());

        sysOrderInvoiceExportInfoVO.setSysOrderInvoiceExportSkuDetailList(sysOrderInvoiceExportSkuDetailList);

        return sysOrderInvoiceExportInfoVO;
    }

    /**
     * 组装速卖通订单信息
     *
     * @param sysOrderInvoiceExportInfoVO        {@link SysOrderInvoiceExportInfoVO}
     * @param sysOrderInvoiceExportSkuDetailList {@link SysOrderInvoiceExportSkuDetailVO}
     * @param sysOrderInvoiceInsertOrUpdateDTO   {@link SysOrderInvoiceInsertOrUpdateDTO}
     * @param sourceOrderId                      平台订单ID
     * @return {@link SysOrderInvoiceExportInfoVO}
     */
    private SysOrderInvoiceExportInfoVO assembleInvoiceInfoFromAliExpressOrder(SysOrderInvoiceExportInfoVO sysOrderInvoiceExportInfoVO,
                                                                               List<SysOrderInvoiceExportSkuDetailVO> sysOrderInvoiceExportSkuDetailList,
                                                                               SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO,
                                                                               String sourceOrderId) {

        AliExpressOrderInfoDTO aliExpressOrderInfoDTO = aliexpressOrderService.findAliExpressOrderByOrderId(sourceOrderId);
        AliexpressOrder aliexpressOrder = aliExpressOrderInfoDTO.getAliexpressOrder();

        if (null == aliexpressOrder) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该速卖通订单原始订单信息不存在，无法导出PDF发票");
        }
        //新增对没有商品详情列表的订单的发票导出予以阻止
        if (aliExpressOrderInfoDTO.getAliexpressOrderChildList()==null||aliExpressOrderInfoDTO.getAliexpressOrderChildList().isEmpty()){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该速卖通订单下未包含任何商品信息，不予开具发票信息");
        }

        // 所有商品总价
        BigDecimal subTotal = new BigDecimal(0);

        // 获取物流费用
        // 速卖通的费用已经转换成美元
        BigDecimal shippingFee = aliexpressOrder.getLogisticsAmount();

        List<AliexpressOrderChild> aliexpressOrderChildList = aliExpressOrderInfoDTO.getAliexpressOrderChildList();

        for (AliexpressOrderChild aliexpressOrderChild : aliexpressOrderChildList) {
            SysOrderInvoiceExportSkuDetailVO sysOrderInvoiceExportSkuDetailVO = new SysOrderInvoiceExportSkuDetailVO();
            BigDecimal itemPrice = aliexpressOrderChild.getAmount();
            Integer quantity = aliexpressOrderChild.getProductCount();

            sysOrderInvoiceExportSkuDetailVO.setItemPrice(itemPrice);
            sysOrderInvoiceExportSkuDetailVO.setSkuQuantity(quantity);
            sysOrderInvoiceExportSkuDetailVO.setItemNameEn(aliexpressOrderChild.getProductName());
            sysOrderInvoiceExportSkuDetailVO.setSourceOrderId(sourceOrderId);
            sysOrderInvoiceExportSkuDetailVO.setSku(aliexpressOrderChild.getSkuCode());

            // 单个商品总价
            BigDecimal total = itemPrice.multiply(new BigDecimal(quantity));
            subTotal = subTotal.add(total);

            sysOrderInvoiceExportSkuDetailVO.setTotal(total);
            sysOrderInvoiceExportSkuDetailList.add(sysOrderInvoiceExportSkuDetailVO);
        }
        sysOrderInvoiceExportInfoVO = calculateInvoiceFee(sysOrderInvoiceExportInfoVO, subTotal, shippingFee,
                sysOrderInvoiceInsertOrUpdateDTO.getVatTaxRate(), sysOrderInvoiceInsertOrUpdateDTO.getVatTaxType());
        sysOrderInvoiceExportInfoVO.setSysOrderInvoiceExportSkuDetailList(sysOrderInvoiceExportSkuDetailList);

        return sysOrderInvoiceExportInfoVO;
    }

    /**
     * 计算发票所需的金额数据
     *
     * @param sysOrderInvoiceExportInfoVO {@link SysOrderInvoiceExportInfoVO}
     * @param subTotal                    所有商品的总金额
     * @param shippingFee                 运费
     * @param vatTaxRate                  vat税率
     * @param vatTaxType                  vat应税
     * @return {@link SysOrderInvoiceExportInfoVO}
     */
    private SysOrderInvoiceExportInfoVO calculateInvoiceFee(SysOrderInvoiceExportInfoVO sysOrderInvoiceExportInfoVO,
                                                            BigDecimal subTotal, BigDecimal shippingFee, BigDecimal vatTaxRate,
                                                            String vatTaxType) {
        BigDecimal zero = new BigDecimal("0");
        BigDecimal taxFee = new BigDecimal("0");
        if (Objects.equals(vatTaxType, Constants.InvoiceTemplate.VAT_TAX_TYPE_1)) {
            // total 含税
            BigDecimal subTotalTaxFee = getTaxFee(vatTaxRate, subTotal, zero);
            BigDecimal shippingFeeTaxFee = getTaxFee(vatTaxRate, shippingFee, zero);
            sysOrderInvoiceExportInfoVO.setSubTotal(subTotal.subtract(subTotalTaxFee));
            sysOrderInvoiceExportInfoVO.setShippingFee(shippingFee.subtract(shippingFeeTaxFee));
            taxFee = subTotalTaxFee.add(shippingFeeTaxFee);
        } else if (Objects.equals(vatTaxType, Constants.InvoiceTemplate.VAT_TAX_TYPE_2)) {
            // total (1+VAT) 不含税
            taxFee = getTaxFee(vatTaxRate, subTotal, shippingFee);

            sysOrderInvoiceExportInfoVO.setSubTotal(subTotal);
            sysOrderInvoiceExportInfoVO.setShippingFee(shippingFee);
        }
        sysOrderInvoiceExportInfoVO.setTaxFee(taxFee);
        // 暂无其他费用
        BigDecimal otherFee = new BigDecimal(0);
        sysOrderInvoiceExportInfoVO.setOtherFee(otherFee);

        sysOrderInvoiceExportInfoVO.setInvoiceTotal(sysOrderInvoiceExportInfoVO.getSubTotal()
                .add(sysOrderInvoiceExportInfoVO.getShippingFee()).add(taxFee).add(otherFee));

        return sysOrderInvoiceExportInfoVO;
    }

    /**
     * 保存发票内容信息
     *
     * @param sysOrderInvoiceInsertOrUpdateDTO {@link SysOrderInvoiceInsertOrUpdateDTO}
     * @return {@link SysOrderInvoiceExportInfoVO}
     */
    @Override
    public void saveInvoiceInfo(SysOrderInvoiceInsertOrUpdateDTO sysOrderInvoiceInsertOrUpdateDTO) {
        SysOrderNew sysOrderNew = systemOrderService.getSysOrderNew(sysOrderInvoiceInsertOrUpdateDTO.getSysOrderId());

        if (null == sysOrderNew) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单信息不存在，无法保存发票");
        }

        if (StringUtils.isBlank(sysOrderNew.getSourceOrderId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "手动创建的订单不能保存发票");
        }

        String loginName = getLoginUserInformationByToken.getUserDTO().getLoginName();
        sysOrderInvoiceInsertOrUpdateDTO.setCreator(loginName);
        sysOrderInvoiceInsertOrUpdateDTO.setModifier(loginName);

        LOGGER.error("sysOrderInvoiceInsertOrUpdateDTO={}", FastJsonUtils.toJsonString(sysOrderInvoiceInsertOrUpdateDTO));
        this.insertOrUpdateSysOrderInvoice(sysOrderInvoiceInsertOrUpdateDTO);
    }
}
