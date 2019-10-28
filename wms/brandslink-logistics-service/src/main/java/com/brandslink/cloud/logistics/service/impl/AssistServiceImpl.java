package com.brandslink.cloud.logistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.logistics.thirdLogistics.RemoteMiaoXinLogisticsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssistServiceImpl {
    @Autowired
    private RemoteMiaoXinLogisticsService miaoXinLogisticsService;

    private final static Logger _log = LoggerFactory.getLogger(AssistServiceImpl.class);

    public void insertBatchMethodData() {
        String back = miaoXinLogisticsService.getProductList();
        if (StringUtils.isBlank(back)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取淼信物流渠道列表返回结果为空。。。");
        }
        JSONArray array = JSONArray.parseArray(back);
        if (CollectionUtils.isNotEmpty(array)){
            for (Object obj : array) {
                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(obj));
                String express_type = jsonObject.getString("express_type");
                String product_id = jsonObject.getString("product_id");
                String product_shortname = jsonObject.getString("product_shortname");
                _log.error("_______express_type:{}________product_id:{}________product_shortname:{}", express_type, product_id, product_shortname);
            }
        }
    }
}
