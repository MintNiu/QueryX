package com.example.queryx;

import io.github.core.queryx.annotation.Between;
import io.github.core.queryx.annotation.BetweenValue;
import io.github.core.queryx.annotation.Eq;
import io.github.core.queryx.annotation.In;
import io.github.core.queryx.annotation.Like;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 用户查询 DTO - 演示所有支持的注解
 */
@Data
public class UserQuery {
    
    /**
     * 精确等于查询 - 用户名
     * @Eq 注解会生成 SQL: WHERE username = '张三'
     */
    @Eq("username")
    private String username;
    
    /**
     * 不等于查询 - 状态
     * @Eq(not = true) 会生成 SQL: WHERE status != 1
     */
    @Eq(value = "status", not = true)
    private Integer excludeStatus;
    
    /**
     * 模糊查询 - 邮箱
     * @Like 注解默认前后缀匹配，生成 SQL: WHERE email LIKE '%test%'
     */
    @Like("email")
    private String email;
    
    /**
     * NOT LIKE 查询 - 用户名
     * @Like(not = true) 会生成 SQL: WHERE username NOT LIKE '%张%'
     */
    @Like(value = "username", not = true)
    private String excludeUsername;
    
    /**
     * 前缀匹配 - 用户名
     * @Like(value = "username", likePrefix = true, likeSuffix = false) 
     * 生成 SQL: WHERE username LIKE '张%'
     */
    @Like(value = "username", likePrefix = true, likeSuffix = false)
    private String usernamePrefix;
    
    /**
     * IN 查询 - ID列表
     * @In 注解会生成 SQL: WHERE id IN (1, 2, 3)
     */
    @In
    private List<Long> ids;
    
    /**
     * NOT IN 查询 - 排除的ID列表
     * @In(value = "id", not = true) 会生成 SQL: WHERE id NOT IN (1, 2, 3)
     */
    @In(value = "id", not = true)
    private List<Long> excludeIds;
    
    /**
     * 等于查询 - 状态
     * @Eq 注解会生成 SQL: WHERE status = 1
     */
    @Eq("status")
    private Integer status;
    
    /**
     * 大于查询 - 年龄
     * @Eq(op = Eq.Op.GT) 会生成 SQL: WHERE age > 18
     */
    @Eq(value = "age", op = Eq.Op.GT)
    private Integer minAge;
    
    /**
     * 小于等于查询 - 年龄
     * @Eq(op = Eq.Op.LE) 会生成 SQL: WHERE age <= 60
     */
    @Eq(value = "age", op = Eq.Op.LE)
    private Integer maxAge;
    
    /**
     * 范围查询 - 创建时间
     * @Between 注解会生成 SQL: WHERE create_time BETWEEN ? AND ?
     */
    @Between("create_time")
    private BetweenValue createTime;
}
