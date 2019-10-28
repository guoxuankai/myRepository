package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.user.entity.CreditLogger;
import com.rondaful.cloud.user.entity.ShowProperty;
import com.rondaful.cloud.user.entity.SysLog;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.logger.CreditLoggerDTO;
import com.rondaful.cloud.user.model.dto.logger.PageLoggerDTO;
import com.rondaful.cloud.user.model.dto.logger.QueryLoggerDTO;
import com.rondaful.cloud.user.model.dto.logger.ShowPropertyDTO;
import com.rondaful.cloud.user.service.ILoggerService;
import jdk.nashorn.internal.objects.annotations.Where;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.commons.util.IdUtils;
import org.springframework.core.env.PropertyResolver;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Service("sysLogService")
public class ILoggerServiceImpl implements ILoggerService {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private PropertyResolver propertyResolver;

	/**
	 * 添加用户的操作日志内容
	 * @param syslog
	 * @return
	 */
	@Override
	public Integer insert(SysLog syslog) {
		syslog.setCreateDate(new Date());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM");
		String tableName=TABLE_NAME+ formatter.format(syslog.getCreateDate());
		this.mongoTemplate.save(syslog,tableName);
		return 1;
	}

	/**
	 * 分页查询日志
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public PageDTO<PageLoggerDTO> getsPage(QueryLoggerDTO dto) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM");
		String tableName=TABLE_NAME+ formatter.format(dto.getStartTime());
		Query query=new Query();
		query.addCriteria(where("loginName").is(dto.getLoginName()));
		query.addCriteria(where("platformType").is(dto.getPlatformType()));
		query.addCriteria(where("createDate").gte(dto.getStartTime()).lte(dto.getEndTime()));
		query.addCriteria(where("optionActiontype").nin("QUERY"));
		query.with(new Sort(Sort.Direction.DESC,"createDate"));
		Long total = this.mongoTemplate.count(query, SysLog.class, tableName);
		PageDTO<PageLoggerDTO> result=new PageDTO<>(total,dto.getCurrentPage().longValue());
		if (total==0||total<(dto.getCurrentPage() - 1) * dto.getPageSize()){
			return result;
		}
		query.skip((dto.getCurrentPage() - 1) * dto.getPageSize());
		query.limit(dto.getPageSize());

		List<SysLog> data = this.mongoTemplate.find(query, SysLog.class,tableName);
		List<PageLoggerDTO> dataList=new ArrayList<>(data.size());
		data.forEach(log->{
			PageLoggerDTO dto1=new PageLoggerDTO();
			BeanUtils.copyProperties(log,dto1);
			if (StringUtils.isNotEmpty(dto.getLanguageType())){
				dto1.setOptionDescrption(Utils.translation(log.getOptionDescrption()));
			}
			dataList.add(dto1);
		});
		result.setList(dataList);
		return result;
	}

	/**
	 * 插入授信日志
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public Integer insertCredits(CreditLoggerDTO dto) {
		CreditLogger logger=new CreditLogger();
		BeanUtils.copyProperties(dto,logger);
		logger.setCreateDate(new Date());
		logger.setId(System.currentTimeMillis());
		this.mongoTemplate.insert(logger);
		return 1;
	}

	/**
	 * 根据用户查询授信信息
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List<CreditLoggerDTO> getsCreditById(Integer userId,String languageType) {
		Query query=new Query();
		query.addCriteria(where("userId").is(userId));
		query.with(new Sort(Sort.Direction.DESC,"createDate"));
		List<CreditLogger> list=this.mongoTemplate.find(query,CreditLogger.class);
		List<CreditLoggerDTO> result=new ArrayList<>();
		if (CollectionUtils.isEmpty(list)){
			return result;
		}
		for (CreditLogger loggerDO:list) {
			CreditLoggerDTO dto=new CreditLoggerDTO();
			BeanUtils.copyProperties(loggerDO,dto);
			dto.setOperate(StringUtils.isEmpty(languageType)?loggerDO.getOperate():Utils.translation(loggerDO.getOperate()));
			result.add(dto);
		}
		return result;
	}

	/**
	 * 插入展示列
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public Integer insertHtmlProperty(ShowPropertyDTO dto) {
		if (this.getHtmlProperty(dto.getPath(),dto.getUserId(),dto.getPlatformType())==null){
			ShowProperty property=new ShowProperty();
			BeanUtils.copyProperties(dto,property);
			this.mongoTemplate.insert(property,"html_show_property");
		}else {
			Query query=query(where("platform_type").is(dto.getPlatformType()).and("user_id").is(dto.getUserId()).and("path").is(dto.getPath()));
			Update update=new Update();
			update.set("hide", dto.getHide());
			update.set("show", dto.getShow());
			this.mongoTemplate.upsert(query, update,ShowProperty.class,"html_show_property");
		}
		return 1;
	}

	/**
	 * 获取展示列表
	 *
	 * @param path
	 * @param userId
	 * @param platformType
	 * @return
	 */
	@Override
	public ShowPropertyDTO getHtmlProperty(String path, Integer userId, Integer platformType) {
		Query query=query(where("platform_type").is(platformType).and("user_id").is(userId).and("path").is(path));
		List<ShowProperty> list= this.mongoTemplate.find(query,ShowProperty.class,"html_show_property");
		if (CollectionUtils.isEmpty(list)){
			return null;
		}
		ShowPropertyDTO result=new ShowPropertyDTO();
		BeanUtils.copyProperties(list.get(0),result);
		return result;
	}

}
