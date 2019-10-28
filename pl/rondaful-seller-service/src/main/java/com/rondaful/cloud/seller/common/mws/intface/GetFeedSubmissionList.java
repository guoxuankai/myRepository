package com.rondaful.cloud.seller.common.mws.intface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebService;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebServiceClient;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebServiceConfig;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebServiceException;
import com.rondaful.cloud.seller.common.mws.model.GetFeedSubmissionListRequest;
import com.rondaful.cloud.seller.common.mws.model.GetFeedSubmissionListResponse;
import com.rondaful.cloud.seller.common.mws.model.IdList;
import com.rondaful.cloud.seller.common.mws.model.TypeList;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;

/**
 * 获取上报xml的结果报告
 * @author ouxiangfeng
 *
 */
@Component
public class GetFeedSubmissionList extends BaseMission {

	private final Logger logger = LoggerFactory.getLogger(GetFeedSubmissionList.class);
	/**
	 * 执行调用amazon接口
	 * @param countryCode
	 * @param merchantId
	 * @return
	 */
	public GetFeedSubmissionListResponse invoke(String countryCode,final String merchantId,String FeedSubmissionId,String feedType) {
		
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplace().get(countryCode);
		config.setServiceURL(marketplaceId.getUri());
		MarketplaceWebService service = new MarketplaceWebServiceClient(this.getAccessKeyId(), this.getSecretAccessKey(), appName,
				appVersion, config);
		GetFeedSubmissionListRequest request = new GetFeedSubmissionListRequest();
        request.setMerchant( merchantId );
        request.setFeedSubmissionIdList(new IdList().withId(FeedSubmissionId));
        request.setFeedTypeList(new TypeList().withType(feedType));
        GetFeedSubmissionListResponse response;
		try {
			response = service.getFeedSubmissionList(request);
			if(logger.isDebugEnabled())
				logger.debug(JSON.toJSONString(response));
			return response;
		} catch (MarketplaceWebServiceException e) {
			logger.error("上报服务连接错误",e);
		}
        return null;
	}
}
