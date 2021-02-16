CREATE TABLE `account` (
                           `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                           `cardNo` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '卡号',
                           `name` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '名字',
                           `money` int(10) DEFAULT NULL COMMENT '金钱',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COMMENT='账户';