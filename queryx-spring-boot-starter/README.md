# QueryX Spring Boot Starter

QueryX Spring Boot Starter 是基于 MyBatis Plus 的查询增强框架，通过注解驱动的方式自动生成查询条件，减少重复的代码编写。

## 快速开始

### 1. 引入依赖

在您的 Spring Boot 项目中添加以下依赖：

```xml
<dependency>
    <groupId>io.github.MintNiu</groupId>
    <artifactId>queryx-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### 2. 定义 Query DTO

使用 QueryX 注解定义查询条件：

#### 基本示例

```java
import io.github.core.queryx.annotation.Eq;
import io.github.core.queryx.annotation.Like;
import lombok.Data;

@Data
public class UserQuery {
    
    @Eq("username")
    private String username;
    
    @Like("email")
    private String email;
    
    @Eq
    private Integer status;
}
```

#### 完整示例（包含所有注解）

```java
import io.github.core.queryx.annotation.Between;
import io.github.core.queryx.annotation.BetweenValue;
import io.github.core.queryx.annotation.Eq;
import io.github.core.queryx.annotation.In;
import io.github.core.queryx.annotation.Like;
import lombok.Data;

import java.util.Date;
import java.util.Arrays;

@Data
public class UserQuery {
    
    // 精确等于查询：WHERE status = 1
    @Eq
    private Integer status;
    
    // 指定字段名：WHERE username LIKE '%张三%'
    @Like("username")
    private String username;
    
    // 前缀匹配：WHERE email LIKE 'test@%'
    @Like(value = "email", likePrefix = true, likeSuffix = false)
    private String email;
    
    // 后缀匹配：WHERE phone LIKE '138%'
    @Like(value = "phone", likePrefix = false, likeSuffix = true)
    private String phone;
    
    // IN 查询：WHERE id IN (1, 2, 3)
    @In
    private List<Long> ids;
    
    // BETWEEN 查询：WHERE create_time BETWEEN '2024-01-01' AND '2024-12-31'
    @Between("create_time")
    private BetweenValue createTime;
}
```

#### 使用示例

```java
UserQuery query = new UserQuery();
query.setStatus(1);
query.setUsername("张三");
query.setIds(Arrays.asList(1L, 2L, 3L));
query.setCreateTime(new BetweenValue(new Date("2024-01-01"), new Date("2024-12-31")));
```

### 3. 在 Service 中使用

```java
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.core.queryx.builder.WrapperBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    private final WrapperBuilder wrapperBuilder;

    public UserService(WrapperBuilder wrapperBuilder) {
        this.wrapperBuilder = wrapperBuilder;
    }

    public List<User> searchUsers(UserQuery query) {
        LambdaQueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return list(wrapper);
    }
}
```

## 支持的注解

- `@Eq`: 等于（=）
- `@Like`: 模糊查询（LIKE）
- `@In`: IN 查询
- `@Between`: BETWEEN 查询

## 工作原理

1. **自动配置**: Spring Boot 启动时自动注册 QueryX 核心组件
2. **注解解析**: 通过反射解析 Query DTO 上的注解
3. **Wrapper 生成**: 自动生成 MyBatis Plus 的 QueryWrapper
4. **条件应用**: 将注解定义的条件应用到 Wrapper 中

## 特性

- ✅ 零配置开箱即用
- ✅ 减少重复的条件拼装代码
- ✅ 与 Spring Boot 完美集成
- ✅ 支持自定义查询操作符
- ✅ 类型安全

## 配置选项

可选配置（application.yml）：

```yaml
queryx:
  enabled: true  # 是否启用 QueryX，默认为 true
```

## 技术栈

- Java 17+
- Spring Boot 3.5.x
- MyBatis Plus 3.5.x

## 项目结构

```
queryx/
├── queryx-core              # 核心模块，包含基础注解和解析逻辑
├── queryx-spring-boot-autoconfigure  # Spring Boot 自动配置模块
├── queryx-spring-boot-starter  # Spring Boot Starter 模块
└── queryx-example               # 示例模块
```

## 开源协议

MIT License
