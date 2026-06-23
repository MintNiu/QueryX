package io.github.core.queryx.autoconfigure;

import io.github.core.queryx.builder.DefaultWrapperBuilder;
import io.github.core.queryx.builder.WrapperBuilder;
import io.github.core.queryx.parser.QueryParser;
import io.github.core.queryx.parser.ReflectionQueryParser;
import io.github.core.queryx.support.DataPermissionProvider;
import io.github.core.queryx.validator.OrderByValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * QueryX 自动配置类
 * 
 * <p>通过 Spring Boot 自动配置机制，自动注册以下核心 Bean：</p>
 * <ul>
 *   <li>{@link io.github.core.queryx.parser.QueryParser} - 查询解析器，解析 DTO 上的注解</li>
 *   <li>{@link io.github.core.queryx.builder.WrapperBuilder} - Wrapper 构建器，将查询对象转为 MyBatis Plus QueryWrapper</li>
 *   <li>{@link QueryXExceptionHandler} - 全局异常处理器（可选，通过 {@code queryx.exceptionHandlerEnabled} 控制）</li>
 * </ul>
 * 
 * <h3>配置项（application.yml）：</h3>
 * <pre>
 * queryx:
 *   enabled: true                        # 是否启用 QueryX（设为 false 则不注册任何 Bean）
 *   orderByWhitelistEnabled: true        # 是否启用排序字段白名单验证
 *   orderByWhitelist:                    # 允许的排序字段列表
 *     - id
 *     - username
 *   maxPageSize: 500                     # 分页最大每页数量
 *   exceptionHandlerEnabled: true        # 是否启用全局异常处理器
 * </pre>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
@Configuration
@EnableConfigurationProperties(QueryXProperties.class)
public class QueryXAutoConfiguration {

    private final QueryXProperties queryXProperties;

    public QueryXAutoConfiguration(QueryXProperties queryXProperties) {
        this.queryXProperties = queryXProperties;
    }

    /**
     * 创建查询解析器 Bean
     * <p>仅在 queryx.enabled=true 时生效，默认为 true。</p>
     * <p>使用 @ConditionalOnMissingBean 支持用户自定义替换。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "queryx", name = "enabled", havingValue = "true", matchIfMissing = true)
    public QueryParser queryParser() {
        return new ReflectionQueryParser();
    }

    /**
     * 创建 Wrapper 构建器 Bean
     * <p>仅在 queryx.enabled=true 时生效，默认为 true。</p>
     * <p>自动应用以下配置：</p>
     * <ul>
     *   <li>排序字段白名单验证（orderByWhitelist）</li>
     *   <li>分页最大数量限制（maxPageSize）</li>
     *   <li>数据权限提供者（如果存在）</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "queryx", name = "enabled", havingValue = "true", matchIfMissing = true)
    public WrapperBuilder wrapperBuilder(QueryParser queryParser,
                                         @Autowired(required = false) DataPermissionProvider dataPermissionProvider) {
        DefaultWrapperBuilder builder = new DefaultWrapperBuilder(queryParser);
        
        // 配置排序字段白名单
        if (queryXProperties.isOrderByWhitelistEnabled() 
                && !CollectionUtils.isEmpty(queryXProperties.getOrderByWhitelist())) {
            OrderByValidator validator = new OrderByValidator(queryXProperties.getOrderByWhitelist());
            builder.setOrderByValidator(validator);
        }
        
        // 配置分页最大数量（默认 500）
        builder.setMaxPageSize(queryXProperties.getMaxPageSize());
        
        // 配置数据权限提供者（如果存在）
        if (dataPermissionProvider != null) {
            builder.setDataPermissionProvider(dataPermissionProvider);
        }
        
        return builder;
    }
    
    /**
     * 全局异常处理器自动配置
     * 
     * <p>独立内部配置类，满足以下全部条件时自动注册 {@link QueryXExceptionHandler}：</p>
     * <ul>
     *   <li>{@code spring-web} 在类路径中（@ConditionalOnClass）</li>
     *   <li>{@code queryx.exceptionHandlerEnabled=true}（默认开启）</li>
     *   <li>用户未自定义 QueryXExceptionHandler Bean（@ConditionalOnMissingBean）</li>
     * </ul>
     * 
     * <p>用户如需自定义异常处理，可自行创建 {@link QueryXExceptionHandler} 的同名 Bean 覆盖，
     * 或配置 {@code queryx.exceptionHandlerEnabled=false} 完全禁用。</p>
     */
    @Configuration
    @ConditionalOnClass(RestControllerAdvice.class)
    @ConditionalOnProperty(prefix = "queryx", name = "exceptionHandlerEnabled", havingValue = "true", matchIfMissing = true)
    static class ExceptionHandlerAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(QueryXExceptionHandler.class)
        public QueryXExceptionHandler queryXExceptionHandler() {
            return new QueryXExceptionHandler();
        }
    }
}
