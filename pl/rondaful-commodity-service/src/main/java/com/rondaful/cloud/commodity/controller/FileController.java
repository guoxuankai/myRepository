package com.rondaful.cloud.commodity.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rondaful.cloud.commodity.constant.CommonConstant;
import com.rondaful.cloud.commodity.utils.Utils;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.FileService;

@Api(description = "文件上传接口")
@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

	private final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Value("${rondaful.system.env}")
	public String env;

	@Resource
	private FileService fileService;
	
	
	@ApiOperation(value = "小文件上传(小于10兆)")
	@PostMapping("/updateFileList")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "type", value = "1：商品、2：文件、3：系统相关、4：app应用、5：品连学院", dataType = "Integer", paramType = "query", required=true),
        @ApiImplicitParam(name = "saveOriName", value = "是否保留原文件名，1：保留、0：不保留，默认0", dataType = "Integer", paramType = "query")})
	public Map<String, String> updateFileList(@RequestParam("files") MultipartFile[] files,Integer type,Integer saveOriName) throws IOException {
		if (type==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "Incorrect file type");
		}
		String folder=getFolder(type);
		if (StringUtils.isBlank(folder)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "Incorrect file type");
		}
		Map<String, Object> map = new HashMap<>();
		for (MultipartFile file : files) {
			if (saveOriName != null && saveOriName==1) {
				map.put(file.getOriginalFilename(), file.getBytes());
			}else {
				map.put(Utils.createFileName(file.getOriginalFilename()), file.getBytes());
			}
		}
		return getName(fileService.uploadMultipleFile(ConstantAli.getEnv(env), ConstantAli.getFolder(folder), map, null, null, null));
	}


	@ApiOperation(value = "大文件上传(大于10兆),后台先返回URL")
	@PostMapping("/bigUpdateFileList")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "type", value = "1：商品、2：文件、3：系统相关、4：app应用、5：品连学院", dataType = "Integer", paramType = "query", required=true),
        @ApiImplicitParam(name = "saveOriName", value = "是否保留原文件名，1：保留、0：不保留，默认0", dataType = "Integer", paramType = "query")})
	public Map<String, String> bigUpdateFileList(@RequestParam("files") MultipartFile[] files, Integer type,Integer saveOriName) throws IOException {
		if (type==null) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "Incorrect file type");
		}
		String folder=getFolder(type);
		if (StringUtils.isBlank(folder)) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "Incorrect file type");
		}
		Map<String, Object> map = new HashMap<>();
		for (MultipartFile file : files) {
			if (saveOriName != null && saveOriName==1) {
				map.put(file.getOriginalFilename(), file.getBytes());
			}else {
				map.put(Utils.createFileName(file.getOriginalFilename()), file.getBytes());
			}
		}
		new Thread(() -> {
			fileService.uploadMultipleFile(ConstantAli.getEnv(env), ConstantAli.getFolder(folder), map, null, null, null);
		}).start();
		return getName(fileService.beforehandGetPath(ConstantAli.getEnv(env), ConstantAli.getFolder(folder), map));
	}

	@ApiOperation(value = "APP版本上传.[ 文件名相同会进行替换 ]")
	@PostMapping("/updateFileApp")
	public String updateFileApp(@RequestParam("files") MultipartFile[] files) throws IOException {
		MultipartFile file = files[0];
		String name = "version/" + file.getOriginalFilename();
		new Thread(() -> {
			try {
				fileService.specifiedSaveFile(ConstantAli.getEnv(env), ConstantAli.getFolder("app"), name, file.getBytes());
			} catch (IOException e) {
				logger.error("app文件上传失败!{}", e);
			}
		}).start();
		String url = fileService.specifiedBeforehandGetPath(ConstantAli.getEnv(env), ConstantAli.getFolder("app"), name);
		return url.replace(CommonConstant.DEF[0], CommonConstant.DO_MAIN[0])
				.replace(CommonConstant.DEF[1], CommonConstant.DO_MAIN[1])
				.replace(CommonConstant.DEF[2], CommonConstant.DO_MAIN[2]);
	}
	
	@ApiOperation(value = "品连学院上传接口")
	@PostMapping("/updateFileCollege")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "saveOriName", value = "是否保留原文件名，1：保留、0：不保留，默认0", dataType = "Integer", paramType = "query")})
	public String updateFileCollege(@RequestParam("files") MultipartFile[] files,Integer saveOriName) throws IOException {
		MultipartFile file = files[0];
		StringBuilder name = new StringBuilder();
		if (saveOriName != null && saveOriName==1) {
			name.append("college/").append(file.getOriginalFilename());
		}else {
			name.append("college/").append(Utils.createFileName(file.getOriginalFilename()));
		}
		new Thread(() -> {
			try {
				fileService.specifiedSaveFile(ConstantAli.getEnv(env), ConstantAli.getFolder("college"), name.toString(), file.getBytes());
			} catch (IOException e) {
				logger.error("品连学院文件上传失败!{}", e);
			}
		}).start();
		String url = fileService.specifiedBeforehandGetPath(ConstantAli.getEnv(env), ConstantAli.getFolder("college"), name.toString());
		return url.replace(CommonConstant.DEF[0], CommonConstant.DO_MAIN[0])
				.replace(CommonConstant.DEF[1], CommonConstant.DO_MAIN[1])
				.replace(CommonConstant.DEF[2], CommonConstant.DO_MAIN[2]);
	}

	/**
	 * 添加域名.目前替换 是临时方法
	 * 
	 * @param map
	 * @return
	 */
	private Map<String, String> getName(Map<String, String> map) {
		Map<String, String> data = new HashMap<>();
		map.forEach((k, v) -> {
			v = v.replace(CommonConstant.DEF[0], CommonConstant.DO_MAIN[0])
					.replace(CommonConstant.DEF[1], CommonConstant.DO_MAIN[1])
					.replace(CommonConstant.DEF[2], CommonConstant.DO_MAIN[2]);
			//data.put(k.substring(16, k.length()), v);
			data.put(k, v);
		});
		return data;
	}
	
	private String getFolder(int type) {
		switch (type) {
		case 1:
			return ConstantAli.FILE_TYPE[0];
		case 2:
			return ConstantAli.FILE_TYPE[1];
		case 3:
			return ConstantAli.FILE_TYPE[2];
		case 4:
			return ConstantAli.FILE_TYPE[3];
		case 5:
			return ConstantAli.FILE_TYPE[4];
		default:
			return "";
		}
	}

}