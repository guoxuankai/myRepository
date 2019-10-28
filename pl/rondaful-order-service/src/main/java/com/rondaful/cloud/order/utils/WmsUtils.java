package com.rondaful.cloud.order.utils;

import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.utils
 * @ClassName: WmsUtils
 * @Author: Superhero
 * @Description: 调用WMS类
 * @Date: 2019/8/12 15:17
 */
@Component
public class WmsUtils {
    @Value("${brandslink.wms.url}")
    private String wmsUrl;

    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;

    /**
     * 调用wms取消订单接口
     *
     * @param warehouseId
     * @param packageId
     * @return
     */
    public String wmsCannelOrder(String warehouseId, String packageId) {
        try {
            if (StringUtils.isBlank(warehouseId) && StringUtils.isBlank(packageId)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
            }
            WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(warehouseId);
            if (warehouseDTO != null) {
                String appKey = warehouseDTO.getAppKey();
                String appToken = warehouseDTO.getAppToken();
                if (StringUtils.isNotBlank(appKey) && StringUtils.isNotBlank(appToken)) {
                    URIBuilder uri = new URIBuilder(wmsUrl + Constants.WmsSystem.CANCEL_ORDER_URL).addParameter("customerAppId", appKey).addParameter("sign", appToken);
                    return HttpUtil.wmsPost(uri.toString(), new HashMap<String, String>() {{ put("packageNum", packageId); }});
                }
            }
        } catch (URISyntaxException e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return null;
    }
}
