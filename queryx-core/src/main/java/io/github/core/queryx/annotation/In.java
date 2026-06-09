package io.github.core.queryx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IN 查询注解
 * 
 * @author MintNiu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface In {

    /**
     * 数据库字段名，为空时使用属性名
     */
    String value() default "";
    
    /**
     * 是否取反（NOT IN）
     */
    boolean not() default false;
}
