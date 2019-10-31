package com.brandslink.cloud.common.service;

import com.brandslink.cloud.common.constant.ConstantAli;

import java.util.List;
import java.util.Map;

public interface FileService {

	/**
	 * 删除单个文件
	 * 
	 * @param url     文件地址
	 * @param id      文件ID[暂时可传入NULL]
	 * @param orderId 文件对于订单ID[暂时可传入NULL]
	 * @param type    平台类型[暂时可传入NULL]
	 * @return 1成功 0失败
	 */
	int deleteFile(String url, Long id, String orderId, ConstantAli.PlatformType type);

	/**
	 * 删除多个文件
	 * 
	 * @param url 文件地址集合
	 * @return 1成功 0失败
	 */
	int deleteList(List<String> url);

	/**
	 * 文件存储
	 * 
	 * @param bucketName   目录名
	 * @param fileName     实际文件名+后缀[尽量自己生成，唯一]
	 * @param folder       文件所在目录
	 * @param obj          文件[byte、File、InputStream]
	 * @param platformType 平台类型 [暂时可传入NULL]
	 * @param orderId      订单ID [暂时可传入NULL]
	 * @param userId       用户ID [暂时可传入NULL]
	 * @return url
	 */
	public String saveFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, Object obj, String platformType, String orderId, Long userId);

	/**
	 * 多文件上传
	 * 
	 * @param bucketName   目录名
	 * @param folder       文件所在目录
	 * @param fileMap      实际文件名+后缀[尽量自己生成，唯一],文件
	 * @param platformType 平台类型 [暂时可传入NULL]
	 * @param orderId      订单ID [暂时可传入NULL]
	 * @param userId       用户ID [暂时可传入NULL]
	 * @return url
	 */
	public Map<String, String> uploadMultipleFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, Map<String, Object> fileMap, String platformType, String orderId, Long userId);

	/**
	 * 多文件上传
	 * 
	 * @param bucketName 目录名
	 * @param folder     文件所在目录
	 * @param fileMap    实际文件名+后缀[尽量自己生成，唯一],文件
	 * @return url
	 */
	public Map<String, String> beforehandGetPath(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, Map<String, Object> fileMap);

	/**
	 * 指定目录文件存储，文件名相同会进行替换
	 * 
	 * @param bucketName 目录名
	 * @param fileName   实际文件名+后缀[尽量自己生成，唯一]
	 * @param folder     文件所在目录
	 * @param b          文件
	 * @return
	 */
	public String specifiedSaveFile(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName, byte[] b);

	/**
	 * 指定目录文件.预先获取上传成功地址
	 * 
	 * @param bucketName
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public String specifiedBeforehandGetPath(ConstantAli.BucketType bucketName, ConstantAli.FolderType folder, String fileName);

	/**
	 * 文件保存临时目录
	 * 
	 * @param fileName [文件名]
	 * @param obj      [byte、File、InputStream]
	 * @return 上传成功的URL地址
	 */
	public String temp(String fileName, Object obj);

}
