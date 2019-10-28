
/*2019-03-27 刊登模板表 */
CREATE TABLE `amazon_template_rule` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `empower_id` bigint(20) NOT NULL,
  `template_name` varchar(180) NOT NULL COMMENT '模板名称',
  `default_template` int(1) NOT NULL COMMENT '是否默认模板 0 ：是 1：否  2:全局默认模板',
  `category_first_rule` varchar(120) NOT NULL COMMENT '商品第一分类, [{"检索分类映射表":"检索分类映射表","no":"1"}]',
  `category_second_rule` varchar(120) DEFAULT NULL COMMENT '商品第二分类, [{"不设置商品第二分类":"不设置商品第二分类","no":"1"},{"检索分类映射表":"检索分类映射表","no":"1"}]',
  `publish_type` int(1) NOT NULL DEFAULT '1' COMMENT '刊登类型，1:单属性格式,2:多属性格式',
  `fulfillment_latency` int(2) DEFAULT NULL COMMENT '从订单生成到发货之间的天数，默认2天内发货 (1到30之间的整数)',
  `platform_sku_rule` varchar(256) NOT NULL COMMENT '平台SKU ， [{"固定值":"1111","no":"1"},{"商品名称":"商品名称","no":"4"},{"品连sku":"品连sku","no":"2"}]',
  `brand_rule` varchar(256) NOT NULL COMMENT '品牌名    [{"默认值":"1111","no":"1"},{"取店铺名称":"取店铺名称","no":"4"},{"实际品牌名称":"实际品牌名称","no":"2"}，{"实际品牌名称若为空":"55555555","no":"2"}]',
  `product_title_rule` varchar(120) NOT NULL COMMENT '商品标题    [{"商品英文名":"商品英文名","no":"1"},{"品牌名称+商品英文名":"品牌名称+商品英文名","no":"4"}]',
  `product_no_rule` varchar(120) NOT NULL COMMENT '商品编码    [{"用户自行填写":"用户自行填写","no":"1"},{"自动获取UPC":"自动获取UPC","no":"2"},{"自动获取EAN":"自动获取EAN","no":"3"}]',
  `product_price_rule` varchar(120) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '价格  [{"用户自行填写":"用户自行填写","no":"1"}]',
  `quantity_rule` varchar(120) NOT NULL COMMENT '可售数  [{"默认值":"默认值","no":"1"}]',
  `manufacturer_rule` varchar(120) NOT NULL COMMENT '制造商 [{"默认值":"1111","no":"1"},{"取店铺名称":"取店铺名称","no":"4"},{"实际品牌名称":"实际品牌名称","no":"2"}，{"实际品牌名称若为空":"55555555","no":"2"}]',
  `part_number` varchar(255) DEFAULT NULL COMMENT 'part_number [{"默认值":"1111","no":"1"},{"设置为平台SKU":"设置为平台SKU","no":"2"}]',
  `description_rule` varchar(320) DEFAULT NULL COMMENT '商品描述  [{"随机欢迎语":["aaa","bbbbb","ccccc"],"no":"1"},{"商品标题":"商品标题","no":"2"},{"商品卖点":"商品卖点","no":"3"},{"商品描述":"商品描述","no":"2"},{"包装清单":"包装清单","no":"2"},{"随机结束语":["1111","2222","33333"],"no":"2"}]',
  `parent_main_image_rule` varchar(255) DEFAULT NULL COMMENT '父体图片 "主图":[{"从SPU图片中随机取一张","从SPU图片中随机取一张"},{"混合所有SKU的主图并从中随机取一张":"混合所有SKU的主图并从中随机取一张"},{"若SPU图片为空或不足取":"混合所有SKU的主图并从中随机取一张"}]',
  `parent_addition_image_rule` varchar(255) DEFAULT NULL COMMENT '父体图片  "附图":[{"从SPU图片中随机取":{"min":1,"max":8}},{"混合所有SKU的附图并从中随机取随机取":{"min":1,"max":8}},{"若SPU图片为空或不足取":"混合所有SKU的附图并从中随机取随机取"}]',
  `child_main_image_rule` varchar(255) DEFAULT NULL COMMENT '子体图片 "主图":[{"从SKU图片中随机取一张","从SKU图片中随机取一张"}]',
  `child_addition_image_rule` varchar(255) DEFAULT NULL COMMENT '子体图片 "附图":[{"从SKU图片中随机取":{"min":1,"max":8}}]',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_name` varchar(50) DEFAULT NULL,
  `update_user_name` varchar(50) DEFAULT NULL COMMENT '更新操作人名字',
  `update_user_id` bigint(20) DEFAULT NULL COMMENT '更新操作人id',
  `third_party_name` varchar(50) DEFAULT NULL COMMENT '第三方的账号或id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=538 DEFAULT CHARSET=utf8 COMMENT='amazon刊登模板规则';

/*2019-03-27 创建通用模板数据*/
INSERT INTO `amazon_template_rule` (
	`empower_id`,
	`template_name`,
	`default_template`,
	`category_first_rule`,
	`category_second_rule`,
	`publish_type`,
	`fulfillment_latency`,
	`platform_sku_rule`,
	`brand_rule`,
	`product_title_rule`,
	`product_no_rule`,
	`product_price_rule`,
	`quantity_rule`,
	`manufacturer_rule`,
	`part_number`,
	`description_rule`,
	`parent_main_image_rule`,
	`parent_addition_image_rule`,
	`child_main_image_rule`,
	`child_addition_image_rule`,
	`create_user_id`,
	`create_time`,
	`update_time`,
	`create_user_name`,
	`update_user_name`,
	`update_user_id`,
	`third_party_name`
)
VALUES
	(
		'0',
		'通用模板',
		'2',
		'[{\"classifyMap\":\"classifyMap\"}]',
		'[{\"notSet\":\"notSet\"}]',
		'2',
		'2',
		'[{\"notRuleNo\":\"notRuleNo\"}]',
		'[{\"shopName\":\"shopName\"}]',
		'[{\"brandAddGoodsEnName\":\"brandAddGoodsEnName\"}]',
		'[{\"default\":\"default\"}]',
		'[{\"inputBox\":\"inputBox\"}]',
		'[{\"default\":\"1000\"}]',
		'[{\"shopName\":\"shopName\"}]',
		'[{\"platformSKU\":\"platformSKU\"}]',
		'[{\"doodsTitle\":[\"通用模板商品标题\"],\"no\":1},{\"goodsVirtue\":[\"通用模板商品卖点\"],\"no\":2},{\"goodsDescrip\":[\"通用模板商品描述\"],\"no\":3},{\"packList\":[\"通用模板包装清单\"],\"no\":4}]',
		'[{\"findToSKU\":\"findToSKU\"}]',
		'[{\"findToSKU\":{\"min\":1,\"max\":8}}]',
		'[{\"findToSKU\":\"findToSKU\"}]',
		'[{\"findToSKU\":{\"min\":1,\"max\":8}}]',
		'0',
		NOW(),
		NOW(),
		'System',
		'System',
		NULL,
		NULL
	);
	
/*2019-03-28  刊登操作日志表*/	
CREATE TABLE `publish_log` (
  `id` bigint(12) NOT NULL AUTO_INCREMENT,
  `content` text COMMENT '操作内容',
  `type` varchar(10) DEFAULT NULL COMMENT '操作类型',
  `publish_id` bigint(20) DEFAULT NULL COMMENT '刊登id',
  `operator_id` int(11) DEFAULT NULL COMMENT '操作人id',
  `operator_name` varchar(60) DEFAULT NULL COMMENT '操作人名字',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=611 DEFAULT CHARSET=utf8 COMMENT='刊登操作日志表';
	


