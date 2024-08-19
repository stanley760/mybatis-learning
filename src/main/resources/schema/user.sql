DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` int(11) DEFAULT NULL,
                        `user_name` varchar(255) DEFAULT NULL,
                        `age` int(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `user` VALUES ('1', '张三', '18');