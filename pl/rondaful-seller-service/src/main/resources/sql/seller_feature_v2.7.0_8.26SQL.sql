/*2019-08-06 销售人员ID*/
alter table amazon_publish_listing add sale_user_id int(11) default null COMMENT '销售人员id';


/*-------------2.8_916版本sql-------------------------*/
/*2019-09-02 plsku捆绑销售数量*/
alter table amazon_publish_sub_listing add pl_sku_sale_num int(1) default 1 COMMENT 'plsku捆绑销售数量';