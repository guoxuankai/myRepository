package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.logistics.entity.LogisticsDeliverCallBack;
import com.brandslink.cloud.logistics.entity.centre.*;

import javax.xml.bind.ValidationException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface ICentralServerService {

    Page<MethodVO> selectLogisticsMethod(String warehouse);

    List<LogisticsFreightCallBack> freight(LogisticsFreight logisticsFreight) throws ValidationException;

    LogisticsDeliverCallBack deliverSingle(BaseOrder baseOrder) throws Exception;

    /**
     * 处理PDF格式面单转成图片格式，保存到文件服务器并返回图片路径
     * @param labelURL 面单URL
     * @param orderNumber 所属订单号
     * @param imgType 图片格式
     * @return
     */
    String transferPDF2PIC(String labelURL, String orderNumber, String imgType);
}
