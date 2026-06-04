package io.github.core.queryx.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
