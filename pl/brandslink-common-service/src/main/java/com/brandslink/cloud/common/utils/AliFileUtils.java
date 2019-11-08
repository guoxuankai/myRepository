package com.brandslink.cloud.common.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.LifecycleRule.RuleStatus;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.brandslink.cloud.common.config.AliConfig;
import com.brandslink.cloud.common.constant.ConstantAli;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 阿里云文件操作
 */
@Component
public class AliFileUtils {

	private static final Logger logger = LoggerFactory.getLogger(AliFileUtils.class);

	public final static String URL_SPLIT = "RONDAFUL-OSS-FILE";

	private OSSClient client;

	@Resource
	private AliConfig aliConfig;

	@PostConstruct
	public void setAttribute() {
		client = (OSSClient) new OSSClientBuilder().build(aliConfig.getEndpoint(), aliConfig.getAccessKeyId(), aliConfig.getAccessKeySecret());
	}

	/**
	 * 获取目录下所有文件
	 * 
	 * @param bucketName
	 * @return
	 */
	public List<OSSObjectSummary> getBucketFile(ConstantAli.BucketType bucketName) {
		List<OSSObjectSummary> objectSummary = null;
		try {
			ObjectListing objectListing = client.listObjects(bucketName.getType());
			objectSummary = objectListing.getObjectSummaries();
		} catch (Exception e) {
			logger.error("获取目录下所有文件异常 : ", e);
		}
		return objectSummary;
	}

	/**
	 * 字节文件存储
	 * 
	 * @param bucketName 目录名
	 * @param fileName   实际文件名+后缀[尽量自己生成，唯一]
	 * @param folder     文件所在目录
	 * @param b          文件
	 * @return url 文件地址
	 */
	public String saveByteFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, byte[] b) {
		String url = null, name = null;
		try {
			name = createFolder(folder.getType(), fileName, true);
			InputStream is = new ByteArrayInputStream(b);
			client.putObject(bucketName.getType(), name, is);
			url = getUrl(client.getEndpoint(), bucketName.getType(), name);
		} catch (Exception e) {
			logger.error("字节文件存储异常 : ", e);
		}
		return url;
	}

	/**
	 * 预先获取上传成功地址
	 * 
	 * @return
	 */
	public String beforehandGetPath(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName) {
		String name = createFolder(folder.getType(), fileName, true);
		return getUrl(client.getEndpoint(), bucketName.getType(), name);
	}

	/**
	 * 指定目录文件.预先获取上传成功地址
	 * 
	 * @return
	 */
	public String specifiedBeforehandGetPath(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName) {
		return getUrl(client.getEndpoint(), bucketName.getType(), folder.getType() + fileName);
	}

	/**
	 * 文件存储
	 * 
	 * @param bucketName 目录名
	 * @param fileName   实际文件名+后缀[尽量自己生成，唯一]
	 * @param folder     文件所在目录
	 * @param file       文件
	 * @return url 文件地址
	 */
	public String saveFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, File file) {
		String url = null, name = null;
		try {
			name = createFolder(folder.getType(), fileName, true);
			client.putObject(bucketName.getType(), name, file);
			url = getUrl(client.getEndpoint(), bucketName.getType(), name);
		} catch (Exception e) {
			logger.error("文件存储异常 : ", e);
		}
		return url;
	}

	/**
	 * 指定目录文件存储，文件名相同会进行替换
	 * 
	 * @param bucketName 目录名
	 * @param fileName   实际文件名+后缀[尽量自己生成，唯一]
	 * @param folder     文件所在目录
	 * @param input      文件
	 * @return url 文件地址
	 */
	public String specifiedSaveFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, byte[] b) {
		String url = null, name = null;
		try {
			name = createFolder(folder.getType(), fileName, false);
			InputStream is = new ByteArrayInputStream(b);
			client.putObject(bucketName.getType(), name, is);
			url = getUrl(client.getEndpoint(), bucketName.getType(), name);
		} catch (Exception e) {
			logger.error("字节文件存储异常 : ", e);
		}
		return url;
	}

	/**
	 * 流文件存储
	 * 
	 * @param bucketName 目录名
	 * @param fileName   实际文件名+后缀[尽量自己生成，唯一]
	 * @param folder     文件所在目录
	 * @param input      流文件
	 * @return url 文件地址
	 */
	public String saveStreamFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, InputStream input) {
		String url = null, name = null;
		try {
			name = createFolder(folder.getType(), fileName, true);
			client.putObject(bucketName.getType(), name, input);
			url = getUrl(client.getEndpoint(), bucketName.getType(), name);
		} catch (Exception e) {
			logger.error("流文件存储异常 : ", e);
		}
		return url;
	}

	/**
	 * 多文件上传
	 * 
	 * @param bucketName 目录名
	 * @param folder     文件所在目录
	 * @param fileMap    实际文件名+后缀[尽量自己生成，唯一],文件
	 * @return
	 */
	public List<String> uploadMultipleFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, Map<String, Object> fileMap) {
		List<Future<String>> task = new ArrayList<Future<String>>();
		ExecutorService executor = Executors.newCachedThreadPool();
		fileMap.forEach((k, v) -> {
			Future<String> result = executor.submit(new TaskUploadFile(bucketName, folder, k, v));
			task.add(result);
		});
		executor.shutdown();
		return task.stream().map(t -> {
			try {
				return t.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());
	}

	/**
	 * 删除文件
	 * 
	 * @param bucketName 目录名
	 * @param fileName   文件目录+文件名
	 * @return true成功 false失败
	 */
	public boolean deleteFile(ConstantAli.BucketType bucketName, String fileName) {
		boolean state = false;
		try {
			client.deleteObject(bucketName.getType(), fileName);
			state = true;
		} catch (Exception e) {
			logger.error("文件删除异常 : ", e);
		}
		return state;
	}

	/**
	 * 删除文件
	 * 
	 * @param bucketName 目录名
	 * @param folder     文件所在目录
	 * @param createDate 文件创建时间格式[yyyy-MM-dd]
	 * @param fileName   文件名
	 * @return true成功 false失败
	 */
	public boolean deleteFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, String createDate) {
		boolean state = false;
		try {
			client.deleteObject(bucketName.getType(), new StringBuilder().append(folder.getType()).append(createDate).append("/").append(fileName).toString());
			state = true;
		} catch (Exception e) {
			logger.error("文件删除异常 : ", e);
		}
		return state;
	}

	/**
	 * 批量删除文件
	 * 
	 * @param bucketName 目录名
	 * @param folder     文件所在目录
	 * @param createDate 文件创建时间格式[yyyy-MM-dd]
	 * @param fileName   文件名
	 * @return true成功 false失败
	 */
	public boolean deleteListFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String createDate, List<String> fileName) {
		boolean state = false;
		try {
			if (fileName.size() > 0) {
				List<String> keys = fileName.stream().map(f -> {
					return new StringBuilder().append(folder.getType()).append(createDate).append("/").append(f).toString();
				}).collect(Collectors.toList());
				client.deleteObjects(new DeleteObjectsRequest(bucketName.getType()).withKeys(keys));
			}
			state = true;
		} catch (Exception e) {
			logger.error("批量删除文件异常 : ", e);
		}
		return state;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param bucketName 目录名
	 * @param fileName   文件名
	 * @param createDate 文件创建时间格式[yyyy-MM-dd]
	 * @return true存在,false不存在
	 */
	public boolean existFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String createDate, String fileName) {
		boolean exist = false;
		try {
			exist = client.doesObjectExist(bucketName.getType(), new StringBuilder().append(folder.getType()).append(createDate).append("/").append(fileName).toString());
		} catch (Exception e) {
			logger.error("判断文件是否存在异常 : ", e);
		}
		return exist;
	}

	/**
	 * 创建文件目录
	 * 
	 * @param bucketName 目录
	 * @param folder     文件子目录[目录结尾一定要带/]
	 * @return true成功、false失败
	 */
	public boolean createFolder(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder) {
		boolean state = false;
		try {
			if (!client.doesObjectExist(bucketName.getType(), folder.getType())) {
				client.putObject(bucketName.getType(), folder.getType(), new ByteArrayInputStream(new byte[0]));
				client.getObject(bucketName.getType(), folder.getType());
				state = true;
			} else {
				state = false;
			}
		} catch (Exception e) {
			logger.error("创建文件目录异常 : ", e);
		}
		return state;
	}

	private String getUrl(URI uri, String bucketName, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(uri.toString());
		sb.insert(8, bucketName + ".");
		sb.append("/");
		sb.append(fileName);
		return sb.toString();
	}

	/**
	 * 文件名生成
	 * 
	 * @param suffix 文件后缀
	 * @return
	 */
	public String createFileName(String suffix) {
		StringBuilder sb = new StringBuilder();
		sb.append(UUID.randomUUID().toString().replace("-", ""));
		if (StringUtils.isNotEmpty(suffix)) {
			sb.append(".");
			sb.append(suffix.replace(".", "")).toString();
		}
		return sb.toString();
	}

	private String createFolder(String folder, String fileName, boolean isDate) {
		StringBuilder sb = new StringBuilder();
		sb.append(folder);
		if (isDate) {
			sb.append(DateUtils.dateToString(new Date(), DateUtils.FORMAT_3));
			sb.append("/");
		}
		sb.append(fileName);
		return sb.toString();
	}

	/**
	 * 设置可过期文件
	 * 
	 * @param bucketName
	 * @param id
	 * @param prefix
	 * @param day
	 */
	public void automaticFailure(ConstantAli.BucketType bucketName, String id, String prefix, int day) {
		SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName.getType());
		request.AddLifecycleRule(new LifecycleRule(id, prefix, RuleStatus.Enabled, day));
		client.setBucketLifecycle(request);
	}

	public class TaskUploadFile implements Callable<String> {

		private String url = null;

		public TaskUploadFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, Object obj) {
			StringBuilder sb = new StringBuilder();
			sb.append(fileName);
			sb.append(URL_SPLIT);
			if (obj instanceof byte[]) {
				sb.append(saveByteFile(bucketName, folder, fileName, (byte[]) obj));
			} else if (obj instanceof File) {
				sb.append(saveFile(bucketName, folder, fileName, (File) obj));
			} else if (obj instanceof InputStream) {
				sb.append(saveStreamFile(bucketName, folder, fileName, (InputStream) obj));
			}
			url = sb.toString();
		}

		@Override
		public String call() throws Exception {
			return url;
		}
	}

}
