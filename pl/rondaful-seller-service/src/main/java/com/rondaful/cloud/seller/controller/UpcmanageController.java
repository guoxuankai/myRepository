package com.rondaful.cloud.seller.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.UpcGenerate;
import com.rondaful.cloud.seller.remote.RemoteUserService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.service.UpcGenerateService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.seller.entity.Upcmanage;
import com.rondaful.cloud.seller.entity.upcResult;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.service.UpcmanageService;
import com.rondaful.cloud.seller.utils.ExcelUtil;

import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/upcmanage")
public class UpcmanageController {

	@Autowired
	private UpcmanageService  upcmanageService;
	@Autowired
	private UpcGenerateService upcGenerateService;

	@Autowired
	private GetLoginUserInformationByToken userInfo;
	@Autowired
	private RemoteUserService remoteUserService;
	@Autowired
	private AuthorizationSellerService authorizationSellerService;
	private final Logger logger = LoggerFactory.getLogger(UpcmanageController.class);


	@ApiOperation(value="查询upc列表信息",notes="查询upc列表信息")
	@PostMapping("/findAll")
	@RequestRequire(require = "page, row", parameter = String.class)
	public Page<Upcmanage> findAll(String page,String row){
		try {
			Page.builder(page, row);
			return upcmanageService.findAll(getUserId()+"");
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}

	}


	@ApiOperation(value="模糊查询upc列表信息",notes="查询upc列表信息")
	@PostMapping("/fuzzyFindAll")
	@RequestRequire(require = "page, row", parameter = String.class)
	public Page<Upcmanage> fuzzyFindAll(String numberBatch,String numberType,String number,String page,String row){
		try {
			Page.builder(page, row);
			String username = getUserId()+"";
//			String username = "MJPT@qq.com";
			Page<Upcmanage> fuzzyfindAll = upcmanageService.fuzzyFindAll(numberBatch, numberType,number,username);
			return fuzzyfindAll;
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}



	@ApiOperation(value="解析excel插入upc数据",notes="解析excel插入upc数据")
	@PostMapping("/insertAllObject")
	public Object insertAllObject(HttpServletRequest request ,
								  HttpServletResponse response,
								  @RequestParam("file") MultipartFile file,
								  String numberType){
		try {
			List<ArrayList<String>> readResult = null;//总行记录

			//判断文件是否为空
			if ((file).isEmpty()) {
				return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"上传文件为空");
			}
			//判断文件大小
			long size = file.getSize();
			String name = file.getOriginalFilename();
			if (StringUtils.isBlank(name) || size == 0) {
				return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"文件名为空");
			}
			//获取文件后缀
			String postfix = ExcelUtil.getPostfix(name);
			String userId = getUserId()+"";
			if("UPC".equals(numberType)){
			//读取文件内容
				Upcmanage upcmanage = new Upcmanage();
				upcmanage.setNumbertype(numberType);

				//获取当前时间作为批次
				String numberBatch = ExcelUtil.getNumberBatch();
				upcmanage.setNumberbatch(numberBatch);
				upcmanage.setAccount(userId);
				upcmanage.setStatus(0);
				upcmanage.setUsestatus(0);

				//解析excel文件
				readResult = ExcelUtil.readXlsx(file);
				//遍历结果
				for (ArrayList<String> arrayList : readResult) {
					for (int i = 0; i < arrayList.size(); i++) {

						Integer checkNumber = upcmanageService.checkNumber(arrayList.get(i));
						if(checkNumber > 0){
							return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入的商品编码" + arrayList.get(i)+ "已存在，请重新输入！");
						}
						
						while(arrayList.get(i).length() != 12){
							return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入的商品编码" + arrayList.get(i)+ "长度有误，请重新输入！");
						}
						upcmanage.setNumber(arrayList.get(i));
						upcmanage.setCreatedtime(new Date());
						upcmanageService.insertAllObject(upcmanage);
					}
				}
			}else if("EAN".equals(numberType)){
				//读取文件内容
					Upcmanage upcmanage = new Upcmanage();
					upcmanage.setNumbertype(numberType);

					//获取当前时间作为批次
					String numberBatch = ExcelUtil.getNumberBatch();
					upcmanage.setNumberbatch(numberBatch);
					upcmanage.setAccount(userId);
					upcmanage.setStatus(0);
					upcmanage.setUsestatus(0);

					//解析excel文件
					readResult = ExcelUtil.readXlsx(file);
					//遍历结果
					for (ArrayList<String> arrayList : readResult) {
						for (int i = 0; i < arrayList.size(); i++) {

							Integer checkNumber = upcmanageService.checkNumber(arrayList.get(i));
							if(checkNumber > 0){
								return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入的商品编码" + arrayList.get(i)+ "已存在，请重新输入！");
							}
							
							while(arrayList.get(i).length() != 13){
								return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入的商品编码" + arrayList.get(i)+ "长度有误，请重新输入！");
							}
							upcmanage.setNumber(arrayList.get(i));
							upcmanage.setCreatedtime(new Date());
							upcmanageService.insertAllObject(upcmanage);
						}
					}
			}else if("ISBN".equals(numberType)){
				//读取文件内容
					Upcmanage upcmanage = new Upcmanage();
					upcmanage.setNumbertype(numberType);

					//获取当前时间作为批次
					String numberBatch = ExcelUtil.getNumberBatch();
					upcmanage.setNumberbatch(numberBatch);
					upcmanage.setAccount(userId);
					upcmanage.setStatus(0);
					upcmanage.setUsestatus(0);

					//解析excel文件
					readResult = ExcelUtil.readXlsx(file);
					//遍历结果
					for (ArrayList<String> arrayList : readResult) {
						for (int i = 0; i < arrayList.size(); i++) {

							Integer checkNumber = upcmanageService.checkNumber(arrayList.get(i));
							if(checkNumber > 0){
								return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入的商品编码" + arrayList.get(i)+ "已存在，请重新输入！");
							}
							
							while(arrayList.get(i).length() != 13){
								return new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入的商品编码" + arrayList.get(i)+ "长度有误，请重新输入！");
							}
							upcmanage.setNumber(arrayList.get(i));
							upcmanage.setCreatedtime(new Date());
							upcmanageService.insertAllObject(upcmanage);
						}
				}
			}else{
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"新增商品编码异常");
			}	
				
			return  new GlobalException(ResponseCodeEnum.RETURN_CODE_100200,"数据写入成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"新增商品编码异常");
		}
	}



	@ApiOperation(value="通过id停用status",notes="通过id停用status")
	@GetMapping("/stopStatusById/{id}")
	public void stopStatusById(@PathVariable Integer id){
		try {
			Integer status = 1;
			upcmanageService.updateStatusById(status,id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"停用失败");
		}

	}

	@ApiOperation(value="通过id启用status",notes="通过id启用status")
	@GetMapping("/startStatusById/{id}")
	public void startStatusById(@PathVariable Integer id){
		
		
		try {
			Integer status = 0;
			upcmanageService.updateStatusById(status,id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"停用失败");
		}
	}



	@ApiOperation(value="通过类型停用",notes="通过类型停用status")
	@PostMapping("/stopStatusByNumberType")
	public void stopStatusByNumberType(String numberType){
		try {
			Integer status = 1;
			String account = this.getUserId().toString();
			upcmanageService.updateStatusByNumberType(status, numberType,account);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"通过类型停用失败");
		}
	}


	@ApiOperation(value="通过类型启用",notes="通过类型停用status")
	@PostMapping("/startStatusByNumberType")
	public void startStatusByNumberType(String numberType){
		try {
			Integer status = 0;
			String account = this.getUserId().toString();
			upcmanageService.updateStatusByNumberType(status, numberType,account);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"通过类型启用失败");
		}
	}


	@ApiOperation(value="通过批次停用",notes="通过批次停用status")
	@PostMapping("/stopStatusByNumberBatch")
	public void stopStatusByNumberBatch(String numberBatch){
		
		List<String> checkNumberBatch = upcmanageService.checkNumberBatch(this.getUserId().toString());
		
		String[] split = numberBatch.split(",");
		
		for (int i = 0; i < split.length; i++) {
			if(!checkNumberBatch.contains(split[i])){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入的批次不存在！");
			}
		}
		
		try {
			Integer status = 1;
			String account = this.getUserId().toString();
			upcmanageService.updateStatusByNumberBatch(status,numberBatch,account);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"通过批次停用失败");
		}

	}


	@ApiOperation(value="通过批次启用",notes="通过批次停用status")
	@PostMapping("/startStatusByNumberBatch")
	public void startStatusByNumberBatch(String numberBatch){
		
        List<String> checkNumberBatch = upcmanageService.checkNumberBatch(this.getUserId().toString());
		
		String[] split = numberBatch.split(",");
		
		for (int i = 0; i < split.length; i++) {
			if(!checkNumberBatch.contains(split[i])){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"输入的批次不存在！");
			}
		}
		
		try {
			Integer status = 0;
			String account = this.getUserId().toString();
//			String account = "MJPT@qq.com";
			upcmanageService.updateStatusByNumberBatch(status,numberBatch,account);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"通过批次启用失败");
		}
	}


	@ApiOperation(value="停用全部status",notes="停用全部status")
	@PostMapping("/stopAllStatus")
	public void stopAllStatus(){
		try {
			Integer status = 1;
			String account = this.getUserId().toString();
			upcmanageService.updateAllStatus(status,account);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"停用全部商品编码失败");
		}
	}



	@ApiOperation(value="启用全部status",notes="启用全部status")
	@PostMapping("/startAllStatus")
	public void startAllStatus(){
		try {
			Integer status = 0;
			String account = this.getUserId().toString();
			upcmanageService.updateAllStatus(status,account);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"启用全部商品编码失败");
		}
	}



	@ApiOperation(value="统计数据查询",notes="统计数据查询")
	@PostMapping("/selectUpcResult")
	public List<Integer> selectUpcResult(){
		try {
			String username = this.getUserId().toString();
			return upcmanageService.selectUpcResult(username);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}

	@ApiOperation(value="查询upc数据",notes="查询upc数据")
	@PostMapping("/selectObject")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "numbertype", value = "类型UPC，EAN，ISBN", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "number", value = "数量", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "usedplatform", value = "1 ebay  2amazon  3 ebay,amazon", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = true)
	})
	public List<String> selectObject(String numbertype,Integer number,Integer usedplatform,Integer empowerId){
		UserDTO user= userInfo.getUserDTO();
		Integer userId = 0 ;
		if(user.getManage()){
			userId = user.getUserId();
		}else {
			userId = user.getTopUserId();
		}


		if(StringUtils.isBlank(numbertype))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"号码批次不能为空");
		if(number == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"数量不能为空");

		//upc:1-启用,2-仅租用,0-禁用
		String result = "0";
		Integer oneselfType = null;
		Empower empower = null;
		try{
			String str = remoteUserService.getUpc(userId);
			result = Utils.returnRemoteResultDataString(str, "转换失败");
			if("2".equals(result)){
				if(empowerId==null){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"授权账号不能为空");
				}
				empower = authorizationSellerService.selectByPrimaryKey(empowerId.toString());
				if(empower==null){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"授权账号不能为空");
				}
				if(empower.getRentStatus()==null || empower.getRentStatus()==0) {
					oneselfType = 0;
				}
			}else if("0".equals(result)){
				oneselfType = 0;
			}
		}catch (GlobalException e){
			throw e;
		}catch (Exception e){
			e.printStackTrace();
		}

		Integer selectEableCounts = upcmanageService.selectEableCounts(numbertype, userId.toString(), usedplatform,oneselfType);

		if(20 >= selectEableCounts){
			//只同步upc
			try{
				//result  upc:1-启用,2-仅租用,0-禁用
				Integer userIds = userId;
				if("1".equals(result)){
					//在线程中调用耗时操作
					new Thread(){
						public void run() {
							insertUpcmanage(userIds);
						}
					}.start();
				}else if("2".equals(result) && oneselfType==null){

					if(empower!=null && empower.getRentStatus()!=null && empower.getRentStatus()==1){
						//在线程中调用耗时操作
						new Thread(){
							public void run() {
								insertUpcmanage(userIds);
							}
						}.start();
					}
				}
			}catch (GlobalException e){
				throw e;
			}catch (Exception e){
				e.printStackTrace();
			}

		}
		if(number > selectEableCounts){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"可用商品编码数量不足！");
		}

		try {

//			String username = "13048840483";
			List<String> selectObject = upcmanageService.selectObject(numbertype,number,userId.toString(),usedplatform, oneselfType);


			for (int i = 0; i < selectObject.size(); i++) {

				if(usedplatform == 1){
					if (StringUtils.isNotBlank(selectObject.get(i))){
						Integer selectUsedplatform = upcmanageService.selectUsedplatform(selectObject.get(i));
						logger.info("queryUsedplatform:{}",selectUsedplatform);
						if(selectUsedplatform == null )	{
							selectUsedplatform =1;
						}else{
							if (selectUsedplatform >1){
								selectUsedplatform =3;
							}else{
								selectUsedplatform =1;
							}
						}
						upcmanageService.updateUPCStatus(selectObject.get(i), 1, selectUsedplatform);
					}
				}else if(usedplatform == 2){
					if (StringUtils.isNotBlank(selectObject.get(i))){
						Integer selectUsedplatform = upcmanageService.selectUsedplatform(selectObject.get(i));
						logger.info("queryUsedplatform:{}",selectUsedplatform);
						if(selectUsedplatform == null )	{
							selectUsedplatform =2;
						}else{
							if (selectUsedplatform < 2){
								selectUsedplatform =3;
							}else{
								selectUsedplatform =2;
							}
						}
						upcmanageService.updateUPCStatus(selectObject.get(i), 1, selectUsedplatform);
					}
				}
			}

			return selectObject;
		} catch (Exception e) {
			logger.error("没有可使用的商品编码",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"没有可使用的商品编码");
		}
	}

	private void insertUpcmanage(Integer userId){
		List<UpcGenerate> listUpcGenerate = upcGenerateService.getUpc( "200");

		String numberBatch = ExcelUtil.getNumberBatch();
		for(UpcGenerate upcGenerate:listUpcGenerate){
			Integer checkNumber = upcmanageService.checkNumber(upcGenerate.getNumber());
			if(checkNumber > 0){
				continue;
			}
			Upcmanage upcmanage = new Upcmanage();
			upcmanage.setAccount(userId+"");
			upcmanage.setNumberbatch(numberBatch);
			upcmanage.setNumbertype("UPC");
			upcmanage.setNumber(upcGenerate.getNumber());
			upcmanage.setUsestatus(0);
			upcmanage.setStatus(0);
			upcmanage.setCreatedtime(new Date());
			upcmanage.setOneselfType(1);
			upcmanageService.insertAllObject(upcmanage);
		}
	}



	@ApiOperation(value="刊登时upc码的状态修改",notes="刊登时upc码的状态修改")
	@PostMapping("/updateUpcStatus")
	public void updateUpcStatus(String number,Integer status,Integer usedplatform){
		try {
			upcmanageService.updateUPCStatus(number,status,usedplatform);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("upc码的状态修改异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"upc码的状态修改异常");
		}
	}



	@ApiOperation(value="查询UPC已用平台",notes="查询UPC已用平台")
	@PostMapping("/selectUsedplatform")
	public Integer selectUsedplatform(String number){
		try {
			return upcmanageService.selectUsedplatform(number);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查询UPC已用平台异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询UPC已用平台异常");
		}
	}

	private Integer getUserId(){
		Integer userId = null;
		UserDTO userDTO = userInfo.getUserDTO();
		if(userDTO.getManage()){
			userId = userDTO.getUserId();
		}else{
			userId = userDTO.getTopUserId();
		}
		return userId;
	}
}
