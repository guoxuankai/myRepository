package com.rondaful.cloud.seller.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.entity.PublishStyle;
import com.rondaful.cloud.seller.entity.PublishStyleType;
import com.rondaful.cloud.seller.mapper.PublishStyleMapper;
import com.rondaful.cloud.seller.mapper.PublishStyleTypeMapper;
import com.rondaful.cloud.seller.service.PublishStyleService;
import com.rondaful.cloud.seller.vo.PublishStyleSearchVO;
import com.rondaful.cloud.seller.vo.PublishStyleTypeSearchVO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class PublishStyleServiceImpl implements PublishStyleService {

    private final Logger logger = LoggerFactory.getLogger(PublishStyleServiceImpl.class);

	@Autowired
	private PublishStyleTypeMapper publishStyleTypeMapper;

	@Autowired
	private PublishStyleMapper publishStyleMapper;
//	@Autowired
//	private EmpowerMapper empowerMapper;
	@Override
	public Page<PublishStyleType> findPublishStyleTypePage(PublishStyleTypeSearchVO vo) throws Exception {
		PageHelper.startPage(vo.getPage(), vo.getRow());
		List<PublishStyleType> list = publishStyleTypeMapper.findPublishStyleTypePage(vo);
		PageInfo<PublishStyleType> pageInfo = new PageInfo<>(list);
		Page<PublishStyleType> page = new Page<>(pageInfo);
		return page;
	}

	@Override
	public PublishStyleType savePublishStyleType(PublishStyleType publishStyleType) {
		this.check(publishStyleType);

		Date date = new Date();
		if(publishStyleType.getId()==null){
			publishStyleType.setCreateTime(date);
			publishStyleType.setUpdateTime(date);
			publishStyleTypeMapper.insertSelective(publishStyleType);
		}else{
			publishStyleType.setUpdateTime(date);
			publishStyleTypeMapper.updateByPrimaryKeySelective(publishStyleType);
		}
		return publishStyleType;
	}

	@Override
	public Integer deletePublishStyleType(Long id) {
		PublishStyleType publishStyleType = new PublishStyleType();
		publishStyleType.setId(id);
		publishStyleType.setStatus(1);
		return publishStyleTypeMapper.updateByPrimaryKeySelective(publishStyleType);
	}

	@Override
	public PublishStyleType getPublishStyleTypeById(Long id) {
		return publishStyleTypeMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<PublishStyleType> getPublishStyleTypeAll(Integer platform, Integer createId,Boolean systemIs) {
		return publishStyleTypeMapper.getPublishStyleTypeAll(platform,createId,systemIs);
	}

	@Override
	public Page<PublishStyle> findPublishStylePage(PublishStyleSearchVO vo) throws Exception {
		PageHelper.startPage(vo.getPage(), vo.getRow());
		List<PublishStyle> list = publishStyleMapper.findPublishStylePage(vo);
		PageInfo<PublishStyle> pageInfo = new PageInfo<>(list);
		Page<PublishStyle> page = new Page<>(pageInfo);
		return page;
	}

	@Override
	public PublishStyle savePublishStyle(PublishStyle publishStyle) {
		this.checkPublishStyle(publishStyle);

		Date date = new Date();
		if(publishStyle.getId()==null){
			publishStyle.setCreateTime(date);
			publishStyle.setUpdateTime(date);
			publishStyleMapper.insertSelective(publishStyle);
		}else{
			publishStyle.setUpdateTime(date);
			publishStyleMapper.updateByPrimaryKeySelective(publishStyle);
		}
		return publishStyle;
	}

	@Override
	public Integer deletePublishStyle(Long id) {
		PublishStyle publishStyle = new PublishStyle();
		publishStyle.setId(id);
		publishStyle.setStatus(1);
		return publishStyleMapper.updateByPrimaryKeySelective(publishStyle);
	}

	@Override
	public PublishStyle getPublishStyleById(Long id) {
		return publishStyleMapper.selectByPrimaryKey(id);
	}

    @Override
    public Integer checkPublishStyle(Long styleTypeId) {
        return publishStyleMapper.checkPublishStyle(styleTypeId);
    }

	@Override
	public PublishStyle getStyleTypeCategory(Integer platform, Long createId, String plCategory) {
		List<PublishStyle> list = publishStyleMapper.findPublishStyle(platform,createId,plCategory);
		if(list!=null && list.size()>0){
			if(list.size()==1){
				return list.get(0);
			}else{
				//二个以上去掉all的数据
				PublishStyle retPublishStyle = null;
				for(PublishStyle style:list){
					if(!"all".equals(style.getApplyAccount())){
						retPublishStyle = style;
					}
				}
				if(retPublishStyle==null){
					retPublishStyle = list.get(0);
				}
				return retPublishStyle;
			}
		}
		return null;
	}


	private void check(PublishStyleType publishStyleType){
		if(publishStyleType.getSystemIs()==null){
			publishStyleType.setSystemIs(false);
		}
		if(publishStyleType.getPlatform()==null){
			publishStyleType.setPlatform(2);
		}

		if(StringUtils.isBlank(publishStyleType.getStyleTypeName())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "风格类型名称不能为空");
		}

		int countNum = publishStyleTypeMapper.checkStyleTypeName(publishStyleType.getId(),publishStyleType.getPlAccount(),publishStyleType.getStyleTypeName());
		if(countNum>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "风格类型名称重复");
		}
	}
	private void checkPublishStyle(PublishStyle publishStyle){
		publishStyle.setStatus(0);
		if(publishStyle.getStyleTypeId()==null){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "风格类型不能为空");
		}
		if(StringUtils.isBlank(publishStyle.getStyleName())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "风格名称不能为空");
		}

		int countNum = publishStyleMapper.checkStyleName(publishStyle.getId(),publishStyle.getCreateId(),publishStyle.getStyleName());
		if(countNum>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "风格名称重复");
		}
		if(StringUtils.isBlank(publishStyle.getApplyAccount())){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "关联品类为空");
		}

		int countPlCategory = publishStyleMapper.checkStylePlCategory(publishStyle.getId(),publishStyle.getCreateId(),publishStyle.getApplyAccount());
		if(countPlCategory>0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "关联品类重复");
		}
//		if("all".equals(publishStyle.getApplyAccount())){
//			publishStyle.setApplyAccountName("全部适用");
//		}
//		//添加账号名称
//		if(StringUtils.isNotBlank(publishStyle.getApplyAccount()) && StringUtils.isBlank(publishStyle.getApplyAccountName())){
//			List<Integer> empowerIds = Lists.newArrayList();
//			String[] ids = publishStyle.getApplyAccount().split(",");
//			for(String id:ids){
//				if(StringUtils.isNotBlank(publishStyle.getApplyAccount())){
//					empowerIds.add(Integer.valueOf(id));
//				}
//			}
//			if(empowerIds.size()==0){
//				return;
//			}
//			List<Empower> listEmpower = empowerMapper.getEmpowerByIds(empowerIds);
//			StringBuffer str = new StringBuffer("");
//			boolean bool = true;
//			listEmpower.forEach(empower->{
//				str.append(","+empower.getAccount());
//			});
//			if("".equals(str.toString())){
//				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "适用账号为空");
//			}
//			publishStyle.setApplyAccountName(str.substring(1));
//		}
	}

}
