/*2019-07-09 物流方式*/
alter table amazon_publish_listing add logistics_type int(1) default null COMMENT '物流方式1价格最低  2综合最优  3时效最快';

/*2019-07-09 发货仓库*/
alter table amazon_publish_listing add warehouse_id int(1) default null COMMENT '发货仓库';

/*2019-07-15*/
UPDATE `amazon_template_rule`
SET 
 `publish_type` = '3'
WHERE
	(`create_user_name` = 'System');


/*------730需求------------*/

/*2019-07-09 计价模板*/
alter table amazon_template_rule add compute_template varchar(550) default null COMMENT '计价模板';

/*2019-07-22 计价模板*/
alter table amazon_publish_listing add logistics_code varchar(100) default null COMMENT '物流方式code';

/*2019-07-019 刊登模板计价模板通用数据*/
/*{"saleProfit":"30","logisticsAddress":"US","brokeragePriceRatio":"15","brokeragePriceText":0,"items":[]}*/
UPDATE `amazon_template_rule`
SET 
 `compute_template` = '{"saleProfit":"30","logisticsAddress":"US","brokeragePriceRatio":"15","brokeragePriceText":0,"items":[]}'
WHERE
	(`create_user_name` = 'System');
	
	
