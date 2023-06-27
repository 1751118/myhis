/*
 Navicat Premium Data Transfer

 Source Server         : wudapeng
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : hospital_info_system

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 09/05/2023 16:58:24
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '123456');

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `did` bigint(0) NULL DEFAULT NULL,
  `pid` bigint(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '发起预约的时间',
  `appoint_time` datetime(0) NULL DEFAULT NULL COMMENT '预约的时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '预约完成的时间',
  `status` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (7, 1653650796141182978, 1, '2023-04-04 00:00:00', '2023-04-24 15:00:00', '2023-05-08 10:52:08', 1);

-- ----------------------------
-- Table structure for cases
-- ----------------------------
DROP TABLE IF EXISTS `cases`;
CREATE TABLE `cases`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `did` bigint(0) NULL DEFAULT NULL COMMENT '医生id',
  `pid` bigint(0) NULL DEFAULT NULL COMMENT '患者id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `result` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '治疗结果',
  `remedy` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '治疗方式：手术、处方等',
  `diagnosis` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '医生诊断',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cases
-- ----------------------------
INSERT INTO `cases` VALUES (2, 1653650796141182978, 1, '2023-04-04 00:00:00', '2023-04-24 23:59:59', '痊愈', '处方', '冠状病毒');
INSERT INTO `cases` VALUES (5, 1653650796141182978, 1655392291936370689, '2023-04-04 00:00:00', '2023-04-24 23:59:59', '好转', '处方', '胸闷');

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `hid` bigint(0) NULL DEFAULT NULL,
  `department` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `level` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `uid` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1655420467852963842 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES (1653650796141182978, '华佗', 1, '内科', '医师', '2023-05-03 14:40:48', 2);

-- ----------------------------
-- Table structure for drug
-- ----------------------------
DROP TABLE IF EXISTS `drug`;
CREATE TABLE `drug`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `produce_time` date NULL DEFAULT NULL COMMENT '生产日期',
  `expiration` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '保质期',
  `status` int(0) NULL DEFAULT NULL COMMENT '售卖状态',
  `hid` bigint(0) NULL DEFAULT NULL COMMENT '指定某家医院的情况',
  `count` int(0) NULL DEFAULT NULL COMMENT '现存数量',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最后操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of drug
-- ----------------------------
INSERT INTO `drug` VALUES (5, '左氧氟沙星滴眼液', 8.50, '2022-05-04', '12个月', 1, 151, 10, '2023-05-04 19:32:07');
INSERT INTO `drug` VALUES (6, '莲花清瘟颗粒', 9.15, '2022-05-04', '24个月', 1, 150, 10, '2023-05-08 10:38:20');
INSERT INTO `drug` VALUES (7, '板蓝根', 9.15, '2022-05-04', '24个月', 1, 150, 10, '2023-05-08 10:40:41');

-- ----------------------------
-- Table structure for hospital
-- ----------------------------
DROP TABLE IF EXISTS `hospital`;
CREATE TABLE `hospital`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `telephone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `level` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1655415814310359043 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hospital
-- ----------------------------
INSERT INTO `hospital` VALUES (1, '北京市仁和医院', '北京朝阳区', '85623521', '一级');
INSERT INTO `hospital` VALUES (150, '嘉定区人民医院', '嘉定区', '65989999', '高级');
INSERT INTO `hospital` VALUES (151, '杨浦区人民医院', '杨浦区', '65898544', '一级丙等');
INSERT INTO `hospital` VALUES (1653606651959832578, '同济大学校医院', '同济大学', '65989120', '一级次等');
INSERT INTO `hospital` VALUES (1653637356668436481, '上海市眼科医院', '杨浦区', '65989120', '三级');
INSERT INTO `hospital` VALUES (1653637633429598209, '中医院', '静安区', '65989120', '三级');
INSERT INTO `hospital` VALUES (1653638365675257858, '同济大学校医院111', '同济大学', '65989120', '三级');
INSERT INTO `hospital` VALUES (1653638954983415809, '同济大学校医院222', '同济大学', '65989120', '三级');
INSERT INTO `hospital` VALUES (1655400416856850434, '贵州省人民医院', '贵阳市', '65989120', '二级');

-- ----------------------------
-- Table structure for patient
-- ----------------------------
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `uid` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1655394017775030275 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of patient
-- ----------------------------
INSERT INTO `patient` VALUES (1, '张三', 1, '同济大学', '2023-05-06 15:09:17');
INSERT INTO `patient` VALUES (1655392291936370689, '李四', 1655392291898621953, '同济大学黄渡校区', '2023-05-08 10:00:53');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `age` int(0) NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `telephone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'zhangsan', '123456', 22, '男', '18338565555');
INSERT INTO `user` VALUES (2, 'huatuo', '123456', 66, '男', '18018594177');
INSERT INTO `user` VALUES (1655392291898621953, 'lisi', '123456', 25, '男', '18018594171');
INSERT INTO `user` VALUES (1655420467785854978, 'madaha', '123123', 18, '男', '18018594179');

SET FOREIGN_KEY_CHECKS = 1;
