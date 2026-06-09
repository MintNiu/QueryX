package io.github.core.queryx.builder;

import io.github.core.queryx.annotation.BetweenValue;
import io.github.core.queryx.metadata.QueryFieldMetadata;
import io.github.core.queryx.metadata.QueryOperator;
import io.github.core.queryx.parser.QueryParser;
import io.github.core.queryx.support.BasePageQuery;
import io.github.core.queryx.validator.OrderByResult;
import io.github.core.queryx.validator.OrderByValidator;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 默认的 Wrapper 构建器实现
 * 
 * @author MintNiu
 */
@Slf4j
public class DefaultWrapperBuilder implements WrapperBuilder {

    private final QueryParser queryParser;
    
    /**
     * 排序字段白名单验证器（可选）
     */
    private OrderByValidator orderByValidator;

    public DefaultWrapperBuilder(QueryParser queryParser) {
        this.queryParser = queryParser;
    }
    
    public DefaultWrapperBuilder(QueryParser queryParser, String... allowedOrderFields) {
        this.queryParser = queryParser;
        if (allowedOrderFields != null && allowedOrderFields.length > 0) {
            this.orderByValidator = new OrderByValidator(allowedOrderFields);
        }
    }
    
    public DefaultWrapperBuilder(QueryParser queryParser, Set<String> allowedOrderFields) {
        this.queryParser = queryParser;
        if (allowedOrderFields != null && !allowedOrderFields.isEmpty()) {
            this.orderByValidator = new OrderByValidator(allowedOrderFields);
        }
    }

    @Override
    public <T> QueryWrapper<T> build(Object query) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (query == null) {
            return wrapper;
        }

        List<QueryFieldMetadata> metadataList = queryParser.parse(query);
        for (QueryFieldMetadata metadata : metadataList) {
            // 跳过排序字段（不是查询条件）
            if (metadata.getOperator() == QueryOperator.ORDER_BY) {
                continue;
            }
            applyCondition(wrapper, metadata);
        }

        return wrapper;
    }
    
    @Override
    public <T> Page<T> buildPage(Object query) {
        if (!(query instanceof BasePageQuery)) {
            throw new IllegalArgumentException("Query object must extend BasePageQuery for pagination");
        }
        
        BasePageQuery pageQuery = (BasePageQuery) query;
        return new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
    }
    
    @Override
    public <T> QueryWrapper<T> buildPageWrapper(Object query) {
        // 构建查询条件
        QueryWrapper<T> wrapper = build(query);
        
        // 应用排序到 QueryWrapper
        List<QueryFieldMetadata> metadataList = queryParser.parse(query);
        for (QueryFieldMetadata metadata : metadataList) {
            if (metadata.getOperator() == QueryOperator.ORDER_BY) {
                String orderByStr = (String) metadata.getValue();
                applyOrder(wrapper, orderByStr);
                break; // 只处理第一个 @OrderBy 字段
            }
        }
        
        return wrapper;
    }
    
    @Override
    public <T> void applyOrder(QueryWrapper<T> wrapper, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return;
        }
        
        // 白名单验证和过滤
        String filteredOrderBy = orderBy;
        if (orderByValidator != null) {
            OrderByResult result = orderByValidator.validateAndFilter(orderBy);
            filteredOrderBy = result.getFilteredOrderBy();
            
            // 输出警告信息
            if (result.hasWarnings()) {
                result.getWarnings().forEach(warn -> log.warn("[QueryX] {}", warn));
            }
        }
        
        // 如果过滤后没有合法的排序字段，直接返回
        if (filteredOrderBy == null || filteredOrderBy.trim().isEmpty()) {
            return;
        }
        
        // 解析排序字符串："id:desc,age:asc"
        String[] orderFields = filteredOrderBy.split(",");
        for (String orderField : orderFields) {
            String[] parts = orderField.trim().split(":");
            String fieldName = parts[0].trim();
            
            if (fieldName.isEmpty()) {
                continue;
            }
            
            // 默认 asc
            String direction = parts.length > 1 ? parts[1].trim().toLowerCase() : "asc";
            
            if ("desc".equals(direction)) {
                wrapper.orderByDesc(fieldName);
            } else {
                wrapper.orderByAsc(fieldName);
            }
        }
    }
    
    /**
     * 设置排序字段白名单验证器
     * 
     * @param validator 验证器
     */
    public void setOrderByValidator(OrderByValidator validator) {
        this.orderByValidator = validator;
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
