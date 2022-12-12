/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : localhost:3306
 Source Schema         : security_jwt

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 13/12/2022 00:16:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS security_jwt;
USE security_jwt;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`
(
    `id`       bigint UNSIGNED                                         NOT NULL AUTO_INCREMENT,
    `username` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL,
    `password` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL,
    `email`    varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL,
    `avatar`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL,
    `status`   tinyint                                                 NULL     DEFAULT 1,
    `role`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'user',
    `created`  datetime                                                NULL     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 21
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info`
VALUES (1, 'pcdd', '1', 'foo@qq.com',
        'https://img0.baidu.com/it/u=1942253063,3807598283&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', 1,
        'ROLE_admin,ROLE_sa,user.edit', '2021-02-27 22:59:28');
INSERT INTO `user_info`
VALUES (2, 'zhangsan', '1', 'bar@qq.com',
        'https://img2.baidu.com/it/u=4244269751,4000533845&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', 1, 'ROLE_user',
        '2018-09-06 22:59:43');
INSERT INTO `user_info`
VALUES (3, 'Anya', '1', 'aka@qq.com',
        'https://img0.baidu.com/it/u=869010084,3883818022&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500', 0, 'ROLE_user',
        '2021-04-03 22:59:56');

SET FOREIGN_KEY_CHECKS = 1;
