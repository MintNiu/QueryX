package com.example.queryx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.queryx.UserPageQuery;
import com.example.queryx.UserQuery;
import com.example.queryx.entity.User;
import com.example.queryx.service.UserService;
import io.github.core.queryx.annotation.BetweenValue;
import io.github.core.queryx.builder.WrapperBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 用户控制器 - 演示如何使用 QueryX 进行查询
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final WrapperBuilder wrapperBuilder;

    /**
     * 根据条件查询用户列表（不分页）
     */
    @GetMapping("/list")
    public List<User> listUsers(UserQuery query) {
        // 使用 QueryX 构建查询条件
        QueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return userService.list(wrapper);
    }
    
    /**
     * 分页查询用户列表（支持动态排序）
     * 
     * 请求示例：
     * GET /api/users/page?status=1&username=张&orderBy=id:desc,age:asc&current=1&size=20
     */
    @GetMapping("/page")
    public Page<User> pageUsers(UserPageQuery query) {
        // 使用 QueryX 构建分页对象
        Page<User> page = wrapperBuilder.buildPage(query);
        
        // 使用 QueryX 构建查询条件（包含排序）
        QueryWrapper<User> wrapper = wrapperBuilder.buildPageWrapper(query);
        
        // 执行分页查询
        return userService.page(page, wrapper);
    }

    // ==================== 测试接口（验证两轮审查修复的功能增强） ====================

    /**
     * 测试 1：@Or LIKE 操作符正常生效
     * 
     * 验证：@Or(fields={"username","email"}, operator=Or.Op.LIKE) 正确生成 OR 条件
     * 
     * 请求：
     * GET /api/users/test/or-like?keyword=张
     * 
     * 预期 SQL 片段：WHERE (username LIKE '%张%' OR email LIKE '%张%')
     * 预期结果：返回匹配 username 或 email 包含 "张" 的用户列表
     */
    @GetMapping("/test/or-like")
    public List<User> testOrLike(String keyword) {
        UserPageQuery query = new UserPageQuery();
        query.setKeyword(keyword);
        QueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return userService.list(wrapper);
    }

    /**
     * 测试 2：空集合自动跳过，不生成 IN ()
     * 
     * 验证：@In 传入空 List 时自动过滤，不生成无效 SQL
     * 
     * 请求：
     * GET /api/users/test/empty-collection
     * 
     * 预期 SQL：不应包含 id IN () 或 id NOT IN ()
     * 预期结果：返回所有租户 ID 匹配的用户（不受空集合影响）
     */
    @GetMapping("/test/empty-collection")
    public List<User> testEmptyCollection() {
        UserPageQuery query = new UserPageQuery();
        query.setIds(java.util.Collections.emptyList());
        QueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return userService.list(wrapper);
    }

    /**
     * 测试 3：空字符串 LIKE 自动跳过
     * 
     * 验证：@Like 传入空字符串时自动过滤，不生成 LIKE '%%' 匹配全部行
     * 
     * 请求：
     * GET /api/users/test/empty-like?username=
     * 
     * 预期 SQL：不应包含 username LIKE
     * 预期结果：返回所有租户 ID 匹配的用户（不受空字符串影响）
     */
    @GetMapping("/test/empty-like")
    public List<User> testEmptyLike(String username) {
        UserPageQuery query = new UserPageQuery();
        query.setUsername(username);
        QueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return userService.list(wrapper);
    }

    /**
     * 测试 4：继承字段注解生效
     * 
     * 验证：BasePageQuery 的 current/size 字段正确解析
     * 
     * 请求：
     * GET /api/users/test/inherited?current=2&size=20
     * 
     * 预期输出：current=2, size=20
     */
    @GetMapping("/test/inherited")
    public String testInherited(Long current, Long size) {
        UserPageQuery query = new UserPageQuery();
        query.setCurrent(current);
        query.setSize(size);
        return "current=" + query.getCurrent() + ", size=" + query.getSize();
    }

    /**
     * 测试 5：BetweenValue 单侧 null 降级处理
     * 
     * 验证：单侧 null 时自动降级为 >= 或 <=
     * 
     * 请求：
     * GET /api/users/test/between-left-null （测试左侧 null → 应生成 <=）
     * GET /api/users/test/between-right-null （测试右侧 null → 应生成 >=）
     * 
     * 预期 SQL：应包含 <= 或 >= 而非 BETWEEN
     * 预期结果：返回 create_time <= 当前时间的用户
     */
    @GetMapping("/test/between-left-null")
    public List<User> testBetweenLeftNull() {
        UserQuery query = new UserQuery();
        query.setCreateTime(new BetweenValue(null, new Date()));
        QueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return userService.list(wrapper);
    }

    /**
     * 测试 6：多租户字段与用户条件共存，不重复
     * 
     * 验证：租户条件与用户手动指定的同名字段去重
     * 
     * 请求：
     * GET /api/users/test/tenant-dedup?status=1
     * 
     * 预期 SQL：应包含 status = 1 AND tenant_id = 1（去重后不重复）
     * 预期结果：返回 status=1 且 tenant_id=1 的用户
     */
    @GetMapping("/test/tenant-dedup")
    public List<User> testTenantDedup(Integer status) {
        UserPageQuery query = new UserPageQuery();
        query.setStatus(status);
        QueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return userService.list(wrapper);
    }

    /**
     * 测试 7：buildPageWrapper 单次解析验证（性能优化）
     * 
     * 验证：buildPageWrapper() 只调用一次 parse()，复用元数据
     * 
     * 请求：
     * GET /api/users/test/single-parse?username=张&orderBy=id:desc
     * 
     * 预期 SQL：应同时包含 username LIKE 和 ORDER BY id DESC
     * 预期结果：返回按 ID 降序排列、username 包含 "张" 的用户列表
     */
    @GetMapping("/test/single-parse")
    public List<User> testSingleParse(String username, String orderBy) {
        UserPageQuery query = new UserPageQuery();
        query.setUsername(username);
        query.setOrderBy(orderBy);
        QueryWrapper<User> wrapper = wrapperBuilder.buildPageWrapper(query);
        return userService.list(wrapper);
    }
}
