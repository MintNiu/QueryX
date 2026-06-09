package io.github.core.queryx.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryX 配置属性
 * 
 * @author MintNiu
 */
@ConfigurationProperties(prefix = "queryx")
public class QueryXProperties {

    /**
     * 是否启用 QueryX
     */
    private boolean enabled = true;
    
    /**
     * 排序字段白名单（为空表示不限制）
     * 例如：id,username,email,status,create_time
     */
    private List<String> orderByWhitelist = new ArrayList<>();
    
    /**
     * 是否启用排序字段白名单验证
     */
    private boolean orderByWhitelistEnabled = false;
    
    /**
     * 分页最大每页数量
     */
    private Long maxPageSize = 500L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public List<String> getOrderByWhitelist() {
        return orderByWhitelist;
    }

    public void setOrderByWhitelist(List<String> orderByWhitelist) {
        this.orderByWhitelist = orderByWhitelist;
    }
    
    public boolean isOrderByWhitelistEnabled() {
        return orderByWhitelistEnabled;
    }

    public void setOrderByWhitelistEnabled(boolean orderByWhitelistEnabled) {
        this.orderByWhitelistEnabled = orderByWhitelistEnabled;
    }
    
    public Long getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(Long maxPageSize) {
        this.maxPageSize = maxPageSize;
    }
}
