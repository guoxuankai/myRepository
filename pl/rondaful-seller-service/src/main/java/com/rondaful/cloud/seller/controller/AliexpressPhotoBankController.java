package com.rondaful.cloud.seller.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.seller.common.aliexpress.AliexpressMethodNameEnum;
import com.rondaful.cloud.seller.common.aliexpress.HttpTaoBaoApi;
import com.rondaful.cloud.seller.common.aliexpress.ImgSet;
import com.rondaful.cloud.seller.common.aliexpress.JsonAnalysis;
import com.rondaful.cloud.seller.entity.AliexpressPhoto;
import com.rondaful.cloud.seller.entity.AliexpressPhotoBankinfo;
import com.rondaful.cloud.seller.entity.AliexpressPhotoGroup;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPhotoModel;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPhotoUrlModel;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressUploadImageResponse;
import com.rondaful.cloud.seller.rabbitmq.AliexpressSender;
import com.rondaful.cloud.seller.service.IAliexpressPhotoBankService;
import com.rondaful.cloud.seller.service.IAliexpressPublishListingService;
import com.rondaful.cloud.seller.vo.AliexpressPhotoSearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片银行Controller
 *
 * @author chenhan
 *
 */
@Api(description = "Aliexpress图片银行")
@RestController
@RequestMapping("/aliexpress/photobank")
public class AliexpressPhotoBankController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(AliexpressPhotoBankController.class);
	@Autowired
	private IAliexpressPhotoBankService aliexpressPhotoBankService;

	@Autowired
	private GetLoginUserInformationByToken getUserInfo;
	@Autowired
	private AliexpressSender sender;
	@Autowired
	private HttpTaoBaoApi httpTaoBaoApi;
	@Resource
	private UserUtils userUtils;



	@AspectContrLog(descrption = "速卖通图片查询",actionType = SysLogActionType.QUERY)
	@ApiOperation(value = "速卖通图片列表数据查询", notes = "")
	@PostMapping("/findPhotoPage")
	public Page<AliexpressPhoto> findAll(AliexpressPhotoSearchVO vo) {
		try {
			UserDTO userDTO = getUserInfo.getUserDTO();
			//设置默认分页页数
			if(vo.getPage()==null){
				vo.setPage(1);
			}
			if(vo.getRow()==null){
				vo.setRow(10);
			}
			if(userDTO.getManage()){
				vo.setPlAccountId(userDTO.getUserId().longValue());
			}else{
				vo.setPlAccountId(userDTO.getTopUserId().longValue());
			}
			Page<AliexpressPhoto> findAll = aliexpressPhotoBankService.findPage(vo);
			return findAll;
		} catch (Exception e) {
			logger.error("速卖通图片查询异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"速卖通图片查询异常");
		}
	}
	@AspectContrLog(descrption="同步银行图片",actionType= SysLogActionType.QUERY)
	@PostMapping("/syncAliexpressPhoto")
	@ApiOperation(value="同步银行图片",notes="")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = true)
	})
	public void syncAliexpressPhoto(Long empowerId){
		if (empowerId == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "刊登账号为空");
		}
		UserDTO userDTO = getUserInfo.getUserDTO();
		Long plAccountId;
		if(userDTO.getManage()){
			plAccountId = userDTO.getUserId().longValue();
		}else{
			plAccountId = userDTO.getTopUserId().longValue();
		}
		String key ="aliexpress-syncPhoto"+empowerId;
		if (redisUtils.exists(key)){
			return ;
		}else {
			redisUtils.set(key,1,3600L);//1小时
		}

		Map<String,Object> map = Maps.newHashMap();
		String token = aliexpressPhotoBankService.getEmpowerById(empowerId);
		map.put("sessionKey",token);
		map.put("pageSize",50L);
        map.put("currentPage",1L);
		map.put("locationType","allGroup");
		String json = httpTaoBaoApi.getTaoBaoApi(AliexpressMethodNameEnum.FINDIMAGEPAGE.getCode(),map);
		Map<String,Object> retmap = JsonAnalysis.getGatewayMsg(json);
		String success = retmap.get("success").toString();
		if("200".equals(success)){
			AliexpressPhotoUrlModel model = JSONObject.parseObject(retmap.get("data").toString(),AliexpressPhotoUrlModel.class);
			if(model!=null) {
				aliexpressPhotoBankService.insertAliexpressPhoto(model.getImages(), empowerId,plAccountId);
				AliexpressPhotoModel photoModelmodel = new AliexpressPhotoModel();
				photoModelmodel.setPageSize(50L);
				photoModelmodel.setEmpowerId(empowerId);
				photoModelmodel.setLocationType("allGroup");
				photoModelmodel.setSellerId(plAccountId);
				photoModelmodel.setToken(token);

				if(model.getTotalPage()>1){
					for (long i=2;i<=model.getTotalPage();i++){
						photoModelmodel.setCurrentPage(i);
						sender.sendPhoto(photoModelmodel);
					}
				}
			}
		}else {
			String msg = retmap.get("msg").toString();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, msg);
		}
	}


	@AspectContrLog(descrption="获取图片银行信息",actionType= SysLogActionType.QUERY)
	@PostMapping("/getPhotoBankinfo")
	@ApiOperation(value="获取图片银行信息",notes="")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = true)
	})
	public AliexpressPhotoBankinfo getPhotoBankinfo(Long empowerId)
	{
		try
		{
			if (empowerId == null) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "刊登账号为空");
			}
			AliexpressPhotoBankinfo aliexpressPhotoBankinfo = aliexpressPhotoBankService.getPhotoBankinfo(empowerId);
			return aliexpressPhotoBankinfo;
		}catch (GlobalException e) {
			throw e;
		}catch(Exception e)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
	}


	@AspectContrLog(descrption="获取图片银行分组",actionType= SysLogActionType.QUERY)
	@PostMapping("/getPhotoGroupList")
	@ApiOperation(value="获取图片银行分组",notes="")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = true)
	})
	public List<AliexpressPhotoGroup> getPhotoGroupList(Long empowerId)
	{
		try
		{
			if (empowerId == null) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "刊登账号为空");
			}
			List<AliexpressPhotoGroup> list = aliexpressPhotoBankService.getPhotoGroupList(empowerId);
			if(list!=null) {
				return this.rebuildList2Tree(list);
			}else{
				return null;
			}
		}catch (GlobalException e) {
			throw e;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
	}
	/**
	 * 使用递归方法建树
	 *
	 * @param listAliexpressPhotoGroup
	 * @return
	 */
	private static List<AliexpressPhotoGroup> rebuildList2Tree(List<AliexpressPhotoGroup> listAliexpressPhotoGroup) {
		boolean existRootNode = false;
		List<AliexpressPhotoGroup> newTree = Lists.newArrayList();//初始化一个新的列表
		for (AliexpressPhotoGroup photoGroup : listAliexpressPhotoGroup) {
			if (isRootNode(photoGroup, listAliexpressPhotoGroup)) {//选择根节点数据开始找儿子
				newTree.add(findChildren(photoGroup, listAliexpressPhotoGroup));
				existRootNode = true;
			}
		}
		if(!existRootNode){//也可能大家都是根节点
			return listAliexpressPhotoGroup;
		}
		return newTree;
	}

	/**
	 * 判断节点是否是根节点
	 * @param checkNode
	 * @param AliexpressPhotoGroups
	 * @return
	 */
	private static boolean isRootNode(AliexpressPhotoGroup checkNode, List<AliexpressPhotoGroup> AliexpressPhotoGroups) {
		for (AliexpressPhotoGroup group : AliexpressPhotoGroups) {
			if (checkNode.getParentGroupId().equals(group.getGroupId())) {//判断checkNode是不是有爸爸
				return  false;
			}
		}
		return true;
	}


	/**
	 * 递归查找子节点
	 *
	 * @param AliexpressPhotoGroups
	 * @return
	 */
	private static AliexpressPhotoGroup findChildren(AliexpressPhotoGroup parentNode, List<AliexpressPhotoGroup> AliexpressPhotoGroups) {
		List<AliexpressPhotoGroup> children = parentNode.getChildren();
		for (AliexpressPhotoGroup it : AliexpressPhotoGroups) {
			if (parentNode.getGroupId().equals(it.getParentGroupId())) {//找儿子，判断parentNode是不是有儿子
				children.add(findChildren(it, AliexpressPhotoGroups));
			}
		}
		return parentNode;
	}
	
	
	@AspectContrLog(descrption="速卖通上传到图片银行",actionType= SysLogActionType.UDPATE)
	@ApiOperation(value = "速卖通上传到图片银行 小文件上传(小于3兆)")
	@PostMapping("/updatefile")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "groupId", value = "图片分组id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "File", name = "file", value = "文件", required = true)
	})
	public AliexpressUploadImageResponse updateFile(@RequestParam("file") MultipartFile file, Long empowerId, String groupId) throws IOException {
		if(file==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "上传图片不能为空");
		}
		if (empowerId == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "刊登账号为空");
		}

		String base64 = Base64Utils.encodeToString(file.getBytes());
		String imgName = file.getOriginalFilename();
		if(imgName==null){
			imgName = file.getName();
		}
		return aliexpressPhotoBankService.uploadimageforsdkBase64new(base64,imgName, groupId,null, empowerId);
	}
	@AspectContrLog(descrption="速卖通上传到图片银行",actionType= SysLogActionType.UDPATE)
	@ApiOperation(value = "速卖通上传到图片银行 小文件上传(小于3兆)")
	@PostMapping("/updateImgUrl")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "groupId", value = "图片分组id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "imgUrl", value = "文件url", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "imageName", value = "图片名称没有默认最后的名称(名称加图片类型)", required = true)
	})
	public AliexpressUploadImageResponse updateImgUrl(String imgUrl,String imageName,Long empowerId,String groupId) throws IOException {
		if(imgUrl==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "上传图片不能为空");
		}
		if (empowerId == null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "刊登账号为空");
		}
		if(imageName==null){
			imageName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
			//是否有?在后面
			int num = imageName.indexOf("?");
			if(num>0){
				imageName = imageName.substring(0,num);
			}
		}
		String base64 = ImgSet.encodeImageToBase64(imgUrl);
		return aliexpressPhotoBankService.uploadimageforsdkBase64new(base64,imageName, groupId,null, empowerId);
	}

}


