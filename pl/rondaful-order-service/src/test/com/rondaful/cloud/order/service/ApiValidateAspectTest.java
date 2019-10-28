package com.rondaful.cloud.order.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.SkuGroupVo;
import com.rondaful.cloud.common.model.vo.freight.SupplierGroupVo;
import com.rondaful.cloud.common.utils.MD5;
import com.rondaful.cloud.common.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yangzefei
 * @Classname ApiValidateAspectTest
 * @Description TODO
 * @Date 2019/8/1 11:06
 */
public class ApiValidateAspectTest {
    /**
     * 生成签名
     * @param sortMap 排序后的参数字符串
     * @param secret 密钥
     * @param timestamp 时间戳
     * @return
     */
    public static String createSign(Map<String,String> sortMap,String secret,String timestamp){
        StringBuilder sb=new StringBuilder();
        for(String key:sortMap.keySet()){
            sb.append(sortMap.get(key));
        }
        try{
            sb.append(secret);
            if(StringUtils.isNotEmpty(timestamp)){
                sb.append(timestamp);
            }
            System.out.println("加密前的字符串:"+sb.toString());
            return MD5.getMd5(sb.toString());
        }catch (UnsupportedEncodingException e){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, Utils.i18n("生成签名失败"));
        }
    }

    public static void main(String[] args){
        String appId="5dabf2e419c36a19f7ca907c1d077f94";
        String appSecret="a0106eaa-d2bc-4253-95fa-7981533a65d4";
        String timestamp="";
        Map<String,String> sortMap=new TreeMap<>();
        sortMap.put("v","1.0.0");
//        String sign=testGetParams(sortMap,appSecret,timestamp);
//        String sign=testPostEntity(sortMap,appSecret,timestamp);
//        String sign=testPostList(sortMap,appSecret,timestamp);
        String sign=testPost(sortMap,appSecret,timestamp);

        System.out.println("access_token:"+sign+appId);
    }

    public static String testGetParams(Map<String,String> sortMap,String secretKey,String timestamp){
        sortMap.put("a","1");
        sortMap.put("b","12312312");
        return createSign(sortMap,secretKey,timestamp);
    }
    public static String testPostEntity(Map<String,String> sortMap,String secretKey,String timestamp){
        SkuGroupVo skuGroupVo=new SkuGroupVo(1L,"1231",12, new BigDecimal(0),1);
        String result= JSON.toJSONString(skuGroupVo);
        sortMap.put("dataJson",result);
        return createSign(sortMap,secretKey,timestamp);
    }

    public static String testPostList(Map<String,String> sortMap,String secretKey,String timestamp){
        List<SkuGroupVo> list=new ArrayList<>();
        SkuGroupVo skuGroupVo=new SkuGroupVo(1L,"1231",12, new BigDecimal(0),1);
        list.add(skuGroupVo);
        list.add(skuGroupVo);
        String result= JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
        sortMap.put("dataJson",result);
        return createSign(sortMap,secretKey,timestamp);
    }

    public static String testPost(Map<String,String> sortMap,String secretKey,String timestamp){
        List<SkuGroupVo> list=new ArrayList<>();
        SkuGroupVo skuGroupVo=new SkuGroupVo(1L,"1231",12, new BigDecimal(0),1);
        list.add(skuGroupVo);
        list.add(skuGroupVo);
        SupplierGroupVo supplier=new SupplierGroupVo();
        supplier.setSupplierCost(new BigDecimal(2).setScale(0));
        supplier.setSupplierId(10L);
        supplier.setItems(list);
        String result= JSON.toJSONString(supplier, SerializerFeature.DisableCircularReferenceDetect);
        sortMap.put("dataJson",result);
        return createSign(sortMap,secretKey,timestamp);
    }

}
