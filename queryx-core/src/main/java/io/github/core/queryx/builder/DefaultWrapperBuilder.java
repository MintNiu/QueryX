package io.github.core.queryx.builder;

import io.github.core.queryx.annotation.BetweenValue;
import io.github.core.queryx.metadata.QueryFieldMetadata;
import io.github.core.queryx.metadata.QueryOperator;
import io.github.core.queryx.parser.QueryParser;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Collection;
import java.util.List;

/**
 * 默认的 Wrapper 构建器实现
 * 
 * @author MintNiu
 */
public class DefaultWrapperBuilder implements WrapperBuilder {

    private final QueryParser queryParser;

    public DefaultWrapperBuilder(QueryParser queryParser) {
        this.queryParser = queryParser;
    }

    @Override
    public <T> QueryWrapper<T> build(Object query) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (query == null) {
            return wrapper;
        }

        List<QueryFieldMetadata> metadataList = queryParser.parse(query);
        for (QueryFieldMetadata metadata : metadataList) {
            applyCondition(wrapper, metadata);
        }

        return wrapper;
    }

    private <T> void applyCondition(QueryWrapper<T> wrapper, QueryFieldMetadata metadata) {
        String fieldName = metadata.getFieldName();
        Object value = metadata.getValue();
        QueryOperator operator = metadata.getOperator();

        switch (operator) {
            case EQ:
                wrapper.eq(fieldName, value);
                break;
            case NE:
                wrapper.ne(fieldName, value);
                break;
            case GT:
                wrapper.gt(fieldName, value);
                break;
            case LT:
                wrapper.lt(fieldName, value);
                break;
            case GE:
                wrapper.ge(fieldName, value);
                break;
            case LE:
                wrapper.le(fieldName, value);
                break;
            case LIKE:
                applyLikeCondition(wrapper, fieldName, value, metadata);
                break;
            case NOT_LIKE:
                applyNotLikeCondition(wrapper, fieldName, value, metadata);
                break;
            case IN:
                if (value instanceof Collection) {
                    wrapper.in(fieldName, (Collection<?>) value);
                }
                break;
            case NOT_IN:
                if (value instanceof Collection) {
                    wrapper.notIn(fieldName, (Collection<?>) value);
                }
                break;
            case BETWEEN:
                if (value instanceof BetweenValue) {
                    BetweenValue betweenValue = (BetweenValue) value;
                    wrapper.between(fieldName, betweenValue.getLeft(), betweenValue.getRight());
                }
                break;
            default:
                wrapper.eq(fieldName, value);
        }
    }

    private <T> void applyLikeCondition(QueryWrapper<T> wrapper, String fieldName, 
                                         Object value, QueryFieldMetadata metadata) {
        String strValue = value.toString();
        boolean likePrefix = metadata.isLikePrefix();
        boolean likeSuffix = metadata.isLikeSuffix();

        // 优先判断最常见情况：前后都加 %（默认配置）
        if (likePrefix && likeSuffix) {
            // 前后都加 %：like(%value%) → 模糊匹配（最常用）
            wrapper.like(fieldName, strValue);
        } else if (likePrefix) {
            // 只加前缀 %：likeRight(value%) → 匹配以 value 开头的
            wrapper.likeRight(fieldName, strValue);
        } else if (likeSuffix) {
            // 只加后缀 %：likeLeft(%value) → 匹配以 value 结尾的
            wrapper.likeLeft(fieldName, strValue);
        } else {
            // 前后都不加 %：精确匹配（很少用）
            wrapper.like(fieldName, strValue);
        }
    }
    
    private <T> void applyNotLikeCondition(QueryWrapper<T> wrapper, String fieldName, 
                                            Object value, QueryFieldMetadata metadata) {
        String strValue = value.toString();
        boolean likePrefix = metadata.isLikePrefix();
        boolean likeSuffix = metadata.isLikeSuffix();

        // 优先判断最常见情况：前后都加 %（默认配置）
        if (likePrefix && likeSuffix) {
            // 前后都加 %：notLike(%value%) → 模糊不匹配
            wrapper.notLike(fieldName, strValue);
        } else if (likePrefix) {
            // 只加前缀 %：notLike(value%) → 不以 value 开头
            wrapper.notLikeRight(fieldName, strValue);
        } else if (likeSuffix) {
            // 只加后缀 %：notLike(%value) → 不以 value 结尾
            wrapper.notLikeLeft(fieldName, strValue);
        } else {
            // 前后都不加 %：精确不匹配
            wrapper.notLike(fieldName, strValue);
        }
    }
}
