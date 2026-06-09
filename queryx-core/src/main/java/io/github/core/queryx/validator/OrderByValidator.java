package io.github.core.queryx.validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 排序字段白名单验证器
 * 
 * <p>用于验证动态排序字段是否在允许的白名单中，防止 SQL 注入攻击。</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 1. 创建验证器，指定允许的排序字段
 * OrderByValidator validator = new OrderByValidator("id", "username", "email", "status", "create_time");
 * 
 * // 2. 验证排序字符串
 * validator.validate("id:desc,username:asc");  // ✅ 通过
 * validator.validate("id:desc,password:asc"); // ❌ 抛出异常：password 不在白名单中
 * </pre>
 * 
 * <h3>安全特性：</h3>
 * <ul>
 *   <li>只允许白名单中的字段参与排序</li>
 *   <li>防止恶意用户通过排序字段注入 SQL</li>
 *   <li>支持配置多个允许的排序字段</li>
 * </ul>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
public class OrderByValidator {

    /**
     * 允许的排序字段白名单
     */
    private final Set<String> allowedFields;

    /**
     * 创建排序字段验证器
     * 
     * @param allowedFields 允许的字段列表
     */
    public OrderByValidator(String... allowedFields) {
        if (allowedFields == null || allowedFields.length == 0) {
            this.allowedFields = Collections.emptySet();
        } else {
            this.allowedFields = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(allowedFields)));
        }
    }

    /**
     * 创建排序字段验证器
     * 
     * @param allowedFields 允许的字段集合
     */
    public OrderByValidator(Set<String> allowedFields) {
        if (allowedFields == null) {
            this.allowedFields = Collections.emptySet();
        } else {
            this.allowedFields = Collections.unmodifiableSet(new HashSet<>(allowedFields));
        }
    }
    
    /**
     * 创建排序字段验证器
     * 
     * @param allowedFields 允许的字段列表
     */
    public OrderByValidator(List<String> allowedFields) {
        if (allowedFields == null || allowedFields.isEmpty()) {
            this.allowedFields = Collections.emptySet();
        } else {
            this.allowedFields = Collections.unmodifiableSet(new HashSet<>(allowedFields));
        }
    }

    /**
     * 验证排序字符串
     * 
     * @param orderBy 排序字符串，格式："id:desc,username:asc"
     * @throws IllegalArgumentException 如果排序字段不在白名单中
     */
    public void validate(String orderBy) {
        // 如果白名单为空，不进行验证
        if (allowedFields.isEmpty()) {
            return;
        }

        if (orderBy == null || orderBy.trim().isEmpty()) {
            return;
        }

        // 解析并验证每个排序字段
        String[] orderFields = orderBy.split(",");
        for (String orderField : orderFields) {
            String[] parts = orderField.trim().split(":");
            String fieldName = parts[0].trim();

            if (fieldName.isEmpty()) {
                continue;
            }

            // 检查字段是否在白名单中
            if (!allowedFields.contains(fieldName)) {
                throw new IllegalArgumentException(
                    String.format("排序字段 '%s' 不在允许的白名单中，允许的字段：%s", 
                        fieldName, String.join(", ", allowedFields))
                );
            }
        }
    }

    /**
     * 检查是否允许指定字段排序
     * 
     * @param fieldName 字段名
     * @return true 如果允许，false 否则
     */
    public boolean isAllowed(String fieldName) {
        return allowedFields.isEmpty() || allowedFields.contains(fieldName);
    }

    /**
     * 获取允许的排序字段列表
     * 
     * @return 允许的字段集合（不可变）
     */
    public Set<String> getAllowedFields() {
        return allowedFields;
    }
}
