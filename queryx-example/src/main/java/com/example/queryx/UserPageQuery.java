package com.example.queryx;

import io.github.core.queryx.annotation.Between;
import io.github.core.queryx.annotation.BetweenValue;
import io.github.core.queryx.annotation.Eq;
import io.github.core.queryx.annotation.In;
import io.github.core.queryx.annotation.Like;
import io.github.core.queryx.annotation.Or;
import io.github.core.queryx.annotation.OrderBy;
import io.github.core.queryx.support.BasePageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户分页查询 DTO - 演示分页和动态排序功能
 * 
 * 使用示例：
 * GET /api/users/page?status=1&username=张&orderBy=id:desc,age:asc&current=1&size=20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends BasePageQuery {
    
    /**
     * 动态排序字段
     * 格式：字段名:排序方向（asc/desc），多个字段用逗号分隔
     * 示例：
     * - "id:desc" - 按 ID 降序
     * - "id:desc,age:asc" - 先按 ID 降序，再按年龄升序
     * - "status:asc,create_time:desc" - 多字段排序
     */
    @OrderBy
    private String orderBy;
    
    /**
     * 精确等于查询 - 用户名
     */
    @Like("username")
    private String username;
    
    /**
     * 不等于查询 - 状态
     */
    @Eq(value = "status", not = true)
    private Integer excludeStatus;
    
    /**
     * 模糊查询 - 邮箱
     */
    @Like("email")
    private String email;
    
    /**
     * NOT LIKE 查询 - 用户名
     */
    @Like(value = "username", not = true)
    private String excludeUsername;
    
    /**
     * IN 查询 - ID列表
     */
    @In
    private List<Long> ids;
    
    /**
     * NOT IN 查询 - 排除的ID列表
     */
    @In(value = "id", not = true)
    private List<Long> excludeIds;
    
    /**
     * 等于查询 - 状态
     */
    @Eq("status")
    private Integer status;
    
    /**
     * 大于查询 - 年龄
     */
    @Eq(value = "age", op = Eq.Op.GT)
    private Integer minAge;
    
    /**
     * 小于等于查询 - 年龄
     */
    @Eq(value = "age", op = Eq.Op.LE)
    private Integer maxAge;
    
    /**
     * 范围查询 - 创建时间
     */
    @Between("create_time")
    private BetweenValue createTime;
    
    /**
     * OR 组合查询 - 关键词搜索
     * 同时匹配 username 和 email 字段
     * 生成 SQL：AND (username LIKE '%keyword%' OR email LIKE '%keyword%')
     */
    @Or(fields = {"username", "email"}, operator = Or.Op.LIKE)
    private String keyword;
}
