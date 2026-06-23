package io.github.core.queryx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OR 组合查询注解
 * 
 * <p>将同一个字段值应用到多个字段的 OR 组合中，与其他条件保持 AND 关系。</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 搜索关键词同时匹配 username 和 email
 * // 生成：AND (username LIKE '%张%' OR email LIKE '%张%')
 * &#64;Or(fields = {"username", "email"}, operator = Or.Op.LIKE)
 * private String keyword;
 * </pre>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Or {

    /**
     * 参与 OR 组合的数据库字段名数组
     */
    String[] fields();

    /**
     * 操作符类型
     */
    Op operator();

    /**
     * 操作符枚举
     */
    enum Op {
        /** 精确等于 */
        EQ,
        /** 模糊匹配 */
        LIKE,
        /** IN 查询 */
        IN
    }
}
