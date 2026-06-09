package io.github.core.queryx.metadata;

/**
 * 查询操作符枚举
 * 
 * @author MintNiu
 */
public enum QueryOperator {

    EQ,
    NE,         // 不等于
    LIKE,
    NOT_LIKE,   // NOT LIKE
    IN,
    NOT_IN,     // NOT IN
    BETWEEN,
    GT,         // 大于 >
    LT,         // 小于 <
    GE,         // 大于等于 >=
    LE          // 小于等于 <=
}
