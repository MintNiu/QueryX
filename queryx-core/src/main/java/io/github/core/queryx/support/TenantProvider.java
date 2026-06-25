package io.github.core.queryx.support;

/**
 * 租户 ID 提供者接口
 * 
 * <p>用户实现此接口提供当前用户的租户 ID。</p>
 * <p>框架会在构建 QueryWrapper 时自动调用此接口，并追加租户条件（全局生效）。</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * &#64;Component
 * public class MyTenantProvider implements TenantProvider {
 *     &#64;Override
 *     public Object getTenantId() {
 *         // 从当前登录用户上下文获取租户 ID
 *         return TenantContextHolder.getCurrentTenantId();
 *     }
 * }
 * </pre>
 * 
 * <h3>配置方式（application.yml）：</h3>
 * <pre>
 * queryx:
 *   tenantEnabled: true      # 启用多租户
 *   tenantField: tenant_id   # 租户字段名（默认 tenant_id）
 * </pre>
 * 
 * <h3>返回值说明：</h3>
 * <ul>
 *   <li>返回 null → 不追加租户条件（超级管理员场景）</li>
 *   <li>返回单个值 → 生成 WHERE tenant_id = value</li>
 *   <li>返回 Collection → 生成 WHERE tenant_id IN (values)</li>
 * </ul>
 * 
 * <h3>与数据权限（@DataScope）的区别：</h3>
 * <ul>
 *   <li>数据权限：需要标注 @DataScope 注解，单个 DTO 生效</li>
 *   <li>多租户：全局生效，所有查询自动追加租户条件</li>
 * </ul>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
public interface TenantProvider {

    /**
     * 获取当前用户的租户 ID
     * 
     * @return 租户 ID，返回 null 表示不限制（超级管理员）
     */
    Object getTenantId();
}
