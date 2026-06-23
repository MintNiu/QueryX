package com.example.queryx.provider;

import io.github.core.queryx.support.DataPermissionProvider;
import org.springframework.stereotype.Component;

@Component
public class TestDataPermissionProvider implements DataPermissionProvider {
    @Override
    public Object getPermissionValue() {
        // 模拟普通用户只能查 status=1 的数据
        return 1;

        // 返回 null 表示管理员，不限制
        // return null;
    }
}

