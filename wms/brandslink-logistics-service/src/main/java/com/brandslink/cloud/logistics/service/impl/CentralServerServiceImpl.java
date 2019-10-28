package com.brandslink.cloud.logistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.ConstantAli;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.FileService;
import com.brandslink.cloud.common.utils.Utils;
import com.brandslink.cloud.logistics.entity.LogisticsDeliverCallBack;
import com.brandslink.cloud.logistics.entity.centre.*;
import com.brandslink.cloud.logistics.entity.common.CommonBean;
import com.brandslink.cloud.logistics.entity.remote.ProductSku;
import com.brandslink.cloud.logistics.mapper.LogisticsMethodMapper;
import com.brandslink.cloud.logistics.remote.RemoteCenterService;
import com.brandslink.cloud.logistics.service.ICentralServerService;
import com.brandslink.cloud.logistics.strategy.HandlerContext;
import com.brandslink.cloud.logistics.thirdLogistics.BaseHandler;
import com.brandslink.cloud.logistics.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.xml.bind.ValidationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CentralServerServiceImpl implements ICentralServerService {
    @Autowired
    private RemoteCenterService remoteCenterService;
    @Autowired
    private LogisticsMethodMapper methodMapper;
    @Autowired
    private ValidateUtils validateUtils;
    @Autowired
    private FileService fileService;

    private final static Logger _log = LoggerFactory.getLogger(CentralServerServiceImpl.class);

    @Override
    public Page<MethodVO> selectLogisticsMethod(String warehouse) {
        List<MethodVO> list = methodMapper.selectLogisticsMethod(warehouse);
        /*if (StringUtils.isNotBlank(warehouse)){
            list = methodMapper.selectLogisticsMethod(warehouse);
        }else {
            list = methodMapper.selectAllLogisticsMethod();
        }*/
        return new Page(list);
    }

    @Override
    public List<LogisticsFreightCallBack> freight(LogisticsFreight logisticsFreight) throws ValidationException {
        this.validateAndSetBaseData(logisticsFreight);
        _log.info("_____________调用运费试算接口入参为__________{}___________", JSON.toJSONString(logisticsFreight));
        List<LogisticsFreightCallBack> list = methodMapper.selectMethodFreightByMultiCondition(logisticsFreight);
        _log.info("_____________调用运费试算返回结果为__________{}___________", JSON.toJSONString(list));
        List<LogisticsFreightCallBack> backList = list.stream().filter(x -> x.getPrediscountFee() != null).collect(Collectors.toList());
        for (LogisticsFreightCallBack callBack : backList) {
            JSONArray supportPlatform = callBack.getSupportPlatform();
            List<CommonBean> beanList = supportPlatform.toJavaList(CommonBean.class);
            callBack.setSupportPlatform(JSONArray.parseArray(JSON.toJSONString(beanList.stream().filter(x -> "1".equals(x.getType())).collect(Collectors.toList()))));
            callBack.setMinDay(Integer.valueOf(JSONObject.parseObject(JSON.toJSONString(callBack.getPromiseDays())).getString("min")));
            callBack.setMaxDay(Integer.valueOf(JSONObject.parseObject(JSON.toJSONString(callBack.getPromiseDays())).getString("max")));
            callBack.setMinWeight(Integer.valueOf(JSONObject.parseObject(JSON.toJSONString(callBack.getWeightRange())).getString("min")));
            callBack.setMaxWeight(Integer.valueOf(JSONObject.parseObject(JSON.toJSONString(callBack.getWeightRange())).getString("max")));
        }
        return backList;
    }

    private void validateAndSetBaseData(LogisticsFreight logisticsFreight) throws ValidationException {
        Byte searchType = logisticsFreight.getSearchType();
        if (searchType.intValue() == 1) {//TODO ENUM
            Integer length = logisticsFreight.getLength();
            Integer wide = logisticsFreight.getWide();
            Integer height = logisticsFreight.getHeight();
            Integer weight = logisticsFreight.getWeight();
            if (length == null || wide == null || height == null || weight == null ||
                    length.intValue() <= 0 || wide.intValue() <= 0 || height.intValue() <= 0 || weight.intValue() <= 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "按重量计费时长宽高重量必须传大于零的值");
            }
            if (length.compareTo(wide) < 0 || wide.compareTo(height) < 0 || length.compareTo(height) < 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "必须满足：长≥宽≥高");
            }
        } else if (searchType.intValue() == 2) {
            List<SkuQuantity> list = logisticsFreight.getSkuQuantityList();
            int count = list.stream().mapToInt(SkuQuantity::getQuantity).sum();
            for (SkuQuantity skuQuantity : list) {
                validateUtils.validate(skuQuantity);
            }
            String data = remoteCenterService.getSkuInfoBySku(list.stream().map(x -> x.getSku()).collect(Collectors.toSet()).toArray(new String[0]));
            String result = Utils.returnRemoteResultDataString(data, "根据SKU查询长宽高重量远程调用中心服务异常。。。");
            if (StringUtils.isNotBlank(result)) {
                List<ProductSku> productSkuList = JSONArray.parseArray(result, ProductSku.class);
                if (count == 1) {
                    logisticsFreight.setLength(productSkuList.get(0).getPackageLength().intValue());
                    logisticsFreight.setWide(productSkuList.get(0).getPackageWidth().intValue());
                    logisticsFreight.setHeight(productSkuList.get(0).getPackageHeight().intValue());
                    logisticsFreight.setWeight(productSkuList.get(0).getPackageWeight().intValue());
                } else {
                    int weight = 0;
                    Map<String, Integer> map = list.stream().collect(Collectors.groupingBy(SkuQuantity::getSku, Collectors.summingInt(SkuQuantity::getQuantity)));
                    for (ProductSku productSku : productSkuList) {
                        if (map.containsKey(productSku.getProductSku())) {
                            weight += productSku.getPackageWeight() * map.get(productSku.getProductSku());
                        }
                    }
                    logisticsFreight.setWeight(weight);
                }
            }
        }
    }

    @Autowired
    private HandlerContext handlerContext;

    @Override
    public LogisticsDeliverCallBack deliverSingle(BaseOrder baseOrder) throws Exception {
        String logisticsCode = baseOrder.getLogisticsCode();
        BaseHandler handler = handlerContext.getInstance(logisticsCode);
        return handler.deliverSingle(baseOrder);
    }

    @Override
    public String transferPDF2PIC(String labelURL, String orderNumber, String imgType) {
        StringBuilder sb = new StringBuilder();
        try {
            RestTemplate rest = new RestTemplate();
            ResponseEntity<Resource> entity = rest.getForEntity(labelURL, Resource.class);
            InputStream inputStream = entity.getBody().getInputStream();
            PDDocument doc = PDDocument.load(inputStream);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "PNG", byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                String fileURL = fileService.specifiedSaveFile(ConstantAli.BucketType.BUCKET_FILE_DEV, ConstantAli.FolderType.WMS_OUTBOUND,
                        orderNumber + "_" + (i + 1) + ".png", bytes);
                sb.append(fileURL).append(";");
            }
        } catch (IOException e) {
            _log.error("pdf文件转换图片失败:", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "pdf文件转换图片失败");
        }
        return sb.toString().endsWith(";") ? sb.toString().substring(0,sb.toString().lastIndexOf(";")) : sb.toString();
    }
}
