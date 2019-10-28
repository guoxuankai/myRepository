package com.rondaful.cloud.supplier.remote;

import com.ebay.eis.dto.request.GetAddressPreferenceListRequest;
import com.ebay.eis.dto.request.GetAddressPreferenceListRequestData;
import com.ebay.eis.dto.request.GetConsignPreferenceListRequest;
import com.ebay.eis.dto.request.GetConsignPreferenceListRequestData;
import com.ebay.eis.dto.responses.*;
import com.eis.client.ApiException;
import com.eis.client.api.OAuthApi;
import com.eis.client.ebay.DefaultEbayClient;
import com.eis.client.ebay.EbayClient;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *  @author: xieyanbin
 *  @Date: 2019/8/21 2019/8/21
 *  @Description: 调用edis接口
 */
public interface RemoteEdisService {

	
	/**
	* @Description 获取token
	* @Author  xieyanbin
	* @Param  devId 开发者id  secret 密钥
	* @Return      token
	* @Exception   GlobalException
	* 
	*/
	String getToken(String devId,String secret);

	
	/**
	* @Description
	* @Author  xieyanbin
	* @Param  token ebayId
	* @Return   AddressInfoResponses 地址集合
	* @Exception   GlobalException
	* 
	*/
	List<AddressInfoResponses> getEdisAddress(String token,String ebayId);


	/**
	* @Description
	* @Author  xieyanbin
	* @Param  token ebayId
	* @Return      ConsignPreferenceInfoResponses
	* @Exception   GlobalException
	*
	*/
	List<ConsignPreferenceInfoResponses> getConsignPreference(String token,String ebayId);

	@Service
	class RemoteErpServiceImpl implements RemoteEdisService {
		
		private final Logger logger = LoggerFactory.getLogger(RemoteErpServiceImpl.class);

		@Value("${edis.url}")
		private String edisUrl;

		@Override
		public String getToken(String devId, String secret) {
			OAuthApi apiInstance = new OAuthApi();
			FetchTokenResponses responses;
			try{
				responses = apiInstance.fetchToken(edisUrl,devId,secret);
			}catch (ApiException e){
				logger.error("调用edis接口异常",e);
				throw  new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用edis接口失败");
			}
			if(!"success".equals(responses.getStatus().getMessage())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"调用edis接口失败");
			if(StringUtils.isEmpty(responses.getToken())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"edis无数据");
			return responses.getToken();
		}

		@Override
		public List<AddressInfoResponses> getEdisAddress(String token,String ebayId) {
			List<AddressInfoResponses> addressInfoList = new ArrayList<>();
			EbayClient client = new DefaultEbayClient(edisUrl,token);
			GetAddressPreferenceListRequest req = new GetAddressPreferenceListRequest();
			GetAddressPreferenceListRequestData reqData = new GetAddressPreferenceListRequestData();
			reqData.setPageNumber(1);
			reqData.setPageSize(100);
			req.setMessageId(DateUtils.dateToString(new Date(),DateUtils.FORMAT_5));
			req.setTimestamp(System.currentTimeMillis());
			req.setData(reqData);
			req.setEbayId(ebayId);
			GetAddressPreferenceListResponses rsp;
			try {
				rsp = client.execute(req);
			}catch (ApiException e){
				logger.error("调用edis接口异常",e);
				throw  new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用edis接口失败");
			}
			if(!"success".equals(rsp.getStatus().getMessage())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"调用edis接口失败");
			if(CollectionUtils.isEmpty(rsp.getData().getAddressList())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"edis无数据");
			addressInfoList = rsp.getData().getAddressList();
			return addressInfoList;
		}

		@Override
		public List<ConsignPreferenceInfoResponses> getConsignPreference(String token,String ebayId) {
			List<ConsignPreferenceInfoResponses> preferenceList = new ArrayList<>();
			EbayClient client = new DefaultEbayClient(edisUrl,token);
			GetConsignPreferenceListRequest req = new GetConsignPreferenceListRequest();
			GetConsignPreferenceListRequestData reqData = new GetConsignPreferenceListRequestData();
			reqData.setPageNumber(1);
			reqData.setPageSize(500);
			req.setEbayId(ebayId);
			req.setData(reqData);
			req.setMessageId(DateUtils.dateToString(new Date(),DateUtils.FORMAT_5));
			req.setTimestamp(System.currentTimeMillis());
			GetConsignPreferenceListResponses rsp;
			try {
				 rsp = client.execute(req);
			}catch (ApiException e){
				logger.error("调用edis接口异常",e);
				throw  new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"调用edis接口失败");
			}
			if(!"success".equals(rsp.getStatus().getMessage())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"调用edis接口失败");
			if(CollectionUtils.isEmpty(rsp.getData().getConsignPreferenceList())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"edis无数据");
			preferenceList = rsp.getData().getConsignPreferenceList();
			return preferenceList;
		}
	}

}
