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
            case LIKE:
                applyLikeCondition(wrapper, fieldName, value, metadata);
                break;
            case IN:
                if (value instanceof Collection) {
                    wrapper.in(fieldName, (Collection<?>) value);
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

        if (!likePrefix && !likeSuffix) {
            wrapper.like(fieldName, strValue);
        } else if (likePrefix && !likeSuffix) {
            wrapper.likeRight(fieldName, strValue);
        } else if (!likePrefix && likeSuffix) {
            wrapper.notLike(fieldName, strValue);
        } else {
            wrapper.notLike(fieldName, strValue);
        }
    }
}
