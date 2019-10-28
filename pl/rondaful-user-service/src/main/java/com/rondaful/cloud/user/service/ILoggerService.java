package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.entity.SysLog;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.logger.CreditLoggerDTO;
import com.rondaful.cloud.user.model.dto.logger.PageLoggerDTO;
import com.rondaful.cloud.user.model.dto.logger.QueryLoggerDTO;
import com.rondaful.cloud.user.model.dto.logger.ShowPropertyDTO;

import java.util.List;

public interface ILoggerService {

	public static String TABLE_NAME="system_operation_logger_";
	public static String TABLE_NAME_CREDIT="system_operation_logger_credit ";

	/**
	 * 插入记录
	 * @param syslog
	 * @return
	 */
	 Integer insert(SysLog syslog);
	 

	/**
	 * 分页查询日志
	 * @param dto
	 * @return
	 */
	 PageDTO<PageLoggerDTO> getsPage(QueryLoggerDTO dto);

	/**
	 * 插入授信日志
	 * @param dto
	 * @return
	 */
	Integer insertCredits(CreditLoggerDTO dto);

	/**
	 * 根据用户查询授信信息
	 * @param userId
	 * @return
	 */
	List<CreditLoggerDTO> getsCreditById(Integer userId,String languageType);

	/**
	 * 插入展示列
	 * @param dto
	 * @return
	 */
	Integer insertHtmlProperty(ShowPropertyDTO dto);

	/**
	 * 获取展示列表
	 * @param path
	 * @param userId
	 * @param platformType
	 * @return
	 */
	ShowPropertyDTO getHtmlProperty(String path,Integer userId,Integer platformType);


	 
}
