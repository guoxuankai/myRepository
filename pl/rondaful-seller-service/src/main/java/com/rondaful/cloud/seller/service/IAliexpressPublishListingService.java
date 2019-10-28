package com.rondaful.cloud.seller.service;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingDTO;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingExcelDTO;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishListingMobile;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishModel;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishRequest;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishUpdateRequest;
import com.rondaful.cloud.seller.vo.AliexpressPublishListingSearchVO;
import com.rondaful.cloud.seller.vo.ResultPublishListingVO;

import java.util.List;
import java.util.Map;

public interface IAliexpressPublishListingService {

	/**
	 * 列表信息
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	Page<AliexpressPublishListingDTO> findPage(AliexpressPublishListingSearchVO vo) throws Exception;

	List<AliexpressPublishListingExcelDTO> findAllExcel(AliexpressPublishListingSearchVO vo);

	/**
	 * 手机端查询数据
	 * @param sellerId
	 * @return
	 */
	Page<AliexpressPublishListingMobile> getAllMobile(String sellerId,List<Integer> empowerIds);

    AliexpressPublishListingMobile getAliexpressPublishListingMobileById(Long id,String headeri18n);

	/**
	 * 新增速卖通刊登信息
	 * @param publishRequest
	 * @param loginUserName
	 * @param status
	 * @param empower
	 * @return
	 */
	public AliexpressPublishListing insertPublishListing(AliexpressPublishRequest publishRequest, String loginUserName, Integer status, Empower empower );

	/**
	 * 修改速卖通刊登信息
	 * @param publishRequest
	 * @param loginUserName
	 * @param status
	 * @param empower
	 * @return
	 */
	public AliexpressPublishListing updatePublishListing(AliexpressPublishRequest publishRequest, String loginUserName, Integer status, Empower empower );

	/**
	 * 查看速卖通刊登详情
	 * @param id
	 * @param type 1普通详情 2 编辑详情
	 * @return
	 */
	public AliexpressPublishModel getPublishModelById(Long id,int type);

	/**
	 * 查看速卖通刊登表
	 * @param id
	 * @return
	 */
	public AliexpressPublishListing getAliexpressPublishListingById(Long id);

	/**
	 * 删除速卖通刊登
	 * @param id
	 * @return
	 */
	public String deleteAliexpressPublishListing(Long id);

	/**
	 * 复制速卖通刊登
	 * @param id
	 * @return
	 */
	public Long insertcopyAliexpressPublish(Long id,int type,Long empowerId,Integer publishStatus);

	/**
	 * 日志
	 * @param listingId
	 * @param operationUser
	 * @param operationType
	 * @param operationContent
	 */
	public void insertAliexpressOperationLog(Long listingId,String operationUser,String operationType,String operationContent,Long userId);
	/**
	 * 修改速卖通刊登
	 * @param aliexpressPublishListing
	 * @return
	 */
	public Integer updateByPrimaryKeySelective(AliexpressPublishListing aliexpressPublishListing);

	/**
	 * 修改刊登商品信息
	 * @param aliexpressPublishListingProduct
	 * @return
	 */
	public Integer updateByAliexpressPublishListingProduct(AliexpressPublishListingProduct aliexpressPublishListingProduct);

	/**
	 * 刊登操作日志
	 * @param listingId
	 * @return
	 */
	public List<AliexpressOperationLog> getAliexpressOperationLogBylistingId(Long listingId);
	/**
	 * 刊登日志
	 * @param listingId
	 * @return
	 */
	public List<AliexpressPublishListingError> getAliexpressPublishListingErrorBylistingId(Long listingId);


	/**
	 *
	 * @param type 类型  1刊登结果  2 刊登成功之后的审核结果
	 * @param id 商品id
	 * @param publishMessage 刊登的信息返回
	 */
	public void updateAliexpressPublishListingSuccee(int type,Long id,String publishMessage);

	/**
	 * 修改刊登商品价格和库存
	 * @param request
	 * @return
	 */
	public int updateAliexpressPublishListingProduct(AliexpressPublishUpdateRequest request);

	/**
	 * 调用中转项目的接口
	 * @param type 1 上架 2 下架  3 编辑商品单个SKU价格 4编辑商品单个SKU库存
	 * @param paramsMap
	 * @param empowerId
	 * @return
	 */
	public String postAliexpressApi(int type, Map<String, String> paramsMap, Long empowerId);

	/**
	 * 速卖通图片上传
	 * @param imgUrl
	 * @param groupId
	 * @param sessionKey
	 * @param empowerId
	 * @return
	 */
	public String uploadimageforsdk(String imgUrl,String groupId, String sessionKey, Long empowerId);

	/**
	* 速卖通图片上传
	 * @param base64
	 * @param groupId
	 * @param sessionKey
	 * @param empowerId
	 * @return
			 */
	public String uploadimageforsdkBase64(String base64,String imgName,String groupId, String sessionKey, Long empowerId);

	/**
	 * 查询订单状态为4审核中的数据 定时查询是否审核成功
	 * @return
	 */
	public List<AliexpressPublishModel> getAliexpressPublishModelList(Integer publishStatus);

	public void insertAliexpressPublishListingError(Long listingId,String errorCode,String errorMessage,String severityCode);

	/**
	 * 移动端查询速卖通刊登成功过的条数
	 * @param plAccount
	 * @return
	 */
	public Integer getUserNameCount(String plAccount);

	/**
	 * 速卖通上线下线操作
	 * @param id 刊登表id
	 * @param type 类型1上线2下线
	 */
	public void udpateAliexpressPublishListing(Long id,int type);

	/**
	 * 平台sku是否重复
	 * @param platformSku
	 * @param publishListingId
	 * @return
	 */
	public List<AliexpressPublishListingProduct> getProductByPlatformSku(String platformSku,Long publishListingId);

	/**
	 * Aliexpress速卖通刊登数量统计
	 * @return
	 */
	List<Map<String,Object>> getAliexpressSkuNumber();

	/**
	 * 平台sku获取库存code
	 * @param platformSkus
	 * @return
	 */
	List<ResultPublishListingVO> getAliexpressResultPublishListingVO(List<String> platformSkus,Integer empowerId);

	/**
	 * 修改详情
	 * @param detail
	 * @return
	 */
	public Integer updateByPublishListingDetail(AliexpressPublishListingDetail detail);

	/**
	 * listing 对象校验
	 * @param publishRequest 接收到的数据对像
	 * @param isEdit 是否是编辑
	 */
	public void check(AliexpressPublishRequest publishRequest, boolean isEdit,AliexpressPublishListing publishListing);

}
