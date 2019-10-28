/*2019-05-08 亚马逊刊登主表添加数据来源*/
alter table amazon_publish_listing add data_source INT(1) not null default 1 COMMENT '数据来源1品连2亚马逊';

/*2019-05-13 主账号ID*/
alter table amazon_template_rule add top_user_id INT(11) default null COMMENT '主账号id';

/*2019-05-17 老数据都是主账号所以同步处理top_user_id*/
UPDATE `amazon_template_rule`
SET 
 `top_user_id` = '236'
WHERE
	(`create_user_id` = '236');



UPDATE `amazon_template_rule`
SET 
 `top_user_id` = '242'
WHERE
	(`create_user_id` = '242');



UPDATE `amazon_template_rule`
SET 
 `top_user_id` = '103'
WHERE
	(`create_user_id` = '103');



UPDATE `amazon_template_rule`
SET 
 `top_user_id` = '104'
WHERE
	(`create_user_id` = '104');
	
/*2019-05-18 同步处理publish_message中的sku*/	
update amazon_publish_listing set publish_message=replace(publish_message,'sKU','sku'); 	

/*2019-06-12 添加字段在线时间*/
alter table amazon_publish_listing add online_time datetime(0)  DEFAULT NULL COMMENT '上线时间';  

/*2019-06-22 添加是否有必填项字段*/
alter table amazon_publish_listing add has_required INT(1) not null default 0 COMMENT '是否有必填项字段,0没有1有';