package io.github.core.queryx.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.core.queryx.annotation.BetweenValue;
import io.github.core.queryx.metadata.QueryFieldMetadata;
import io.github.core.queryx.metadata.QueryOperator;
import io.github.core.queryx.parser.QueryParser;
import io.github.core.queryx.support.BasePageQuery;
import io.github.core.queryx.validator.OrderByResult;
import io.github.core.queryx.validator.OrderByValidator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 默认的 Wrapper 构建器实现
 *
 * <p>核心功能：</p>
 * <ul>
 *   <li>将查询 DTO 对象解析为 MyBatis Plus 的 {@link QueryWrapper}</li>
 *   <li>支持分页对象构建，并自动限制每页数量不超过 {@link #maxPageSize}</li>
 *   <li>支持多字段动态排序，可选配合 {@link OrderByValidator} 进行白名单验证</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * // 1. 构建查询条件
 * QueryWrapper&lt;User&gt; wrapper = wrapperBuilder.build(query);
 *
 * // 2. 构建分页对象（自动限制每页数量）
 * Page&lt;User&gt; page = wrapperBuilder.buildPage(query);
 *
 * // 3. 构建包含排序的查询 Wrapper（用于分页查询）
 * QueryWrapper&lt;User&gt; pageWrapper = wrapperBuilder.buildPageWrapper(query);
 *
 * // 4. 执行分页查询
 * userService.page(page, pageWrapper);
 * </pre>
 *
 * @author MintNiu
 * @since 0.1.0
 */
@Slf4j
public class DefaultWrapperBuilder implements WrapperBuilder {

    private final QueryParser queryParser;

    /**
     * 排序字段白名单验证器（可选）
     * -- SETTER --
     * 设置排序字段白名单验证器
     *
     * @param validator 验证器
     */
    @Setter
    private OrderByValidator orderByValidator;

    /**
     * 分页最大每页数量（默认 500）
     * <p>超过此值的每页数量将被自动调整，并输出警告日志。</p>
     * <p>可通过 {@link #setMaxPageSize(Long)} 或配置 {@code queryx.maxPageSize} 修改。</p>
     */
    private Long maxPageSize = 500L;

    /**
     * 基础构造函数
     *
     * @param queryParser 查询解析器
     */
    public DefaultWrapperBuilder(QueryParser queryParser) {
        this.queryParser = queryParser;
    }

    /**
     * 带白名单数组的构造函数
     *
     * @param queryParser        查询解析器
     * @param allowedOrderFields 允许的排序字段数组
     */
    public DefaultWrapperBuilder(QueryParser queryParser, String... allowedOrderFields) {
        this.queryParser = queryParser;
        if (allowedOrderFields != null && allowedOrderFields.length > 0) {
            this.orderByValidator = new OrderByValidator(allowedOrderFields);
        }
    }

    /**
     * 带白名单集合的构造函数
     *
     * @param queryParser        查询解析器
     * @param allowedOrderFields 允许的排序字段集合
     */
    public DefaultWrapperBuilder(QueryParser queryParser, Set<String> allowedOrderFields) {
        this.queryParser = queryParser;
        if (allowedOrderFields != null && !allowedOrderFields.isEmpty()) {
            this.orderByValidator = new OrderByValidator(allowedOrderFields);
        }
    }

    /**
     * 构建查询条件 Wrapper
     *
     * <p>解析查询对象上的注解，生成对应的 WHERE 条件。</p>
     * <p>注意：@OrderBy 注解的字段会被跳过，不会作为查询条件。</p>
     *
     * @param query 查询对象
     * @return MyBatis Plus QueryWrapper
     */
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
            // 处理 OR 组合查询
            if (metadata.getOperator() == QueryOperator.OR_GROUP) {
                applyOrGroupCondition(wrapper, metadata);
            } else {
                applyCondition(wrapper, metadata);
            }
        }

        return wrapper;
    }

    /**
     * 构建分页对象
     *
     * <p>要求查询对象必须继承 {@link BasePageQuery}。</p>
     * <p>自动限制每页数量不超过 {@link #maxPageSize}，超出部分将被调整并输出警告日志。</p>
     *
     * @param query 查询对象（必须继承 BasePageQuery）
     * @return MyBatis Plus Page 对象
     * @throws IllegalArgumentException 如果查询对象未继承 BasePageQuery
     */
    @Override
    public <T> Page<T> buildPage(Object query) {
        if (!(query instanceof BasePageQuery)) {
            throw new IllegalArgumentException("Query object must extend BasePageQuery for pagination");
        }

        BasePageQuery pageQuery = (BasePageQuery) query;

        // 使用配置的 maxPageSize 限制每页数量
        long size = pageQuery.getSize();
        if (size > maxPageSize) {
            log.warn("[QueryX] 每页数量 {} 超过最大限制 {}，已自动调整为 {}", size, maxPageSize, maxPageSize);
            size = maxPageSize;
        }

        return new Page<>(pageQuery.getCurrent(), size);
    }

    /**
     * 构建包含排序的分页查询 Wrapper
     *
     * <p>内部调用 {@link #build(Object)} 构建查询条件，并解析 @OrderBy 注解应用排序。</p>
     * <p>仅处理第一个 @OrderBy 注解字段。</p>
     *
     * @param query 查询对象
     * @return 包含查询条件和排序的 QueryWrapper
     */
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

    /**
     * 应用排序到 QueryWrapper
     *
     * <p>解析排序字符串，格式为 {@code field:direction,field2:direction2}。</p>
     * <p>direction 支持 {@code asc} 和 {@code desc}，默认为 asc。</p>
     *
     * <p>如果配置了 {@link OrderByValidator}，将自动进行白名单验证：</p>
     * <ul>
     *   <li>合法字段保留并应用排序</li>
     *   <li>非法字段被过滤，并输出警告日志</li>
     * </ul>
     *
     * @param wrapper 目标 QueryWrapper
     * @param orderBy 排序字符串
     */
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
     * 设置分页最大每页数量
     *
     * @param maxPageSize 最大每页数量
     */
    public void setMaxPageSize(Long maxPageSize) {
        if (maxPageSize != null && maxPageSize > 0) {
            this.maxPageSize = maxPageSize;
        }
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

    /**
     * 应用 OR 组合条件
     *
     * <p>生成 SQL：AND (field1 OP value OR field2 OP value OR ...)</p>
     */
    private <T> void applyOrGroupCondition(QueryWrapper<T> wrapper, QueryFieldMetadata metadata) {
        List<String> orFields = metadata.getOrFields();
        if (orFields == null || orFields.isEmpty()) {
            return;
        }

        Object value = metadata.getValue();
        if (value == null) {
            return;
        }

        // 使用 and() 包裹 OR 组：AND (field1 OP value OR field2 OP value ...)
        wrapper.and(w -> {
            for (int i = 0; i < orFields.size(); i++) {
                String fieldName = orFields.get(i);
                if (i > 0) {
                    w.or();
                }
                applyOrGroupFieldCondition(w, fieldName, value, metadata);
            }
        });
    }

    /**
     * 应用 OR 组中单个字段的条件
     */
    private <T> void applyOrGroupFieldCondition(QueryWrapper<T> wrapper, String fieldName,
                                                Object value, QueryFieldMetadata metadata) {
        // 根据 @Or 注解的操作符类型应用对应条件
        // 目前支持 EQ、LIKE、IN
        String strValue = value.toString();

        // 根据值的类型和注解配置决定使用哪种操作符
        if (value instanceof Collection) {
            wrapper.in(fieldName, (Collection<?>) value);
        } else {
            // 默认使用 LIKE（最常见的搜索场景）
            wrapper.like(fieldName, strValue);
        }
    }
}
