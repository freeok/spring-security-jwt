SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS spring_security_jwt;
USE spring_security_jwt;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
create table user_info
(
    id          bigint unsigned auto_increment
        primary key,
    username    varchar(255)                     not null,
    password    varchar(255)                     not null,
    email       varchar(255)                     null,
    avatar      varchar(255)                     null,
    status      tinyint      default 1           null,
    permissions varchar(255) default 'ROLE_user' not null,
    created     datetime                         null
) ENGINE = InnoDB
  CHARACTER SET = utf8;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO spring_security_jwt.user_info (id, username, password, email, avatar, status, permissions, created)
VALUES (1, 'pcdd', '$2a$10$K9X9H9SSI7Ap6cEtTXY55ewGTHs2IRj7APlbtaxJo.BLzdqMg2TQ2', 'foo@qq.com',
        'https://img0.baidu.com/it/u=1942253063,3807598283&fm=253', 1, 'ROLE_admin,ROLE_sa,user.edit',
        '2021-02-27 22:59:28');
INSERT INTO spring_security_jwt.user_info (id, username, password, email, avatar, status, permissions, created)
VALUES (2, 'Josh Bloch', '$2a$10$K9X9H9SSI7Ap6cEtTXY55ewGTHs2IRj7APlbtaxJo.BLzdqMg2TQ2', 'bar@qq.com',
        'https://img2.baidu.com/it/u=4244269751,4000533845&fm=253', 1, 'ROLE_user,user.view', '2018-09-06 22:59:43');
INSERT INTO spring_security_jwt.user_info (id, username, password, email, avatar, status, permissions, created)
VALUES (3, 'Doug Lea', '$2a$10$K9X9H9SSI7Ap6cEtTXY55ewGTHs2IRj7APlbtaxJo.BLzdqMg2TQ2', 'aka@qq.com',
        'https://img0.baidu.com/it/u=869010084,3883818022&fm=253', 0, 'ROLE_user,user.edit', '2021-04-03 22:59:56');

SET FOREIGN_KEY_CHECKS = 1;
