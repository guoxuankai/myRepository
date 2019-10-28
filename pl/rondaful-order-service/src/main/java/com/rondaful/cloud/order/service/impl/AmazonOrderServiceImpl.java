package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonservices.mws.orders._2013_09_01.model.Address;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsByNextTokenResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersByNextTokenResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersResponse;
import com.amazonservices.mws.orders._2013_09_01.model.Money;
import com.amazonservices.mws.orders._2013_09_01.model.Order;
import com.amazonservices.mws.orders._2013_09_01.model.OrderItem;
import com.amazonservices.mws.orders._2013_09_01.samples.ListOrderItemsByNextTokenSample;
import com.amazonservices.mws.orders._2013_09_01.samples.ListOrderItemsSample;
import com.amazonservices.mws.orders._2013_09_01.samples.ListOrdersByNextTokenSample;
import com.amazonservices.mws.orders._2013_09_01.samples.ListOrdersSample;
import com.amazonservices.mws.products.model.GetMatchingProductResponse;
import com.amazonservices.mws.products.model.GetMatchingProductResult;
import com.amazonservices.mws.products.model.Product;
import com.amazonservices.mws.products.samples.GetMatchingProductSample;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.enums.MessageEnum;
import com.rondaful.cloud.common.enums.OrderHandleEnum;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.order.entity.Amazon.AmazonEmpower;
import com.rondaful.cloud.order.entity.Amazon.AmazonItemProperty;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.entity.PlatformExport;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.Time;
import com.rondaful.cloud.order.enums.AmazonEnum;
import com.rondaful.cloud.order.enums.ConvertAmzonResponseEnum;
import com.rondaful.cloud.order.enums.OrderHandleLogEnum;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.mapper.AmazonEmpowerMapper;
import com.rondaful.cloud.order.mapper.AmazonItemPropertyMapper;
import com.rondaful.cloud.order.mapper.AmazonOrderDetailMapper;
import com.rondaful.cloud.order.mapper.AmazonOrderMapper;
import com.rondaful.cloud.order.mapper.CountryCodeMapper;
import com.rondaful.cloud.order.mapper.SysOrderDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderMapper;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderReceiveAddressDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDetailDTO;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.IAmazonOrderService;
import com.rondaful.cloud.order.service.ISkuMapService;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.utils.AmazonOrderUtils;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.GetAccountAndShopInfoUtils;
import com.rondaful.cloud.order.utils.JudgeAuthorityUtils;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.RateUtil;
import com.rondaful.cloud.order.utils.ThreadPoolUtil;
import com.rondaful.cloud.order.utils.TimeUtil;
import com.rondaful.cloud.order.utils.TranslateResponseContentUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * 作者: Administrator
 * 时间: 2018-12-03 16:08
 * 包名: com.rondaful.cloud.order.service.impl
 * 描述:亚马逊平台订单相关接口业务实现层
 */
@Service
public class AmazonOrderServiceImpl extends BaseServiceImpl<AmazonOrder> implements IAmazonOrderService {
    @Autowired
    private ISysOrderService sysOrderService;
    @Autowired
    private RedissLockUtil redissLockUtil;
    @Autowired
    private JudgeAuthorityUtils judgeAuthorityUtils;
    @Autowired
    private GetAccountAndShopInfoUtils getAccountAndShopInfoUtils;
    @Autowired
    private ISkuMapService skuMapService;
    @Autowired
    private AmazonEmpowerMapper amazonEmpowerMapper;
    @Autowired
    private AmazonOrderServiceImpl amazonOrderServiceImpl;
    @Autowired
    private RateUtil rateUtil;
    @Autowired
    private IAmazonOrderService amazonOrderService;
    @Autowired
    private ISysOrderLogService sysOrderLogService;
    @Autowired
    private OrderMessageSender orderMessageSender;
    @Resource
    private AmazonOrderMapper amazonOrderMapper;
    @Resource
    private CountryCodeMapper countryCodeMapper;
    @Resource
    private SysOrderMapper sysOrderMapper;
    @Resource
    private SysOrderDetailMapper sysOrderDetailMapper;
    @Autowired
    private RemoteSellerService remoteSellerService;
    @Autowired
    private AmazonOrderDetailMapper amazonOrderDetailMapper;
    @Autowired
    private AmazonItemPropertyMapper amazonItemPropertyMapper;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
    private ListOrdersResponse listOrdersResponse = null;//亚马逊订单响应对象
    private final static Logger logger = LoggerFactory.getLogger(AmazonOrderServiceImpl.class);

    @Override
    /**
     * description: 设置数据
     * @Param: amazonOrderList 亚马逊订单集合
     * @Param: isSeller  卖家系统导出true  品连系统导出false
     * @return java.util.List<com.rondaful.cloud.order.entity.PlatformExport>  导出的数据集合
     * create by wujiachuang
     */
    public List<PlatformExport> setData(List<AmazonOrder> amazonOrderList, boolean isSeller) {
        List<PlatformExport> platformExportList = new ArrayList<>();
        String status = "";
        if (isSeller) {
            for (AmazonOrder amazonOrder : amazonOrderList) {
                String skus = "";
                String itemPrice = "";
                String itemCount = "";
                PlatformExport platformExport = new PlatformExport();
                platformExport.setOrderId(amazonOrder.getOrderId());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                platformExport.setDate(sdf.format(amazonOrder.getPaymentTime()));
                platformExport.setSellerId(amazonOrder.getAmazonSellerAccount());
                platformExport.setPrice(String.valueOf(amazonOrder.getOrderTotal()));
                platformExport.setName(amazonOrder.getConsigneeName());
                platformExport.setCountry(amazonOrder.getCountry());
                platformExport.setProvince(amazonOrder.getState());
                platformExport.setCity(amazonOrder.getCity());
                platformExport.setAddress(amazonOrder.getAddress1());
                platformExport.setPostcode(amazonOrder.getPostalCode());
                platformExport.setPhone(amazonOrder.getPhone());
                platformExport.setPostcode(amazonOrder.getPostalCode());
                platformExport.setOrderStatus(amazonOrder.getOrderStatus());
                if (amazonOrder.getPlProcessStatus() == 0) {
                    status = "待处理";
                }
                if (amazonOrder.getPlProcessStatus() == 1) {
                    status = "转入成功";
                }
                if (amazonOrder.getPlProcessStatus() == 2) {
                    status = "转入失败";
                }
                if (amazonOrder.getPlProcessStatus() == 3) {
                    status = "部分转入成功";
                }
                platformExport.setProcessStatus(status);
                for (AmazonOrderDetail amazonOrderDetail : amazonOrder.getAmazonOrderDetails()) {
                    skus += amazonOrderDetail.getPlatformSku() + ",";
                    itemPrice += String.valueOf(amazonOrderDetail.getItemPrice()) + ",";
                    itemCount += amazonOrderDetail.getQuantity() + ",";
                }
                if (skus.length() - 1 <= 0) {
                    platformExport.setPlatformSku(skus);
                } else {
                    platformExport.setPlatformSku(skus.substring(0, skus.length() - 1));
                }
                if (itemPrice.length() - 1 <= 0) {
                    platformExport.setItemPrice(itemPrice);
                } else {

                    platformExport.setItemPrice(itemPrice.substring(0, itemPrice.length() - 1));
                }
                if (itemCount.length() - 1 <= 0) {
                    platformExport.setItemCount(itemCount);
                } else {
                    platformExport.setItemCount(itemCount.substring(0, itemCount.length() - 1));
                }
                platformExportList.add(platformExport);
            }
        } else {
            for (AmazonOrder amazonOrder : amazonOrderList) {
                String skus = "";
                String itemPrice = "";
                String itemCount = "";
                PlatformExport platformExport = new PlatformExport();
                platformExport.setOrderId(amazonOrder.getOrderId());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                platformExport.setDate(sdf.format(amazonOrder.getPaymentTime()));
                platformExport.setSeller(amazonOrder.getPlSellerAccount());
                platformExport.setSellerId(amazonOrder.getAmazonSellerAccount());
                platformExport.setPrice(String.valueOf(amazonOrder.getOrderTotal()));
                platformExport.setName(amazonOrder.getConsigneeName());
                platformExport.setCountry(amazonOrder.getCountry());
                platformExport.setProvince(amazonOrder.getState());
                platformExport.setCity(amazonOrder.getCity());
                platformExport.setAddress(amazonOrder.getAddress1());
                platformExport.setPostcode(amazonOrder.getPostalCode());
                platformExport.setPhone(amazonOrder.getPhone());
                platformExport.setOrderStatus(amazonOrder.getOrderStatus());
                if (amazonOrder.getPlProcessStatus() == 0) {
                    status = "待处理";
                }
                if (amazonOrder.getPlProcessStatus() == 1) {
                    status = "转入成功";
                }
                if (amazonOrder.getPlProcessStatus() == 2) {
                    status = "转入失败";
                }
                if (amazonOrder.getPlProcessStatus() == 3) {
                    status = "部分转入成功";
                }
                platformExport.setProcessStatus(status);
                for (AmazonOrderDetail amazonOrderDetail : amazonOrder.getAmazonOrderDetails()) {
                    skus += amazonOrderDetail.getPlatformSku() + ",";
                    itemPrice += String.valueOf(amazonOrderDetail.getItemPrice()) + ",";
                    itemCount += amazonOrderDetail.getQuantity() + ",";
                }
                if (skus.length() - 1 <= 0) {
                    platformExport.setPlatformSku(skus);
                } else {
                    platformExport.setPlatformSku(skus.substring(0, skus.length() - 1));
                }
                if (itemPrice.length() - 1 <= 0) {
                    platformExport.setItemPrice(itemPrice);
                } else {

                    platformExport.setItemPrice(itemPrice.substring(0, itemPrice.length() - 1));
                }
                if (itemCount.length() - 1 <= 0) {
                    platformExport.setItemCount(itemCount);
                } else {
                    platformExport.setItemCount(itemCount.substring(0, itemCount.length() - 1));
                }
               /* platformExport.setPlatformSku(skus.substring(0, skus.length() - 1));
                platformExport.setItemPrice(itemPrice.substring(0, itemPrice.length() - 1));
                platformExport.setItemCount( itemCount.substring(0, itemCount.length() - 1));*/
                platformExportList.add(platformExport);
            }
        }
        for (PlatformExport platformExport : platformExportList) {
            //翻译
            platformExport.setProcessStatus(com.rondaful.cloud.common.utils.Utils.translation(platformExport.getProcessStatus()));
        }
        return platformExportList;
    }

    @Override
    public JSONArray export(List<PlatformExport> platformExportList) {
        JSONArray array = new JSONArray();
        for (PlatformExport platformExport : platformExportList) {
            JSONObject json = new JSONObject();
            json.put("orderId", platformExport.getOrderId());
            json.put("date", platformExport.getDate());
            json.put("seller", platformExport.getSeller());
            json.put("sellerId", platformExport.getSellerId());
            json.put("platformSku", platformExport.getPlatformSku());
            json.put("itemPrice", platformExport.getItemPrice());
            json.put("itemCount", platformExport.getItemCount());
            json.put("price", platformExport.getPrice());
            json.put("name", platformExport.getName());
            json.put("country", platformExport.getCountry());
            json.put("province", platformExport.getProvince());
            json.put("city", platformExport.getCity());
            json.put("address", platformExport.getAddress());
            json.put("postcode ", platformExport.getPostcode());
            json.put("phone ", platformExport.getPhone());
            json.put("email ", platformExport.getEmail());
            json.put("orderStatus ", platformExport.getOrderStatus());
            json.put("processStatus ", platformExport.getProcessStatus());
            array.add(json);
        }
        return array;
    }

    @Override
    public AmazonOrder selectAmazonOrderByOrderId(String orderId) {
        AmazonOrder amazonOrder = null;
        amazonOrder = amazonOrderMapper.selectAmazonOrderByOrderId(orderId);
//        PageInfo<AmazonOrder> pageInfo = new PageInfo<>(list);
//        return new Page<>(pageInfo);
        //翻译
        amazonOrder.setOrderStatus(com.rondaful.cloud.common.utils.Utils.i18n(amazonOrder.getOrderStatus()));
        return amazonOrder;
    }

    public AmazonOrder findAmazonOrderByOrderId(String orderId) {
        AmazonOrder amazonOrder = null;
        amazonOrder = amazonOrderMapper.selectAmazonOrderByOrderId(orderId);
        return amazonOrder;
    }

    @Override
    public List<AmazonOrder> getExportResults(String platformSellerAccount, String orderId, String sellerPlAccount, String orderStatus, String
            plstatus, String startDate, String endDate) throws Exception {
        List<Integer> userIds = new ArrayList<>();  //账号ID集合
        List<Integer> empIds = new ArrayList<>();  //店铺ID集合
        sellerPlAccount = getSellerPlAccount(sellerPlAccount);//获取卖家的品连主账号和ID
        Integer plSellerId = null;
        plSellerId = getAccountAndShopInfoUtils.getPlSellerIdIfNotNull(sellerPlAccount, plSellerId); //存在则返回品连账号ID
        Integer shopId = getAccountAndShopInfoUtils.getShopIdIfNotNull(platformSellerAccount, sellerPlAccount);//存在则返回店铺ID
        if (shopId != null) {
            empIds.add(shopId);
        }
        if (judgeAuthorityUtils.judgeUserAuthorityAndSetDataToList(plSellerId, getLoginUserInformationByToken.getUserDTO(), userIds, empIds)) {
            return null;
        }
        Byte plProcessStatuss = getPlProcessStatuss(plstatus);//获取亚马逊订单转入值
        int count = amazonOrderMapper.selectAmazonOrderByMultiConditionCounts(empIds, orderId, userIds, orderStatus, (byte) plProcessStatuss, startDate, endDate);
        List<AmazonOrder> result = new ArrayList<>();//返回结果
        if (count == 0) {
            return null;
        }
        int num = 500;//每次查询的条数
        //需要查询的次数
        int times = count / num;
        if (count % num != 0) {
            times = times + 1;
        }
        //开始查询的行数
        int bindex = 0;
        List<Callable<List<AmazonOrder>>> tasks = new ArrayList<Callable<List<AmazonOrder>>>();//添加任务
        for (int i = 0; i < times; i++) {
            Callable<List<AmazonOrder>> qfe = new AmazonOrderThredQuery(this, empIds, orderId, userIds, orderStatus, (byte) plProcessStatuss, startDate, endDate, bindex, num);
            tasks.add(qfe);
            bindex = bindex + num;
        }
        //定义固定长度的线程池  防止线程过多
        ThreadPoolExecutor execservice = ThreadPoolUtil.getInstance();
//        ExecutorService execservice = Executors.newFixedThreadPool(15);
        List<Future<List<AmazonOrder>>> futures = null;
        futures = execservice.invokeAll(tasks);
        // 处理线程返回结果
        if (futures != null && futures.size() > 0) {
            for (Future<List<AmazonOrder>> future : futures) {
                result.addAll(future.get());
            }
        }
//        execservice.shutdown();  // 关闭线程池
        return result;
    }

    @Override
    /**
     * description: 不定条件查询亚马逊订单
     * @Param: shopName 亚马逊店铺名
           * @Param: orderId  订单ID
           * @Param: sellerPlAccount 品连账号
           * @Param: orderStatus  订单状态
           * @Param: plstatus  品连处理状态
           * @Param: startDate 订单创建的起始时间
           * @Param: endDate    订单创建的结束时间
     * @return com.rondaful.cloud.common.entity.Page<com.rondaful.cloud.order.entity.Amazon.AmazonOrder>  亚马逊订单集合
     * create by wujiachuang
     */
    public PageInfo<AmazonOrder> selectAmazonOrderByMultiCondition(String platformSellerAccount, String orderId, String sellerPlAccount, String orderStatus, String
            plstatus, String startDate, String endDate) {
        List<Integer> userIds = new ArrayList<>();  //账号ID集合
        List<Integer> empIds = new ArrayList<>();  //店铺ID集合
        boolean isCMS = false;
        if (UserEnum.platformType.CMS.getPlatformType().equals(getLoginUserInformationByToken.getUserDTO().getPlatformType())) {//管理后台
            isCMS = true;
        }
        sellerPlAccount = getSellerPlAccount(sellerPlAccount);//获取卖家的品连主账号和ID
        Integer plSellerId = null;
        plSellerId = getAccountAndShopInfoUtils.getPlSellerIdIfNotNull(sellerPlAccount, plSellerId); //存在则返回品连账号ID
        Integer shopId = getAccountAndShopInfoUtils.getShopIdIfNotNull(platformSellerAccount, sellerPlAccount);//存在则返回店铺ID
        if (shopId != null) {
            empIds.add(shopId);
        }
        if (judgeAuthorityUtils.judgeUserAuthorityAndSetDataToList(plSellerId, getLoginUserInformationByToken.getUserDTO(), userIds, empIds))
            return new PageInfo<>(null);
        Byte plProcessStatuss = getPlProcessStatuss(plstatus);//获取亚马逊订单转入值
        List<AmazonOrder> list;
        if (isCMS) {
            list = amazonOrderMapper.selectAmazonOrderByMultiConditionByCMS(empIds, orderId, userIds, orderStatus, (byte) plProcessStatuss, startDate, endDate, null, null);
        } else {
            list = amazonOrderMapper.selectAmazonOrderByMultiCondition(empIds, orderId, userIds, orderStatus, (byte) plProcessStatuss, startDate, endDate, null, null);
        }
        PageInfo<AmazonOrder> pageInfo = new PageInfo(list);
        for (AmazonOrder amazonOrder : pageInfo.getList()) {
            if (!amazonOrder.getOrderStatus().equalsIgnoreCase(AmazonEnum.OrderStatus.UNSHIPPED.getValue()) && !amazonOrder.getOrderStatus().equalsIgnoreCase(AmazonEnum.OrderStatus.CANCELED.getValue())) {
                amazonOrder.setDeliveryTime(amazonOrder.getLastUpdateTime());
            }
            //翻译
            amazonOrder.setOrderStatus(com.rondaful.cloud.common.utils.Utils.translation(amazonOrder.getOrderStatus()));
          /*  String shopName = getAccountAndShopInfoUtils.GetSellerShopNameByShopId(amazonOrder.getAmazonShopId(), amazonOrder.getPlSellerAccount());
            amazonOrder.setAmazonShopName(shopName);*/
        }
        return pageInfo;
    }

    public byte getPlProcessStatuss(String plstatus) {
        byte plProcessStatuss;
        if (StringUtils.isBlank(plstatus)) {
            plProcessStatuss = 4;   //全选
        } else {
            if (plstatus.equals("待转入")) {
                plProcessStatuss = 0;
            } else if (plstatus.equals("转入成功")) {
                plProcessStatuss = 1;
            } else if (plstatus.equals("转入失败")) {
                plProcessStatuss = 2;
            } else if (plstatus.equals("部分转入成功")) {
                plProcessStatuss = 3;
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数错误");
            }
        }
        return plProcessStatuss;
    }

    public String getSellerPlAccount(String sellerPlAccount) {
        if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 1) {  //卖家平台
            sellerPlAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        } else if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 2) {  //管理后台
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);//其他平台无权限查看
        }
        return sellerPlAccount;
    }

    @Override
    /**
     * description: 远程调用卖家服务查询可用的亚马逊授权信息并插入数据库
     * @Param: setPlAccountIfManualSync 1.手动同步情况：品连用户名  2.自动同步情况：空串""
     * @return java.util.List<com.rondaful.cloud.order.entity.Amazon.AmazonEmpower>   亚马逊授权信息集合
     * create by wujiachuang
     */
    public List<AmazonEmpower> queryRemoteSellerServiceAndInsertDb(String setPlAccountIfManualSync) {
        if (setPlAccountIfManualSync.length() == 0) {
            //自动同步
            Integer platform = 2;//代表亚马逊平台
            Integer status = 1;//有效
            String allRemote = remoteSellerService.findAllRemote(platform, status);
            //解析远程调用授权接口数据,返回亚马逊授权信息集合
            List<AmazonEmpower> amazonEmpowerList = getAmazonEmpowers(allRemote);
            return amazonEmpowerList;
        } else {
            String result = remoteSellerService.selectObjectByAccount(null, setPlAccountIfManualSync,
                    1, null, 2);
            String data = com.rondaful.cloud.common.utils.Utils.returnRemoteResultDataString(result, "调用卖家微服务异常 。。。");
            List<Empower> empowers = JSONObject.parseArray(data, Empower.class);
            if (CollectionUtils.isEmpty(empowers) || empowers.size() == 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查不到卖家的授权信息！");
            }
            List<AmazonEmpower> amazonEmpowerList = new ArrayList<>();
            for (Empower empower : empowers) {
                amazonEmpowerList.add(new AmazonEmpower(empower.getEmpowerid(), empower.getAccount(), empower
                        .getPinlianaccount(), empower.getPinlianid(), empower.getToken(), empower.getThirdpartyname(), empower
                        .getWebname(), empower.getStatus()
                        , (byte) 0));
            }
            return amazonEmpowerList;
        }
    }

    @Override
    public List<AmazonOrder> selectAmazonOrderByOrderListId(List<String> ids) {

        List<AmazonOrder> amazonOrderList = amazonOrderMapper.selectAmazonOrderByOrderListId(ids);

        return amazonOrderList;
    }


    /**
     * description: 解析远程调用授权接口数据，返回亚马逊授权信息集合
     *
     * @return create by wujiachuang
     * @Param: allRemote1 远程调用卖家服务返回的结果
     */
    private List<AmazonEmpower> getAmazonEmpowers(String allRemote1) {
        JSONObject jsonObject = JSONObject.parseObject(allRemote1);
        JSONArray data = jsonObject.getJSONArray("data");
   /*     String s = data.toString();
        System.out.println(s);*/
        List<Empower> list = data.toJavaList(Empower.class);
        List<AmazonEmpower> amazonEmpowerList = new ArrayList<>();
        for (Empower empower : list) {
            amazonEmpowerList.add(new AmazonEmpower(empower.getEmpowerid(), empower.getAccount(), empower
                    .getPinlianaccount(), empower.getPinlianid(), empower.getToken(), empower.getThirdpartyname(), empower
                    .getWebname(), empower.getStatus()
                    , (byte) 0));
        }
        return amazonEmpowerList;
    }

    @Override

    /**
     * description: 同步订单任务（多线程）
     * @Param: bigList 授权信息集合
     * @return void
     * create by wujiachuang
     */
    public void addAutoGetAmazonOrdersTask(List<AmazonEmpower> bigList) {
        if (CollectionUtils.isEmpty(bigList)) {
            logger.error("异常：获取卖家服务有效的亚马逊授权信息为空，同步订单任务终止！！！");
            return;
        }
        //将大List转为5个小List，并且开启5个线程
        logger.info("亚马逊同步任务：多线程同步亚马逊订单任务开始启动---start");
        //分组并切分
        try {
            ThreadPoolUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    List<List<List<AmazonEmpower>>> listGroupBySellerId = getListsByOrderBySellerIdAndAverageAssign(bigList);
                    for (List<List<AmazonEmpower>> lists : listGroupBySellerId) {
                        if (lists.size() == 0 || lists == null) {
                            continue;
                        }
                        addAutoGetAmazonOrders(lists, true);//TODO 取消多线程同步订单，变量共享存在问题。 后期解决再开启多线程
                    }
                }
            });
        } catch (Exception e) {
            logger.error("异常：多线程开启异常:{}", e);
        }
        /*List<List<List<AmazonEmpower>>> listGroupBySellerId = getListsByOrderBySellerIdAndAverageAssign(bigList);
        for (List<List<AmazonEmpower>> lists : listGroupBySellerId) {
            if (lists.size() == 0 || lists == null) {
                continue;
            }
            addAutoGetAmazonOrders(lists, true);//TODO 取消多线程同步订单，变量共享存在问题。 后期解决再开启多线程
         *//*   try {    //同步亚马逊订单
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            addAutoGetAmazonOrders(lists, true);
//                        } catch (Exception e) {
//                            logger.error("异常：线程同步异常:{}", e);
//                        }
//                    }
//                }).start();
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        addAutoGetAmazonOrders(lists, true);
                    }
                });
            } catch (Exception e) {
                logger.error("异常：多线程开启异常:{}", e);
            }*//*
        }
        logger.info("亚马逊同步任务：多线程同步亚马逊订单启动成功---end");*/
    }

    /**
     * description: 对授权信息集合进行处理
     *
     * @return List<List < List < AmazonEmpower>>> 根据SellerID分组并切分成n份的授权信息集合
     * create by wujiachuang
     * @Param: bigList 授权信息集合
     */
    private List<List<List<AmazonEmpower>>> getListsByOrderBySellerIdAndAverageAssign(List<AmazonEmpower> bigList) {
        Map<String, List<AmazonEmpower>> collect = bigList.stream().collect(Collectors.groupingBy(AmazonEmpower::getAmazonSellerId));
        Collection<List<AmazonEmpower>> values1 = collect.values();
        List<List<AmazonEmpower>> values = new ArrayList<>(values1);
        return OrderUtils.averageAssign(values, 5);
    }

    /**
     * description: 对授权信息集合进行处理  手动同步订单用
     *
     * @return List<List < AmazonEmpower>> 根据SellerID分组
     * create by wujiachuang
     * @Param: bigList 授权信息集合
     */
    public List<List<AmazonEmpower>> getLists(List<AmazonEmpower> bigList) {
        if (CollectionUtils.isEmpty(bigList)) {
            return null;
        }
        Map<String, List<AmazonEmpower>> collect = bigList.stream().collect(Collectors.groupingBy(AmazonEmpower::getAmazonSellerId));
        Collection<List<AmazonEmpower>> values1 = collect.values();
        List<List<AmazonEmpower>> values = new ArrayList<>(values1);
        return values;
    }


    /**
     * description: 同步亚马逊订单(自动OR手动)
     * @Param: list 根据SellerID分组并切分成n份的授权信息集合
     * @Param: isAuto 是否自动同步
     * @return java.lang.String
     * create by wujiachuang
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addAutoGetAmazonOrders(List<List<AmazonEmpower>> list, boolean isAuto) {
        if (CollectionUtils.isEmpty(list)) {
            logger.error("本次手动同步订单的授权信息集合为空");
            return null;
        }
        int amazonOrderCount = 0;  //控制亚马逊订单访问速率的标记
        int amazonOrderDetailscount = 0;  //控制亚马逊订单项访问速率的标记
        int amazonItemPropertycount = 0;  //控制亚马逊商品属性速率的标记
        String msg = "";  //手动同步返回的msg;
        String sellerId;//sellerid
        String marketplaceId;//站点名称
        String mwsAuthToken;//访问Token
        Integer plAccountId;//品连账号ID
        String plAccount;//卖家品连账号
        String amazonShopName;//亚马逊店铺名
        Integer amazonShopNameId;//亚马逊店铺ID
        int updateCount; //判断是否正在同步订单的标记
        int count = 1;//判断手动同步订单是否完全完成的标记

        int lastSyncCount = 0;
        for (List<AmazonEmpower> amazonEmpowers : list) {
            lastSyncCount += amazonEmpowers.size();
        }
        logger.info("授权信息集合：{}", FastJsonUtils.toJsonString(list));
        for (List<AmazonEmpower> list1 : list) {  //list1为根据亚马逊SellerId分组好的list  TODO 每个list1为相同sellerId不同marketplaceId的对象
            //重置同步接口的三个标记（亚马逊访问限制）
            amazonOrderCount = 0;
            amazonOrderDetailscount = 0;
            amazonItemPropertycount = 0;
            //查询当前授权对象在授权表的最后更新时间和当前时间进行对比
            Date amazonLastUpdateTime = amazonEmpowerMapper.queryLastUpdateTime(list1.get(0).getAmazonSellerId());
            if (amazonLastUpdateTime != null) {
                //卖家不在授权表
                long time = new Date().getTime() - amazonLastUpdateTime.getTime();
                if (time <= 360000) {
                    if (isAuto == false) {
                        //手动
                        String translateContent = TranslateResponseContentUtils.getTranslateContent("距离下次可同步时间还有：" + OrderUtils.formatTime(360006 - time) + "，请稍后重试！");
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, translateContent);
                    }
                    continue;  //跳过当前卖家，进行下一个卖家的订单拉取
                }
            }
            amazonEmpowerMapper.insertBulk(list1); //插入更新亚马逊授权信息表
            for (AmazonEmpower amazonEmpower : list1) {   //TODO 同个卖家下不同站点的对象
                List<AmazonOrder> amazonOrderList = new ArrayList<>(); //放订单的集合
                List<AmazonOrderDetail> amazonOrderDetailList = new ArrayList<>();//放订单项的集合
                List<AmazonItemProperty> amazonItemPropertyList = new ArrayList<>();//放商品属性的集合
                //循环list集合获得品连账号、授权信息卖家ID、授权码、销售站点
                plAccount = amazonEmpower.getPlAccount();
                sellerId = amazonEmpower.getAmazonSellerId();
                marketplaceId = amazonEmpower.getMarketplaceId();
                mwsAuthToken = amazonEmpower.getMwsToken();
                synchronized (AmazonOrderServiceImpl.class) {
                    //尝试更改亚马逊授权信息表的同步订单状态
                    updateCount = amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 1);
                    logger.info("plAccount:{},sellerid:{},markerplaceid:{}修改同步数据updateCount：{}",plAccount,sellerId,marketplaceId,updateCount);
                }
                if (updateCount > 0) {//更改同步状态成功，可以执行同步订单操作
                    /*-------------------------------------------------开始进行同步订单START-----------------------------------------------*/
                    logger.info("亚马逊同步任务：品连账号：" + plAccount + ",站点ID：" + marketplaceId + ",卖家ID：" + sellerId + "，开始同步订单！！" );
                    amazonShopName = amazonEmpower.getAccount();
                    amazonShopNameId = amazonEmpower.getEmpowerId();
                    plAccountId = amazonEmpower.getPlAccountId();
                    Time lastUpdateTimeDetail = new Time();
                    if (amazonOrderMapper.selectPlAccountIsExist(plAccount, marketplaceId, sellerId).size() == 0) {
//                    在亚马逊中查无该卖家的信息，证明是新加入的卖家
                        logger.info("亚马逊同步任务：品连账号：" + plAccount + ",站点ID：" + marketplaceId + ",卖家ID：" + sellerId + "是新加入的卖家，默认开始同步时间为3天前：" + TimeUtil.getPastDate(3));
                        //自己设置需要从什么时候开始同步
                        try {
                            lastUpdateTimeDetail = OrderUtils.getLastUpdateTimeDetail(TimeUtil.getPastDate(3), "Greenwich");
                        } catch (ParseException e) {
                            logger.error("同步异常：" + plAccount + ":同步时间解析异常");
                            amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                            if (isAuto == false) {
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统获取同步时间异常，同步订单失败!");
                            }
                            continue;
                        }
                    } else {
                        //根据品连账号获取数据库最后的更新时间
                        try {
                            lastUpdateTimeDetail = OrderUtils.getLastUpdateTimeDetail(amazonOrderMapper.selectLastUpdateTimeByPlAccount(plAccount, marketplaceId, sellerId), "Greenwich");
                        } catch (ParseException e) {
                            logger.error("同步异常：品连账号：" + plAccount + ",站点ID：" + marketplaceId + ",卖家ID：" + sellerId + ":同步时间解析异常");
                            amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                            if (isAuto == false) {
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统获取同步时间异常，同步订单失败");
                            }
                            continue;
                        }
                    }
                    try {
                        if (amazonOrderCount >= 4) {
                            Thread.sleep(60000);
                        }
                        //抓取亚马逊订单数据
                        logger.info("开始抓取订单,同步时间为：{},卖家ID为{},站点ID为{},token为{}", FastJsonUtils.toJsonString(lastUpdateTimeDetail),sellerId, marketplaceId,mwsAuthToken);
                        listOrdersResponse = getAmazonOrders(lastUpdateTimeDetail, sellerId, marketplaceId, mwsAuthToken);
                        logger.info("卖家ID为{},站点ID为{},token为{}，亚马逊抓单返回结果:{}",sellerId,marketplaceId,mwsAuthToken,FastJsonUtils.toJsonString(listOrdersResponse));
                        amazonOrderCount++;
                        if (listOrdersResponse==null||listOrdersResponse.getListOrdersResult()==null||listOrdersResponse.getListOrdersResult().getOrders() == null) {
                            logger.error("listOrdersResponse为null");
                            if (isAuto == false) {
                                if (count == lastSyncCount) {     //最后一个
                                    amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                                    return "同步订单成功！";
                                } else {
                                    count++;
                                    amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                                    continue;
                                }
                            }
                            amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                            continue;
                        }
                    } catch (Exception e) {
                        if (e.getCause().toString().contains("The LastUpdatedAfter date is after the LastUpdatedBefore date.")) {
                            logger.error("同步异常：（同步时间有问题）：品连账号为" + plAccount + ",SellerId:" + sellerId + ",站点：" + marketplaceId + "错误代码为400，The LastUpdatedAfter date is after the LastUpdatedBefore " + "date" + "." + e);
                            amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                            if (isAuto == false) {
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统同步时间异常，同步订单失败!");
                            }
                            continue;  //跳过该账号信息的同步：同步时间有问题
                        }
                        if (e.getCause().toString().contains("Request is throttled")) {
                            logger.error("同步异常：（频繁请求，连接被亚马逊服务器断开）：品连账号为" + plAccount + ",SellerId:" + sellerId + ",站点：" + marketplaceId + ".错误代码为503，Request is " +
                                    "throttled" +
                                    "." + e.toString());
                            amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                            if (isAuto == false) {
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "频繁请求，同步订单失败！");
                            }
                            continue;  //跳过该账号信息的同步：频繁请求
                        }
                        if (e.getCause().toString().contains("Access denied")) {
                            logger.error("同步异常：（拒绝访问，账号可能被封了）：品连账号为" + plAccount + ",SellerId:" + sellerId + ",站点：" + marketplaceId + ".错误码401：" + e.toString());
                            amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                            if (isAuto == false) {
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "拒绝访问，同步订单失败！请联系管理员处理。");
                            }
                            continue;  //跳过该账号信息的同步：拒绝访问
                        }
                        logger.error("同步异常：品连账号为" + plAccount + ",卖家ID为" + sellerId + ",站点ID为" + marketplaceId + "，抓取亚马逊订单数据异常：" + e.toString());
                        amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                        if (isAuto == false) {
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "网络异常，同步订单失败!");
                        }
                        continue;
                    }

                    try {
                        //---同步订单到集合amazonOrderList-----------------------------start
                        AmazonOrder amazonOrder = new AmazonOrder();
                        List<Order> orders = listOrdersResponse.getListOrdersResult().getOrders();
                        logger.info("品连账号为" + plAccount + ",卖家ID为" + sellerId + ",站点ID为" + marketplaceId + "本次同步下来{}订单,数据结构{}", orders.size(), FastJsonUtils.toJsonString(orders));
                        if (orders.size() > 0) {
                            List<AmazonOrder> lis = setAmazonOrderAndInsertDb(orders, amazonOrder, sellerId, plAccount, amazonShopName, amazonShopNameId, plAccountId);
                            amazonOrderList.addAll(lis);
                            String nextToken = listOrdersResponse.getListOrdersResult().getNextToken();
                            while (nextToken != null) {
                                if (amazonOrderCount >= 6) {
                                    Thread.sleep(60000);
                                }
                                ListOrdersByNextTokenResponse amazonOrdersByNextToken = new ListOrdersByNextTokenSample()
                                        .getAmazonOrdersByNextToken(nextToken, sellerId, mwsAuthToken, marketplaceId);
                                List<Order> orders1 = amazonOrdersByNextToken.getListOrdersByNextTokenResult().getOrders();
                                List<AmazonOrder> list2 = setAmazonOrderAndInsertDb(orders1, amazonOrder, sellerId, plAccount, amazonShopName, amazonShopNameId, plAccountId);
                                amazonOrderList.addAll(list2);
                                nextToken = amazonOrdersByNextToken.getListOrdersByNextTokenResult().getNextToken();
                                amazonOrderCount++;
                            }
                        }
                        //--同步订单到amazonOrderList------------------------------end
                        if (amazonOrderList == null || amazonOrderList.size() == 0) {
                            amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                            if (isAuto == false) {
                                if (count == lastSyncCount) {     //最后一个
                                    return "同步订单成功！";
                                } else {
                                    count++;
                                    continue;
                                }
                            }
                            continue;
                        }
                    } catch (Exception e1) {
                        logger.error("同步异常：卖家账号为" + plAccount + ",卖家ID为" + sellerId + ",站点ID为" + marketplaceId + "，将亚马逊订单数据设置到集合异常：" + e1.toString());
                        amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                        if (isAuto == false) {
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，同步订单失败！");
                        }
                        continue;
                    }
                    try {
                        //同步亚马逊订单项数据到amazonOrderDetailList---------------------start
                        for (AmazonOrder amazonOrder : amazonOrderList) {
                            if (amazonOrder.getOrderStatus() == "已取消") {
                                continue;
                            }
                            String orderId = amazonOrder.getOrderId();
                            if (amazonOrderDetailscount >= 15) {
                                Thread.sleep(2000);
                            }
                            ListOrderItemsResponse listOrderItemsResponse = new ListOrderItemsSample().getListOrderItemsResponse(orderId, sellerId, mwsAuthToken, marketplaceId);
                            List<OrderItem> orderItems = listOrderItemsResponse.getListOrderItemsResult().getOrderItems();
                            List<AmazonOrderDetail> amazonOrderDetails = setAmazonOrderDetailAndInsertDb(orderItems, orderId, sellerId, marketplaceId, plAccount);
                            amazonOrderDetailList.addAll(amazonOrderDetails);
                            amazonOrderDetailscount++;
                            String nextToken = listOrderItemsResponse.getListOrderItemsResult().getNextToken();
                            while (nextToken != null) {
                                if (amazonOrderDetailscount >= 15) {
                                    Thread.sleep(2000);
                                }
                                ListOrderItemsByNextTokenResponse listOrderItemsByNextTokenResponse =
                                        new ListOrderItemsByNextTokenSample().getListOrderItemsByNextTokenResponse(nextToken, sellerId, mwsAuthToken, marketplaceId);
                                List<OrderItem> orderItems1 = listOrderItemsByNextTokenResponse.getListOrderItemsByNextTokenResult()
                                        .getOrderItems();
                                List<AmazonOrderDetail> lis = setAmazonOrderDetailAndInsertDb(orderItems1, orderId, sellerId, marketplaceId, plAccount);
                                amazonOrderDetailList.addAll(lis);
                                nextToken = listOrderItemsByNextTokenResponse.getListOrderItemsByNextTokenResult()
                                        .getNextToken();
                                amazonOrderDetailscount++;
                            }
                        }
                        //同步亚马逊订单项数据amazonOrderDetailList-----------------------end
                    } catch (Exception e) {
                        logger.error("同步异常：卖家账号为" + plAccount + ",卖家ID为" + sellerId + ",站点ID为" + marketplaceId + "，通过亚马逊订单ID抓取订单详细数据异常：" + e.toString());
                        //异常，更改同步状态为未同步
                        amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                        if (isAuto == false) {
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，同步订单失败!");
                        }
                        continue;
                    }
                    if (amazonOrderDetailList != null && amazonOrderDetailList.size() > 0) {
                        //通过亚马逊ASIN码抓取商品属性数据并持久化到数据库
                        try {
                            //同步亚马逊商品属性到amazonItemPropertyList------------------start
                            List<List<AmazonOrderDetail>> splitList = OrderUtils.split(amazonOrderDetailList, 10);
                            for (List<AmazonOrderDetail> amazonOrderDetails : splitList) {
                                List<String> stringList = new ArrayList<>();
                                amazonOrderDetails.forEach(amazonOrderDetail -> stringList.add(amazonOrderDetail.getAsin()));
                                if (amazonItemPropertycount >= 13) {
                                    Thread.sleep(1000);
                                }
                                GetMatchingProductResponse getMatchingProductResponse = new GetMatchingProductSample().get(stringList, sellerId, marketplaceId, mwsAuthToken);
                                List<GetMatchingProductResult> getMatchingProductResult = getMatchingProductResponse.getGetMatchingProductResult();
                                for (GetMatchingProductResult productResult : getMatchingProductResult) {
                                    if (productResult.getError() == null) {
                                        String asin = productResult.getASIN();//ASIN码
                                        Product product = productResult.getProduct();
                                        if (product == null) {
                                            logger.error("ASIN码：" + asin + ",调用getMatchingProduct接口返回的Product对象为null，错误码为null");
                                            continue;
                                        }
                                        Map<String, Object> itemAttributesMap = amazonOrderServiceImpl.parse(product.getAttributeSets().toXML(), "ItemAttributes");
                                        Map<String, Object> itemAttributes = (Map<String, Object>) itemAttributesMap.get("ItemAttributes");
                                        String title = (String) itemAttributes.get("Title");//商品名
                                        Map<String, Object> map = (Map<String, Object>) itemAttributes.get("SmallImage");
                                        String url = (String) map.get("URL");//商品URL
                                        AmazonItemProperty amazonItemProperty = new AmazonItemProperty();
                                        amazonItemProperty.setAsin(asin);
                                        amazonItemProperty.setItemUrl(url);
                                        amazonItemProperty.setItemTitle(title);
                                        amazonItemPropertyList.add(amazonItemProperty);
                                    }
                                }
                                amazonItemPropertycount++;
                            }
                            //同步亚马逊商品属性到amazonItemPropertyList---------------end
                        } catch (Exception e) {
                            logger.error("同步异常：卖家账号为" + plAccount + ",卖家ID为" + sellerId + ",站点ID为" + marketplaceId + "，通过亚马逊订单ASIN抓取商品数据异常：" + e);
                            //异常，更改同步状态为未同步
                            amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                            if (isAuto == false) {
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，同步订单失败!");
                            }
                            continue;
                        }
                    }
                    /*--------------------------------------------------同步订单END-----------------------------------------------------*/
                    String date = TimeUtil.DateToString2(new Date());
                    logger.info("批量插入数据库的amazonOrderList集合：{}",FastJsonUtils.toJsonString(amazonOrderList));
                    logger.info("批量插入数据库的amazonOrderDetailList集合：{}",FastJsonUtils.toJsonString(amazonOrderDetailList));
                    logger.info("批量插入数据库的amazonItemPropertyList集合：{}",FastJsonUtils.toJsonString(amazonItemPropertyList));
                    //批量插入数据库（亚马逊订单集合、亚马逊订单商品集合、亚马逊订单商品属性集合）
                    try {
                        amazonOrderService.addBulkInsert(amazonOrderList, amazonOrderDetailList, amazonItemPropertyList);
                    } catch (Exception e) {
                        logger.error("同步异常：订单、商品、属性3个批量插入异常，卖家账号为：" + plAccount + "，卖家ID：" + sellerId + "，站点ID：" + marketplaceId + e.toString());
                        amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                        if (isAuto == false) {
                            amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，同步订单失败!");
                        }
                        continue;
                    }
                    logger.info("亚马逊同步任务：同步订单并插入数据库结束，下一步进行转单~");
                    /*----------------------------------------------------转入系统订单START--------------------------------------------------*/
                    List<AmazonOrder> switchList = null;
                    try {
                        //取出转入状态为0（待转入）的list<AmazonOrder>----
                        logger.info("取出待转入的订单集合长度为..请求参数为：{},{},{}", marketplaceId, date,sellerId);
                        switchList = amazonOrderMapper.selectAmazonOrdersByPLProcessStatusAnaPlAccount(marketplaceId, date, sellerId, (byte) 0);
                        logger.info("亚马逊同步任务：取出待转入的订单集合长度为：" + switchList.size() + switchList.toString());
                    } catch (Exception e) {
                        logger.error("同步异常：平台订单转入系统订单异常：卖家账号为" + plAccount + ",卖家ID为" + sellerId + ",站点ID为" + marketplaceId + "，取出待转入的平台订单异常：" + e.toString());
                        amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                        if (isAuto == false) {
                            if (count == lastSyncCount) {     //最后一个
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，同步订单成功，自动转入系统订单失败！");
                            } else {
                                count++;
                                continue;
                            }
                        }
                        continue;
                    }
                    //待转入的平台订单根据SKU映射转为系统订单
                    try {
                        if (CollectionUtils.isNotEmpty(switchList)) {
                            for (AmazonOrder amazonOrder : switchList) {
                                if (!redissLockUtil.tryLock(amazonOrder.getOrderId(), 0, 60)) {  //TODO 加锁
                                    logger.error("亚马逊自动转入系统订单加锁失败！订单号：" + amazonOrder.getOrderId());
                                }
                            }
                        }

//                        if (CollectionUtils.isNotEmpty(switchList)) {
//                            // 发送转单消息
//                            try {
//                                orderMessageSender.sendBaseConvertOrder(switchList, OrderSourceEnum.CONVER_FROM_AMAZON);
//                            } catch (Exception e) {
//                                logger.error("Amazon发送转单消息异常", e);
//                            }
//                        }

                        msg = addTurnToSysOrderAndUpdateStatus(switchList, true);  //不管是自动同步还是手动同步订单都为转单都为自动转单
                        if (CollectionUtils.isNotEmpty(switchList)) {
                            for (AmazonOrder amazonOrder : switchList) {
                                redissLockUtil.unlock(amazonOrder.getOrderId());//TODO 释放锁
                            }
                        }
                    } catch (Exception e) {
                        logger.error("异常：平台订单转入系统订单异常：卖家账号为" + plAccount + ",卖家ID为" + sellerId + ",站点ID为" + marketplaceId + "，通过SKU映射转为系统订单失败：" + e.toString());
                        amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0);
                        if (isAuto == false) {
                            if (count == lastSyncCount) {     //最后一个
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，同步订单成功，自动转入系统订单失败！");
                            } else {
                                count++;
                                continue;
                            }
                        }
                        continue;
                    }
                    /*----------------------------------------------------转入系统订单END--------------------------------------------------*/
                    amazonEmpowerMapper.updateIsSync(plAccount, marketplaceId, sellerId, (byte) 0); //更改亚马逊授权信息表的同步状态为0，没有在进行同步任务了
                    /*返回卖家手动同步返回的msg*/
                    if (isAuto == false) {
                        if (count == lastSyncCount) {     //最后一个
                            return "同步订单成功!";
                        } else {
                            count++;
                            continue;
                        }
                    }
                } else {
                    if (isAuto == false) { /*返回卖家手动同步返回的msg*/
                        if (count ==lastSyncCount) {     //最后一个
                            return "系统正在自动同步中，手动同步失败！";
                        } else {
                            count++;
                            continue;
                        }
                    }
                    continue; //更改授权信息表同步状态失败（证明同步任务进行中），跳过此次同步该卖家订单的任务
                }
            }
        }
        return "";
    }

    /**
     * description: 批量插入数据库（多线程锁操作）
     *
     * @return create by wujiachuang
     * @Param: amazonOrderList  亚马逊订单集合
     * @Param: amazonOrderDetailList 亚马逊订单项集合
     * @Param: amazonItemPropertyList 亚马逊商品属性集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public synchronized void addBulkInsert(List<AmazonOrder> amazonOrderList, List<AmazonOrderDetail> amazonOrderDetailList, List<AmazonItemProperty>
            amazonItemPropertyList) {
        //多线程、  一个事务下进行三个集合的批量插入
        logger.info("chuang-开始插入SQL");
        amazonOrderMapper.insertBulk(amazonOrderList);
        if (amazonOrderDetailList != null && amazonOrderDetailList.size() > 0) {
            amazonOrderDetailMapper.insertBulk(amazonOrderDetailList);
        }
        if (amazonItemPropertyList != null && amazonItemPropertyList.size() > 0) {
            amazonItemPropertyMapper.insertBulk(amazonItemPropertyList);
        }
        logger.info("chuang-结束插入SQL");
    }


    /**
     * description: Amazon平台订单转系统订单  通过SKU映射，订单规则匹配然后转入系统订单、更新平台订单状态
     *
     * @return 1（转入成功） 2（部分转入成功） 3（全部转入失败） 暂无需要转入的平台订单！
     * create by wujiachuang
     * @Param: switchList 需要转入系统订单的亚马逊平台订单集合
     * @Param: isAuto  是否是自动转入（true 是 false 否）
     */
    @Override
    public String addTurnToSysOrderAndUpdateStatus(List<AmazonOrder> list, boolean isAuto) {
        logger.info("进来转单方法");
        if (CollectionUtils.isEmpty(list)) {
            return "暂无需要转入的平台订单！";
        }
        ArrayList<AmazonOrder> switchList = getSwitchList(list, isAuto); //得到待转的亚马逊订单集合
        List<SysOrderDTO> sysOrderDTOList = getSysOrderList(switchList);//将平台订单转为系统订单DTO    设置值
        try {
            logger.info("转单：亚马逊转单日志：SKU映射开始,进入映射前的数据json格式：{}", FastJsonUtils.toJsonString(sysOrderDTOList));
            skuMapService.orderMapByOrderListNew(OrderRuleEnum.platformEnm.AMAZON.getPlatform(), sysOrderDTOList);
            logger.info("转单：亚马逊转单日志：SKU映射结束" + FastJsonUtils.toJsonString(sysOrderDTOList));
        } catch (Exception e) {
            //TODO 全部转入失败
            logger.error("异常：全部订单的SKU全部映射失败！", e);
            if (isAuto == false) { //卖家手动转入
                //设置全部转入失败的订单状态回平台(手动)
                setFailOrderConvertStatusToAmazon(sysOrderDTOList);
                return getConvertResult(null);
            }
            //设置全部转入失败的订单状态回平台(自动)
            setFailOrderConvertStatusToAmazonAuto(sysOrderDTOList);
            return "";   //自动同步的 跳出方法
        }
        //TODO 全部转入成功或者部分转入成功
        SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO = sysOrderService.splitInsertSysOrderData(sysOrderDTOList);
        try {
            sysOrderService.insertSysOrderBatch(sysOrderTransferInsertOrUpdateDTO);    //TODO 批量插入采购订单
        } catch (Exception e) {
            logger.error("批量插入异常", FastJsonUtils.toJsonString(e.toString()));
            if (isAuto == false) {
                return ConvertAmzonResponseEnum.CONVERT_FAILURE.getValue();
            } else {
                return "";
            }
        }
        //统一标记完后   手工的判断转入状态返回前端
        if (isAuto == false) {   //卖家手动转入成功或者
            //   根据SKU映射后的状态来更改平台订单状态和插入系统订部分成功单（手动）  可能转过来的订单不是完整的
            updateAmazonOrderStatusAndInsertSysOrder(sysOrderDTOList);
            return getConvertResult(sysOrderDTOList);
        }
        amazonOrderService.updateAmazonOrderBatchForConvert(sysOrderTransferInsertOrUpdateDTO);   //TODO 根据SKU映射后的状态来更改平台订单状态（自动）  完整的订单
        return "";
    }

    @Override
    public void amazonConverSYS(List<SysOrderDTO> sysOrderDTOList) {
        for (SysOrderDTO sysOrder : sysOrderDTOList) {
            //TODO 加锁  其中有一个添加锁失败，则此次全部订单不转
            if (!redissLockUtil.tryLock(sysOrder.getSourceOrderId(), 10, 10)) {  //加锁
                return;
            }
        }
        try {
            logger.info("转单：亚马逊转单日志：SKU映射开始,进入映射前的数据json格式：{}", FastJsonUtils.toJsonString(sysOrderDTOList));
            skuMapService.orderMapByOrderListNew(OrderRuleEnum.platformEnm.AMAZON.getPlatform(), sysOrderDTOList);
            logger.info("转单：亚马逊转单日志：SKU映射结束" + FastJsonUtils.toJsonString(sysOrderDTOList));
        } catch (Exception e) {
            //TODO 全部转入失败
            logger.info("异常：全部订单的SKU全部映射失败！", e.toString());
            //设置全部转入失败的订单状态回平台(自动)
            setFailOrderConvertStatusToAmazonAuto(sysOrderDTOList);
            for (SysOrderDTO sysOrder : sysOrderDTOList) {   //TODO 释放锁
                redissLockUtil.unlock(sysOrder.getSourceOrderId());
            }
            return;
        }
        logger.info("出来映射后的数据：" + FastJsonUtils.toJsonString(sysOrderDTOList));
        //TODO 全部转入成功或者部分转入成功
        SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO = sysOrderService.splitInsertSysOrderData(sysOrderDTOList);
        try {
            sysOrderService.insertSysOrderBatch(sysOrderTransferInsertOrUpdateDTO);    //TODO 批量插入采购订单
        } catch (Exception e) {
            logger.error("批量插入异常", FastJsonUtils.toJsonString(e.toString()));
            return;
        }
        //   根据SKU映射后的状态来更改平台订单状态和插入系统订单（手动）
        updateAmazonOrderStatusAndInsertSysOrder(sysOrderDTOList);
        //TODO 根据SKU映射后的状态来更改平台订单状态和插入系统订单（自动）
        for (SysOrderDTO sysOrder : sysOrderDTOList) {   //TODO 释放锁
            redissLockUtil.unlock(sysOrder.getSourceOrderId());
        }
    }

    /**
     * 判断转化结果给前端传值
     *
     * @param newSysOrderList
     * @return
     */
    public String getConvertResult(List<SysOrderDTO> newSysOrderList) {
        if (newSysOrderList == null) {
            return ConvertAmzonResponseEnum.CONVERT_FAILURE.getValue();  //手动转入订单返回值给前端代表  SKU全部映射错误导致转入系统订单失败！
        }
        int count = 0;
        for (SysOrderDTO sysOrder : newSysOrderList) {
            if (sysOrder.getConverSysStatus() == OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue() || sysOrder.getConverSysStatus() == OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue()) {
                count++;
            }
        }
        if (count != 0) {
            return ConvertAmzonResponseEnum.CONVERT_PORTION_SUCCESS.getValue();    //手动转入订单返回值给前端代表   部分转入成功！
        } else {
            return ConvertAmzonResponseEnum.CONVERT_SUCCESS.getValue();    //手动转入订单返回值给前端代表  全部转入成功！
        }
    }

    /**
     * 得到待转入的亚马逊订单集合
     *
     * @param list
     * @param isAuto
     * @return
     */
    public ArrayList<AmazonOrder> getSwitchList(List<AmazonOrder> list, boolean isAuto) {
        ArrayList<AmazonOrder> switchList = new ArrayList<>(); //转入的订单集合，自己根据订单ID查出订单封装进集合
        //优化后的平台订单转入系统订单
        if (isAuto == false) {  //手动转单
            for (AmazonOrder amazonOrder : list) {
                ArrayList<AmazonOrderDetail> amazonOrderDetailList = new ArrayList<>();
                AmazonOrder order = amazonOrderMapper.selectAmazonOrderOnlyUseConvertOrder(amazonOrder.getOrderId());//TODO 根据订单ID查询亚马逊订单
                if (order == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "已存在系统订单中");
                if (order.getOrderStatus().equals(AmazonEnum.OrderStatus.PENDING_AVAILABILITY.getValue()) ||
                        order.getOrderStatus().equals(AmazonEnum.OrderStatus.PENDING.getValue())
                        || order.getOrderStatus().equals(AmazonEnum.OrderStatus.UNFULFILLABLE.getValue())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单状态有误！");
                }
                for (AmazonOrderDetail amazonOrderDetail : amazonOrder.getAmazonOrderDetails()) {
                    if (amazonOrderDetail.getAmazonOrderitemId() == null)
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
                    AmazonOrderDetail orderDetail1 = amazonOrderDetailMapper.selectAmazonOrderItem(amazonOrderDetail.getAmazonOrderitemId());
                    if (orderDetail1 == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
                    amazonOrderDetailList.add(orderDetail1);
                }
                order.setAmazonOrderDetails(amazonOrderDetailList);//TODO 重新设置订单项集合（待转入的）
                switchList.add(order);
            }
        } else {
            switchList.addAll(list);
        }
        return switchList;
    }

    /**
     * 设置全部转入失败的订单状态回平台(手动)
     *
     * @param sysOrderList
     */
    private void setFailOrderConvertStatusToAmazon(List<SysOrderDTO> sysOrderList) {
        for (SysOrderDTO sysOrder : sysOrderList) {
            for (SysOrderDetailDTO SysOrderDetailDTO : sysOrder.getSysOrderDetailList()) {
                updatePlProcessStatus(SysOrderDetailDTO, OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
            }
            //根据订单ID找到订单项集合
            amazonOrderService.updateAmazonProcessStatus(sysOrder);
        }
    }
//    private void setFailOrderConvertStatusToAmazon(List<SysOrder> sysOrderList) {
//        for (SysOrder sysOrder : sysOrderList) {
//            for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
//                updatePlProcessStatus(sysOrderDetail, OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
//            }
//            //根据订单ID找到订单项集合
//            updateAmazonProcessStatus(sysOrder);
//        }
//    }

    /**
     * 根据SKU映射后的状态来更改平台订单状态和插入系统订单（手动）
     *
     * @param newSysOrderList
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateAmazonOrderStatusAndInsertSysOrder(List<SysOrderDTO> newSysOrderList) {
        for (SysOrderDTO sysOrderDTO : newSysOrderList) {
            for (SysOrderDetailDTO sysOrderDetail : sysOrderDTO.getSysOrderDetailList()) {     //TODO 更改亚马逊订单项的转入状态
                updatePlProcessStatus(sysOrderDetail, sysOrderDetail.getConverSysDetailStatus());
            }
            updateAmazonProcessStatus(sysOrderDTO);
        }
    }
//    public void updateAmazonOrderStatusAndInsertSysOrder(List<SysOrder> newSysOrderList) {
//        for (SysOrder sysOrder : newSysOrderList) {
//            for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {     //TODO 更改亚马逊订单项的转入状态
//                updatePlProcessStatus(sysOrderDetail, sysOrderDetail.getConverSysDetailStatus());
//            }
//            updateAmazonProcessStatus(sysOrder); //TODO 根据订单项的转入状态来更改订单的转入状态
//            Byte converSysStatus = sysOrder.getConverSysStatus();
//            if (converSysStatus == OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue()) {  //全部订单项映射成功
//                setSysOrderData(sysOrder); //设置系统订单数据----------------------------------------
//                sysOrderMapper.insertSelective(sysOrder);//1插入系统订单表
//                for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
//                    //插入系统订单项表
//                    sysOrderDetailMapper.insertSelective(sysOrderDetail);
//                }
//                addLogAndSendMsg(sysOrder, userUtils.getUser().getUsername(), "异常：手动转入系统订单，添加操作日志异常!订单号为", "异常：发送订单新建消息错误，订单id为：");
//            } else if (converSysStatus == OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue()) {   //部分转入成功
//                setSysOrderData(sysOrder); //设置系统订单数据----------------------------------------
//                sysOrderMapper.insertSelective(sysOrder);//1插入系统订单表
//                for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
//                    if (sysOrderDetail.getConverSysDetailStatus() == OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue()) {
//                        //2商品SKU映射成功,插入系统订单项表
//                        sysOrderDetailMapper.insertSelective(sysOrderDetail);
//                    }
//                }
//                addLogAndSendMsg(sysOrder, userUtils.getUser().getUsername(), "异常：手动转入系统订单，添加操作日志异常!订单号为", "异常：发送订单新建消息错误，订单id为：");
//            } else {  //converSysStatus == 2 全部订单项映射失败
//                //只修改平台订单状态（前面已经增加），不新增系统订单，不做任何操作，下一个；
//                continue;
//            }
//        }
//    }

    /**
     * 添加操作日志并且发送消息通知卖家
     *
     * @param sysOrder
     * @param username
     * @param s
     * @param s2
     */
    public void addLogAndSendMsg(SysOrder sysOrder, String username, String s, String s2) {
        try {
            sysOrderLogService.insertSelective(
                    new SysOrderLog(sysOrder.getSysOrderId(),
                            OrderHandleLogEnum.Content.NEW_ORDER.newOrder(sysOrder.getSysOrderId()),
                            OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                            username));
        } catch (Exception e) {
            logger.error(s + sysOrder.getSysOrderId());
        }
        try {
            orderMessageSender.sendOrderStockOut(sysOrder.getSellerPlAccount(), sysOrder.getSysOrderId(),
                    MessageEnum.ORDER_NEW_NOTICE, null);
        } catch (JSONException e) {
            logger.error(s2 + sysOrder.getSysOrderId(), e);
        }
    }

    /**
     * 更新亚马逊订单转入状态
     *
     * @param sysOrder
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAmazonProcessStatus(SysOrderDTO sysOrder) {
        Byte plProcessStatus; //实际平台订单的转入状态
        //根据订单ID找到订单项集合
        List<AmazonOrderDetail> list = amazonOrderDetailMapper.selectAmazonOrderDetail(sysOrder.getSourceOrderId());
        List<Byte> byteList = new ArrayList<>();
        for (AmazonOrderDetail amazonOrderDetail : list) {
            //判断订单项状态得出订单状态添加进集合
            byteList.add(amazonOrderDetail.getPlProcessSubStatus());
        }
        //根据byteList集合内容判断出订单的状态是转入成功还是部分转入成功
        Collections.sort(byteList);//对list进行排序（默认升序）
        if (byteList.get(0) == byteList.get(byteList.size() - 1)) {   //全部数据相等
            if (byteList.get(0) == 0) {
                plProcessStatus = 0;   //待转入
            } else if (byteList.get(0) == OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue()) {
                plProcessStatus = OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue();   //全部转入成功
            } else {
                plProcessStatus = OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue();  //全部转入失败
            }
        } else {  //数据不全部相等
            if (byteList.contains(OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue())) {  //1包含成功
                plProcessStatus = OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue(); //部分转入成功
            } else {
                plProcessStatus = OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue();   //全部转入失败
            }
        }

        //根据订单ID更改平台订单状态
        amazonOrderMapper.updateplProcessStatusByOrderId(plProcessStatus, sysOrder.getSourceOrderId());
    }
//    public void updateAmazonProcessStatus(SysOrder sysOrder) {
//        Byte plProcessStatus; //实际平台订单的转入状态
//        //根据订单ID找到订单项集合
//        List<AmazonOrderDetail> list = amazonOrderDetailMapper.selectAmazonOrderDetail(sysOrder.getSourceOrderId());
//        List<Byte> byteList = new ArrayList<>();
//        for (AmazonOrderDetail amazonOrderDetail : list) {
//            //判断订单项状态得出订单状态添加进集合
//            byteList.add(amazonOrderDetail.getPlProcessSubStatus());
//        }
//        //根据byteList集合内容判断出订单的状态是转入成功还是部分转入成功
//        Collections.sort(byteList);//对list进行排序（默认升序）
//        if (byteList.get(0) == byteList.get(byteList.size() - 1)) {   //全部数据相等
//            if (byteList.get(0) == 0) {
//                plProcessStatus = 0;   //待转入
//            } else if (byteList.get(0) == OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue()) {
//                plProcessStatus = OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue();   //全部转入成功
//            } else {
//                plProcessStatus = OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue();  //全部转入失败
//            }
//        } else {  //数据不全部相等
//            if (byteList.contains(OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue())) {  //1包含成功
//                plProcessStatus = OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue(); //部分转入成功
//            } else {
//                plProcessStatus = OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue();   //全部转入失败
//            }
//        }
//
//        //根据订单ID更改平台订单状态
//        amazonOrderMapper.updateplProcessStatusByOrderId(plProcessStatus, sysOrder.getSourceOrderId());
//    }

    /**
     * 设置全部转入失败的订单状态回平台(自动),因为自动转每次都是整个订单转，所以无需考虑转部分订单的情况
     *
     * @param newSysOrderList
     */
    private void setFailOrderConvertStatusToAmazonAuto(List<SysOrderDTO> newSysOrderList) {
        for (SysOrderDTO sysOrder : newSysOrderList) {
            //转入失败,只更新平台订单转入状态为2
            amazonOrderMapper.updateplProcessStatusByOrderId(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue(), sysOrder.getSourceOrderId());
            //转入失败，更改平台订单项转入状态为2
            for (SysOrderDetailDTO sysOrderDetail : sysOrder.getSysOrderDetailList()) {
                updatePlProcessStatus(sysOrderDetail, OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
            }
        }
    }
//    private void setFailOrderConvertStatusToAmazonAuto(List<SysOrder> newSysOrderList) {
//        for (SysOrder sysOrder : newSysOrderList) {
//            //转入失败,只更新平台订单转入状态为2
//            amazonOrderMapper.updateplProcessStatusByOrderId(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue(), sysOrder.getSourceOrderId());
//            //转入失败，更改平台订单项转入状态为2
//            for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
//                updatePlProcessStatus(sysOrderDetail, OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
//            }
//        }
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAmazonOrderBatchForConvert(SysOrderTransferInsertOrUpdateDTO updateDTOS) {
        List<UpdateSourceOrderDetailDTO> updateSourceOrderDetailDTOList = updateDTOS.getUpdateSourceOrderDetailDTOList();
        logger.info("全部或者部分转入成功修改平台订单项状态数据：{}", FastJsonUtils.toJsonString(updateSourceOrderDetailDTOList));
        List<UpdateSourceOrderDTO> updateSourceOrderDTOList = updateDTOS.getUpdateSourceOrderDTOList();
        logger.info("全部或者部分转入成功修改平台订单状态数据：{}", FastJsonUtils.toJsonString(updateSourceOrderDTOList));
        amazonOrderMapper.updateConvertStatusBatch(updateSourceOrderDTOList);
        amazonOrderDetailMapper.updateConvertStatusStatusBatch(updateSourceOrderDetailDTOList);
    }

    /**
     * 将亚马逊平台订单转为系统订单    设置值
     *
     * @param switchList
     * @return
     */
    public List<SysOrderDTO> getSysOrderList(List<AmazonOrder> switchList) {
        List<SysOrderDTO> sysOrderDTOList = new ArrayList<>();
        for (AmazonOrder amazonOrder : switchList) {
            SysOrderDTO sysOrderDTO = new SysOrderDTO();
            SysOrderReceiveAddressDTO addressDTO = new SysOrderReceiveAddressDTO();
            //--------------设置系统订单属性
            String site = amazonOrder.getMarketplaceId();
            sysOrderDTO.setSite(site);//设置亚马逊站点ID

            String plOrderNumber = OrderUtils.getPLOrderNumber();
            sysOrderDTO.setSysOrderId(plOrderNumber);   //设置品连订单号

            String orderId = amazonOrder.getOrderId();//亚马逊订单ID
            sysOrderDTO.setSourceOrderId(orderId);//设置来源订单的ID

            Date latestShipTime = amazonOrder.getLatestShipTime();
            sysOrderDTO.setDeliverDeadline(TimeUtil.DateToString2(latestShipTime));   //设置最迟发货时间


            sysOrderDTO.setOrderSource(Integer.valueOf((OrderSourceEnum.CONVER_FROM_AMAZON.getValue())));//设置订单来源：亚马逊

            String amazonSellerAccount = amazonOrder.getAmazonShopName();
            sysOrderDTO.setPlatformSellerAccount(amazonSellerAccount);//设置卖家平台店铺名

            Integer amazonShopId = amazonOrder.getAmazonShopId();
            sysOrderDTO.setPlatformShopId(amazonShopId);//设置卖家店铺ID
            sysOrderDTO.setEmpowerId(amazonShopId);

            sysOrderDTO.setSysOrderPackageList(new ArrayList<SysOrderPackageDTO>() {{
                add(new SysOrderPackageDTO() {{
                    setEmpowerId(sysOrderDTO.getEmpowerId());
                }});
            }});

            String buyerName = amazonOrder.getBuyerName();
            sysOrderDTO.setBuyerName(buyerName);//买家姓名

            String sellerId = amazonOrder.getAmazonSellerAccount();
            sysOrderDTO.setPlatformSellerId(sellerId);  //平台卖家ID

            Integer plSellerAccountId = amazonOrder.getPlSellerAccountId();
            sysOrderDTO.setSellerPlId(plSellerAccountId);//设置卖家品连ID

            String plSellerAccount = amazonOrder.getPlSellerAccount();
            sysOrderDTO.setSellerPlAccount(plSellerAccount);//设置卖家品连账号

            sysOrderDTO.setPlatformTotalPrice(amazonOrder.getPlConvertUsaAmount());    //设置转入的订单金额(转了美元)

            Date paymentTime = amazonOrder.getPaymentTime();
            sysOrderDTO.setOrderTime(TimeUtil.DateToString2(paymentTime));  //设置下单时间

            String marketplaceId = amazonOrder.getMarketplaceId();
            sysOrderDTO.setMarketplaceId(marketplaceId); //设置站点ID

            if (amazonOrder.getState() == "") {
                addressDTO.setShipToState(amazonOrder.getCity());
            }

            String consigneeName = amazonOrder.getConsigneeName();
            addressDTO.setShipToName(consigneeName);//设置收货人姓名

            String country = amazonOrder.getCountry();
            addressDTO.setShipToCountryName(country);//设置收货目的地/国家名

            String state = amazonOrder.getState();
            addressDTO.setShipToState(state);//设置省/州名

            String countryCode = amazonOrder.getCountryCode();
            addressDTO.setShipToCountry(countryCode);//设置国家代码

            String city = amazonOrder.getCity();
            addressDTO.setShipToCity(city);//设置城市名

            if (amazonOrder.getAddress1() == "") {
                if (amazonOrder.getAddress2() == "") {  //1.2 为空
                    addressDTO.setShipToAddrStreet1(amazonOrder.getAddress3());
                } else {   //1为空，2不为空
                    if (amazonOrder.getAddress3() == "") {
                        addressDTO.setShipToAddrStreet1(amazonOrder.getAddress2());
                    } else {
                        addressDTO.setShipToAddrStreet1(amazonOrder.getAddress2());
                        addressDTO.setShipToAddrStreet2(amazonOrder.getAddress3());
                    }
                }
            } else {
                addressDTO.setShipToAddrStreet1(amazonOrder.getAddress1());//设置收货地址1
                addressDTO.setShipToAddrStreet2(amazonOrder.getAddress2());//设置收货地址2
                addressDTO.setShipToAddrStreet3(amazonOrder.getAddress3());//设置收货地址3
            }

            String postalCode = amazonOrder.getPostalCode();
            addressDTO.setShipToPostalCode(postalCode);//设置邮政编码

            String phone = amazonOrder.getPhone();
            addressDTO.setShipToPhone(phone);//设置收货人电话

            String buyerEmail = amazonOrder.getBuyerEmail();
            addressDTO.setShipToEmail(buyerEmail);//设置收货人邮箱

            sysOrderDTO.setSysOrderReceiveAddress(addressDTO);  //TODO 将地址对象设置到系统订单DTO里面
            //------------------------------------设置系统订单对象 end
            List<SysOrderDetailDTO> sysOrderDetailList = new ArrayList<>();
            StringBuffer stringBuffer = new StringBuffer();
            List<AmazonOrderDetail> amazonOrderDetails1 = amazonOrder.getAmazonOrderDetails();
            for (AmazonOrderDetail amazonOrderDetail : amazonOrderDetails1) {
                if (amazonOrderDetail.getConditionNote() != "") {
                    stringBuffer.append(amazonOrderDetail.getConditionNote());
                }
                SysOrderDetailDTO sysOrderDetailDTO = new SysOrderDetailDTO();
                //----------------设置系统订单项属性
                String conditionNote = amazonOrderDetail.getConditionNote();
                sysOrderDetailDTO.setRemark(conditionNote);//设置卖家留言
                sysOrderDetailDTO.setSysOrderId(plOrderNumber);  //设置对应系统订单的ID
                sysOrderDetailDTO.setSourceOrderId(orderId);//设置来源订单的ID
                sysOrderDetailDTO.setSourceOrderLineItemId(amazonOrderDetail.getAmazonOrderitemId()); //设置来源订单项ID
                String amazonOrderitemId = OrderUtils.getPLOrderItemNumber();
                sysOrderDetailDTO.setOrderLineItemId(amazonOrderitemId);//设置系统订单项ID
                String platformSku = amazonOrderDetail.getPlatformSku();
                sysOrderDetailDTO.setPlatformSKU(platformSku); //设置平台SKU
                Integer quantity = amazonOrderDetail.getQuantity();
                sysOrderDetailDTO.setSkuQuantity(quantity);//设置购买此SKU的数量
                sysOrderDetailList.add(sysOrderDetailDTO);//添加进系统订单项集合
                sysOrderDTO.setSysOrderDetailList(sysOrderDetailList);
            }
            if (StringUtils.isNotBlank(stringBuffer.toString())) {
                sysOrderDTO.setBuyerCheckoutMessage(stringBuffer.toString());  //设置卖家留言
            }
            sysOrderDTOList.add(sysOrderDTO);//把系统对象添加进系统订单集合
        }
        return sysOrderDTOList;
    }

    /**
     * 根据SKU映射后的状态来更改平台订单状态和插入系统订单（自动）
     *
     * @param newSysOrderList
     */
    private void updateAmazonOrderStatusAndInsertSysOrderAuto(List<SysOrder> newSysOrderList) {
        //根据转入状态插入系统订单、更改平台订单转入状态
        for (SysOrder sysOrder : newSysOrderList) {
            Byte converSysStatus = sysOrder.getConverSysStatus();  //1  转入成功
            if (converSysStatus == OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue()) {
                //转入成功
                setSysOrderData(sysOrder); //设置系统订单数据----------------------------------------
                sysOrderMapper.insertSelective(sysOrder);//1插入系统订单表
                List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();
                for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
                    //2商品SKU映射成功,插入系统订单项表
                    sysOrderDetailMapper.insertSelective(sysOrderDetail);
                    updatePlProcessStatus(null, sysOrderDetail.getConverSysDetailStatus());
                }
                //3更新平台订单转入成功的转入状态
                amazonOrderMapper.updateplProcessStatusByOrderId(converSysStatus, sysOrder.getSourceOrderId());
                //4.（可加可不加）更新平台订单项转入成功的转入状态
                //5.返回部分转入成功 提示信息
                addLogAndSendMsg(sysOrder, OrderHandleLogEnum.Operator.SYSTEM.getMsg(), "自动转入系统订单，添加操作日志异常!订单号为", "发送订单新建消息错误，订单id为：");
            } else if (converSysStatus == OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue()) {
                //部分转入成功
                setSysOrderData(sysOrder); //设置系统订单数据----------------------------------------
                sysOrderMapper.insertSelective(sysOrder);//1插入系统订单表
                List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();
                for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
                    Byte converSysDetailStatus = sysOrderDetail.getConverSysDetailStatus();
                    if (converSysDetailStatus == 1) {
                        //2商品SKU映射成功,插入系统订单项表
                        sysOrderDetailMapper.insertSelective(sysOrderDetail);
                    }
                    //3.更新平台订单项转入成功或者转入失败的状态
                    updatePlProcessStatus(null, converSysDetailStatus);
                }
                //4.更新平台订单部分转入成功的转入状态
                amazonOrderMapper.updateplProcessStatusByOrderId(converSysStatus, sysOrder.getSourceOrderId());
                addLogAndSendMsg(sysOrder, OrderHandleLogEnum.Operator.SYSTEM.getMsg(), "自动转入系统订单，添加操作日志异常!订单号为", "发送订单新建消息错误，订单id为：");
            } else {
                //转入失败,只更新平台订单转入状态
                amazonOrderMapper.updateplProcessStatusByOrderId(converSysStatus, sysOrder.getSourceOrderId());
                //更改平台订单项转入状态
                for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
//                    updatePlProcessStatus(sysOrderDetail, sysOrderDetail.getConverSysDetailStatus());
                }
            }
        }

    }
//    private void updateAmazonOrderStatusAndInsertSysOrderAuto(List<SysOrder> newSysOrderList) {
//        //根据转入状态插入系统订单、更改平台订单转入状态
//        for (SysOrder sysOrder : newSysOrderList) {
//            Byte converSysStatus = sysOrder.getConverSysStatus();  //1  转入成功
//            if (converSysStatus == OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue()) {
//                //转入成功
//                setSysOrderData(sysOrder); //设置系统订单数据----------------------------------------
//                sysOrderMapper.insertSelective(sysOrder);//1插入系统订单表
//                List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();
//                for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
//                    //2商品SKU映射成功,插入系统订单项表
//                    sysOrderDetailMapper.insertSelective(sysOrderDetail);
////                    updatePlProcessStatus(sysOrderDetail, sysOrderDetail.getConverSysDetailStatus());
//                }
//                //3更新平台订单转入成功的转入状态
//                amazonOrderMapper.updateplProcessStatusByOrderId(converSysStatus, sysOrder.getSourceOrderId());
//                //4.（可加可不加）更新平台订单项转入成功的转入状态
//                //5.返回部分转入成功 提示信息
//                addLogAndSendMsg(sysOrder, OrderHandleLogEnum.Operator.SYSTEM.getMsg(), "自动转入系统订单，添加操作日志异常!订单号为", "发送订单新建消息错误，订单id为：");
//            } else if (converSysStatus == OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue()) {
//                //部分转入成功
//                setSysOrderData(sysOrder); //设置系统订单数据----------------------------------------
//                sysOrderMapper.insertSelective(sysOrder);//1插入系统订单表
//                List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();
//                for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
//                    Byte converSysDetailStatus = sysOrderDetail.getConverSysDetailStatus();
//                    if (converSysDetailStatus == 1) {
//                        //2商品SKU映射成功,插入系统订单项表
//                        sysOrderDetailMapper.insertSelective(sysOrderDetail);
//                    }
//                    //3.更新平台订单项转入成功或者转入失败的状态
////                    updatePlProcessStatus(sysOrderDetail, converSysDetailStatus);
//                }
//                //4.更新平台订单部分转入成功的转入状态
//                amazonOrderMapper.updateplProcessStatusByOrderId(converSysStatus, sysOrder.getSourceOrderId());
//                addLogAndSendMsg(sysOrder, OrderHandleLogEnum.Operator.SYSTEM.getMsg(), "自动转入系统订单，添加操作日志异常!订单号为", "发送订单新建消息错误，订单id为：");
//            } else {
//                //转入失败,只更新平台订单转入状态
//                amazonOrderMapper.updateplProcessStatusByOrderId(converSysStatus, sysOrder.getSourceOrderId());
//                //更改平台订单项转入状态
//                for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
////                    updatePlProcessStatus(sysOrderDetail, sysOrderDetail.getConverSysDetailStatus());
//                }
//            }
//        }
//
//    }

    /**
     * 根据来源订单项ID更改传入的转入状态值
     *
     * @param sysOrderDetail
     * @param converSysDetailStatus2
     */
    private void updatePlProcessStatus(SysOrderDetailDTO sysOrderDetail, Byte converSysDetailStatus2) {
        String[] split = sysOrderDetail.getSourceOrderLineItemId().split("\\#");
        for (String sourceOrderLineItemId : split) {
            //根据订单项ID更改平台订单项转入状态
            amazonOrderDetailMapper.updatePlProcessStatus(converSysDetailStatus2, sourceOrderLineItemId);
        }
    }
//    private void updatePlProcessStatus(SysOrderDetail sysOrderDetail, Byte converSysDetailStatus2) {
//        String[] split = sysOrderDetail.getSourceOrderLineItemId().split("\\#");
//        for (String sourceOrderLineItemId : split) {
//            //根据订单项ID更改平台订单项转入状态
//            amazonOrderDetailMapper.updatePlProcessStatus(converSysDetailStatus2, sourceOrderLineItemId);
//        }
//    }

    /**
     * description: 设置系统订单数据
     *
     * @return create by wujiachuang
     * @Param: sysOrder 系统订单
     */
    public SysOrder setSysOrderData(SysOrder sysOrder) {
        //设置系统订单的总金额 ，值统计订单项转入成功的，失败的不要加入统计
        sysOrder.setOrderTrackId(OrderUtils.getPLTrackNumber());//设置品连订单跟踪号
        return sysOrder;
    }


    @Override
    /*
     * 抓取亚马逊订单数据
     * */
    public ListOrdersResponse getAmazonOrders(Time time, String sellerId, String marketplaceIds, String mwsAuthToken) throws Exception {
        ListOrdersResponse listOrdersResponse = null;
        listOrdersResponse = new ListOrdersSample().getAmazonOrders(time, sellerId, marketplaceIds, mwsAuthToken);
        return listOrdersResponse;
    }

    /*
     * 设置AmazonOrder对象并持久化到数据库
     * */
    public List<AmazonOrder> setAmazonOrderAndInsertDb(List<Order> orders, AmazonOrder amazonOrder1, String
            sellerId, String pinlianAccount, String amazonShopName, Integer amazonShopNameId, Integer plAccountId) throws
            ParseException {
        List<AmazonOrder> insertAmazonOrderList = new ArrayList<>(); //批量插入
        for (Order order : orders) {
            AmazonOrder amazonOrder = new AmazonOrder();

            amazonOrder.setAmazonSellerAccount(sellerId);//平台账号

            amazonOrder.setAmazonShopName(amazonShopName);//亚马逊店铺名

            amazonOrder.setAmazonShopId(amazonShopNameId);//亚马逊店铺ID

            amazonOrder.setPlSellerAccountId(plAccountId);//品连账号ID

            amazonOrder.setPlSellerAccount(pinlianAccount);//品连账号

            String marketplaceId = order.getMarketplaceId();//销售站点ID
            amazonOrder.setMarketplaceId(marketplaceId);

            String amazonOrderId = order.getAmazonOrderId();//亚马逊订单ID
            amazonOrder.setOrderId(amazonOrderId);

            XMLGregorianCalendar latestShipTime = order.getLatestShipDate();  //订单最迟发货时间
            Date LatestShipDate = OrderUtils.getTimeToDate(latestShipTime.toString());
            amazonOrder.setLatestShipTime(LatestShipDate);


            XMLGregorianCalendar purchaseDate = order.getPurchaseDate();//下单时间
            String last = purchaseDate.toString();
            Date timeToDate = OrderUtils.getTimeToDate(last);
            amazonOrder.setPaymentTime(timeToDate);

            XMLGregorianCalendar lastUpdateDate = order.getLastUpdateDate();//最后一次更新时间
            String lastUpdateTime = lastUpdateDate.toString();
            Date time = OrderUtils.getTimeToDate(lastUpdateTime);
            amazonOrder.setLastUpdateTime(time);

            String orderStatus = AmazonOrderUtils.getOrderStatusMap().get(order.getOrderStatus());//订单状态

            amazonOrder.setOrderStatus(orderStatus);
            //TODO 目前抓下来如果是已取消状态的订单，可能很多东西都是空串导致插入数据库除了订单状态，其余为空，后续如有需求再更改。
           /* //如果订单状态为已取消：分两种情况：1.数据库中没有这个订单的直接添加就可以，2.数据库中存在这个订单的就只更改订单状态
            if (orderStatus == "已取消") {
                AmazonOrder a = amazonOrderMapper.selectAmazonOrderByOrderId(order.getAmazonOrderId());
                if (a == null) {
                //查询数据库中没有这个订单
                    String paymentMethod = order.getPaymentMethod();//支付方式
                    amazonOrder.setPaymentMethod(paymentMethod);
                    Money orderTotal = order.getOrderTotal();
                    if (orderTotal == null) {
                        amazonOrder.setOrderTotal(new BigDecimal(0));
                        amazonOrder.setCurrencyCode("");
                        amazonOrder.setBuyerName("");
                        amazonOrder.setConsigneeName("");
                        amazonOrder.setCountry("");
                        amazonOrder.setCountryCode("");//设置国家代码
                        amazonOrder.setState("");
                        amazonOrder.setCity("");
                        amazonOrder.setDistrict("");
                        amazonOrder.setAddress1("");
                        amazonOrder.setAddress1("");
                        amazonOrder.setAddress2("");
                        amazonOrder.setPostalCode("");
                        amazonOrder.setPhone("");
                        amazonOrder.setBuyerEmail("");
                        insertAmazonOrderList.add(amazonOrder);
                        continue;
                    }
                    String amount = orderTotal.getAmount();//订单总金额
                    BigDecimal bigDecimal = new BigDecimal(amount);
                    amazonOrder.setOrderTotal(bigDecimal);

                    String huiLv = rateUtil.remoteExchangeRateByCurrencyCode(orderTotal.getCurrencyCode(), "CNY");
                    amazonOrder.setPlExchangeRate(huiLv); //汇率

                    BigDecimal RMB_amount = bigDecimal.multiply(new BigDecimal(huiLv));
                    amazonOrder.setPlConvertRmbAmount(RMB_amount);  //人民币金额

                    String currencyCode = orderTotal.getCurrencyCode();//订单总金额的货币代码
                    amazonOrder.setCurrencyCode(currencyCode);

                    String buyerName = order.getBuyerName();//买家姓名
                    amazonOrder.setBuyerName(buyerName);

                    Address shippingAddress = order.getShippingAddress();
                    System.out.println(order.getAmazonOrderId());
                    String name = shippingAddress.getName();//收货人姓名
                    amazonOrder.setConsigneeName(name);

                    String countryCode = shippingAddress.getCountryCode();
                    String country = countryCodeMapper.findCountryByISO(countryCode);//收货人国家
                    amazonOrder.setCountry(country);

                    amazonOrder.setCountryCode(countryCode);//设置国家代码

                    String stateOrRegion = shippingAddress.getStateOrRegion();//省/州
                    amazonOrder.setState(stateOrRegion);

                    String city = shippingAddress.getCity();//城市
                    amazonOrder.setCity(city);

                    String district = shippingAddress.getDistrict();//区，县
                    amazonOrder.setDistrict(district);

                    String addressLine1 = shippingAddress.getAddressLine1();//收货地址1
                    String addressLine2 = shippingAddress.getAddressLine2();//收货地址2
                    String addressLine3 = shippingAddress.getAddressLine3();//收货地址3
                    if (addressLine1 == "") {
                        //地址1为空
                        if (addressLine2== "") {
                            //地址2为空
                            amazonOrder.setAddress1(addressLine3);
                        }else{
                            if (addressLine3 == "") {
                                amazonOrder.setAddress1(addressLine2);
                            }else{
                                amazonOrder.setAddress1(addressLine2);
                                amazonOrder.setAddress2(addressLine3);
                            }
                        }
                    }else{
                        if (addressLine2 == "") {
                            amazonOrder.setAddress1(addressLine1);
                            amazonOrder.setAddress2(addressLine3);
                        }else{
                            amazonOrder.setAddress1(addressLine1);
                            amazonOrder.setAddress2(addressLine2);
                            amazonOrder.setAddress3(addressLine3);
                        }
                    }

                    String postalCode = shippingAddress.getPostalCode();//收货人邮政编码
                    amazonOrder.setPostalCode(postalCode);

                    String phone = shippingAddress.getPhone();//收货人电话
                    amazonOrder.setPhone(phone);

                    String buyerEmail = order.getBuyerEmail();//收货人邮箱
                    amazonOrder.setBuyerEmail(buyerEmail);
                }else{
                    amazonOrder=a;
                    amazonOrder.setOrderStatus("已取消");
                }
            }*/

            String paymentMethod = order.getPaymentMethod();//支付方式
            amazonOrder.setPaymentMethod(paymentMethod);
            Money orderTotal = order.getOrderTotal();
            if (orderTotal == null) {
                amazonOrder.setOrderTotal(new BigDecimal(0));
                amazonOrder.setCurrencyCode("");
                amazonOrder.setBuyerName("");
                amazonOrder.setConsigneeName("");
                amazonOrder.setCountry("");
                amazonOrder.setCountryCode("");//设置国家代码
                amazonOrder.setState("");
                amazonOrder.setCity("");
                amazonOrder.setDistrict("");
                amazonOrder.setAddress1("");
                amazonOrder.setAddress1("");
                amazonOrder.setAddress2("");
                amazonOrder.setPostalCode("");
                amazonOrder.setPhone("");
                amazonOrder.setBuyerEmail("");
                insertAmazonOrderList.add(amazonOrder);
                continue;
            }
            String amount = orderTotal.getAmount();//订单总金额
            BigDecimal bigDecimal = new BigDecimal(amount);
            amazonOrder.setOrderTotal(bigDecimal);

            String huiLv = rateUtil.remoteExchangeRateByCurrencyCode(orderTotal.getCurrencyCode(), "USD"); //转成美元
            amazonOrder.setPlExchangeRate(huiLv); //汇率

            BigDecimal USD_amount = bigDecimal.multiply(new BigDecimal(huiLv));
            amazonOrder.setPlConvertUsaAmount(OrderUtils.calculateMoney(USD_amount, true));  //转成美元金额

            String currencyCode = orderTotal.getCurrencyCode();//订单总金额的货币代码
            amazonOrder.setCurrencyCode(currencyCode);

            String buyerName = order.getBuyerName();//买家姓名
            amazonOrder.setBuyerName(buyerName);


            Address shippingAddress = order.getShippingAddress();

            if (shippingAddress != null) {
                System.out.println(order.getAmazonOrderId());
                String name = shippingAddress.getName();//收货人姓名
                amazonOrder.setConsigneeName(name);

                String countryCode = shippingAddress.getCountryCode();
                String country = countryCodeMapper.findCountryByISO(countryCode);//收货人国家
                amazonOrder.setCountry(country);

                amazonOrder.setCountryCode(countryCode);//设置国家代码

                String stateOrRegion = shippingAddress.getStateOrRegion();//省/州
                amazonOrder.setState(stateOrRegion);

                String city = shippingAddress.getCity();//城市
                amazonOrder.setCity(city);

                String district = shippingAddress.getDistrict();//区，县
                amazonOrder.setDistrict(district);

                String addressLine1 = shippingAddress.getAddressLine1();//收货地址1
                String addressLine2 = shippingAddress.getAddressLine2();//收货地址2
                String addressLine3 = shippingAddress.getAddressLine3();//收货地址3
                if (StringUtils.isBlank(addressLine1)) {
                    //地址1为空
                    if (StringUtils.isBlank(addressLine2)) {
                        //地址2为空
                        amazonOrder.setAddress1(addressLine3);
                    } else {
                        if (StringUtils.isBlank(addressLine3)) {
                            amazonOrder.setAddress1(addressLine2);
                        } else {
                            amazonOrder.setAddress1(addressLine2);
                            amazonOrder.setAddress2(addressLine3);
                        }
                    }
                } else {
                    if (StringUtils.isBlank(addressLine2)) {
                        amazonOrder.setAddress1(addressLine1);
                        amazonOrder.setAddress2(addressLine3);
                    } else {
                        amazonOrder.setAddress1(addressLine1);
                        amazonOrder.setAddress2(addressLine2);
                        amazonOrder.setAddress3(addressLine3);
                    }
                }

                String postalCode = shippingAddress.getPostalCode();//收货人邮政编码
                amazonOrder.setPostalCode(postalCode);

                String phone = shippingAddress.getPhone();//收货人电话
                amazonOrder.setPhone(phone);

                String buyerEmail = order.getBuyerEmail();//收货人邮箱
                amazonOrder.setBuyerEmail(buyerEmail);
            } else {
                amazonOrder.setBuyerEmail("");
                amazonOrder.setPhone("");
                amazonOrder.setPostalCode("");
                amazonOrder.setAddress1("");
                amazonOrder.setAddress2("");
                amazonOrder.setAddress3("");
                amazonOrder.setDistrict("");
                amazonOrder.setCity("");
                amazonOrder.setState("");
                amazonOrder.setCountry("");
                amazonOrder.setConsigneeName("");
                amazonOrder.setCountryCode("");//设置国家代码
            }

            insertAmazonOrderList.add(amazonOrder);
        }
        return insertAmazonOrderList;


    }

    @Override
    /*
     * 通过订单ID查询订单详情
     * */
    public AmazonOrder getAmazonOrderDetailByOrderId(String orderId) {
        AmazonOrder amazonOrder = null;
        amazonOrder = amazonOrderMapper.getAmazonOrderDetailByOrderId(orderId);
        if (!amazonOrder.getOrderStatus().equalsIgnoreCase(AmazonEnum.OrderStatus.UNSHIPPED.getValue()) && !amazonOrder.getOrderStatus().equalsIgnoreCase(AmazonEnum.OrderStatus.CANCELED.getValue())) {
            amazonOrder.setDeliveryTime(amazonOrder.getLastUpdateTime());
        }
        if (amazonOrder == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
        //翻译
        amazonOrder.setOrderStatus(com.rondaful.cloud.common.utils.Utils.translation(amazonOrder.getOrderStatus()));
        return amazonOrder;
    }

    /*
     * 设置AmazonOrderDetail对象并持久化到数据库
     * */
    public List<AmazonOrderDetail> setAmazonOrderDetailAndInsertDb(List<OrderItem> orderItems, String
            orderId, String sellerId, String markerPlaceId, String plAccount) {
        List<AmazonOrderDetail> list = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            AmazonOrderDetail amazonOrderDetail = new AmazonOrderDetail();
            amazonOrderDetail.setOrderId(orderId);//订单ID

            amazonOrderDetail.setPlSellerAccount(plAccount);
            amazonOrderDetail.setMarketplaceId(markerPlaceId);
            amazonOrderDetail.setAmazonSellerAccount(sellerId);

            String orderItemId = orderItem.getOrderItemId();//订单项ID
            amazonOrderDetail.setAmazonOrderitemId(orderItemId);

            String asin = orderItem.getASIN();
            amazonOrderDetail.setAsin(asin);//asin码

            String sellerSKU = orderItem.getSellerSKU();//平台SKU
            amazonOrderDetail.setPlatformSku(sellerSKU);

            String title = orderItem.getTitle();
            amazonOrderDetail.setItemTitle(title);  //商品名

            if (orderItem.getItemPrice() != null) {
                String currencyCode = orderItem.getItemPrice().getCurrencyCode();
                amazonOrderDetail.setItemCurrencyCode(currencyCode);//商品金额货币
            } else {
                amazonOrderDetail.setItemCurrencyCode("");
            }

            int quantityOrdered = orderItem.getQuantityOrdered();//商品数量
            amazonOrderDetail.setQuantity(quantityOrdered);

            if (orderItem.getItemPrice() != null) {
                String amount = orderItem.getItemPrice().getAmount();
                double d = Double.parseDouble(amount);
//                BigDecimal bigDecimal = new BigDecimal(d/quantityOrdered);
//                amazonOrderDetail.setItemPrice(new BigDecimal(d / quantityOrdered).setScale(4, BigDecimal.ROUND_DOWN));  //商品单价
                amazonOrderDetail.setItemPrice(OrderUtils.calculateMoney(new BigDecimal(d / quantityOrdered), true));  //商品单价
            } else {
                amazonOrderDetail.setItemPrice(BigDecimal.valueOf(0));
            }

            if (orderItem.getShippingPrice() != null) {
                String amount1 = orderItem.getShippingPrice().getAmount();
                BigDecimal bigDecimal2 = new BigDecimal(amount1);
                amazonOrderDetail.setShippingPrice(bigDecimal2);//商品运费

                String currencyCode1 = orderItem.getShippingPrice().getCurrencyCode();
                amazonOrderDetail.setShippingCurrencyCode(currencyCode1);//商品运费货币
            } else {
                amazonOrderDetail.setShippingPrice(BigDecimal.valueOf(0));//商品运费
                amazonOrderDetail.setShippingCurrencyCode("");//商品运费货币
            }

            String conditionNote = orderItem.getConditionNote();
            amazonOrderDetail.setConditionNote(conditionNote);//卖家商品备注

            list.add(amazonOrderDetail);
        }
        return list;
    }

    public Map<String, Object> parse(String soap, String type) throws DocumentException, NullPointerException {
        Map<String, Object> map = new HashMap<String, Object>();
        Document doc = DocumentHelper.parseText(soap);// 报文转成doc对象
        Element root = doc.getRootElement();// 获取根元素，准备递归解析这个XML树
        map.put(type, getCode(root.element(type)));
        return map;
    }

    public Map<String, Object> getCode(Element root) throws NullPointerException {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (null != root.elements()) {
            @SuppressWarnings("unchecked")
            List<Element> list = root.elements();// 如果当前跟节点有子节点，找到子节点
            for (Element e : list) {// 遍历每个节点
                if (e.elements().size() > 0) {
                    resultMap.put(e.getName(), getCode(e));// 当前节点不为空的话，递归遍历子节点；
                }
                if (e.elements().size() == 0) {
                    resultMap.put(e.getName(), e.getTextTrim());
                } // 如果为叶子节点，那么直接把名字和值放入map
            }
            return resultMap;
        }
        return null;
    }


    /* int amazonItemPropertycount=0;
    @Override
    public List<AmazonItemProperty> GetOrderItemsPropertyByASINAndInsertDb(List<AmazonOrderDetail> list, String sellerId, String marketplaceId, String
            mwsAuthToken) throws
            InterruptedException,
            DocumentException {
        List<AmazonItemProperty> amazonItemPropertyList = new ArrayList<>();
        if (list.size() == 0) {
            return null;
        }

        for (AmazonOrderDetail amazonOrderDetail : list) {
            String asin = amazonOrderDetail.getAsin();
            if (amazonItemPropertycount >= 20) {
                Thread.sleep(1000);
            }
            GetMatchingProductResponse getMatchingProductResponse = GetMatchingProductSample.get(asin,sellerId,marketplaceId,mwsAuthToken);
            List<GetMatchingProductResult> getMatchingProductResult = getMatchingProductResponse.getGetMatchingProductResult();
            for (GetMatchingProductResult productResult : getMatchingProductResult) {
                Product product = productResult.getProduct();
                Map<String, Object> itemAttributesMap = amazonOrderServiceImpl.parse(product.getAttributeSets().toXML(),"ItemAttributes");
                Map<String, Object> itemAttributes = (Map<String, Object>) itemAttributesMap.get("ItemAttributes");
                String title = (String) itemAttributes.get("Title");//商品名
                Map<String, Object> map = (Map<String, Object>) itemAttributes.get("SmallImage");
                String url = (String) map.get("URL");//商品URL
                AmazonItemProperty amazonItemProperty = new AmazonItemProperty();
                amazonItemProperty.setAsin(asin);
                amazonItemProperty.setItemUrl(url);
                amazonItemProperty.setItemTitle(title);
                amazonItemPropertyList.add(amazonItemProperty);
            }
            amazonItemPropertycount+=1;
        }
        return amazonItemPropertyList;

    }*/

     /*int amazonOrderDetailscount = 0;
    @Override
    *//*
     * 循环亚马逊订单ID来查询订单商品列表并持久化到数据库，有nextToken的再循环查询下一页订单商品列表并持久化到数据库
     * *//*
    public List<AmazonOrderDetail> GetOrderItemsByOrderIdAndInsertDb(List<AmazonOrder> lists, String sellerId, String
            mwsAuthToken, String markerPlaceId, String plAccount)
            throws
            Exception {
        List<AmazonOrderDetail> list = new ArrayList<>();
        if (lists.size() == 0) {
            return null;
        }
        for (AmazonOrder amazonOrder : lists) {
            if (amazonOrder.getOrderStatus() == "已取消") {
                continue;
            }
            String orderId = amazonOrder.getOrderId();
            if (amazonOrderDetailscount >= 30) {
                Thread.sleep(2000);
            }
            ListOrderItemsResponse listOrderItemsResponse = ListOrderItemsSample.getListOrderItemsResponse(orderId,sellerId,mwsAuthToken);
            List<OrderItem> orderItems = listOrderItemsResponse.getListOrderItemsResult().getOrderItems();
            List<AmazonOrderDetail> amazonOrderDetails = setAmazonOrderAndInsertDb(orderItems, orderId, sellerId, markerPlaceId, plAccount);
            list.addAll(amazonOrderDetails);
            amazonOrderDetailscount++;
            String nextToken = listOrderItemsResponse.getListOrderItemsResult().getNextToken();
            while (nextToken != null) {
                if (amazonOrderDetailscount >= 30) {
                    Thread.sleep(2000);
                }
                ListOrderItemsByNextTokenResponse listOrderItemsByNextTokenResponse =
                        ListOrderItemsByNextTokenSample.getListOrderItemsByNextTokenResponse(nextToken,sellerId,mwsAuthToken);
                List<OrderItem> orderItems1 = listOrderItemsByNextTokenResponse.getListOrderItemsByNextTokenResult()
                        .getOrderItems();
                List<AmazonOrderDetail> list1 = setAmazonOrderAndInsertDb(orderItems1, orderId, sellerId, markerPlaceId, plAccount);
                list.addAll(list1);
                nextToken = listOrderItemsByNextTokenResponse.getListOrderItemsByNextTokenResult()
                        .getNextToken();
                amazonOrderDetailscount++;
            }

        }
        return  list;
    }
*/
}

