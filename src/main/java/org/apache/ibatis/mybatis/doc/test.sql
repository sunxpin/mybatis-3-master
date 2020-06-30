CREATE TABLE items (
  id INT NOT NULL  AUTO_INCREMENT,
  itemsname VARCHAR(32) NOT NULL COMMENT '商品名称',
  price FLOAT(10,1) NOT NULL COMMENT '商品定价',
  detail TEXT COMMENT '商品描述',
  pic VARCHAR(64) DEFAULT NULL COMMENT '商品图片',
  createtime DATETIME NOT NULL COMMENT '生产日期',
  PRIMARY KEY (id)
)  DEFAULT CHARSET=utf8;


/*Table structure for table `t_user` */

CREATE TABLE t_user (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(32) NOT NULL COMMENT '用户名称',
  birthday DATE DEFAULT NULL COMMENT '生日',
  sex CHAR(1) DEFAULT NULL COMMENT '性别',
  address  VARCHAR(256) DEFAULT NULL COMMENT '地址',
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

/*Table structure for table `orders` */

CREATE TABLE orders (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL COMMENT '下单用户id',
  number VARCHAR(30) NOT NULL COMMENT '订单号',
  createtime DATETIME NOT NULL COMMENT '创建订单时间',
  note VARCHAR(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `FK_orders_1` (`user_id`),
  CONSTRAINT `FK_orders_id` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
)  DEFAULT CHARSET=utf8;

/*Table structure for table `orderdetail` */

CREATE TABLE orderdetail (
  id INT NOT NULL AUTO_INCREMENT,
 orders_id INT NOT NULL COMMENT '订单id',
  items_id INT NOT NULL COMMENT '商品id',
  items_num INT  DEFAULT NULL COMMENT '商品购买数量',
  PRIMARY KEY (id),
  KEY `FK_orderdetail_1` (`orders_id`),
  KEY `FK_orderdetail_2` (`items_id`),
  CONSTRAINT `FK_orderdetail_1` FOREIGN KEY (`orders_id`) REFERENCES `orders` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_orderdetail_2` FOREIGN KEY (`items_id`) REFERENCES `items` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
)  DEFAULT CHARSET=utf8;





/*Data for the table `items` */

INSERT  INTO items(itemsname,price,detail,pic,createtime) VALUES
('台式机',3000.0,'该电脑质量非常好！',NULL,'2015-07-07 13:28:53'),
('笔记本',6000.0,'笔记本性能好，质量好！',NULL,'2015-07-08 13:22:57'),
('背包',200.0,'名牌背包，容量大质量好！',NULL,'2015-07-010 13:25:02');

/*Data for the table `orderdetail` */

INSERT  INTO `orderdetail`(`orders_id`,`items_id`,`items_num`) VALUES
 (1,1,1),
 (1,2,3),
 (2,3,4),
 (3,2,3);

/*Data for the table `orders` */

INSERT  INTO `orders`(`user_id`,`number`,`createtime`,`note`) VALUES
(1,'1000010','2015-06-04 13:22:35',NULL),
(1,'1000011','2015-07-08 13:22:41',NULL),
(2,'1000012','2015-07-17 14:13:23',NULL),
(3,'1000012','2015-07-16 18:13:23',NULL),
(4,'1000012','2015-07-15 19:13:23',NULL),
(5,'1000012','2015-07-14 17:13:23',NULL),
(6,'1000012','2015-07-13 16:13:23',NULL);

/*Data for the table `user` */

INSERT  INTO `t_user`(`username`,`birthday`,`sex`,`address`) VALUES
('王五',NULL,'2',NULL),
('张三','2014-07-10','1','北京市'),
('张小明',NULL,'1','河南郑州'),
('陈小明',NULL,'1','河南郑州'),
('张三丰',NULL,'1','河南郑州'),
('陈小明',NULL,'1','河南郑州'),
('王五',NULL,NULL,NULL),
 ('小A','2015-06-27','2','北京'),
('小B','2015-06-27','2','北京'),
('小C','2015-06-27','1','北京'),
('小D','2015-06-27','2','北京');

/*

https://www.cnblogs.com/selene/p/4627446.html

t_user和orders：

　　t_user---->orders：一个用户可以创建多个订单，一对多

　　orders--->t_user：一个订单只由一个用户创建，一对一

orders和orderdetail：

　　orders--->orderdetail：一个订单可以包括多个订单明细，因为一个订单可以购买多个商品，每个商品的购买信息在orderdetail记录，一对多关系

　　orderdetail--> orders：一个订单明细只能包括在一个订单中，一对一

orderdetail和items：

　　orderdetail--->items：一个订单明细只对应一个商品信息，一对一

　　items--> orderdetail:一个商品可以包括在多个订单明细 ，一对多

再分析数据库级别没有关系的表之间是否有业务关系：

orders和items：

　　orders和items之间可以通过orderdetail表建立 关系。

*/

