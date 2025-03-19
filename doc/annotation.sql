/*
 Navicat Premium Data Transfer

 Source Server         : 小鑫讲解线上
 Source Server Type    : MySQL
 Source Server Version : 50732
 Source Host           : 121.36.103.203:3306
 Source Schema         : hz_asset_test

 Target Server Type    : MySQL
 Target Server Version : 50732
 File Encoding         : 65001

 Date: 19/03/2025 08:49:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for globle_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `globle_sys_config`;
CREATE TABLE `globle_sys_config`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `config_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数名称',
  `config_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数键名',
  `config_value` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数键值',
  `is_system` tinyint(4) NOT NULL DEFAULT 0 COMMENT '系统内置（1:是，0：否）',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '最近一次更新时间',
  `update_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最近一次更新人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `create_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '全局-系统配置参数' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of globle_sys_config
-- ----------------------------
INSERT INTO `globle_sys_config` VALUES ('01', '账号初始密码', 'sys.account.initPassword', 'SYks@', 1, '2023-08-30 11:30:11', '', '2022-11-24 15:21:55', '', '');
INSERT INTO `globle_sys_config` VALUES ('02', '锁定账号的时间', 'sys.login.failedNumAfterLockMinute', '10', 1, '2023-08-27 11:20:52', '', '2022-11-24 15:22:34', '', '');
INSERT INTO `globle_sys_config` VALUES ('03', '登录失败多少次数后锁定账号', 'sys.login.failedNumAfterLockAccount', '5', 1, '2023-08-27 11:20:54', '', '2022-11-24 15:22:59', '', '');
INSERT INTO `globle_sys_config` VALUES ('04', '登录时图形验证码开关', 'sys.login.captchaPicOnOff', 'true', 1, '2023-08-27 11:20:55', '', '2022-11-24 15:23:23', '', '');
INSERT INTO `globle_sys_config` VALUES ('05', '登录时验证码大小写开关', 'sys.login.captchaLowercaseOnOff', 'true', 1, '2023-08-27 11:20:58', '', '2022-11-24 15:23:49', '', '');
INSERT INTO `globle_sys_config` VALUES ('06', '考点端登录时图形验证码开关', 'kw.login.captchaPicOnOff', 'true', 1, '2023-08-30 11:32:29', '', '2022-11-24 15:23:23', '', '');
INSERT INTO `globle_sys_config` VALUES ('07', '识别端登录时图形验证码开关', 'score.login.captchaPicOnOff', 'true', 1, '2023-08-30 11:32:31', '', '2022-11-24 15:23:23', '', '');
INSERT INTO `globle_sys_config` VALUES ('08', '考生端是否允许查询', 'student.query.enable', 'true', 1, '2023-09-10 08:40:30', '', '2023-09-10 08:40:28', '', '');
INSERT INTO `globle_sys_config` VALUES ('09', '考生端允许成绩查询开始时间', 'student.query.startTime', '', 0, NULL, '', '2023-09-26 10:19:44', '', '');
INSERT INTO `globle_sys_config` VALUES ('10', '考生端允许成绩查询结束时间', 'student.query.endTime', '', 0, NULL, '', '2023-09-26 10:19:44', '', '');
INSERT INTO `globle_sys_config` VALUES ('11', '应用级缓存开关', 'cache.app.enable', 'true', 1, '2023-10-05 00:09:18', '', '2023-10-05 00:09:11', '', '');

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
  `menu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '菜单名称',
  `pid` int(11) NOT NULL DEFAULT 0 COMMENT '父级菜单',
  `route` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由路径',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '图片',
  `sort` int(11) NOT NULL COMMENT '排序',
  `is_del` int(4) NOT NULL DEFAULT 0 COMMENT '是否删除0否1是',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, '固定资产', 0, '', 'fixed_asset', 0, 0);
INSERT INTO `menu` VALUES (2, '耗材资产', 0, '', 'consume_asset', 0, 0);
INSERT INTO `menu` VALUES (3, '资产档案', 1, '/assetArchives', 'asset_record_mor', 1, 0);
INSERT INTO `menu` VALUES (4, '部门资产', 1, '/departmentalAssets', 'department_asset_mor', 2, 0);
INSERT INTO `menu` VALUES (5, '报废资产', 1, '/scrapAssets', 'scrap_asset_mor', 3, 0);
INSERT INTO `menu` VALUES (6, '项目管理', 1, '/projectManagement', 'project_management_mor', 4, 0);
INSERT INTO `menu` VALUES (7, '分类管理', 1, '/classificationManagement', 'classify_management_mor', 5, 0);
INSERT INTO `menu` VALUES (8, '系统设置', 0, '', 'system_settings', 0, 0);
INSERT INTO `menu` VALUES (9, '用户管理', 8, '/userManagement', 'user_management_mor', 1, 0);
INSERT INTO `menu` VALUES (10, '角色管理', 8, '/roleManagement', 'role_management_mor', 2, 0);
INSERT INTO `menu` VALUES (11, '操作日志', 8, '/operationLog', 'operate_mor', 3, 0);
INSERT INTO `menu` VALUES (12, '登录日志', 8, '/loginLog', 'operate_mor', 4, 0);
INSERT INTO `menu` VALUES (13, '耗材档案', 2, '/consumableFile', 'consumable_file', 1, 0);
INSERT INTO `menu` VALUES (14, '库存查询', 2, '/inventoryInquiry', 'inventory_inquiry', 2, 0);
INSERT INTO `menu` VALUES (15, '入库管理', 2, '/warehouseManagement', 'warehouse_management', 3, 0);
INSERT INTO `menu` VALUES (16, '出库管理', 2, '/outboundManagement', 'outbound_management', 4, 0);
INSERT INTO `menu` VALUES (17, '耗材分类', 2, '/classificationOfConsumables', 'consume_material', 5, 0);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `is_del` int(4) NULL DEFAULT 0 COMMENT '是否删除0否1是',
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '资产管理员', 0, '', NULL, '', NULL, '');
INSERT INTO `role` VALUES (2, '普通用户', 0, '', NULL, '', NULL, '');

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色菜单表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu` VALUES (1, 1);
INSERT INTO `role_menu` VALUES (1, 2);
INSERT INTO `role_menu` VALUES (1, 3);
INSERT INTO `role_menu` VALUES (1, 4);
INSERT INTO `role_menu` VALUES (1, 5);
INSERT INTO `role_menu` VALUES (1, 6);
INSERT INTO `role_menu` VALUES (1, 7);
INSERT INTO `role_menu` VALUES (1, 8);
INSERT INTO `role_menu` VALUES (1, 9);
INSERT INTO `role_menu` VALUES (1, 10);
INSERT INTO `role_menu` VALUES (1, 11);
INSERT INTO `role_menu` VALUES (1, 12);
INSERT INTO `role_menu` VALUES (1, 13);
INSERT INTO `role_menu` VALUES (1, 14);
INSERT INTO `role_menu` VALUES (1, 15);
INSERT INTO `role_menu` VALUES (1, 16);
INSERT INTO `role_menu` VALUES (1, 17);
INSERT INTO `role_menu` VALUES (2, 1);
INSERT INTO `role_menu` VALUES (2, 2);
INSERT INTO `role_menu` VALUES (2, 4);
INSERT INTO `role_menu` VALUES (2, 13);
INSERT INTO `role_menu` VALUES (2, 14);

-- ----------------------------
-- Table structure for sys_user_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_login_log`;
CREATE TABLE `sys_user_login_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户姓名',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '结果状态',
  `login_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录时间',
  `ip_addr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'browser',
  `os` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'os操作系统',
  `msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '返回数据',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `opt_log_request_time`(`login_location`) USING BTREE,
  INDEX `opt_log_user_name`(`account`) USING BTREE,
  INDEX `opt_log_status`(`status`) USING BTREE,
  INDEX `opt_log_login_time`(`login_time`) USING BTREE,
  INDEX `opt_log_browser`(`browser`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户登录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_opt_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_opt_log`;
CREATE TABLE `sys_user_opt_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `date_str` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '归档日期:yyyy-MM-dd',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作内容',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户登录账号',
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '账户主键',
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户姓名',
  `operation_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作类型',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '操作状态（0：正常，1：异常）',
  `client_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户端IP',
  `ip_region` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'ip所属地区',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '请求的方法',
  `request_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '请求方式',
  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '请求路径',
  `request_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
  `request_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '请求时间',
  `cost_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '请求耗时（毫秒单位）',
  `browser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '浏览器',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作系统',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '访问的ua',
  `response_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '返回数据',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误提示信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `opt_log_date_str`(`date_str`) USING BTREE,
  INDEX `opt_log_account`(`account`) USING BTREE,
  INDEX `opt_log_operation_type`(`operation_type`) USING BTREE,
  INDEX `opt_log_request_time`(`request_time`) USING BTREE,
  INDEX `opt_log_content`(`content`) USING BTREE,
  INDEX `opt_log_user_id`(`user_id`) USING BTREE,
  INDEX `opt_log_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_opt_log
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `mobile` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '手机号',
  `real_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `ding_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '钉钉ID',
  `is_del` int(4) NULL DEFAULT 0 COMMENT '是否删除0否1是',
  `state` tinyint(4) NULL DEFAULT 1 COMMENT '状态1启用0停用',
  `dutie` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '职务',
  `role_id` tinyint(4) NULL DEFAULT NULL COMMENT '角色ID',
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mobile`(`mobile`) USING BTREE,
  INDEX `ding_id`(`ding_id`) USING BTREE,
  INDEX `is_del`(`is_del`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  INDEX `user_type`(`dutie`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '15931167376', '卢东岳', '121063442221157113', 0, 1, '', 1, '1', '2024-04-28 10:31:01', '', '2024-04-28 14:49:25', '');

SET FOREIGN_KEY_CHECKS = 1;
