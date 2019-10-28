package com.rondaful.cloud.supplier.remote;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.supplier.entity.FreightTrial;
import org.apache.cxf.common.util.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;


/**
 *  @author: xieyanbin
 *  @Date: 2019/8/8 2019/8/8
 *  @Description: 调用wms物流方式的接口
 */
public interface RemoteWmsService {

	
	/**
	* @Description 调用wms物流方式接口
	* @Author  xieyanbin
	* @Param  warehouse 仓库编码
	* @Return      
	* @Exception   
	* 
	*/
	String selectLogisticsMethod(String warehouse);


	String wmsFreight(FreightTrial freightTrial);



	@Service
	class RemoteWmsServiceImpl implements RemoteWmsService {

		private final Logger logger = LoggerFactory.getLogger(RemoteWmsServiceImpl.class);

		@Value("${brandslink.wms.url}")
		private String wmsUrl;

		private String logistics = "/logistics/centre/selectLogisticsMethod";

		private String freight = "/logistics/centre/freight";


		@Override
		public String selectLogisticsMethod(String warehouse) {
			String param =  "?warehouse=" + warehouse + "&page=1&row=500";
			String url = wmsUrl + logistics + param;
			String result = HttpUtil.wmsGet(url);
			return result;
		}

		@Override
		public String wmsFreight(FreightTrial freightTrial) {
			JSONObject params = new JSONObject();
			params.put("warehouse",freightTrial.getWarehouseCode()); //仓库code
			params.put("country",freightTrial.getCountryCode()); //国家简码
			params.put("searchType",2); //搜索类型
			params.put("skuQuantityList",freightTrial.getList()); //搜索类型
			params.put("method",freightTrial.getLogisticsCode() == null ?"":freightTrial.getLogisticsCode()); //物流方式code
			params.put("city",freightTrial.getCity() == null ?"":freightTrial.getCity()); //城市
			String platform = "";
            if (StringUtils.isEmpty(freightTrial.getPlatformType())){
                platform = "OTHER";
			}else if ("2".equals(freightTrial.getPlatformType().trim())){
				platform = "AMAZON";
			}else if ("3".equals(freightTrial.getPlatformType().trim())){
				platform = "WISH";
			}else if ("4".equals(freightTrial.getPlatformType().trim())){
				platform = "ALIEXPRESS";
            }else if("1".equals(freightTrial.getPlatformType().trim())){
                 platform = "EBAY";
             }
			params.put("platform",platform); //平台
			URIBuilder uri= null;
			try {
				uri = new URIBuilder(this.wmsUrl+freight);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			uri.addParameter("customerAppId",freightTrial.getAppKey());
			uri.addParameter("sign",freightTrial.getAppToken());
			String result = HttpUtil.wmsPost(uri.toString(),params);
			JSONObject json = JSONObject.parseObject(result);
			if("false".equals(json.getString("success"))) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用第三方接口：" + json.getString("msg"));
			if(StringUtils.isEmpty(json.getString("data")))  throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"wms没数据");
			return json.getString("data");
		}
	}


}
