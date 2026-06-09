# QueryX 使用测试指南

## 📚 快速开始

### 1. 数据库准备

首先在你的 MySQL 数据库中执行以下 SQL：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS test_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE test_db;

-- 创建用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试数据
INSERT INTO sys_user (username, email, status) VALUES 
('张三', 'zhangsan@example.com', 1),
('李四', 'lisi@example.com', 1),
('王五', 'wangwu@example.com', 0),
('张三要', 'zhangsanyao@example.com', 1);
```

### 2. 配置数据库连接

编辑 `queryx-example/src/main/resources/application.yml` 文件，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root      # 改为你的 MySQL 用户名
    password: your_password  # 改为你的 MySQL 密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. 启动应用

```bash
cd E:\QueryXs\QueryX\queryx-example
mvn spring-boot:run
```

或者运行 `QueryXDemoApplication.java` 的 main 方法。

### 4. 测试查询功能

#### 方式一：使用 Postman 或 curl

**测试精确等于查询（@Eq）**
```bash
curl -X GET "http://localhost:8080/api/users/list?username=张三"
```

**测试模糊查询（@Like）**
```bash
curl -X GET "http://localhost:8080/api/users/list?email=@example.com"
```

**测试前缀匹配（@Like with likePrefix=true）**
```bash
curl -X GET "http://localhost:8080/api/users/list?usernamePrefix=张"
```

#### 方式二：使用浏览器或 API 工具

访问以下 URL 进行测试：

1. **查询所有用户**
   ```
   GET http://localhost:8080/api/users/list
   ```

2. **根据用户名查询**
   ```
   GET http://localhost:8080/api/users/list?username=张三
   ```

3. **根据邮箱模糊查询**
   ```
   GET http://localhost:8080/api/users/list?email=@example.com
   ```

4. **根据用户名前缀查询**
   ```
   GET http://localhost:8080/api/users/list?usernamePrefix=张
   ```

## 📖 注解说明

### @Eq - 精确等于
```java
@Eq("username")
private String username;
```
生成 SQL: `WHERE username = ?`

### @Like - 模糊查询
```java
@Like("email")
private String email;
```
生成 SQL: `WHERE email LIKE '%?%'` （前后缀都匹配）

支持自定义前缀和后缀匹配：
```java
@Like(value = "username", likePrefix = true, likeSuffix = false)
private String usernamePrefix;
```
生成 SQL: `WHERE username LIKE '?%'` （只有前缀匹配）

### @In - IN 集合查询
```java
@In
private List<Long> ids;
```
生成 SQL: `WHERE id IN (?, ?, ?)`

### @Between - 范围查询
```java
@Between("create_time")
private BetweenValue createTime;
```
生成 SQL: `WHERE create_time BETWEEN ? AND ?`

## 💡 示例代码

### UserQuery.java
```java
@Data
public class UserQuery {
    
    @Eq("username")
    private String username;
    
    @Like("email")
    private String email;
    
    @Like(value = "username", likePrefix = true, likeSuffix = false)
    private String usernamePrefix;
    
    @In
    private List<Long> ids;
    
    @Between("create_time")
    private BetweenValue createTime;
}
```

### UserController.java
```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final WrapperBuilder wrapperBuilder;
    
    @GetMapping("/list")
    public List<User> listUsers(UserQuery query) {
        // 使用 QueryX 构建查询条件
        QueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return userService.list(wrapper);
    }
}
```

## 🎯 预期结果

当你访问 `http://localhost:8080/api/users/list?username=张三` 时，应该返回：

```json
[
    {
        "id": 1,
        "username": "张三",
        "email": "zhangsan@example.com",
        "status": 1,
        "createTime": "2026-06-09T10:00:00"
    }
]
```

## ❓ 常见问题

### Q: 启动时出现数据库连接错误？
A: 请检查 `application.yml` 中的数据库配置是否正确，确保 MySQL 服务正在运行。

### Q: 查询返回空结果？
A: 请确认数据库中是否有对应的测试数据，以及查询参数是否正确。

### Q: 如何查看生成的 SQL 语句？
A: 在 `application.yml` 中启用 MyBatis Plus 的 SQL 日志：
```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 📝 注意事项

1. 确保使用了正确的 Java 版本（Java 17）
2. 确保 MySQL 服务正在运行
3. 确保数据库和表已创建
4. 确保测试数据已插入

祝你使用愉快！🎉
