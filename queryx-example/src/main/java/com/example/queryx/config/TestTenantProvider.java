package com.example.queryx.config;

import io.github.core.queryx.support.TenantProvider;
import org.springframework.stereotype.Component;

/**
 * 测试用租户提供者
 * 
 * <p>实际项目中应从用户登录上下文获取租户 ID，例如：</p>
 * <pre>
 * return SecurityContextHolder.getContext().getTenantId();
 * </pre>
 * 
 * @author MintNiu
 */
@Component
public class TestTenantProvider implements TenantProvider {
    @Override
    public Object getTenantId() {
        // 测试用：硬编码返回租户 ID = 1
        return 1;
    }
}
