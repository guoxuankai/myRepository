package com.brandslink.cloud.common.service.impl;

import com.brandslink.cloud.common.config.AliConfig;
import com.brandslink.cloud.common.constant.ConstantAli;
import com.brandslink.cloud.common.constant.ConstantAli.FolderType;
import com.brandslink.cloud.common.service.FileService;
import com.brandslink.cloud.common.utils.AliFileUtils;
import com.brandslink.cloud.common.utils.SpringContextUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileServiceImpl implements FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

	@Resource
	private AliFileUtils aliFileUtils;

	@Resource
	private AliConfig aliConfig;

	@Resource
	private SpringContextUtil scu;

	private Pattern http;
	private Pattern https;

	@SuppressWarnings("static-access")
	@PostConstruct
	public void setAttribute() {
		String oss = aliConfig.getEndpoint().replace("http://", "").replace("https://", "");
		http = Pattern.compile("http://(.*?)." + oss);
		https = Pattern.compile("https://(.*?)." + oss);
		aliFileUtils.automaticFailure(ConstantAli.getEnv(scu.getActiveProfile()), ConstantAli.getEnvStr(scu.getActiveProfile()) + "id", FolderType.TEMP.getType(), 7);
	}

	@Override
	public int deleteList(List<String> url) {
		if (url.size() > 0) {
			try {
				url.forEach(file -> {
					aliFileUtils.deleteFile(getBucketName(file), getFolderName(file));
				});
				return 1;
			} catch (Exception e) {
				logger.error("单文件删除失败", e);
			}
		}
		return 0;
	}

	@Override
	public int deleteFile(String url, Long id, String orderId, ConstantAli.PlatformType type) {
		try {
			aliFileUtils.deleteFile(getBucketName(url), getFolderName(url));
			return 1;
		} catch (Exception e) {
			logger.error("单文件删除失败", e);
		}
		return 0;
	}

	private ConstantAli.BucketType getBucketName(String url) {
		Matcher m = StringUtils.startsWith(url, "https") ? https.matcher(url) : http.matcher(url);
		String bucket = "";
		while (m.find()) {
			bucket = m.group(1);
		}
		return ConstantAli.BucketType.valueOf(bucket.replace("-", "_").replace("rondaful", "bucket").toUpperCase());
	}

	private String getFolderName(String url) {
		Matcher m = StringUtils.startsWith(url, "https") ? https.matcher(url) : http.matcher(url);
		String f = "";
		while (m.find()) {
			f = m.group();
		}
		return url.replace(f + "/", "");
	}

	@Override
	public String saveFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, Object obj, String platformType, String orderId, Long userId) {
		String url = "";
		if (obj instanceof byte[]) {
			url = aliFileUtils.saveByteFile(bucketName, folder, fileName, (byte[]) obj);
		} else if (obj instanceof File) {
			url = aliFileUtils.saveFile(bucketName, folder, fileName, (File) obj);
		} else if (obj instanceof InputStream) {
			url = aliFileUtils.saveStreamFile(bucketName, folder, fileName, (InputStream) obj);
		}
		logger.info("文件上传成功 : {}", url);
		return url;
	}

	@Override
	public Map<String, String> uploadMultipleFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, Map<String, Object> fileMap, String platformType, String orderId, Long userId) {
		List<String> data = aliFileUtils.uploadMultipleFile(bucketName, folder, fileMap);
		Map<String, String> map = new HashMap<>();
		data.forEach(d -> {
			String[] url = d.split(AliFileUtils.URL_SPLIT);
			map.put(url[0], url[1]);
		});
		logger.info("多文件上传成功 : {}", map);
		return map;
	}

	@Override
	public Map<String, String> beforehandGetPath(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, Map<String, Object> fileMap) {
		Map<String, String> url = new HashMap<>();
		fileMap.forEach((k, v) -> {
			url.put(k, aliFileUtils.beforehandGetPath(bucketName, folder, k));
		});
		return url;
	}

	@Override
	public String specifiedSaveFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, byte[] b) {
		return aliFileUtils.specifiedSaveFile(bucketName, folder, fileName, b);
	}

	@Override
	public String specifiedBeforehandGetPath(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName) {
		return aliFileUtils.specifiedBeforehandGetPath(bucketName, folder, fileName);
	}

	@Override
	public String temp(String fileName, Object obj) {
		String url = "";
		if (obj instanceof byte[]) {
			url = aliFileUtils.saveByteFile(ConstantAli.getEnv(SpringContextUtil.getActiveProfile()), FolderType.TEMP, fileName, (byte[]) obj);
		} else if (obj instanceof File) {
			url = aliFileUtils.saveFile(ConstantAli.getEnv(SpringContextUtil.getActiveProfile()), FolderType.TEMP, fileName, (File) obj);
		} else if (obj instanceof InputStream) {
			url = aliFileUtils.saveStreamFile(ConstantAli.getEnv(SpringContextUtil.getActiveProfile()), FolderType.TEMP, fileName, (InputStream) obj);
		}
		logger.info("临时文件上传成功 : {}", url);
		return url;
	}

}
