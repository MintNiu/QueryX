package io.github.core.queryx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 精确等于查询注解
 * 
 * @author MintNiu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Eq {

    /**
     * 数据库字段名
     */
    String value() default "";
    
    /**
     * 是否取反（不等于）
     */
    boolean not() default false;
    
    /**
     * 比较运算符（优先级高于 not 属性）
     * 当设置此属性时，not 属性将被忽略
     */
    Op op() default Op.EQ;
    
    /**
     * 比较运算符枚举
     */
    enum Op {
        /** 等于 = */
        EQ,
        /** 大于 > */
        GT,
        /** 小于 < */
        LT,
        /** 大于等于 >= */
        GE,
        /** 小于等于 <= */
        LE
    }
}
