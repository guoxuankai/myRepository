package com.rondaful.cloud.seller.service;

import java.util.List;

import com.rondaful.cloud.seller.entity.amazon.AmazonNode;

public interface AmazonXsdTemplateService {

	/**
	 *  获取xsd对应的模版属性
	 */
	public List<AmazonNode> getFirstXsdTemplateCategory( String marketplaceId) throws Exception ;
	
	/**
	 * 根据path通过类反射获得属性
	 */
	public  List<AmazonNode> getNextXsdTemplateCategory( String classPath,String marketplaceId)  throws ClassNotFoundException, InstantiationException, IllegalAccessException ;
}
