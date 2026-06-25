-- ============================================
-- QueryX 测试数据库脚本
-- ============================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS test_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE test_db;

-- 2. 创建用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    age INT COMMENT '年龄',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_id INT COMMENT '租户Id',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 插入测试数据
INSERT INTO sys_user (username, email, age, status) VALUES 
('张三', 'zhangsan@example.com', 25, 1,1),
('李四', 'lisi@example.com', 30, 1,1),
('王五', 'wangwu@example.com', 45, 0,1),
('张三要', 'zhangsanyao@example.com', 18, 1,2),
('赵六', 'zhaoliu@example.com', 35, 1,2);

-- 4. 查询验证
SELECT * FROM sys_user;
