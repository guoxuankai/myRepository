package com.brandslink.cloud.logistics.thirdLogistics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.ConstantAli;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.FileService;
import com.brandslink.cloud.common.utils.HttpUtil;
import com.brandslink.cloud.logistics.entity.LogisticsDeliverCallBack;
import com.brandslink.cloud.logistics.entity.centre.BaseOrder;
import com.brandslink.cloud.logistics.enums.CommonEnum;
import com.brandslink.cloud.logistics.service.ICentralServerService;
import com.brandslink.cloud.logistics.strategy.HandlerType;
import com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin.MiaoXinOrder;
import com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin.MiaoXinPrintVO;
import com.brandslink.cloud.logistics.thirdLogistics.enums.MiaoXinAPI;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RemoteMiaoXinLogisticsService {

    /**
     * 身份认证
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    String selectAuth(@NotBlank(message = "用户名不能为空") String username, @NotBlank(message = "密码不能为空") String password);

    /**
     * 渠道列表
     *
     * @return
     */
    String getProductList();

    /**
     * 创建淼信物流运单
     *
     * @param order 运单数据
     * @return
     */
    String createOrderApi(@NotNull(message = "创建运单数据不能为空") @Valid MiaoXinOrder order) throws UnsupportedEncodingException;

    /**
     * 批量添加订单
     *
     * @param orders
     * @return
     */
    String createOrderBatchApi(@NotEmpty(message = "运单数据集合不能为空") @Valid List<MiaoXinOrder> orders) throws UnsupportedEncodingException;

    /**
     * 标记发货
     *
     * @param customer_id               客户ID，必填
     * @param order_customerinvoicecode 原单号，必填
     * @return
     */
    String postOrderApi(@NotBlank(message = "客户ID，必填") String customer_id, @NotBlank(message = "原单号，必填") String order_customerinvoicecode);

    /**
     * 打印标签
     *
     * @param vo 打印标签数据对象
     * @return
     */
    String printLabel(@NotNull(message = "打印标签数据对象不能为空") @Valid MiaoXinPrintVO vo) throws Exception;

    /**
     * 获取打印类型printType
     *
     * @return
     */
    String selectLabelType();

    /**
     * 查询运单轨迹
     *
     * @param documentCode 订单号
     * @return
     */
    String selectTrack(@NotBlank(message = "订单号，必填") String documentCode);

    /**
     * 获取跟踪号
     *
     * @param documentCode 订单号
     * @param order_id     订单ID
     * @return
     */
    String getOrderTrackingNumber(String documentCode, Long order_id);

    /**
     * 更新预报重量
     *
     * @param customerId 客户ID
     * @param orderNo    订单号
     * @param weight     重量
     * @return
     */
    String updateOrderWeightByApi(@NotBlank(message = "客户ID不能为空") String customerId,
                                  @NotBlank(message = "订单号不能为空") String orderNo,
                                  @NotNull(message = "重量不能为空") BigDecimal weight);

    /**
     * 修改保险金额
     *
     * @param customerId
     * @param documentCode
     * @param insuranceValue
     * @return
     */
    String modifyInsurance(@NotBlank(message = "客户ID不能为空") String customerId,
                           @NotBlank(message = "订单号，必填") String documentCode,
                           @NotNull(message = "保险金额不能为空") @Digits(integer = 18, fraction = 3, message = "保险金额限制为18位整数3位小数") BigDecimal insuranceValue);


    @Validated
    @Service
    @HandlerType("MiaoXin")
    class RemoteMiaoXinLogisticsServiceImpl implements RemoteMiaoXinLogisticsService, BaseHandler {
        @Autowired
        ICentralServerService centralServerService;

        @Value("${logistics.miaoxin.url1}")
        private String url1;

        @Value("${logistics.miaoxin.url2}")
        private String url2;

        private final static Logger _log = LoggerFactory.getLogger(RemoteMiaoXinLogisticsServiceImpl.class);

        @Override
        public String selectAuth(@NotBlank(message = "用户名不能为空") String username, @NotBlank(message = "密码不能为空") String password) {
            String data = HttpUtil.post(url1 + MiaoXinAPI.SELECTAUTH.getApi(), new HashMap<String, String>(1) {{
                this.put("username", username);
                this.put("password", password);
            }});
            _log.info("_________淼信物流身份认证返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "淼信物流身份认证返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"true".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "淼信物流身份认证失败。。。");
            }
            return data;
        }

        @Override
        public String getProductList() {
            String data = HttpUtil.post(url1 + MiaoXinAPI.GETPRODUCTLIST.getApi(), null);
            _log.info("_________获取淼信物流渠道列表返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取淼信物流渠道列表返回结果为空。。。");
            }
            return data;
        }

        @Override
        public String createOrderApi(@NotNull(message = "创建运单数据不能为空") @Valid MiaoXinOrder order) throws UnsupportedEncodingException {
            _log.info("_________创建淼信物流运单传入参数_______{}_______", JSON.toJSONString(order));
            String data = HttpUtil.post(url1 + MiaoXinAPI.CREATEORDERAPI.getApi(), new HashMap<String, String>(1) {{
                this.put("param", JSON.toJSONString(order));
            }});
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "创建淼信物流运单返回结果为空。。。");
            }
            String str = URLDecoder.decode(data, "utf-8");
            _log.info("_________创建淼信物流运单返回结果_______{}_______", str);
            JSONObject jsonObject = JSONObject.parseObject(str);
            if (!"true".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "创建淼信物流运单失败，" + jsonObject.getString("message"));
            }
            return str;
        }

        @Override
        public String createOrderBatchApi(@NotEmpty(message = "运单数据集合不能为空") @Valid List<MiaoXinOrder> orders) throws UnsupportedEncodingException {
            _log.info("_________批量创建淼信物流运单订单个数为____________[{}]_________订单数据为_______{}_______", orders.size(), JSON.toJSONString(orders));
            String data = HttpUtil.post(url1 + MiaoXinAPI.CREATEORDERBATCHAPI.getApi(), new HashMap<String, String>(1) {{
                this.put("param", JSON.toJSONString(orders));
            }});
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "创建淼信物流运单返回结果为空。。。");
            }
            String str = URLDecoder.decode(data, "utf-8");
            _log.info("_________创建淼信物流运单返回结果_______{}_______", str);
            JSONObject jsonObject = JSONObject.parseObject(str);
            if (!"true".equals(jsonObject.getString("resultCode"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "创建淼信物流运单失败，" + jsonObject.getString("message"));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String postOrderApi(@NotBlank(message = "客户ID，必填") String customer_id, @NotBlank(message = "原单号，必填") String order_customerinvoicecode) {
            String data = HttpUtil.post(url1 + MiaoXinAPI.POSTORDERAPI.getApi(), new HashMap<String, String>(1) {{
                this.put("customer_id", customer_id);
                this.put("order_customerinvoicecode", order_customerinvoicecode);
            }});
            if (!"true".equalsIgnoreCase(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "标记淼信物流运单发货失败。。。");
            }
            return null;
        }

//        @Override
//        public String printLabel(@NotNull(message = "打印标签数据对象不能为空") @Valid MiaoXinPrintVO vo, HttpServletRequest request, HttpServletResponse response) throws Exception {
//            List<Long> orderIds = vo.getOrderIds();
//            StringBuilder order_id = new StringBuilder();
//            for (Long order : orderIds) {
//                order_id.append(order).append(",");
//            }
//            HashMap<String, String> hashMap = new HashMap<String, String>(4) {{
//                this.put("Format", vo.getFormat());
//                this.put("PrintType", vo.getPrintType());
//                this.put("order_id", order_id.toString());
//                this.put("Print", vo.getPrint());
//            }};
//            String param = URLEncoder.encode(JSON.toJSONString(hashMap), "UTF-8");
//            _log.info("_________请求的JSON格式参数为_______{}_______", param);
//            String data = HttpUtil.post(url2 + MiaoXinAPI.PRINTLABEL.getApi(), new HashMap<String, String>(1) {{
//                this.put("param", param);
//            }}, null);
////            response.sendRedirect(data);
//            _log.info("_________打印淼信物流运单标签返回结果_______{}_______", data);
//            return null;
//        }

        @Override
        public String printLabel(@NotNull(message = "打印标签数据对象不能为空") @Valid MiaoXinPrintVO vo) throws Exception {
            List<String> orderIds = vo.getOrderIds();
            StringBuilder order_id = new StringBuilder();
            for (String order : orderIds) {
                order_id.append(order).append(",");
            }
            String data = this.get(url2 + MiaoXinAPI.PRINTLABEL.getApi(), new HashMap<String, String>(1) {{
                this.put("Format", vo.getFormat());
                this.put("PrintType", vo.getPrintType() == null ? "A4" : vo.getPrintType());
                this.put("Print", vo.getPrint());
            }});
            String reqURL = data + "&order_id=" + order_id;
            if (reqURL.endsWith(",")) {
                reqURL = reqURL.substring(0, reqURL.lastIndexOf(","));
            }
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.getResponseCode();
            String realUrl = conn.getURL().toString();
            conn.disconnect();
            _log.info("_________淼信物流面单PDF的URL_______{}_______", realUrl);
            return realUrl;
        }

        private String get(String url, Map<String, String> param) throws Exception {
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            return httpGet.getURI().toString();
        }

        @Override
        public String selectLabelType() {
            String data = HttpUtil.post(url1 + MiaoXinAPI.SELECTLABELTYPE.getApi(), null);
            _log.info("_________获取淼信物流打印类型返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取淼信物流打印类型返回结果为空。。。");
            }
            return data;
        }

        @Override
        public String selectTrack(@NotBlank(message = "订单号，必填") String documentCode) {
            String data = HttpUtil.post(url1 + MiaoXinAPI.SELECTTRACK.getApi(), new HashMap<String, String>(1) {{
                this.put("documentCode", documentCode);
            }});
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询淼信物流运单物流轨迹返回结果为空。。。");
            }
            _log.info("_________查询淼信物流运单物流轨迹返回结果_______{}_______", data);
            JSONArray array = JSONArray.parseArray(data);
            for (int i = 0; i <= array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!"true".equals(obj.get("ack"))) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "创建淼信物流运单失败，" + obj.getString("message"));
                }
            }
            return data;
        }

        @Override
        public String getOrderTrackingNumber(String documentCode, Long order_id) {
            if (StringUtils.isBlank(documentCode) && order_id == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数为空。。。");
            }
            String data = null;
            try {
                data = HttpUtil.post(url1 + MiaoXinAPI.GETORDERTRACKINGNUMBER.getApi(), new HashMap<String, String>(1) {{
                    this.put("documentCode", documentCode);
                    this.put("order_id", order_id == null ? null : order_id.toString());
                }});
            } catch (Exception e) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "调用<<淼信物流>>获取跟踪号接口失败");
            }
            _log.info("_________查询淼信物流运单跟踪号返回结果_______{}_______", data);
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (StringUtils.isBlank(jsonObject.getString("order_id"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "<<淼信物流>>未查询到相应跟踪号");
            }
            if (!"200".equals(jsonObject.getString("status"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "<<淼信物流>>查询跟踪号异常:" + jsonObject.getString("msg"));
            }

            return jsonObject.getString("order_serveinvoicecode");
        }

        @Override
        public String updateOrderWeightByApi(@NotBlank(message = "客户ID不能为空") String customerId, @NotBlank(message = "订单号不能为空") String orderNo, @NotNull(message = "重量不能为空") BigDecimal weight) {
            String data = HttpUtil.post(url1 + MiaoXinAPI.UPDATEORDERWEIGHTBYAPI.getApi(), new HashMap<String, String>(1) {{
                this.put("customerId", customerId);
                this.put("orderNo", orderNo);
                this.put("weight", weight.toString());
            }});
            _log.info("_________更新淼信物流运单（订单：{}）预报重量返回结果_______{}_______", orderNo, data);
            if (!"true".equalsIgnoreCase(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更新淼信物流运单预报重量失败。。。");
            }
            return null;
        }

        @Override
        public String modifyInsurance(@NotBlank(message = "客户ID不能为空") String customerId, @NotBlank(message = "订单号，必填") String documentCode,
                                      @NotNull(message = "保险金额不能为空") @Digits(integer = 18, fraction = 3, message = "保险金额限制为18位整数3位小数") BigDecimal insuranceValue) {
            String data = HttpUtil.post(url1 + MiaoXinAPI.MODIFYINSURANCE.getApi(), new HashMap<String, String>(1) {{
                this.put("customerId", customerId);
                this.put("documentCode", documentCode);
                this.put("insuranceValue", insuranceValue.toString());
            }});
            _log.info("_________修改淼信物流运单（订单：{}）保险金额返回结果_______{}_______", documentCode, data);
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"true".equalsIgnoreCase(jsonObject.getString("status"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "修改淼信物流运单保险金额失败。。。");
            }
            return null;
        }

        @Autowired
        private Mapper dozerMapper;

        @Override
        public LogisticsDeliverCallBack deliverSingle(BaseOrder baseOrder) throws Exception {
            MiaoXinOrder order = dozerMapper.map(baseOrder, MiaoXinOrder.class);
            order.setTrade_type("ZYXT");
            order.setCustomer_id("16462");
            order.setCustomer_userid("13521");
            order.getOrderInvoiceParam().forEach(x -> x.setInvoice_amount(x.getUnitPrice().multiply(new BigDecimal(x.getInvoice_pcs())).toString()));
            String callback = this.createOrderApi(order);
            LogisticsDeliverCallBack back = new LogisticsDeliverCallBack();
            back.setCustomerOrderNumber(JSONObject.parseObject(callback).getString("reference_number"));
            back.setWayBillNumber(JSONObject.parseObject(callback).getString("order_id"));
            back.setTrackNumber(JSONObject.parseObject(callback).getString("tracking_number"));
            this.setFaceSheetUrl(back);
            return back;
        }

        private void setFaceSheetUrl(LogisticsDeliverCallBack callBack) throws Exception {
            MiaoXinPrintVO printVO = new MiaoXinPrintVO();
            printVO.setPrintType("A4");
            printVO.setOrderIds(new ArrayList<String>(1) {{
                this.add(callBack.getWayBillNumber());
            }});
            String labelURL = this.printLabel(printVO);
            callBack.setFaceSheetUrl(centralServerService.transferPDF2PIC(labelURL, callBack.getCustomerOrderNumber(), CommonEnum.IMG_TYPE.PNG.getTpye()));
        }
    }
}
