package com.example.queryx.config;

import io.github.core.queryx.support.TenantProvider;
import org.springframework.stereotype.Component;

@Component
public class TestTenantProvider implements TenantProvider {
    @Override
    public Object getTenantId() {
        return 1;
    }
}

