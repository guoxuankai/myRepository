package com.rondaful.cloud.seller.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.rondaful.cloud.common.entity.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.common.aliexpress.AliexpressMethodNameEnum;
import com.rondaful.cloud.seller.common.aliexpress.HttpTaoBaoApi;
import com.rondaful.cloud.seller.common.aliexpress.JsonAnalysis;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.seller.entity.AliexpressPhoto;
import com.rondaful.cloud.seller.entity.AliexpressPhotoBankinfo;
import com.rondaful.cloud.seller.entity.AliexpressPhotoGroup;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPhotoGroupModel;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPhotoModel;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressUploadImageResponse;
import com.rondaful.cloud.seller.mapper.AliexpressPhotoBankinfoMapper;
import com.rondaful.cloud.seller.mapper.AliexpressPhotoGroupMapper;
import com.rondaful.cloud.seller.mapper.AliexpressPhotoMapper;
import com.rondaful.cloud.seller.mapper.EmpowerMapper;
import com.rondaful.cloud.seller.rabbitmq.AliexpressSender;
import com.rondaful.cloud.seller.service.IAliexpressPhotoBankService;
import com.rondaful.cloud.seller.vo.AliexpressPhotoSearchVO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *速卖通 api参考地址
 *
 *开发者中心
 *
 * @author chenhan
 *
 */

@Service
public class AliexpressPhotoBankServiceImpl implements IAliexpressPhotoBankService {

    private final Logger logger = LoggerFactory.getLogger(AliexpressPhotoBankServiceImpl.class);

	@Autowired
	private GetLoginUserInformationByToken getUserInfo;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private AliexpressPhotoBankinfoMapper aliexpressPhotoBankinfoMapper;
	@Autowired
	private AliexpressPhotoGroupMapper aliexpressPhotoGroupMapper;
	@Autowired
	private AliexpressPhotoMapper aliexpressPhotoMapper;
	@Autowired
	private EmpowerMapper empowerMapper;
	@Autowired
	private HttpTaoBaoApi httpTaoBaoApi;
	@Autowired
	private AliexpressConfig config;
	@Autowired
	private AliexpressSender sender;
	@Override
	public Page<AliexpressPhoto> findPage(AliexpressPhotoSearchVO vo) {
		PageHelper.startPage(vo.getPage(), vo.getRow());
		List<AliexpressPhoto> list = aliexpressPhotoMapper.findPage(vo);
		PageInfo<AliexpressPhoto> pageInfo = new PageInfo<>(list);
		Page<AliexpressPhoto> page = new Page<>(pageInfo);
		return  page;
	}
	@Override
	public int insertAliexpressPhoto(List<AliexpressPhoto> listAliexpressPhoto,Long empowerId,Long plAccountId){
		if(listAliexpressPhoto==null || listAliexpressPhoto.size()==0){
			return 0;
		}
		Date date = new Date();
		List<Long> photoIds = Lists.newArrayList();
		listAliexpressPhoto.forEach(photo->{
			photo.setPlAccountId(plAccountId);
			photo.setEmpowerId(empowerId);
			photo.setCreateTime(date);
			photo.setUpdateTime(date);
			photo.setStatus(0);
			if(photo.getGroupId()==null){
				photo.setGroupId(0L);
			}
			if(photo.getIid()!=null) {
				photo.setPhotoId(photo.getIid());
			}else{
				photo.setPhotoId(0L);
			}
			photoIds.add(photo.getPhotoId());
		});
		List<AliexpressPhoto> list = aliexpressPhotoMapper.getAliexpressPhotoList(empowerId,photoIds);
		List<AliexpressPhoto> updatelistAliexpressPhoto = Lists.newArrayList();
		if(list!=null && list.size()>0){
			for(AliexpressPhoto photo:list){
				for(AliexpressPhoto tempphoto:listAliexpressPhoto){
					if(tempphoto.getPhotoId().equals(photo.getPhotoId())){
						listAliexpressPhoto.remove(tempphoto);
						updatelistAliexpressPhoto.add(tempphoto);
						break;
					}
				}
			}
		}
		if(listAliexpressPhoto.size()>0){
			aliexpressPhotoMapper.insertBatch(listAliexpressPhoto);
		}
		if(updatelistAliexpressPhoto.size()>0){
			aliexpressPhotoMapper.updateBatch(updatelistAliexpressPhoto);
		}
		return 1;
	}

	@Override
	public AliexpressPhotoBankinfo getPhotoBankinfo(Long empowerId) {
		Map<String,Object> map = Maps.newHashMap();
        map.put("sessionKey",this.getEmpowerById(empowerId));
        String json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.GETPHOTOBANKINFO.getCode(),map);
		Map<String,Object> retmap = JsonAnalysis.getGatewayMsg(json);
		String success = retmap.get("success").toString();
		if("200".equals(success)){
			AliexpressPhotoBankinfo aliexpressPhotoBankinfo = JSONObject.parseObject(retmap.get("data").toString(),AliexpressPhotoBankinfo.class);
			String key = "photoBank"+empowerId;
			if(!redisUtils.exists(key)){
				//每隔2个小时更新数据
				aliexpressPhotoBankinfo.setEmpowerId(empowerId);
				redisUtils.set(key, aliexpressPhotoBankinfo, 7200L);
				this.insertPhotoBankinfo(aliexpressPhotoBankinfo);
			}
			return aliexpressPhotoBankinfo;
		}else {
			String msg = retmap.get("msg").toString();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, msg);
		}
	}
	private void insertPhotoBankinfo(AliexpressPhotoBankinfo aliexpressPhotoBankinfo){
		AliexpressPhotoBankinfo temp = aliexpressPhotoBankinfoMapper.getAliexpressPhotoBankinfoByEmpowerId(aliexpressPhotoBankinfo.getEmpowerId());
		if(temp!=null){
			aliexpressPhotoBankinfo.setId(temp.getId());
			aliexpressPhotoBankinfoMapper.updateByPrimaryKeySelective(aliexpressPhotoBankinfo);
		}else {
			aliexpressPhotoBankinfo.setCreateTime(new Date());
			aliexpressPhotoBankinfoMapper.insertSelective(aliexpressPhotoBankinfo);
		}
	}

	@Override
	public List<AliexpressPhotoGroup> getPhotoGroupList(Long empowerId) {
		String key = "photoGroup"+empowerId;
		if(redisUtils.exists(key)){
			return  (List<AliexpressPhotoGroup>)redisUtils.get(key);
		}

		Map<String,Object> map = Maps.newHashMap();
		String sessionKey = this.getEmpowerById(empowerId);
		map.put("sessionKey",sessionKey);
		String json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDPHOTOBANKGROUP.getCode(),map);
		Map<String,Object> retmap = JsonAnalysis.getGatewayMsg(json);
		String success = retmap.get("success").toString();

		if("200".equals(success)){
			List<AliexpressPhotoGroup> listAliexpressPhotoGroup = null;
			AliexpressPhotoGroupModel photoGroupModel = JSONObject.parseObject(retmap.get("data").toString(), AliexpressPhotoGroupModel.class);
			if(photoGroupModel!=null){
				listAliexpressPhotoGroup = photoGroupModel.getPhotoBankImageGroupList();
				if(listAliexpressPhotoGroup==null){
					redisUtils.set(key, null, 7200L);
					return null;
				}
				for (AliexpressPhotoGroup group:listAliexpressPhotoGroup){
					group.setParentGroupId("0");
					if(redisUtils.exists(key)) {
						List<AliexpressPhotoGroup> redisUPhotoGroup = (List<AliexpressPhotoGroup>)redisUtils.get(key);
						redisUPhotoGroup.add(group);
						redisUtils.set(key, redisUPhotoGroup, 7200L);
					}else{
						List<AliexpressPhotoGroup> redisUPhotoGroup = Lists.newArrayList();
						redisUPhotoGroup.add(group);
						redisUtils.set(key, redisUPhotoGroup, 7200L);
					}
					this.getPhotoGroup(sessionKey,group.getGroupId(),key);
				}
				listAliexpressPhotoGroup = (List<AliexpressPhotoGroup>)redisUtils.get(key);
				this.insertAliexpressPhotoGroup(listAliexpressPhotoGroup, empowerId);
			}else{
				return null;
			}


			return listAliexpressPhotoGroup;
		}else {
			String msg = retmap.get("msg").toString();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, msg);
		}
	}

	private List<AliexpressPhotoGroup> getPhotoGroup(String sessionKey,String groupId,String key){
		Map<String,Object> map = Maps.newHashMap();
		map.put("sessionKey",sessionKey);
		map.put("groupId",groupId);
		String json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDPHOTOBANKGROUP.getCode(),map);
		Map<String,Object> retmap = JsonAnalysis.getGatewayMsg(json);
		String success = retmap.get("success").toString();
		if("200".equals(success)){
			List<AliexpressPhotoGroup> listAliexpressPhotoGroup = null;
			AliexpressPhotoGroupModel photoGroupModel = JSONObject.parseObject(retmap.get("data").toString(), AliexpressPhotoGroupModel.class);
			if(photoGroupModel!=null){
				listAliexpressPhotoGroup = photoGroupModel.getPhotoBankImageGroupList();
				if(listAliexpressPhotoGroup==null){
					return null;
				}
				for (AliexpressPhotoGroup group:listAliexpressPhotoGroup){
					group.setParentGroupId(groupId);
					if(redisUtils.exists(key)) {
						List<AliexpressPhotoGroup> redisUPhotoGroup = (List<AliexpressPhotoGroup>)redisUtils.get(key);
						redisUPhotoGroup.add(group);
						redisUtils.set(key, redisUPhotoGroup, 7200L);
					}else{
						List<AliexpressPhotoGroup> redisUPhotoGroup = Lists.newArrayList();
						redisUPhotoGroup.add(group);
						redisUtils.set(key, redisUPhotoGroup, 7200L);
					}
					this.getPhotoGroup(sessionKey,group.getGroupId(),key);
				}
			}else{
				return null;
			}
			return listAliexpressPhotoGroup;
		}
		return null;
	}
	private void insertAliexpressPhotoGroup(List<AliexpressPhotoGroup> listAliexpressPhotoGroup,Long empowerId){
		aliexpressPhotoGroupMapper.deletePhotoGroupByEmpowerId(empowerId);
		Date date = new Date();
		for(AliexpressPhotoGroup group:listAliexpressPhotoGroup){
			group.setCreateTime(date);
			group.setEmpowerId(empowerId);
			aliexpressPhotoGroupMapper.insertSelective(group);
		}
	}
	@Override
	public String getEmpowerById(Long empowerId){
		Empower empower = new Empower();
		empower.setStatus(1);
		empower.setEmpowerId(empowerId.intValue());
		empower.setPlatform(3);//速卖通平台
		empower = empowerMapper.selectOneByAcount(empower);
		if(empower==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "账号状态异常");
		}
		return empower.getToken();
	}



	@Override
	public AliexpressUploadImageResponse uploadimageforsdkBase64new(String base64,String imgName, String groupId, String sessionKey, Long empowerId) {
		if(StringUtils.isBlank(sessionKey) && empowerId!=null) {
			sessionKey = this.getEmpowerById(empowerId);
		}
		String url = config.getAliexpressUrl()+"/api/aliexpress/uploadimageforsdknew";
		Map<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put("sessionKey",sessionKey);
		paramsMap.put("imgBase64", base64);
		paramsMap.put("imgName", imgName);//图片名称
		String body= HttpUtil.post(url,paramsMap);
		Map<String,Object> retmap = JsonAnalysis.getGatewayMsg(body);
		String success = retmap.get("success").toString();
		if("200".equals(success)) {
			AliexpressUploadImageResponse aliexpressUploadImageResponse = JSONObject.parseObject(retmap.get("data").toString(), AliexpressUploadImageResponse.class);
			if(aliexpressUploadImageResponse!=null){
				UserDTO userDTO = getUserInfo.getUserDTO();
				Long plAccountId=0L;
				if(userDTO.getManage()){
					plAccountId = userDTO.getUserId().longValue();
				}else{
					plAccountId = userDTO.getTopUserId().longValue();
				}
				AliexpressPhotoModel model = new AliexpressPhotoModel();
				model.setType(2);
				model.setToken(sessionKey);
				model.setEmpowerId(empowerId);
				model.setSellerId(plAccountId);
				model.setImageUrl(aliexpressUploadImageResponse.getPhotobankUrl());
				sender.sendPhoto(model);
				return aliexpressUploadImageResponse;
			}
		}else{
			String msg = retmap.get("msg")==null?"null":retmap.get("msg").toString();
			throw new GlobalException(success, msg);
		}
		return null;
	}



}
