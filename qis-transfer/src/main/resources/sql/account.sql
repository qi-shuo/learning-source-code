/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50723
 Source Host           : localhost:3306
 Source Schema         : qis-transfer

 Target Server Type    : MySQL
 Target Server Version : 50723
 File Encoding         : 65001

 Date: 16/02/2021 15:36:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
                           `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                           `cardNo` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '卡号',
                           `name` varchar(55) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '名字',
                           `money` int(10) DEFAULT NULL COMMENT '金钱',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COMMENT='账户';

-- ----------------------------
-- Records of account
-- ----------------------------
BEGIN;
INSERT INTO `account` VALUES (1, '6029621011001', '韩梅梅', 10000);
INSERT INTO `account` VALUES (2, '6029621011000', '李大雷', 10000);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
