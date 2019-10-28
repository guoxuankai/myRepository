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
import com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu.YunTuOrder;
import com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu.YunTuParcel;
import com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu.YunTuUser;
import com.brandslink.cloud.logistics.thirdLogistics.enums.YunTuAPI;
import com.brandslink.cloud.logistics.thirdLogistics.enums.YunTuAPIResultCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.IOUtils;
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

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RemoteYunTuLogisticsService {

    /**
     * 查询国家简码
     *
     * @return
     */
    String getCountry() throws Exception;

    /**
     * 查询运输方式
     *
     * @param countryCode 国家简码
     * @return
     */
    String getShippingMethods(@Size(max = 5, message = "国家简码字符个数最大为5") String countryCode) throws Exception;

    /**
     * 查询货品类型
     *
     * @return
     */
    String getGoodsType() throws Exception;

    /**
     * 查询价格
     *
     * @param countryCode 国家简码
     * @param weight      包裹重量，单位kg,支持3位小数
     * @param length      包裹长度,单位cm,不带小数,不填写默认1
     * @param width       包裹宽度,单位cm,不带小数，不填写默认1
     * @param height      包裹高度,单位cm,不带小数，不填写默认1
     * @param packageType 包裹类型，1-包裹，2-文件，3-防水袋，默认1
     * @return
     */
    String getPriceTrial(@NotNull(message = "国家简码不能为空") @Size(max = 5, message = "国家简码字符个数最大为5") String countryCode,
                         @NotNull(message = "包裹重量不能为空") @Digits(integer = 18, fraction = 3, message = "包裹重量限制为18位整数3位小数") BigDecimal weight,
                         @NotNull(message = "包裹长度不能为空") Integer length,
                         @NotNull(message = "包裹宽度不能为空") Integer width,
                         @NotNull(message = "包裹高度不能为空") Integer height,
                         @NotNull(message = "包裹类型不能为空") Integer packageType) throws Exception;

    /**
     * 查询跟踪号
     *
     * @param customerOrderNumbers 客户订单号,多个以逗号分开
     * @return
     */
    String getTrackingNumber(@NotEmpty(message = "客户订单号不能为空") List<String> customerOrderNumbers) throws Exception;

    /**
     * 查询发件人信息
     *
     * @param orderNumber 查询号码，可输入运单号、订单号、跟踪号
     * @return
     */
    String getSender(@NotBlank(message = "查询号码不能为空") String orderNumber) throws Exception;

    /**
     * 创建运单
     *
     * @param orders 运单数据集合
     * @return
     */
    String createOrder(@NotEmpty(message = "批量创建运单数据对象不能为空") @Size(max = 10, min = 1, message = "批量创建运单数据集合元素个数最小为1最大为10") @Valid List<YunTuOrder> orders) throws Exception;

    /**
     * 查询运单
     *
     * @param orderNumber 物流系统运单号，客户订单或跟踪号
     * @return
     */
    String getOrder(@NotBlank(message = "物流系统运单号，客户订单或跟踪号不能为空") @Size(max = 50, message = "物流系统运单号，客户订单或跟踪号字符个数限制为50") String orderNumber) throws Exception;

    /**
     * 修改订单预报重量
     *
     * @param orderNumber 订单号
     * @param weight      修改重量
     * @return
     */
    String updateWeight(@NotBlank(message = "订单号不能为空") @Size(max = 50, message = "订单号字符个数最大为50") String orderNumber,
                        @NotNull(message = "修改重量不能为空") @Digits(integer = 18, fraction = 3, message = "修改重量限制为18位整数3位小数") BigDecimal weight) throws Exception;

    /**
     * 订单删除
     *
     * @param orderNumber 单号
     * @param orderType   单号类型：1-云途单号,2-客户订单号,3-跟踪号
     * @return
     */
    String delete(@NotBlank(message = "单号不能为空") String orderNumber, @NotNull(message = "单号类型不能为空") Integer orderType) throws Exception;

    /**
     * 订单拦截
     *
     * @param orderNumber 单号
     * @param orderType   单号类型：1-云途单号,2-客户订单号,3-跟踪号
     * @param remark      拦截原因
     * @return
     */
    String intercept(@NotBlank(message = "单号不能为空") String orderNumber, @NotNull(message = "单号类型不能为空") Integer orderType,
                     @NotBlank(message = "拦截原因不能为空") String remark) throws Exception;

    /**
     * 标签打印
     *
     * @param orderNumbers 物流系统运单号，客户订单或跟踪号
     * @return
     */
    String print(@NotEmpty(message = "物流系统运单号，客户订单或跟踪号不能为空") @Size(max = 50, message = "请求打印运单标签的运单个数最大为50") List<String> orderNumbers) throws Exception;

    /**
     * 查询物流运费明细
     *
     * @param wayBillNumber 运单号
     * @return
     */
    String getShippingFeeDetail(@NotBlank(message = "运单号不能为空") @Size(max = 50, message = "运单号字符个数最大为50") String wayBillNumber) throws Exception;

    /**
     * 用户注册
     *
     * @param user 注册用户的数据
     * @return
     */
    String register(@NotNull(message = "注册用户的数据不能为空") @Valid YunTuUser user) throws Exception;

    /**
     * 查询物流轨迹信息
     *
     * @param orderNumber 物流系统运单号，客户订单或跟踪号
     * @return
     */
    String getTrackInfo(@NotBlank(message = "物流系统运单号，客户订单或跟踪号不能为空") @Size(max = 50, message = "单号字符个数最大为50") String orderNumber) throws Exception;


    @Validated
    @Service
    @HandlerType("YunTu")
    class RemoteYunTuLogisticsServiceImpl implements RemoteYunTuLogisticsService, BaseHandler {

        @Value("${logistics.yuntu.serverAddress}")
        private String serverAddress;

        @Value("${logistics.yuntu.authorization}")
        private String authorization;

        private final static Logger _log = LoggerFactory.getLogger(RemoteYunTuLogisticsServiceImpl.class);

        @Override
        public String getCountry() throws Exception {
            String data = HttpUtil.get(serverAddress + YunTuAPI.GETCOUNTRY.getApi(), null, new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________查询云途物流__国家简码__返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流国家简码返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流国家简码提交失败。。。");
            }
            String result = jsonObject.getString("Items");
            return result;
        }

        @Override
        public String getShippingMethods(@Size(max = 5, message = "国家简码字符个数最大为5") String countryCode) throws Exception {
            String data = HttpUtil.get(serverAddress + YunTuAPI.GETSHIPPINGMETHODS.getApi(), new HashMap<String, String>(1) {{
                this.put("CountryCode", countryCode);
            }}, new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________查询云途物流__运输方式__返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流运输方式返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流运输方式提交失败。。。");
            }
            String result = jsonObject.getString("Items");
            return result;
        }

        @Override
        public String getGoodsType() throws Exception {
            String data = HttpUtil.get(serverAddress + YunTuAPI.GETGOODSTYPE.getApi(), null, new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________查询云途物流__货品类型__返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流货品类型返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流货品类型提交失败。。。");
            }
            String result = jsonObject.getString("Items");
            return result;
        }

        @Override
        public String getPriceTrial(@NotNull(message = "国家简码不能为空") @Size(max = 5, message = "国家简码字符个数最大为5") String countryCode,
                                    @NotNull(message = "包裹重量不能为空") @Digits(integer = 18, fraction = 3, message = "包裹重量限制为18位整数3位小数") BigDecimal weight,
                                    @NotNull(message = "包裹长度不能为空") Integer length,
                                    @NotNull(message = "包裹宽度不能为空") Integer width,
                                    @NotNull(message = "包裹高度不能为空") Integer height,
                                    @NotNull(message = "包裹类型不能为空") Integer packageType) throws Exception {
            String data = HttpUtil.get(serverAddress + YunTuAPI.GETPRICETRIAL.getApi(), new HashMap<String, String>(6) {{
                this.put("CountryCode", countryCode);
                this.put("Weight", String.valueOf(weight));
                this.put("Length", String.valueOf(length));
                this.put("Width", String.valueOf(width));
                this.put("Height", String.valueOf(height));
                this.put("PackageType", String.valueOf(packageType));
            }}, new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________查询云途物流__价格__返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流价格返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流价格提交失败。。。");
            }
            String result = jsonObject.getString("Items");
            return result;
        }

        @Override
        public String getTrackingNumber(@NotEmpty(message = "客户订单号不能为空") List<String> customerOrderNumbers) throws Exception {
            if (customerOrderNumbers.size() > 40) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "查询云途物流跟踪号请求订单个数超过限制。。。");
            }
            StringBuilder customerOrderNumber = new StringBuilder();
            for (String order : customerOrderNumbers) {
                customerOrderNumber.append(order).append(",");
            }
            String data = null;
            try {
                data = HttpUtil.get(serverAddress + YunTuAPI.GETTRACKINGNUMBER.getApi(), new HashMap<String, String>(1) {{
                    this.put("CustomerOrderNumber", customerOrderNumber.toString());
                }}, new HashMap<String, String>(1) {{
                    this.put("Authorization", authorization);
                }});
            } catch (Exception e) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "调用<<云途物流>>获取跟踪号接口失败");
            }
            _log.info("_________查询云途物流__跟踪号__返回结果_______{}_______", data);
            JSONObject jsonObject = JSONObject.parseObject(data);
            String items = jsonObject.getString("Items");
            return items;
        }

        @Override
        public String getSender(@NotBlank(message = "查询号码不能为空") String orderNumber) throws Exception {
            String data = HttpUtil.get(serverAddress + YunTuAPI.GETSENDER.getApi(), new HashMap<String, String>(1) {{
                this.put("OrderNumber", orderNumber);
            }}, new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________查询云途物流__发件人信息__返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流发件人信息返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流发件人信息提交失败。。。");
            }
            String result = jsonObject.getString("Items");
            return result;
        }

        @Override
        public String createOrder(@NotEmpty(message = "批量创建运单数据对象不能为空") @Size(max = 10, min = 1, message = "批量创建运单数据集合元素个数最小为1最大为10") @Valid List<YunTuOrder> orders) throws Exception {
            String string = JSON.toJSONString(orders);
            _log.info("_________请求批量创建云途物流运单请求参数_______{}_______", string);
            String data = HttpUtil.postJson(serverAddress + YunTuAPI.CREATEORDER.getApi(), string, new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________批量创建云途物流运单返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "批量创建云途物流运单返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            String result = jsonObject.getString("Item");
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                List<JSONObject> jsonObjects = JSONObject.parseArray(result, JSONObject.class);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "批量创建云途物流运单失败:" + jsonObjects.get(0).getString("Remark"));
            }
            return result;
        }

        @Override
        public String getOrder(@NotBlank(message = "物流系统运单号，客户订单或跟踪号不能为空") @Size(max = 50, message = "物流系统运单号，客户订单或跟踪号字符个数限制为50") String orderNumber) throws Exception {
            String data = HttpUtil.get(serverAddress + YunTuAPI.GETORDER.getApi(), new HashMap<String, String>(1) {{
                this.put("OrderNumber", orderNumber);
            }}, new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________查询云途物流运单返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流运单返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流运单失败。。。");
            }
            String result = jsonObject.getString("Item");
            return result;
        }

        @Override
        public String updateWeight(@NotBlank(message = "订单号不能为空") @Size(max = 50, message = "订单号字符个数最大为50") String orderNumber,
                                   @NotNull(message = "修改重量不能为空") @Digits(integer = 18, fraction = 3, message = "修改重量限制为18位整数3位小数") BigDecimal weight) throws Exception {
            Map<String, String> map = new HashMap<String, String>(2) {{
                this.put("OrderNumber", orderNumber);
                this.put("Weight", weight.toString());
            }};
            String data = HttpUtil.postJson(serverAddress + YunTuAPI.UPDATEWEIGHT.getApi(), JSON.toJSONString(map), new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________修改云途物流运单(订单号：{})预报重量返回结果_______{}_______", orderNumber, data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "修改云途物流运单预报重量返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "修改云途物流运单预报重量失败。。。");
            }
            String result = jsonObject.getString("Item");
            JSONObject object = JSONObject.parseObject(result);
            if ("failure".equalsIgnoreCase(object.getString("Status"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "云途物流:" + object.getString("Remark"));
            }
            return null;
        }

        @Override
        public String delete(@NotBlank(message = "单号不能为空") String orderNumber, @NotNull(message = "单号类型不能为空") Integer orderType) throws Exception {
            Map<String, String> map = new HashMap<String, String>(2) {{
                this.put("OrderNumber", orderNumber);
                this.put("OrderType", orderType.toString());
            }};
            String data = HttpUtil.postJson(serverAddress + YunTuAPI.DELETE.getApi(), JSON.toJSONString(map), new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________删除云途物流运单(订单号：{})返回结果_______{}_______", orderNumber, data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除云途物流运单返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "删除云途物流运单失败。。。");
            }
            String result = jsonObject.getString("Item");
            JSONObject object = JSONObject.parseObject(result);
            if (!YunTuAPIResultCode.DELETE_SUCCESS.getCode().equalsIgnoreCase(object.getString("Status"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "云途物流:" + object.getString("Remark"));
            }
            return null;
        }

        @Override
        public String intercept(@NotBlank(message = "单号不能为空") String orderNumber, @NotNull(message = "单号类型不能为空") Integer orderType,
                                @NotBlank(message = "拦截原因不能为空") String remark) throws Exception {
            Map<String, String> map = new HashMap<String, String>(2) {{
                this.put("OrderNumber", orderNumber);
                this.put("OrderType", orderType.toString());
                this.put("Remark", remark);
            }};
            String data = HttpUtil.postJson(serverAddress + YunTuAPI.INTERCEPT.getApi(), JSON.toJSONString(map), new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________拦截云途物流运单(订单号：{})返回结果_______{}_______", orderNumber, data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "拦截云途物流运单返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "拦截云途物流运单失败。。。");
            }
            String result = jsonObject.getString("Item");
            JSONObject object = JSONObject.parseObject(result);
            if (!YunTuAPIResultCode.INTERCEPT_SUCCESS.getCode().equalsIgnoreCase(object.getString("Result"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "云途物流:" + object.getString("Remark"));
            }
            return null;
        }

        @Override
        public String print(@NotEmpty(message = "物流系统运单号，客户订单或跟踪号不能为空") @Size(max = 50, message = "请求打印运单标签的运单个数最大为50") List<String> orderNumbers) throws Exception {
            String data = HttpUtil.postJson(serverAddress + YunTuAPI.PRINT.getApi(), JSON.toJSONString(orderNumbers), new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________批量打印云途物流运单标签返回结果_______{}_______", data);
            JSONObject jsonObject = JSONObject.parseObject(data);
            String item = jsonObject.getString("Item");
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "批量打印云途物流运单标签返回结果为空。。。");
            }
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "批量打印云途物流运单标签操作失败。。。");
            }
            return JSONObject.parseObject(JSON.toJSONString(JSONObject.parseArray(item).get(0))).getString("Url");
        }

        @Override
        public String getShippingFeeDetail(@NotBlank(message = "运单号不能为空") @Size(max = 50, message = "运单号字符个数最大为50") String wayBillNumber) throws Exception {
            String data = HttpUtil.get(serverAddress + YunTuAPI.GETSHIPPINGFEEDETAIL.getApi(), new HashMap<String, String>(1) {{
                this.put("WayBillNumber", wayBillNumber);
            }}, new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________查询云途物流运单(运单号：{})运费明细返回结果_______{}_______", wayBillNumber, data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流运单运费明细返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.NO_DATA_FOUND.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "云途物流:" + jsonObject.getString("Message"));
            }
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流运单运费明细失败。。。");
            }
            String result = jsonObject.getString("Item");
            return result;
        }

        @Override
        public String register(@NotNull(message = "注册用户的数据不能为空") @Valid YunTuUser user) throws Exception {
            String data = HttpUtil.postJson(serverAddress + YunTuAPI.REGISTER.getApi(), JSON.toJSONString(user), new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________注册云途物流用户(用户名：{})返回结果_______{}_______", user.getUserName(), data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "注册云途物流用户返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "注册云途物流用户失败。。。");
            }
            String result = jsonObject.getString("Item");
            return result;
        }

        @Override
        public String getTrackInfo(@NotBlank(message = "物流系统运单号，客户订单或跟踪号不能为空") @Size(max = 50, message = "单号字符个数最大为50") String orderNumber) throws Exception {
            String data = HttpUtil.get(serverAddress + YunTuAPI.GETTRACKINFO.getApi(), new HashMap<String, String>(1) {{
                this.put("OrderNumber", orderNumber);
            }}, new HashMap<String, String>(1) {{
                this.put("Authorization", authorization);
            }});
            _log.info("_________查询云途物流运单（订单号：{}）轨迹信息返回结果_______{}_______", orderNumber, data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流运单轨迹信息返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!YunTuAPIResultCode.COMMIT_SUCCESS.getCode().equals(jsonObject.getString("Code"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询云途物流运单轨迹信息提交失败。。。");
            }
            String result = jsonObject.getString("Item");
            return result;
        }

        @Autowired
        private Mapper dozerMapper;
        @Autowired
        ICentralServerService centralServerService;

        @Override
        public LogisticsDeliverCallBack deliverSingle(BaseOrder baseOrder) throws Exception {
            YunTuOrder order = dozerMapper.map(baseOrder, YunTuOrder.class);
            order.setWeight(new BigDecimal(0));
            order.setPackageCount(1);
            for (YunTuParcel parcel : order.getParcels()) {
                order.setWeight(order.getWeight().add(parcel.getUnitWeight().multiply(new BigDecimal(parcel.getQuantity()))));
            }
            String callback = this.createOrder(new ArrayList<YunTuOrder>(1) {{
                this.add(order);
            }});
            JSONArray jsonArray = JSONObject.parseArray(callback);
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(jsonArray.get(0)));
            if (!"1".equals(jsonObject.getString("Success"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "云途物流创建运单失败，错误信息:" + jsonObject.getString("Remark"));
            }
            LogisticsDeliverCallBack back = new LogisticsDeliverCallBack();
            back.setCustomerOrderNumber(jsonObject.getString("CustomerOrderNumber"));
            back.setWayBillNumber(jsonObject.getString("WayBillNumber"));
            back.setTrackNumber(jsonObject.getString("TrackingNumber"));
            this.setFaceSheetUrl(back);
            return back;
        }

        private void setFaceSheetUrl(LogisticsDeliverCallBack callBack) throws Exception {
            String labelURL = this.print(new ArrayList<String>(1) {{
                this.add(callBack.getCustomerOrderNumber());
            }});
            callBack.setFaceSheetUrl(centralServerService.transferPDF2PIC(labelURL, callBack.getCustomerOrderNumber(), CommonEnum.IMG_TYPE.PNG.getTpye()));
        }
    }
}
