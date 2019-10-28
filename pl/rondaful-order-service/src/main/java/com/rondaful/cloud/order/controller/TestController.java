package com.rondaful.cloud.order.controller;

import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.MessageEnum;
import com.rondaful.cloud.common.utils.MD5;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.entity.commodity.Commodity;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.orderRule.SKUMapMailRuleDTO;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.mapper.AmazonOrderMapper;
import com.rondaful.cloud.order.mapper.SysOrderMapper;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.rabbitmq.TestSender;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.remote.RemoteFinanceService;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.service.ICommodityService;
import com.rondaful.cloud.order.service.IEbayOrderHandleService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.service.ISystemOrderService;
import com.rondaful.cloud.order.service.TriggerConverSYSService;
import com.rondaful.cloud.order.service.impl.SysOrderServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class TestController extends BaseController {
    @Autowired
    private ISysOrderService sysOrderService;
    private final static Logger log = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private RemoteFinanceService remoteFinanceService;
    @Autowired
    private RemoteSupplierService remoteSupplierService;
    @Value("${spring.commodity.datasource.name}")
    private String a;
    @Autowired
    private AmazonOrderMapper amazonOrderMapper;
    @Autowired
    private ICommodityService commodityService;
    @Autowired
    private RemoteSellerService remoteSellerService;
    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;
    @Autowired
    private IEbayOrderHandleService ebayOrderHandleService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TestSender helloSender;
    @Autowired
    private OrderMessageSender orderMessageSender;
    @Autowired
    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private SysOrderMapper mapper;
    @Autowired
    private SysOrderMapper sysOrderMapper;

    @ApiOperation(value = "测试", notes = "")
    @GetMapping("/map")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String testDemo() {
        return a;
    }

    @ApiOperation(value = "查询所有商品", notes = "page当前页码，row每页显示行数", response = Commodity.class)
    @GetMapping("/findAll")
    @RequestRequire(require = "page, row", parameter = String.class)
    public Page findAll(String page, String row) {
        Page.builder(page, row);
        Page p = commodityService.page(null);
        return p;
    }

    @ApiOperation(value = "更新商品信息", notes = "")
    @PostMapping("/update")
    public int update(Commodity commodity) throws NoSuchFieldException, IllegalAccessException {
        //Commodity co = commodityService.selectByPrimaryKey(commodity.getId());
        //co.setCommodityName(commodity.getCommodityName());
        int result = commodityService.updateByPrimaryKeySelective(commodity);
        //int i = 1/0;
        return result;
    }

    @ApiOperation(value = "手动推消息到订单mq", notes = "")
    @PostMapping("/orderMqMessage")
    public int orderMqMessage() throws NoSuchFieldException, IllegalAccessException, JSONException {
//         orderMessageSender.sendOrderStockOut11111("这是一个假的卖家名称","这是一个假的订单号", MessageEnum.ORDER_DELIVERY_NOTICE,"");
        orderMessageSender.sendOrderStockOut("wujiachuang", "444", MessageEnum.ORDER_EXCEPTION_NOTICE, "缺货了！！！！");
        return 6;
    }

    @ApiOperation(value = "查询商品", notes = "")
    @PostMapping("/orderMid")
    @ApiImplicitParam(name = "i", value = "1", dataType = "Integer", paramType = "query")
    public String orderMid(Integer i) {
        String commodityListBySystemSKU = null;
        if (i == 1) {
            commodityListBySystemSKU = remoteCommodityService.getCommodityListBySystemSKU(new ArrayList<String>() {{
                this.add("123");
            }});
        } else if (i == 2) {
            commodityListBySystemSKU = remoteCommodityService.getSkuList("1", "10", null, null, null, null, null,
                    null, null, null);
        }
        return commodityListBySystemSKU;
    }
@Autowired
private ISystemOrderService systemOrderService;
    @GetMapping("/send")
    public void send() {
        helloSender.send();
    }

//    @ApiOperation(value = "查询可用库存接口")
////    @GetMapping("/getWareHousetInventory")
////    @ApiImplicitParams({@ApiImplicitParam(name = "list", value = "查询可用库存接收对象结合", paramType = "body", required = true)})
////    public Map<String, Object> getWareHousetInventory(@RequestBody List<WarehouseInventory> list) throws Exception {
////        return systemOrderCommonService.getWareHousetInventory(list);
////    }

 /*   @ApiOperation(value = "根据仓库code查询仓库服务商数据")
    @PostMapping("/getGCAuthorizeByCompanyCode")
    @ApiImplicitParam(name = "set", value = "谷仓CompanyCode集合", required = true)
    public Map<String, AuthorizeDTO> getGCAuthorizeByCompanyCode(@RequestBody Set<String> set) {
        return systemOrderCommonService.getGCAuthorizeByCompanyCode(set);
    }*/

//    @ApiOperation(value = "根据ERP计算预估物流费")
//    @PostMapping("/calculateErpTrialBySKUS")
//    @ApiImplicitParam(name = "sysOrder", value = "谷仓CompanyCode集合", required = true)
//    public String calculateErpTrialBySKUS(@RequestBody SysOrder sysOrder) throws Exception {
//        return systemOrderCommonService.calculateErpTrialBySKUS(sysOrder);
//    }

    @ApiOperation(value = "获取商品最迟发货时间")
    @PostMapping("/getItemListDeadline")
//    @CacheEvict(value = "commodityListCache", allEntries = true)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "itemIDList", dataType = "Array", value = "平台订单ItemID集合", required = true),
//            @ApiImplicitParam(name = "empowerId", dataType = "Integer", value = "店铺授权ID", required = false)})
    public Map<String, Integer> getItemListDeadline(@ApiParam(name = "itemIDList", value = "平台订单ItemID集合，多个id以逗号隔开传递", required = true)
//                                                    @RequestParam("itemIDList") List<String> itemIDList,
                                                    @RequestBody List<String> itemIDList,
                                                    @ApiParam(name = "empowerId", value = "店铺授权ID", required = true)
                                                    @RequestParam("empowerId") Integer empowerId) {
        return ebayOrderHandleService.getDispatchTimeMax(empowerId, itemIDList);
    }

    @PostMapping("/deliverAmazonSYSOrder")
    @ApiOperation("单个订单发货")
    @ApiImplicitParams({@ApiImplicitParam(name = "sysOrderId", value = "请求发货的系统订单ID", paramType = "query", required = true)})
    public void deliverGoodSingle(String sysOrderId) throws Exception {
        systemOrderCommonService.deliverAmazonSYSOrder(sysOrderId);
    }
    @Autowired
    private SysOrderServiceImpl service;
    @Autowired
    private TriggerConverSYSService triggerConverSYSService;
    @ApiOperation(value = "测试")
    @GetMapping("/test")
    @ApiImplicitParam(name = "test", value = "测试", required = true)
    public String test() throws Exception {
        SKUMapMailRuleDTO dto=new SKUMapMailRuleDTO();
        dto.setEmpowerID(1373);
        dto.setPlatform("amazon");
        HashMap<String, String> map = new HashMap<>();
        map.put("JE0098400|6997407403", "A-1-75013512-540706");
        dto.setSkuRelationMap(map);
        triggerConverSYSService.triggerSKUMapAndMailRuleMate(Collections.singletonList(dto));

        String result = remoteFinanceService.autopayVerify(3);
        Boolean flag = Boolean.valueOf(Utils.returnRemoteResultDataString(result, "财务服务异常"));
//        boolean b = service.cancelOrderByWms("554","11", "LXL0022");
//        String result = remoteSupplierService.updateLocalShipping(573, "06158592001", 1);
//        System.out.println(6);
//        systemOrderService.pushDeliverInfoToWareHouse("TK163333846AlpsjdTl");
//        systemOrderService.pushDeliverInfoToWareHouse("TK160742624hDlVgfX1");
//        String[] strings = new String[2];
//        strings[0] = "A-2-E062737D-723488";
//        strings[1] = "90968320001";
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.set(0, "A-2-E062737D-723488");
//        jsonArray.set(1, "90968320001");
//        System.out.println(jsonArray.toString());
//        String s = remoteSupplierService.getsBySku(180, jsonArray.toString());
//        String s1 = Utils.returnRemoteResultDataString(s, "");
//        List<InventoryDTO> inventoryDTOS = JSONObject.parseArray(s1, InventoryDTO.class);
//        for (InventoryDTO inventoryDTO : inventoryDTOS) {
//            System.out.println(inventoryDTO.toString());
//        }

//        JSONArray jsonArray = new JSONArray();
//        jsonArray.set(0, "46689024004");
//        jsonArray.set(1, "59884800005");
//        String result = remoteSupplierService.getsInvBySku(jsonArray.toString());
//        String s = Utils.returnRemoteResultDataString(result, "");
//        List<OrderInvDTO> orderInvDTOS = JSONObject.parseArray(s, OrderInvDTO.class);
//        orderInvDTOS.forEach(x-> System.out.println(x));
//        String o = JSONObject.toJSON(new ArrayList<String>() {{
//            add("191");
//            add("182");
//        }}).toString();
//        String result = remoteSupplierService.getWarehouseByIds(o);
//        String data = Utils.returnRemoteResultDataString(result, "");
//        List<WarehouseDTO> warehouseDTOS = JSONObject.parseArray(data, WarehouseDTO.class);
//        for (WarehouseDTO warehouseDTO : warehouseDTOS) {
//            System.out.println(warehouseDTO);
//        }
//        String result = remoteSupplierService.getsByType("GOODCANG");
//        String data = Utils.returnRemoteResultDataString(result, "");
//        List<Integer> ids = JSONObject.parseArray(data, Integer.class);
//        System.out.println(ids);
//        System.out.println(new GetWarehouseIdsUtils().getWarehouseIdsByServiceCode("RONDAFUL"));
//        System.out.println(new GetWarehouseIdsUtils().getWarehouseIdsByServiceCode("GOODCANG"));


//        String data = Utils.returnRemoteResultDataString(remoteSupplierService.getsByType("GOODCANG"), "供应商服务异常");
//        List<Integer> ids = JSONObject.parseArray(data, Integer.class);
//        //1,查询出谷仓所有配货中的订单id
//        List<SysOrder> sysOrderList = sysOrderMapper.getOrderByWarehouseId(ids);
//        for (SysOrder sysOrder : sysOrderList) {
//            System.out.println(sysOrder);
//        }
//        boolean goodcangOrder = systemOrderCommonService.isGoodCangWarehouse("181");
//        System.out.println(goodcangOrder);
//        SysOrder sysOrder = new SysOrder();
//        sysOrder.setOrderSource((byte) 5);
//        sysOrder.setOrderAmount(BigDecimal.valueOf(55));
//        sysOrder.setDeliveryWarehouseId("189");
//        sysOrder.setShipToCountry("GB");
//        sysOrder.setShipToPostalCode("1");
//        sysOrder.setDeliveryMethodCode("15883_001");
//        SysOrderPackageDetailDTO dto = new SysOrderPackageDetailDTO();
//        dto.setSku("C-1-AC387190-356525");
//        dto.setSkuQuantity(2);
//        dto.setSupplierId(100);
//        dto.setFreeFreight(0);
//        sysOrder.setSkuList(new ArrayList<SysOrderPackageDetailDTO>(){{
//            add(dto);}});
//        String str = FastJsonUtils.toJsonString(sysOrder);
//        System.out.println(str);
//        Map<String, BigDecimal> grossMargin = sysOrderService.getGrossMargin(sysOrder);
//        System.out.println(grossMargin);
        return null;
//        return systemOrderCommonService.test();
    }

    @PostMapping("/test/getToken")
    public String getToken(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        TreeMap<String,String> sortMap=new TreeMap<>();
        Enumeration<String> params=attributes.getRequest().getParameterNames();
        while(params.hasMoreElements()){
            String name=params.nextElement();
            String value=attributes.getRequest().getParameter(name);
            sortMap.put(name,value);
        }
        String appSecret=sortMap.get("appSecret");
        String appId=sortMap.get("appId");
        sortMap.remove("appId");
        sortMap.remove("appSecret");
        return MD5.getAccessToken(sortMap,appId,appSecret);
    }

    @GetMapping("/test/jsoup")
    public String jsoup() throws IOException {
        Connection conn = Jsoup.connect("https://www.ebay.com.au/itm/392304117552?ViewItem=&item=392304117552&vti=Strap%20Colour%09Cyan%20Blue").timeout(5000);
        Document doc = conn.get();
        Elements elements = doc.select("img[id=icImg]");
        if (elements.size() > 0 ) {

            String src = elements.get(0).attr("src");
            return src;

        }
        return "fail";
    }


    @GetMapping("/test/convertOrder")
    public String convertOrder()  {
        List list = new ArrayList();
        EbayOrder ebayOrder = new EbayOrder();
        ebayOrder.setCountry("cn");
        ebayOrder.setCityName("sz");
        list.add(ebayOrder);
        orderMessageSender.sendBaseConvertOrder(list, OrderSourceEnum.CONVER_FROM_EBAY);
        return "success";
    }







}
