-- 铁路12306售票系统数据库初始化脚本

CREATE DATABASE IF NOT EXISTS tielu12306 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tielu12306;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(32) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
    `real_name` VARCHAR(20) DEFAULT NULL COMMENT '真实姓名',
    `id_card` VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    `user_type` TINYINT DEFAULT 0 COMMENT '用户类型: 0成人 1学生 2儿童',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 乘车人表
CREATE TABLE IF NOT EXISTS `passenger` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `passenger_name` VARCHAR(20) NOT NULL COMMENT '乘车人姓名',
    `passenger_id_card` VARCHAR(20) NOT NULL COMMENT '身份证号',
    `passenger_type` TINYINT DEFAULT 0 COMMENT '乘车人类型: 0成人 1学生 2儿童',
    `phone` VARCHAR(11) DEFAULT NULL COMMENT '手机号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='乘车人表';

-- 车站表
CREATE TABLE IF NOT EXISTS `station` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `station_code` VARCHAR(10) NOT NULL COMMENT '车站编码(电报码)',
    `station_name` VARCHAR(50) NOT NULL COMMENT '车站名称',
    `city_code` VARCHAR(10) DEFAULT NULL COMMENT '所属城市编码',
    `pinyin` VARCHAR(50) DEFAULT NULL COMMENT '拼音',
    `pinyin_short` VARCHAR(20) DEFAULT NULL COMMENT '拼音首字母',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0停用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_station_code` (`station_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车站表';

-- 车次表
CREATE TABLE IF NOT EXISTS `train` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `train_no` VARCHAR(20) NOT NULL COMMENT '车次号 (如G1234)',
    `train_type` VARCHAR(5) NOT NULL COMMENT '类型: G高铁 D动车 K快速 T特快 Z直达',
    `start_station_id` BIGINT NOT NULL COMMENT '始发站ID',
    `end_station_id` BIGINT NOT NULL COMMENT '终点站ID',
    `seat_types` VARCHAR(50) DEFAULT NULL COMMENT '席位类型 JSON数组',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0停运 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_train_no` (`train_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次表';

-- 车次线路表
CREATE TABLE IF NOT EXISTS `train_station` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `train_id` BIGINT NOT NULL COMMENT '车次ID',
    `station_id` BIGINT NOT NULL COMMENT '车站ID',
    `station_seq` INT NOT NULL COMMENT '站点序号(从1开始)',
    `arrival_time` TIME DEFAULT NULL COMMENT '到站时间',
    `departure_time` TIME DEFAULT NULL COMMENT '离站时间',
    `distance_km` INT DEFAULT 0 COMMENT '距始发站里程(km)',
    `stop_minutes` INT DEFAULT 0 COMMENT '停留分钟数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_train_id` (`train_id`),
    KEY `idx_station_id` (`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次线路表';

-- 车次票价表
CREATE TABLE IF NOT EXISTS `train_price` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `train_id` BIGINT NOT NULL COMMENT '车次ID',
    `seat_type` VARCHAR(10) NOT NULL COMMENT '席位类型',
    `base_price` DECIMAL(10,2) NOT NULL COMMENT '基础票价',
    `price_per_km` DECIMAL(10,2) DEFAULT 0 COMMENT '每公里价格',
    PRIMARY KEY (`id`),
    KEY `idx_train_id` (`train_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次票价表';

-- 车次库存表
CREATE TABLE IF NOT EXISTS `train_seat_inventory` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `train_id` BIGINT NOT NULL COMMENT '车次ID',
    `travel_date` DATE NOT NULL COMMENT '乘车日期',
    `seat_type` VARCHAR(10) NOT NULL COMMENT '席位类型',
    `total_count` INT NOT NULL COMMENT '总座位数',
    `sold_count` INT DEFAULT 0 COMMENT '已售数量',
    `version` BIGINT DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_train_date_seat` (`train_id`, `travel_date`, `seat_type`),
    KEY `idx_train_id` (`train_id`),
    KEY `idx_travel_date` (`travel_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次库存表';

-- 座位锁定表
CREATE TABLE IF NOT EXISTS `seat_lock` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `train_id` BIGINT NOT NULL COMMENT '车次ID',
    `travel_date` VARCHAR(20) NOT NULL COMMENT '乘车日期',
    `seat_type` VARCHAR(10) NOT NULL COMMENT '席位类型',
    `seat_no` VARCHAR(10) NOT NULL COMMENT '座位号',
    `carriage_no` VARCHAR(5) NOT NULL COMMENT '车厢号',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `lock_minutes` INT DEFAULT 30 COMMENT '锁定分钟数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_train_date` (`train_id`, `travel_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位锁定表';

-- 订单表
CREATE TABLE IF NOT EXISTS `ticket_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `train_id` BIGINT NOT NULL COMMENT '车次ID',
    `travel_date` DATE NOT NULL COMMENT '乘车日期',
    `from_station_id` BIGINT NOT NULL COMMENT '出发站ID',
    `to_station_id` BIGINT NOT NULL COMMENT '到达站ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0待支付 1已支付 2已取消 3已退票 4已改签',
    `pay_time` VARCHAR(30) DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_train_date` (`train_id`, `travel_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单详情表
CREATE TABLE IF NOT EXISTS `ticket_order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `passenger_name` VARCHAR(20) NOT NULL COMMENT '乘车人姓名',
    `passenger_id_card` VARCHAR(20) NOT NULL COMMENT '身份证号',
    `seat_type` VARCHAR(10) NOT NULL COMMENT '席位类型',
    `carriage_no` VARCHAR(5) DEFAULT NULL COMMENT '车厢号',
    `seat_no` VARCHAR(10) DEFAULT NULL COMMENT '座位号',
    `ticket_price` DECIMAL(10,2) NOT NULL COMMENT '票价',
    `ticket_status` TINYINT DEFAULT 0 COMMENT '状态: 0正常 1已退 2已改签',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS `payment_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `payment_no` VARCHAR(32) NOT NULL COMMENT '支付单号',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `payment_method` TINYINT NOT NULL COMMENT '支付方式: 1微信 2支付宝 3银行卡',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0待支付 1已支付 2支付失败',
    `pay_time` VARCHAR(30) DEFAULT NULL COMMENT '支付时间',
    `channel_order_no` VARCHAR(64) DEFAULT NULL COMMENT '渠道订单号',
    `callback_time` VARCHAR(30) DEFAULT NULL COMMENT '回调时间',
    `callback_content` TEXT DEFAULT NULL COMMENT '回调内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 退款记录表
CREATE TABLE IF NOT EXISTS `refund_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `refund_no` VARCHAR(32) NOT NULL COMMENT '退款单号',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `payment_no` VARCHAR(32) NOT NULL COMMENT '支付单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `refund_amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `refund_type` TINYINT NOT NULL COMMENT '退款类型: 1退票 2改签',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0处理中 1已退款 2退款失败',
    `refund_time` VARCHAR(30) DEFAULT NULL COMMENT '退款时间',
    `reason` VARCHAR(255) DEFAULT NULL COMMENT '退款原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- 插入示例车站数据
INSERT INTO `station` (`station_code`, `station_name`, `city_code`, `pinyin`, `pinyin_short`, `status`) VALUES
('BJP', '北京', '010', 'beijing', 'bj', 1),
('SHH', '上海', '021', 'shanghai', 'sh', 1),
('GZH', '广州', '020', 'guangzhou', 'gz', 1),
('SZH', '深圳', '0755', 'shenzhen', 'sz', 1),
('NJH', '南京', '025', 'nanjing', 'nj', 1),
('WHN', '武汉', '027', 'wuhan', 'wh', 1),
('CDW', '成都', '028', 'chengdu', 'cd', 1),
('XAB', '西安', '029', 'xian', 'xa', 1),
('HZD', '杭州', '0571', 'hangzhou', 'hz', 1),
('TJN', '天津', '022', 'tianjin', 'tj', 1);

-- 插入示例车次数据
INSERT INTO `train` (`train_no`, `train_type`, `start_station_id`, `end_station_id`, `seat_types`, `status`) VALUES
('G1', 'G', 1, 2, '["business", "first_class", "second_class"]', 1),
('G2', 'G', 2, 1, '["business", "first_class", "second_class"]', 1),
('D1', 'D', 1, 3, '["first_class", "second_class", "soft_sleeper", "hard_sleeper"]', 1),
('K1', 'K', 1, 5, '["hard_seat", "soft_seat", "hard_sleeper", "soft_sleeper"]', 1);

-- 插入车次线路数据
INSERT INTO `train_station` (`train_id`, `station_id`, `station_seq`, `arrival_time`, `departure_time`, `distance_km`, `stop_minutes`) VALUES
(1, 1, 1, NULL, '08:00:00', 0, 0),
(1, 5, 2, '10:30:00', '10:35:00', 1024, 5),
(1, 2, 3, '12:00:00', NULL, 1320, 0),
(2, 2, 1, NULL, '14:00:00', 0, 0),
(2, 5, 2, '15:30:00', '15:35:00', 296, 5),
(2, 1, 3, '18:00:00', NULL, 1320, 0);

-- 插入车次票价数据
INSERT INTO `train_price` (`train_id`, `seat_type`, `base_price`, `price_per_km`) VALUES
(1, 'business', 1000.00, 0.50),
(1, 'first_class', 500.00, 0.30),
(1, 'second_class', 300.00, 0.20),
(2, 'business', 1000.00, 0.50),
(2, 'first_class', 500.00, 0.30),
(2, 'second_class', 300.00, 0.20);

-- 插入车次库存数据
INSERT INTO `train_seat_inventory` (`train_id`, `travel_date`, `seat_type`, `total_count`, `sold_count`, `version`) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'business', 10, 3, 0),
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'first_class', 40, 15, 0),
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'second_class', 80, 32, 0),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'business', 10, 5, 0),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'first_class', 40, 20, 0),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'second_class', 80, 45, 0);

-- 插入测试用户
INSERT INTO `user` (`username`, `password`, `phone`, `real_name`, `id_card`, `user_type`, `status`) VALUES
('testuser', '123456', '13800138000', '测试用户', '110101199001011234', 0, 1);
