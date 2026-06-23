package io.github.core.queryx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限控制注解
 * 
 * <p>标注在查询 DTO 类上，自动追加数据权限条件。</p>
 * <p>配合 {@link io.github.core.queryx.support.DataPermissionProvider} 使用。</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * &#64;Data
 * &#64;DataScope(field = "department_id", type = DataScope.Type.EQ)
 * public class UserPageQuery extends BasePageQuery {
 *     &#64;Like("username")
 *     private String username;
 * }
 * </pre>
 * 
 * <h3>生成的 SQL：</h3>
 * <pre>
 * -- 普通用户（department_id = 3）
 * WHERE username LIKE '%张%' AND department_id = 3
 * 
 * -- 管理员（Provider 返回 null 则不限制）
 * WHERE username LIKE '%张%'
 * </pre>
 * 
 * @author MintNiu
 * @since 0.1.0
 * @see io.github.core.queryx.support.DataPermissionProvider
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 数据权限字段名（数据库字段）
     */
    String field();

    /**
     * 操作类型
     */
    Type type() default Type.EQ;

    /**
     * 操作类型枚举
     */
    enum Type {
        /**
         * 精确匹配：WHERE field = value
         */
        EQ,
        
        /**
         * 集合匹配：WHERE field IN (value1, value2, ...)
         */
        IN
    }
}
