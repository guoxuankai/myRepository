package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListSku;
import com.rondaful.cloud.common.utils.*;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.*;
import com.rondaful.cloud.order.entity.commodity.CommoditySpec;
import com.rondaful.cloud.order.entity.commodity.SkuInventoryVo;
import com.rondaful.cloud.order.entity.supplier.LogisticsDTO;
import com.rondaful.cloud.order.entity.supplier.OrderInvDTO;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.order.enums.*;
import com.rondaful.cloud.order.mapper.*;
import com.rondaful.cloud.order.model.dto.syncorder.*;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsResultVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsSkuVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsSupplierVO;
import com.rondaful.cloud.order.model.vo.sysorder.ExcelImportLogVO;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.*;
import com.rondaful.cloud.order.thread.ImportOrderThread;
import com.rondaful.cloud.order.utils.CheckOrderUtils;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 用户处理批量导入数据
 *
 * @author liusiying
 */
@Service
public class ImportExcelOrderService implements IImportExcelOrderInterface {
    private final static Logger logger = LoggerFactory.getLogger(SysOrderServiceImpl.class);
    // remote service
    @Autowired
    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private RemoteSupplierService remoteSupplierService;
    @Autowired
    RemoteSellerService remoteSellerService;
    //load service
    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;
    @Autowired
    private SystemOrderCommonServiceImpl orderCommonService;
    @Autowired
    private ISysOrderLogService sysOrderLogService;

    //mapper
    @Autowired
    private SysOrderDetailMapper sysOrderDetailMapper;
    @Autowired
    private SysOrderNewMapper sysOrderNewMapper;
    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;
    @Autowired
    private SysOrderPackageDetailMapper sysOrderPackageDetailMapper;
    @Autowired
    private SysOrderReceiveAddressMapper sysOrderReceiveAddressMapper;
    @Autowired
    private AliFileUtils aliFileUtils;
    @Autowired
    OrderRuleWarehouseMapper orderRuleWarehouseMapper;
    @Autowired
    private SkuMapServiceImpl skuMapService;
    @Autowired
    private ISystemOrderService systemOrderService;

    @Autowired
    private OrderMessageSender orderMessageSender;

    @Autowired
    private ICountryCodeService countryCodeService;

    @Autowired
    private SysOrderServiceImpl sysOrderServiceImpl;


    /**
     * 仅用于测试
     *
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    @Transactional
    @Override
    public Map<String, Object> resolverExcelAndSaveDate(File file) throws IOException, InvalidFormatException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        //1.读取sheets
        XSSFSheet sheet = workbook.getSheetAt(0);
        Map<String, ExcelOrder> orderMap = new TreeMap<>();
        //2. 解析excel
        Map<String, Object> result = resolveSheetToOrder(sheet, orderMap, null, null);
        //3. 将解析出来的订单信息保存至数据库表
        orderMap.values().stream().forEach(sysOrder -> {
            logger.info(String.format("设置前从excel解析得到的订单详情信息: %s", JSON.toJSONString(sysOrder)));
            try {
                String resultStr = remoteSellerService.selectObjectByAccount(null, null, null, sysOrder.getPlatformSellerAccount(), null);
                String data = Utils.returnRemoteResultDataString(resultStr, "调用卖家服务异常");
                List<Empower> empowerList = JSON.parseArray(data, Empower.class);
                if (empowerList == null || CollectionUtils.isEmpty(empowerList)) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, String.format("未获取到授权信息"));
                }
                Integer empowerid = empowerList.get(0).getEmpowerid();
                sysOrder.setPlatformShopId(empowerid);
                String pinlianaccount = empowerList.get(0).getPinlianaccount();
                if (!StringUtils.equalsIgnoreCase(pinlianaccount, sysOrder.getPlatformSellerId())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "品连卖家账号与授权信息不一致");
                }
                sysOrder.setSellerPlId(empowerList.get(0).getPinlianid());
                SysOrderNew orderNew = new SysOrderNew();
                sysOrder.setEmpowerId(empowerid);
                if (!CheckOrderUtils.judgeSkusAbleSaleAndIsPutAway(sysOrder.getSellerPlId(), sysOrder.getSysOrderDetails().stream().map(x -> x.getSku()).collect(Collectors.toList()))) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "含有不可售商品或已下架商品");
                }
                List<SearchLogisticsListSku> plSkuList = new LinkedList<>();
                SysOrderDTO sysOrderDTO = new SysOrderDTO();
                List<SysOrderPackageDTO> sysOrderPackageList = Collections.synchronizedList(new ArrayList<>());
                SysOrderPackageDTO sysOrderPackageDTO = new SysOrderPackageDTO();
                sysOrderPackageDTO.setSysOrderId(sysOrder.getSysOrderId());
                sysOrderPackageDTO.setEmpowerId(sysOrder.getEmpowerId());
                sysOrderPackageDTO.setItemNum(Long.valueOf(String.valueOf(sysOrder.getSysOrderDetails().size())));
                sysOrderPackageDTO.setSellerPlAccount(sysOrder.getSellerPlAccount());
                sysOrderPackageDTO.setSkus(sysOrder.getSkus());

                sysOrderPackageList.add(sysOrderPackageDTO);
                sysOrderDTO.setSysOrderPackageList(sysOrderPackageList);
                skuMapService.mappingLogisticsRule(sysOrder.getPlatform(), empowerList.get(0), sysOrderDTO, plSkuList, null, false);
                setSysOrderItemInfo(sysOrder);//设置订单商品信息
                setOrderData(sysOrder);//设置订单相关数据
                logger.info(String.format("程序设置后订单详情信息: %s", JSON.toJSONString(sysOrder)));
                String loginName = "";
//                String loginName = "admin";
                setOrderPackageInfo(sysOrder, loginName, orderNew, new SysOrderReceiveAddress(), new SysOrderPackage(), new ArrayList<SysOrderPackageDetail>());//设置订单、包裹、包裹详情数据
//                setShipFee(orderNew);//物流费计算(设置每个SKU的卖家和供应商物流费还有订单级别的卖家物流费)
//                setOrderDataAgain(sysOrder, orderNew); //再次设置订单数据（物流费、利润）
                logger.info(String.format("转化后的数据信息: %s", JSON.toJSONString(orderNew)));
                String deliverDeadline = TimeUtil.DateToString2(new Date());
                orderNew.setDeliverDeadline(deliverDeadline);
                orderNew.setOrderTime(deliverDeadline);
                insertBulk(sysOrder, orderNew, null, null);//开启事务将订单、订单详情、包裹、包裹详情插入数据库并添加操作日志
            } catch (Exception e) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());

            }
        });
        List<ExcelImportLogVO> importLogVOS = Collections.synchronizedList(new ArrayList<>());
        //创建日志文件
        String username = "";
        orderMap.entrySet().stream().forEach(entry -> {
            ExcelOrder sysOrder = entry.getValue();
            ExcelImportLogVO excelImportLogVO = new ExcelImportLogVO();
            excelImportLogVO.setOperator(username);
            excelImportLogVO.setOrderID(sysOrder.getSysOrderId());
            excelImportLogVO.setSn(sysOrder.getSn());
            excelImportLogVO.setStatus(sysOrder.isSuccess());
            importLogVOS.add(excelImportLogVO);
        });
        String[] header = {Utils.translation("虚拟订单号"), Utils.translation("采购订单号"), Utils.translation("操作人"), Utils.translation("导入状态")};
        String[] key = {"sn", "orderID", "operator", "status"};
        String[] width = {"3000", "3000", "3000", "3000"};
        Map<String, String[]> map = ExcelUtil.createMap(header, key, width);
        JSONArray array = export(importLogVOS);
        InputStream inputStream = ExcelUtil.fileStream(array, map);
        long millis = System.currentTimeMillis();
        String url = aliFileUtils.saveStreamFile(ConstantAli.BucketType.BUCKET, ConstantAli.FolderType.FILE_FOLDER, String.valueOf(millis), inputStream);
        result.put("logUrl", url);
        //5. 返回结果
        return result;
    }

    /**
     * 读取文件内容并进行解析作业
     *
     * @param workbook excel表格
     * @throws IOException
     */
    //@Transactional
    @Override
    @Async
    public void resolverExcelAndSaveDate(XSSFWorkbook workbook, UserDTO userDTO) throws IOException, InterruptedException {
        String date = DateUtils.formatDate(new Date(), DateUtils.FORMAT_2);
        //1.读取sheets
        XSSFSheet sheet = workbook.getSheetAt(0);

        Map<String, ExcelOrder> orderMap = new TreeMap<>();
        //2. 解析excel
        ConcurrentHashMap<String, ExcelOrderStatisticsDTO> concurrentHashMap = new ConcurrentHashMap();//后台导入时，记录账户对应数量

        Map<String, Object> result = resolveSheetToOrder(sheet, orderMap, userDTO, concurrentHashMap);
        //3. 将解析出来的订单信息保存至数据库表
        Integer successNum = excelDataToDataTableHandler(orderMap, userDTO, concurrentHashMap);

        //通过消息系统通知用户下载导入日志
        String belongSys = String.valueOf(userDTO.getPlatformType());
        String fileName = "订单导入详情";

        try {
            if (userDTO.getPlatformType() == 2){
                logger.error("统计导入数据：{}", JSONObject.toJSONString(concurrentHashMap));
                for (Map.Entry<String, ExcelOrderStatisticsDTO> entry : concurrentHashMap.entrySet()) {
                    String sellerAccount = entry.getKey();
                    ExcelOrderStatisticsDTO orderStatisticsDTO = entry.getValue();
                    Integer totalCount = orderStatisticsDTO.getTotalCount().intValue();
                    Integer successCount = orderStatisticsDTO.getSuccessCount().intValue();
                    Integer failCount = totalCount - successCount;
                    String content  = date +"#"+ totalCount +"#"+ successCount + "#" + failCount;
                    String fileUrl = outputOperateLogFile(orderMap, userDTO, sellerAccount);
                    orderMessageSender.sendOrderForImported(sellerAccount, content, null, "1", fileUrl, fileName);
                }
            } else {
                String userName = userDTO.getLoginName();
                Integer totalNum = 0;
                if (!ObjectUtils.isEmpty(result.get("rowCount")) ){
                    totalNum = Integer.valueOf(result.get("rowCount").toString());
                }
                Integer failNum = totalNum - successNum;
                String content  = date +"#"+ totalNum +"#"+ successNum + "#" + failNum;
                String url = outputOperateLogFile(orderMap, userDTO, null);
                orderMessageSender.sendOrderForImported(userName, content, null, belongSys, url, fileName);
            }
        } catch (Exception e) {
            logger.error("异常：发送导入订单下载日志消息错误");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
        }
    }

    /**
     * 多线程处理数据
     * @param orderMap
     * @param userDTO
     * @throws InterruptedException
     */
    private Integer excelDataToDataTableHandler(Map<String, ExcelOrder> orderMap, UserDTO userDTO, ConcurrentHashMap<String, ExcelOrderStatisticsDTO> concurrentHashMap) throws InterruptedException {
        logger.error("excel文件解析未发生异常,现将开始进入数据保存环节");
        List<ExcelOrder> list = new ArrayList<>();
        orderMap.values().stream().forEach(sysOrder -> {
            if (sysOrder.getSuccess()){
                list.add(sysOrder);
            }

        });
        //计算成功数
        AtomicInteger totalNum = new AtomicInteger(0);

        int count = 20;                    //一个线程处理300条数据
        int listSize = list.size();        //数据集合大小
        int runSize = (listSize/count)+1;  //开启的线程数
        List<ExcelOrder> newlist = null;   //存放每个线程的执行数据
        ExecutorService executor = Executors.newFixedThreadPool(runSize);      //创建一个线程池，数量和开启线程的数量一样
        //创建两个个计数器
        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(runSize);
        //循环创建线程
        for (int i = 0; i < runSize ; i++) {
            //计算每个线程执行的数据
            if((i+1)==runSize){
                int startIndex = (i*count);
                int endIndex = list.size();
                newlist= list.subList(startIndex, endIndex);
            }else{
                int startIndex = (i*count);
                int endIndex = (i+1)*count;
                newlist= list.subList(startIndex, endIndex);
            }
            //线程类
            ImportOrderThread mythead = new ImportOrderThread(newlist,begin,end, userDTO, totalNum, concurrentHashMap);
            executor.execute(mythead);
        }
        begin.countDown();
        end.await();
        //执行完关闭线程池
        executor.shutdown();
        return totalNum.intValue();
    }

    public void fillOrderInfo(ExcelOrder sysOrder, UserDTO userDTO, AtomicInteger toutalNum, ConcurrentHashMap<String, ExcelOrderStatisticsDTO> concurrentHashMap) {
        logger.info(String.format("设置前从excel解析得到的订单详情信息: %s", JSON.toJSONString(sysOrder)));

        try {
            //1.商品可售情况校验
            logger.error("1.商品可售情况校验");
            if (!CheckOrderUtils.judgeSkusAbleSaleAndIsPutAway(sysOrder.getSellerPlId(), sysOrder.getSysOrderDetails().stream().map(x -> x.getSku()).collect(Collectors.toList()))) {
                sysOrder.setErrorMsg("含有不可售商品或已下架商品");
                sysOrder.setIsSuccess(false);
                return;
            }
        } catch (Exception e) {
            sysOrder.setErrorMsg("含有不可售商品或已下架商品");
            sysOrder.setIsSuccess(false);
            logger.error("含有不可售商品或已下架商品");
            return;
        }
        //2.查询订单规则并填充物流与仓库信息
        SysOrderDTO sysOrderDTO = null;
        try {
            sysOrderDTO = FillLogisticsAndWarehousingInfo(sysOrder);
        } catch (Exception e) {
            sysOrder.setErrorMsg("订单规则异常");
            sysOrder.setIsSuccess(false);
            logger.error("订单规则异常");
            return;
        }

        SysOrderNew orderNew = new SysOrderNew();
        //3.设置订单商品信息
        setSysOrderItemInfo(sysOrder);
        //4.设置订单相关数据
        setOrderData(sysOrder);
        logger.info(String.format("程序设置后订单详情信息: %s", JSON.toJSONString(sysOrder)));
        String loginName = userDTO.getLoginName();
        //设置包裹信息
        setOrderPackageInfo(sysOrder, loginName, orderNew, new SysOrderReceiveAddress(), new SysOrderPackage(), new ArrayList<SysOrderPackageDetail>());//设置订单、包裹、包裹详情数据

        logger.info(String.format("转化后的数据信息: %s", JSON.toJSONString(orderNew)));
        //设置其他非空约束字段值
        String deliverDeadline = TimeUtil.DateToString2(new Date());
        orderNew.setDeliverDeadline(deliverDeadline);
        orderNew.setOrderTime(deliverDeadline);

        //设置物流信息
        if (null != sysOrderDTO.getSysOrderPackageList() && sysOrderDTO.getSysOrderPackageList().size() > 0){
            SysOrderPackage sysOrderPackage = orderNew.getSysOrderPackageList().get(0);
            SysOrderPackageDTO sysOrderPackageDTO = sysOrderDTO.getSysOrderPackageList().get(0);
            sysOrderPackage.setDeliveryWarehouseId(sysOrderPackageDTO.getDeliveryWarehouseId());
            sysOrderPackage.setDeliveryWarehouse(sysOrderPackageDTO.getDeliveryWarehouse());
            sysOrderPackage.setDeliveryWarehouseCode(sysOrderPackageDTO.getDeliveryWarehouseCode());
            sysOrderPackage.setDeliveryMethodCode(sysOrderPackageDTO.getDeliveryMethodCode());
            sysOrderPackage.setDeliveryMethod(sysOrderPackageDTO.getDeliveryMethod());
            sysOrderPackage.setShippingCarrierUsed(sysOrderPackageDTO.getShippingCarrierUsed());
            sysOrderPackage.setShippingCarrierUsedCode(sysOrderPackageDTO.getShippingCarrierUsedCode());
            sysOrderPackage.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsStrategy());
            sysOrderPackage.setEbayCarrierName(sysOrderPackageDTO.getEbayCarrierName());
            sysOrderPackage.setAmazonCarrierName(sysOrderPackageDTO.getAmazonCarrierName());
            sysOrderPackage.setAmazonShippingMethod(sysOrderPackageDTO.getAmazonShippingMethod());
        }

        try {
            //存在未关联的平台SKU、未选中仓库、物流等信息不全情况下不展示预估利润、运费、商品价格等
            SysOrderPackage sysOrderPackage = orderNew.getSysOrderPackageList().get(0);
            if (!ObjectUtils.isEmpty(sysOrderPackage.getDeliveryWarehouseId())
                    && StringUtils.isNotBlank(sysOrderPackage.getDeliveryWarehouse())
                    && StringUtils.isNotBlank(sysOrderPackage.getShippingCarrierUsedCode())
                    && StringUtils.isNotBlank(sysOrderPackage.getShippingCarrierUsed())
                    && StringUtils.isNotBlank(sysOrderPackage.getDeliveryMethodCode())
                    && StringUtils.isNotBlank(sysOrderPackage.getDeliveryMethod())) {
                //物流费计算
                sysOrderServiceImpl.setShipFee(orderNew);

                //分仓定价
                BigDecimal orderAmount = BigDecimal.ZERO;
                for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {
                    for (SysOrderPackageDetail sysOrderPackageDetail : orderPackage.getSysOrderPackageDetailList()) {
                        //TODO 分仓定价业务  重新设置商品价格 订单价格 根据仓库ID
                        orderAmount = reSetItemPrice(orderAmount, orderPackage, sysOrderPackageDetail, sysOrderPackageDetail);
                    }
                }
                orderNew.setOrderAmount(orderAmount);
            } else {
                orderNew.setOrderAmount(null);
            }
        } catch (Exception e) {
            logger.info("物流费计算错误");
        }


        //设置总售价
        try {
            this.setGrossMarginAndProfitMargin(orderNew);
        } catch (Exception e) {
            logger.info("设置总售价错误");
        }

        //持久化至数据库中
        try {
            insertBulk(sysOrder, orderNew, userDTO, sysOrderDTO);
        } catch (Exception e) {
            sysOrder.setErrorMsg("持久化数据异常");
            sysOrder.setIsSuccess(false);
            return;
        }
        sysOrder.setIsSuccess(true);
        toutalNum.addAndGet(1);
        if (userDTO.getPlatformType() == 2){
            //计算卖家导入总数
            String sellerPlAccount = orderNew.getSellerPlAccount();
            if (concurrentHashMap.containsKey(sellerPlAccount)){
                ExcelOrderStatisticsDTO orderStatisticsDTO = concurrentHashMap.get(sellerPlAccount);
                orderStatisticsDTO.getSuccessCount().addAndGet(1);
            }
        }
    }

    /**
     * 设置总售价
     *
     * @param sysOrder
     */
    public void setGrossMarginAndProfitMargin(SysOrderNew sysOrder) {
        BigDecimal orderAmount = sysOrder.getOrderAmount();//商品成本
        BigDecimal estimateShipCost = sysOrder.getEstimateShipCost();//预估物流费
        if (orderAmount == null || estimateShipCost == null){
            sysOrder.setTotal(null);
        } else {
            sysOrder.setTotal(sysOrder.getOrderAmount().add(sysOrder.getEstimateShipCost() == null? BigDecimal.ZERO  :sysOrder.getEstimateShipCost())); //设置总售价
        }

    }

    /**
     * 分仓定价
     * @param orderAmount
     * @param sysOrderPackage
     * @param sysOrderPackageDetail
     * @param sysOrderPackageDetailNew
     * @return
     */
    public BigDecimal reSetItemPrice(BigDecimal orderAmount, SysOrderPackage sysOrderPackage, SysOrderPackageDetail sysOrderPackageDetail, SysOrderPackageDetail sysOrderPackageDetailNew) {
        String result = remoteCommodityService.test("1", "1", null, null, null, null,
                sysOrderPackageDetail.getSku(), null, null);
        String data = Utils.returnRemoteResultDataString(result, "调用商品服务异常");
        JSONObject parse1 = (JSONObject) JSONObject.parse(data);
        String pageInfo = parse1.getString("pageInfo");
        JSONObject parse2 = (JSONObject) JSONObject.parse(pageInfo);
        JSONArray list1 = parse2.getJSONArray("list");
        List<CommoditySpec> commodityDetails = list1.toJavaList(CommoditySpec.class);
        for (CommoditySpec commodityDetail : commodityDetails) {
            List<SkuInventoryVo> inventoryList = commodityDetail.getInventoryList();
            int count = 0;
            if (CollectionUtils.isNotEmpty(inventoryList)) {  //TODO 仓库分仓定价业务  匹配到相同仓库ID的则取仓库商品价
                for (SkuInventoryVo skuInventoryVo : inventoryList) {
                    if (String.valueOf(skuInventoryVo.getWarehouseId()).equals(sysOrderPackage.getDeliveryWarehouseId())) {
                        count++;
                        //商品系统成本价
                        sysOrderPackageDetailNew.setSkuCost(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                        //商品系统单价
                        sysOrderPackageDetailNew.setSkuPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                    }
                }
                if (count == 0) {
                    //商品系统成本价
                    sysOrderPackageDetailNew.setSkuCost(commodityDetail.getCommodityPriceUs());
                    //商品系统单价
                    sysOrderPackageDetailNew.setSkuPrice(commodityDetail.getCommodityPriceUs());
                }

            } else {
                //商品系统成本价
                sysOrderPackageDetailNew.setSkuCost(commodityDetail.getCommodityPriceUs());
                //商品系统单价
                sysOrderPackageDetailNew.setSkuPrice(commodityDetail.getCommodityPriceUs());
            }
            orderAmount = orderAmount.add(sysOrderPackageDetailNew.getSkuPrice().multiply(BigDecimal.valueOf(sysOrderPackageDetail.getSkuQuantity())));
        }
        return orderAmount;
    }

    private String outputOperateLogFile(Map<String, ExcelOrder> orderMap, UserDTO userDTO, String sellerAccount) throws IOException {
        logger.error("进入生成操作日志文件环节:{}",JSONObject.toJSONString(orderMap));
        List<ExcelImportLogVO> importLogVOS = Collections.synchronizedList(new ArrayList<>());
        //创建日志文件
        String username = userDTO.getUserName();
        orderMap.entrySet().stream().forEach(entry -> {
            ExcelOrder sysOrder = entry.getValue();
            ExcelImportLogVO excelImportLogVO = new ExcelImportLogVO();
            excelImportLogVO.setOperator(username);
            excelImportLogVO.setOrderID(sysOrder.getSysOrderId());
            excelImportLogVO.setSn(sysOrder.getSn());
            excelImportLogVO.setStatus(sysOrder.getSuccess());
            excelImportLogVO.setRemark(sysOrder.getErrorMsg());
            if (StringUtils.isNotBlank(sellerAccount)){
                if (sysOrder.getSellerPlAccount().equals(sellerAccount)){
                    importLogVOS.add(excelImportLogVO);
                }
            } else {
                importLogVOS.add(excelImportLogVO);
            }

        });
        String[] header = {Utils.translation("虚拟订单号"), Utils.translation("采购订单号"), Utils.translation("操作人"), Utils.translation("导入状态"), Utils.translation("失败原因")};
        String[] key = {"sn", "orderID", "operator", "status", "remark"};
        String[] width = {"3000", "3000", "3000", "3000", "3000"};
        Map<String, String[]> map = ExcelUtil.createMap(header, key, width);
        logger.error("导出日志文件:{}",JSONObject.toJSONString(importLogVOS));
        JSONArray array = export(importLogVOS);
        InputStream inputStream = ExcelUtil.fileStream(array, map);
        logger.error("日志文件生成操作结束,即将进行阿里服务上传...");
        long millis = System.currentTimeMillis();
        String downloadUrl = aliFileUtils.saveStreamFile(ConstantAli.BucketType.BUCKET, ConstantAli.FolderType.FILE_FOLDER, String.format("%s.xlsx", millis), inputStream);
        logger.error("操作日志上传操作结束,无异常");
        return downloadUrl;
    }

    private SysOrderDTO FillLogisticsAndWarehousingInfo(ExcelOrder sysOrder) {
        //组装调用订单规则匹配的相关参数信息
        logger.error(String.format("组装订单请求参数信息"));
        SysOrderDTO sysOrderDTO = new SysOrderDTO();
        BeanUtils.copyProperties(sysOrder, sysOrderDTO);
        List<SysOrderPackageDTO> sysOrderPackageList = Collections.synchronizedList(new ArrayList<>());
        SysOrderPackageDTO sysOrderPackageDTO = new SysOrderPackageDTO();
        BeanUtils.copyProperties(sysOrder, sysOrderPackageDTO);
        List<SysOrderPackageDetailDTO> sysOrderPackageDetailList = Collections.synchronizedList(new ArrayList<>());
        List<SysOrderDetailDTO> sysOrderDetailList =Collections.synchronizedList(new ArrayList<>());
        sysOrder.getSysOrderDetails().stream().forEach(d -> {
            SysOrderPackageDetailDTO v = new SysOrderPackageDetailDTO();
            BeanUtils.copyProperties(d, v);
            sysOrderPackageDetailList.add(v);
            SysOrderDetailDTO o=new SysOrderDetailDTO();
            BeanUtils.copyProperties(d,o);
            sysOrderDetailList.add(o);
        });
        sysOrderPackageDTO.setSysOrderPackageDetailList(sysOrderPackageDetailList);
        sysOrderPackageDTO.setItemNum(Long.valueOf(String.valueOf(sysOrder.getSysOrderDetails().size())));
        sysOrderPackageList.add(sysOrderPackageDTO);
        sysOrderDTO.setSysOrderPackageList(sysOrderPackageList);
        SysOrderReceiveAddressDTO sysOrderReceiveAddressDTO = new SysOrderReceiveAddressDTO();
        BeanUtils.copyProperties(sysOrder, sysOrderReceiveAddressDTO);
        sysOrderDTO.setSysOrderReceiveAddress(sysOrderReceiveAddressDTO);

        List<SearchLogisticsListSku> plSkuList = new LinkedList<>();

        sysOrder.getSysOrderDetails().stream().forEach(d -> {
            SearchLogisticsListSku searchLogisticsListSku = new SearchLogisticsListSku();
            searchLogisticsListSku.setSkuNumber(d.getSkuQuantity());
            searchLogisticsListSku.setSku(d.getSku());
            plSkuList.add(searchLogisticsListSku);

        });
        logger.error(String.format("组装订单请求参数信息结束"));
        sysOrderDTO.setSysOrderDetailList(sysOrderDetailList);
        try {
            skuMapService.mappingLogisticsRule(sysOrder.getPlatform(), sysOrder.getEmpower(), sysOrderDTO, plSkuList, null, false);
        } catch (Exception e) {
            logger.error(String.format("匹配订单规则发生异常,订单相关信息如下:%s", JSON.toJSONString(sysOrder)));
            logger.error("匹配订单规则发生异常,异常信息如下:{}", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "匹配物流规则作业发生异常");
        }
        return sysOrderDTO;
    }

    public void setOrderDataAgain(SysOrder sysOrder, SysOrderNew orderNew) {
        //因为是手工新增 平台信息设置Null
        sysOrder.setCommoditiesAmount(new BigDecimal("0"));
        sysOrder.setShippingServiceCost(new BigDecimal("0"));
        sysOrder.setEstimateShipCost(orderNew.getEstimateShipCost());//设置预估物流费
        sysOrder.setTotal(sysOrder.getOrderAmount().add(sysOrder.getEstimateShipCost()));//设置订单总售价：预估物流费+系统商品总金额---------
        orderNew.setTotal(sysOrder.getTotal());
        //设置预估利润、利润率
        systemOrderCommonService.setGrossMarginAndProfitMargin(sysOrder);
        orderNew.setProfitMargin(sysOrder.getProfitMargin());
        orderNew.setGrossMargin(sysOrder.getGrossMargin());
    }

    public JSONArray export(List<ExcelImportLogVO> systemExportList) {
        if (CollectionUtils.isEmpty(systemExportList)) {
            return new JSONArray();
        }
        JSONArray array = new JSONArray();
        for (ExcelImportLogVO systemExport : systemExportList) {
            JSONObject json = new JSONObject();
            json.put("orderID", systemExport.getOrderID());
            json.put("sn", systemExport.getSn());
            String status = systemExport.isStatus() ? "成功" : "失败";
            json.put("status", status);
            json.put("operator", systemExport.getOperator());
            json.put("remark", systemExport.getRemark());
            array.add(json);
        }
        return array;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insertBulk(SysOrder sysOrder, SysOrderNew sysOrderNew, UserDTO userDTO, SysOrderDTO sysOrderDTO) {
        SysOrderPackage sysOrderPackage = sysOrderNew.getSysOrderPackageList().get(0);

        sysOrder.getSysOrderDetails().forEach(sysOrderDetail -> {  //遍历插入订单详情
            sysOrderDetailMapper.insertSelective(sysOrderDetail);
        });

        //最晚发货时间默认2天
        sysOrderNew.setDeliverDeadline(TimeUtil.DateToString2(TimeUtil.dateAddSubtract(new Date(), 60 * 24 * 2)));
        sysOrderNew.setSourceOrderId(sysOrder.getSourceOrderId());
        sysOrderNewMapper.insertSelective(sysOrderNew);  //插入订单
        sysOrderPackageMapper.insertSelective(sysOrderPackage);//插入包裹
        sysOrderPackage.getSysOrderPackageDetailList().forEach(sysOrderPackageDetail -> {//遍历插入包裹详情
            sysOrderPackageDetailMapper.insertSelective(sysOrderPackageDetail);
        });
        sysOrderReceiveAddressMapper.insertSelective(sysOrderNew.getSysOrderReceiveAddress());  //插入地址
        sysOrderLogService.insertSelective(        //添加订单操作日志
                new SysOrderLog(sysOrderNew.getSysOrderId(),
                        OrderHandleLogEnum.Content.NEW_ORDER.newOrder(sysOrderNew.getSysOrderId()),
                        OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                        userDTO.getUserName()));
    }

    private Map<String, Object> resolveSheetToOrder(XSSFSheet sheet, Map<String, ExcelOrder> orderMap, UserDTO userDTO, ConcurrentHashMap<String, ExcelOrderStatisticsDTO> concurrentHashMap) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger totalCount = new AtomicInteger(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            XSSFCell xssfCell = row.getCell(i);
            if (xssfCell != null){
                xssfCell.setCellType(CellType.STRING);
            }

            String cellValue = getCellValue(row.getCell(0));
            String sellerPlAccount = getCellValue(row.getCell(2));//2.品连卖家账号
            ExcelOrder order = new ExcelOrder();
            if (StringUtils.isBlank(cellValue)){
                continue;
            }
            if (!orderMap.containsKey(cellValue)) {
                order = new ExcelOrder();
                order.setSn(cellValue);
                orderMap.put(cellValue, order);
                totalCount.addAndGet(1);
                if (userDTO.getPlatformType() == 2){
                    //计算卖家导入总数
                    if (!concurrentHashMap.containsKey(sellerPlAccount)){
                        ExcelOrderStatisticsDTO orderStatisticsDTO = new ExcelOrderStatisticsDTO();
                        orderStatisticsDTO.setTotalCount(new AtomicInteger(1));
                        concurrentHashMap.put(sellerPlAccount, orderStatisticsDTO);
                    } else {
                        ExcelOrderStatisticsDTO orderStatisticsDTO = concurrentHashMap.get(sellerPlAccount);
                        orderStatisticsDTO.getTotalCount().addAndGet(1);
                    }
                }
            } else {
                order = orderMap.get(cellValue);
            }
            order.addCount();
            try {
                addOrderFormSupplyChainManagement(orderMap, row, userDTO);
                order.addSuccess();
                successCount.addAndGet(1);
                order.setIsSuccess(true);
            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.error(e.getLocalizedMessage());
                failCount.addAndGet(1);
                order.addFail();
                order.setErrorMsg(e.getMessage());
                order.setIsSuccess(false);
            }
        }
        Map<String, Object> result = new TreeMap<>();
        result.put("rowCount", totalCount);
        return result;
    }
    /**
     * 从map中获取order对象
     *
     * @param orderMap
     * @param row
     * @return
     */
    private void addOrderFormSeller(Map<String, ExcelOrder> orderMap, XSSFRow row, UserDTO userDTO) throws Exception {
        String virtualOrderID = getCellValue(row.getCell(0));//0.虚拟订单号
        String authorizationShop = getCellValue(row.getCell(2));//2.授权店铺
        String consignee = getCellValue(row.getCell(4)); //收货人
        String country = getCellValue(row.getCell(5));
        String province = getCellValue(row.getCell(6));
        String city = getCellValue(row.getCell(7));
        String detailedAddress1 = getCellValue(row.getCell(8));
        String detailedAddress2 = getCellValue(row.getCell(9));
        String zipCode = getCellValue(row.getCell(10));
        String contactNumber = getCellValue(row.getCell(11));
        String mailbox = getCellValue(row.getCell(12));
        String buyerMessage = getCellValue(row.getCell(13));
        String pinlianSKU = getCellValue(row.getCell(14));
        String quantity = getCellValue(row.getCell(15));

        String pinlianSellerAccount = userDTO.getLoginName();

        ExcelOrder order = orderMap.get(virtualOrderID);
        if (!order.getFill()) {
            order.setSn(virtualOrderID);
            //1.设置来源信息
            order.setRemark(buyerMessage);
            order.setShipToEmail(mailbox);
            order.setShipToPhone(contactNumber);
            order.setShipToPostalCode(zipCode);
            order.setShipToAddrStreet2(detailedAddress2);
            order.setShipToAddrStreet1(detailedAddress1);
            order.setPlatformSellerId(pinlianSellerAccount);
            order.setSellerPlAccount(pinlianSellerAccount);
            order.setShipToCity(city);
            order.setShipToState(province);
            order.setShipToCountry(country);
            order.setShipToName(consignee);
            order.setPlatformSellerAccount(authorizationShop);
            order.setOrderSource(Byte.valueOf(String.valueOf(2)));
            SysOrderDetail detail = new SysOrderDetail();
            detail.setSku(pinlianSKU);
            detail.setSkuQuantity(new BigDecimal(quantity).intValue());
            order.setSysOrderDetails(Arrays.asList(detail));
            order.setFill(true);
            //对平台的存在性进行校验
            Byte platformCode = verifyPlatform(order.getPlatform());
            //检验店铺信息是否在平台中存在
            String resultStr = remoteSellerService.selectObjectByAccount(null, null, null, order.getPlatformSellerAccount(), null);
            String data = Utils.returnRemoteResultDataString(resultStr, "调用卖家服务异常");
            List<Empower> empowerList = JSON.parseArray(data, Empower.class);
            if (empowerList == null || CollectionUtils.isEmpty(empowerList)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, String.format("未获取到授权信息"));
            }
            Integer empowerid = empowerList.get(0).getEmpowerid();
            if (!(Math.abs(platformCode - empowerList.get(0).getPlatform()) == 0)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "店铺信息与平台信息不对称");
            }
            order.setPlatformShopId(empowerid);
            String pinlianaccount = empowerList.get(0).getPinlianaccount();
            if (!StringUtils.equalsIgnoreCase(pinlianaccount, order.getSellerPlAccount())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "品连卖家账号与授权信息不一致");
            }
            order.setSellerPlId(empowerList.get(0).getPinlianid());
        } else {
            SysOrderDetail detail = new SysOrderDetail();
            detail.setSkuQuantity(new BigDecimal(quantity).intValue());
            detail.setSku(pinlianSKU);
            order.getSysOrderDetails().add(detail);
        }
    }

    /**
     * 根据excel sheet 创建订单对象 liusiying
     *
     * @param row
     * @return
     */
    private void addOrderFormSupplyChainManagement(Map<String, ExcelOrder> orderMap, XSSFRow row, UserDTO userDTO) throws Exception {
        String virtualOrderID = "";//0. 虚拟订单编号
        String platform = "";//1.平台
        String pinlianSellerAccount = "";//2.品连卖家账号
        String authorizationShop = "";  //3.授权店铺
        String platformOrderID = "";//平台订单号
        String consignee = "";//收货人
        String country = ""; //国家
        String province = "";//省州
        String city = "";    //城市
        String detailedAddress1 = "";  //详细地址1
        String detailedAddress2 = "";  //详细地址2
        String zipCode = ""; //邮编
        String contactNumber = "";   //联系电话
        String mailbox = "";   //邮箱
        String buyerMessage = "";  //买家留言
        String pinlianSKU = "";   //品连SKU
        String qty = "";  //数量
        //根据平台进行设置卖家账户信息: 账号平台类型   0供应商平台  1卖家平台  2管理平台
        Integer platformType = userDTO.getPlatformType();
        if (platformType == 2 ){
            virtualOrderID = getCellValue(row.getCell(0));//0. 虚拟订单编号
            platform = getCellValue(row.getCell(1));//1.平台
            pinlianSellerAccount = getCellValue(row.getCell(2));//2.品连卖家账号
            authorizationShop = getCellValue(row.getCell(3));  //3.授权店铺


            platformOrderID = getCellValue(row.getCell(4));//平台订单号
            consignee = getCellValue(row.getCell(5));//收货人
            country = getCellValue(row.getCell(6)); //国家
            province = getCellValue(row.getCell(7));//省州
            city = getCellValue(row.getCell(8));    //城市
            detailedAddress1 = getCellValue(row.getCell(9));  //详细地址1
            detailedAddress2 = getCellValue(row.getCell(10));  //详细地址2
            zipCode = getCellValue(row.getCell(11)); //邮编
            contactNumber = getCellValue(row.getCell(12));   //联系电话
            mailbox = getCellValue(row.getCell(13));   //邮箱
            buyerMessage = getCellValue(row.getCell(14));  //买家留言
            pinlianSKU = getCellValue(row.getCell(15));   //品连SKU
            qty = getCellValue(row.getCell(16));  //数量
            if (StringUtils.isBlank(pinlianSellerAccount)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连卖家账号不能为空");
            }
        } else {
            virtualOrderID = getCellValue(row.getCell(0));//0. 虚拟订单编号
            platform = getCellValue(row.getCell(1));//1.平台
            pinlianSellerAccount = userDTO.getLoginName();//2.品连卖家账号
            authorizationShop = getCellValue(row.getCell(2));  //3.授权店铺


            platformOrderID = getCellValue(row.getCell(3));//平台订单号
            consignee = getCellValue(row.getCell(4));//收货人
            country = getCellValue(row.getCell(5)); //国家
            province = getCellValue(row.getCell(6));//省州
            city = getCellValue(row.getCell(7));    //城市
            detailedAddress1 = getCellValue(row.getCell(8));  //详细地址1
            detailedAddress2 = getCellValue(row.getCell(9));  //详细地址2
            zipCode = getCellValue(row.getCell(10)); //邮编
            contactNumber = getCellValue(row.getCell(11));   //联系电话
            mailbox = getCellValue(row.getCell(12));   //邮箱
            buyerMessage = getCellValue(row.getCell(13));  //买家留言
            pinlianSKU = getCellValue(row.getCell(14));   //品连SKU
            qty = getCellValue(row.getCell(15));  //数量
        }

        ExcelOrder order = orderMap.get(virtualOrderID);
        if (!order.getFill()) {
            order.setSn(virtualOrderID);
            order.setOrderSource(Byte.valueOf(String.valueOf(OrderSourceCovertToGoodCandPlatformEnum.getOrderSource(platform))));
            order.setPlatformSellerAccount(authorizationShop);
            order.setShipToName(consignee);
            order.setPlatformSellerId(pinlianSellerAccount);
            order.setSellerPlAccount(pinlianSellerAccount);
            order.setSourceOrderId(platformOrderID);
            order.setPlatform(platform);
            order.setShipToCountry(country);
            order.setShipToState(province);
            order.setShipToCity(city);
            order.setShipToAddrStreet1(detailedAddress1);
            order.setShipToAddrStreet2(detailedAddress2);
            order.setShipToPostalCode(zipCode);
            order.setShipToPhone(contactNumber);
            order.setShipToEmail(mailbox);
            order.setRemark(buyerMessage);
            order.setBuyerCheckoutMessage(buyerMessage);
            order.setOrderTrackId(OrderUtils.getPLOrderItemNumber());
            //设置订单详情
            List<SysOrderDetail> details = Collections.synchronizedList(new ArrayList<>());
            SysOrderDetail detail = new SysOrderDetail();
            detail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
            detail.setSkuQuantity(new BigDecimal(qty).intValue());
            detail.setSku(pinlianSKU);

            details.add(detail);
            order.setSysOrderDetails(details);
            order.setSkus(order.getSysOrderDetails().stream().map(d -> d.getSku()).collect(Collectors.toList()));
            order.setFill(true);

            //对平台的存在性进行校验
            Byte platformCode = verifyPlatform(order.getPlatform());
            //检验店铺信息是否在平台中存在
            String resultStr = remoteSellerService.selectObjectByAccount(null, null, null, order.getPlatformSellerAccount(), null);
            String data = Utils.returnRemoteResultDataString(resultStr, "调用卖家服务异常");
            List<Empower> empowerList = JSON.parseArray(data, Empower.class);
            if (empowerList == null || CollectionUtils.isEmpty(empowerList)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, String.format("未获取到授权信息"));
            }
            Integer empowerid = empowerList.get(0).getEmpowerid();
            if (!(Math.abs(platformCode - empowerList.get(0).getPlatform()) == 0)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "店铺信息与平台信息不对称");
            }
            order.setPlatformShopId(empowerid);
            String pinlianaccount = empowerList.get(0).getPinlianaccount();
            if (!StringUtils.equalsIgnoreCase(pinlianaccount, order.getSellerPlAccount())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "品连卖家账号与授权信息不一致");
            }
            order.setSellerPlId(empowerList.get(0).getPinlianid());
            order.setMarketplaceId(empowerList.get(0).getWebname());
            order.setEmpower(empowerList.get(0));
        } else {
            //如果虚拟订单号相同，校验各种信息是否相等才设置订单详情
            ExcelOrder orderNew = new ExcelOrder();

            orderNew.setOrderSource(Byte.valueOf(String.valueOf(OrderSourceCovertToGoodCandPlatformEnum.getOrderSource(platform))));
            if (orderNew.getOrderSource() != null){
                if (OrderSourceCovertToGoodCandPlatformEnum.EBAY.getOrderSource() == orderNew.getOrderSource().intValue()){
                    orderNew.setRecordNumber(platformOrderID);
                }
            }

            orderNew.setPlatformSellerAccount(authorizationShop);
            orderNew.setShipToName(consignee);

            orderNew.setPlatformSellerId(pinlianSellerAccount);
            orderNew.setSellerPlAccount(pinlianSellerAccount);
            orderNew.setSourceOrderId(platformOrderID);
            orderNew.setPlatform(platform);
            orderNew.setShipToCountry(country);
            orderNew.setShipToState(province);
            orderNew.setShipToCity(city);
            orderNew.setShipToAddrStreet1(detailedAddress1);
            orderNew.setShipToAddrStreet2(detailedAddress2);
            orderNew.setShipToPostalCode(zipCode);
            orderNew.setShipToPhone(contactNumber);
            orderNew.setShipToEmail(mailbox);
            orderNew.setRemark(buyerMessage);
            if (checkInfo(order, orderNew)){
                //设置订单详情
                SysOrderDetail detail = new SysOrderDetail();
                detail.setSku(pinlianSKU);
                detail.setSkuQuantity(new BigDecimal(qty).intValue());

                order.getSysOrderDetails().add(detail);
                order.setSkus(order.getSysOrderDetails().stream().map(d -> d.getSku()).collect(Collectors.toList()));
            }
        }

        //校验数据
        if (StringUtils.isNotBlank(qty)){
            if (Integer.valueOf(qty) <= 0){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku数量不能小于0");
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku数量不能为空");
        }
        CountryCode countryCode = countryCodeService.findCountryByISO(country);
        if (null == countryCode){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家二字码错误");
        }
    }

    /**
     * 检验是否相同
     * @param orderOld
     * @param orderNew
     * @return
     */
    private Boolean checkInfo(ExcelOrder orderOld, ExcelOrder orderNew){
        if (orderOld.getOrderSource() != orderNew.getOrderSource()){
            return false;
        } else if (!orderOld.getPlatformSellerAccount().equals(orderNew.getPlatformSellerAccount())){
            return false;
        } else if (!orderOld.getSellerPlAccount().equals(orderNew.getSellerPlAccount())){
            return false;
        } else if (!orderOld.getSourceOrderId().equals(orderNew.getSourceOrderId())){
            return false;
        } else if (!orderOld.getShipToName().equals(orderNew.getShipToName())){
            return false;
        } else if (!orderOld.getShipToCountry().equals(orderNew.getShipToCountry())){
            return false;
        } else if (!orderOld.getShipToState().equals(orderNew.getShipToState())){
            return false;
        } else if (!orderOld.getShipToCity().equals(orderNew.getShipToCity())){
            return false;
        } else if (!orderOld.getShipToAddrStreet1().equals(orderNew.getShipToAddrStreet1())){
            return false;
        } else if (!orderOld.getShipToAddrStreet2().equals(orderNew.getShipToAddrStreet2())){
            return false;
        } else if (!orderOld.getShipToPostalCode().equals(orderNew.getShipToPostalCode())){
            return false;
        } else if (!orderOld.getShipToPhone().equals(orderNew.getShipToPhone())){
            return false;
        } else if (!orderOld.getRemark().equals(orderNew.getRemark())){
            return false;
        }

        return true;
    }

    private boolean getUserfulWarehouseBySkus(List<SearchLogisticsListSku> plSkuList, HashMap<Object, Object> map) {
        List<String> skus = new ArrayList<>();
        for (SearchLogisticsListSku sku : plSkuList) {
            skus.add(sku.getSku());
        }
        List<OrderInvDTO> warehouseList = systemOrderCommonService.getMappingWarehouseBySkuList(skus);
        if (CollectionUtils.isEmpty(warehouseList)) {
            return true;
        } else {
            warehouseList.forEach(orderInvDTO -> {
                map.put(orderInvDTO.getWarehouseId(), orderInvDTO);
            });
        }
        return false;
    }

    /**
     * 平台校验 liusiying
     * 平台 (1 ebay   2 Amazon 3 aliexpress)
     *
     * @param platform
     */
    private Byte verifyPlatform(String platform) {
        if (StringUtils.isBlank(platform)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台名称不正确");
        } else if (platform.equalsIgnoreCase(PlatformRuleCovertToUserServicePlatformEnum.E_BAY.getPlatform())) {
            return 1;
        } else if (platform.equalsIgnoreCase(PlatformRuleCovertToUserServicePlatformEnum.ALIEXPRESS.getPlatform())) {
            return 3;
        } else if (platform.equalsIgnoreCase(PlatformRuleCovertToUserServicePlatformEnum.AMAZON.getPlatform())) {
            return 2;
        } else if (platform.equalsIgnoreCase(PlatformRuleCovertToUserServicePlatformEnum.WISH.getPlatform())) {
            return 0;
        } else if (platform.equalsIgnoreCase(PlatformRuleCovertToUserServicePlatformEnum.OTHER.getPlatform())) {
            return 4;
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台名称不正确");
        }
    }

    public String getCellValue(XSSFCell cell) {
        String value = "";
        if (cell == null) {
            return value;
        }
        cell.setCellType(CellType.STRING);
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                DecimalFormat df = new DecimalFormat("0");
                value = df.format(cell.getNumericCellValue());
                //value = String.valueOf(cell.getNumericCellValue());
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case _NONE:
                break;
            case BLANK:
                break;
            case ERROR:
                break;
            case BOOLEAN:
                break;
            case FORMULA:
                break;
            default:
                break;
        }
        return value.trim();
    }


    private void getSellerPlAccountAndId(SysOrder sysOrder) {
//        if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 1) {  //卖家平台
//            String topUserLoginName = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
//            Integer topUserId = getLoginUserInformationByToken.getUserDTO().getTopUserId();
//            sysOrder.setSellerPlAccount(topUserLoginName);
//            sysOrder.setSellerPlId(topUserId);
//        } else if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 2) {  //管理后台
//            String result = remoteUserService.findUserIdOrUserName("userId", 1, new String[]{String.valueOf(sysOrder.getSellerPlId())});
//            String data = Utils.returnRemoteResultDataString(result, "用户服务异常");
//            if (JSONObject.parseObject(data).get(String.valueOf(sysOrder.getSellerPlId())) == null) {
//                logger.error(String.valueOf(sysOrder.getSellerPlId()) + "找不到品连账号");
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据品连账号ID找不到该品连账号");
//            } else {
//                String sellerPlAccount = (String) JSONObject.parseObject(data).get(String.valueOf(sysOrder.getSellerPlId()));
//                sysOrder.setSellerPlAccount(sellerPlAccount);
//            }
//        } else {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401);//其他平台无权限查看
//        }
    }


    /**
     * 调用供应商服务设置仓库信息
     *
     * @param sysOrder
     */
    public void setWareHouseInfo(SysOrder sysOrder) {


//        sysOrder.setDeliveryWarehouseCode(warehouseDTO.getWarehouseCode());
//        sysOrder.setDeliveryWarehouse(warehouseDTO.getWarehouseName());

        String str = remoteSupplierService.queryLogisticsByCode(sysOrder.getDeliveryMethodCode(), Integer.valueOf(sysOrder.getDeliveryWarehouseId()));
        String dataString = Utils.returnRemoteResultDataString(str, "供应商服务异常");
        if (StringUtils.isNotBlank(dataString)) {
            LogisticsDTO logisticsDTO = JSONObject.parseObject(dataString, LogisticsDTO.class);
            String shortName = logisticsDTO.getShortName();
            sysOrder.setDeliveryMethod(shortName);
            sysOrder.setShippingCarrierUsed(logisticsDTO.getCarrierName());
            sysOrder.setShippingCarrierUsedCode(logisticsDTO.getCarrierCode());
            sysOrder.setAmazonCarrierName(logisticsDTO.getAmazonCarrier());
            sysOrder.setAmazonShippingMethod(logisticsDTO.getAmazonCode());
            sysOrder.setEbayCarrierName(logisticsDTO.getEbayCarrier());
        } else {
            logger.error("根据仓库ID：{}和邮寄方式code：{}找不到仓库相关信息，供应商服务返回Null", sysOrder.getDeliveryWarehouseId(), sysOrder.getDeliveryMethodCode());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据仓库ID和邮寄方式code查不到仓库信息");
        }
    }

    /**
     * 设置订单商品信息
     *
     * @param sysOrder
     */
    public void setSysOrderItemInfo(SysOrder sysOrder) {
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            String result = remoteCommodityService.test("1", "1", null, null, null, null,
                    sysOrderDetail.getSku(), null, null);
            String data = Utils.returnRemoteResultDataString(result, "调用商品服务异常");
            JSONObject parse1 = (JSONObject) JSONObject.parse(data);
            String pageInfo = parse1.getString("pageInfo");
            JSONObject parse2 = (JSONObject) JSONObject.parse(pageInfo);
            JSONArray list1 = parse2.getJSONArray("list");
            List<CommoditySpec> commodityDetails = list1.toJavaList(CommoditySpec.class);
            sysOrderDetail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
            for (CommoditySpec commodityDetail : commodityDetails) {
                List<SkuInventoryVo> inventoryList = commodityDetail.getInventoryList();
                int count = 0;
                if (CollectionUtils.isNotEmpty(inventoryList)) {  //TODO 仓库分仓定价业务  匹配到相同仓库ID的则取仓库商品价
                    for (SkuInventoryVo skuInventoryVo : inventoryList) {
                        if (String.valueOf(skuInventoryVo.getWarehouseId()).equals(sysOrder.getDeliveryWarehouseId())) {
                            count++;
                            //品连单个商品成本价
                            sysOrderDetail.setItemCost(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                            //商品系统单价
                            sysOrderDetail.setItemPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                            //供应商sku单价
                            sysOrderDetail.setSupplierSkuPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                        }
                    }
                    if (count == 0) {
                        //品连单个商品成本价
                        sysOrderDetail.setItemCost(commodityDetail.getCommodityPriceUs());
                        //商品系统单价
                        sysOrderDetail.setItemPrice(commodityDetail.getCommodityPriceUs());
                        //供应商sku单价
                        sysOrderDetail.setSupplierSkuPrice(commodityDetail.getCommodityPriceUs());
                    }
                } else {
                    //品连单个商品成本价
                    sysOrderDetail.setItemCost(commodityDetail.getCommodityPriceUs());
                    //商品系统单价
                    sysOrderDetail.setItemPrice(commodityDetail.getCommodityPriceUs());
                    //供应商sku单价
                    sysOrderDetail.setSupplierSkuPrice(commodityDetail.getCommodityPriceUs());
                }

                //体积
                sysOrderDetail.setBulk(commodityDetail.getPackingHeight().multiply(commodityDetail.getPackingWidth()).multiply(commodityDetail.getPackingLength()));
                //重量
                sysOrderDetail.setWeight(commodityDetail.getPackingWeight());
                //商品ID
                sysOrderDetail.setItemId(commodityDetail.getId());
                //商品URL
                sysOrderDetail.setItemUrl(commodityDetail.getMasterPicture());
                //商品名称
                sysOrderDetail.setItemName(commodityDetail.getCommodityNameCn());
                sysOrderDetail.setItemNameEn(commodityDetail.getCommodityNameEn());
                //商品属性
                sysOrderDetail.setItemAttr(commodityDetail.getCommoditySpec());
                //订单项SKU
                sysOrderDetail.setSku(commodityDetail.getSystemSku());
                //SKU标题
                sysOrderDetail.setSkuTitle(commodityDetail.getCommodityNameCn());
                //供应商ID
                sysOrderDetail.setSupplierId(Long.valueOf(commodityDetail.getSupplierId()));
                //供应商名称
                sysOrderDetail.setSupplierName(commodityDetail.getSupplierName());
                //供应商SKU
                sysOrderDetail.setSupplierSku(commodityDetail.getSupplierSku());
                //供应商SKU标题
                sysOrderDetail.setSupplierSkuTitle(commodityDetail.getCommodityNameCn());
                //服务费 优先取固定服务费
                sysOrderDetail.setFareTypeAmount(commodityDetail.getFeePriceUs() != null ? "1#" + commodityDetail.getFeePriceUs().toString() : "2#" + commodityDetail.getFeeRate().toString());
                //是否包邮
                sysOrderDetail.setFreeFreight(commodityDetail.getFreeFreight());
            }
        }
        //判断该订单是包邮还是不包邮还是部分包邮
        int count = 0;
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            count += sysOrderDetail.getFreeFreight();
        }
        if (count == 0) {
            sysOrder.setFreeFreightType(Constants.SysOrder.NOT_FREE_FREIGHT);
        } else if (count == sysOrder.getSysOrderDetails().size()) {
            sysOrder.setFreeFreightType(Constants.SysOrder.FREE_FREIGHT);
        } else {
            sysOrder.setFreeFreightType(Constants.SysOrder.PART_FREE_FREIGHT);
        }
    }

    public void setOrderData(SysOrder sysOrder) {
        //设置店铺类型
        sysOrder.setShopType(orderCommonService.queryAuthorizationFromSeller(sysOrder).getRentstatus() == 0 ? "PERSONAL" : "RENT");
        //获取该用户的供应链公司信息，如果是新接入的订单来源则更改为具体的订单来源，否则默认都为手工新建
        resetOrderSourceIfExistNewSource(sysOrder);//TODO
        //远程调用用户服务获取供应链公司ID和名称并设置进系统订单
        setSupplyChainInfo(sysOrder, null, true);
        sysOrder.setOrderTrackId(OrderUtils.getPLTrackNumber());//设置订单跟踪号
        String plOrderNumber = OrderUtils.getPLOrderNumber();
        sysOrder.setSysOrderId(plOrderNumber);//设置品连系统订单ID
        BigDecimal orderTotal = new BigDecimal(0);//系统商品总价格
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            setSupplyChainInfo(null, sysOrderDetail, false);
            sysOrderDetail.setSysOrderId(plOrderNumber);  //设置品连系统订单ID
            sysOrderDetail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());//设置品连系统订单项ID
            //商品数量*价格
            orderTotal = orderTotal.add(sysOrderDetail.getItemPrice().multiply(new BigDecimal(sysOrderDetail.getSkuQuantity())));
        }
        //设置系统商品总价格
        sysOrder.setOrderAmount(orderTotal);
    }

    private void setSupplyChainInfo(SysOrder sysOrder, SysOrderDetail sysOrderDetail, Boolean isSeller) {
        if (sysOrder != null) {
            if (isSeller) {  //卖家查询供应链公司信息
                List<Integer> list = new ArrayList<>();
                list.add(sysOrder.getSellerPlId());
                String result = remoteUserService.getSupplyChainByUserId("1", list);
                String string = Utils.returnRemoteResultDataString(result, null);
                if (StringUtils.isBlank(string)) {
                    logger.error("新建订单调用用户服务查询供应链公司信息异常！");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户服务异常");
                }
                List<SupplyChainCompany> supplyChainCompanies = JSONObject.parseArray(string, SupplyChainCompany.class);
                String supplyChainCompanyName = supplyChainCompanies.get(0).getSupplyChainCompanyName();
                String supplyId = supplyChainCompanies.get(0).getSupplyId();
                if (StringUtils.isBlank(supplyChainCompanyName)) {
                    logger.error("订单新建异常，供应链公司名称为空");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
                }
                if (StringUtils.isBlank(supplyId)) {
                    logger.error("订单新建异常，供应链公司ID为空");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
                }
                sysOrder.setSupplyChainCompanyId(Integer.valueOf(supplyId));  //供应链公司ID
                sysOrder.setSupplyChainCompanyName(supplyChainCompanyName);   //供应链公司名称
            } else {   //供应商查询供应链公司信息
                List<Integer> list = new ArrayList<>();
                list.add(sysOrder.getSellerPlId());
                String result = remoteUserService.getSupplyChainByUserId("0", list);
                String string = Utils.returnRemoteResultDataString(result, null);
                if (StringUtils.isBlank(string)) {
                    logger.error("新建订单调用用户服务查询供应链公司信息异常！");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户服务异常");
                }
                List<SupplyChainCompany> supplyChainCompanies = JSONObject.parseArray(string, SupplyChainCompany.class);
                String supplyChainCompanyName = supplyChainCompanies.get(0).getSupplyChainCompanyName();
                String supplyId = supplyChainCompanies.get(0).getSupplyId();
                sysOrder.setSupplyChainCompanyId(Integer.valueOf(supplyId));  //供应链公司ID
                sysOrder.setSupplyChainCompanyName(supplyChainCompanyName);   //供应链公司名称
            }
        } else {   //sysOrder为null,sysOrderDetail!=null
            if (isSeller) {  //卖家查询供应链公司信息
                List<Integer> list = new ArrayList<>();
                list.add(Integer.valueOf(Long.toString(sysOrderDetail.getSupplierId())));
                String result = remoteUserService.getSupplyChainByUserId("1", list);
                String string = Utils.returnRemoteResultDataString(result, null);
                if (StringUtils.isBlank(string)) {
                    logger.error("新建订单调用用户服务查询供应链公司信息异常！");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户服务异常");
                }
                List<SupplyChainCompany> supplyChainCompanies = JSONObject.parseArray(string, SupplyChainCompany.class);
                String supplyChainCompanyName = supplyChainCompanies.get(0).getSupplyChainCompanyName();
                String supplyId = supplyChainCompanies.get(0).getSupplyId();
                sysOrderDetail.setSupplyChainCompanyId(Integer.valueOf(supplyId));  //供应链公司ID
                sysOrderDetail.setSupplyChainCompanyName(supplyChainCompanyName);   //供应链公司名称
            } else {   //供应商查询供应链公司信息
                List<Integer> list = new ArrayList<>();
                list.add(Integer.valueOf(Long.toString(sysOrderDetail.getSupplierId())));
                String result = remoteUserService.getSupplyChainByUserId("0", list);
                String string = Utils.returnRemoteResultDataString(result, "用户服务异常");
                if (StringUtils.isBlank(string)) {
                    logger.error("新建订单调用用户服务查询供应链公司信息异常！");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户服务异常");
                }
                List<SupplyChainCompany> supplyChainCompanies = JSONObject.parseArray(string, SupplyChainCompany.class);
                String supplyChainCompanyName = supplyChainCompanies.get(0).getSupplyChainCompanyName();
                String supplyId = supplyChainCompanies.get(0).getSupplyId();
                if (StringUtils.isBlank(supplyChainCompanyName)) {
                    logger.error("订单新建异常，供应链公司名称为空");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
                }
                if (StringUtils.isBlank(supplyId)) {
                    logger.error("订单新建异常，供应链公司ID为空");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
                }
                sysOrderDetail.setSupplyChainCompanyId(Integer.valueOf(supplyId));  //供应链公司ID
                sysOrderDetail.setSupplyChainCompanyName(supplyChainCompanyName);   //供应链公司名称
            }
        }

    }


    public void setOrderPackageInfo(SysOrder sysOrder, String loginName, SysOrderNew orderNew, SysOrderReceiveAddress address, SysOrderPackage orderPackage, List<SysOrderPackageDetail> orderPackageDetailList) {
        //设置平台佣金率
        if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
            orderNew.setPlatformCommissionRate(PlatformCommissionEnum.AMAZON.getValue());
        } else if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
            orderNew.setPlatformCommissionRate(PlatformCommissionEnum.EBAY.getValue());
        } else if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_WISH.getValue()) {
            orderNew.setPlatformCommissionRate(PlatformCommissionEnum.WISH.getValue());
        }
        //设置订单表数据
        orderNew.setFreeFreightType(Byte.valueOf(String.valueOf(sysOrder.getFreeFreightType())));
        orderNew.setSysOrderId(sysOrder.getSysOrderId());
        orderNew.setDeliverDeadline(sysOrder.getDeliverDeadline());
        orderNew.setOrderSource(sysOrder.getOrderSource());
        orderNew.setPlatformShopId(sysOrder.getPlatformShopId());
        orderNew.setPlatformSellerAccount(sysOrder.getPlatformSellerAccount());
        orderNew.setShopType(sysOrder.getShopType());
        orderNew.setPlatformSellerId(sysOrder.getPlatformSellerId());
        orderNew.setSellerPlId(sysOrder.getSellerPlId());
        orderNew.setSellerPlAccount(sysOrder.getSellerPlAccount());
        orderNew.setSupplyChainCompanyId(sysOrder.getSupplyChainCompanyId());
        orderNew.setSupplyChainCompanyName(sysOrder.getSupplyChainCompanyName());
//        orderNew.setTotal(sysOrder.getTotal());  // 后面物流费算出来后再补上
        orderNew.setOrderAmount(sysOrder.getOrderAmount());
//        orderNew.setEstimateShipCost(sysOrder.getEstimateShipCost());// 后面物流费算出来后再补上
        orderNew.setMarketplaceId(sysOrder.getMarketplaceId());
        orderNew.setOrderTime(sysOrder.getOrderTime());
        orderNew.setBuyerCheckoutMessage(sysOrder.getBuyerCheckoutMessage());
        orderNew.setCreateBy(loginName);
        orderNew.setUpdateBy(loginName);
//        orderNew.setGrossMargin(sysOrder.getGrossMargin());// 后面物流费算出来后再补上
//        orderNew.setProfitMargin(sysOrder.getProfitMargin());// 后面物流费算出来后再补上
        //设置收货地址数据
        address.setSysOrderId(sysOrder.getSysOrderId());
        address.setShipToName(sysOrder.getShipToName());
        address.setShipToCountry(sysOrder.getShipToCountry());
        CountryCode countryCode = countryCodeService.findCountryByISO(sysOrder.getShipToCountry());
        if (null != countryCode){
            address.setShipToCountryName(countryCode.getNameZh());
        }
        address.setShipToState(sysOrder.getShipToState());
        address.setShipToCity(sysOrder.getShipToCity());
        address.setShipToAddrStreet1(sysOrder.getShipToAddrStreet1());
        address.setShipToAddrStreet2(sysOrder.getShipToAddrStreet2());
        address.setShipToAddrStreet3(sysOrder.getShipToAddrStreet3());
        address.setShipToPostalCode(sysOrder.getShipToPostalCode());
        address.setShipToPostalCode(sysOrder.getShipToPostalCode());
        address.setShipToPhone(sysOrder.getShipToPhone());
        address.setShipToEmail(sysOrder.getShipToEmail());
        address.setCreater(loginName);
        address.setModifier(loginName);
        //设置包裹数据
        orderPackage.setSysOrderId(sysOrder.getSysOrderId());
        orderPackage.setOrderTrackId(sysOrder.getOrderTrackId());
        orderPackage.setCreater(loginName);
        orderPackage.setModifier(loginName);
        //设置包裹详情数据

        sysOrder.getSysOrderDetails().forEach(sysOrderDetail -> {
            SysOrderPackageDetail orderPackageDetail = new SysOrderPackageDetail();
            orderPackageDetail.setOrderTrackId(orderPackage.getOrderTrackId());
            orderPackageDetail.setSku(sysOrderDetail.getSku());
            orderPackageDetail.setSkuQuantity(sysOrderDetail.getSkuQuantity());
            orderPackageDetail.setSkuCost(sysOrderDetail.getItemCost());
            orderPackageDetail.setSkuUrl(sysOrderDetail.getItemUrl());
            orderPackageDetail.setSkuName(sysOrderDetail.getItemName());
            orderPackageDetail.setSkuNameEn(sysOrderDetail.getItemNameEn());
            orderPackageDetail.setSkuAttr(sysOrderDetail.getItemAttr());
            orderPackageDetail.setSkuPrice(sysOrderDetail.getItemPrice());
            orderPackageDetail.setBulk(sysOrderDetail.getBulk());
            orderPackageDetail.setWeight(sysOrderDetail.getWeight());
            orderPackageDetail.setBulk(sysOrderDetail.getBulk());
            orderPackageDetail.setSupplierId(Math.toIntExact(sysOrderDetail.getSupplierId()));
            orderPackageDetail.setSupplierName(sysOrderDetail.getSupplierName());
            orderPackageDetail.setSupplyChainCompanyId(sysOrderDetail.getSupplyChainCompanyId());
            orderPackageDetail.setSupplyChainCompanyName(sysOrderDetail.getSupplyChainCompanyName());
            orderPackageDetail.setFareTypeAmount(sysOrderDetail.getFareTypeAmount());
//            orderPackageDetail.setSellerShipFee(null);  // TODO 暂时设置为NULL
//            orderPackageDetail.setSupplierShipFee(null); // TODO 暂时设置为NULL
//            orderPackageDetail.setLogisticCompanyShipFee(null); // TODO 暂时设置为NULL
            orderPackageDetail.setFreeFreight(sysOrderDetail.getFreeFreight());
            orderPackageDetail.setCreater(loginName);
            orderPackageDetail.setModifier(loginName);
            orderPackageDetailList.add(orderPackageDetail);
        });
        orderPackage.setSysOrderPackageDetailList(orderPackageDetailList);
        orderNew.setSysOrderPackageList(new ArrayList<SysOrderPackage>() {{
            add(orderPackage);
        }});
        orderNew.setSysOrderReceiveAddress(address);
    }

    public void resetOrderSourceIfExistNewSource(SysOrder sysOrder) {
        if (sysOrder.getOrderSource() == null) sysOrder.setOrderSource((byte) 1);
        String result = remoteUserService.getSupplyChinByUserIdOrUsername(sysOrder.getSellerPlId(), null, 1);
        JSONObject data = (JSONObject) JSONObject.parse(Utils.returnRemoteResultDataString(result, "用户服务异常"));
        if (data != null) {
            String supplyUsername = (String) data.get("supplyUsername");
            if ("星商".equals(supplyUsername)) {  //星商订单来源
                sysOrder.setOrderSource((byte) 8);    //设置订单来源为星商订单来源
            }
        }
    }
}
