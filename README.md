# QueryX

QueryX 是一个基于 MyBatis Plus 的查询增强框架。通过 @Eq、@Like、@In、@Between 等注解，可以直接在 Query DTO 中定义查询规则，自动生成 QueryWrapper，减少大量重复的条件拼装代码。

目标是让开发者专注于业务，而不是编写重复的查询逻辑。

```
UserQuery
      ↓
   QueryX
      ↓
LambdaQueryWrapper
      ↓
   SQL
```

**Write DTO, not Wrapper.**

QueryX is an annotation-driven query enhancement framework for MyBatis Plus.

It automatically converts Query DTOs into LambdaQueryWrapper objects, helping developers reduce repetitive query-building code.

## Quick Start

### 1. 引入依赖

```xml
<dependency>
    <groupId>io.github.MintNiu</groupId>
    <artifactId>queryx-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### 2. 定义 Query DTO

```java
@Data
public class UserQuery {
    @Eq("username")
    private String username;
    
    @Like("email")
    private String email;
    
    @In
    private List<Long> ids;
}
```

### 3. 使用 WrapperBuilder

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final WrapperBuilder wrapperBuilder;
    
    @GetMapping("/list")
    public List<User> listUsers(UserQuery query) {
        QueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return userService.list(wrapper);
    }
}
```

### 4. 分页查询（新功能）⭐

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends BasePageQuery {
    
    @OrderBy
    private String orderBy;  // "id:desc,age:asc"
    
    @Like("username")
    private String username;
    
    @Eq("status")
    private Integer status;
}
```

```java
@GetMapping("/page")
public Page<User> pageUsers(UserPageQuery query) {
    // 构建分页对象
    Page<User> page = wrapperBuilder.buildPage(query);
    
    // 构建查询条件（包含排序）
    QueryWrapper<User> wrapper = wrapperBuilder.buildPageWrapper(query);
    
    // 执行分页查询
    return userService.page(page, wrapper);
}
```

**请求示例**：
```bash
# 基础分页（默认第1页，每页10条）
GET http://localhost:8080/api/users/page

# 指定分页参数
GET http://localhost:8080/api/users/page?current=2&size=20

# 单字段排序
GET http://localhost:8080/api/users/page?orderBy=id:desc

# 多字段排序
GET http://localhost:8080/api/users/page?orderBy=status:asc,id:desc

# 组合查询 + 分页 + 排序
GET http://localhost:8080/api/users/page?username=张&status=1&orderBy=id:desc&current=1&size=10
```

### 5. 测试接口

```bash
# 查询所有用户
GET http://localhost:8080/api/users/list

# 条件查询
GET http://localhost:8080/api/users/list?username=张三&email=@example.com
```

## Example

### Before

```java
LambdaQueryWrapper<User> wrapper =
    Wrappers.lambdaQuery();

if (StringUtils.isNotBlank(query.getUsername())) {
    wrapper.like(User::getUsername, query.getUsername());
}

if (query.getStatus() != null) {
    wrapper.eq(User::getStatus, query.getStatus());
}
```

### After

```java
public class UserQuery {

    @Like
    private String username;

    @Eq
    private Integer status;
}
```

```java
LambdaQueryWrapper<User> wrapper =
    queryExecutor.wrapper(query);
```

### 高级用法 - NOT 条件

```java
public class UserQuery {
    
    // 不等于：WHERE status != 0
    @Eq(value = "status", not = true)
    private Integer excludeStatus;
    
    // NOT LIKE：WHERE username NOT LIKE '%张%'
    @Like(value = "username", not = true)
    private String excludeUsername;
    
    // NOT IN：WHERE id NOT IN (1, 2, 3)
    @In(value = "id", not = true)
    private List<Long> excludeIds;
}
```

### LIKE 查询配置

```java
public class UserQuery {
    
    // 默认：LIKE '%value%'（前后匹配）
    @Like("email")
    private String email;
    
    // 前缀匹配：LIKE 'value%'
    @Like(value = "username", likePrefix = true, likeSuffix = false)
    private String usernamePrefix;
    
    // 后缀匹配：LIKE '%value'
    @Like(value = "username", likePrefix = false, likeSuffix = true)
    private String usernameSuffix;
}
```

### 比较运算符

```java
public class UserQuery {
    
    // 大于：WHERE age > 18
    @Eq(value = "age", op = Eq.Op.GT)
    private Integer minAge;
    
    // 小于：WHERE age < 60
    @Eq(value = "age", op = Eq.Op.LT)
    private Integer maxAge;
    
    // 大于等于：WHERE score >= 60
    @Eq(value = "score", op = Eq.Op.GE)
    private Integer minScore;
    
    // 小于等于：WHERE score <= 100
    @Eq(value = "score", op = Eq.Op.LE)
    private Integer maxScore;
}
```

**支持的运算符**：

| 运算符 | 枚举值 | SQL 生成 | 说明 |
|--------|--------|---------|------|
| `=` | `Eq.Op.EQ` | `WHERE status = 1` | 等于（默认） |
| `!=` | `not = true` | `WHERE status != 1` | 不等于 |
| `>` | `Eq.Op.GT` | `WHERE age > 18` | 大于 |
| `<` | `Eq.Op.LT` | `WHERE age < 60` | 小于 |
| `>=` | `Eq.Op.GE` | `WHERE age >= 18` | 大于等于 |
| `<=` | `Eq.Op.LE` | `WHERE age <= 60` | 小于等于 |

### 分页查询和动态排序（新功能）⭐

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends BasePageQuery {
    
    // 动态排序：支持多字段排序
    @OrderBy
    private String orderBy;  // 格式："id:desc,age:asc"
    
    @Like("username")
    private String username;
    
    @Eq("status")
    private Integer status;
}
```

**排序格式**：`字段名:排序方向`，多个字段用逗号分隔
- `id:desc` - 按 ID 降序
- `id:desc,age:asc` - 先按 ID 降序，再按年龄升序
- `status:asc,create_time:desc` - 多字段排序

**BasePageQuery 基类**：
```java
public abstract class BasePageQuery implements Serializable {
    private Long current = 1L;      // 当前页码，默认 1
    private Long size = 10L;        // 每页显示数量，默认 10
}
```

## Features

### 核心功能
* ✅ `@Eq` - 精确等于查询（支持 `not` 取反、比较运算符）
* ✅ `@Like` - 模糊查询（支持前后缀匹配、`not` 取反）
* ✅ `@In` - 集合查询（支持 `not` 取反）
* ✅ `@Between` - 范围查询
* ✅ `@OrderBy` - 动态排序（支持多字段排序）
* ✅ `BasePageQuery` - 分页查询基类
* ✅ `@Ne` - 不等于查询（通过 `@Eq(not = true)` 实现）
* ✅ `@NotLike` - NOT LIKE 查询（通过 `@Like(not = true)` 实现）
* ✅ `@NotIn` - NOT IN 查询（通过 `@In(not = true)` 实现）
* ✅ **比较运算符** - 大于/小于/大于等于/小于等于（通过 `@Eq(op = Eq.Op.GT)` 等实现）

### 技术特性
* ✅ Spring Boot Starter 自动配置
* ✅ MyBatis Plus 3.5.5 集成
* ✅ 基于反射的查询解析器
* ✅ 注解驱动的查询条件生成
* ✅ 支持复杂条件组合
* ✅ 灵活的 LIKE 匹配模式（前缀、后缀、前后）
* ✅ **分页查询支持**（BasePageQuery 基类）
* ✅ **动态排序支持**（多字段排序：`field:desc,field2:asc`）

### 版本信息
* **Spring Boot**: 3.2.5
* **MyBatis Plus**: 3.5.5
* **MyBatis Spring**: 3.0.3
* **Java**: 17

## Roadmap

### 已完成 ✅
* [x] Query DTO -> Wrapper 转换
* [x] @Eq 精确等于查询
* [x] @Like 模糊查询（支持前后缀配置）
* [x] @In 集合查询
* [x] @Between 范围查询
* [x] NOT 条件支持（@Eq/@Like/@In 的 not 属性）
* [x] 比较运算符支持（GT/LT/GE/LE）
* [x] **分页查询支持**（BasePageQuery 基类）
* [x] **动态排序支持**（@OrderBy 注解，多字段排序）
* [x] Spring Boot Starter 自动配置
* [x] 完整的测试示例项目
* [x] MyBatis Plus 分页插件配置

### 开发中 🚧
* [ ] 分页参数校验和限制
* [ ] 排序字段白名单机制

### 计划中 📋
* [ ] 多表 Join 查询
* [ ] 数据权限控制
* [ ] 多租户支持
* [ ] Kotlin DSL
* [ ] 查询缓存优化
* [ ] 复杂条件嵌套（OR/AND 组合）

## Project Structure

```
queryx/
├── queryx-core/                          # 核心模块
│   ├── annotation/                       # 查询注解
│   │   ├── @Eq                           # 精确等于（支持 not 取反、比较运算符 GT/LT/GE/LE）
│   │   ├── @Like                         # 模糊查询（支持前后缀、not 取反）
│   │   ├── @In                           # 集合查询（支持 not 取反）
│   │   ├── @Between                      # 范围查询
│   │   └── @OrderBy                      # 动态排序（多字段排序）
│   ├── builder/                          # Wrapper 构建器
│   │   ├── WrapperBuilder                # 接口定义
│   │   └── DefaultWrapperBuilder         # 默认实现
│   ├── parser/                           # 查询解析器
│   ├── metadata/                         # 元数据定义
│   └── support/                          # 支持类
│       └── BasePageQuery                 # 分页查询基类
├── queryx-spring-boot-autoconfigure/     # Spring Boot 自动配置
├── queryx-spring-boot-starter/           # Starter 依赖
└── queryx-example/                       # 示例项目
    ├── config/
    │   └── MybatisPlusConfig             # MyBatis Plus 配置（分页插件）
    └── controller/
        └── UserController                # 示例控制器
```

