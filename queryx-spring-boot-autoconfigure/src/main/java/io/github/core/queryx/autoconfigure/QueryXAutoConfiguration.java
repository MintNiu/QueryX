package io.github.core.queryx.autoconfigure;

import io.github.core.queryx.builder.DefaultWrapperBuilder;
import io.github.core.queryx.builder.WrapperBuilder;
import io.github.core.queryx.parser.QueryParser;
import io.github.core.queryx.parser.ReflectionQueryParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryX 自动配置类
 * 
 * @author MintNiu
 */
@Configuration
@EnableConfigurationProperties(QueryXProperties.class)
public class QueryXAutoConfiguration {

    private final QueryXProperties queryXProperties;

    public QueryXAutoConfiguration(QueryXProperties queryXProperties) {
        this.queryXProperties = queryXProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryParser queryParser() {
        return new ReflectionQueryParser();
    }

    @Bean
    @ConditionalOnMissingBean
    public WrapperBuilder wrapperBuilder(QueryParser queryParser) {
        if (!queryXProperties.isEnabled()) {
            throw new IllegalStateException("QueryX is disabled by configuration");
        }
        return new DefaultWrapperBuilder(queryParser);
    }
}
