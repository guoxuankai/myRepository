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
import com.brandslink.cloud.logistics.thirdLogistics.bean.SunYou.SunYouCommonBean;
import com.brandslink.cloud.logistics.thirdLogistics.bean.SunYou.SunYouPackage;
import com.brandslink.cloud.logistics.thirdLogistics.bean.SunYou.SunYouProduct;
import com.brandslink.cloud.logistics.thirdLogistics.enums.SunYouAPI;
import org.apache.commons.collections.CollectionUtils;
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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RemoteSunYouLogisticsService {


    /**
     * 批量创建并预报包裹
     *
     * @param list 申请顺友物流发货的包裹集合(一次调用最多支持提交 100 个包裹。)
     * @return
     */
    String createAndConfirmPackages(@Size(max = 100, message = "一次调用最多支持提交 100 个包裹。") @NotEmpty(message = "顺友物流包裹集合不能为空") List<SunYouPackage> list) throws Exception;

    /**
     * 批量获取包裹状态
     *
     * @param syOrderNoList  顺友流水号集合
     * @param customerNoList 客户订单号集合
     * @return
     */
    String getPackagesDetails(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception;

    /**
     * 批量获取包裹详情
     *
     * @param syOrderNoList  顺友流水号集合
     * @param customerNoList 客户订单号集合
     * @return
     */
    String getPackagesStatus(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception;

    /**
     * 批量获取包裹追踪号
     *
     * @param syOrderNoList  顺友流水号集合
     * @param customerNoList 客户订单号集合
     * @return
     */
    String getPackagesTrackingNumber(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception;

    /**
     * 批量删除包裹
     *
     * @param syOrderNoList  顺友流水号集合
     * @param customerNoList 客户订单号集合
     * @return
     */
    String deletePackages(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception;

    /**
     * 批量获取包裹面单变量
     *
     * @param syOrderNoList  顺友流水号集合
     * @param customerNoList 客户订单号集合
     * @return
     */
    String getPackagesLabelVariables(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception;

    /**
     * 批量获取包裹面单变量
     *
     * @param syOrderNoList  顺友流水号集合
     * @param customerNoList 客户订单号集合
     * @param packMethod     0：返回包含多个面单的单个 PDF 文件 1：返回包含多个 PDF 文件的 ZIP 包，每个 PDF 文件中仅包含一个包裹的面单信息，仅有一个 包裹时也将被打包为 ZIP 默认值：0
     * @param dataFormat     0：标签返回数据类型为 byte 数组 1：标签返回数据类型为路径 默认值：0
     * @return
     */
    String getPackagesLabel(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList,
                            @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList,
                            Integer packMethod,
                            Integer dataFormat) throws Exception;

    /**
     * 查询邮寄方式
     *
     * @param countryCode 目的国家二字代码
     * @param pickupCity  揽收城市
     * @param postCode    收件地址邮编
     * @return
     * @throws Exception
     */
    String findShippingMethods(String countryCode, String pickupCity, String postCode) throws Exception;

    /**
     * 修改预报重量
     *
     * @param list
     * @return
     * @throws Exception
     */
    String operationPackages(List<SunYouCommonBean> list) throws Exception;

    @Validated
    @Service
    @HandlerType("SunYou")
    class RemoteSunYouLogisticsServiceImpl implements RemoteSunYouLogisticsService, BaseHandler {

        @Value("${logistics.sunyou.apiDevUserToken}")
        private String apiDevUserToken;


        @Value("${logistics.sunyou.apiLogUsertoken}")
        private String apiLogUsertoken;

        @Value("${logistics.sunyou.url}")
        private String url;

        private final static Logger _log = LoggerFactory.getLogger(RemoteSunYouLogisticsServiceImpl.class);

        @Override
        public String createAndConfirmPackages(@Size(max = 100, message = "一次调用最多支持提交 100 个包裹。") @NotEmpty(message = "顺友物流包裹集合不能为空") List<SunYouPackage> list) throws Exception {
            HashMap<String, Object> hashMap = new HashMap<String, Object>(1) {{
                this.put("apiDevUserToken", apiDevUserToken);
                this.put("apiLogUsertoken", apiLogUsertoken);
                this.put("data", new HashMap<String, List<SunYouPackage>>(1) {{
                    this.put("packageList", list);
                }});
            }};
            String json = JSON.toJSONString(hashMap);
            _log.info("_________顺友物流批量创建并预报包裹请求JSON数据为________{}_______", json);
            String data = HttpUtil.postJson(url + SunYouAPI.CREATEANDCONFIRMPACKAGES.getApi(), json, null);
            _log.info("_________顺友物流批量创建并预报包裹返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流创建并预报包裹返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"success".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流创建并预报包裹失败。。。");
            }
            String dataString = jsonObject.getString("data");
            return JSONObject.parseObject(dataString).getString("resultList");
        }

        @Override
        public String getPackagesDetails(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList,
                                         @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception {
            if (CollectionUtils.isEmpty(syOrderNoList) && CollectionUtils.isEmpty(customerNoList)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友流水号集合和客户订单号集合不能同时为空");
            }
            Map<String, List<String>> map = CollectionUtils.isNotEmpty(syOrderNoList) ? new HashMap<String, List<String>>(1) {{
                this.put("syOrderNoList", syOrderNoList);
            }} : new HashMap<String, List<String>>(1) {{
                this.put("customerNoList", customerNoList);
            }};
            HashMap<String, Object> hashMap = new HashMap<String, Object>(1) {{
                this.put("apiDevUserToken", apiDevUserToken);
                this.put("apiLogUsertoken", apiLogUsertoken);
                this.put("data", map);
            }};
            String data = HttpUtil.postJson(url + SunYouAPI.GETPACKAGESDETAILS.getApi(), JSON.toJSONString(hashMap), null);
            _log.info("_________顺友物流批量获取包裹详情返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹详情返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"success".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹详情失败。。。");
            }
            return data;
        }

        @Override
        public String getPackagesStatus(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception {
            if (CollectionUtils.isEmpty(syOrderNoList) && CollectionUtils.isEmpty(customerNoList)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友流水号集合和客户订单号集合不能同时为空");
            }
            Map<String, List<String>> map = CollectionUtils.isNotEmpty(syOrderNoList) ? new HashMap<String, List<String>>(1) {{
                this.put("syOrderNoList", syOrderNoList);
            }} : new HashMap<String, List<String>>(1) {{
                this.put("customerNoList", customerNoList);
            }};
            HashMap<String, Object> hashMap = new HashMap<String, Object>(1) {{
                this.put("apiDevUserToken", apiDevUserToken);
                this.put("apiLogUsertoken", apiLogUsertoken);
                this.put("data", map);
            }};
            String data = HttpUtil.postJson(url + SunYouAPI.GETPACKAGESSTATUS.getApi(), JSON.toJSONString(hashMap), null);
            _log.info("_________顺友物流批量获取包裹状态返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹状态返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"success".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹状态失败。。。");
            }
            return data;
        }

        @Override
        public String getPackagesTrackingNumber(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception {
            if (CollectionUtils.isEmpty(syOrderNoList) && CollectionUtils.isEmpty(customerNoList)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友流水号集合和客户订单号集合不能同时为空");
            }
            Map<String, List<String>> map = CollectionUtils.isNotEmpty(syOrderNoList) ? new HashMap<String, List<String>>(1) {{
                this.put("syOrderNoList", syOrderNoList);
            }} : new HashMap<String, List<String>>(1) {{
                this.put("customerNoList", customerNoList);
            }};
            HashMap<String, Object> hashMap = new HashMap<String, Object>(1) {{
                this.put("apiDevUserToken", apiDevUserToken);
                this.put("apiLogUsertoken", apiLogUsertoken);
                this.put("data", map);
            }};
            String data = HttpUtil.postJson(url + SunYouAPI.GETPACKAGESTRACKINGNUMBER.getApi(), JSON.toJSONString(hashMap), null);
            _log.info("_________顺友物流批量获取包裹追踪号返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹追踪号返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"success".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取获取包裹追踪号失败。。。");
            }
            return data;
        }

        @Override
        public String deletePackages(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception {
            if (CollectionUtils.isEmpty(syOrderNoList) && CollectionUtils.isEmpty(customerNoList)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友流水号集合和客户订单号集合不能同时为空");
            }
            Map<String, List<String>> map = CollectionUtils.isNotEmpty(syOrderNoList) ? new HashMap<String, List<String>>(1) {{
                this.put("syOrderNoList", syOrderNoList);
            }} : new HashMap<String, List<String>>(1) {{
                this.put("customerNoList", customerNoList);
            }};
            HashMap<String, Object> hashMap = new HashMap<String, Object>(1) {{
                this.put("apiDevUserToken", apiDevUserToken);
                this.put("apiLogUsertoken", apiLogUsertoken);
                this.put("data", map);
            }};
            String data = HttpUtil.postJson(url + SunYouAPI.DELETEPACKAGES.getApi(), JSON.toJSONString(hashMap), null);
            _log.info("_________顺友物流批量删除包裹返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量删除包裹返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"success".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量删除包裹失败。。。");
            }
            return data;
        }

        @Override
        public String getPackagesLabelVariables(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList) throws Exception {
            if (CollectionUtils.isEmpty(syOrderNoList) && CollectionUtils.isEmpty(customerNoList)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友流水号集合和客户订单号集合不能同时为空");
            }
            Map<String, List<String>> map = CollectionUtils.isNotEmpty(syOrderNoList) ? new HashMap<String, List<String>>(1) {{
                this.put("syOrderNoList", syOrderNoList);
            }} : new HashMap<String, List<String>>(1) {{
                this.put("customerNoList", customerNoList);
            }};
            HashMap<String, Object> hashMap = new HashMap<String, Object>(1) {{
                this.put("apiDevUserToken", apiDevUserToken);
                this.put("apiLogUsertoken", apiLogUsertoken);
                this.put("data", map);
            }};
            String data = HttpUtil.postJson(url + SunYouAPI.GETPACKAGESLABELVARIABLES.getApi(), JSON.toJSONString(hashMap), null);
            _log.info("_________顺友物流批量获取包裹面单变量返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹面单变量返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"success".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹面单变量失败。。。");
            }
            return data;
        }

        @Override
        public String getPackagesLabel(@Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> syOrderNoList, @Size(max = 200, message = "单次最大查询包裹数量为 200") List<String> customerNoList, Integer packMethod, Integer dataFormat) throws Exception {
            if (CollectionUtils.isEmpty(syOrderNoList) && CollectionUtils.isEmpty(customerNoList)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友流水号集合和客户订单号集合不能同时为空");
            }
            Map<String, Object> map = CollectionUtils.isNotEmpty(syOrderNoList) ? new HashMap<String, Object>(1) {{
                this.put("syOrderNoList", syOrderNoList);
                this.put("packMethod", packMethod);
                this.put("dataFormat", dataFormat);
            }} : new HashMap<String, Object>(1) {{
                this.put("customerNoList", customerNoList);
                this.put("packMethod", packMethod);
                this.put("dataFormat", dataFormat);
            }};
            HashMap<String, Object> hashMap = new HashMap<String, Object>(1) {{
                this.put("apiDevUserToken", apiDevUserToken);
                this.put("apiLogUsertoken", apiLogUsertoken);
                this.put("data", map);
            }};
            String data = HttpUtil.postJson(url + SunYouAPI.GETPACKAGESLABEL.getApi(), JSON.toJSONString(hashMap), null);
            _log.info("_________顺友物流批量获取包裹面单变量返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹面单变量返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"success".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹面单变量失败。。。");
            }
            String dataString = jsonObject.getString("data");
            return JSONObject.parseObject(dataString).getString("labelPath");
        }

        @Override
        public String findShippingMethods(String countryCode, String pickupCity, String postCode) throws Exception {
            Map<String, String> map = new HashMap<String, String>(1) {{
                this.put("countryCode", countryCode);
                this.put("pickupCity", pickupCity);
                this.put("postCode", postCode);
            }};
            HashMap<String, Object> hashMap = new HashMap<String, Object>(1) {{
                this.put("apiDevUserToken", apiDevUserToken);
                this.put("apiLogUsertoken", apiLogUsertoken);
                this.put("data", map);
            }};
            String data = HttpUtil.postJson(url + SunYouAPI.FINDSHIPPINGMETHODS.getApi(), JSON.toJSONString(hashMap), null);
            _log.info("_________顺友物流查询邮寄方式返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流查询邮寄方式返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"success".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流查询邮寄方式失败。。。");
            }
            return data;
        }

        @Override
        public String operationPackages(List<SunYouCommonBean> list) throws Exception {
            if (list.stream().anyMatch(x -> StringUtils.isBlank(x.getSyOrderNo()) && StringUtils.isBlank(x.getCustomerNo()))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友流水号和客户订单号不能同时为空");
            }
            List<Map<String, Object>> packageList = new ArrayList<>();
            for (SunYouCommonBean bean : list) {
                Map<String, Object> map0 = new HashMap<>(1);
                if (StringUtils.isNotBlank(bean.getSyOrderNo())) {
                    map0.put("syOrderNo", bean.getSyOrderNo());
                    map0.put("predictionWeight", bean.getPredictionWeight());
                } else if (StringUtils.isNotBlank(bean.getSyOrderNo())) {
                    map0.put("customerNo", bean.getCustomerNo());
                    map0.put("predictionWeight", bean.getPredictionWeight());
                }
                packageList.add(map0);
            }
            HashMap<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>() {{
                this.put("packageList", packageList);
            }};
            HashMap<String, Object> hashMap = new HashMap<String, Object>(1) {{
                this.put("apiDevUserToken", apiDevUserToken);
                this.put("apiLogUsertoken", apiLogUsertoken);
                this.put("data", map);
            }};
            String data = HttpUtil.postJson(url + SunYouAPI.OPERATIONPACKAGES.getApi(), JSON.toJSONString(hashMap), null);
            _log.info("_________顺友物流批量获取包裹面单变量返回结果_______{}_______", data);
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹面单变量返回结果为空。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (!"success".equals(jsonObject.getString("ack"))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流批量获取包裹面单变量失败。。。");
            }
            return data;
        }

        @Autowired
        private Mapper dozerMapper;
        @Autowired
        ICentralServerService centralServerService;

        @Override
        public LogisticsDeliverCallBack deliverSingle(BaseOrder baseOrder) throws Exception {
            SunYouPackage sunYouPackage = dozerMapper.map(baseOrder, SunYouPackage.class);
            sunYouPackage.setPredictionWeight(new BigDecimal(0));
            for (SunYouProduct product : sunYouPackage.getProductList()) {
                sunYouPackage.setPredictionWeight(sunYouPackage.getPredictionWeight().add(product.getUnitWeight().multiply(new BigDecimal(product.getQuantity()))));
            }
            String callback = this.createAndConfirmPackages(new ArrayList<SunYouPackage>(1) {{
                this.add(sunYouPackage);
            }});
            Object object = JSONObject.parseArray(callback).get(0);
            if ("failure".equals(JSONObject.parseObject(JSON.toJSONString(object)).getString("processStatus"))) {
                String errorListStr = JSONObject.parseObject(JSON.toJSONString(object)).getString("errorList");
                JSONObject jsonObject = (JSONObject) JSONObject.parseArray(errorListStr).get(0);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "顺友物流创建并预报包裹失败，错误信息:" + jsonObject.getString("errorMsg"));
            }
            LogisticsDeliverCallBack back = new LogisticsDeliverCallBack();
            back.setCustomerOrderNumber(JSONObject.parseObject(JSON.toJSONString(object)).getString("customerOrderNo"));
            back.setWayBillNumber(JSONObject.parseObject(JSON.toJSONString(object)).getString("syOrderNo"));
            back.setTrackNumber(JSONObject.parseObject(JSON.toJSONString(object)).getString("trackingNumber"));
            this.setFaceSheetUrl(back);
            return back;
        }

        private void setFaceSheetUrl(LogisticsDeliverCallBack callBack) throws Exception {
            String labelURL = this.getPackagesLabel(null, new ArrayList<String>(1) {{
                this.add(callBack.getCustomerOrderNumber());
            }}, 0, 1);
            callBack.setFaceSheetUrl(centralServerService.transferPDF2PIC(labelURL, callBack.getCustomerOrderNumber(), CommonEnum.IMG_TYPE.PNG.getTpye()));
        }
    }
}
