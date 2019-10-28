package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.entity.AliexpressPhoto;
import com.rondaful.cloud.seller.entity.AliexpressPhotoBankinfo;
import com.rondaful.cloud.seller.entity.AliexpressPhotoGroup;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressUploadImageResponse;
import com.rondaful.cloud.seller.vo.AliexpressPhotoSearchVO;

import java.util.List;

public interface IAliexpressPhotoBankService {

	/**
	 * 图片分页
	 * @param vo
	 * @return
	 */
	public Page<AliexpressPhoto> findPage(AliexpressPhotoSearchVO vo);

	/**
	 * 批量保存图片
	 * @param listAliexpressPhoto
	 * @param empowerId
	 * @return
	 */
	public int insertAliexpressPhoto(List<AliexpressPhoto> listAliexpressPhoto,Long empowerId,Long plAccountId);

	/**
	 * 获取图片银行信息
	 * @param empowerId
	 * @return
	 */
	public AliexpressPhotoBankinfo getPhotoBankinfo(Long empowerId);

	/**
	 * 获取图片银行分组
	 * @param empowerId
	 * @return
	 */
	public List<AliexpressPhotoGroup> getPhotoGroupList(Long empowerId);

	public String getEmpowerById(Long empowerId);

	/**
	 * 奇门图片上传
	 * @param base64
	 * @param imgName
	 * @param groupId
	 * @param sessionKey
	 * @param empowerId
	 * @return
	 */
	public AliexpressUploadImageResponse uploadimageforsdkBase64new(String base64, String imgName, String groupId, String sessionKey, Long empowerId);

}
