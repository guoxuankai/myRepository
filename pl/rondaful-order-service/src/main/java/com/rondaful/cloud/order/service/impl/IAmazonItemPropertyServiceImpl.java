package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.order.entity.Amazon.AmazonItemProperty;
import com.rondaful.cloud.order.mapper.AmazonItemPropertyMapper;
import com.rondaful.cloud.order.service.IAmazonItemPropertyService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * 作者: Administrator
 * 时间: 2018-12-10 17:49
 * 包名: com.rondaful.cloud.order.service.impl
 * 描述:
 */

@Service
public class IAmazonItemPropertyServiceImpl extends BaseServiceImpl<AmazonItemProperty> implements IAmazonItemPropertyService {
    @Autowired
    private IAmazonItemPropertyServiceImpl amazonItemPropertyService;
    @Autowired
    private AmazonItemPropertyMapper amazonItemPropertyMapper;

    int amazonItemPropertycount=0;
//    @Override
//    public List<AmazonItemProperty> GetOrderItemsPropertyByASINAndInsertDb(List<AmazonOrderDetail> list,String sellerId,String marketplaceId,String
//            mwsAuthToken) throws
//            InterruptedException,
//            DocumentException {
//        List<AmazonItemProperty> amazonItemPropertyList = new ArrayList<>();
//        if (list.size() == 0) {
//            return null;
//        }
//        for (AmazonOrderDetail amazonOrderDetail : list) {
//            String asin = amazonOrderDetail.getAsin();
//            if (amazonItemPropertycount >= 20) {
//                Thread.sleep(1000);
//            }
//            GetMatchingProductResponse getMatchingProductResponse = GetMatchingProductSample.get(asin,sellerId,marketplaceId,mwsAuthToken);
//            List<GetMatchingProductResult> getMatchingProductResult = getMatchingProductResponse.getGetMatchingProductResult();
//            for (GetMatchingProductResult productResult : getMatchingProductResult) {
//                Product product = productResult.getProduct();
//                Map<String, Object> itemAttributesMap = amazonItemPropertyService.parse(product.getAttributeSets().toXML(),"ItemAttributes");
//                Map<String, Object> itemAttributes = (Map<String, Object>) itemAttributesMap.get("ItemAttributes");
//                String title = (String) itemAttributes.get("Title");//商品名
//                Map<String, Object> map = (Map<String, Object>) itemAttributes.get("SmallImage");
//                String url = (String) map.get("URL");//商品URL
//                AmazonItemProperty amazonItemProperty = new AmazonItemProperty();
//                amazonItemProperty.setAsin(asin);
//                amazonItemProperty.setItemUrl(url);
//                amazonItemProperty.setItemTitle(title);
////                amazonItemPropertyMapper.insertSelective(amazonItemProperty);
//                amazonItemPropertyList.add(amazonItemProperty);
//            }
//            amazonItemPropertycount++;
//        }
//        return amazonItemPropertyList;
//
//    }



    public  Map<String, Object> parse(String soap, String type) throws DocumentException,NullPointerException{
        Map<String, Object> map = new HashMap<String, Object>();
        Document doc = DocumentHelper.parseText(soap);// 报文转成doc对象
        Element root = doc.getRootElement();// 获取根元素，准备递归解析这个XML树
        map.put(type, getCode(root.element(type)));
        return map;
    }

    public  Map<String, Object> getCode(Element root) throws NullPointerException{
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
}
