/*
SQLyog Ultimate
MySQL - 5.5.61 : Database - zhiping
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `dept` */

CREATE TABLE `dept` (
  `d_id` varchar(255) NOT NULL COMMENT '部门编号',
  `d_name` varchar(255) DEFAULT NULL COMMENT '部门名称  1.研发部  2.人事部  3.公关部  4.教育部',
  `d_location` varchar(255) DEFAULT NULL COMMENT '部门地点',
  `d_wei` varchar(255) DEFAULT NULL COMMENT '部门大小  分为：小型、中小型、大型、特大型',
  PRIMARY KEY (`d_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `dept` */

insert  into `dept`(`d_id`,`d_name`,`d_location`,`d_wei`) values ('1c085ffd6a9348c182ce20dda22ca6a9','研发部','广东深圳龙华','小型');
insert  into `dept`(`d_id`,`d_name`,`d_location`,`d_wei`) values ('b1d52de9fed24dcab49e188d7c8e09c5','人事部','珠海','大型');
insert  into `dept`(`d_id`,`d_name`,`d_location`,`d_wei`) values ('16596076e65346699f6c081b5957036e','公关部','广东深圳龙华','小型');
insert  into `dept`(`d_id`,`d_name`,`d_location`,`d_wei`) values ('91f5c02ea8f44d04a35577dc4f517119','教育部','厦门','中小型');

/*Table structure for table `emp` */

CREATE TABLE `emp` (
  `e_id` varchar(32) NOT NULL COMMENT '员工编号',
  `e_portrait` varchar(255) DEFAULT NULL COMMENT '员工头像',
  `e_name` varchar(255) DEFAULT NULL COMMENT '员工账号',
  `e_pwd` varchar(255) DEFAULT NULL COMMENT '员工密码  要md5加密',
  `e_uname` varchar(255) DEFAULT NULL COMMENT '员工姓名',
  `e_birthday` date DEFAULT NULL COMMENT '员工生日',
  `e_level` int(11) DEFAULT NULL COMMENT '员工级别 1.王者  2.黄金  3.青铜 4.白银',
  `e_six` int(11) DEFAULT NULL COMMENT '员工性别  1.男  2.女',
  `e_wages` int(255) DEFAULT NULL COMMENT '员工工资',
  `e_hobby` varchar(255) DEFAULT NULL COMMENT '员工爱好',
  `e_mibiao` varchar(255) DEFAULT NULL COMMENT '员工密保人',
  `e_midaan` varchar(255) DEFAULT NULL COMMENT '员工密保答案',
  `e_add` datetime DEFAULT NULL COMMENT '员工创建时间',
  `e_update` datetime DEFAULT NULL COMMENT '员工更新时间',
  `e_login` datetime DEFAULT NULL COMMENT '员工上一次登录时间',
  `e_xzlogin` datetime DEFAULT NULL COMMENT '员工现在登录时间',
  `e_remark` varchar(255) DEFAULT NULL COMMENT '员工备注',
  `e_deptid` varchar(255) DEFAULT NULL COMMENT '员工外键',
  PRIMARY KEY (`e_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `emp` */

insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('02f2ce04634a41eda4abf0f5ddada562','/3.png','55555','af2b2e4a8a376a441ef3bb72022d1d8e','积分工会尽快','2012-11-11',1,2,444,'18824250388','456435756475647555','外环境污染沙特人身体还有人为活动是否会发生的','2018-12-12 03:25:37','2018-12-23 09:33:52','2019-01-04 18:37:13','2019-01-04 18:37:13','uu8','91f5c02ea8f44d04a35577dc4f517119');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('098753b4bf4a4f7ea0ad33ebe33d0764','img/zhiping.jpg','8888','cf79ae6addba60ad018347359bd144d2','8888','1019-05-03',4,1,8888,'17412745278','657546354634563455','房间号是否感觉发图截图','2018-12-23 10:30:04','2018-12-24 08:12:37','2019-01-04 18:37:13','2019-01-04 18:37:13','ertyu','1c085ffd6a9348c182ce20dda22ca6a9');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('1ed1aed422f1470782b1d69e31dd5c55','img/3.png','gggg','e6b9b7dff9a0c807db537bc625157a8f','gggg','2009-03-03',1,2,10000,'18824250388','635754676543563454','忽而是他家儿童与金额体育局','2018-12-16 09:13:27','2018-12-18 23:58:11','2019-01-04 18:37:13','2019-01-04 18:37:13','不是的','91f5c02ea8f44d04a35577dc4f517119');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('3d8b60ed4194484bb35962959cad2f00','img/zhiping.jpg','i56i7','218ac3fe3df6ff2c8fe8f9353f1084f6','85687','2019-01-03',2,2,587,'18777777777','431124199703131688','户无人试图将对方国家的规范环境和法国','2019-01-04 11:27:55','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','b1d52de9fed24dcab49e188d7c8e09c5');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('4671cf029fee44f0a3b537358b3bd839','img/3.png','2222','2497a7d9315fe5cf35214f7054837df0','日4忍','1019-03-03',2,1,2500,'18824250388','564545645645665433','京东方已经安然额问题为儿童热问题永无永无','2018-12-11 18:50:22','2018-12-12 10:26:39','2019-01-04 18:37:13','2019-01-04 18:37:13','得分后卫色弱','91f5c02ea8f44d04a35577dc4f517119');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('4b5a27ba654c46f1b96cb4751fc7fee5','img/zhiping.jpg','7867','2f7b52aacfbf6f44e13d27656ecb1f59','1111','2009-01-03',1,2,1111,'12222222222','431124199703131688','法院告过过过过不不才发现是豆腐干热问题我也','2018-12-24 08:04:27','2019-01-04 11:27:23','2019-01-04 18:37:13','2018-12-24 08:05:13','444','16596076e65346699f6c081b5957036e');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('7aceab91b2654b12bde9504fd83020d2','img/zhiping.jpg','qwe','76d80224611fc919a5d54f0ff9fba446','志平：责任感','2019-01-04',1,1,188000,'13333333333','345634564564555455','海棠依旧特右上方的给我儿童意外而一位台湾而我国','2019-01-04 11:22:18','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','1c085ffd6a9348c182ce20dda22ca6a9');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('bda5465225ea4f18bb5828543c58b1d3','img/zhiping.jpg','qaz','4eae18cf9e54a0f62b44176d074cbe2f','志平：责任感','2019-01-04 18:37:13',1,1,188000,'16666666666','431124199703131688','他惹我而不是富士通热一热天圆地方','2019-01-04 10:59:14','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','1c085ffd6a9348c182ce20dda22ca6a9');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('c1bbb38c439842f3aac1739906eada2a','img/3.png','555','6f181f206b8555c5dc619bc206ab35ad','23','1994-03-03',4,2,500,'18824250388','345645546456343456','尔特乳头和月供少奋斗和施工方和对方过后','2018-12-16 18:24:22','2018-12-16 20:44:59','2019-01-04 18:37:13','2019-01-04 18:37:13','热我还以为','1c085ffd6a9348c182ce20dda22ca6a9');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('d11e29e007d444a4bc9fa190230e84e1','img/zhiping.jpg','5555','6074c6aa3488f3c2dddff2a7ca821aab','志平：责任感','1997-03-13',1,1,15000,'旅游家','志平之家','你还是那么幽默.........','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','1c085ffd6a9348c182ce20dda22ca6a9');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('d8b7b9400a4e4f91a1ee85a6c21e8170','img/zhiping.jpg','4444','44eb55eef77d762b1a9aa179075c222e','志平：责任感','1997-03-13',1,1,15000,'18824250388','431124199703131688','三个结合地方哈地方很多份水果和如何让','2018-12-16 19:01:38','2019-01-04 18:36:15','2019-01-04 18:37:13','2019-01-04 18:37:13','额外人也','b1d52de9fed24dcab49e188d7c8e09c5');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('f687bdb7264f465e99680244d7baeda2','img/zhiping.jpg','11','d41d8cd98f00b204e9800998ecf8427e','志平：责任感','1997-03-13',1,2,15000,'17777775555','345634565466345543','无法统计和复印件的风格很是的粉红色发过火','2019-01-03 14:23:48','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','16596076e65346699f6c081b5957036e');
insert  into `emp`(`e_id`,`e_portrait`,`e_name`,`e_pwd`,`e_uname`,`e_birthday`,`e_level`,`e_six`,`e_wages`,`e_hobby`,`e_mibiao`,`e_midaan`,`e_add`,`e_update`,`e_login`,`e_xzlogin`,`e_remark`,`e_deptId`) values ('fb193010181c478dbef8d7846b3443fc','img/zhiping.jpg','222','bcbe3365e6ac95ea2c0343a2395834dd','志平：责任感','1997-03-13',1,1,15000,'18824250388','431124199703131688','挥金如土世纪东方哈地方还是法国队和大润发','2019-01-03 14:26:08','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','2019-01-04 18:37:13','1c085ffd6a9348c182ce20dda22ca6a9');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
