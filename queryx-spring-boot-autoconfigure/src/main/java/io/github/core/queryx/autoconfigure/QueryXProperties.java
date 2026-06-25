package io.github.core.queryx.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryX 配置属性
 * 
 * <p>通过 {@code @ConfigurationProperties} 绑定 application.yml 中的 queryx 前缀配置。</p>
 * 
 * <h3>配置示例（application.yml）：</h3>
 * <pre>
 * queryx:
 *   enabled: true                        # 是否启用 QueryX
 *   orderByWhitelistEnabled: true        # 是否启用排序字段白名单验证
 *   orderByWhitelist:                    # 允许的排序字段列表
 *     - id
 *     - username
 *     - create_time
 *   maxPageSize: 500                     # 分页最大每页数量
 *   exceptionHandlerEnabled: true        # 是否启用全局异常处理器
 *   tenantEnabled: true                  # 是否启用多租户
 *   tenantField: tenant_id               # 租户字段名（默认 tenant_id）
 * </pre>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "queryx")
public class QueryXProperties {

    /**
     * 是否启用 QueryX
     * <p>设为 false 将不注册 QueryParser 和 WrapperBuilder Bean。</p>
     * <p>默认：true</p>
     */
    private boolean enabled = true;
    
    /**
     * 排序字段白名单（为空表示不限制）
     * <p>例如：id,username,email,status,create_time</p>
     * <p>需配合 {@link #orderByWhitelistEnabled} 使用。</p>
     */
    private List<String> orderByWhitelist = new ArrayList<>();
    
    /**
     * 是否启用排序字段白名单验证
     * <p>启用后，未在 {@link #orderByWhitelist} 中的排序字段将被过滤，并输出警告日志。</p>
     * <p>默认：false（向后兼容，不验证）</p>
     */
    private boolean orderByWhitelistEnabled = false;
    
    /**
     * 分页最大每页数量
     * <p>超过此值的每页数量将被自动调整为该值，并输出警告日志。</p>
     * <p>默认：500</p>
     */
    private Long maxPageSize = 500L;
    
    /**
     * 是否启用全局异常处理器
     * <p>启用后自动注册 {@link QueryXExceptionHandler}，统一处理异常并返回 JSON 响应。</p>
     * <p>用户可自行创建同名 Bean 覆盖，或设为 false 完全禁用。</p>
     * <p>默认：true</p>
     */
    private boolean exceptionHandlerEnabled = true;

    /**
     * 是否启用多租户
     * <p>启用后，所有查询自动追加租户条件（需配合 TenantProvider 使用）。</p>
     * <p>默认：false</p>
     */
    private boolean tenantEnabled = false;

    /**
     * 租户字段名（数据库字段）
     * <p>默认：tenant_id</p>
     */
    private String tenantField = "tenant_id";

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
    
    public boolean isExceptionHandlerEnabled() {
        return exceptionHandlerEnabled;
    }

    public void setExceptionHandlerEnabled(boolean exceptionHandlerEnabled) {
        this.exceptionHandlerEnabled = exceptionHandlerEnabled;
    }

    public boolean isTenantEnabled() {
        return tenantEnabled;
    }

    public void setTenantEnabled(boolean tenantEnabled) {
        this.tenantEnabled = tenantEnabled;
    }

    public String getTenantField() {
        return tenantField;
    }

    public void setTenantField(String tenantField) {
        this.tenantField = tenantField;
    }
}
