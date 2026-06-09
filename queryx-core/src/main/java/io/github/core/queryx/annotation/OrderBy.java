package io.github.core.queryx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态排序注解
 * 
 * 使用示例：
 * <pre>
 * // 单字段排序
 * @OrderBy("orderBy")
 * private String orderBy;  // 接收 "id:desc"
 * 
 * // 多字段排序
 * @OrderBy("orderBy")
 * private String orderBy;  // 接收 "id:desc,age:asc,status:asc"
 * </pre>
 * 
 * 支持的格式：
 * - 字段名:排序方向（asc/desc），多个字段用逗号分隔
 * - 默认方向为 asc
 * 
 * @author MintNiu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrderBy {

    /**
     * 接收排序字符串的字段名
     * 例如：orderBy 表示接收 URL 参数中的 orderBy 值
     */
    String value() default "";
}
